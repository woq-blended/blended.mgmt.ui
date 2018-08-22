package blended.mgmt.ui.server.internal

import blended.akka.http.{HttpContext, SimpleHttpContext}
import domino.DominoActivator

class UiServerActivator extends DominoActivator {

  whenBundleActive {
    // In an OSGi container this will be picked by the Akka Http service using a whiteboard pattern
    SimpleHttpContext("management", UiRoute.route).providesService[HttpContext]
  }
}
