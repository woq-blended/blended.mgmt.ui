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
      }.toSeq)
    )
  }

  override lazy val layout: (Option[Page], MgmtAppState) => Element = { (p, s) =>
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
          TopLevelPageResolver.topLevelPage(p, s)
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
