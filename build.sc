import coursier.Repository
import coursier.maven.MavenRepository
import mill._
import mill.define._
import mill.scalajslib.ScalaJSModule
import mill.scalajslib.api.ModuleKind
import mill.scalalib._
import mill.scalalib.publish._
import $file.build_util
import build_util.{FilterUtil, ZipUtil}
import mill.modules.Jvm

object Deps {
  val blendedCoreVersion = "3.2-SNAPSHOT"
  val scalaVersion = "2.12.11"
  val scalaJSVersion = "0.6.32"

  val logbackVersion = "1.2.3"
  val slf4jVersion = "1.7.25"

  val cmdOption = ivy"de.tototec:de.tototec.cmdoption:0.6.0"
  val logbackCore = ivy"ch.qos.logback:logback-core:${logbackVersion}"
  val logbackClassic = ivy"ch.qos.logback:logback-classic:${logbackVersion}"
  val slf4j = ivy"org.slf4j:slf4j-api:${slf4jVersion}"

  object Js {
    val react4s = ivy"com.github.ahnfelt::react4s::0.9.27-SNAPSHOT"
    val scalaJsDom = ivy"org.scala-js::scalajs-dom::0.9.5"
    val scalatest = ivy"org.scalatest::scalatest::3.0.8"

    val blendedJmx = ivy"de.wayofquality.blended::blended.jmx::$blendedCoreVersion"
  }
}

/** Project directory. */
val baseDir: os.Path = build.millSourcePath

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
      versionControl = VersionControl.github("woq-blended", "blended.mgmt.ui"),
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
  override def scalaJSVersion : T[String]  = Deps.scalaJSVersion

  override def moduleKind: T[ModuleKind] = T{ ModuleKind.CommonJSModule }

  trait Tests extends super.Tests {
    def blendedTestModule : String = jsBase.blendedModule + ".test"
    override def artifactName = blendedTestModule

    override def millSourcePath = jsBase.millSourcePath / "src" / "test"

    override def sources: Sources = T.sources(
      millSourcePath / "scala"
    )
    override def ivyDeps = T{ super.ivyDeps() ++ Agg(
      Deps.Js.scalatest
    )}
    override def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}

trait YarnUtils extends Module {

  def npmModulesDir : String = "node-modules"

  def yarnInstall : T[PathRef] = T {
    val log = T.ctx().log
    val modules = T.ctx().dest / npmModulesDir
    val result = os.proc("yarn", "install", "--modules-folder", modules.toIO.getAbsolutePath()).call(cwd = baseDir)
    log.info(new String(result.out.bytes))
    PathRef(modules)
  }
}

object blended extends Module {

  def version = T.input {
    os.read(baseDir / "version.txt").trim()
  }

  object mgmt extends Module {
    object ui extends Module {
      object common extends BlendedJSModule {

        override def ivyDeps = T { super.ivyDeps() ++ Agg(
          Deps.Js.react4s,
          Deps.Js.scalaJsDom
        )}

        override def moduleDeps = super.moduleDeps ++ Seq(router)
      }
      object router extends BlendedJSModule {
        object test extends super.Tests
      }

      object materialGen extends BlendedModule {
        override def blendedModule = "blended.mgmt.ui.material.gen"

        override def millSourcePath = baseDir / "material-gen"

        override def mainClass = Some("blended.material.gen.MaterialGenerator")

        override def ivyDeps = T { super.ivyDeps() ++ Agg(
          Deps.cmdOption,
          Deps.slf4j,
          Deps.logbackCore,
          Deps.logbackClassic
        )}
      }

      object material extends YarnUtils with BlendedJSModule {

        override def generatedSources = T {

          val npmModules = yarnInstall()

          val genTarget = T.ctx().dest / "generatedSources"

          val generate = Jvm.runSubprocess(
            mainClass = materialGen.mainClass().get,
            classPath = materialGen.runClasspath().map(_.path),
            mainArgs = Seq(
              "-d", (npmModules.path / "@material-ui").toIO.getAbsolutePath(),
              "-o", genTarget.toIO.getAbsolutePath()
            )
          )

          super.generatedSources() ++ Seq(PathRef(genTarget))
        }

        override def ivyDeps = T { super.ivyDeps() ++ Agg(
          Deps.Js.react4s
        )}
      }

      object theme extends BlendedJSModule {
        override def moduleDeps = super.moduleDeps ++ Seq(material)
      }

      object components extends BlendedJSModule {
        override def moduleDeps = super.moduleDeps ++ Seq(material, theme)

        override def ivyDeps = T { super.ivyDeps() ++ Agg(
          Deps.Js.blendedJmx
        )}
      }

      object sampleApp extends BlendedJSModule {

        override def moduleDeps = super.moduleDeps ++ Seq(common, components)

        override def ivyDeps = T { super.ivyDeps() ++ Agg(
          Deps.Js.react4s,
          Deps.Js.scalaJsDom
        )}
      }
    }
  }
}

