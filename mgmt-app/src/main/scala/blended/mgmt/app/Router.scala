package blended.mgmt.app

sealed trait Page {
  val loginRequired : Boolean = true
}

final case object HomePage extends Page
final case class ContainerPage(parent: Page = HomePage) extends Page
final case class ServicePage(parent: Page = HomePage) extends Page
final case class ProfilePage(parent : Page = HomePage) extends Page
final case class OverlayPage(parent : Page = HomePage) extends Page
final case class RolloutPage(parent: Page = HomePage) extends Page
final case class HelpPage(parent: Page = HomePage) extends Page {
  override val loginRequired: Boolean = false
}
