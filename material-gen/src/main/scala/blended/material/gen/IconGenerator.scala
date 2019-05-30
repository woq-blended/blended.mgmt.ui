package blended.material.gen

import java.util.regex.Pattern

class IconGenerator(sourceFile: String, targetFile: String) extends AbstractGenerator(sourceFile, targetFile) {

  private[this] val usedIcons : Seq[String] = Seq("AddCircle", "RemoveCircle")

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
    s"""package blended.material.ui
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
       |    def createIcon(componentClass: Any, clazzes : Map[String, CssClass]) : JsComponentConstructor = {
       |      val effectiveChildren : Seq[JsTag]= if (clazzes.nonEmpty) {
       |        Seq(J("classes", clazzes.map( c => c._1 -> c._2.name).toJSDictionary))
       |      } else {
       |        Seq.empty
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
         |    def apply(): JsComponentConstructor =
         |      Styles.withStyles(S.color("#808080"))(createIcon($icon, Map.empty))
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
