package blended.ui.components.material

abstract class MaterialComponents[AS,P] {

  implicit val styles : MaterialStyles
  val appTitle : AS => String

  object AppBar extends AppBarT[AS](appTitle)
  object MenuDrawer extends MenuDrawerT[P]()
}
