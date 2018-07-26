package blended.mgmt.app.components

import blended.mgmt.app.state.{AppEvent, LoggedIn, MgmtAppState}
import blended.ui.components.login.{LoginRequest, ReactLogin}
import com.github.ahnfelt.react4s._

case class MgmtLoginComponent(state: P[MgmtAppState]) extends Component[AppEvent] {

  override def render(get: Get): Node = {

    Component(ReactLogin, get(state).currentUser.isDefined).withHandler {
      case LoginRequest(user, _) =>
        emit(LoggedIn(user))
    }
  }
}
