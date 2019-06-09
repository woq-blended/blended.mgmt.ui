package blended.ui.components.reacttree

import blended.material.ui.MatIcons.{AddCircleIcon, RemoveCircleIcon}
import blended.mgmt.ui.theme.Theme
import blended.mgmt.ui.theme.Theme.IconStyles
import blended.ui.material.MaterialUI.{IconButton, Typography}
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
    children : List[TreeNode] = List.empty,
  ) {
    val isLeaf : Boolean = children.isEmpty
  }

  /**
    * A convenience renderer to render node values as Strings.
    */
  val defaultNodeRenderer : NodeRenderer = nd => _ => E.div(
    S.marginTop.auto(),
    Typography(
      J("variant", "headline"),
      J("component", "h2"),
      Text(s"$nd")
    )
  )

  val defaultKeyExtractor : NodeData => String = { _.hashCode().toString() }

  /**
    * A class holding the configuration for the tree, such as a custom renderer and a key
    * extractor.
    */
  case class TreeProperties(
    renderer : NodeRenderer = defaultNodeRenderer,
    keyExtractor : NodeData => String = defaultKeyExtractor
  )

  private case class TreeNodeComponent(data : P[TreeNode], props : P[TreeProperties], level: P[Int])
    extends Component[NoEmit] {

    private[this] val collapsed : State[Boolean] = State(true)

    override def render(get: Get): Node = {

      val toggle : Tag = if (get(data).isLeaf) {
        E.div(S.height.px(24), S.width.px(24))
      } else {
        if (get(collapsed)) {
          IconButton(
            IconStyles,
            AddCircleIcon(
              A.onLeftClick{_ =>
                collapsed.modify(v => !v)
              }
            )
          )
        } else {
          IconButton(
            IconStyles,
            RemoveCircleIcon(
              A.onLeftClick{_ =>
                collapsed.modify(v => !v)
              }
            )
          )
        }
      }

      val renderValue: NodeData => TreeProperties => Node = d => p => E.div(
        S.display("flex"),
        S.flexFlow("row"),
        E.div(
          S.width.pt(Theme.spacingUnit * 2 * get(level)),
          S.height.px(1)
        ),
        Tags(toggle),
        p.renderer(d)(get(level))
      )

      val render: TreeNode => TreeProperties => Node = n => p => {

        val childTags : Seq[Tag] = if (get(collapsed)) {
          Seq.empty
        } else {
          n.children.map { c =>
            Component(TreeNodeComponent, c, p, get(level) + 1).withKey(p.keyExtractor(c.nodeValue))
          }
        }

        E.div(
          renderValue(n.nodeValue)(p),
          Tags(childTags)
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
