package blended.ui.common

import blended.ui.router.Router
import com.github.ahnfelt.react4s._
import org.scalajs.dom

case class AppState[P] (
  currentPage : Option[P]
)

abstract class MainComponent[P,AS,E] extends Component[NoEmit] {

  val initialState : AS

  val routerPath : Router[P]
  val routes : Router.Tree[P,P]

  protected def topLevelPage(state: AS) : Node

  protected[this] val appState = State(initialState)

  protected[this] def href(page : P): String =
    if(dom.window.location.href.contains("?")) {
      "#" + routes.path(page)
    }  else {
      routes.path(page)
    }

  protected[this] def path(): String =
    if(dom.window.location.href.contains("?")) {
      dom.window.location.hash.drop(1)
    } else {
      dom.window.location.pathname
    }

  protected def menuEntries: Seq[(String, Option[P])] = routes
    .prettyPaths
    .map(_.split("/"))
    .filter(_.length == 2)
    .map(_.toSeq)
    .map(_.last)
    .map(_.replaceAll("'", ""))
    .map(_.split("->"))
    .map(_.head.trim())
    .map(p => (p, routes.data(p)))
    .filter(_._2.isDefined)
    .map(p => (p._1.capitalize, p._2))

  val layout : Get => Element

  override def render(get: Get): Element = layout(get)
}
