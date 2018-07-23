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

  case class ColumnConfig(
    name : String,
    renderer : CellRenderer[_]
  )

  case class TableProperties(
    // The configuration of the table columns
    configs: Seq[ColumnConfig] = Seq.empty
  )

  case class ReactTableRow(row: P[TableData], props: P[TableProperties]) extends Component[NoEmit] {
    override def render(get: Get): Node = {

      val cells : Seq[Tag] = get(props).configs.map { cfg =>
        cfg.renderer(get(row))
      }

      E.div(cells:_*)
    }
  }

  case class ReactTableHeader(
    props: P[TableProperties],
    style : P[ReactTableStyle]
  ) extends Component[NoEmit] {

    override def render(get: Get): Node = {
      E.div(
        get(style).reactTableRow,
        Tags(
          get(props).configs.map { c =>
            E.span(Text(c.name.capitalize))
          }
        )
      )
    }
  }

  case class ReactTable(data: P[Seq[TableData]], props: P[TableProperties], style: P[ReactTableStyle]) extends Component[NoEmit] {

    override def render(get: Get): Node = {
      E.div(
        Component(ReactTableHeader, get(props), get(style)),
        Tags(get(data).map(r => Component(ReactTableRow, r, get(props))))
      )
    }
  }
}
