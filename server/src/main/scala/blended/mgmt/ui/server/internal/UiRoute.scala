package blended.mgmt.ui.server.internal

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

object UiRoute {

  // All resources will be served from the classpath
  val route : Route = getFromResourceDirectory("")
}
