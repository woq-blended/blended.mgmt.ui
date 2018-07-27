package blended.ui.samples

import blended.ui.material._
import blended.ui.samples.compoments.SampleMainComponent
import com.github.ahnfelt.react4s._

object SamplesLoader {

  def main(args: Array[String]) : Unit = {

    val theme = Styles.createMuiTheme(Colors.palette)

    val main = E.div(
      CssBaseline(),
      MuiThemeProvider(J("theme", theme), Component(SampleMainComponent))
    )

    ReactBridge.renderToDomById(main, "content")
  }
}
