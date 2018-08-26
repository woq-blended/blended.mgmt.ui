package blended.mgmt.app.components

import blended.mgmt.app.state.MgmtAppState
import blended.mgmt.app.theme.Theme
import blended.ui.material.MaterialUI
import com.github.ahnfelt.react4s._

object MgmtAppBar {

  sealed trait AppBarEvent
  case object Logout extends AppBarEvent

  case class comp(s: P[MgmtAppState]) extends Component[AppBarEvent] {

    private[this] def title(get: Get) : String =
      "Blended Management Console" + get(s).currentPage.map(p => " - " + p.title.capitalize).getOrElse("")

    private[this] def logoutButton(get : Get) : Seq[JsTag] = get(s).currentUser match {
      case None => Seq.empty
      case Some(_) => Seq(
        MaterialUI.Button(
          J("color", "inherit"),
          A.onClick(_ => emit(Logout)),
          Text("Logout")
        )
      )
    }

    override def render(get: Get): Node = {

      val children : Seq[JsTag] = Seq(
        MaterialUI.Typography(
          J("variant", "title"),
          J("color", "inherit"),
          S.flexGrow.number(1),
          Text(title(get))
        ),
      ) ++ logoutButton(get)

      MaterialUI.AppBar(
        Theme.AppBarStyles,
        MaterialUI.Toolbar(
          children:_*
        )
      )
    }
  }

}

