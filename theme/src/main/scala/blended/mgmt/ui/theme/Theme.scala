package blended.mgmt.ui.theme

import blended.material.ui.{Colors, Styles}
import com.github.ahnfelt.react4s._

import scala.scalajs.js

object Theme {

  val palette : js.Dynamic  = js.Dynamic.literal(
    "palette" -> js.Dynamic.literal (
      "primary" -> js.Dynamic.literal (
        "main" -> Colors.brown("700")
      ),
      "secondary" -> js.Dynamic.literal (
        "main" -> Colors.deepOrange("200")
      ),
      "background" -> js.Dynamic.literal (
        "default" -> Colors.grey("200").asInstanceOf[String]
      )
    )
  )

  val theme : js.Dynamic = Styles.createMuiTheme(palette)

  val primary : String = theme.palette.primary.main.asInstanceOf[String]
  val secondary : String = theme.palette.secondary.main.asInstanceOf[String]
  val background : String = theme.palette.background.default.asInstanceOf[String]

  val spacingUnit : Int = theme.spacing.unit.asInstanceOf[Int]

  // Applied to the Browser window as a hole
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
    S.background(background),
    S.padding.px(spacingUnit * 3)
  )

  object IconStyles extends CssClass (
    S.color(secondary)
  )
}
