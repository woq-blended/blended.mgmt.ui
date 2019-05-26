lazy val jsDeps = JsDependencies
lazy val npmDeps = NpmDependencies
lazy val javaDeps = JavaDependencies

// The location for the local maven repository
lazy val m2Repo = "file://" + System.getProperty("maven.repo.local", System.getProperty("user.home") + "/.m2/repository")

lazy val global = Def.settings(
  Global/scalariformAutoformat := false,
  Global/scalariformWithBaseDirectory := true,

  Global/testlogDirectory := target.value / "testlog",

  Global/useGpg := false,
  Global/pgpPublicRing := baseDirectory.value / "project" / ".gnupg" / "pubring.gpg",
  Global/pgpSecretRing := baseDirectory.value / "project" / ".gnupg" / "secring.gpg",
  Global/pgpPassphrase := sys.env.get("PGP_PASS").map(_.toArray)
)

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
  publishLocal := {}
)

// *******************************************************************************************************
// Overall settings for this build
// *******************************************************************************************************
inThisBuild(Seq(
  organization := ProjectDeprecated.organization,
  version := "0.5-SNAPSHOT",
  scalaVersion := crossScalaVersions.value.head,
  crossScalaVersions := Seq("2.12.8"),
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
  .settings(global)
  .aggregate(common, router, components, materialGen, material, app, server, uitest, sampleApp)

lazy val router = MgmtUiRouter.project
lazy val common = MgmtUiCommon.project
lazy val components = MgmtUiComponents.project
lazy val materialGen = MgmtUiMaterialGen.project
lazy val material = MgmtUiMaterial.project
lazy val sampleApp = MgmtUiSampleApp.project
lazy val app = MgmtUiApp.project

lazy val server = MgmtUiServer.project
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
      javaDeps.scalaTest % "test",
      javaDeps.selenium % "test",
      javaDeps.akkaHttp % "test",
      javaDeps.akkaHttpTestkit % "test"
    )
  )
  .settings(noPublish)

