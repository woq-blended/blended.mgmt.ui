import java.nio.file.{Files, StandardCopyOption}

import blended.sbt.phoenix.osgi.{OsgiBundle, OsgiConfig}
import com.typesafe.sbt.osgi.SbtOsgi
import phoenix.ProjectFactory
import sbt._
import sbt.Keys._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import com.typesafe.sbt.osgi.SbtOsgi.autoImport._

object MgmtUiServer extends ProjectFactory {

  object config extends ProjectSettings with OsgiConfig {
    override def projectName: String = "blended.mgmt.ui.server"
    override def description: String = "A simple Akka Http server serving the management app."
    override def projectDir: Option[String] = Some("server")

    override def plugins: Seq[AutoPlugin] = super.plugins ++ Seq(SbtOsgi)

    override def deps = Def.setting(super.deps.value ++ Seq(
      JavaDependencies.dominoOsgi,
      JavaDependencies.blendedDomino,
      JavaDependencies.blendedAkkaHttp,
      JavaDependencies.akkaStream
    ))

    override def bundle: OsgiBundle = super.bundle.copy(
      bundleActivator = "blended.mgmt.ui.server.internal.UiServerActivator"
    )

    override def settings: Seq[sbt.Setting[_]] = super.settings ++ Seq(
      Compile/resourceGenerators += Def.task {
        val jsFiles : Seq[(File, String)]= ((MgmtUiApp.project/Compile/fullOptJS/webpack).value.map(f => f.data).filterNot(_.getName().contains("bundle")) ++
          Seq(
            (MgmtUiApp.project/Compile/fullOptJS/crossTarget).value / "node_modules" / "react" / "umd" / "react.production.min.js",
            (MgmtUiApp.project/Compile/fullOptJS/crossTarget).value / "node_modules" / "react-dom" / "umd" / "react-dom.production.min.js"
          )).map { f => (f, "assets/" + f.getName()) } ++
          Seq(
            (MgmtUiApp.project/baseDirectory).value / "src" / "universal" / "index.html" -> "index.html"
          )

        jsFiles.map { case (srcFile, mapping) =>
          val targetFile = (Compile/resourceManaged).value / "webapp" / mapping
          Files.createDirectories(targetFile.getParentFile().toPath())
          Files.copy(srcFile.toPath, targetFile.toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
          targetFile
        }
      }.taskValue
    )
  }
}
