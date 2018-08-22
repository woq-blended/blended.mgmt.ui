import java.nio.file.{Files, StandardCopyOption}
import com.typesafe.sbt.packager.SettingsHelper._
import com.typesafe.sbt.osgi.SbtOsgi.autoImport._

// The location for the local maven repository
lazy val m2Repo = "file://" + System.getProperty("maven.repo.local", System.getProperty("user.home") + "/.m2/repository")

// General settings for subprojects to be published
lazy val doPublish = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if(isSnapshot.value) {
      Some("snapshots" at nexus + "content/repositories/snapshots")
    } else {
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  }
)

// General settings for subprojects not to be published
lazy val noPublish = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {}
)

// General settings for subprojects using NPM
lazy val npmSettings = Seq(
  useYarn := true,
  npmDependencies.in(Compile) := Seq(
    "react" -> Versions.react,
    "react-dom" -> Versions.react,
    "jsdom" -> Versions.jsdom,
    "@material-ui/core" -> "1.4.3",
    "@material-ui/icons" -> "2.0.0",
    "jsonwebtoken" -> "8.3.0"
  )
)

// *******************************************************************************************************
// Overall settings for this build
// *******************************************************************************************************
inThisBuild(Seq(
  organization := "de.wayofquality.blended",
  version := "0.1.1-SNAPSHOT",
  scalaVersion := crossScalaVersions.value.head,
  crossScalaVersions := Seq("2.12.6"),
  scalacOptions in Compile ++= Seq(
    "-deprecation",
    "-feature"
  ),
  javacOptions in Compile ++= Seq(
    "-source", "1.8",
    "-target", "1.8"
  ),
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  publishArtifact in Test := false,

  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    "Maven2 Local" at m2Repo
  )
))


// *******************************************************************************************************
// The root project
// *******************************************************************************************************
lazy val root = project.in(file("."))
  .settings(noPublish)
  .aggregate(common, router, components, materialGen, material, app, server, uitest, sampleApp)

// *******************************************************************************************************
// The sub project for the router
// *******************************************************************************************************
lazy val router = project.in(file("router"))
  .settings(
    name := "router",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
    emitSourceMaps := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % Versions.scalaTest % "test"
    )
  ).enablePlugins(ScalaJSBundlerPlugin)

// *******************************************************************************************************
// The sub project for common utilities
// *******************************************************************************************************
lazy val common = project.in(file("common"))
  .settings(
    name := "common",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
    emitSourceMaps := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % Versions.scalaJsDom,
      "com.github.ahnfelt" %%% "react4s" % Versions.react4s
    )
  )
  .dependsOn(router)
  .enablePlugins(ScalaJSBundlerPlugin)

// *******************************************************************************************************
// The sub project for reusable components
// *******************************************************************************************************
lazy val components = project.in(file("components"))
  .settings(
    name := "components",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
    emitSourceMaps := true,
    libraryDependencies ++= Seq(
      "com.github.ahnfelt" %%% "react4s" % Versions.react4s
    )
  )
  .enablePlugins(ScalaJSBundlerPlugin)
  .dependsOn(common,material)

// *******************************************************************************************************
// The sub project for the Material UI generator
// *******************************************************************************************************
lazy val materialGen = project.in(file("material-gen"))
  .settings(
    name := "merial-gen",
    libraryDependencies ++= Seq(
      "de.tototec" % "de.tototec.cmdoption" % Versions.cmdOption,
      "org.slf4j" % "slf4j-api" % Versions.slf4j,
      "ch.qos.logback" % "logback-core" % Versions.logback,
      "ch.qos.logback" % "logback-classic" % Versions.logback
    )
  )
  .settings(npmSettings)
  .settings(noPublish)
  .enablePlugins(ScalaJSBundlerPlugin)

// *******************************************************************************************************
// The sub project for the React4s Material UI wrapper
// *******************************************************************************************************
lazy val generateMui = TaskKey[Seq[File]]("generateMui")

lazy val material = project.in(file("material"))
  .settings(
    name := "material",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
    emitSourceMaps := true,
    libraryDependencies ++= Seq(
      "com.github.ahnfelt" %%% "react4s" % Versions.react4s
    ),
    generateMui := {

      runner.value.run(
        "blended.material.gen.MaterialGenerator",
        (materialGen/Runtime/fullClasspath).value.files,
        Seq(
          "-d", ((materialGen/Compile/npmUpdate).value / "node_modules" / "@material-ui").getAbsolutePath(),
          "-o", (sourceManaged.value / "main").getAbsolutePath()
        ),
        streams.value.log
      )

      val pathFinder : PathFinder = sourceManaged.value ** "*.scala"
      pathFinder.get.filter(_.getAbsolutePath().endsWith("scala")).map(_.getAbsoluteFile())
    },

    Compile/sourceGenerators += generateMui
  )
  .settings(npmSettings)
  .settings(noPublish)
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(common)

// *******************************************************************************************************
// The sub project for Sample App - A playground to test out components
// *******************************************************************************************************
lazy val sampleApp = project.in(file("sampleApp"))
  .settings(
    name := "sampleApp",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryAndApplication(),
    emitSourceMaps := true,
    scalaJSUseMainModuleInitializer := true,

    libraryDependencies ++= Seq(
      "com.github.ahnfelt" %%% "react4s" % Versions.react4s,
      "org.scalatest" %%% "scalatest" % Versions.scalaTest % "test"
    ),

    Compile/fastOptJS/webpack := {
      val result = (Compile/fastOptJS/webpack).toTask.value
      val dir = baseDirectory.value / "index-dev.html"
      val t = target.value / ("scala-" + scalaBinaryVersion.value) / "scalajs-bundler" / "main" / "index-dev.html"

      Files.copy(dir.toPath, t.toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
      result
    }
  )
  .settings(noPublish:_*)
  .settings(npmSettings:_*)
  .enablePlugins(ScalaJSBundlerPlugin)
  .dependsOn(router, common, components, material)

// *******************************************************************************************************
// The sub project for the Blended Management Console
// *******************************************************************************************************
lazy val app = project.in(file("mgmt-app"))
  .settings(
    name := "mgmt-app",

    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryAndApplication(),
    fastOptJS/emitSourceMaps := true,
    fullOptJS/emitSourceMaps := false,
    scalaJSUseMainModuleInitializer := true,

    Compile/fastOptJS/webpack := {
      val result = (Compile/fastOptJS/webpack).toTask.value
      val dir = baseDirectory.value / "index-dev.html"
      val t = target.value / ("scala-" + scalaBinaryVersion.value) / "scalajs-bundler" / "main" / "index-dev.html"

      Files.copy(dir.toPath, t.toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
      result
    },

    libraryDependencies ++= Seq(
      "org.akka-js" %%% "akkajsactor" % Versions.akkaJs,
      "org.scala-js" %%% "scalajs-dom" % Versions.scalaJsDom,
      "com.github.ahnfelt" %%% "react4s" % Versions.react4s,
      "com.github.benhutchison" %%% "prickle" % Versions.prickle,
      organization.value %%% "blended.updater.config" % Versions.blended,
      organization.value %%% "blended.security" % Versions.blended,

      "org.scalatest" %%% "scalatest" % Versions.scalaTest % "test"
    ),

    topLevelDirectory := None,

    Universal/mappings ++= (Compile/fullOptJS/webpack).value.map{ f =>
      f.data -> s"assets/${f.data.getName()}"
    } ++ Seq(
      target.value / ("scala-" + scalaBinaryVersion.value) / "scalajs-bundler" / "main" / "node_modules" / "react" / "cjs" / "react.production.min.js" -> "assets/react.production.min.js",
      target.value / ("scala-" + scalaBinaryVersion.value) / "scalajs-bundler" / "main" / "node_modules" / "react-dom" / "cjs" / "react-dom.production.min.js" -> "assets/react-dom.production.min.js"
    ),

    makeDeploymentSettings(Universal, Universal/packageBin, "zip"),
    addArtifact(Universal/packageBin/artifact, Universal/packageBin),

    publish := publish.dependsOn(Universal/publish).value,
    publishM2 := publishM2.dependsOn(Universal/publishM2).value,
    publishLocal := publishLocal.dependsOn(Universal/publishLocal).value
  )
  .settings(doPublish:_*)
  .settings(npmSettings:_*)
  .enablePlugins(ScalaJSBundlerPlugin,UniversalPlugin,UniversalDeployPlugin)
  .dependsOn(router, common, components)

// *******************************************************************************************************
// The sub project for the deployable Akka Http Server Module
// *******************************************************************************************************
lazy val server = project.in(file("server"))
  .settings(

    libraryDependencies ++= Seq(
      "com.github.domino-osgi" %% "domino" % "1.1.2",
      organization.value % "blended.domino" % Versions.blended,
      organization.value % "blended.akka.http" % Versions.blended
    ),

    Compile/packageBin := OsgiKeys.bundle.value,
    Compile/packageBin/artifact := {
      val previous = (Compile/packageBin/artifact).value
      previous.withName("blended.mgmt.ui.server")
    }
  )
  .settings(OsgiHelper.osgiSettings(
    bundleSymbolicName = "blended.mgmt.ui.server",
    bundleActivator = "blended.mgmt.ui.server.internal.UiServerActivator",
    privatePackage = Seq("blended.mgmt.ui.server.internal")
  ))
  .enablePlugins(SbtOsgi)

// *******************************************************************************************************
// The sub project for the Selenium Tests for the Management Console
// *******************************************************************************************************
lazy val uitest = project.in(file("mgmt-app-test"))
  .settings(
    name := "uitest",

    Test/fork := true,
    Test/javaOptions ++= Seq(
      "-DappUnderTest=" + ((app/target).value / ("scala-" + scalaBinaryVersion.value) / "scalajs-bundler" / "main"),
      "-Dwebdriver.chrome.driver=/opt/chromedriver"
    ),

    Test/test := {
      (Test/test).dependsOn((app/Compile/fastOptJS/webpack).toTask).value
    },

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % Versions.scalaTest % "test",
      "org.seleniumhq.selenium" % "selenium-java" % Versions.selenium % "test",
      "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp % "test",
      "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp % "test"
    )
  )
  .settings(noPublish)
