package blended.ui.components.reacttree

import blended.mgmt.ui.theme.Theme
import com.github.ahnfelt.react4s._

object TreeStyle {

  private val indent : Int = Theme.spacingUnit * 2

  val indentStyle : Int => Seq[Tag] = level => Seq(
    S.width.pt(indent * level),
    S.height.px(1)
  )

  object NodeLabelDivStyle extends CssClass(
    S.marginTop.auto(),
    S.flex("1")
  )

  object NodeSelectedStyle extends CssClass(
    S.background(Theme.background)
  )

  object NodeLabelTextStyle {
    def apply() : Seq[JsProp] = Seq(
      J("variant", "body1")
    )
  }
}
