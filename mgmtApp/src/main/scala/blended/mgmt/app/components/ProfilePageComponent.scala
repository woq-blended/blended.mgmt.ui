package blended.mgmt.app.components

import blended.mgmt.app.state._
import com.github.ahnfelt.react4s._

case class ProfilePageComponent(state: P[MgmtAppState]) extends Component[AppEvent] {

  override def render(get: Get): Node = E.div(
    E.h1(Text("Profiles"))
  )
}
