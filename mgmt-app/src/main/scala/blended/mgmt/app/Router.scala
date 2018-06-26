package blended.mgmt.app

import blended.mgmt.app.components.{ContainerPageComponent, HelpPageComponent, HomePageComponent}
import blended.mgmt.app.state.AppState
import blended.ui.router.Router
import com.github.ahnfelt.react4s._

sealed trait Page
final object HomePage extends Page
final case class ContainerPage(parent: HomePage.type = HomePage) extends Page
final case class HelpPage(parent: HomePage.type = HomePage) extends Page

object Routes {

  val path = new Router[Page]

  val router = path(HomePage,
    path("container", ContainerPage),
    path("help" , HelpPage)
  )
}

object TopLevelPageResolver {

  def topLevelPage(state: AppState) : Node = {
    state.currentPage match {
      case Some(p) => p match {
        case HomePage => Component(HomePageComponent, state)
        case ContainerPage(_) => Component(ContainerPageComponent, state)
        case HelpPage(_) => Component(HelpPageComponent, state)
      }
      case None => E.div(E.p(Text("Not found")))
    }
  }
}
