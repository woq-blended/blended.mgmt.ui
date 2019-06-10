package blended.material.ui

import scala.scalajs.js

object PaletteIntention {
  def apply(
    fromJs: js.Dynamic
  ): PaletteIntention = {
    PaletteIntention(
      light = fromJs.light.toString(),
      main = fromJs.main.toString(),
      dark = fromJs.dark.toString(),
      contrastText = fromJs.contrastText.toString()
    )
  }
}

case class PaletteIntention(
  light: String,
  main: String,
  dark: String,
  contrastText: String
)

