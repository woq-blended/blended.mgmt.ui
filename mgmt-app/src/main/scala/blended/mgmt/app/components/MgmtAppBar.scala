package blended.mgmt.app.components

import blended.mgmt.app.state.MgmtAppState
import blended.mgmt.app.theme.{AppBarStyles, Theme}
import blended.ui.components.AppBar
import com.github.ahnfelt.react4s.CssClass

import scala.scalajs.js

object MgmtAppBar extends AppBar[MgmtAppState] {
  override val theme: js.Dynamic = Theme.theme
  override val styles: Option[CssClass] = Some(AppBarStyles)
}
