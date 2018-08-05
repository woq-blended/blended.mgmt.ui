package blended.ui.samples.compoments

import blended.material.ui.Icons.MenuIcon
import blended.ui.material.MaterialUI._
import blended.ui.samples.state.SampleAppState
import com.github.ahnfelt.react4s._

case class AppBarComponent(s : P[SampleAppState]) extends Component[NoEmit] {

  private object AppBarStyles {

    object root extends CssClass (
      S.flexGrow("1")
    )

    object flex extends CssClass (
      S.flexGrow("1")
    )

    object menuButton extends CssClass (
      S.marginLeft.pt(-12),
      S.marginRight.pt(20)
    )
  }

  override def render(get: Get): Node = AppBar(
    AppBarStyles.root,
    Toolbar(
      IconButton(
        AppBarStyles.menuButton,
        J("color", "inherit"),
        J("aria-label", "Menu"),
        MenuIcon()
      ),
      Typography(
        AppBarStyles.flex,
        J("variant", "title"), J("color", "inherit"),
        Text("Blended Component Samples")
      ),
      Button(J("color", "inherit"), Text("Login"))
    )
  )
}
