package blended.mgmt.app.components

import blended.mgmt.app.backend.UserInfo
import blended.mgmt.app.state.{AppEvent, LoggedIn, MgmtAppState}
import blended.mgmt.app.theme.Theme
import blended.security.BlendedPermissions
import blended.ui.material.MaterialUI._
import com.github.ahnfelt.react4s._

case class MgmtLoginComponent(state: P[MgmtAppState]) extends Component[AppEvent] {

  private[this] case class LoginDetails(
    url: String = "http://localhost:9995/management?#",
    user : String = "",
    pwd : String = ""
  ) {
    def isValid() : Boolean = !(url.isEmpty() || user.isEmpty() || pwd.isEmpty())
  }

  private[this] val loginDetails : State[LoginDetails] = State(LoginDetails())
  override def render(get: Get): Node = {

    val details = get(loginDetails)

    Paper(
      Theme.LoginPaper,
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
        J("variant", "contained"),
        J("color", "primary"),
        J("fullWidth", true),
        J("disabled", !details.isValid()),
        Text("Login"),
        A.onClick { _ =>
          emit(LoggedIn(UserInfo(details.user, BlendedPermissions())))
        }
      )
    )
  }
}
