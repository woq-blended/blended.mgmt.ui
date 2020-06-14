import mill.scalalib._

import $ivy.`de.wayofquality.blended::blended-mill:0.3`
import de.wayofquality.blended.mill.modules.BlendedDependencies

trait UiDeps extends BlendedDependencies { deps =>
  val blendedCoreVersion : String = "3.2-alpha1-41-g89bae49f7"

  val akkaJsActorVersion = "2.2.6.5"
  val seleniumVersion = "3.141.59"

  val blendedAkkaHttp = blendedDep(blendedCoreVersion)("akka.http")

  val scalatestSelenium = ivy"org.scalatestplus::selenium-3-141:3.1.2.0"
  val selenium = ivy"org.seleniumhq.selenium:selenium-java:$seleniumVersion"

  object Js {
    val akkaJsActor = ivy"org.akka-js::akkajsactor::$akkaJsActorVersion"
    val react4s = ivy"com.github.ahnfelt::react4s::0.10.0-SNAPSHOT"
    val scalaJsDom = ivy"org.scala-js::scalajs-dom::1.0.0"

    val blendedJmx = toJs(blendedDep(blendedCoreVersion)("jmx"))
    val blendedSecurity = toJs(blendedDep(blendedCoreVersion)("security"))
    val blendedUpdaterConfig = toJs(blendedDep(blendedCoreVersion)("updater.config"))
  }
}

object UiDeps {
  def scalaVersions : Map[String, UiDeps] = Map(Deps_2_13.scalaVersion -> Deps_2_13)
  object Deps_2_13 extends UiDeps
}
