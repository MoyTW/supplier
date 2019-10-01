package com.mtw.supplier.ecs.components

import com.mtw.supplier.ecs.Component
import com.mtw.supplier.encounter.map.EncounterMap
import com.mtw.supplier.encounter.rulebook.Action
import com.mtw.supplier.encounter.rulebook.actions.MoveAction
import com.mtw.supplier.encounter.rulebook.actions.WaitAction

class AIComponent: Component() {
    fun decideNextAction(encounterMap: EncounterMap): Action {
        // TODO: All of this is a placeholder
        /*
        val firstOtherEntity = encounterMap.getEntities()
            .firstOrNull { it != this.parent }
            ?: return WaitAction(this.parent)
        */
        val currentNodeId = this.parent.getComponent(EncounterLocationComponent::class).locationNodeId
        val connectedNodeIds = encounterMap.getDirectlyConnectedNodes(currentNodeId)
        return if (connectedNodeIds.isNotEmpty()) {
            MoveAction(this.parent, connectedNodeIds[0])
        } else {
            WaitAction(this.parent)
        }
    }
}