package blended.ui.samples

import blended.material.ui.Styles
import blended.ui.material.MaterialUI.CssBaseline
import blended.ui.samples.compoments.SampleMainComponent
import blended.ui.samples.theme.Theme
import com.github.ahnfelt.react4s._

object SamplesLoader {

  def main(args: Array[String]) : Unit = {

    val t = Theme.theme

    val main = E.div(
      CssBaseline(),
      Styles.MuiThemeProvider(J("theme", t), Component(SampleMainComponent))
    )

    ReactBridge.renderToDomById(main, "content")
  }
}
