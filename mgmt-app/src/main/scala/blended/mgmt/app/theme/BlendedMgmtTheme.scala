package blended.mgmt.app.theme

import blended.ui.themes.{BlendedPalette, SidebarMenuTheme}

object BlendedMgmtPalette extends BlendedPalette {

  val background : String = "#e6e6e6"
  val text       : String = "black"
  val primary    : String = "#337ab7"
  val success    : String = "#5cb85c"
  val info       : String = "#5bc0de"
  val warning    : String = "#f0ad4e"
  val danger     : String = "#d9534f"
}

object BlendedMgmtTheme extends SidebarMenuTheme {

  override val palette: BlendedPalette = BlendedMgmtPalette
}