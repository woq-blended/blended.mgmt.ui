import phoenix.ProjectFactory
import sbt.AutoPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin

object MgmtUiCommon extends ProjectFactory {

  object config extends ProjectSettings {
    override def projectName: String = "blended.mgmt.ui.common"
    override def description: String = "Common functionality for the management UI"

    /**
      * Override this method to specify additional plugins for this project.
      */
    override def plugins: Seq[AutoPlugin] = super.plugins ++
      Seq(ScalaJSBundlerPlugin)
  }

}


//object BlendedMgmtAgent extends ProjectFactory {
//  object config extends ProjectSettings {
//    override val projectName = "blended.mgmt.agent"
//    override val description = "Bundle to regularly report monitoring information to a central container hosting the container registry"
//
//    override def deps = Seq(
//      Dependencies.orgOsgi,
//      Dependencies.akkaOsgi,
//      Dependencies.akkaHttp,
//      Dependencies.akkaStream,
//      Dependencies.akkaTestkit % Test,
//      Dependencies.scalatest % Test
//    )
//
//    override def bundle = super.bundle.copy(
//      bundleActivator = s"${projectName}.internal.AgentActivator",
//      exportPackage = Seq()
//    )
//
//    override def dependsOn: Seq[ClasspathDep[ProjectReference]] = Seq(
//      BlendedAkka.project,
//      BlendedUpdaterConfigJvm.project,
//      BlendedUtilLogging.project,
//      BlendedPrickleAkkaHttp.project
//    )
//  }
//}

//lazy val common = project.in(file("common"))
//  .settings(
//    name := "common",
//    webpackBundlingMode := scalajsbundler.BundlingMode.LibraryOnly(),
//    emitSourceMaps := true,
//    libraryDependencies ++= Seq(
//      jsDeps.scalaJsDom.value, jsDeps.react4s.value
//    )
//  )
//  .dependsOn(router)
//  .enablePlugins(ScalaJSBundlerPlugin)