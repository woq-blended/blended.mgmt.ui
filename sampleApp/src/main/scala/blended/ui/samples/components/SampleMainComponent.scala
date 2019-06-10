package blended.ui.samples.components

import blended.mgmt.ui.theme.Theme
import blended.ui.common.MainComponent
import blended.ui.router.Router
import blended.ui.samples.state._
import blended.ui.samples.{HomePage, SamplePage}
import com.github.ahnfelt.react4s._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.timers.SetIntervalHandle

case class SampleMainComponent() extends MainComponent[SamplePage, SampleAppState, SampleAppEvent] {

  private[this] var intervalHandle : Option[SetIntervalHandle] = None

  override lazy val initialState: SampleAppState = SampleAppState()

  override val routerPath: Router[SamplePage] = new Router[SamplePage]
  override val routes = routerPath(
    HomePage
  )

  override def componentWillRender(get: Get): Unit = {

    if(dom.window.location.href.contains("?")) {
      dom.window.onhashchange = { _ =>
        appState.modify(SampleAppState.redux(PageSelected(routes.data(path()))))
      }
    }
  }

  override def componentWillUnmount(get: Get): Unit = {
    intervalHandle.foreach(js.timers.clearInterval)
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
