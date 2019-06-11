package blended.ui.samples.components

import blended.mgmt.ui.theme.Theme
import blended.ui.material.MaterialUI
import com.github.ahnfelt.react4s._

object SampleMenuDrawer {

  sealed trait MenuDrawerEvent
  case class MenuItemSelected(item: String) extends MenuDrawerEvent

  case class Comp(entries : P[Seq[(String, String)]], selected: P[Option[String]]) extends Component[MenuDrawerEvent] {

    private val selectedKey : State[Option[String]] = State(None)

    override def render(get: Get): Node = {

      MaterialUI.Drawer(Map("paper" -> Theme.MenuDrawerStyles))(
        J("variant", "permanent"),
        Theme.MenuDrawerStyles,
        E.div(S.height.px(64)),
        MaterialUI.List(Tags(
          get(entries).map { case (key, value) =>
            MaterialUI.ListItem(
              S.background(Theme.secondary.main).when(get(selected).isDefined && get(selected).forall(_.equals(key))),
              A.onClick(_ => {
                emit(MenuItemSelected(key))
              }),
              MaterialUI.Typography(Text(value))
            )
          }
        ))
      )
    }

  }
}