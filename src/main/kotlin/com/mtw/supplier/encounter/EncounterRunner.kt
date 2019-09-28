package com.mtw.supplier.encounter

class EncounterRunner(
    val encounterMap: EncounterMap
) {
    fun runTurn() {
        for(entity in encounterMap.getEntities()) {
            System.out.println(entity)
        }
    }
}
