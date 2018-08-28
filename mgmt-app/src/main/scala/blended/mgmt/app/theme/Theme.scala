package blended.mgmt.app.theme

import blended.material.ui.{Colors, Styles}
import com.github.ahnfelt.react4s.{CssClass, S}

import scala.scalajs.js

object Theme {

  val palette : js.Dynamic  = js.Dynamic.literal(
    "palette" -> js.Dynamic.literal (
      "primary" -> js.Dynamic.literal (
        "main" -> Colors.purple("900")
      ),
      "background" -> js.Dynamic.literal (
        "default" -> "#fafafa"
      )
    )
  )

  val theme = Styles.createMuiTheme(palette)

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

  // Applied to the Paper of the login component
  object LoginPaper extends CssClass (
    S.width.px(400),
    S.padding.px(spacingUnit * 2),
    S.margin("auto")
  )

  object LoginComponent extends CssClass (
    S.marginTop.pt(spacingUnit),
    S.marginBottom.pt(spacingUnit),
  )

  object LoginTitle extends CssClass (
    S.background(theme.palette.primary.main.asInstanceOf[String]),
    S.color(theme.palette.primary.contrastText.asInstanceOf[String]),
  )
}