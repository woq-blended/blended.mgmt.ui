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

object ColumnCss extends CssClass(
  S.position.absolute(),
  S.boxSizing.borderBox(),
  S.top.px(0),
  S.bottom.px(0)
)

object ColumnContainerCss extends CssClass(
  S.position.absolute(),
  S.top.px(50),
  S.bottom.px(0),
  S.left.px(0),
  S.right.px(0),
)

object ContentColumnCss extends CssClass(
  ColumnCss,
  S.left.px(200),
  S.right.px(0)
)

object MenuColumnCss extends CssClass(
  ColumnCss,
  S.left.px(0),
  S.width.px(200)
)

object MenuCategoryCss extends CssClass(
  S.paddingTop.px(20),
  S.paddingLeft.px(20),
  S.textTransform("uppercase"),
  S.fontFamily("Verdana"),
  S.fontSize.px(14),
  S.color(Palette.text)
)

object MenuEntryCss extends CssClass(
  S.paddingTop.px(10),
  S.paddingLeft.px(20),
  S.fontFamily("Verdana"),
  S.fontSize.px(16),
  S.color(Palette.primary)
)


