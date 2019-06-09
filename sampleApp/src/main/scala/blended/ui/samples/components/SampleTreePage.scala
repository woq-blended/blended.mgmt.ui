package blended.ui.samples.components

import blended.ui.components.reacttree.{JmxTree, JmxTreeHelper}
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import com.github.ahnfelt.react4s._

case class SampleTreePage(state : P[SampleAppState]) extends Component[SampleAppEvent] {

  override def render(get: Get): Node = {

    E.div(
      Tags(
        Component(
          JmxTree.ReactTree,
          JmxTreeHelper.treeModel(get(state).names),
          JmxTree.TreeProperties()
        )
      )
    )
  }
}
