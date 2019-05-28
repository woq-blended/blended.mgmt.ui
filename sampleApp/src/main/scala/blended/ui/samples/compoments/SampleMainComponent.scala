package blended.ui.samples.compoments

import blended.ui.common.{Logger, MainComponent}
import blended.ui.router.Router
import blended.ui.samples.state._
import blended.ui.samples.{HomePage, SamplePage}
import com.github.ahnfelt.react4s._
import org.scalajs.dom
import blended.ui.samples.theme.Theme

case class SampleMainComponent() extends MainComponent[SamplePage, SampleAppState, SampleAppEvent] {

  private[this] val log = Logger[SampleMainComponent]

  override lazy val initialState: SampleAppState = SampleAppState()

  override val routerPath: Router[SamplePage] = new Router[SamplePage]
  override val routes = routerPath(
    HomePage
  )

  if(dom.window.location.href.contains("?")) {
    dom.window.onhashchange = { _ =>
      appState.modify(SampleAppState.redux(PageSelected(routes.data(path()))))
    }
  }

  override def topLevelPage(state: SampleAppState): Node = {
    state.currentPage.map {
      //case p @ HomePage => Component(HomePageComponent, state)
      case HomePage => Component(SampleTreePage, state)
    }.getOrElse(E.div(E.p(Text("Not found"))))
  }

  override lazy val layout: Get => Element = { get =>

    val s = get(appState)

    E.div(
      Theme.RootStyles,
      Component(SampleAppBar.comp, s),
      Component(SampleMenuDrawer.Comp, menuEntries),
      E.div(
        Theme.ContentStyles,
        E.div(S.height.px(64)),
        E.main(
          Theme.ContentArea,
          topLevelPage(s),
        )
      )
    )
  }
}
