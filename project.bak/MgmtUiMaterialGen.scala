import phoenix.ProjectFactory
import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin

object MgmtUiMaterialGen extends ProjectFactory {

  object config extends ProjectSettings with NoPublish with NpmSettings {

    override def projectName: String = "blended.mgmt.ui.material.gen"

    override def description: String = "A simple generator to generate MaterialUI bindings for react4s."


    override def projectDir: Option[String] = Some("material-gen")

    override def deps = Def.setting(super.deps.value ++ Seq(
      JavaDependencies.cmdOption,
      JavaDependencies.slf4jApi,
      JavaDependencies.logbackCore,
      JavaDependencies.logbackClassic
    ))

    override def plugins: Seq[AutoPlugin] = super.plugins ++ Seq(ScalaJSBundlerPlugin)
  }
}
