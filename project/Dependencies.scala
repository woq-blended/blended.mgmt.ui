import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

object Project {
  val organization = "de.wayofquality.blended"
}

object Versions {

  val akka = "2.5.19"
  val akkaHttp = "10.1.5"
  val akkaJs = "1.2.5.13"
  val blended = "3.0-M4"
  val cmdOption = "0.6.0"
  val dominoOsgi = "1.1.3"
  val jsdom = "11.12.0"
  val logback = "1.2.3"
  val prickle = "1.1.14"
  val react = "16.4.2"
  val react4s = "0.9.15-SNAPSHOT"
  val scalaJsDom = "0.9.5"
  val scalaTest = "3.0.5"
  val selenium = "3.13.0"
  val slf4j = "1.7.25"
}

object NpmDependencies {

  val jsDom = "jsdom" -> Versions.jsdom
  val jsonWebToken = "jsonwebtoken" -> "8.3.0"
  val materialUi = "@material-ui/core" -> "1.4.3"
  val materialIcons = "@material-ui/icons" -> "2.0.0"
  val react = "react" -> Versions.react
  val reactDom = "react-dom" -> Versions.react
}

object JsDependencies {
  val akkaJsActor = Def.setting("org.akka-js" %%% "akkajsactor" % Versions.akkaJs)
  val blendedSecurity = Def.setting(Project.organization %%% "blended.security" % Versions.blended)
  val blendedUpdaterConfig = Def.setting(Project.organization %%% "blended.updater.config" % Versions.blended)
  val prickle = Def.setting("com.github.benhutchison" %%% "prickle" % Versions.prickle)
  val react4s = Def.setting("com.github.ahnfelt" %%% "react4s" % Versions.react4s)
  val scalaJsDom = Def.setting("org.scala-js" %%% "scalajs-dom" % Versions.scalaJsDom)
  val scalaTestJs = Def.setting("org.scalatest" %%% "scalatest" % Versions.scalaTest)
}

object JavaDependencies {

  val akkaHttp = "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp
  val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % Versions.akka
  val blendedAkkaHttp = Project.organization %% "blended.akka.http" % Versions.blended
  val blendedDomino = Project.organization %% "blended.domino" % Versions.blended
  val cmdOption = "de.tototec" % "de.tototec.cmdoption" % Versions.cmdOption
  val dominoOsgi = "com.github.domino-osgi" %% "domino" % Versions.dominoOsgi
  val logbackClassic = "ch.qos.logback" % "logback-classic" % Versions.logback
  val logbackCore = "ch.qos.logback" % "logback-core" % Versions.logback
  val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest
  val selenium = "org.seleniumhq.selenium" % "selenium-java" % Versions.selenium
  val slf4jApi = "org.slf4j" % "slf4j-api" % Versions.slf4j
}
