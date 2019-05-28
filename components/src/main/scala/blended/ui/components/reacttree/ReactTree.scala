package blended.ui.components.reacttree

import com.github.ahnfelt.react4s._

trait ReactTree[NodeData] {

  /**
    * A node renderer takes an instance of [[NodeData]] and renders it as a Tag.
    */
  type NodeRenderer = NodeData => Node

  object TreeNode {
    def apply(
      nodeValue: NodeData,
      children: NodeData*
    ): TreeNode = new TreeNode(
      nodeValue,
      children.map(c => TreeNode(c)).toList
    )
  }

  case class TreeNode(
    nodeValue : NodeData,
    children : List[TreeNode] = List.empty
  )

  /**
    * A convenience renderer to render node values as Strings.
    */
  val defaultNodeRenderer : NodeRenderer = nd => E.span(Text(nd.toString))

  /**
    * A class holding the configuration for the tree, such as a custom renderer and a key
    * extractor.
    */
  case class TreeProperties(
    renderer : NodeRenderer = defaultNodeRenderer,
    keyExtractor : NodeData => String = {_.hashCode().toString() }
  )

  trait NodeComponent {
    val renderValue  : NodeData => TreeProperties => Node = d => p => E.div(
      p.renderer(d)
    ).withKey(p.keyExtractor(d))

    val render : TreeNode => TreeProperties => Node = n => p => renderValue(n.nodeValue)(p)
  }

  case class ReactTree(data : P[TreeNode], props : P[TreeProperties]) extends Component[NoEmit]
    with NodeComponent {

    override def render(get: Get): Node = render(get(data))(get(props))
  }
}
