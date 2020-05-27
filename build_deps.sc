import mill.scalalib._

object Deps {
  val akkaVersion = "2.5.26"
  val akkaHttpVersion = "10.1.11"
  val akkaJsActorVersion = "2.2.6.5"
  val blendedCoreVersion = "3.1-RC6-194-a67aa0"

  val scalaVersion = "2.13.2"
  val scalaJSVersion = "1.0.1"
  val scalatestVersion = "3.1.2"
  val seleniumVersion = "3.141.59"
  val logbackVersion = "1.2.3"
  val slf4jVersion = "1.7.25"

  protected def akka(m: String) = ivy"com.typesafe.akka::akka-${m}:${akkaVersion}"
  protected def akkaHttpModule(m: String) = ivy"com.typesafe.akka::akka-${m}:${akkaHttpVersion}"

  val akkaActor = akka("actor")
  val akkaHttp = akkaHttpModule("http")
  val akkaHttpCore = akkaHttpModule("http-core")
  val akkaStream = akka("stream")
  val akkaTestkit = akka("testkit")

  protected def blended(m: String) = ivy"de.wayofquality.blended::blended.$m:$blendedCoreVersion"
  val blendedAkkaHttp = blended("akka.http")

  val cmdOption = ivy"de.tototec:de.tototec.cmdoption:0.6.0"
  val logbackCore = ivy"ch.qos.logback:logback-core:${logbackVersion}"
  val logbackClassic = ivy"ch.qos.logback:logback-classic:${logbackVersion}"

  val scalatest = ivy"org.scalatest::scalatest:$scalatestVersion"
  val scalatestSelenium = ivy"org.scalatestplus::selenium-3-141:3.1.2.0"
  val selenium = ivy"org.seleniumhq.selenium:selenium-java:$seleniumVersion"
  val slf4j = ivy"org.slf4j:slf4j-api:${slf4jVersion}"

  object Js {
    val akkaJsActor = ivy"org.akka-js::akkajsactor::$akkaJsActorVersion"
    val react4s = ivy"com.github.ahnfelt::react4s::0.10.0-SNAPSHOT"
    val scalaJsDom = ivy"org.scala-js::scalajs-dom::1.0.0"
    val scalatest = ivy"org.scalatest::scalatest::$scalatestVersion"

    val blendedJmx = ivy"de.wayofquality.blended::blended.jmx::$blendedCoreVersion"
    val blendedSecurity = ivy"de.wayofquality.blended::blended.security::$blendedCoreVersion"
    val blendedUpdaterConfig = ivy"de.wayofquality.blended::blended.updater.config::$blendedCoreVersion"
  }
}
