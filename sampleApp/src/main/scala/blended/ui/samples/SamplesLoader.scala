package blended.mgmt.app

import blended.ui.samples.compoments.SampleMainComponent
import com.github.ahnfelt.react4s._

object SamplesLoader {

  def main(args: Array[String]) : Unit = {

    val main = Component(SampleMainComponent)
    ReactBridge.renderToDomById(main, "content")
  }
}