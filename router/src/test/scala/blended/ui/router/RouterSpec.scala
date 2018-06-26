package blended.ui.router

import org.scalatest.FreeSpec

class RouterSpec extends FreeSpec {

  sealed trait TestPage
  final case object HomePage extends TestPage
  final case class SomePage(parent: HomePage.type) extends TestPage
  final case class StringPage(id: String, parent: HomePage.type) extends TestPage
  final case class IntPage(id: Int, parent: HomePage.type) extends TestPage

  "The Router should" - {

    "resolve the root object correctly" in {

      val path = new Router[TestPage]
      val router = path(HomePage)

      assert(router.data("/").isDefined)
      assert(router.data("/").forall(_ == HomePage))

      assert(router.path(HomePage) == "/")
    }

    "resolve a constant subPath correctly" in {
      val path = new Router[TestPage]
      val router = path(HomePage,
        path("some", SomePage)
      )

      assert(router.data("/some" ).isDefined)
      assert(router.data("/some").forall(_ == SomePage(HomePage)))

      assert(router.path(SomePage(HomePage)) == "/some")
    }

    "resolve a string subpath correctly" in {
      val path = new Router[TestPage]
      val router = path(HomePage,
        path(Router.string, StringPage)
      )

      assert(router.data("/foo" ).isDefined)
      assert(router.data("/foo").forall(_ == StringPage("foo", HomePage)))

      assert(router.data("/bar" ).isDefined)
      assert(router.data("/bar").forall(_ == StringPage("bar", HomePage)))

      assert(router.path(StringPage("foo", HomePage)) == "/foo")
    }

    "resolve an int subpath correctly" in {
      val path = new Router[TestPage]
      val router = path(HomePage,
        path(Router.int, IntPage)
      )

      assert(router.data("/1" ).isDefined)
      assert(router.data("/1").forall(_ == IntPage(1, HomePage)))

      assert(router.path(IntPage(1, HomePage)) == "/1")
    }
  }
}
