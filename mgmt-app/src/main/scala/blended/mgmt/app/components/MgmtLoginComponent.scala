package blended.mgmt.app.components

import blended.mgmt.app.backend.{JsonWebToken, UserInfo}
import blended.mgmt.app.state.{AppEvent, LoggedIn, MgmtAppState}
import blended.mgmt.app.theme.Theme
import blended.security.BlendedPermissions
import blended.ui.common.Logger
import blended.ui.material.MaterialUI._
import com.github.ahnfelt.react4s._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.window

import scala.util.control.NonFatal
import scala.util.{Failure, Success}

case class MgmtLoginComponent(state: P[MgmtAppState]) extends Component[AppEvent] {

  private[this] val log = Logger[MgmtLoginComponent]

  private[this] case class LoginDetails(
    host: String = "",
    port: Integer = 0,
    user : String = "",
    pwd : String = "",
    errorMsg : Option[String] = None
  ) {
    def isValid() : Boolean = !(host.isEmpty() || user.isEmpty() || pwd.isEmpty() || port <= 0)
  }

  private[this] val initialized : State[Boolean] = State(false)
  private[this] val loginDetails : State[LoginDetails] = State(LoginDetails())
  private[this] val loggingIn : State[Boolean] = State(false)

  private[this] def performLogin(get: Get) : Unit = {

    implicit val eCtxt = get(state).actorSystem.dispatcher
    val details = get(loginDetails)

    val requestUrl = s"http://${details.host}:${details.port}/login/"

    Ajax.post(
      url = requestUrl,
      headers = Map("Authorization" -> ("Basic " + window.btoa(details.user + ":" + details.pwd)))
    ).onComplete {
      case Failure(e) =>
        loginDetails.modify(_.copy(errorMsg = Some(e.getMessage())))
      case Success(s) =>
        log.info("login succeeded")

        val token = s.responseText
        log.info(token)

        val decoded = JsonWebToken.decode(token)
        val json = decoded.getOrElse("permissions", "").asInstanceOf[String]

        log.info(json)

        BlendedPermissions.fromJson(json) match {
          case Failure(e) => log.error("Could not decode permissions")
          case Success(p) =>

            emit(LoggedIn(
              details.host, details.port,
              UserInfo(
                details.user, token, p
              )
            ))
        }
    }
  }

  private[this] def showLoginForm(get : Get) : Node = {

    val s = get(state)

    if (!get(initialized)) {
      loginDetails.set(LoginDetails(s.host, s.port, "", ""))
      initialized.set(true)
    }

    val details = get(loginDetails)

    val children : Seq[JsTag] = Seq(
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
      E.div(S.height.px(Theme.spacingUnit)),
      E.div(
        S.flexDirection("row"),
        S.flex("1 100%"),
        TextField(
        A.onChangeText{ t => loginDetails.modify (_.copy(host = t) ) },
          S.width.percent(70),
          S.paddingRight.px(Theme.spacingUnit),
          J("id", "host"),
          J("label", "host"),
          J("value", details.host)
        ),
        TextField(
          S.width.percent(30),
          A.onChangeText{ t =>
            try {
              val newPort : Integer = Integer.parseInt(t)
              loginDetails.modify (_.copy(port = newPort) )
            } catch {
              case NonFatal(e) =>
                loginDetails.modify(_ => details)
                log.warn(s"could not parse [$t] into an Integer")
            }
          },
          J("id", "port"),
          J("label", "Port"),
          J("value", details.port),
          J("type", "number")
        )
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
      )
    ) ++ details.errorMsg.map { msg =>
      Typography(
        J("color", "error"),
        Text(msg)
      )
    }.toSeq ++ Seq(
      Button(
        Theme.LoginComponent,
        J("id", "submit"),
        J("variant", "contained"),
        J("color", "primary"),
        J("fullWidth", true),
        J("disabled", !details.isValid()),
        Text("Login"),
        A.onClick { _ => performLogin(get) }
      )
    )

    Paper(children:_*)
  }

  override def render(get: Get): Node = {
    showLoginForm(get)
  }
}
