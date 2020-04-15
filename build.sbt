import java.nio.file.Files

inThisBuild(
  Seq(
    organization := ProjectDeprecated.organization,
    scalaVersion := crossScalaVersions.value.head,
    crossScalaVersions := Seq("2.12.11"),
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
    resolvers += Resolver.sonatypeRepo("snapshots")
  ) ++ Seq(
    version := Files.readAllLines( (baseDirectory.value / "version.txt").toPath).get(0),
    isSnapshot := version.value.endsWith("SNAPSHOT")
  )
)

// *******************************************************************************************************
// The root project
// *******************************************************************************************************
lazy val root = project.in(file("."))
  .settings(RootSettings.settings)
  .aggregate(common, router, components, materialGen, material, app, server, uitest, sampleApp, theme)

lazy val router = MgmtUiRouter.project
lazy val common = MgmtUiCommon.project
lazy val components = MgmtUiComponents.project
lazy val materialGen = MgmtUiMaterialGen.project
lazy val material = MgmtUiMaterial.project
lazy val sampleApp = MgmtUiSampleApp.project
lazy val app = MgmtUiApp.project
lazy val server = MgmtUiServer.project
lazy val uitest = MgmtUiTest.project
lazy val theme = MgmtUiTheme.project
