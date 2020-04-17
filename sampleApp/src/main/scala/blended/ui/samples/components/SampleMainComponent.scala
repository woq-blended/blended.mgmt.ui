package blended.ui.samples.components

import blended.mgmt.ui.theme.Theme
import blended.ui.components.pagecontainer.PageRegistry
import blended.material.ui.MaterialUI.Typography
import blended.ui.samples.components.SampleMenuDrawer.MenuItemSelected
import blended.ui.samples.state._
import com.github.ahnfelt.react4s._

case class SampleMainComponent() extends Component[NoEmit] {

  private val appState : State[SampleAppState] = State(SampleAppState())

  private val pages : PageRegistry[SampleAppState, SampleAppEvent] = new PageRegistry[SampleAppState, SampleAppEvent] {}
  pages.registerPage(HomePage)
  pages.registerPage(SampleTreePage)

  private val menuEntries : Seq[(String, String)] = pages.listPages.toSeq

  private def pageNode : SampleAppState => Tag = s => (s.currentPage match {
    case None => None
    case Some(id) =>
      pages.pageFromId(id).map{page =>
        page.renderPage(s, page.pageProperties)
      }
  }).getOrElse(Typography(Text("Please select a page")))

  override def render(get: Get): Node = {

    val s = get(appState)

    E.div(
      Theme.RootStyles,
      Component(SampleAppBar.comp, s),
      Component(SampleMenuDrawer.Comp, menuEntries, s.currentPage).withHandler{
        case MenuItemSelected(item) => appState.modify(
          SampleAppState.redux(PageSelected(Some(item)))
        )
      },
      E.div(
        Theme.ContentStyles,
        E.div(S.height.px(64)),
        E.main(
          Theme.ContentArea,
          pageNode(s)
        )
      )
    )
  }
}
