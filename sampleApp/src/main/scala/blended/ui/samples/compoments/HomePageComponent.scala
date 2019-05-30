package blended.ui.samples.compoments

import blended.material.ui.Styles
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import com.github.ahnfelt.react4s._

import scala.scalajs.js.JSConverters._
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("@material-ui/icons/AddCircle", JSImport.Default)
object AddCircle extends js.Object

trait RichMatIcon{

  def createIcon(componentClass: Any, clazzes : Map[String, CssClass]) : JsComponentConstructor = {
    val effectiveChildren : Seq[JsTag]= if (clazzes.nonEmpty) {
      Seq(J("classes", clazzes.map( c => c._1 -> c._2.name).toJSDictionary))
    } else {
      Seq.empty
    }

    JsComponentConstructor(componentClass, effectiveChildren, None, None)
  }
}

object AddCircleIcon extends RichMatIcon {
  def apply() =
    Styles.withStyles(S.color("#ff0000"))(createIcon(AddCircle, Map.empty))
}

case class HomePageComponent(state: P[SampleAppState]) extends Component[SampleAppEvent] {

  override def render(get: Get): Node = {
    E.div(
      Tags(
        AddCircleIcon(),
        Component(PersonTable.ReactTable, get(state).persons, PersonTable.props)
      )
    )
  }
}
