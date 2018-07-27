package blended.material.gen

import java.io.File

abstract class AbstractGenerator(fileName : String) {

  val content : Seq[String] = {
    val bufferedSource = io.Source.fromFile(new File(fileName))
    val lines = (for (line <- bufferedSource.getLines()) yield line).toSeq
    lines
  }

  def generate() : String

}
