package blended.ui.samples.components

import blended.ui.components.pagecontainer.PageContainer
import blended.ui.samples.state.{SampleAppEvent, SampleAppState}
import com.github.ahnfelt.react4s._

object HomePage extends PageContainer[SampleAppState, SampleAppEvent] {

  override def pageName: String = "ReactTable"

  override def renderPage(state: SampleAppState, props: HomePage.PageProperties): Node = E.div(
    Tags(
      Component(PersonTable.ReactTable, state.persons, PersonTable.props)
    )
  )
}
