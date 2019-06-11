package blended.ui.components.pagecontainer

import scala.collection.mutable

trait PageRegistry[State, Event] {

  private val pages : mutable.Map[String, PageContainer[State, Event]] = mutable.Map.empty

  def registerPage(p : PageContainer[State, Event]) : Option[PageContainer[State, Event]] = {
    pages.put(p.pageId, p)
  }

  def pageFromId(id : String) : Option[PageContainer[State, Event]] = pages.get(id)

  def listPages : Map[String, String] = pages.mapValues(_.pageName).toMap
}
