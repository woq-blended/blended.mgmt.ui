package blended.mgmt.app.components

import blended.mgmt.app.state.{AppEvent, MgmtAppState}
import com.github.ahnfelt.react4s._

case class ContainerPageComponent(state: P[MgmtAppState]) extends Component[AppEvent] {

  override def render(get: Get): Node = E.div(
    Component(ContainerCollectionComponent, get(state).containerInfo)
  )
}
