package blended.mgmt.app

sealed trait Page

case object HomePage extends Page
case class ContainerPage(parent: HomePage.type ) extends Page
case class HelpPage(parent: HomePage.type) extends Page
