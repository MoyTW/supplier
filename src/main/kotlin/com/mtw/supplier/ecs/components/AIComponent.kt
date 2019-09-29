package com.mtw.supplier.ecs.components

import com.mtw.supplier.ecs.Component
import com.mtw.supplier.ecs.Entity
import com.mtw.supplier.encounter.EncounterMap
import com.mtw.supplier.encounter.rulebook.Action
import com.mtw.supplier.encounter.rulebook.ActionType

class AIComponent: Component() {
    fun decideNextAction(self: Entity, encounterMap: EncounterMap): Action {
        TODO()
    }
}