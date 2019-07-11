import phoenix.ProjectConfig
import sbt.Keys._
import sbt._
import com.typesafe.sbt.SbtScalariform.autoImport._
import de.wayofquality.sbt.testlogconfig.TestLogConfig.autoImport._
import com.typesafe.sbt.SbtPgp.autoImport._

object RootSettings extends ProjectConfig with NoPublish {

  override def projectName: String = "blended.mgmt.ui"

  override def settings: Seq[sbt.Setting[_]] = super.settings ++ Seq(
    scalaVersion := "2.12.8",
    Global/scalariformAutoformat := false,
    Global/scalariformWithBaseDirectory := true,

    Global/testlogDirectory := target.value / "testlog",

    Global/useGpg := false,
    Global/pgpPublicRing := baseDirectory.value / "project" / ".gnupg" / "pubring.gpg",
    Global/pgpSecretRing := baseDirectory.value / "project" / ".gnupg" / "secring.gpg",
    Global/pgpPassphrase := sys.env.get("PGP_PASS").map(_.toArray)
  )
}
