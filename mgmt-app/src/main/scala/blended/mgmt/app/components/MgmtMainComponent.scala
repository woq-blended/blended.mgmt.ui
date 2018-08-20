package blended.mgmt.app.components

import akka.actor.{ActorRef, ActorSystem}
import blended.mgmt.app._
import blended.mgmt.app.backend.WSClientActor
import blended.mgmt.app.state.{AppEvent, MgmtAppState, UpdateContainerInfo}
import blended.mgmt.app.theme._
import blended.ui.common.{I18n, MainComponent}
import blended.ui.material.MaterialUI
import blended.updater.config.ContainerInfo
import blended.updater.config.json.PrickleProtocol._
import com.github.ahnfelt.react4s._
import prickle.Unpickle

case class MgmtMainComponent() extends MainComponent[Page, MgmtAppState, AppEvent] {

  private[this] val i18n = I18n()

  override lazy val initialPage: Page = HomePage
  override lazy val initialState: MgmtAppState = MgmtAppState()

  override val mainRoutes: Map[String, Page => Page with Product] = Map(
    "container" -> ContainerPage,
    "services" -> ServicePage,
    "profiles" -> ProfilePage,
    "overlays" -> OverlayPage,
    "rollout" -> RolloutPage,
    "help" -> HelpPage
  )

  override val routes = {

    val subRoutes = mainRoutes.map(p => routerPath(p._1, p._2)).toSeq

    routerPath(
      initialPage,
      subRoutes:_*
    )
  }

  val system : ActorSystem = ActorSystem("MgmtApp")

  private[this] val ctListener = State[Option[ActorRef]](None)

  // A web sockets handler decoding container Info's

  override def componentWillRender(get: Get): Unit =
    if (get(ctListener).isEmpty) {

      val handleCtInfo : PartialFunction[Any, Unit] = {
        case s : String =>
          Unpickle[ContainerInfo].fromString(s).map { ctInfo =>
            appState.modify(MgmtAppState.redux(UpdateContainerInfo(ctInfo)))
          }
      }

      ctListener.set(Some(system.actorOf(WSClientActor.props(
        "ws://localhost:9995/mgmtws/timer?name=test",
        handleCtInfo
      ))))
    }

  def topLevelPage(page: Option[Page], state: MgmtAppState) : Node = {

    def pageOrLogin(p: Page, n: Node, state: MgmtAppState) : Node = {
      if (!p.loginRequired || state.currentUser.isDefined){
        n
      } else {
        Component(MgmtLoginComponent, state).withHandler(event => appState.modify(MgmtAppState.redux(event)))
      }
    }

    page.map{
      case p @ HomePage         => pageOrLogin(p, Component(HomePageComponent, state), state)
      case p @ ContainerPage(_) => pageOrLogin(p, Component(ContainerPageComponent, state), state)
      case p @ ServicePage(_)   => pageOrLogin(p, Component(ServicePageComponent, state), state)
      case p @ ProfilePage(_)   => pageOrLogin(p, Component(ProfilePageComponent, state), state)
      case p @ OverlayPage(_)   => pageOrLogin(p, Component(OverlayPageComponent, state), state)
      case p @ RolloutPage(_)   => pageOrLogin(p, Component(RolloutPageComponent, state), state)
      case p @ HelpPage(_)      => pageOrLogin(p, Component(HelpPageComponent, state), state)
    }.getOrElse(E.div(E.p(Text("Not found"))))
  }

  override lazy val layout: Get => Element = { get =>

    val page = get(currentPage)
    val state = get(appState)

    E.div(
      RootStyles,
      Component(MgmtAppBar.AppBarComponent, state),
      MaterialUI.Drawer(
        J("variant", "permanent"),
        //J("classes", paperStyles),
        DrawerStyles,
        E.div(S.height.px(64)),
        MaterialUI.List(Tags(
          mainRoutes.map { r =>
            MaterialUI.ListItem(J("button", true), MaterialUI.Typography(Text(r._1)))
          }.toSeq
        ))
      ),
      E.div(
        ContentStyles,
        E.div(S.height.px(64)),
        E.main(
          ContentArea,
          topLevelPage(page, state)
        )
      )
    )
  }
}
