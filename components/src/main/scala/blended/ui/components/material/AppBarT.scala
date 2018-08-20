package blended.ui.components.material

import blended.ui.material.MaterialUI
import com.github.ahnfelt.react4s._

class AppBarT[AS](title : AS => String)(implicit styles : MaterialStyles) {

  case class AppBarC(s : P[AS]) extends Component[NoEmit] {

    override def render(get: Get): Node = {

      MaterialUI.AppBar(
        styles.AppBarStyles,
        MaterialUI.Toolbar(
          MaterialUI.Typography(
            J("variant", "title"),
            J("color", "inherit"),
            Text(title(get(s)))
          )
        )
      )
    }
  }
}
