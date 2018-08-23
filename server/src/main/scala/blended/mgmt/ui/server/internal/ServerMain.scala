package blended.mgmt.ui.server.internal

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContext}

object ServerMain {

  implicit val actorSystem : ActorSystem = ActorSystem("UiServer")
  implicit val materializer : ActorMaterializer = ActorMaterializer()
  implicit val executionContext : ExecutionContext = actorSystem.dispatcher

  private[this] lazy val svrBinding : ServerBinding = {
    val binding = Http().bindAndHandle(route, interface = "localhost", port=4444)
    Await.result(binding, 10.seconds)
  }

  val route : Route = new UiRoute(classOf[UiRoute].getClassLoader()).route

  private[this] def port() : Int = svrBinding.localAddress.getPort

  def main(args: Array[String]) : Unit = {
    println("Server Port = " + port())
    Thread.sleep(1.hour.toMillis)
  }

}
