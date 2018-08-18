package blended.ui.samples.compoments

import blended.ui.common.{Logger, MainComponent}
import blended.ui.material.MaterialUI._
import blended.ui.router.Router
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import blended.ui.samples.theme.{BlendedSamplesTheme, ContentStyles, Theme}
import blended.ui.samples.{HomePage, Routes, SamplePage}
import blended.ui.themes.SidebarMenuTheme
import com.github.ahnfelt.react4s._

case class SampleMainComponent() extends MainComponent[SamplePage, SampleAppState, SampleAppEvent] {

  private[this] val log = Logger[SampleMainComponent]

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

    E.div(
      Component(AppBarComponent, s),
      E.main(
        ContentStyles.root,
        Drawer(
          J("variant", "permanent"),
          S.height("100%"),
          S.width("200pt"),
          List(
            ListItem(
              J("button", "true"),
              Typography(Text("foo"))
            )
          )
        ),
        E.div(S.height("40pt")),
        Card(
          S.width("200pt"),
          Typography(Text("You think water moves fast? You should see ice."))
        ),
        Card(
          S.background(Theme.theme.palette.error.main.asInstanceOf[String]),
          S.width("200pt"),
          Typography(Text("You think water moves fast? You should see ice."))
        )
      )
    )
  }
}
