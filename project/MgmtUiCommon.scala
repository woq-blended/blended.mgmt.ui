import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.emitSourceMaps
import phoenix.ProjectFactory
import sbt.{AutoPlugin, ClasspathDep, Def, ProjectReference}
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.webpackBundlingMode
import sbt._

object MgmtUiCommon extends ProjectFactory {

  object config extends ProjectSettings {
    override def projectName: String = "blended.mgmt.ui.common"
    override def description: String = "Common functionality for the management UI"

    override def projectDir: Option[String] = Some("common")

    override def settings: Seq[sbt.Setting[_]] = super.settings ++ Seq(
      webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
      emitSourceMaps := true,
    )

    /** Dependencies */
    override def deps = Def.setting(super.deps.value ++ Seq(
      JsDependencies.scalaJsDom.value,
      JsDependencies.react4s.value
    ))

    /**
      * Override this method to specify additional plugins for this project.
      */
    override def plugins: Seq[AutoPlugin] = super.plugins ++
      Seq(ScalaJSBundlerPlugin)

    override def dependsOn: Seq[ClasspathDep[ProjectReference]] = Seq(MgmtUiRouter.project)
  }

}
