package blended.mgmt.app.components

import blended.ui.components.reacttable.ReactTable
import blended.updater.config.ContainerInfo

import scala.scalajs.js.Date

object ContainerTable extends ReactTable[ContainerInfo] {

  val lastUpdate : ContainerInfo => String = { ct => new Date(ct.timestampMsec).toISOString() }

  val props = TableProperties(
    columns = Seq(
      ColumnConfig(name = "Container Id", renderer = defaultCellRenderer(_.containerId)),
      ColumnConfig(name = "Last Update", renderer = defaultCellRenderer(lastUpdate))
    ),
    keyExtractor = _.containerId
  )
}
