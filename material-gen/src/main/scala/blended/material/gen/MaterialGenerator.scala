package blended.material.gen

import java.io.File
import java.nio.file.{Files, OpenOption, Path, Paths}

import de.tototec.cmdoption._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

object MaterialGenerator {

  private[this] val log = LoggerFactory.getLogger(classOf[MaterialGenerator])

  class CmdLine {

    @CmdOption(names = Array("--help", "-h"), description = "Show this help", isHelp = true)
    var help = false

    @CmdOption(names = Array("-d"), args = Array("String"), description = "The MaterialUI base directory")
    var dir = "."

    @CmdOption(names = Array("-o"), args = Array("String"), description = "The output base directory")
    var out = "./gen"
  }

  private[this] def reportError(msg: String): Unit = {
    //scalastyle:off regex
    log.error(msg)
    Console.err.println(msg)
    sys.error(msg)
    //scalastyle:on regex
  }

  private[this] def parseArgs(args: Array[String]): Try[CmdLine] = Try {
    val cmdline = new CmdLine()
    val cp = new CmdlineParser(cmdline)
    try {
      cp.parse(args: _*)
    } catch {
      case e: CmdlineParserException =>
        reportError(s"${e.getMessage()}\nRun launcher --help for help.")
    }

    if (cmdline.help) {
      cp.usage(System.out)
      System.exit(0)
    }

    cmdline
  }

  def main(args: Array[String]) : Unit = {

    parseArgs(args) match {
      case Success(options) =>
        new MaterialGenerator(options).run()
      case Failure(t) =>
        reportError(t.getMessage())
    }
  }
}

class MaterialGenerator(options: MaterialGenerator.CmdLine) {

  def writeFile(dir: File, fileName: String, content: String) : Unit = {
    val f = new File(dir, fileName).toPath()
    Files.write(f, content.getBytes())
  }

  def run() : Unit = {

    val targetDir = new File(options.out + "/blended/ui/material")
    Files.createDirectories(targetDir.toPath())

    val componentIndex = options.dir + "/core/index.js"
    writeFile(targetDir, "MaterialUI.scala", new ComponentGenerator(componentIndex).generate())

    val colorIndex = options.dir + "/core/colors/index.js"
    writeFile(targetDir, "Colors.scala", new ColorGenerator(colorIndex).generate())
  }
}