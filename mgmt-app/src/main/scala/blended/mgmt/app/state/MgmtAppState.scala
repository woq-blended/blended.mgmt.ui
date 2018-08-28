package blended.mgmt.app.state

import akka.actor.{ActorRef, ActorSystem}
import blended.mgmt.app.backend.{UserInfo, WSClientActor}
import blended.mgmt.app.{HomePage, Page}
import blended.updater.config.ContainerInfo
import prickle.Unpickle
import blended.updater.config.json.PrickleProtocol._

sealed trait AppEvent
final case class UpdateContainerInfo(info: ContainerInfo) extends AppEvent
final case class LoggedIn(host: String, port: Int, user: UserInfo) extends AppEvent
case object LoggedOut extends AppEvent
final case class PageSelected(p: Option[Page]) extends AppEvent

object MgmtAppState {

  def redux(event: AppEvent)(old: MgmtAppState) : MgmtAppState = {

    event match {
      case UpdateContainerInfo(info) =>
        old.copy(
          containerInfo = old.containerInfo.filterKeys(_ != info.containerId) + (info.containerId -> info)
        )

      case LoggedIn(host: String, port: Int, user: UserInfo) =>
        old.copy(
          host = host,
          port = port,
          currentUser = Some(user),
          ctListener = Some( {

            val handleCtInfo : PartialFunction[Any, Unit] = {
              case s : String =>
                Unpickle[ContainerInfo].fromString(s).map { ctInfo =>
                  old.actorSystem.eventStream.publish(UpdateContainerInfo(ctInfo))
                }
            }

            old.actorSystem.actorOf(WSClientActor.props(
              "ws://localhost:9995/mgmtws/timer?name=test",
              handleCtInfo
            ))
          })
        )

      case LoggedOut =>
        old.ctListener.foreach(a => old.actorSystem.stop(a))
        old.copy(
          containerInfo = Map.empty,
          currentPage = Some(HomePage),
          currentUser = None
        )

      case PageSelected(p) =>
        old.copy(currentPage = p)
    }
  }
}

case class MgmtAppState(
  host : String = "localhost",
  // scalastyle:off magic.number
  port : Integer = 9995,
  // scalastyle:on magic.number
  currentPage : Option[Page] = Some(HomePage),
  currentUser : Option[UserInfo] = None,
  containerInfo : Map[String, ContainerInfo] = Map.empty,
  ctListener : Option[ActorRef] = None,
  serverPublicKey : Option[String] = None
) {

  private[this] val system : ActorSystem = ActorSystem("MgmtApp")
  def actorSystem : ActorSystem = system

}
