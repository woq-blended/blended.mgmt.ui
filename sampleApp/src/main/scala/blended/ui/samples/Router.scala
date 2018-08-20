package blended.ui.samples

import blended.ui.material.MaterialUI.Paper
import blended.ui.router.Router
import blended.ui.samples.compoments._
import blended.ui.samples.state.SampleAppState
import com.github.ahnfelt.react4s._

sealed trait SamplePage
object HomePage extends SamplePage

object Routes {

  val path : Router[SamplePage] = new Router[SamplePage]

  val routes : Router.Tree[SamplePage,SamplePage] = path(
    HomePage
  )
}

object TopLevelPageResolver {

}
