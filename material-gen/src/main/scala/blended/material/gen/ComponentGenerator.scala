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
       |import js.JSConverters._
       |
       |object MaterialUI {
       |
       |  @js.native
       |  @JSImport("@material-ui/core", JSImport.Namespace)
       |  private[this] object MatComponents extends js.Object {
       |""".stripMargin

  private[this] val toComponent : String => String = { s => s"    val $s : js.Dynamic = js.native" }
  private[this] val toObject : String => String = { s =>
    s"""  object $s extends RichMatComponent {
       |    def apply(clazzes: Map[String, CssClass])(children: JsTag*) : JsComponentConstructor = createComponent(MatComponents.$s, clazzes, children:_*)
       |    def apply(children: JsTag*) : JsComponentConstructor = createComponent(MatComponents.$s, Map.empty, children:_*)
       |  }
     """.stripMargin
  }

  def doGenerate() : String = {

    prelude +
      componentNames.map(toComponent).mkString("\n") +
      "\n  }\n" +
    """
      |   trait RichMatComponent{
      |
      |    def createComponent(componentClass: Any, clazzes : Map[String, CssClass], children: JsTag*) : JsComponentConstructor = {
      |
      |      val effectiveChildren = if (!clazzes.isEmpty) {
      |        J("classes", clazzes.map( c => c._1 -> c._2.name).toJSDictionary) +: children
      |      } else {
      |        children
      |      }
      |
      |      JsComponentConstructor(componentClass, effectiveChildren, None, None)
      |    }
      |  }
      |""".stripMargin +
      componentNames.map(toObject).mkString("\n") +
      "\n}\n"
  }
}
