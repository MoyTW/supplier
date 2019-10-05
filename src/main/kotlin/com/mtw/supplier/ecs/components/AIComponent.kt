package com.mtw.supplier.ecs.components

import com.mtw.supplier.ecs.Component
import com.mtw.supplier.ecs.Entity
import com.mtw.supplier.encounter.map.EncounterMap
import com.mtw.supplier.encounter.rulebook.Action
import com.mtw.supplier.encounter.rulebook.actions.AttackAction
import com.mtw.supplier.encounter.rulebook.actions.MoveAction
import com.mtw.supplier.encounter.rulebook.actions.WaitAction
import kotlinx.serialization.Serializable

@Serializable
class AIComponent(override var _parentId: Int? = null) : Component() {

    private fun parentIsHostileTo(parentEntity: Entity, otherEntity: Entity, encounterMap: EncounterMap): Boolean {
        val mapFactionRegistry = encounterMap.factionRegistry
        val score = mapFactionRegistry.getRelationshipScore(
            parentEntity.getComponent(FactionComponent::class).factionId,
            otherEntity.getComponent(FactionComponent::class).factionId
        )
        return score < 0.0
    }

    fun decideNextAction(encounterMap: EncounterMap): Action {
        val parentEntity = encounterMap.getEntity(this.parentId)
        // TODO: All of this is a placeholder
        val firstOtherAliveEnemy = encounterMap.getEntities()
            .firstOrNull {
                it != parentEntity &&
                it.hasComponent(AIComponent::class) &&
                it.hasComponent(FactionComponent::class) &&
                this.parentIsHostileTo(parentEntity, it, encounterMap)
            }
            ?: return WaitAction(parentEntity)


        val parentLocation = parentEntity.getComponent(EncounterLocationComponent::class).locationNodeId
        val firstOtherEntityLocation = firstOtherAliveEnemy
            .getComponent(EncounterLocationComponent::class)
            .locationNodeId

        // wow ugly!
        return if (encounterMap.getNodeDirectlyConnected(parentLocation, firstOtherEntityLocation)) {
            AttackAction(parentEntity, firstOtherAliveEnemy)
        } else  {
            val pathToFirstOtherEntity = badDepthFirstSearch(parentLocation, firstOtherEntityLocation, encounterMap)
            if (pathToFirstOtherEntity != null) {
                MoveAction(parentEntity, pathToFirstOtherEntity[pathToFirstOtherEntity.size - 2])
            } else {
                WaitAction(parentEntity)
            }
        }
    }

    /**
     * look this is from memory ok it's not pretty
     * I feel compelled to defend my mediocre-to-bad on-the-spot algorithm skills because it's been so long since I've
     * actually written a classical algorithm, versus business logic & APIs & sequence diagrams & kafka streams lol
     */
    fun badDepthFirstSearch(startNode: Int, endNode: Int, encounterMap: EncounterMap): List<Int>? {
        return dfsRecurse(startNode, endNode, encounterMap, setOf())
    }

    private fun dfsRecurse(startNode: Int, endNode: Int, encounterMap: EncounterMap, visitedNodes: Set<Int>): MutableList<Int>? {
        val exits = encounterMap.getDirectlyConnectedNodes(startNode)

        return when {
            startNode == endNode -> mutableListOf(startNode)
            exits.isEmpty() -> null
            else -> return exits.map { exitId ->
                if (exitId !in visitedNodes) {
                    val newVisitedNodes = visitedNodes.toMutableSet() // this should copy it!
                    newVisitedNodes.add(startNode)
                    val recurseResult = dfsRecurse(exitId, endNode, encounterMap, newVisitedNodes)
                    recurseResult?.add(startNode)
                    recurseResult
                } else {
                    null
                }
            }.firstOrNull {
                it != null
            }
        }
    }
}
