package com.mtw.supplier.encounter

import com.mtw.supplier.ecs.Entity
import com.mtw.supplier.ecs.components.EncounterLocationComponent

class EncounterMap (
    private val nodes: MutableMap<Int, EncounterNode> = mutableMapOf()
) {
    fun addNode(node: EncounterNode): EncounterMap {
        if (node.id in this.nodes) {
            throw java.lang.IllegalArgumentException("Specified node id $node.id was already found in the map's nodes!")
        }
        nodes[node.id] = node
        return this
    }

    // TODO: Possibly maintain internal list
    fun getEntities(): List<Entity> {
        return nodes.flatMap { n -> n.value.entities }
    }

    fun isFull(node: EncounterNode): Boolean {
        TODO()
    }

    fun getNode(nodeId: Int): EncounterNode {
        if (nodeId !in nodes) {
            throw IllegalArgumentException("Specified node id $nodeId was not found in the map's nodes!")
        }
        return nodes[nodeId]!!
    }

    fun placeEntity(entity: Entity, nodeId: Int) {
        TODO()
    }

    fun removeEntity(entity: Entity) {
        TODO()
    }

    fun relocateEntity(entity: Entity, nodeId: Int) {
        entity.getComponent(EncounterLocationComponent::class).locationNodeId = nodeId
    }
}
