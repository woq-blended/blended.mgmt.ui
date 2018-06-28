package blended.mgmt.app.state

import blended.updater.config.ContainerInfo

sealed trait AppEvent
final case class UpdateContainerInfo(info: ContainerInfo) extends AppEvent

object MgmtAppState {
  def redux(event: AppEvent)(old: MgmtAppState) : MgmtAppState = event match {

    case UpdateContainerInfo(info) =>
      old.copy( containerInfo = old.containerInfo.filterKeys(_ != info.containerId) + (info.containerId -> info))
  }
}

case class MgmtAppState(
  containerInfo : Map[String, ContainerInfo] = Map.empty
)
