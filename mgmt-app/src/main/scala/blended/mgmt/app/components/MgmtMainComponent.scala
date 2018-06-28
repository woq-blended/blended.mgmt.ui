package blended.mgmt.app.components

import akka.actor.{ActorRef, ActorSystem}
import blended.mgmt.app.backend.WSClientActor
import blended.mgmt.app.state.{AppEvent, MgmtAppState, UpdateContainerInfo}
import blended.mgmt.app._
import blended.ui.common.MainComponent
import blended.ui.router.Router
import blended.updater.config.ContainerInfo
import com.github.ahnfelt.react4s._
import prickle.Unpickle
import blended.updater.config.json.PrickleProtocol._
import blended.ui.themes.SidebarMenuTheme._

case class MgmtMainComponent() extends MainComponent[Page, MgmtAppState, AppEvent] {

  override lazy val initialPage: Page = HomePage
  override lazy val initialState: MgmtAppState = MgmtAppState()
  override lazy val routes: Router.Tree[Page, Page] = Routes.routes

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

  private[this] lazy val menu: Node = E.div(
    MenuColumnCss,
    E.div(
      menuEntry(MenuEntryCss, LinkCss, "Overview", HomePage),
      menuEntry(MenuEntryCss, LinkCss, "Container", ContainerPage())
    )
  )

  override lazy val layout: (Option[Page], MgmtAppState) => Element = { (p, s) =>
    E.div(
      E.div(
        TopBarCss,
        E.a(Text("Blended Management Console"), A.href(href(HomePage)), BrandTitleCss, LinkCss)
      ),
      E.div(
        ColumnContainerCss,
        menu,
        E.div(
          ContentColumnCss,
          TopLevelPageResolver.topLevelPage(p, s)
        )
      ),
      E.div(
        BottomBarCss,
        E.div(
          Text("Powered by "),
          E.a(Text("blended"), A.target("_blank"), A.href("https://github.com/woq-blended/blended"))
        )
      )
    )
  }
}
