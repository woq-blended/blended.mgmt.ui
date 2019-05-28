package blended.ui.samples.compoments

import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import com.github.ahnfelt.react4s._

case class SampleTreePage(state : P[SampleAppState]) extends Component[SampleAppEvent] {

  override def render(get: Get): Node = {

    val root : SampleTree.TreeNode = SampleTree.TreeNode("Hallo Andreas")

    E.div(
      Tags(
        Component(SampleTree.ReactTree, root, SampleTree.TreeProperties())
      )
    )
  }
}
