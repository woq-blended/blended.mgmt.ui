package blended.mgmt.app

import blended.ui.router.Router

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
