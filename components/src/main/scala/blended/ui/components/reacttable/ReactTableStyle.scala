package blended.ui.components.reacttable

import blended.ui.themes.{BlendedDefaultPalette, BlendedPalette}
import com.github.ahnfelt.react4s._

trait ReactTableStyle {

  val palette : BlendedPalette = new BlendedDefaultPalette {}
  val hoverRow : String = "rgba(244,244,244,0.77) "

  def reactTable : CssClass
  def reactTableHeader : CssClass
  def reactTableRow : CssClass
}

trait DefaultReactTableStyle extends ReactTableStyle {

  override def reactTable: CssClass = ReactTableCss
  override def reactTableHeader: CssClass = ReactTableHeader
  override def reactTableRow: CssClass = ReactTableRow

  def reactTableRowBase : CssClass = ReactTableRowBase

  private[this] object ReactTableRowBase extends CssClass (
    S.display("flex"),
    S.flexDirection("row"),
    S.verticalAlign("middle"),
    S.padding.px(8),
    S.margin.px(5)
  )

  private[this] object ReactTableHeader extends CssClass (
    reactTableRowBase,
    S.boxShadow("0 1px 3px " + palette.shadow),
    S.backgroundColor(palette.primary),
    S.color("white"),
    S.fontWeight("bold")
  )

  private[this] object ReactTableRow extends CssClass(
    reactTableRowBase,
    S.borderBottom(s"solid 1px ${palette.primary}"),
    Css.hover(
      S.backgroundColor(hoverRow)
    )
  )

  private[this] object ReactTableCss extends CssClass(
    S.display("flex"),
    S.flexDirection("column")
  )
}