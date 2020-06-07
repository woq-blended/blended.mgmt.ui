import coursierapi.{Credentials, MavenRepository}
import os.Path

val blendedMillVersion : String = "v0.1-12-c862fa"

interp.repositories() ++= Seq(
  MavenRepository.of(s"https://u233308-sub2.your-storagebox.de/blended-mill/$blendedMillVersion")
    .withCredentials(Credentials.of("u233308-sub2", "px8Kumv98zIzSF7k"))
)

interp.load.ivy("de.wayofquality.blended" %% "blended-mill" % blendedMillVersion)

@

import coursier.Repository
import coursier.maven.MavenRepository
import mill._
import mill.define.{Sources, Task, Target}
import mill.scalajslib.ScalaJSModule
import mill.scalajslib.api.ModuleKind
import mill.scalalib._
import mill.scalalib.publish._
import ammonite.ops.Path
import coursier.core.Authentication
import mill.modules.Jvm
import os.RelPath

import $file.build_deps
import build_deps.UiDeps

import mill.api.Loose

// This import the mill-osgi plugin
import $ivy.`de.tototec::de.tobiasroeser.mill.osgi:0.3.0`
import de.tobiasroeser.mill.osgi._

// imports from the blended-mill plugin
import de.wayofquality.blended.mill.versioning.GitModule
import de.wayofquality.blended.mill.publish.BlendedPublishModule
import de.wayofquality.blended.mill.webtools.WebTools
import de.wayofquality.blended.mill.modules._
import de.wayofquality.blended.mill.utils._

/** Project directory. */
val projectDir: os.Path = {
  build.millSourcePath
}

object GitSupport extends GitModule {
  override def millSourcePath: Path = projectDir
}

def blendedVersion = T { GitSupport.publishVersion() }

trait WebUtils extends Module {

  // The node modules directory to be used - This should be "node_modules" located in the base directory
  // because normally webpack operations search the node_modules in parent directories as well, So all
  // mill modules would find the npm modules
  def npmModulesDir : Path = projectDir / "node_modules"

  def nodeVersion() = T.command {
    os.proc("node", "-v").call().out.text()
  }

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
    val modules = npmModulesDir
    val result = os.proc("yarn", "install", "--modules-folder", modules).call(cwd = projectDir)
    T.log.info(new String(result.out.bytes))

    val yarnErrors : os.Path = projectDir / "yarn-error.log"
    if ( (yarnErrors.toIO.exists()) ) {
      T.log.info(new String(os.read.bytes(yarnErrors)))
    }

    PathRef(modules)
  }

  // A simple index page that will reference the web app that has been run through webpack
  // and additional libs
  def simpleIndexPage(dir : Path) : String = {

    val scriptLine : String => String = libName => s"""<script type="text/javascript" src="./$libName"></script>"""

    val packagedLibs : String = os.walk(dir)
      .filter(_.last.endsWith(".js"))
      .map(_.relativeTo(dir))
      .map(lib => scriptLine(lib.toString()))
      .mkString("\n")

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
         |</body>
         |</html>
         |""".stripMargin
  }

  // Run webpack with a generated config (to cover the simplest case of running some fastOptJS or fullOptJS output
  // thorugh webpack)
  def webpack : T[PathRef] = T {

    if (!nodeVersion()().startsWith("v12")) {
      sys.error("Please install Node version 12 according to node install instructions for your environment.")
    }

    val cfgFromSource : Path = millSourcePath / "webpack.config.js"
    val dist : Path = T.dest / "webpack"

    val usedCfg : Path = if (cfgFromSource.toIO.exists()) {
      cfgFromSource
    } else {
      val webpackConfig : String = packagedWebApp() match {
        case None => throw new Exception("a packaged application must be defined to generate a webpack configuration")
        case Some(app) =>
          s"""const path = require('path');
             |const webpack = require('webpack');
             |
             |module.exports = {
             |  entry: '${app.path.toIO.getAbsolutePath()}',
             |  plugins: [
             |    new webpack.HashedModuleIdsPlugin(), // so that file hashes don't change unexpectedly
             |  ],
             |  output: {
             |    path: '$dist',
             |    filename: '[name].[contenthash].js',
             |  },
             |  optimization: {
             |    runtimeChunk: 'single',
             |    splitChunks: {
             |      chunks: 'all',
             |      maxInitialRequests: Infinity,
             |      minSize: 0,
             |      cacheGroups: {
             |        vendor: {
             |          test: /[\\/]node_modules[\\/]/,
             |          name(module) {
             |            // get the name. E.g. node_modules/packageName/not/this/part.js
             |            // or node_modules/packageName
             |            const packageName = module.context.match(/[\\/]node_modules[\\/](.*?)([\\/]|$$)/)[1];
             |            // npm package names are URL-safe, but some servers don't like @ symbols
             |            return `npm.$${packageName.replace('@', '')}`;
             |          },
             |        },
             |      },
             |    },
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

    val modules = yarnInstall().path

    val rc = os.proc("node", s"$modules/webpack-cli/bin/cli.js", "--progress", "--config", usedCfg.toIO.getAbsolutePath()).call(cwd = millSourcePath)
    T.log.info(new String(rc.out.bytes))
    PathRef(dist)
  }

  // create a directory that has everuthing to serve a webapp
  def packageHtml : T[PathRef] = T {

    val dist : Path = T.dest / "dist"
    val bundledApp : Path = webpack().path

    os.copy(from = bundledApp, to = dist)

    htmlSources().foreach{ ref =>
      if (ref.path.toIO.exists()) {
        os.list(ref.path).iterator.foreach { p =>
          os.copy.into(p, dist)
        }
      }
    }

    val modules : Path = npmModulesDir

    packagedJsLibs.foreach{ lib =>
      val file : Path = modules / RelPath(lib)
      os.copy.into(file, dist)
    }

    val index : Path = dist / "index.html"
    if (!index.toIO.exists()) {
      T.log.info(s"Generating simple index.html to [$dist]")
      os.write(index, simpleIndexPage(dist))
    } else {
      T.log.info("Using index.html from sources")
    }
    PathRef(dist)
  }

  def devServer : T[PathRef] = T {
    val distDir = packageHtml().path.toIO.getAbsolutePath()
    val rc = os.proc(s"$npmModulesDir/webpack-dev-server/bin/webpack-dev-server.js",  "--content-base", distDir,  "--port", s"$webPackDevServerPort").call(cwd = projectDir)
    PathRef(T.dest)
  }
}

object blended extends Cross[BlendedUiCross](UiDeps.scalaVersions.keys.toSeq:_*)
class BlendedUiCross(crossScalaVersion : String) extends GenIdeaModule { blended =>

  val crossDeps = UiDeps.scalaVersions(crossScalaVersion)

  def version = T.input {
    os.read(projectDir / "version.txt").trim()
  }

  trait UiCoursierModule extends CoursierModule {
    private def zincWorker: ZincWorkerModule = mill.scalalib.ZincWorkerModule
    override def repositories: Seq[Repository] = zincWorker.repositories ++ Seq(
      MavenRepository("https://oss.sonatype.org/content/repositories/snapshots/"),
      MavenRepository(
        s"https://u233308-sub2.your-storagebox.de/blended-core/${crossDeps.blendedCoreVersion}",
        Some(Authentication("u233308-sub2", "px8Kumv98zIzSF7k"))
      )
    )
  }

  trait UiPublishModule extends BlendedPublishModule {
    def githubRepo : String = "blended.mgmt.ui"
    def scpTargetDir : String = "mgmt-ui"

    override def publishVersion = T { blendedVersion() }
  }

  trait UiModule extends BlendedJvmModule
    with UiCoursierModule
    with UiPublishModule { uiModule =>

    override def description = s"Blended Module $blendedModule"
    override def baseDir : os.Path = projectDir
    override def scalaVersion = crossDeps.scalaVersion

    override def scalacOptions = T { super.scalacOptions().filter(_ != "-Werror") }

    trait UiJs extends super.BlendedJs with UiPublishModule with UiCoursierModule {
      override def millSourcePath = uiModule.millSourcePath
    }

    override type ProjectDeps = UiDeps
    override def deps = crossDeps
  }

  trait UiJsModule extends BlendedJsModule
    with UiCoursierModule
    with UiPublishModule {

    override def description = s"Blended Module $blendedModule"

    override def baseDir : os.Path = projectDir

    override type ProjectDeps = UiDeps
    override def deps = crossDeps

    override def scalacOptions = T { super.scalacOptions().filter(_ != "-Werror") }
  }

  trait UiWebBundle extends BlendedWebModule with UiModule {
    override def blendedCoreVersion = crossDeps.blendedCoreVersion
  }

  object mgmt extends Module {
    object ui extends Module {
      object common extends UiJsModule {

        override def millSourcePath = projectDir / "common"

        override def ivyDeps = T { super.ivyDeps() ++ Agg(
          deps.Js.react4s,
          deps.Js.scalaJsDom
        )}

        override def moduleDeps = super.moduleDeps ++ Seq(router)
      }

      object router extends UiJsModule {
        override def millSourcePath = projectDir / "router"

        object test extends super.BlendedJsTests
      }

      object materialGen extends UiModule {
        override def description = "Generator classes for Material UI"

        override def blendedModule = "blended.mgmt.ui.material.gen"

        override def millSourcePath = projectDir / "materialGen"

        override def mainClass = Some("blended.material.gen.MaterialGenerator")

        override def ivyDeps = T { super.ivyDeps() ++ Agg(
          deps.cmdOption,
          deps.slf4j,
          deps.logbackCore,
          deps.logbackClassic
        )}
      }

      object material extends UiJsModule with WebUtils {

        override def millSourcePath = projectDir / "material"
        override def appName = blendedModule

        override def generatedSources = T {

          val npmModules = yarnInstall()

          val genTarget = T.dest / "generatedSources"

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

        override def ivyDeps = T {
          super.ivyDeps() ++ Agg(
            deps.Js.react4s
          )
        }
      }

      object theme extends UiJsModule {
        override def millSourcePath = baseDir / "theme"
        override def moduleDeps = super.moduleDeps ++ Seq(material)
      }

      object components extends UiJsModule {

        override def millSourcePath = baseDir / "components"
        override def moduleDeps = super.moduleDeps ++ Seq(material, theme)

        override def ivyDeps = T {
          super.ivyDeps() ++ Agg(
            deps.Js.blendedJmx
          )
        }
      }

      object sampleApp extends UiJsModule with WebUtils {

        override def millSourcePath = baseDir / "sampleApp"

        override def appName = blendedModule

        override def appTitle = Some("Blended Component Playground")

        override def packagedJsLibs = Seq(
          "react/umd/react.development.js",
          "react-dom/umd/react-dom.development.js"
        )

        override def packagedWebApp = T {
          Some(fastOpt())
        }

        override def moduleDeps = super.moduleDeps ++ Seq(common, components)

        override def ivyDeps = T {
          super.ivyDeps() ++ Agg(
            deps.Js.react4s,
            deps.Js.scalaJsDom
          )
        }

        object webBundle extends UiWebBundle {
          override def webContext = "sample"
          override def exportPackages = Seq.empty
          override def bundleSymbolicName = millModuleSegments.parts.filter(_ != crossDeps.scalaVersion).mkString(".")
          override def bundleActivator = "blended.mgmt.ui.SampleActivator"

          override def webContent = T {
            sampleApp.packageHtml()
          }
        }
      }

      object mgmtApp extends UiJsModule with WebUtils {

        override def millSourcePath = baseDir / "mgmt-app"

        override def appName = blendedModule

        override def appTitle = Some("Blended Management Console")

        override def packagedJsLibs = Seq(
          "react/umd/react.development.js",
          "react-dom/umd/react-dom.development.js"
        )

        override def packagedWebApp = T {
          Some(fastOpt())
        }

        override def moduleDeps = super.moduleDeps ++ Seq(common, components)

        override def ivyDeps = T {
          super.ivyDeps() ++ Agg(
            deps.Js.blendedUpdaterConfig,
            deps.Js.blendedJmx,
            deps.Js.blendedSecurity,
            deps.Js.akkaJsActor,
            deps.Js.react4s,
            deps.Js.scalaJsDom
          )
        }

        object webBundle extends UiWebBundle {
          override def webContext = "management"
          override def exportPackages = Seq.empty
          override def bundleActivator = "blended.mgmt.ui.MgmtActivator"
          override def bundleSymbolicName = millModuleSegments.parts.filter(_ != crossDeps.scalaVersion).mkString(".")

          override def webContent = T {
            mgmtApp.packageHtml()
          }
        }

      }

      object mgmtAppSelenium extends UiModule {

        override def description = "Selenium tests for the Management UI"

        override def millSourcePath = baseDir / "mgmtAppSelenium"

        object test extends super.BlendedJvmTests {

          override def ivyDeps = T { super.ivyDeps() ++ Agg(
            deps.akkaActor,
            deps.akkaStream,
            deps.akkaHttp,
            deps.akkaHttpCore,
            deps.akkaTestkit,
            deps.selenium,
            deps.scalatestSelenium
          )}

          override def forkArgs = T {
            val appDir = mgmtApp.packageHtml().path
            super.forkArgs() ++ Seq(
              s"-DappUnderTest=$appDir"
          )}
        }
      }
    }
  }
}

