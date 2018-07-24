package blended.ui.components.login

import com.github.ahnfelt.react4s._

sealed trait LoginEvent
case class LoginRequest(user: String, Password: String) extends LoginEvent
case class LoggedIn(user: String) extends LoginEvent
case class LoggedOut(user: String) extends LoginEvent

case class ReactLogin(loggedIn : P[Boolean]) extends Component[LoginEvent] {

  val credentials : State[(String, String)] = State("", "")

  override def render(get: Get): Node = {

    E.div(
      E.input(A.`type`("text"), A.onChangeText{ t => credentials.modify{ case (u, p) => (t,p) } }),
      E.input(A.`type`("password"), A.onChangeText{ t => credentials.modify{ case (u, p) => (u,t) } }),
      E.button(A.`type`("submit"), A.onClick{ _ =>
        val (user, pwd) = get(credentials)
        emit(LoginRequest(user, pwd))
      })
    )
  }
}
