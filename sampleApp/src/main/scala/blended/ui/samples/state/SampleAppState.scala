package blended.ui.samples.state

import blended.ui.samples.{HomePage, SamplePage}

case class Person(
  first: String,
  last: String,
  age: Int,
  eMail: String
)

sealed trait SampleAppEvent
final case class PageSelected(p: Option[SamplePage]) extends SampleAppEvent

object SampleAppState {

  def redux(event: SampleAppEvent)(old: SampleAppState) : SampleAppState = event match {
    case PageSelected(p) =>
      old.copy(currentPage = p)
  }
}

case class SampleAppState(
  currentPage : Option[SamplePage] = Some(HomePage),
  persons : Seq[Person] = Seq(
    // scalastyle:off magic.number
    Person("Andreas", "Gies", 50, "andreas@wayofquality.de"),
    Person("Karin", "Gies", 52, "kgies@godea-life.de"),
    Person("Tatjana", "Gies", 28, "gies_tat@yahoo.com"),
    Person("Sabrina", "Gies", 24, "sabrina@godea-life.de")
    // scalastyle:on magic.number
  )
)
