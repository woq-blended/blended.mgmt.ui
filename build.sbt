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

  resolvers += "Local Maven Repository" at m2Repo
))

lazy val m2Repo = "file://" + System.getProperty("maven.repo.local", System.getProperty("user.home") + "/.m2/repository")

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
    "jsdom" -> Versions.jsdom
  )
)

lazy val root = project.in(file("."))
  .settings(noPublish)
  .aggregate(utils,router,components, app)

lazy val router = project.in(file("router"))
  .settings(
    name := "router",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % Versions.scalaTest % "test"
    )
  ).enablePlugins(ScalaJSBundlerPlugin)

lazy val utils = project.in(file("common"))
  .settings(
    name := "common",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.5",
      "com.github.ahnfelt" %%% "react4s" % Versions.react4s
    )
  )
  .dependsOn(router)
  .enablePlugins(ScalaJSBundlerPlugin)

lazy val components = project.in(file("components"))
  .settings(
    name := "components",
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
    libraryDependencies ++= Seq(
      "com.github.ahnfelt" %%% "react4s" % Versions.react4s
    )
  ).enablePlugins(ScalaJSBundlerPlugin)

lazy val app = project.in(file("mgmt-app"))
  .settings(
    name := "mgmt-app",
    //jsEnv := PhantomJSEnv().value,
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryAndApplication(),
    scalaJSUseMainModuleInitializer := true,

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
  .dependsOn(router, utils, components)
