package blended.mgmt.app.state

import blended.mgmt.app.backend.UserInfo
import blended.mgmt.app.{HomePage, Page}
import blended.updater.config.ContainerInfo

sealed trait AppEvent
final case class UpdateContainerInfo(info: ContainerInfo) extends AppEvent
final case class LoggedIn(user: UserInfo) extends AppEvent
final case object LoggedOut extends AppEvent
final case class PageSelected(p: Option[Page]) extends AppEvent

object MgmtAppState {

  def redux(event: AppEvent)(old: MgmtAppState) : MgmtAppState = {

    event match {
      case UpdateContainerInfo(info) =>
        old.copy(
          containerInfo = old.containerInfo.filterKeys(_ != info.containerId) + (info.containerId -> info)
        )

      case LoggedIn(user: UserInfo) =>
        old.copy(currentUser = Some(user))

      case LoggedOut =>
        old.copy(currentUser = None)

      case PageSelected(p) =>
        old.copy(currentPage = p)
    }
  }
}

case class MgmtAppState(
  currentPage : Option[Page] = Some(HomePage),
  currentUser : Option[UserInfo] = None,
  containerInfo : Map[String, ContainerInfo] = Map.empty
)
