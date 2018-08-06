package blended.ui.samples.theme

import blended.ui.components.reacttable.DefaultReactTableStyle
import blended.ui.themes.{BlendedDefaultPalette, BlendedPalette, DefaultSideBarMenuTheme}
import com.github.ahnfelt.react4s._

object SamplesPalette extends BlendedDefaultPalette {

  override val primary = "#1B9375"
}

object BlendedSamplesTheme extends DefaultSideBarMenuTheme {
  override val palette: BlendedPalette = SamplesPalette
}

object BlendedSamplesTableStyle extends DefaultReactTableStyle {
  override val palette: BlendedPalette = SamplesPalette
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


