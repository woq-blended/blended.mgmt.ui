import java.nio.file.{Files, StandardCopyOption}

import com.typesafe.sbt.packager.universal.{UniversalDeployPlugin, UniversalPlugin}
import phoenix.ProjectFactory
import sbt.Keys._
import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import ScalaJSBundlerPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin.AutoImport._
import UniversalPlugin.autoImport._
import com.typesafe.sbt.packager.SettingsHelper._

object MgmtUiApp extends ProjectFactory {

  object config extends ProjectSettings with NpmSettings with Publish {
    import JsDependencies._

    override def projectName: String = "blended.mgmt.ui.app"
    override def description: String = "The blended management console"

    override def projectDir: Option[String] = Some("mgmt-app")

    override def settings: Seq[sbt.Setting[_]] = super.settings ++ Seq(
      webpackBundlingMode := scalajsbundler.BundlingMode.LibraryAndApplication(),
      fastOptJS/emitSourceMaps := true,
      fullOptJS/emitSourceMaps := false,
      scalaJSUseMainModuleInitializer := true,

      Compile/fastOptJS/webpack := {
        val result = (Compile/fastOptJS/webpack).toTask.value
        val dir = baseDirectory.value / "index-dev.html"
        val t = target.value / ("scala-" + scalaBinaryVersion.value) / "scalajs-bundler" / "main" / "index-dev.html"

        Files.copy(dir.toPath, t.toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
        result
      },

      topLevelDirectory := None,

      Universal/mappings ++= (Compile/fullOptJS/webpack).value.map{ f =>
        f.data -> s"assets/${f.data.getName()}"
      } ++ Seq(
        (fastOptJS/crossTarget).value / "node_modules" / "react" / "umd" / "react.production.min.js" -> "assets/react.production.min.js",
        (fastOptJS/crossTarget).value / "node_modules" / "react-dom" / "umd" / "react-dom.production.min.js" -> "assets/react-dom.production.min.js"
      ),

      publish := publish.dependsOn(Universal/publish).value,
      publishM2 := publishM2.dependsOn(Universal/publishM2).value,
      publishLocal := publishLocal.dependsOn(Universal/publishLocal).value
    ) ++ 
    addArtifact(Universal/packageBin/artifact, Universal/packageBin).settings ++ 
    makeDeploymentSettings(Universal, Universal/packageBin, "zip")

    override def dependsOn: Seq[ClasspathDep[ProjectReference]] = Seq(
      MgmtUiRouter.project,
      MgmtUiCommon.project,
      MgmtUiComponents.project
    )

    /** Dependencies */
    override def deps = Def.setting(super.deps.value ++ Seq(
      akkaJsActor.value,
      scalaJsDom.value,
      react4s.value,
      prickle.value,
      blendedUpdaterConfig.value,
      blendedSecurity.value,
      scalaTestJs.value % "test"
    ))

    override def plugins: Seq[AutoPlugin] = super.plugins ++ Seq(
      ScalaJSBundlerPlugin, UniversalPlugin, UniversalDeployPlugin
    )
  }
}
