package blended.ui.samples.compoments

import blended.ui.common.{Logger, MainComponent}
import blended.ui.material.MaterialUI._
import blended.ui.router.Router
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import blended.ui.samples.theme._
import blended.ui.samples.{HomePage, Routes, SamplePage}
import blended.ui.themes.SidebarMenuTheme
import com.github.ahnfelt.react4s._

import scala.scalajs.js

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

    val paperStyles = js.Dynamic.literal(
      "paper" -> DrawerStyles.name
    )


    E.div(
      RootStyles,
      Component(AppBarComponent, s),
      Drawer(
        J("variant", "permanent"),
        J("classes", paperStyles),
        DrawerStyles,
        E.div(S.height.px(64)),
        List(
          ListItem(
            J("button", "true"),
            Typography(Text("foo"))
          )
        )
      ),
      E.div(
        ContentStyles,
        E.div(S.height.px(64)),
        E.main(
          ContentArea,
          Component(HomePageComponent, s)
        )
      )
    )
  }
}
