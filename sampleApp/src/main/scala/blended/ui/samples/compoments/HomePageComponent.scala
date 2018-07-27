package blended.ui.samples.compoments

import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import blended.ui.samples.theme.BlendedSamplesTableStyle
import com.github.ahnfelt.react4s._

case class HomePageComponent(state: P[SampleAppState]) extends Component[SampleAppEvent] {

  override def render(get: Get): Node = {
    E.div(
      Tags(
        Component(PersonTable.ReactTable, get(state).persons, PersonTable.props, BlendedSamplesTableStyle)
      )
    )
  }
}
