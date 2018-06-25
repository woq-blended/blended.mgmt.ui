package blended.mgmt.app

import com.github.ahnfelt.react4s._

case class Main() extends Component[NoEmit] {

  // A web sockets handler decoding container Info's

  override def render(get: Get): Element = {
    E.div(E.h1(Text("Hello Andreas!")))
  }
}
