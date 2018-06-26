package blended.mgmt.app

import blended.ui.router.Router

object Routes {

  val path = new Router[Page]

  val router = path(HomePage,
    path("container", ContainerPage),
    path("help" , HelpPage)
  )
}
