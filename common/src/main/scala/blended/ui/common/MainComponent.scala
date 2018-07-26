package blended.ui.common

import blended.ui.router.Router
import blended.ui.themes.SidebarMenuTheme
import com.github.ahnfelt.react4s._
import org.scalajs.dom

abstract class MainComponent[P,AS,E]() extends Component[NoEmit] {

  val initialPage : P
  val initialState : AS
  val routes : Router.Tree[P,P]

  val theme : SidebarMenuTheme

  protected val currentPage : State[Option[P]] = State(Some(initialPage))
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

  protected def menuEntry(entryCss: CssClass, menuLinkCss: CssClass, title: String, target: P): Node =
    E.div(entryCss, E.a(menuLinkCss, Text(title), A.href(href(target))))

  val layout : Get => Element

  if(dom.window.location.href.contains("?")) {
    dom.window.onhashchange = { _ =>
      currentPage.set(routes.data(path()))
    }
  }

  override def render(get: Get): Element = layout(get)
}
