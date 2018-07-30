package blended.ui.material

import blended.ui.common.Logger
import com.github.ahnfelt.react4s._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object Styles {

  private[this] val log = Logger[Styles.type]

  @js.native
  @JSImport("@material-ui/core/styles", JSImport.Namespace)
  private[this] object StylesImpl extends js.Object {
    val MuiThemeProvider : js.Dynamic = js.native
    val createMuiTheme : js.Dynamic = js.native
    val withStyles : js.Function1[js.Any, js.Function1[Any, js.Any]] = js.native
  }

  def withStyles(s : Map[String, Styles]) : (JsComponentConstructor => JsComponentConstructor) = { c =>

    def muiStyle(s: Map[String, Styles]) : js.Any = js.Dictionary[js.Any]((s.map{ case (k,v) =>
      k -> v.toJsAny()
    }.toSeq):_*)

    val innerComponent = c.componentClass
    val styledComponent = StylesImpl.withStyles(muiStyle(s))(innerComponent)

    JsComponentConstructor(
      componentClass = styledComponent,
      c.children,
      c.key,
      c.ref
    )
  }

  val createMuiTheme = StylesImpl.createMuiTheme
  object MuiThemeProvider extends JsComponent(StylesImpl.MuiThemeProvider)
}

case class Styles(s : Style*) {

  def toJsAny() : js.Any = {
    js.Dictionary[String](s.map(style => style.name -> style.value):_*)
  }
}
