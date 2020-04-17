package blended.ui.samples

import blended.material.ui.Styles
import blended.mgmt.ui.theme.Theme
import blended.material.ui.MaterialUI.CssBaseline
import blended.ui.samples.components.SampleMainComponent
import com.github.ahnfelt.react4s._

object SamplesLoader {

  def main(args: Array[String]) : Unit = {

    val main = E.div(
      CssBaseline(),
      Styles.MuiThemeProvider(
        J("theme", Theme.theme),
        Component(SampleMainComponent)
      )
    )

    ReactBridge.renderToDomById(main, "content")
  }
}
