package blended.ui.samples.components

import blended.ui.common.Logger
import blended.ui.components.pagecontainer.PageContainer
import blended.ui.components.reacttree._
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import com.github.ahnfelt.react4s._

object SampleTreePage extends PageContainer[SampleAppState, SampleAppEvent] {

  override def pageName: String = "ReactTree"

  private val log : Logger = Logger[SampleTreePage.type]

  override def renderPage(state: SampleAppState, props: SampleTreePage.PageProperties): Node =  {
    E.div(
      Tags(
        Component(
          JmxTree.ReactTree,
          JmxTreeHelper.treeModel(state.names),
          JmxTree.treeConfiguration
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
