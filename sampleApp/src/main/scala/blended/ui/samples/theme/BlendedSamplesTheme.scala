package blended.ui.samples.theme

import blended.material.ui.Colors
import blended.ui.components.reacttable.DefaultReactTableStyle
import blended.ui.material.Styles
import blended.ui.themes.{BlendedDefaultPalette, BlendedPalette, DefaultSideBarMenuTheme}
import com.github.ahnfelt.react4s._

import scala.scalajs.js

object SamplesPalette extends BlendedDefaultPalette {

  override val primary = "#1B9375"
}

object BlendedSamplesTheme extends DefaultSideBarMenuTheme {
  override val palette: BlendedPalette = SamplesPalette
}

object BlendedSamplesTableStyle extends DefaultReactTableStyle {
  override val palette: BlendedPalette = SamplesPalette
}

object Theme {

  val palette : js.Dynamic  = js.Dynamic.literal(
    "palette" -> js.Dynamic.literal (
      "primary" -> js.Dynamic.literal (
        "main" -> Colors.green("900")
      ),
      "background" -> js.Dynamic.literal (
        "default" -> Colors.lightBlue("700")
      )
    )
  )

  val theme = Styles.createMuiTheme(palette)
}


object AppBarStyles {

  object root extends CssClass (
    S.flexGrow("1")
  )

  object flex extends CssClass (
    S.flexGrow("1")
  )

  object menuButton extends CssClass (
    S.marginLeft.pt(-12),
    S.marginRight.pt(20)
  )
}

object ContentStyles {

  object root extends CssClass (
    S.position("absolute"),
    S.height("100%"),
    S.width("100%"),
    S.background(Theme.theme.palette.background.default.asInstanceOf[String]),
    S.padding(s"${Theme.theme.spacing.unit.asInstanceOf[Int] * 3}pt")
  )
}


