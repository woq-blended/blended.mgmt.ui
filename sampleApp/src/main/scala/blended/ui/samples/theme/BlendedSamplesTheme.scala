package blended.ui.samples.theme

import blended.ui.components.reacttable.DefaultReactTableStyle
import blended.ui.themes.{BlendedDefaultPalette, BlendedPalette, DefaultSideBarMenuTheme}

object SamplesPalette extends BlendedDefaultPalette {

  override val primary = "#1B9375"
}

object BlendedSamplesTheme extends DefaultSideBarMenuTheme {
  override val palette: BlendedPalette = SamplesPalette
}

object BlendedSamplesTableStyle extends DefaultReactTableStyle {
  override val palette: BlendedPalette = SamplesPalette
}


