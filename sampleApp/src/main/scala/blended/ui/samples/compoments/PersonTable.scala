package blended.ui.samples.compoments

import blended.ui.components.reacttable.ReactTable
import blended.ui.samples.state.Person

object PersonTable extends ReactTable[Person] {

  val props = TableProperties(
    columns = Seq(
      ColumnConfig(name = "first", renderer = defaultCellRenderer(_.first)),
      ColumnConfig(name = "last", renderer = defaultCellRenderer(_.last)),
      ColumnConfig(name = "age", renderer = defaultCellRenderer(_.age.toString), width = Some("15%")),
      ColumnConfig(name = "eMail", renderer = eMailRenderer(_.eMail))
    )
  )
}
