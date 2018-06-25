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
  useYarn := true
)

lazy val root = project.in(file("."))
  .settings(noPublish)

lazy val app = project.in(file("mgmt-app"))
  .settings(
    name := "mgmt-app",
    jsEnv := PhantomJSEnv().value,
    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryAndApplication(),
    scalaJSUseMainModuleInitializer := true

  )
  .settings(noPublish:_*)
  .settings(npmSettings:_*)
  .enablePlugins(ScalaJSBundlerPlugin)
