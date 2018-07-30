package blended.ui.samples.compoments

import blended.ui.common.{Logger, MainComponent}
import blended.ui.material.MaterialUI._
import blended.ui.material.Styles
import blended.ui.router.Router
import blended.ui.samples.{HomePage, Routes, SamplePage, TopLevelPageResolver}
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import blended.ui.samples.theme.BlendedSamplesTheme
import blended.ui.themes.SidebarMenuTheme
import com.github.ahnfelt.react4s._

case class SampleMainComponent() extends MainComponent[SamplePage, SampleAppState, SampleAppEvent] {

  private[this] val log = Logger[SampleMainComponent]

  override lazy val initialPage: SamplePage = HomePage
  override lazy val initialState: SampleAppState = SampleAppState()
  override lazy val routes: Router.Tree[SamplePage, SamplePage] = Routes.routes

  override val theme: SidebarMenuTheme = BlendedSamplesTheme

  val appBarStyles : Map[String, Styles] = Map(
    "root" -> Styles(S.flexGrow("1"))
  )

  private[this] lazy val menu: Node = E.div(
    theme.menuColumnCss,
    E.div(
      E.div(theme.menuCategoryCss, E.p(Text("ReactTable"))),
      menuEntry(theme.menuEntryCss, theme.menuLinkCss, "Basic Table", HomePage)
    )
  )

  val appBar = AppBar(
    Toolbar(
      Typography(
        J("variant", "title"), J("color", "inherit"),
        Text("Blended Component Samples")
      )
    )
  )

  override lazy val layout: Get => Element = { get =>

    val (p,s) = (get(currentPage), get(appState))

    E.div(
      Styles.withStyles(appBarStyles)(appBar),
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
