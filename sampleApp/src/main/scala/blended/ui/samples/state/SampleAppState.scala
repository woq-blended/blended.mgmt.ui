package blended.ui.samples.state

import blended.ui.samples.components.SampleTree
import blended.ui.samples.{HomePage, SamplePage}

import scala.util.Random

case class Person(
  first: String,
  last: String,
  age: Int,
  eMail: String
)

sealed trait SampleAppEvent
final case class PageSelected(p: Option[SamplePage]) extends SampleAppEvent
final case object RefreshTree extends SampleAppEvent

object SampleAppState {

  def redux(event: SampleAppEvent)(old: SampleAppState) : SampleAppState = event match {
    case PageSelected(p) =>
      old.copy(currentPage = p)

    case RefreshTree =>
      old.copy(tree = sampleTree("Root", 3))
  }

  private val rnd = new Random()

  private def sampleTree(v : String, depth : Int) : SampleTree.TreeNode = {

    val children : List[SampleTree.TreeNode] = if (depth > 0) {
      1.to(rnd.nextInt(3) + 2).map{ i =>
        sampleTree(s"$v - $i", depth - 1)
      }.toList
    } else {
      List.empty
    }

    SampleTree.TreeNode(v, children)
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
  ),

  tree : SampleTree.TreeNode = SampleAppState.sampleTree("Root", 3)
)