package blended.ui.samples.compoments

import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import com.github.ahnfelt.react4s._

import scala.util.Random

case class SampleTreePage(state : P[SampleAppState]) extends Component[SampleAppEvent] {

  private val rnd = new Random()

  override def render(get: Get): Node = {

    def node(v : String, depth : Int) : SampleTree.TreeNode = {

      val children : List[SampleTree.TreeNode] = if (depth > 0) {
        1.to(rnd.nextInt(3) + 2).map{ i =>
          node(s"$v - $i", depth - 1)
        }.toList
      } else {
        List.empty
      }

      SampleTree.TreeNode(v, children)
    }

    E.div(
      Tags(
        Component(SampleTree.ReactTree, node("Root", 3), SampleTree.TreeProperties())
      )
    )
  }
}
