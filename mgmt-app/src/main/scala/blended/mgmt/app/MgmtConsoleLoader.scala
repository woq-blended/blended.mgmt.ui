package blended.mgmt.app

import com.github.ahnfelt.react4s.{Component, ReactBridge}

object MgmtConsoleLoader {

  def main(args: Array[String]) : Unit = {

    val component = Component(Main)
    ReactBridge.renderToDomById(component, "content")
  }
}
