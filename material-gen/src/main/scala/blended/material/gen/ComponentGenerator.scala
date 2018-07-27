package blended.material.gen

import java.io.File
import java.util.regex.Pattern

import org.slf4j.LoggerFactory

class ComponentGenerator(fileName: String) {

  private[this] val log = LoggerFactory.getLogger(classOf[ComponentGenerator])

  private[this] val content : Seq[String] = {
    val bufferedSource = io.Source.fromFile(new File(fileName))
    val lines = (for (line <- bufferedSource.getLines()) yield line).toSeq
    lines
  }

  private[this] val componentNames : Seq[String] = {

    val pattern : Pattern = Pattern.compile("var _([A-Z][^=].*)=.*")

    content.filter { line =>
      val matcher = pattern.matcher(line)
      matcher.matches()
    }.map { line =>
      val matcher = pattern.matcher(line)
      matcher.find()
      matcher.group(1).trim()
    }
  }

  private[this] val prelude : String =
    s"""package blended.ui.material
       |
       |import com.github.ahnfelt.react4s._
       |import scala.scalajs.js
       |import scala.scalajs.js.annotation.JSImport
       |
       |@js.native
       |@JSImport("@material-ui/core", JSImport.Namespace)
       |object MaterialUI extends js.Object {
       |""".stripMargin

  private[this] val toComponent : String => String = { s => s"  val $s : js.Dynamic = js.native" }
  private[this] val toObject : String => String = { s => s"object $s extends JsComponent(MaterialUI.$s)"}

  def generate() : String = {

    prelude +
      componentNames.map(toComponent).mkString("\n") +
      "\n}\n\n" +
      componentNames.map(toObject).mkString("\n") +
      "\n"
  }
}
