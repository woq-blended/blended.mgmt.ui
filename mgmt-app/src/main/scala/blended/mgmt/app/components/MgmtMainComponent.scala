package blended.mgmt.app.components

import akka.actor.{ActorRef, ActorSystem}
import blended.mgmt.app._
import blended.mgmt.app.backend.WSClientActor
import blended.mgmt.app.state.{AppEvent, MgmtAppState, UpdateContainerInfo}
import blended.mgmt.app.theme.BlendedMgmtTheme
import blended.ui.common.MainComponent
import blended.ui.router.Router
import blended.ui.themes.SidebarMenuTheme
import blended.updater.config.ContainerInfo
import blended.updater.config.json.PrickleProtocol._
import com.github.ahnfelt.react4s._
import prickle.Unpickle

case class MgmtMainComponent() extends MainComponent[Page, MgmtAppState, AppEvent] {

  override lazy val initialPage: Page = HomePage
  override lazy val initialState: MgmtAppState = MgmtAppState()
  override lazy val routes: Router.Tree[Page, Page] = Routes.routes

  override val theme: SidebarMenuTheme = BlendedMgmtTheme

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

  private[this] lazy val menu: Node = {
    val entries : Seq[(String, Page)] = Seq(
      "Overview" -> HomePage,
      "Container" -> ContainerPage(),
      "Services" -> ServicePage(),
      "Profiles" -> ProfilePage(),
      "Overlays" -> OverlayPage(),
      "Rollout" -> RolloutPage(),
      "Help" -> HelpPage()
    )

    E.div(
      theme.menuColumnCss,
      Tags(entries.map { case (k, v) =>
        menuEntry(theme.menuEntryCss, theme.menuLinkCss, k, v)
      })
    )
  }

  override lazy val layout: Get => Element = { get =>

    val p = get(currentPage)
    val state = get(appState)

    def topLevelPage : Node = {

      def pageOrLogin(p: Page, n: Node, state: MgmtAppState) : Node = {
        if (!p.loginRequired || state.currentUser.isDefined){
          n
        } else {
          Component(MgmtLoginComponent, state).withHandler(event => appState.modify(MgmtAppState.redux(event)))
        }
      }

      p match {
        case Some(p) => p match {
          case p @ HomePage         => pageOrLogin(p, Component(HomePageComponent, state), state)
          case p @ ContainerPage(_) => pageOrLogin(p, Component(ContainerPageComponent, state), state)
          case p @ ServicePage(_)   => pageOrLogin(p, Component(ServicePageComponent, state), state)
          case p @ ProfilePage(_)   => pageOrLogin(p, Component(ProfilePageComponent, state), state)
          case p @ OverlayPage(_)   => pageOrLogin(p, Component(OverlayPageComponent, state), state)
          case p @ RolloutPage(_)   => pageOrLogin(p, Component(RolloutPageComponent, state), state)
          case p @ HelpPage(_)      => pageOrLogin(p, Component(HelpPageComponent, state), state)
        }
        case None => E.div(E.p(Text("Not found")))
      }
    }


    E.div(
      E.div(
        theme.topBarCss,
        E.a(Text("Blended Management Console"), A.href(href(HomePage)), theme.brandTitleCss, theme.linkCss)
      ),
      E.div(
        theme.columnContainerCss,
        menu,
        E.div(
          theme.contentColumnCss,
          topLevelPage
        )
      ),
      E.div(
        theme.bottomBarCss,
        E.div(
          Text("Powered by "),
          E.a(Text("blended"), A.target("_blank"), A.href("https://github.com/woq-blended/blended"))
        )
      )
    )
  }
}
