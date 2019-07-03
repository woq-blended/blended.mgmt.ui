package blended.ui.components.reacttree

import blended.material.ui.MatIcons.{AddCircleIcon, RemoveCircleIcon}
import blended.material.ui.Styles._
import blended.mgmt.ui.theme.Theme.IconStyles
import blended.ui.components.reacttree.TreeStyle._
import blended.ui.material.MaterialUI.{IconButton, Paper, Typography}
import com.github.ahnfelt.react4s._

trait ReactTree[NodeData] {

  sealed trait TreeEvent
  case class NodeSelected(
    node : NodeData
  ) extends TreeEvent

  /**
    * A node renderer takes an instance of [[NodeData]] and the level of the node and renders it as a Tag.
    */
  type NodeRenderer = NodeData => Int => Boolean => Node

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

  val nodeLabel : NodeData => String = nd => s"$nd"

  /**
    * A convenience renderer to render node values as Strings.
    */
  val defaultNodeRenderer : NodeRenderer = nd => _ => selected =>
    Paper(
      NodeLabelDivStyle,
      NodeSelectedStyle.when(selected),
      NodeHoverStyle,
      withStyles(NodeLabelTextStyle())(Typography(
        Text(s"${nodeLabel(nd)}")
      ))
    )

  val defaultKeyExtractor : NodeData => String = { _.hashCode().toString() }

  /**
    * A class holding the configuration for the tree, such as a custom renderer and a key
    * extractor.
    */
  final case class TreeProperties(
    renderer : NodeRenderer = defaultNodeRenderer,
    keyExtractor : NodeData => String = defaultKeyExtractor,
    showRootNode : Boolean = true
  )

  private final case class TreeNodeComponent(
    data : P[TreeNode],
    selected : P[Option[NodeData]],
    level: P[Int],
    props : P[TreeProperties]
  ) extends Component[TreeEvent] {

    private[this] val initialized : State[Boolean] = State(false)
    private[this] val collapsed : State[Boolean] = State(true)

    override def componentWillRender(get: Get): Unit = if (!get(initialized)) {
      collapsed.set(get(level) > 0)
      initialized.set(true)
    }

    private def childTags(get : Get) : Seq[Tag] = if (get(level) > 0 && get(collapsed)) {
      Seq.empty
    } else {
      val p = get(props)
      get(data).children.map { c =>
        Component(TreeNodeComponent, c, get(selected), get(level) + 1, p)
          .withKey(p.keyExtractor(c.nodeValue))
          .withHandler{ e => emit(e) }
      }
    }

    private def toggleIcon(get : Get) : Tag = {
      if (get(data).isLeaf) {
        E.div(S.height.px(24), S.width.px(24))
      } else {
        if (get(collapsed)) {
          IconButton(
            IconStyles,
            AddCircleIcon( A.onLeftClick{_ => collapsed.modify(v => !v) } )
          )
        } else {
          IconButton(
            IconStyles,
            RemoveCircleIcon( A.onLeftClick{_ => collapsed.modify(v => !v) } )
          )
        }
      }
    }

    override def render(get: Get): Node = {

      val node = get(data)
      val isSelected : Boolean = get(selected) match {
        case None => false
        case Some(s) => s.equals(node.nodeValue)
      }

      val l : Int = get(level)

      val renderValue: NodeData => TreeProperties => Node = d => p => {

        val indent : Int = if (l > 0 && !p.showRootNode) {
          l - 1
        } else {
          l
        }

        E.div(
          S.display("flex"), S.flexFlow("row"),
          E.div(indentStyle(indent)).when(l > 0),
          toggleIcon(get),
          withChildren(
            A.onClick { _ =>
              emit(NodeSelected(d))
            }
          )(p.renderer(d)(get(level))(isSelected))
        )
      }

      val render: TreeNode => TreeProperties => Node = n => p => {

        E.div(
          renderValue(n.nodeValue)(p).when(l > 0 || p.showRootNode),
          Tags(childTags(get))
        )
      }

      render(node)(get(props))
    }
  }

  case class ReactTree(data : P[TreeNode], props : P[TreeProperties]) extends Component[TreeEvent] {

    private val selectedNode : State[Option[NodeData]] = State(None)

    override def render(get: Get): Node = E.div(
      Component(TreeNodeComponent, get(data), get(selectedNode), 0, get(props))
        .withKey(get(props).keyExtractor(get(data).nodeValue))
        .withHandler {
          case s: NodeSelected =>
            selectedNode.set(Some(s.node))
            emit(s)
        }
    )
  }
}
