package blended.ui.components.reacttable

import blended.ui.themes.{BlendedDefaultPalette, BlendedPalette}
import com.github.ahnfelt.react4s._

trait ReactTableStyle {

  val palette : BlendedPalette = new BlendedDefaultPalette {}
  val hoverRow : String = "rgba(244,244,244,0.77) "

  def reactTable : CssClass
  def reactTableRow : CssClass
}

trait DefaultReactTableStyle extends ReactTableStyle {

  override def reactTableRow: CssClass = ReactTableRow

  override def reactTable: CssClass = ReactTableCss

  private[this] object ReactTableRow extends CssClass(
    S.display("flex"),
    S.flexDirection("row"),
    S.verticalAlign("middle"),
    S.padding.rem(0.8),
    S.boxShadow("0 1px 3px " + palette.shadow),
    S.margin.px(5),
    Css.hover(
      S.backgroundColor(hoverRow)
    )
  )

  private[this] object ReactTableCss extends CssClass(
    S.display("flex"),
    S.flexDirection("column")
  )
}