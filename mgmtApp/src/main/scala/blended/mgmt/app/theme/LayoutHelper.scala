package blended.mgmt.app.theme

import com.github.ahnfelt.react4s._

object LayoutHelper {

  /**
    * A convenience method to create and style a panel with an optional heading and custom content.
    * @param panelHeading an optional panel heading, will only be rendered if defined
    * @param content the content to be displayed in the panel body
    * @return the node containing the complete panel
    */
  def contentPanel(
    panelHeading : Option[String] = None
  )(content: Node) : Node = {

    E.div(
      E.div(E.h2(Text(panelHeading.mkString))).when(panelHeading.isDefined),
      E.div(content)
    )
  }
}
