package blended.mgmt.app.backend

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("jsonwebtoken/index.js", JSImport.Namespace)
object JsonWebToken extends js.Object {

  val decode : js.Function1[String, js.Dictionary[Any]] = js.native

}
