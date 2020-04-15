import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._
import JsDependencies._
import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin.autoImport._
import phoenix.ProjectFactory

object MgmtUiRouter extends ProjectFactory {

  object config extends ProjectSettings {
    override def projectName: String = "blended.mgmt.ui.router"
    override def description: String = "A simple router, adopted from https://github.com/werk/router4s"

    override def projectDir: Option[String] = Some("router")

    override def settings: Seq[sbt.Setting[_]] = super.settings ++ Seq(
      webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
      emitSourceMaps := true,
    )

    /** Dependencies */
    override def deps = Def.setting(super.deps.value ++
      Seq(scalaTestJs.value)
    )

    override def plugins: Seq[AutoPlugin] = super.plugins ++
      Seq(ScalaJSBundlerPlugin)
  }

}
