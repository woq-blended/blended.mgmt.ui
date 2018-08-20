package blended.ui.components

import blended.ui.material.MaterialUI
import com.github.ahnfelt.react4s._

import scala.scalajs.js

trait AppBar[AS] extends {

  val theme : js.Dynamic

  object AppBarStyles extends CssClass (
    S.position.fixed(),
    S.zIndex.number(theme.zIndex.drawer.asInstanceOf[Int] + 1)
  )

  val styles : Option[CssClass] = None

  case class AppBarComponent(s : P[AS]) extends Component[NoEmit] {

    override def render(get: Get): Node = {

      val children : Seq[JsTag] = styles.toSeq ++ Seq(
        MaterialUI.Toolbar(
          MaterialUI.Typography(
            J("variant", "title"), J("color", "inherit"),
            Text("Blended Component Samples")
          )
        )
      )

      MaterialUI.AppBar(children:_*)
    }
  }
}
