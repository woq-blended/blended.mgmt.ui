package blended.material.gen

import java.util.regex.Pattern

class IconGenerator(sourceFile: String, targetFile: String) extends AbstractGenerator(sourceFile, targetFile) {

  private[this] val usedIcons : Seq[String] = Seq(
    "AddCircle", "RemoveCircle",
  )

  private[this] val iconNames = {

    val pattern : Pattern = Pattern.compile("var _([A-Z][^=].*)=.*")

    source.filter { line =>
      val matcher = pattern.matcher(line)
      matcher.matches()
    }.map { line =>
      val matcher = pattern.matcher(line)
      matcher.find()
      matcher.group(1).trim()
    }
  }.filter(usedIcons.contains)

  private[this] val prelude =
    s"""package ${MaterialGenerator.pkgName}
       |
       |import com.github.ahnfelt.react4s._
       |import scala.scalajs.js
       |import scala.scalajs.js.JSConverters._
       |
       |import scala.scalajs.js.annotation.JSImport
       |
       |object MatIcons {
       |
       |  trait RichMatIcon{
       |
       |    def createIcon(componentClass: Any, clazzes : Map[String, CssClass], children: JsTag*) : JsComponentConstructor = {
       |      val effectiveChildren : Seq[JsTag]= if (clazzes.nonEmpty) {
       |        J("classes", clazzes.map( c => c._1 -> c._2.name).toJSDictionary) +: children
       |      } else {
       |        children
       |      }
       |
       |      JsComponentConstructor(componentClass, effectiveChildren, None, None)
       |    }
       |  }
       |
       |""".stripMargin

  override def doGenerate(): String = {

    val toIcon : String => String = { icon =>
      s"""
         |  @js.native
         |  @JSImport("@material-ui/icons/$icon", JSImport.Default)
         |  private object $icon extends js.Object
       """.stripMargin
    }

    val toObject : String => String = { icon =>
      s"""
         |  object ${icon}Icon extends RichMatIcon {
         |    def apply(clazzes : Map[String, CssClass])(children : JsTag*): JsComponentConstructor = createIcon($icon, clazzes, children:_*)
         |    def apply(children : JsTag*) : JsComponentConstructor = createIcon($icon, Map.empty, children:_*)
         |  }
       """.stripMargin
    }

    val icons = iconNames.map(toIcon)
    val objects = iconNames.map(toObject)

    prelude +
      icons.mkString("") +
      objects.mkString("") +
      "\n}\n"
  }

}
