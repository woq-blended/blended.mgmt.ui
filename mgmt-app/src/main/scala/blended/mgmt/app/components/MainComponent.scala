package blended.mgmt.app.components

import blended.mgmt.app.{ContainerPage, HomePage, Page, Routes}
import blended.mgmt.app.theme._
import com.github.ahnfelt.react4s._
import org.scalajs.dom

case class MainComponent() extends Component[NoEmit] {

  def href(page : Page) =
    if(dom.window.location.href.contains("?"))
      "#" + Routes.router.path(page)
    else
      Routes.router.path(page)

  def path() =
    if(dom.window.location.href.contains("?"))
      dom.window.location.hash.drop(1)
    else
      dom.window.location.pathname

  val page = State(Routes.router.data(path()))

  if(dom.window.location.href.contains("?")) {
    dom.window.onhashchange = { _ =>
      page.set(Routes.router.data(path()))
    }
  }

  override def render(get: Get): Element = {
    E.div(
      E.div(
        TopBarCss,
        E.a(Text("Blended Management Console"), A.href("/"), BrandTitleCss, LinkCss)
      ),
      E.div(
        ColumnContainerCss,
        E.div(
          MenuColumnCss,
          E.div(
            E.div(MenuEntryCss, E.a(Text("Overview"), LinkCss, A.href(href(HomePage)))),
            E.div(MenuEntryCss, E.a(Text("Container"), LinkCss, A.href(href(ContainerPage()))))
          )
        )
      )
    )
  }
}
