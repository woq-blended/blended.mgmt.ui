package blended.ui.components.reacttable

import com.github.ahnfelt.react4s._

case class ReactTable() extends Component[NoEmit]{

  override def render(get: Get): Node = {
    E.div(E.p(Text("MyTable")))
  }
}
