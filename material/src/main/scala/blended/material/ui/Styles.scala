package blended.material.ui

import com.github.ahnfelt.react4s._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object Styles {

  @js.native
  @JSImport("@material-ui/core/styles", JSImport.Namespace)
  private[this] object StylesImpl extends js.Object {
    val MuiThemeProvider : js.Dynamic = js.native
    val createMuiTheme : js.Dynamic = js.native
    val withStyles : js.Function1[js.Any, js.Function1[Any, js.Any]] = js.native
    val withTheme : js.Function1[js.Any, js.Function1[Any, js.Any]] = js.native
  }

  def withStyles(jsProps : Seq[JsProp]) : ElementOrComponent => ElementOrComponent = {

    case comp : JsComponentConstructor => JsComponentConstructor(comp.componentClass, comp.children ++ jsProps, comp.key, comp.ref)
    case elem : Element => elem.copy(children = jsProps.map(p => Attribute(p.name, p.value)) ++ elem.children)
    case o => o
  }

  def withStyles(s : Styles) : JsComponentConstructor => JsComponentConstructor =
    withStyles(Map("root" -> s))

  def withStyles(s : Map[String, Styles]) : JsComponentConstructor => JsComponentConstructor = { c =>

    def muiStyle(s: Map[String, Styles]) : js.Any = js.Dictionary[js.Any](s.map{ case (k,v) =>
      k -> v.asJsAny
    }.toSeq:_*)

    val innerComponent = c.componentClass
    val styledComponent = StylesImpl.withStyles(muiStyle(s))(innerComponent)

    JsComponentConstructor(
      componentClass = styledComponent,
      c.children,
      c.key,
      c.ref
    )
  }

  def withTheme(theme: js.Any) : JsComponentConstructor => JsComponentConstructor = { c =>

    val innerComponent = c.componentClass
    val themedComponent = StylesImpl.withTheme(theme)(innerComponent)

    JsComponentConstructor(
      componentClass = themedComponent,
      c.children,
      c.key,
      c.ref
    )
  }

  val createMuiTheme : js.Dynamic = StylesImpl.createMuiTheme
  object MuiThemeProvider extends JsComponent(StylesImpl.MuiThemeProvider)
}

case class Styles(s : Style*) {
  val asJsAny : js.Any = js.Dictionary[String](s.map(style => style.name -> style.value):_*)
}
