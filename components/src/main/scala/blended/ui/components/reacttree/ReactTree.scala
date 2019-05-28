package blended.ui.components.reacttree

import com.github.ahnfelt.react4s._

trait ReactTree[NodeData] {

  /**
    * A node renderer takes an instance of [[NodeData]] and renders it as a Tag.
    */
  type NodeRenderer = NodeData => Int => Node

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
  val defaultNodeRenderer : NodeRenderer = nd => level => E.div(
    Text(s"$level - $nd")
  )

  /**
    * A class holding the configuration for the tree, such as a custom renderer and a key
    * extractor.
    */
  case class TreeProperties(
    renderer : NodeRenderer = defaultNodeRenderer,
    keyExtractor : NodeData => String = {_.hashCode().toString() }
  )

  private case class TreeNodeComponent(data : P[TreeNode], props : P[TreeProperties], level: P[Int])
    extends Component[NoEmit] {

    override def render(get: Get): Node = {

      val renderValue: NodeData => TreeProperties => Node = d => p => E.div(
        p.renderer(d)(get(level))
      )

      val render: TreeNode => TreeProperties => Node = n => p => {
        E.div(
          renderValue(n.nodeValue)(p),
          Tags(
            n.children.map { c =>
              Component(TreeNodeComponent, c, p, get(level) + 1).withKey(p.keyExtractor(c.nodeValue))
            }
          )
        )
      }

      render(get(data))(get(props))
    }
  }

  case class ReactTree(data : P[TreeNode], props : P[TreeProperties]) extends Component[NoEmit] {
    override def render(get: Get): Node = E.div(
      Component(TreeNodeComponent, get(data), get(props), 0).withKey(get(props).keyExtractor(get(data).nodeValue))
    )
  }
}
