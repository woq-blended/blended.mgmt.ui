package blended.ui.samples

import blended.material.ui.Colors
import blended.ui.material.MaterialUI.CssBaseline
import blended.ui.material.Styles
import blended.ui.samples.compoments.SampleMainComponent
import com.github.ahnfelt.react4s._

import scala.scalajs.js

object SamplesLoader {

  def main(args: Array[String]) : Unit = {

    object Palette {
      val palette : js.Dynamic  = js.Dynamic.literal(
        "palette" -> js.Dynamic.literal (
          "primary" -> js.Dynamic.literal (
            "main" -> Colors.green("900")
          )
        )
      )
    }

    val theme = Styles.createMuiTheme(Palette.palette)

    val main = E.div(
      CssBaseline(),
      Styles.MuiThemeProvider(J("theme", theme), Component(SampleMainComponent))
    )

    ReactBridge.renderToDomById(main, "content")
  }
}
