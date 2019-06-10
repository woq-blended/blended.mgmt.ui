package blended.ui.samples.components

import blended.ui.common.Logger
import blended.ui.components.reacttree._
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import com.github.ahnfelt.react4s._

case class SampleTreePage(state : P[SampleAppState]) extends Component[SampleAppEvent] {

  private val log : Logger = Logger[SampleTreePage]

  override def render(get: Get): Node = {

    E.div(
      Tags(
        Component(
          JmxTree.ReactTree,
          JmxTreeHelper.treeModel(get(state).names),
          JmxTree.TreeProperties()
        ).withHandler{
          case JmxTree.NodeSelected(node) =>
            val objName = node match {
              case RootNode => "/"
              case DomainNode(domain) => domain
              case ObjNameNode(name, _) => name.toString
            }

            log.info(s"Selected node [$objName]")
        }
      )
    )
  }
}
