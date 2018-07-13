package blended.mgmt.app

import blended.ui.components.reacttable.ReactTable
import blended.ui.components.reacttable.ReactTable.{ColumnConfig, TableRow}
import com.github.ahnfelt.react4s._

case class Person(
  first: String,
  last: String,
  age: Int,
  eMail: String
)

object SamplesLoader {

  def tableRow(p: Person, cols: Seq[ColumnConfig]) : TableRow = {
    TableRow(cols.map { c =>
      c.name match {
        case "first" => Some(p.first)
        case "last" => Some(p.last)
        case "age" => Some(p.age)
        case "eMail" => Some(p.eMail)
        case _ => None
      }
    })
  }

  def main(args: Array[String]) : Unit = {

    val persons :Seq[Person] = Seq(
      Person("Andreas", "Gies", 50, "andreas@wayofquality.de")
    )

    val cols = Seq(
      ReactTable.ColumnConfig(
        name = "first",
        cellRenderer = ReactTable.DefaultCellRenderer[Person, String]{ p => p.first }
      ),
      ReactTable.ColumnConfig(
        name = "last",
        cellRenderer = ReactTable.DefaultCellRenderer[Person, String]{ p => p.last }
      )
    )

    val props : ReactTable.TableProperties = ReactTable.TableProperties(
      data = persons.map(p => tableRow(p, cols)),
      configs = cols
    )

    val main = Component(ReactTable, props)
    ReactBridge.renderToDomById(main, "content")
  }
}