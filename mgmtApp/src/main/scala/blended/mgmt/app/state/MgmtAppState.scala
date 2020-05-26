package blended.mgmt.app.state

import akka.actor.{ActorRef, ActorSystem}
import blended.jmx.JmxObjectName
import blended.mgmt.app.backend.{UserInfo, WSClientActor}
import blended.mgmt.app.{HomePage, Page}
import blended.ui.common.Logger
import blended.updater.config.ContainerInfo
import prickle._
import blended.updater.config.json.PrickleProtocol._
import blended.jmx.json.PrickleProtocol._
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}

sealed trait AppEvent
final case class UpdateContainerInfo(info: ContainerInfo) extends AppEvent
final case class LoggedIn(host: String, port: Int, user: UserInfo) extends AppEvent
case object LoggedOut extends AppEvent
final case class PageSelected(p: Option[Page]) extends AppEvent

object MgmtAppState {

  private[this] val log = Logger[MgmtAppState.type]

  val events : mutable.ListBuffer[AppEvent] = ListBuffer.empty

  def redux(event: AppEvent)(old: MgmtAppState) : MgmtAppState = {

    event match {
      case UpdateContainerInfo(info) =>
        old.copy(
          containerInfo = old.containerInfo.filterKeys(_ != info.containerId).toMap + (info.containerId -> info)
        )

      case LoggedIn(host: String, port: Int, user: UserInfo) =>
        old.copy(
          host = host,
          port = port,
          currentUser = Some(user),
          ctListener = Some( {

            val handleCtInfo : PartialFunction[Any, Unit] = {
              case s : String =>
                Unpickle[ContainerInfo].fromString(s) match {
                  case Success(ctInfo) =>
                    log.info(s"Publishing container AppEvents [$ctInfo]")
                    events.append(UpdateContainerInfo(ctInfo))
                  case Failure(exception) =>
                }

                Unpickle[Seq[JmxObjectName]].fromString(s) match {
                  case Success(names) =>
                    log.info(s"Got new object name list: $names")
                  case Failure(_) =>
                }
            }

            old.system.actorOf(WSClientActor.props(
              s"ws://$host:$port/mgmtws/?token=${user.token}",
              handleCtInfo
            ))
          })
        )

      case LoggedOut =>
        old.ctListener.foreach(a => old.system.stop(a))
        old.copy(
          ctListener = None,
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

  lazy val conf : Config = ConfigFactory
    .parseString(
      """akka {
         loglevel = "DEBUG"
         stdout-loglevel = "DEBUG"
        }""")
    .withFallback(akkajs.Config.default)

  val system : ActorSystem = ActorSystem("MgmtApp", conf)

}
