package blended.mgmt.app

import blended.mgmt.app.components.MgmtMainComponent
import com.github.ahnfelt.react4s._

object MgmtConsoleLoader {

  def main(args: Array[String]) : Unit = {

    val main = Component(MgmtMainComponent)
    ReactBridge.renderToDomById(main, "content")
  }
}
