package blended.mgmt.app.components

import blended.mgmt.app.state.{AppEvent, MgmtAppState}
import blended.mgmt.app.theme.BlendedMgmtTableStyle
import com.github.ahnfelt.react4s._

case class ContainerPageComponent(state: P[MgmtAppState]) extends Component[AppEvent] {

  override def render(get: Get): Node =
    Component(ContainerTable.ReactTable, get(state).containerInfo.values.toSeq, ContainerTable.props, BlendedMgmtTableStyle)

}
