package blended.mgmt.app.components

import akka.actor.{ActorRef, ActorSystem}
import blended.mgmt.app._
import blended.mgmt.app.backend.WSClientActor
import blended.mgmt.app.state.{AppEvent, MgmtAppState, PageSelected, UpdateContainerInfo}
import blended.mgmt.app.theme.MgmtMaterialComponents._
import blended.mgmt.app.theme.BlendedMgmtStyles._
import blended.ui.common.{I18n, Logger, MainComponent}
import blended.ui.router.Router
import blended.updater.config.ContainerInfo
import blended.updater.config.json.PrickleProtocol._
import com.github.ahnfelt.react4s._
import org.scalajs.dom
import prickle.Unpickle

case class MgmtMainComponent() extends MainComponent[Page, MgmtAppState, AppEvent] {

  private[this] val log = Logger[MgmtMainComponent]
  private[this] val i18n = I18n()

  override lazy val initialState: MgmtAppState = MgmtAppState()

  override val routerPath: Router[Page] = new Router[Page]
  override val routes: Router.Tree[Page, Page] = routerPath(
    HomePage,
    routerPath("container", ContainerPage),
    routerPath("services", ServicePage),
    routerPath("profiles", ProfilePage),
    routerPath("overlays", OverlayPage),
    routerPath("rollout", RolloutPage),
    routerPath("help", HelpPage)
  )

  if(dom.window.location.href.contains("?")) {
    dom.window.onhashchange = { _ =>
      appState.modify(MgmtAppState.redux(PageSelected(routes.data(path()))))
    }
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

  def topLevelPage(state: MgmtAppState) : Node = {

    def pageOrLogin(p: Page, n: Node, state: MgmtAppState) : Node = {
      if (!p.loginRequired || state.currentUser.isDefined){
        n
      } else {
        Component(MgmtLoginComponent, state).withHandler(event => appState.modify(MgmtAppState.redux(event)))
      }
    }

    state.currentPage.map{
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

    val state = get(appState)

    E.div(
      RootStyles,
      Component(AppBar.AppBarC, state),
      Component(
        MenuDrawer.MenuDrawerC, menuEntries
      ).withHandler{
        case MenuDrawer.PageSelected(p) =>
          if (p.isDefined) {
            p.foreach(page => dom.window.location.href = href(page))
          }
      },
      E.div(
        ContentStyles,
        E.div(S.height.px(64)),
        E.main(
          ContentArea,
          topLevelPage(state)
        )
      )
    )
  }
}
