package blended.ui.samples.compoments

import blended.ui.components.AppBar
import blended.ui.samples.state.SampleAppState
import blended.ui.samples.theme.Theme
import com.github.ahnfelt.react4s.CssClass

import scala.scalajs.js

object SampleAppBar extends AppBar[SampleAppState] {

  override val theme: js.Dynamic = Theme.theme
  override val styles: Option[CssClass] = Some(AppBarStyles)
}
