package com.mtw.supplier.encounter.map

import com.mtw.supplier.ecs.Entity

internal class EncounterNode(
    val id: Int,
    val name: String,
    var size: Int,
    val entities: MutableList<Entity> = mutableListOf(),
    val exits: MutableList<EncounterNode> = mutableListOf()
) {
    // TODO: Variable sizes!
    fun getOccupiedSize(): Int {
        return this.entities.count()
    }
}
