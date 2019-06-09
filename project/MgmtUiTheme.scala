import JsDependencies._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import phoenix.ProjectFactory
import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

object MgmtUiTheme extends ProjectFactory {

  object config extends ProjectSettings {
    override def projectName: String = "blended.mgmt.ui.theme"
    override def description: String = "A collection of styles for the management UI and the playground"

    override def projectDir: Option[String] = Some("theme")

    override def settings: Seq[sbt.Setting[_]] = super.settings ++ Seq(
      emitSourceMaps := true
    )

    /** Dependencies */
    override def deps : Def.Initialize[Seq[ModuleID]] = Def.setting(super.deps.value ++
      Seq(
        react4s.value
      )
    )


    override def dependsOn: Seq[ClasspathDep[ProjectReference]] = Seq(
      MgmtUiCommon.project,
      MgmtUiMaterial.project,
    )

    override def plugins: Seq[AutoPlugin] = super.plugins ++
      Seq(ScalaJSBundlerPlugin)
  }

}
