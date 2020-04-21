package blended.material.gen

import java.io.File
import java.nio.file.Files

abstract class AbstractGenerator(sourceFile : String, targetFile: String) {

  protected lazy val source : Seq[String] = {
    val bufferedSource = io.Source.fromFile(new File(sourceFile))
    val lines = (for (line <- bufferedSource.getLines()) yield line).toSeq
    lines
  }

  protected def writeFile(targetFile : File, content: String) : Unit = {
    val f = targetFile.toPath()
    Files.write(f, content.getBytes())
  }

  final def generate() : Unit = {

    val srcFile = new File(sourceFile)
    val genFile = new File(targetFile)

    val generate : Boolean = !genFile.exists() || genFile.lastModified() <= srcFile.lastModified()

    if (generate) {
      println(s"Generating file [${genFile.getAbsolutePath()}]")
      writeFile(genFile, doGenerate())
    }
  }

  protected def doGenerate() : String
}
