package blended.ui.samples.components

import blended.mgmt.ui.theme.Theme
import blended.material.ui.MaterialUI
import blended.ui.samples.state.SampleAppState
import com.github.ahnfelt.react4s._

object SampleAppBar {

  // scalastyle:off class.name
  case class comp(s: P[SampleAppState]) extends Component[NoEmit] {
  // scalastyle:on class.name

    private[this] def title(get: Get) : String =
      "Blended Component PlayGround"

    override def render(get: Get): Node = {

      val children : Seq[JsTag] = Seq(
        MaterialUI.Typography(
          J("variant", "title"),
          J("color", "inherit"),
          S.flexGrow.number(1),
          Text(title(get))
        )
      )

      MaterialUI.AppBar(
        Theme.AppBarStyles,
        MaterialUI.Toolbar(
          children:_*
        )
      )
    }
  }

}