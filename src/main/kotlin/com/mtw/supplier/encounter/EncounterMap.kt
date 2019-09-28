package com.mtw.supplier.encounter

import com.mtw.supplier.ecs.Entity

class EncounterMap (
    val nodes: MutableList<EncounterNode>
) {
    // TODO: Possibly maintain internal list
    fun getEntities(): List<Entity> {
        return nodes.flatMap { n -> n.entities }
    }
}
