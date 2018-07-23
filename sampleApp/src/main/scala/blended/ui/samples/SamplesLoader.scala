package blended.mgmt.app

import blended.ui.components.reacttable.ReactTable
import blended.ui.samples.theme.BlendedSamplesTableStyle
import com.github.ahnfelt.react4s._

case class Person(
  first: String,
  last: String,
  age: Int,
  eMail: String
)

object PersonTable extends ReactTable {

  override type TableData = Person

  val props = TableProperties(
    configs = Seq(
      ColumnConfig(name = "first", renderer = defaultCellRenderer(_.first)),
      ColumnConfig(name = "last", renderer = defaultCellRenderer(_.last))
    )
  )
}

object SamplesLoader {

  def main(args: Array[String]) : Unit = {

    val persons :Seq[PersonTable.TableData] = Seq(
      Person("Andreas", "Gies", 50, "andreas@wayofquality.de"),
      Person("Karin", "Gies", 52, "kgies@godea-life.de"),
      Person("Tatjana", "Gies", 28, "gies_tat@yahoo.com"),
      Person("Sabrina", "Gies", 24, "sabrina@godea-life.de")
    )

    val main = Component(PersonTable.ReactTable, persons, PersonTable.props, BlendedSamplesTableStyle)

    ReactBridge.renderToDomById(main, "content")
  }
}