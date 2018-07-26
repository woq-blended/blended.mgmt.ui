import java.nio.file.{Files, StandardCopyOption}

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
    "Local Maven Repository" at m2Repo
  ) 
))

lazy val m2Repo = "file://" + System.getProperty("maven.repo.local", System.getProperty("user.home") + "/.m2/repository")

lazy val doPublish = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
  if(isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  }
)

lazy val noPublish = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {}
)

lazy val npmSettings = Seq(
  useYarn := true,
  npmDependencies.in(Compile) := Seq(
    "react" -> Versions.react,
    "react-dom" -> Versions.react,
    "jsdom" -> Versions.jsdom,
    "@material-ui/core" -> "1.4.1"
  )
)

// The root project
lazy val root = project.in(file("."))
  .settings(noPublish)
  .aggregate(common,router,components, app, uitest, sampleApp)

// The subproject defining the router
lazy val router = project.in(file("router"))
  .settings(
    name := "router",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
    emitSourceMaps := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % Versions.scalaTest % "test"
    )
  ).enablePlugins(ScalaJSBundlerPlugin)

// Some common utilities
lazy val common = project.in(file("common"))
  .settings(
    name := "common",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
    emitSourceMaps := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.5",
      "com.github.ahnfelt" %%% "react4s" % Versions.react4s
    )
  )
  .dependsOn(router)
  .enablePlugins(ScalaJSBundlerPlugin)

// Reusable React4s components
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
  .dependsOn(common)

lazy val sampleApp = project.in(file("sampleApp"))
  .settings(
    name := "sampleApp",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
    emitSourceMaps := true,
    scalaJSUseMainModuleInitializer := true,

    libraryDependencies ++= Seq(
      "com.github.ahnfelt" %%% "react4s" % Versions.react4s
    ),

    Compile/fastOptJS/webpack := {
      val result = (Compile/fastOptJS/webpack).toTask.value
      val dir = baseDirectory.value / "index-dev.html"
      val t = target.value / ("scala-" + scalaBinaryVersion.value) / "scalajs-bundler" / "main" / "index-dev.html"

      Files.copy(dir.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
      result
    }
  )
  .settings(noPublish:_*)
  .settings(npmSettings:_*)
  .enablePlugins(ScalaJSBundlerPlugin)
  .dependsOn(router, common, components)

lazy val app = project.in(file("mgmt-app"))
  .settings(
    name := "mgmt-app",

    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryAndApplication(),
    emitSourceMaps := true,
    scalaJSUseMainModuleInitializer := true,

    Compile/fastOptJS/webpack := {
      val result = (Compile/fastOptJS/webpack).toTask.value
      val dir = baseDirectory.value / "index-dev.html"
      val t = target.value / ("scala-" + scalaBinaryVersion.value) / "scalajs-bundler" / "main" / "index-dev.html"

      Files.copy(dir.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
      result
    },

    libraryDependencies ++= Seq(
      "org.akka-js" %%% "akkajsactor" % Versions.akkaJs,
      "org.scala-js" %%% "scalajs-dom" % "0.9.5",
      "com.github.ahnfelt" %%% "react4s" % Versions.react4s,
      "com.github.benhutchison" %%% "prickle" % Versions.prickle,
      organization.value %%% "blended.updater.config" % Versions.blended,

      "org.scalatest" %%% "scalatest" % Versions.scalaTest % "test"
    )
  )
  .settings(noPublish:_*)
  .settings(npmSettings:_*)
  .enablePlugins(ScalaJSBundlerPlugin)
  .dependsOn(router, common, components)

lazy val uitest = project.in(file("mgmt-app-test"))
  .settings(
    name := "uitest",

    Test/fork := true,
    Test/javaOptions ++= Seq(
      "-DappUnderTest=" + ((app/target).value / ("scala-" + scalaBinaryVersion.value) / "scalajs-bundler" / "main"),
      "-Dwebdriver.chrome.driver=/opt/chromedriver"
    ),

    Test/test := {
      ((Test/test).dependsOn((app/Compile/fastOptJS/webpack).toTask)).value
    },

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % Versions.scalaTest % "test",
      "org.seleniumhq.selenium" % "selenium-java" % Versions.selenium % "test",
      "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp % "test",
      "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp % "test"
    )
  )
  .settings(noPublish)
