package blended.mgmt.app

import blended.ui.components.reacttable.ReactTable
import blended.ui.components.reacttable.ReactTable._
import com.github.ahnfelt.react4s._

case class Person(
  first: String,
  last: String,
  age: Int,
  eMail: String
)

object SamplesLoader {

  def main(args: Array[String]) : Unit = {

    val persons :Seq[Person] = Seq(
      Person("Andreas", "Gies", 50, "andreas@wayofquality.de"),
      Person("Karin", "Gies", 52, "kgies@godea-life.de"),
      Person("Tatjana", "Gies", 28, "gies_tat@yahoo.com")
    )

    val props = TableProperties(
      configs = Seq(
        ColumnConfig(name = "first"),
        ColumnConfig(name = "last"),
        ColumnConfig(name = "age")
      )
    )

    val main = ReactTable.createTable[Person](persons, props){ (p, c) =>
      c.name match {
        case "first" => Some(p.first)
        case "last" => Some(p.last)
        case "age" => Some(p.age)
        case _ => None
      }
    }

    ReactBridge.renderToDomById(main, "content")
  }
}