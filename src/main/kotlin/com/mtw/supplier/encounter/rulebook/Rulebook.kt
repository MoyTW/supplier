package com.mtw.supplier.encounter.rulebook

import com.mtw.supplier.ecs.components.EncounterLocationComponent
import com.mtw.supplier.encounter.map.EncounterMap
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

        val targetNodeSameAsCurrentNode = currentNodeId == action.targetNodeId
        val targetNodeHasRoom = encounterMap.getNodeHasRoom(action.actor, action.targetNodeId)
        val targetNodeReachable = encounterMap.getNodeDirectlyConnected(currentNodeId, action.targetNodeId)

        if (targetNodeSameAsCurrentNode) {
            logger.info("[MOVE]:[INVALID] Target node [${encounterMap.getNodeName(action.targetNodeId)}, ${action.targetNodeId}] and source node are identical!")
        } else if (!targetNodeHasRoom) {
            logger.info("[MOVE]:[INVALID] Target node [${encounterMap.getNodeName(action.targetNodeId)}, ${action.targetNodeId}] full!")
        } else if (!targetNodeReachable) {
            logger.info("[MOVE]:[INVALID] Target node [${encounterMap.getNodeName(action.targetNodeId)}, ${action.targetNodeId}] not adjacent!")
        } else {
            encounterMap.relocateEntity(action.actor, action.targetNodeId)
            logger.info("[MOVE]:[SUCCESS] [${encounterMap.getNodeName(currentNodeId)}, $currentNodeId] to [${encounterMap.getNodeName(action.targetNodeId)}, ${action.targetNodeId}]")
        }
    }
}