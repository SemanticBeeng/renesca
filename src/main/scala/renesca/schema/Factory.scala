package renesca.schema

// Traits for Factories to create either local items or instances of existing ones.

import renesca.{graph => raw}
import renesca.parameter.StringPropertyValue
import renesca.parameter.implicits._

trait NodeFactory[+T <: Node] {
  def label: raw.Label
  def wrap(node: raw.Node): T
}

trait AbstractRelationFactory[+START <: Node, +RELATION <: AbstractRelation[START, END] with Item, +END <: Node] {
  def startNodeFactory: NodeFactory[START]
  def endNodeFactory: NodeFactory[END]
}

trait RelationFactory[+START <: Node, +RELATION <: Relation[START, END], +END <: Node] extends AbstractRelationFactory[START, RELATION, END] {
  def relationType: raw.RelationType
  def wrap(relation: raw.Relation): RELATION
}

trait HyperRelationFactory[
START <: Node,
STARTRELATION <: Relation[START, HYPERRELATION],
HYPERRELATION <: HyperRelation[START, STARTRELATION, HYPERRELATION, ENDRELATION, END],
ENDRELATION <: Relation[HYPERRELATION, END],
END <: Node] extends NodeFactory[HYPERRELATION] with AbstractRelationFactory[START, HYPERRELATION, END] {

  def startRelationType: raw.RelationType
  def endRelationType: raw.RelationType

  def factory: NodeFactory[HYPERRELATION]

  def startRelationWrap(relation: raw.Relation): STARTRELATION
  def endRelationWrap(relation: raw.Relation): ENDRELATION
  def wrap(startRelation: raw.Relation, middleNode: raw.Node, endRelation: raw.Relation): HYPERRELATION = {
    val hyperRelation = wrap(middleNode)
    hyperRelation._startRelation = startRelationWrap(startRelation)
    hyperRelation._endRelation = endRelationWrap(endRelation)
    hyperRelation
  }

  def startRelationLocal(startNode: START, middleNode: HYPERRELATION): STARTRELATION = {
    startRelationWrap(raw.Relation.local(startNode.node, startRelationType, middleNode.node))
  }

  def endRelationLocal(middleNode: HYPERRELATION, endNode: END): ENDRELATION = {
    endRelationWrap(raw.Relation.local(middleNode.node, endRelationType, endNode.node))
  }
}

