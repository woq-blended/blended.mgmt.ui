import java.nio.file.{Files, StandardCopyOption}

import JsDependencies._
import sbt._
import sbt.Keys._
import phoenix.ProjectFactory
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin

object MgmtUiSampleApp extends ProjectFactory {

  // scalastyle:off object.name
  object config extends ProjectSettings with NpmSettings with NoPublish {
  // scalastyle:on object.name

    override def projectName: String = "blended.mgmt.ui.sampleapp"
    override def description: String = "A playground to test UI components without breaking the management app."

    override def projectDir: Option[String] = Some("sampleApp")

    override def settings: Seq[sbt.Setting[_]] = super.settings ++ Seq(
      webpackBundlingMode := scalajsbundler.BundlingMode.LibraryAndApplication(),
      emitSourceMaps := true,
      scalaJSUseMainModuleInitializer := true,
      Compile/fastOptJS/webpack := {
        val result = (Compile/fastOptJS/webpack).toTask.value
        val dir = baseDirectory.value / "index-dev.html"
        val t = (Compile/fastOptJS/crossTarget).value / "index-dev.html"

        Files.copy(dir.toPath, t.toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
        result
      }
    )

    /** Dependencies */
    override def deps : Def.Initialize[Seq[ModuleID]] = Def.setting(super.deps.value ++ Seq(
      blendedJmx.value,
      react4s.value,
      scalaTestJs.value % "test"
    ))

    override def dependsOn: Seq[ClasspathDep[ProjectReference]] = Seq(
      MgmtUiRouter.project,
      MgmtUiCommon.project,
      MgmtUiComponents.project,
      MgmtUiMaterial.project
    )

    override def plugins: Seq[AutoPlugin] = super.plugins ++ Seq(ScalaJSBundlerPlugin)
  }
}
