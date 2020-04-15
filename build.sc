import coursier.Repository
import coursier.maven.MavenRepository
import mill._
import mill.define._
import mill.scalajslib.ScalaJSModule
import mill.scalajslib.api.ModuleKind
import mill.scalalib._
import mill.scalalib.publish._

object Deps {
  val scalaVersion = "2.12.11"
  val scalaJSVersion = "0.6.32"
}

trait BlendedCoursierModule extends CoursierModule {
  private def zincWorker: ZincWorkerModule = mill.scalalib.ZincWorkerModule
  override def repositories: Seq[Repository] = zincWorker.repositories ++ Seq(
    MavenRepository("https://oss.sonatype.org/content/repositories/snapshots/")
  )
}

trait BlendedPublishModule extends PublishModule {
  def description: String = "Blended module ${blendedModule}"
  override def publishVersion = T { blended.version() }
  override def pomSettings: T[PomSettings] = T {
    PomSettings(
      description = description,
      organization = "de.wayofquality.blended",
      url = "https://github.com/woq-blended",
      licenses = Seq(License.`Apache-2.0`),
      versionControl = VersionControl.github("woq-blended", "blended"),
      developers = Seq(
        Developer("atooni", "Andreas Gies", "https://github.com/atooni"),
        Developer("lefou", "Tobias Roeser", "https://github.com/lefou")
      )
    )
  }
}

trait BlendedModule extends SbtModule with ScalaModule with BlendedPublishModule with BlendedCoursierModule {
  def blendedModule: String = millModuleSegments.parts.mkString(".")
  override def artifactName: T[String] = blendedModule
  override def scalaVersion : T[String] = Deps.scalaVersion

  override def millSourcePath : os.Path = baseDir / millModuleSegments.parts.last
}

trait BlendedJSModule extends BlendedModule with ScalaJSModule { jsBase =>
  override def scalaJSVersion : T[String]  = T {"0.6.32"}

  trait Tests extends super.Tests {
    def blendedTestModule : String = jsBase.blendedModule + ".test"
    override def artifactName = blendedTestModule

    override def millSourcePath = jsBase.millSourcePath / "src" / "test"

    override def sources: Sources = T.sources(
      millSourcePath / "scala",
    )
    override def ivyDeps = T{ super.ivyDeps() ++ Agg(
      ivy"org.scalatest::scalatest::3.0.8"
    )}
    override def testFrameworks = Seq("org.scalatest.tools.Framework")
    override def moduleKind: T[ModuleKind] = T{ ModuleKind.CommonJSModule }
  }
}

/** Project directory. */
val baseDir: os.Path = build.millSourcePath

object blended extends Module {

  def version = T.input {
    os.read(baseDir / "version.txt").trim()
  }

  object mgmt extends Module {
    object ui extends Module {
      object common extends BlendedJSModule {

        override def ivyDeps = super.ivyDeps() ++ Agg(
          ivy"com.github.ahnfelt::react4s::0.9.27-SNAPSHOT",
          ivy"org.scala-js::scalajs-dom::0.9.5"
        )

        override def moduleDeps = super.moduleDeps ++ Seq(router)
      }
      object router extends BlendedJSModule {
        object test extends super.Tests
      }
    }
  }
}

