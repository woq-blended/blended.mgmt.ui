package blended.mgmt.app.components

import akka.actor.{ActorRef, ActorSystem}
import blended.mgmt.app.backend.WSClientActor
import blended.mgmt.app.state.{AppState, UpdateContainerInfo, UpdateCurrentPage}
import blended.mgmt.app._
import blended.mgmt.app.theme._
import blended.updater.config.ContainerInfo
import org.scalajs.dom
import blended.updater.config.json.PrickleProtocol._
import com.github.ahnfelt.react4s._
import prickle._

case class MainComponent() extends Component[NoEmit] {

  private[this] def href(page : Page): String =
    if(dom.window.location.href.contains("?"))
      "#" + Routes.router.path(page)
    else
      Routes.router.path(page)

  private[this] def path(): String =
    if(dom.window.location.href.contains("?"))
      dom.window.location.hash.drop(1)
    else
      dom.window.location.pathname

  val state = State(AppState())

  if(dom.window.location.href.contains("?")) {
    dom.window.onhashchange = { _ =>
      state.modify(AppState.redux(UpdateCurrentPage(Routes.router.data(path()))))
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
            state.modify(AppState.redux(UpdateContainerInfo(ctInfo)))
          }
      }

      ctListener.set(Some(system.actorOf(WSClientActor.props(
        "ws://localhost:9995/mgmtws/timer?name=test",
        handleCtInfo
      ))))
    }

  override def render(get: Get): Element = {
    E.div(
      E.div(
        TopBarCss,
        E.a(Text("Blended Management Console"), A.href(href(HomePage)), BrandTitleCss, LinkCss)
      ),
      E.div(
        ColumnContainerCss,
        E.div(
          MenuColumnCss,
          E.div(
            E.div(MenuEntryCss, E.a(Text("Overview"), LinkCss, A.href(href(HomePage)))),
            E.div(MenuEntryCss, E.a(Text("Container"), LinkCss, A.href(href(ContainerPage()))))
          )
        ),
        E.div(
          ContentColumnCss,
          TopLevelPageResolver.topLevelPage(get(state))
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
