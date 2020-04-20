package blended.mgmt.app.theme

import blended.material.ui.{Colors, Styles}
import com.github.ahnfelt.react4s.{CssClass, S}

import scala.scalajs.js

object Theme extends LoginStyles {

  val palette : js.Dynamic  = js.Dynamic.literal(
    "palette" -> js.Dynamic.literal (
      "primary" -> js.Dynamic.literal (
        "main" -> Colors.blue("500")
      ),
      "secondary" -> js.Dynamic.literal (
        "main" -> Colors.lightBlue("A400")
      ),
      "background" -> js.Dynamic.literal (
        "default" -> "#fafafa"
      )
    )
  )

  val theme : js.Dynamic = Styles.createMuiTheme(palette)

  val spacingUnit : Int = theme.spacing.unit.asInstanceOf[Int]

  object RootStyles extends CssClass (
    S.flexGrow.number(1),
    S.height.percent(100),
    S.width.percent(100),
    S.zIndex.number(1),
    S.overflow("hidden"),
    S.position.absolute(),
    S.display("flex")
  )

  // Applied to the Application Bar
  object AppBarStyles extends CssClass (
    S.position.fixed(),
    S.zIndex.number(theme.zIndex.drawer.asInstanceOf[Int] + 1)
  )

  // Applied to the side Menu
  object MenuDrawerStyles extends CssClass (
    S.position.relative(),
    S.height.percent(100),
    S.width.px(200)
  )

  // Applied to the (everything next to the menu drawer including the space UNDER the AppBar)
  object ContentStyles extends CssClass (
    S.position.relative(),
    S.width.percent(100),
    S.display("flex"),
    S.flexDirection("column"),
  )

  // Applied the the content Area (same as content without the AppBar)
  object ContentArea extends CssClass (
    ContentStyles,
    S.height.percent(100),
    S.background(theme.palette.background.default.asInstanceOf[String]),
    S.padding.pt(spacingUnit * 3)
  )

}