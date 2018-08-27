package blended.mgmt.app.backend

import akka.actor.Actor
import blended.mgmt.app.state.MgmtAppState
import blended.ui.common.Logger
import org.scalajs.dom.raw.XMLHttpRequest
import org.scalajs.dom.ext.Ajax

import scala.util.{Failure, Try}

case class BackendCallRequest(
  appState : MgmtAppState,
  urlContext : String,
  useToken: Boolean = true,
  user : Option[String] = None,
  pwd: Option[String] = None
)

case class BackendCallResult[T](result: Try[T])

object BackendCallActor {
  def props[T](responseMapper: XMLHttpRequest => T) = new BackendCallActor[T](responseMapper)
}

class BackendCallActor[T](responseMapper : XMLHttpRequest => T) extends Actor {

  private[this] val log = Logger[BackendCallActor[T]]

  override def receive: Receive = {
    case request : BackendCallRequest =>
      log.debug(s"Executing request to [${request.urlContext}]")
      sender() ! BackendCallResult[T](Failure(new Exception("Boom")))
  }
}
