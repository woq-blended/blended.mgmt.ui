package blended.mgmt.app

sealed trait Page {
  val title : String
  val loginRequired : Boolean = true
}

final case object HomePage extends Page {
  override val title: String = "home"
}

final case class ContainerPage(parent: HomePage.type = HomePage) extends Page {
  override val title: String = "container"
}

final case class ServicePage(parent: HomePage.type = HomePage) extends Page {
  override val title: String = "services"
}

final case class ProfilePage(parent : HomePage.type = HomePage) extends Page {
  override val title: String = "profiles"
}

final case class OverlayPage(parent : HomePage.type = HomePage) extends Page {
  override val title: String = "overlays"
}

final case class RolloutPage(parent: HomePage.type = HomePage) extends Page {
  override val title: String = "rollout"
}

final case class HelpPage(parent: HomePage.type = HomePage) extends Page {
  override val title: String = "help"
  override val loginRequired: Boolean = false
}
