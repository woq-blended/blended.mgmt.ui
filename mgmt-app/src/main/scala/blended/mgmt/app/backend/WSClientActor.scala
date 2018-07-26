package blended.mgmt.app.backend

import akka.actor.{Actor, ActorLogging, Props}
import org.scalajs.dom.raw.WebSocket
import scala.concurrent.duration._

object WSClientActor {

  def props(url: String, onMessage: PartialFunction[Any, Unit]) : Props = {
    Props(new WSClientActor(url, onMessage))
  }
}

class WSClientActor(url: String, onMessage: PartialFunction[Any, Unit]) extends Actor with ActorLogging {

  private case object Initialize
  private case class Closed(reason: String)

  override def preStart(): Unit = self ! Initialize

  override def receive: Receive = initializing

  def initializing : Receive = {
    case Initialize =>
      val socket = new WebSocket(url)

      socket.onopen = {_ =>  log.info(s"Connected to [$url].") }

      socket.onclose = { e =>
        self ! Closed(e.reason)
      }

      socket.onerror = { _ =>
        socket.close()
        self ! Closed(s"Closed upon error in Web Socket!")
      }
      socket.onmessage = { e =>
        onMessage(e.data)
      }
  }

  def active(socket: WebSocket) : Receive = {
    case Closed(reason) =>
      log.info(s"Web Socket connection to [$url] closed: [$reason]")
      context.become(initializing)
      context.system.scheduler.scheduleOnce(1.second, self, Initialize)(context.dispatcher)
  }
}
