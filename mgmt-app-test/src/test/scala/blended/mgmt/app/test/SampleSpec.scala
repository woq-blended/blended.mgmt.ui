package blended.mgmt.app.test

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.LoggingMagnet
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.selenium.WebBrowser
import org.scalatest.{BeforeAndAfterAll, FreeSpecLike}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

class SampleSpec extends TestKit(ActorSystem("uitest"))
  with FreeSpecLike
  with WebBrowser
  with BeforeAndAfterAll {

  implicit val actorSystem : ActorSystem = system
  implicit val materializer : ActorMaterializer = ActorMaterializer()
  implicit val executionContext : ExecutionContext = system.dispatcher

  private[this] var svrBinding : Option[ServerBinding] = None
  private[this] val port = 9999


  val chromeOptions = new ChromeOptions()
  chromeOptions.addArguments("--headless", "--disable-gpu")

  implicit val driver : WebDriver = new ChromeDriver(chromeOptions)

  val route : Route = getFromBrowseableDirectory(System.getProperty("appUnderTest"))

  override protected def beforeAll(): Unit = {
    val binding = Http().bindAndHandle(route, "localhost", port)
    svrBinding = Some(Await.result(binding, 10.seconds))
  }

  override protected def afterAll(): Unit = svrBinding.foreach(_.unbind().flatMap(_ => system.terminate()))

  "The Mgmt App should" - {

    val url = s"http://localhost:$port/index-dev.html"

    "show up" in {
      go.to(url)
      assert(pageTitle == "Blended Management Console")
      if (isScreenshotSupported) {
        setCaptureDir("target")
        capture.to("screen.png")
      }
      close()
    }
  }

}