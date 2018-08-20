package blended.mgmt.app

sealed trait Page {
  val loginRequired : Boolean = true
}

final case object HomePage extends Page
final case class ContainerPage(parent: HomePage.type = HomePage) extends Page
final case class ServicePage(parent: HomePage.type = HomePage) extends Page
final case class ProfilePage(parent : HomePage.type = HomePage) extends Page
final case class OverlayPage(parent : HomePage.type = HomePage) extends Page
final case class RolloutPage(parent: HomePage.type = HomePage) extends Page
final case class HelpPage(parent: HomePage.type = HomePage) extends Page {
  override val loginRequired: Boolean = false
}
