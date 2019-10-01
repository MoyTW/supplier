package com.mtw.supplier.encounter

import com.mtw.supplier.ecs.components.AIComponent
import com.mtw.supplier.encounter.map.EncounterMap
import com.mtw.supplier.encounter.rulebook.Rulebook
import org.slf4j.LoggerFactory

class EncounterRunner(
    private val encounterMap: EncounterMap
) {
    fun runTurn() {
        // TODO: Caching of various iterables, if crawling nodes is slow?
        for(entity in encounterMap.getEntities()) {
            if (entity.hasComponent(AIComponent::class)) {
                val nextAction = entity.getComponent(AIComponent::class).decideNextAction(encounterMap)
                logger.info("Action: $nextAction")
                Rulebook.resolveAction(nextAction, encounterMap)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EncounterRunner::class.java)
    }
}
