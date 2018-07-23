package blended.ui.components.reacttable

import com.github.ahnfelt.react4s._

import scala.reflect.ClassTag

trait ReactTable {

  type TableData
  type CellRenderer[C] = (TableData => Tag)

  def cellRenderer[C](f : TableData => C)(ft : C => Tag)(implicit cTag : ClassTag[C]) : CellRenderer[C] = { data : TableData =>
     ft(f(data))
  }

  def defaultCellRenderer(f: TableData => String) : CellRenderer[String] = cellRenderer(f)(s => E.span(Text(s)))

  def eMailRenderer(f : TableData => String) : CellRenderer[String] = cellRenderer(f)(s => E.a(A.href(s"mailto:$s"), Text(s)))

  case class ColumnConfig(
    name : String,
    renderer : CellRenderer[_],
    width : Option[String] = None
  )

  case class TableProperties(
    // The configuration of the table columns
    configs: Seq[ColumnConfig] = Seq.empty,
    searchExtractor : TableData => String = { _.toString() },
    keyExtractor : TableData => String = { _.hashCode().toString() }
  )

  case class ReactTableRow(row: P[TableData], props: P[TableProperties], style: P[ReactTableStyle]) extends Component[NoEmit] {
    override def render(get: Get): Node = {

      val cells : Seq[Tag] = get(props).configs.map { cfg =>
        E.div(S.flex(cfg.width.map(w => s"0 1 $w").getOrElse("1")), cfg.renderer(get(row)))
      }

      E.div(
        get(style).reactTableRow,
        Tags(cells)
      )
    }
  }

  case class ReactTableHeader(
    props: P[TableProperties],
    style : P[ReactTableStyle]
  ) extends Component[NoEmit] {

    override def render(get: Get): Node = {
      E.div(
        get(style).reactTableHeader,
        Tags(
          get(props).configs.map { cfg =>
            E.span(S.flex(cfg.width.map(w => s"0 1 $w").getOrElse("1")), Text(cfg.name.capitalize))
          }
        )
      )
    }
  }

  case class ReactTable(data: P[Seq[TableData]], props: P[TableProperties], style: P[ReactTableStyle]) extends Component[NoEmit] {

    override def render(get: Get): Node = {

      val p = get(props)
      val s = get(style)

      E.div(
        Component(ReactTableHeader, p, s),
        Tags(get(data).map { r =>
          Component(ReactTableRow, r, p, s).withKey(p.keyExtractor(r))
        })
      )
    }
  }
}
