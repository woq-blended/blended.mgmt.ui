import phoenix.ProjectFactory
import sbt._
import sbt.Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

object MgmtUiTest extends ProjectFactory{

  object config extends ProjectSettings with NoPublish {
    override def projectName: String = "blended.mgmt.ui.test"
    override def description: String = "Automated tests for the management UI"
    override def projectDir: Option[String] = Some("mgmt-app-test")

    override def deps = Def.setting(super.deps.value ++ Seq(
      JavaDependencies.scalaTest % "test",
      JavaDependencies.selenium % "test",
      JavaDependencies.akkaHttp % "test",
      JavaDependencies.akkaHttpTestkit % "test"
    ))

    override def settings: Seq[sbt.Setting[_]] = super.settings ++ Seq(
      Test/fork := true,
      Test/javaOptions ++= Seq(
        "-DappUnderTest=" + ((MgmtUiApp.project/Compile/fastOptJS/crossTarget).value),
        "-Dwebdriver.chrome.driver=/opt/chromedriver"
      ),

      Test/test := {
        (Test/test).dependsOn((MgmtUiApp.project/Compile/fastOptJS/webpack).toTask).value
      }
    )
  }
}
