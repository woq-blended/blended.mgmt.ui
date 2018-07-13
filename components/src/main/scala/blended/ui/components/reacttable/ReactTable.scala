package blended.ui.components.reacttable

import com.github.ahnfelt.react4s._

import scala.reflect.ClassTag

object ReactTable {

  type CellRenderer[T] = T => Node

  def DefaultCellRenderer[T,C](fn : T => C)(implicit tag : ClassTag[C]) : CellRenderer[C] = { c => E.span(Text(c.toString())) }

  case class ColumnConfig(
    name : String,
    cellRenderer : CellRenderer[String]
  )

  case class TableRow(
    cells: Seq[Option[Any]] = Seq.empty
  )

  case class TableProperties(
    // The table data to be displayed
    data: Seq[TableRow],
    // The configuration of the table columns
    configs: Seq[ReactTable.ColumnConfig] = Seq.empty
  )
}

case class ReactTableHeader(tableData: P[ReactTable.TableRow]) extends Component[NoEmit] {
  override def render(get: Get): Node = {
    E.div(Tags(
      get(tableData).cells.map { c =>
        E.div(E.p(Text(c.map(_.toString()).getOrElse(""))))
      }
    ))
  }
}

case class ReactTable(tableData : P[ReactTable.TableProperties]) extends Component[NoEmit]{

  override def render(get: Get): Node = {
    val row = ReactTable.TableRow(get(tableData).configs.map(c => Some(c.name)))
    Component(ReactTableHeader, row)
  }
}
