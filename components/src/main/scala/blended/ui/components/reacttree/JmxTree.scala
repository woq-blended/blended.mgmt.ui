package blended.ui.components.reacttree

import blended.jmx.JmxObjectName

sealed trait JmxNodeType {
  def title : String
}

/**
  * The RootNode is just the entry for the JmxTree Model
  */
case object RootNode extends JmxNodeType {
  override def title: String = "/"
}

/**
  * A DomainNode is a tree with a JMX domain at its root and the object names
  * belonging to that domain as tree nodes.
  *
  * DomainNode instances build the first level of a complete JMX tree.
  * @param domain The JMX domain name
  */
case class DomainNode(domain : String) extends JmxNodeType {
  override def title: String = domain
}

/**
  * An ObjectName node holds (partial) object names as data and mave have other
  * ObjectName nodes as children if they share one more properties within their
  * JMX object name.
  * @param name The object name for this node
  * @param parentName The name of the parent (this is only used internally to determine the node title being displayed
  */
case class ObjNameNode(name : JmxObjectName, parentName: JmxObjectName) extends JmxNodeType {
  // The title of the node that is displayed in the tree is the value of the first differentiating property
  // of the object name compared to the name of the parent
  private val titleKey: String = name.differingKeys(parentName).head
  override def title: String = name.properties(titleKey)
}

object JmxTree extends ReactTree[JmxNodeType] {

  override val nodeLabel: JmxNodeType => String = _.title

  val treeConfiguration : TreeProperties = TreeProperties(
    renderer = defaultNodeRenderer,
    keyExtractor = {
      case RootNode => "JmxRoot"
      case DomainNode(d) => d
      case ObjNameNode(n, _) => n.objectName
    },
    showRootNode = false
  )
}

object JmxTreeHelper {

  private def findGroup(objName : JmxObjectName, names: List[JmxObjectName]): JmxObjectName = {

    def differentiatingKey(
      objName : JmxObjectName,
      names: List[JmxObjectName]
    ) : Option[String] = names match {
      case Nil => None
      case head :: tail =>
        if (head.properties.size == objName.properties.size) {
          head.differingKeys(objName) match {
            case k :: Nil => Some(k)
            case _ => differentiatingKey(objName, tail)
          }
        } else {
          differentiatingKey(objName, tail)
        }
    }

    differentiatingKey(objName, names.filterNot(_.equals(objName))) match {
      case None => objName
      case Some(k) => JmxObjectName(objName.domain, objName.properties.filterKeys(_ != k))
    }
  }

  /**
    * Calculate a set of tree nodes from a given list of object names and a parent path.
    *
    * The idea is to find the object name of the next object name node and then call the method recursively
    * for that object name being the parent and the list of object names that have the same stem in their
    * object names.
    * @param names The list of object names
    * @param parentName the parent path we are applying this method for
    * @return the list of TreeNodes built from the list of object names
    */
  private def nameNodes(names : List[JmxObjectName], parentName: JmxObjectName) : List[JmxTree.TreeNode] = names match {
    // No further object names to process => we are done
    case Nil => Nil

    case head :: tail =>
      if (parentName.isParent(head)) {
        // The parent is a direct parent of the list head => we will add a leaf to the tree
        JmxTree.TreeNode(ObjNameNode(head, parentName)) :: nameNodes(tail, parentName)
      } else {

        // If the parentName is not a direct parent of the head, we will find a list of object names
        // that have the same number of properties and only differ in one property
        val groupName : JmxObjectName = findGroup(head, names)

        // We will take the name of the first differentiating property and append it to the current parent
        // to dtermine the next subtree
        val newKey : String = groupName.differingKeys(parentName).head

        val reducedGroup : JmxObjectName = JmxObjectName(parentName.domain, parentName.properties + (newKey -> groupName.properties(newKey)))

        val ancestor : JmxObjectName => Boolean = n => reducedGroup.isAncestor(n)
        val children = nameNodes(names.filter(ancestor), groupName)

        // We will build a tree node from the new parent we have found
        // and then calculate more tree nodes from the rest of the names
        JmxTree.TreeNode(ObjNameNode(groupName, parentName), children) :: nameNodes(names.filterNot(ancestor), parentName)
      }
  }

  /**
    * Turn a list of object names into a tree model that can be used with in a ReactTree component
    * @param names The list of object names to be used
    * @return The hierarchical model representing the input list as a tree
    */
  def treeModel(names : List[JmxObjectName]) : JmxTree.TreeNode = {

    // The starting point of the object name nodes are the object names with only the domain
    // names and no identifying properties
    val objNameNodes : List[JmxObjectName] => String => List[JmxTree.TreeNode] = n => d =>
      nameNodes(n.filter(_.domain == d), JmxObjectName(d, Map.empty))

    // For each distinct JMX we find we will create a domain node and then calcalute the object name nodes
    // from the list of object names we find for that domain
    val domainNodes : List[JmxTree.TreeNode] = names.map(_.domain).distinct
      .map(d => JmxTree.TreeNode(DomainNode(d), objNameNodes(names)(d)))

    // The tree consists of a Root node with the domain nodes as children
    JmxTree.TreeNode(
      RootNode, domainNodes
    )
  }
}
