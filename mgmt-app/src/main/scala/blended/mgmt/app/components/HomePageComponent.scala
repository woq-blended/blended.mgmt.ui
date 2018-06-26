package blended.mgmt.app.components

import com.github.ahnfelt.react4s._

case class HomePageComponent() extends Component[NoEmit] {

  override def render(get: Get): Node = E.div(
    E.h1(Text("Home"))
  )
}
