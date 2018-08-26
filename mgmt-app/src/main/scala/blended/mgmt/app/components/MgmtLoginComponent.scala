package blended.mgmt.app.components

import blended.mgmt.app.backend.UserInfo
import blended.mgmt.app.state.{AppEvent, LoggedIn, MgmtAppState}
import blended.security.BlendedPermissions
import com.github.ahnfelt.react4s._

case class MgmtLoginComponent(state: P[MgmtAppState]) extends Component[AppEvent] {

  private[this] val credentials : State[(String, String)] = State("", "")

  override def render(get: Get): Node = {

    E.div(
      E.input(A.`type`("text"), A.onChangeText{ t => credentials.modify{ case (_, p) => (t,p) } }),
      E.input(A.`type`("password"), A.onChangeText{ t => credentials.modify{ case (u, _) => (u,t) } }),
      E.button(Text("Login"), A.`type`("submit"), A.onClick{ _ =>
        val (user, pwd) = get(credentials)
        emit(LoggedIn(UserInfo(user, BlendedPermissions())))
      })
    )
  }
}
