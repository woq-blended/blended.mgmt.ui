package blended.mgmt.app

import blended.mgmt.app.components.MgmtMainComponent
import com.github.ahnfelt.react4s.{Component, ReactBridge}
import org.scalatest.FreeSpec
import org.scalajs.dom

class SampleSpec extends FreeSpec {

  "The Mgmt App should" - {

    "show up" in {

      val component = Component(MgmtMainComponent)
      ReactBridge.renderToDom(component, dom.document)

    }
  }
}
