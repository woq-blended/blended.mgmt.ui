package blended.mgmt.app.components

import blended.mgmt.app.backend.{JsonWebToken, UserInfo}
import blended.mgmt.app.state.{AppEvent, LoggedIn, MgmtAppState}
import blended.mgmt.app.theme.Theme
import blended.security.BlendedPermissions
import blended.ui.common.Logger
import blended.material.ui.MaterialUI._
import com.github.ahnfelt.react4s._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.window

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

object MgmtLoginComponent {

  private val log = Logger[MgmtLoginComponent]

  def validateLogin(id : String, token : String) : Try[UserInfo] = {
    val decoded = JsonWebToken.decode(token)
    val json = decoded.getOrElse("permissions", "").asInstanceOf[String]

    log.debug(json)

    BlendedPermissions.fromJson(json).map{ p =>
      UserInfo(id, token, p)
    }
  }
}

trait LoginExecutor {
  def performLogin(
    user : String, password : String, props : Map[String, String]
  )(implicit eCtxt : ExecutionContext) : Future[String]

  def login(
    user : String,
    password : String,
    props : Map[String, String]
  )(implicit eCtxt : ExecutionContext) : Future[UserInfo] = {
    performLogin(user, password, props).map{token => MgmtLoginComponent.validateLogin(user, token).get}
  }
}

object RestLoginExecutor {
  val loginUrlKey = "loginUrl"
}

class DummyLoginExecutor extends LoginExecutor {

  override def performLogin(user: String, password: String, props: Map[String, String])(implicit eCtxt: ExecutionContext): Future[String] = Future {

    val token : String =  "eyJhbGciOiJSUzUxMiJ9.eyJqdGkiOiIyMDE4LTA4LTE1LTE5OjQ3OjQ0OjQ4OS0xIiwic3ViIjoiYW5kcmVhcyIsImlhdCI6MTUzNDM1NTI2NCwicGVybWlzc2lvbnMiOiJ7XCJncmFudGVkXCI6IHtcIiNlbGVtc1wiOiBbe1wicGVybWlzc2lvbkNsYXNzXCI6IHtcIiNlbGVtc1wiOiBbXCJhZG1pbnNcIl19LCBcInByb3BlcnRpZXNcIjoge1wiI2VsZW1zXCI6IFtdfX0sIHtcInBlcm1pc3Npb25DbGFzc1wiOiB7XCIjZWxlbXNcIjogW1wiYmxlbmRlZFwiXX0sIFwicHJvcGVydGllc1wiOiB7XCIjZWxlbXNcIjogW119fV19fSIsImV4cCI6MTUzNDM1NTMyNH0.ObRwVtt2XlA_WRGtcVwhr_jOm1xvzOQUlsvqXu7RMN-j7hqdWp-eqkwjC6OL0jL7iXKTDw3I9ZBz4AvJpUYgsn5YoTbfs5L_5Iqe1F4mh9Pcp4VSN9F9Tuhh5YufEdN1F-YO2AssPC1fYWiW1cBEgqXGY91IVY_p6hHBOdPRPfCC-hLucXtRyzsyK8e3FcvOL-juhbDuY9Nef2E-160AS7Wl-hdEkOretdqMPZYnJxO3eUtiDyQtSU1GiBp8AuhsferqLp6LtHF6hDxq0o7k3_3vbcNR1OSTz9SXl8JJylG2XkcpeBXjpsy4Gc1SRikGPyfDcPKIt8fPH1IrAEoOkw"

    if (user.equals("root") && password.equals("mysecret")) {
      token
    } else {
      throw new Exception("Failed to login")
    }
  }
}

class RestLoginExecutor() extends LoginExecutor {

  override def performLogin(
    user : String, password : String, props : Map[String, String]
  )(implicit eCtxt : ExecutionContext) : Future[String] = {

    props.get(RestLoginExecutor.loginUrlKey) match {
      case None => throw new Exception(s"No ${RestLoginExecutor.loginUrlKey} given to log in.")
      case Some(loginUrl) =>
        Ajax.post(
          url = loginUrl,
          headers = Map("Authorization" -> ("Basic " + window.btoa(user + ":" + password)))
        ).map { s =>
          if (s.status == 200) {
            s.responseText
          } else {
            throw new Exception(s"Login failed with status code [${s.status}]")
          }
        }
    }
  }
}

case class MgmtLoginComponent(state: P[MgmtAppState], loginExecutor : P[LoginExecutor])
  extends Component[AppEvent] {

  private[this] val log = Logger[MgmtLoginComponent]

  private[this] case class LoginDetails(
    host: String = "",
    port: Integer = 0,
    user : String = "",
    pwd : String = "",
    errorMsg : Option[String] = None
  ) {
    def isValid : Boolean = !(host.isEmpty || user.isEmpty || pwd.isEmpty || port <= 0)
  }

  private[this] val initialized : State[Boolean] = State(false)
  private[this] val loginDetails : State[LoginDetails] = State(LoginDetails())

  private[this] def performLogin(get: Get) : Unit = {

    implicit val eCtxt : ExecutionContext = get(state).system.dispatcher
    val details = get(loginDetails)

    val requestUrl = s"http://${details.host}:${details.port}/login/"

    get(loginExecutor).login(
      details.user, details.pwd, Map(RestLoginExecutor.loginUrlKey -> requestUrl)
    ).onComplete {
      case Failure(t) =>
        log.error(t)(t.getMessage)
        loginDetails.modify(_.copy(errorMsg = Some("Failed to login")))
      case Success(info) =>
        log.info(s"User [${info.id}] logged in successfully")
        emit(LoggedIn(details.host, details.port, info))
    }
  }

  // scalastyle:off magic.number
  private[this] def hostAndPort(details: LoginDetails) : Node = E.div(
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
          case NonFatal(_) =>
            loginDetails.modify(_ => details)
            log.warn(s"could not parse [$t] into an Integer")
        }
      },
      J("id", "port"),
      J("label", "Port"),
      J("value", details.port),
      J("type", "number")
    )
  )
  // scalastyle:on magic.number

  private[this] def userAndPwd(details : LoginDetails) : JsTag = Tags(
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
  )

  private[this] val title : JsTag = Tags(
    Toolbar(
      Theme.LoginTitle,
      Typography(
        J("color", "inherit"),
        J("variant", "headline"),
        J("component", "h3"),
        Text("Login to continue")
      )
    ),
    E.div(S.height.px(Theme.spacingUnit))
  )

  private[this] def loginButton(get : Get) : JsTag = {

    val details = get(loginDetails)

    Button(
      Theme.LoginComponent,
      J("id", "submit"),
      J("variant", "contained"),
      J("color", "primary"),
      J("fullWidth", true),
      J("disabled", !details.isValid),
      Text("Login"),
      A.onClick { _ => performLogin(get) }
    )
  }

  private[this] def showLoginForm(get : Get) : Node = {

    val s = get(state)

    if (!get(initialized)) {
      loginDetails.set(LoginDetails(s.host, s.port))
      initialized.set(true)
    }

    val details = get(loginDetails)

    val children : Seq[JsTag] = Seq(
      Theme.LoginPaper,
      J("elevation", 1),
      title,
      hostAndPort(details),
      userAndPwd(details),
    ) ++ details.errorMsg.map { msg =>
      Typography(
        J("color", "error"),
        Text(msg)
      )
    }.toSeq ++ Seq(
      loginButton(get)
    )

    Paper(children:_*)
  }

  override def render(get: Get): Node = {
    showLoginForm(get)
  }
}
