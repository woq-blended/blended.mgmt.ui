package blended.mgmt.app.backend

import blended.security.BlendedPermissions

case class UserInfo(
  id : String,
  permissions: BlendedPermissions
)
