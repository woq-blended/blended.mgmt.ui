package blended.mgmt.app.components

import blended.mgmt.app.state.{AppEvent, AppState}
import com.github.ahnfelt.react4s._

case class ContainerPageComponent(state: P[AppState]) extends Component[AppEvent] {

  override def render(get: Get): Node = E.div(
    Component(ContainerCollectionComponent, get(state).containerInfo)
  )
}
