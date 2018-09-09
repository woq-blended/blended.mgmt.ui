package blended.mgmt.app.components

import akka.actor.Props
import blended.mgmt.app._
import blended.mgmt.app.backend.EventStreamStateHandler
import blended.mgmt.app.state._
import blended.mgmt.app.theme.Theme
import blended.ui.common.{I18n, Logger, MainComponent}
import blended.ui.router.Router
import com.github.ahnfelt.react4s._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.timers.SetIntervalHandle
import scala.util.Try

case class MgmtMainComponent() extends MainComponent[Page, MgmtAppState, AppEvent] {

  private[this] val log = Logger[MgmtMainComponent]
  private[this] val i18n = I18n()

  var intervalHandle : Option[SetIntervalHandle] = None

  override lazy val initialState: MgmtAppState = {
    val state = MgmtAppState()

    val as = state.system
    val stateHandler = as.actorOf(Props(EventStreamStateHandler.props { event =>
      Try {
        appState.modify{ MgmtAppState.redux(event) }
      }
    }))
    as.eventStream.subscribe(stateHandler, classOf[AppEvent])

    state
  }


  override def componentWillRender(get: Get): Unit = {
    if (intervalHandle.isEmpty) {
      intervalHandle = Some(js.timers.setInterval(1000) {
        val events = MgmtAppState.events.toList
        MgmtAppState.events.clear()
        events.foreach { e => appState.modify(MgmtAppState.redux(e)) }
      })
    }
  }

  override def componentWillUnmount(get: Get): Unit = {
    intervalHandle.foreach(i => js.timers.clearInterval(i))
  }

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
  } else {
    dom.window.location.href = dom.window.location.href + "?#"
  }

  def topLevelPage(state: MgmtAppState): Node = {

    def pageOrLogin(p: Page, n: Node, state: MgmtAppState): Node = {
      if (state.currentUser.isDefined) {
        n
      } else {
        Component(MgmtLoginComponent, state).withHandler(event => appState.modify(MgmtAppState.redux(event)))
      }
    }

    state.currentPage.map {
      case p@HomePage => pageOrLogin(p, Component(HomePageComponent, state), state)
      case p@ContainerPage(_) => pageOrLogin(p, Component(ContainerPageComponent, state), state)
      case p@ServicePage(_) => pageOrLogin(p, Component(ServicePageComponent, state), state)
      case p@ProfilePage(_) => pageOrLogin(p, Component(ProfilePageComponent, state), state)
      case p@OverlayPage(_) => pageOrLogin(p, Component(OverlayPageComponent, state), state)
      case p@RolloutPage(_) => pageOrLogin(p, Component(RolloutPageComponent, state), state)
      case p@HelpPage(_) => pageOrLogin(p, Component(HelpPageComponent, state), state)
    }.getOrElse(E.div(E.p(Text("Not found"))))
  }

  override lazy val layout: Get => Element = { get =>

    val state = get(appState)

    val drawer: Option[ConstructorData[_]] = state.currentUser.map { _ =>
      Component(MgmtMenuDrawer.Comp, menuEntries).withHandler {
        case MgmtMenuDrawer.PageSelected(p) =>
          if (p.isDefined) {
            p.foreach(page => dom.window.location.href = href(page))
          }
      }
    }

    E.div(
      Theme.RootStyles,
      Component(MgmtAppBar.Comp, state).withHandler {
        case MgmtAppBar.Logout => appState.modify(MgmtAppState.redux(LoggedOut))
      },
      Tags(drawer.toSeq: _*),
      E.div(
        Theme.ContentStyles,
        E.div(S.height.px(64)),
        E.main(
          Theme.ContentArea,
          topLevelPage(state)
        )
      )
    )
  }
}
