package blended.mgmt.app

import blended.mgmt.app.components.MainComponent
import com.github.ahnfelt.react4s.{Component, ReactBridge}

object MgmtConsoleLoader {

  def main(args: Array[String]) : Unit = {

    val component = Component(MainComponent)
    ReactBridge.renderToDomById(component, "content")
  }
}
