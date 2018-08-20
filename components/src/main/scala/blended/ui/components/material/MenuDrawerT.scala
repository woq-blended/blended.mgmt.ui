package blended.ui.components.material

import blended.ui.material.MaterialUI
import com.github.ahnfelt.react4s._

import scala.scalajs.js

class MenuDrawerT[PT]()(implicit styles: MaterialStyles) {

  sealed trait MenuDrawerEvent
  case class PageSelected(page : Option[PT]) extends MenuDrawerEvent

  case class MenuDrawerC(entries : P[Seq[(String, Option[PT])]]) extends Component[MenuDrawerEvent] {

    val clazzes = js.Dynamic.literal(
      "paper" -> styles.MenuDrawerStyles.name
    )

    override def render(get: Get): Node = MaterialUI.Drawer(
      J("variant", "permanent"),
      J("classes", clazzes),
      styles.MenuDrawerStyles,
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
