import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.emitSourceMaps
import phoenix.ProjectFactory
import sbt._
import sbt.Keys._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

object MgmtUiMaterial extends ProjectFactory {

  object config extends ProjectSettings with NoPublish with NpmSettings {

    private val generateMui = TaskKey[Seq[File]]("generateMui")

    override def projectName: String = "blended.mgmt.ui.material"
    override def description: String = "ScalaJS bindings for MaterialUI"

    override def projectDir: Option[String] = Some("material")

    override def plugins: Seq[AutoPlugin] = super.plugins ++ Seq(ScalaJSBundlerPlugin)

    override def dependsOn: Seq[ClasspathDep[ProjectReference]] = Seq(
      MgmtUiCommon.project
    )

    /** Dependencies */
    override def deps = Def.setting(super.deps.value ++ Seq(
      JsDependencies.scalaJsDom.value,
      JsDependencies.react4s.value
    ))

    override def settings: Seq[sbt.Setting[_]] = super.settings ++ Seq(
      webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
      emitSourceMaps := true,

      generateMui := {

        runner.value.run(
          "blended.material.gen.MaterialGenerator",
          (MgmtUiMaterialGen.project/Runtime/fullClasspath).value.files,
          Seq(
            "-d", ((MgmtUiMaterialGen.project/Compile/npmUpdate).value / "node_modules" / "@material-ui").getAbsolutePath(),
            "-o", (sourceManaged.value / "main").getAbsolutePath()
          ),
          streams.value.log
        )

        val pathFinder : PathFinder = sourceManaged.value ** "*.scala"
        pathFinder.get.filter(_.getAbsolutePath().endsWith("scala")).map(_.getAbsoluteFile())
      },

      Compile/sourceGenerators += generateMui
    )
  }

}

//lazy val material = project.in(file("material"))
//  .settings(
//    name := "material",
//    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
//    emitSourceMaps := true,
//    libraryDependencies ++= Seq(
//      jsDeps.react4s.value
//    ),
//    generateMui := {
//
//      runner.value.run(
//        "blended.material.gen.MaterialGenerator",
//        (materialGen/Runtime/fullClasspath).value.files,
//        Seq(
//          "-d", ((materialGen/Compile/npmUpdate).value / "node_modules" / "@material-ui").getAbsolutePath(),
//          "-o", (sourceManaged.value / "main").getAbsolutePath()
//        ),
//        streams.value.log
//      )
//
//      val pathFinder : PathFinder = sourceManaged.value ** "*.scala"
//      pathFinder.get.filter(_.getAbsolutePath().endsWith("scala")).map(_.getAbsoluteFile())
//    },
//
//    Compile/sourceGenerators += generateMui
//  )
//  .settings(npmSettings)
//  .settings(noPublish)
//  .enablePlugins(ScalaJSPlugin)
//  .dependsOn(common)
