package blended.mgmt.app.components

import akka.actor.Props
import blended.mgmt.app.backend.{EventStreamStateHandler, UserInfo}
import blended.mgmt.app.state.{AppEvent, LoggedIn, MgmtAppState}
import blended.mgmt.app.theme.Theme
import blended.security.BlendedPermissions
import blended.ui.material.MaterialUI._
import com.github.ahnfelt.react4s._

case class MgmtLoginComponent(state: P[MgmtAppState]) extends Component[AppEvent] {

  private[this] case class LoginDetails(
    url: String = "",
    user : String = "",
    pwd : String = ""
  ) {
    def isValid() : Boolean = !(url.isEmpty() || user.isEmpty() || pwd.isEmpty())
  }

  private[this] val initialized : State[Boolean] = State(false)
  private[this] val loginDetails : State[LoginDetails] = State(LoginDetails())

  private[this] def showLoginForm(get : Get) : Node = {

    if (!get(initialized)) {
      loginDetails.set(LoginDetails(get(state).baseUrl, "", ""))
      initialized.set(true)
    }

    val details = get(loginDetails)

    Paper(
      Theme.LoginPaper,
      J("elevation", 1),
      Toolbar(
        Theme.LoginTitle,
        Typography(
          J("color", "inherit"),
          J("variant", "headline"),
          J("component", "h3"),
          Text("Login to continue")
        )
      ),
      Divider(),
      TextField(
        Theme.LoginComponent,
        A.onChangeText{ t => loginDetails.modify (_.copy(url = t) ) },
        J("id", "url"),
        J("label", "URL"),
        J("value", details.url),
        J("fullWidth", true)
      ),
      TextField(
        Theme.LoginComponent,
        A.onChangeText{ t => loginDetails.modify(_.copy(user = t)) },
        J("id", "user"),
        J("label", "Username"),
        J("value", details.user),
        J("fullWidth", true)
      ),
      TextField(
        Theme.LoginComponent,
        A.onChangeText{ t => loginDetails.modify(_.copy(pwd = t)) },
        J("id", "password"),
        J("label", "Password"),
        J("value", details.pwd),
        J("type", "password"),
        J("fullWidth", true)
      ),
      Button(
        Theme.LoginComponent,
        J("id", "submit"),
        J("variant", "contained"),
        J("color", "primary"),
        J("fullWidth", true),
        J("disabled", !details.isValid()),
        Text("Login"),
        A.onClick { _ =>
          emit(LoggedIn(details.url, UserInfo(details.user, "", BlendedPermissions())))
        }
      )
    )
  }

  override def render(get: Get): Node = {
    showLoginForm(get)
  }
}
