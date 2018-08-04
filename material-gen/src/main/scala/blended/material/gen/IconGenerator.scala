package blended.material.gen

import java.util.regex.Pattern

class IconGenerator(fileName: String) extends AbstractGenerator(fileName) {

  private[this] val colorNames = {

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

  private[this] val prelude =
    s"""package blended.material.ui
       |
       |import com.github.ahnfelt.react4s._
       |import scala.scalajs.js
       |
       |import scala.scalajs.js.annotation.JSImport
       |
       |object Icons {
       |  @js.native
       |  @JSImport("@material-ui/icons", JSImport.Namespace)
       |  private object MatIcons extends js.Object {
       |""".stripMargin

  override def generate(): String = {

    val icons = colorNames.map(s => s"    val $s : js.Dynamic = js.native")
    val objects = colorNames.map(s => s"  object ${s}Icon extends JsComponent(MatIcons.$s)")

    prelude +
      icons.mkString("", "\n", "\n") +
      "  }\n\n" +
      objects.mkString("", "\n", "\n") +
      "}\n"
  }

}
