package blended.ui.material

import com.github.ahnfelt.react4s._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("@material-ui/core/styles", JSImport.Namespace)
object Styles extends js.Object {
  val MuiThemeProvider : js.Dynamic = js.native
  val createMuiTheme : js.Dynamic = js.native
}

object MuiThemeProvider extends JsComponent(Styles.MuiThemeProvider)
