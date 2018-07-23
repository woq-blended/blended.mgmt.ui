package blended.ui.themes

import com.github.ahnfelt.react4s.{Css, CssClass, S}

trait SidebarMenuTheme {

  val palette : BlendedPalette = new BlendedDefaultPalette{}

  def topBarCss : CssClass
  def bottomBarCss : CssClass
  def brandTextCss : CssClass
  def brandTitleCss : CssClass
  def linkCss : CssClass
  def columnCss : CssClass
  def columnContainerCss : CssClass
  def contentColumnCss : CssClass
  def menuColumnCss : CssClass
  def menuCategoryCss : CssClass
  def menuEntryCss : CssClass
}

trait DefaultSideBarMenuTheme extends SidebarMenuTheme {

  override def topBarCss: CssClass = TopBarCss
  override def bottomBarCss: CssClass = BottomBarCss
  override def brandTextCss: CssClass = BrandTextCss
  override def brandTitleCss: CssClass = BrandTitleCss
  override def linkCss: CssClass = LinkCss
  override def columnCss: CssClass = ColumnCss
  override def columnContainerCss: CssClass = ColumnContainerCss
  override def contentColumnCss: CssClass = ContentColumnCss
  override def menuColumnCss: CssClass = MenuColumnCss
  override def menuCategoryCss: CssClass = MenuCategoryCss
  override def menuEntryCss: CssClass = MenuEntryCss

  private[this] object TopBarCss extends CssClass(
    S.borderTop("2px solid " + palette.primary),
    S.backgroundColor(palette.background),
    S.boxShadow("0 2px 5px rgba(0, 0, 0, 0.3)"),
    S.boxSizing.borderBox(),
    S.position.absolute(),
    S.left.px(0),
    S.top.px(0),
    S.right.px(0),
    S.height.px(50)
  )

  private[this] object BottomBarCss extends CssClass(
    S.backgroundColor(palette.background),
    S.boxShadow("0 2px 5px rgba(0, 0, 0, 0.3)"),
    S.boxSizing.borderBox(),
    S.position.absolute(),
    S.left.px(0),
    S.bottom.px(0),
    S.right.px(0),
    S.height.px(50)
  )


  private[this] object BrandTextCss extends CssClass(
    S.display.inlineBlock(),
    S.paddingTop.px(8),
    S.fontFamily("Verdana")
  )

  private[this] object BrandTitleCss extends CssClass(
    brandTextCss,
    S.color(palette.primary),
    S.paddingLeft.px(50),
    S.fontSize.px(20),
  )

  private[this] object LinkCss extends CssClass(
    S.color(palette.primary),
    S.textDecoration.none(),
    S.cursor.pointer(),
    Css.hover(
      S.textDecoration("underline")
    )
  )

  private[this] object ColumnCss extends CssClass(
    S.position.absolute(),
    S.boxSizing.borderBox(),
    S.top.px(0),
    S.bottom.px(0)
  )

  private[this] object ColumnContainerCss extends CssClass(
    S.position.absolute(),
    S.top.px(50),
    S.bottom.px(50),
    S.left.px(0),
    S.right.px(0),
  )

  private[this] object ContentColumnCss extends CssClass(
    ColumnCss,
    S.left.px(200),
    S.right.px(0)
  )

  private[this] object MenuColumnCss extends CssClass(
    ColumnCss,
    S.left.px(0),
    S.width.px(200)
  )

  private[this] object MenuCategoryCss extends CssClass(
    S.paddingTop.px(20),
    S.paddingLeft.px(20),
    S.textTransform("uppercase"),
    S.fontFamily("Verdana"),
    S.fontSize.px(14),
    S.color(palette.text)
  )

  private[this] object MenuEntryCss extends CssClass(
    S.paddingTop.px(10),
    S.paddingLeft.px(20),
    S.fontFamily("Verdana"),
    S.fontSize.px(16),
    S.color(palette.primary)
  )
}
