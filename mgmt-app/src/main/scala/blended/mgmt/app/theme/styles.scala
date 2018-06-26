package blended.mgmt.app.theme

import com.github.ahnfelt.react4s._

object TopBarCss extends CssClass(
  S.borderTop("2px solid " + Palette.primary),
  S.backgroundColor(Palette.background),
  S.boxShadow("0 2px 5px rgba(0, 0, 0, 0.3)"),
  S.boxSizing.borderBox(),
  S.position.absolute(),
  S.left.px(0),
  S.top.px(0),
  S.right.px(0),
  S.height.px(50)
)

object BrandTextCss extends CssClass(
  S.display.inlineBlock(),
  S.paddingTop.px(8),
  S.fontFamily("Verdana")
)

object BrandTitleCss extends CssClass(
  BrandTextCss,
  S.color(Palette.primary),
  S.paddingLeft.px(50),
  S.fontSize.px(20),
)

object LinkCss extends CssClass(
  S.color(Palette.primary),
  S.textDecoration.none(),
  S.cursor.pointer(),
  Css.hover(
    S.textDecoration("underline")
  )
)