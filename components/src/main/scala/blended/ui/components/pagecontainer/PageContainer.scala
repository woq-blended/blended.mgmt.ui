package blended.ui.components.pagecontainer

import com.github.ahnfelt.react4s._

/**
  * Define a top level component for an application. The application uses the application state
  * as it state and emit Application Events. Concrete implementations must implement those two
  * types.
  *
  * @tparam State The application State Type parameter
  * @tparam Event The Application Event parameter
  */
trait PageContainer[State, Event] {

  final case class PageProperties(
    id : String = pageId,
    name : String =  pageName
  )

  val pageProperties : PageProperties = PageProperties()

  def pageName : String
  def pageId : String = pageName

  def renderPage(state : State, props : PageProperties) : Node = Text(props.name)

}
