package blended.ui.samples

import blended.ui.router.Router
import blended.ui.samples.compoments._
import blended.ui.samples.state.SampleAppState
import com.github.ahnfelt.react4s._

sealed trait SamplePage
final object HomePage extends SamplePage

object Routes {

  val path : Router[SamplePage] = new Router[SamplePage]

  val routes : Router.Tree[SamplePage,SamplePage] = path(
    HomePage
  )
}

object TopLevelPageResolver {

  def topLevelPage(p: Option[SamplePage], state: SampleAppState) : Node = {
    p match {
      case Some(p) => p match {
        case HomePage => Component(HomePageComponent, state)
      }
      case None => E.div(E.p(Text("Not found")))
    }
  }
}
