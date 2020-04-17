package blended.ui.components.reacttable

import blended.material.ui.MaterialUI._
import com.github.ahnfelt.react4s._

import scala.reflect.ClassTag

/**
  * The trait ReactTable helps to define a component that displays a sequence of items of a given datatype.
  * To achieve this, the trait uses an abstract type variable TableData.
  *
  * Users of the ReactTable need to instantiate the trait with a concrete TableData type and should define
  * a TableProperties instance to configure the table.
  */
trait ReactTable[TableData] {

  /**
    * A cell renderer takes an instance of TableData and renders it as a Tag. Each ColumnConfig has a cell renderer to
    * determine the rendered item.
    */
  type CellRenderer[C] = TableData => Node

  /**
    *
    * @param f is an extractor method to extract an element of type C from the TableData instance
    * @param ft is a rendering function to map the extracted element of type C to a Tag
    * @param cTag is an implicit class tag to determine C from f
    * @tparam C Is the type of the element to be extracted from TableData for rendering
    * @return A rendering function that can be applied to an instance of TableData resulting in a Tag
    */
  def cellRenderer[C](f : TableData => C)(ft : C => Node)(implicit cTag : ClassTag[C]) : CellRenderer[C] = { data : TableData =>
     ft(f(data))
  }

  /**
    * A convenience method to define a string renderer.
    */
  def defaultCellRenderer(f: TableData => String) : CellRenderer[String] = cellRenderer(f)(s => E.span(Text(s)))

  /**
    * A convenience method to render eMails as a mailto:-link.
    */
  def eMailRenderer(f : TableData => String) : CellRenderer[String] = cellRenderer(f)(s => E.a(A.href(s"mailto:$s"), Text(s)))

  /**
    * A ColumnConfig defines the display properties for a single column.
    * @param name is the name of the column and also dtermines the header of the column displayed.
    * @param renderer is the cell renderer used for this column.
    * @param width is an optional width. By default all columns have the same width.
    *
    * @see [[CellRenderer]]
    */
  final case class ColumnConfig(
    name : String,
    renderer : CellRenderer[_],
    numeric : Boolean = false,
    width : Option[String] = None
  )

  final case class TableProperties(
    // The configuration of the table columns
    columns: Seq[ColumnConfig] = Seq.empty,
    searchExtractor : TableData => String = { _.toString() },
    keyExtractor : TableData => String = { _.hashCode().toString() }
  )

  /**
    * @param row is the instance of TableData to be displayed in this row
    * @param props are the table properties determining the display parameters
    */
  final case class ReactTableRow(row: P[TableData], props: P[TableProperties]) extends Component[NoEmit] {
    override def render(get: Get): Node = {

      // determine the sequence of Tags to be displayed in this row
      val cells : Seq[Tag] = get(props).columns.map { col =>
        TableCell(J("numeric", col.numeric), col.renderer(get(row)))
      }

      // bundle the entire row into a div with the appropriate style
      TableRow(
        Tags(cells)
      )
    }
  }

  /**
    * Defines the table header
    * @param props are the table properties defining the table display
    */
  final case class ReactTableHeader(
    props: P[TableProperties]
  ) extends Component[NoEmit] {

    override def render(get: Get): Node = {
      TableHead(
        TableRow(
          Tags(
            get(props).columns.map { cfg =>
              TableCell(J("numeric", cfg.numeric), Text(cfg.name.capitalize))
            }
          )
        )
      )
    }
  }

  final case class ReactTable(data: P[Seq[TableData]], props: P[TableProperties]) extends Component[NoEmit] {

    override def render(get: Get): Node = {

      val p = get(props)

      Paper(
        Table(
          Component(ReactTableHeader, p),
          Tags(get(data).map { r =>
            Component(ReactTableRow, r, p).withKey(p.keyExtractor(r))
          })
        )
      )
    }
  }
}
