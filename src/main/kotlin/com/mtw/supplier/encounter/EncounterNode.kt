package com.mtw.supplier.encounter

import com.mtw.supplier.ecs.Entity

class EncounterNode(
    var size: Int,
    val entities: MutableList<Entity> = mutableListOf(),
    val exits: MutableList<EncounterNode> = mutableListOf()
)
