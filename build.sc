import coursier.Repository
import coursier.maven.MavenRepository
import mill._
import mill.define._
import mill.scalajslib.ScalaJSModule
import mill.scalajslib.api.ModuleKind
import mill.scalalib._
import mill.scalalib.publish._
import $file.build_util
import ammonite.ops.Path
import build_util.{FilterUtil, ZipUtil}
import mill.modules.Jvm
import os.RelPath

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
    val react4s = ivy"com.github.ahnfelt::react4s::0.9.28-SNAPSHOT"
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

trait WebUtils extends Module {

  // The node modules directory to be used - This should be "node_modules" located in the base directory
  // because normally webpack operations search the node_modules in parent directories as well, So all
  // mill modules would find the npm modules
  def npmModulesDir : String = "node_modules"

  // If the mill module should be packaged as a web application, we need to point the module to the output
  // of a fastOptJS or fullOptJS step
  def packagedWebApp : T[Option[PathRef]] = None

  // The appname that is used in various generator steps
  def appName : String

  // The App title that will appear in a generated html if required
  def appTitle : Option[String] = None

  // The port a webpack dev server starts on
  def webPackDevServerPort : Int = 9000

  // Html sources that will be copied into the packaged web app folder
  def htmlSources = T.sources(millSourcePath / "src" / "html")

  def packagedJsLibs : Seq[String] = Seq()

  // run yarn install and download all resources that are defined in the
  // package.json of the project root
  def yarnInstall : T[PathRef] = T {
    val modules = baseDir / npmModulesDir
    val result = os.proc("yarn", "install").call(cwd = baseDir)
    T.log.info(new String(result.out.bytes))
    PathRef(modules)
  }

  // A simple index page that will reference the web app that has been run through webpack
  // and additional libs
  def simpleIndexPage : String = {
    val packagedLibs : String = packagedJsLibs.map { lib =>
      val libName = RelPath(lib).last
      s"""<script type="text/javascript" src="./$libName"></script>"""
    }.mkString("\n")

    s"""<!DOCTYPE html>
       |<!--suppress ALL -->
       |<html>
       |<head>
       |  <meta charset="UTF-8">
       |  <title>${appTitle.getOrElse(appName)}</title>
       |</head>
       |<body>
       |
       |<div id="content"></div>
    """.stripMargin + packagedLibs +
      s"""
         |<script type="text/javascript" src="./$appName.js"></script>
         |
         |</body>
         |</html>
         |""".stripMargin
  }

  // Run webpack with a generated config (to cover the simplest case of running some fastOptJS or fullOptJS output
  // thorugh webpack)
  def webpack : T[PathRef] = T {

    val cfgFromSource : Path = millSourcePath / "webpack.config.js"
    val dist : Path = T.dest / "webpack"

    val usedCfg : Path = if (cfgFromSource.toIO.exists()) {
      cfgFromSource
    } else {
      val webpackConfig : String = packagedWebApp() match {
        case None => throw new Exception("a packaged application must be defined to generate a webpack configuration")
        case Some(app) =>
          s"""const path = require('path');
             |
             |module.exports = {
             |  entry: '${app.path.toIO.getAbsolutePath()}',
             |  output: {
             |    filename: '$appName.js',
             |    path: '$dist',
             |  },
             |  devServer: {
             |    contentBase: '$dist',
             |    port : $webPackDevServerPort
             |  },
             |  devtool: "source-map",
             |  "module": {
             |    "rules": [{
             |      "test": new RegExp("\\.js$$"),
             |      "enforce": "pre",
             |      "use": ["source-map-loader"]
             |    }]
             |  }
             |};
             |""".stripMargin

      }
      val generatedCfg : Path = T.dest / "webpack.config.js"
      os.write(generatedCfg, webpackConfig)
      generatedCfg
    }

    val rc = os.proc("webpack-cli", "--config", usedCfg.toIO.getAbsolutePath()).call(cwd = millSourcePath)
    T.log.info(new String(rc.out.bytes))
    PathRef(dist)
  }

  // create a directory that has everuthing to serve a webapp
  def packageHtml : T[PathRef] = T {

    val dist : Path = T.dest / "dist"
    val bundledApp : Path = webpack().path

    os.copy(from = bundledApp, to = dist)

    htmlSources().foreach{ref =>
      if (ref.path.toIO.exists()) {
        os.list(ref.path).iterator.foreach { p =>
          os.copy.into(p, dist)
        }
      }
    }

    val modules : Path = baseDir / npmModulesDir

    packagedJsLibs.foreach{ lib =>
      val file : Path = modules / RelPath(lib)
      os.copy.into(file, dist)
    }

    val index : Path = dist / "index.html"
    if (!index.toIO.exists()) {
      T.log.info(s"Generating simple index.html to [$dist]")
      os.write(index, simpleIndexPage)
    } else {
      T.log.info("Using index.html from sources")
    }
    PathRef(dist)
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

      object material extends WebUtils with BlendedJSModule {

        override def appName = blendedModule

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

      object sampleApp extends WebUtils with BlendedJSModule {

        override def appName = blendedModule

        override def packagedJsLibs = Seq(
          "react/umd/react.development.js",
          "react-dom/umd/react-dom.development.js"
        )

        override def packagedWebApp = T {
          Some(fastOpt())
        }

        override def moduleDeps = super.moduleDeps ++ Seq(common, components)

        override def ivyDeps = T { super.ivyDeps() ++ Agg(
          Deps.Js.react4s,
          Deps.Js.scalaJsDom
        )}
      }
    }
  }
}

