package blended.material.gen

import java.util.regex.Pattern

class ComponentGenerator(sourceFile : String, targetFile: String) extends AbstractGenerator(sourceFile, targetFile) {

  private[this] val componentNames : Seq[String] = {

    val pattern : Pattern = Pattern.compile("var _([A-Z][^=].*)=.*")

    source.filter { line =>
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
       |object MaterialUI {
       |
       |  @js.native
       |  @JSImport("@material-ui/core", JSImport.Namespace)
       |  private[this] object MatComponents extends js.Object {
       |""".stripMargin

  private[this] val toComponent : String => String = { s => s"    val $s : js.Dynamic = js.native" }
  private[this] val toObject : String => String = { s => s"  object $s extends JsComponent(MatComponents.$s)"}

  def doGenerate() : String = {

    prelude +
      componentNames.map(toComponent).mkString("\n") +
      "\n  }\n\n" +
      componentNames.map(toObject).mkString("\n") +
      "\n}\n"
  }
}
