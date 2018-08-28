package blended.mgmt.app

import blended.material.ui.Styles
import blended.mgmt.app.components.MgmtMainComponent
import blended.mgmt.app.theme.Theme
import blended.ui.material.MaterialUI.CssBaseline
import com.github.ahnfelt.react4s._

object MgmtConsoleLoader {

  def main(args: Array[String]) : Unit = {

    val t = Theme.theme

    val main = E.div(
      CssBaseline(),
      Styles.MuiThemeProvider(J("theme", t), Component(MgmtMainComponent))
    )

    ReactBridge.renderToDomById(main, "content")
  }
}
