package blended.ui.samples.compoments

import blended.ui.common.MainComponent
import blended.ui.material.MaterialUI._
import blended.ui.router.Router
import blended.ui.samples.{HomePage, Routes, SamplePage, TopLevelPageResolver}
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import blended.ui.samples.theme.BlendedSamplesTheme
import blended.ui.themes.SidebarMenuTheme
import com.github.ahnfelt.react4s._

import scala.scalajs.js

case class SampleMainComponent() extends MainComponent[SamplePage, SampleAppState, SampleAppEvent] {

  override lazy val initialPage: SamplePage = HomePage
  override lazy val initialState: SampleAppState = SampleAppState()
  override lazy val routes: Router.Tree[SamplePage, SamplePage] = Routes.routes

  override val theme: SidebarMenuTheme = BlendedSamplesTheme

  private[this] lazy val menu: Node = E.div(
    theme.menuColumnCss,
    E.div(
      E.div(theme.menuCategoryCss, E.p(Text("ReactTable"))),
      menuEntry(theme.menuEntryCss, theme.menuLinkCss, "Basic Table", HomePage)
    )
  )

  override lazy val layout: Get => Element = { get =>

    val (p,s) = (get(currentPage), get(appState))

    val styles = js.Dynamic.literal(
      "root" -> js.Dynamic.literal(
        "flewGrow" -> "1"
      )
    )

    E.div(
      AppBar(
        Toolbar(
          Typography(
            J("variant", "title"), J("color", "inherit"),
            Text("Blended Component Samples")
          )
        )
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
