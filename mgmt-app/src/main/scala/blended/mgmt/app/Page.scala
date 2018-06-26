package blended.mgmt.app

import blended.mgmt.app.components._
import com.github.ahnfelt.react4s._

sealed trait Page {
  val component : () => Component[NoEmit]
}

final case object HomePage extends Page {
  override val component : () => Component[NoEmit] = HomePageComponent
}

final case class ContainerPage(parent: HomePage.type = HomePage) extends Page {
  override val component: () => Component[NoEmit] = ContainerPageComponent
}

final case class HelpPage(parent: HomePage.type = HomePage) extends Page {
  override val component: () => Component[NoEmit] = HelpPageComponent
}
