package blended.mgmt.app.state

import blended.mgmt.app.{HomePage, Page}
import blended.updater.config.ContainerInfo

sealed trait AppEvent
final case class UpdateContainerInfo(info: ContainerInfo) extends AppEvent
final case class UpdateCurrentPage(p: Option[Page]) extends AppEvent

object AppState {
  def redux(event: AppEvent)(old: AppState) : AppState = event match {

    case UpdateContainerInfo(info) =>
      old.copy( containerInfo = old.containerInfo.filterKeys(_ != info.containerId) + (info.containerId -> info))

    case UpdateCurrentPage(page) =>
      old.copy(currentPage = page)
  }
}

case class AppState(
  currentPage: Option[Page] = Some(HomePage),
  containerInfo : Map[String, ContainerInfo] = Map.empty
)
