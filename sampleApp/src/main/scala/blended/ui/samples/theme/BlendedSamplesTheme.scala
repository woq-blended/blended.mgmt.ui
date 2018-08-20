package blended.ui.samples.theme

import blended.material.ui.Colors
import blended.ui.components.material.{MaterialComponents, MaterialStyles}
import blended.ui.material.Styles
import blended.ui.samples.SamplePage
import blended.ui.samples.state.SampleAppState

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

object SampleStyles extends MaterialStyles {
  override val theme: js.Dynamic = Theme.theme
}

object SampleMaterialComponents extends MaterialComponents[SampleAppState, SamplePage] {
  override implicit val styles: MaterialStyles = SampleStyles
  override val appTitle: SampleAppState => String = { _ => "Blended Sample Components"}
}
