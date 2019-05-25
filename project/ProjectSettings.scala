import de.wayofquality.sbt.testlogconfig.TestLogConfig
import net.bzzt.reproduciblebuilds.ReproducibleBuildsPlugin
import phoenix.ProjectConfig
import sbt._

trait ProjectSettings extends ProjectConfig {

  /** The project descriptions. Also used in published pom.xml and as bundle description. */
  def description: String
  /** Dependencies */
  def deps : Def.Initialize[Seq[ModuleID]] = Def.setting(Seq.empty[ModuleID])

  override def settings: Seq[sbt.Setting[_]] = super.settings ++ {

    Seq(
      Keys.name := projectName,
      Keys.moduleName := Keys.name.value,
      Keys.description := description,
      Keys.libraryDependencies ++= deps.value,
    ) ++
      // We need to explicitly load the rb settings again to
      // make sure the OSGi package is post-processed:
      ReproducibleBuildsPlugin.projectSettings
  }


  override def plugins: Seq[AutoPlugin] = super.plugins ++
    Seq(ReproducibleBuildsPlugin) ++
    Seq(TestLogConfig)

}
