package blended.mgmt.app.components

import blended.mgmt.app.Page
import blended.mgmt.app.theme.Theme
import blended.ui.material.MaterialUI
import com.github.ahnfelt.react4s._

object MgmtMenuDrawer {

  sealed trait MenuDrawerEvent
  case class PageSelected(page : Option[Page]) extends MenuDrawerEvent

  case class Comp(entries : P[Seq[(String, Option[Page])]]) extends Component[MenuDrawerEvent] {

    // scalastyle:off magic.number
    override def render(get: Get): Node = MaterialUI.Drawer(Map("paper" -> Theme.MenuDrawerStyles))(
      J("variant", "permanent"),
      Theme.MenuDrawerStyles,
      E.div(S.height.px(64)),
      MaterialUI.List(Tags(
        get(entries).map { r =>
          MaterialUI.ListItem(
            J("button", true),
            A.onClick(_ => emit(PageSelected(r._2))),
            MaterialUI.Typography(Text(r._1))
          )
        }
      ))
    )
    // scalastyle:on magic.number
  }
}
