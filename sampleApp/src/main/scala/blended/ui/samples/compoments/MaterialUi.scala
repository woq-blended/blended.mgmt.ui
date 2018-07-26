package blended.ui.samples.compoments

import com.github.ahnfelt.react4s.JsComponent

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * Just a quick test to pull in an external component
  */
object MaterialUI {

  @js.native
  @JSImport("@material-ui/core/Button", JSImport.Default)
  object Button extends js.Object

  object MatButton extends JsComponent(Button)
}

