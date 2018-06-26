package blended.mgmt.app

sealed trait Page

final case object HomePage extends Page
final case class ContainerPage(parent: HomePage.type = HomePage) extends Page
final case class HelpPage(parent: HomePage.type = HomePage) extends Page
