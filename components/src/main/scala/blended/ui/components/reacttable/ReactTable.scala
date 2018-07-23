package blended.ui.components.reacttable

import blended.ui.components.reacttable.ReactTable.ColumnConfig
import com.github.ahnfelt.react4s._

object ReactTable {

  def createTable[T](
    data  : Seq[T],
    props : TableProperties,
    style : ReactTableStyle
  )(
    extract : (T, ColumnConfig) => Option[Any]
  ) = {

    val rows = data.map{ d =>
      val cells = props.configs.map(c => extract(d,c)).map(_.getOrElse(""))
      TableRow(cells)
    }

    Component(ReactTable, rows, props, style)
  }

  type CellRenderer = Any => Node

  def DefaultCellRenderer(fn : Any => String) : CellRenderer = { data: Any =>
    E.span(Text(fn(data)))
  }

  case class ColumnConfig(
    name : String,
    cellRenderer : CellRenderer = DefaultCellRenderer(_.toString)
  )

  case class TableRow(cells: Seq[Any])

  case class TableProperties(
    // The configuration of the table columns
    configs: Seq[ReactTable.ColumnConfig] = Seq.empty
  )

}

case class ReactTableHeader(
  tableData: P[Seq[ReactTable.ColumnConfig]],
  style : P[ReactTableStyle]
) extends Component[NoEmit] {

  override def render(get: Get): Node = {
    E.div(
      get(style).reactTableRow,
      Tags(
        get(tableData).map { c =>
          E.span(Text(c.name))
        }
      )
    )
  }
}

case class ReactTableRow(
  data: P[ReactTable.TableRow], configs: P[Seq[ColumnConfig]]
) extends Component[NoEmit] {
  override def render(get: Get): Node = {
    E.div(Tags(get(data).cells.map(ReactTable.DefaultCellRenderer(_.toString))))
  }
}

case class ReactTable(
  data  : P[Seq[ReactTable.TableRow]],
  props : P[ReactTable.TableProperties],
  style : P[ReactTableStyle]
) extends Component[NoEmit]{

  override def render(get: Get): Node = {

    val configs = get(props).configs

    val rows : Tag = Tags(get(data).map { r =>
      Component(ReactTableRow, r, configs)
    })

    E.div(
      Component(ReactTableHeader, configs, get(style)),
      rows
    )
  }
}
