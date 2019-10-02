package com.mtw.supplier.ecs.components

import com.mtw.supplier.ecs.Component
import com.mtw.supplier.encounter.map.EncounterMap
import com.mtw.supplier.encounter.rulebook.Action
import com.mtw.supplier.encounter.rulebook.actions.MoveAction
import com.mtw.supplier.encounter.rulebook.actions.WaitAction

class AIComponent: Component() {
    fun decideNextAction(encounterMap: EncounterMap): Action {
        // TODO: All of this is a placeholder
        val firstOtherEntity = encounterMap.getEntities()
            .firstOrNull { it != this.parent }
            ?: return WaitAction(this.parent)

        val parentLocation = this.parent.getComponent(EncounterLocationComponent::class).locationNodeId
        val firstOtherEntityLocation = firstOtherEntity
            .getComponent(EncounterLocationComponent::class)
            .locationNodeId

        val pathToFirstOtherEntity = badDepthFirstSearch(parentLocation, firstOtherEntityLocation, encounterMap)
        return if (pathToFirstOtherEntity != null) {
            MoveAction(this.parent, pathToFirstOtherEntity[pathToFirstOtherEntity.size - 2])
        } else {
            WaitAction(this.parent)
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
        for (exitId in exits) {
            if (exitId == endNode) {
                return mutableListOf(endNode, startNode)
            } else if (exitId !in visitedNodes) {
                val newVisitedNodes = visitedNodes.toMutableSet()
                newVisitedNodes.add(startNode)
                val recurseResult = dfsRecurse(exitId, endNode, encounterMap, newVisitedNodes)
                recurseResult?.add(startNode)
                return recurseResult
            }
        }
        return null
    }
}