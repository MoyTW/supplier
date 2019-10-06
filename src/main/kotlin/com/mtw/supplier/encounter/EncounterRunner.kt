package com.mtw.supplier.encounter

import com.mtw.supplier.ecs.components.AIComponent
import com.mtw.supplier.encounter.state.EncounterState
import com.mtw.supplier.encounter.rulebook.Rulebook
import org.slf4j.LoggerFactory

class EncounterRunner(
    private val encounterState: EncounterState
) {
    fun runTurn() {
        // TODO: Caching of various iterables, if crawling nodes is slow?
        for(entity in encounterState.getEntities()) {
            if (entity.hasComponent(AIComponent::class)) {
                val nextAction = entity.getComponent(AIComponent::class).decideNextAction(encounterState)
                logger.info("Action: $nextAction")
                Rulebook.resolveAction(nextAction, encounterState)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EncounterRunner::class.java)
    }
}
