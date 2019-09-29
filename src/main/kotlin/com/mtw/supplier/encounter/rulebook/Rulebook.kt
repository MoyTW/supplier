package com.mtw.supplier.encounter.rulebook

import com.mtw.supplier.ecs.Entity
import com.mtw.supplier.ecs.components.EncounterLocationComponent
import com.mtw.supplier.encounter.EncounterMap
import com.mtw.supplier.encounter.rulebook.actions.MoveAction
import org.slf4j.LoggerFactory

object Rulebook {
    private val logger = LoggerFactory.getLogger(Rulebook::class.java)

    fun resolveAction(action: Action, encounterMap: EncounterMap) {
        when (action.actionType) {
            ActionType.MOVE -> resolveMove(action as MoveAction, encounterMap)
            ActionType.ATTACK -> TODO()
            ActionType.USE_ITEM -> TODO()
            ActionType.WAIT -> TODO()
        }
    }

    private fun resolveMove(action: MoveAction, encounterMap: EncounterMap) {
        val currentNodeId = action.actor
            .getComponent(EncounterLocationComponent::class)
            .locationNodeId
        val currentNode = encounterMap.getNode(currentNodeId)
        val targetNode = encounterMap.getNode(action.targetNodeId)

        val targetNodeHasRoom = !encounterMap.isFull(targetNode)
        val targetNodeReachable = currentNode.exits.contains(targetNode)

        if (targetNodeHasRoom && targetNodeReachable) {
            encounterMap.relocateEntity(action.actor, action.targetNodeId)
        } else {
            logger.info("Move action not valid!")
        }
    }
}