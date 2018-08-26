package blended.ui.samples.compoments

import blended.ui.material.MaterialUI
import blended.ui.samples.SamplePage
import blended.ui.samples.theme.Theme
import com.github.ahnfelt.react4s._

object SampleMenuDrawer {

  sealed trait MenuDrawerEvent
  case class PageSelected(page : Option[SamplePage]) extends MenuDrawerEvent

  case class comp(entries : P[Seq[(String, Option[SamplePage])]]) extends Component[MenuDrawerEvent] {

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

  }
}