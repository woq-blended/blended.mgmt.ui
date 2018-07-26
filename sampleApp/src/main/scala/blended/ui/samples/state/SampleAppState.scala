package blended.ui.samples.state

sealed trait SampleAppEvent

case class Person(
  first: String,
  last: String,
  age: Int,
  eMail: String
)

case class SampleAppState(
  persons : Seq[Person] = Seq(
    // scalastyle:off magic.number
    Person("Andreas", "Gies", 50, "andreas@wayofquality.de"),
    Person("Karin", "Gies", 52, "kgies@godea-life.de"),
    Person("Tatjana", "Gies", 28, "gies_tat@yahoo.com"),
    Person("Sabrina", "Gies", 24, "sabrina@godea-life.de")
    // scalastyle:on magic.number
  )
)
