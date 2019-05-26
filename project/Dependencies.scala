import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

object ProjectDeprecated {
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
  val react4s = "0.9.26-SNAPSHOT"
  val scalaJsDom = "0.9.5"
  val scalaTest = "3.0.5"
  val selenium = "3.13.0"
  val slf4j = "1.7.25"
}

object NpmDependencies {

  val jsDom : (String, String) = "jsdom" -> Versions.jsdom
  val jsonWebToken : (String, String) = "jsonwebtoken" -> "8.3.0"
  val materialUi : (String, String) = "@material-ui/core" -> "1.4.3"
  val materialIcons : (String, String) = "@material-ui/icons" -> "2.0.0"
  val react : (String, String) = "react" -> Versions.react
  val reactDom : (String, String) = "react-dom" -> Versions.react
}

object JsDependencies {
  val akkaJsActor : Def.Initialize[ModuleID] =
    Def.setting("org.akka-js" %%% "akkajsactor" % Versions.akkaJs)

  val blendedSecurity : Def.Initialize[ModuleID] =
    Def.setting(ProjectDeprecated.organization %%% "blended.security" % Versions.blended)

  val blendedUpdaterConfig : Def.Initialize[ModuleID] =
    Def.setting(ProjectDeprecated.organization %%% "blended.updater.config" % Versions.blended)

  val prickle : Def.Initialize[ModuleID] =
    Def.setting("com.github.benhutchison" %%% "prickle" % Versions.prickle)

  val react4s : Def.Initialize[ModuleID] =
    Def.setting("com.github.ahnfelt" %%% "react4s" % Versions.react4s)

  val scalaJsDom : Def.Initialize[ModuleID] =
    Def.setting("org.scala-js" %%% "scalajs-dom" % Versions.scalaJsDom)

  val scalaTestJs : Def.Initialize[ModuleID] =
    Def.setting("org.scalatest" %%% "scalatest" % Versions.scalaTest)
}

object JavaDependencies {
  val akkaHttp : ModuleID = "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp
  val akkaHttpTestkit : ModuleID = "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp
  val akkaStream : ModuleID = "com.typesafe.akka" %% "akka-stream" % Versions.akka
  val blendedAkkaHttp : ModuleID = ProjectDeprecated.organization %% "blended.akka.http" % Versions.blended
  val blendedDomino : ModuleID = ProjectDeprecated.organization %% "blended.domino" % Versions.blended
  val cmdOption : ModuleID = "de.tototec" % "de.tototec.cmdoption" % Versions.cmdOption
  val dominoOsgi : ModuleID = "com.github.domino-osgi" %% "domino" % Versions.dominoOsgi
  val logbackClassic : ModuleID = "ch.qos.logback" % "logback-classic" % Versions.logback
  val logbackCore : ModuleID = "ch.qos.logback" % "logback-core" % Versions.logback
  val scalaTest : ModuleID = "org.scalatest" %% "scalatest" % Versions.scalaTest
  val selenium : ModuleID = "org.seleniumhq.selenium" % "selenium-java" % Versions.selenium
  val slf4jApi : ModuleID = "org.slf4j" % "slf4j-api" % Versions.slf4j
}
