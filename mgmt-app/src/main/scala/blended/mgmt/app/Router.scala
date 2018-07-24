package blended.mgmt.app

import blended.mgmt.app.components._
import blended.mgmt.app.state.MgmtAppState
import blended.ui.router.Router
import com.github.ahnfelt.react4s._

sealed trait Page {
  val loginRequired : Boolean = true
}

final object HomePage extends Page
final case class ContainerPage(parent: HomePage.type = HomePage) extends Page
final case class ServicePage(parent: HomePage.type = HomePage) extends Page
final case class ProfilePage(parent: HomePage.type = HomePage) extends Page
final case class OverlayPage(parent: HomePage.type = HomePage) extends Page
final case class RolloutPage(parent: HomePage.type = HomePage) extends Page
final case class HelpPage(parent: HomePage.type = HomePage) extends Page {
  override val loginRequired: Boolean = false
}

object Routes {

  val path : Router[Page] = new Router[Page]

  val routes : Router.Tree[Page,Page] = path(
    HomePage,
    path("container", ContainerPage),
    path("services", ServicePage),
    path("profiles", ProfilePage),
    path("overlays", OverlayPage),
    path("rollout", RolloutPage),
    path("help", HelpPage)
  )
}

object TopLevelPageResolver {

  def topLevelPage(p: Option[Page], state: MgmtAppState) : Node = {
    p match {
      case Some(p) => p match {
        case HomePage => Component(HomePageComponent, state)
        case ContainerPage(_) => Component(ContainerPageComponent, state)
        case ServicePage(_) => Component(ServicePageComponent, state)
        case ProfilePage(_) => Component(ProfilePageComponent, state)
        case OverlayPage(_) => Component(OverlayPageComponent, state)
        case RolloutPage(_) => Component(RolloutPageComponent, state)
        case HelpPage(_) => Component(HelpPageComponent, state)
      }
      case None => E.div(E.p(Text("Not found")))
    }
  }
}
