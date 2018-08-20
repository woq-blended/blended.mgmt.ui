package blended.ui.samples.compoments

import blended.ui.common.{Logger, MainComponent}
import blended.ui.material.MaterialUI._
import blended.ui.router.Router
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import blended.ui.samples.theme._
import blended.ui.samples.{HomePage, SamplePage}
import com.github.ahnfelt.react4s._

import scala.scalajs.js

case class SampleMainComponent() extends MainComponent[SamplePage, SampleAppState, SampleAppEvent] {

  private[this] val log = Logger[SampleMainComponent]

  override val routes: Router.Tree[SamplePage, SamplePage] = routerPath(
    HomePage
  )

  override def topLevelPage(page: Option[SamplePage], state: SampleAppState): Node = {
    page.map{
      case p @ HomePage         => Component(HomePageComponent, state)
    }.getOrElse(E.div(E.p(Text("Not found"))))
  }

  override lazy val initialPage: SamplePage = HomePage
  override lazy val initialState: SampleAppState = SampleAppState()

  override lazy val layout: Get => Element = { get =>

    val (p,s) = (get(currentPage), get(appState))

    val paperStyles = js.Dynamic.literal(
      "paper" -> DrawerStyles.name
    )

    E.div(
      RootStyles,
      Component(SampleAppBar.AppBarComponent, s),
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
          topLevelPage(p, s),
        )
      )
    )
  }
}
