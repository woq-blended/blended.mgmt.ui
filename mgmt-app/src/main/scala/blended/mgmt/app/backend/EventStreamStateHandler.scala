package blended.mgmt.app.backend

import akka.actor.Actor
import blended.mgmt.app.state.AppEvent
import blended.ui.common.Logger

import scala.util.{Failure, Success, Try}

object EventStreamStateHandler {

  def props(handleEvent : AppEvent => Try[Unit]) = new EventStreamStateHandler(handleEvent)

}

class EventStreamStateHandler(handleEvent: AppEvent => Try[Unit]) extends Actor {

  private[this] val log = Logger[EventStreamStateHandler]

  override def receive: Receive = {
    case evt : AppEvent => {
      log.debug(s"Handling event [$evt]")
      handleEvent(evt) match {
        case Failure(e) => log.error(s"Failed to process event [$evt] : ${e.getMessage()}")
        case Success(_) => log.debug(s"Successfully processed event [$evt]")
      }
    }
  }
}
