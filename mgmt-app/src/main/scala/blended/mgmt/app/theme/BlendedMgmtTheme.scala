package blended.mgmt.app.theme

import blended.material.ui.Colors
import blended.mgmt.app.Page
import blended.mgmt.app.state.MgmtAppState
import blended.ui.components.material.{MaterialComponents, MaterialStyles}
import blended.ui.material.Styles

import scala.scalajs.js

object Theme {

  val palette : js.Dynamic  = js.Dynamic.literal(
    "palette" -> js.Dynamic.literal (
      "primary" -> js.Dynamic.literal (
        "main" -> Colors.green("900")
      ),
      "background" -> js.Dynamic.literal (
        "default" -> "#fafafa"
      )
    )
  )

  val theme = Styles.createMuiTheme(palette)
}

object BlendedMgmtStyles extends MaterialStyles {
  override val theme: js.Dynamic = Theme.theme
}

object MgmtMaterialComponents extends MaterialComponents[MgmtAppState, Page] {

  override implicit val styles: MaterialStyles = BlendedMgmtStyles
  override val appTitle: MgmtAppState => String = { _ => "Blended Management Console" }
}
