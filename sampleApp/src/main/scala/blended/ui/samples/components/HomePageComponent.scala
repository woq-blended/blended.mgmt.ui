package blended.ui.samples.components

import blended.material.ui.MatIcons._
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import com.github.ahnfelt.react4s._

case class HomePageComponent(state: P[SampleAppState]) extends Component[SampleAppEvent] {

  override def render(get: Get): Node = {
    E.div(
      Tags(
        AddCircleIcon(),
        RemoveCircleIcon(),
        Component(PersonTable.ReactTable, get(state).persons, PersonTable.props)
      )
    )
  }
}
