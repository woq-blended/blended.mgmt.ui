package blended.mgmt.app.state

import blended.updater.config.ContainerInfo

sealed trait AppEvent
final case class UpdateContainerInfo(info: ContainerInfo) extends AppEvent
final case class LoggedIn(user: String) extends AppEvent
final case class LoggedOut(user: String) extends AppEvent

object MgmtAppState {

  def redux(event: AppEvent)(old: MgmtAppState) : MgmtAppState = {

    event match {
      case UpdateContainerInfo(info) =>
        old.copy(
          containerInfo = old.containerInfo.filterKeys(_ != info.containerId) + (info.containerId -> info)
        )

      case LoggedIn(user: String) =>
        old.copy(currentUser = Some(user))

      case LoggedOut(_: String) =>
        old.copy(currentUser = None)
    }
  }
}

case class MgmtAppState(
  currentUser : Option[String] = None,
  containerInfo : Map[String, ContainerInfo] = Map.empty
)
