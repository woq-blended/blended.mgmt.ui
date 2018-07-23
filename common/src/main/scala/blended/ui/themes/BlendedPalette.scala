package blended.ui.themes

trait BlendedPalette {

  val background : String
  val text       : String
  val primary    : String
  val success    : String
  val info       : String
  val warning    : String
  val danger     : String
  val shadow     : String
  val mainMenuText : String
}

trait BlendedDefaultPalette extends BlendedPalette {

  val background : String = "#e6e6e6"
  val text       : String = "black"
  val primary    : String = "#337ab7"
  val success    : String = "#5cb85c"
  val info       : String = "#5bc0de"
  val warning    : String = "#f0ad4e"
  val danger     : String = "#d9534f"
  val shadow     : String = "grey"
  val mainMenuText: String = "white"
}
