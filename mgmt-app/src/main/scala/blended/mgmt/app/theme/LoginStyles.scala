package blended.mgmt.app.theme

import blended.mgmt.app.theme.Theme._
import com.github.ahnfelt.react4s._

trait LoginStyles {

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
