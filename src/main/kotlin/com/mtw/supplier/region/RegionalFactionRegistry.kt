package com.mtw.supplier.region

import kotlinx.serialization.Serializable

@Serializable
class RegionalFactionRegistry {
    private val factions: MutableMap<Int, RegionalFaction> = mutableMapOf()

    internal fun addFaction(regionalFaction: RegionalFaction): RegionalFactionRegistry {
        if (regionalFaction.id in factions) {
            throw FactionAlreadyExistsException(
                "Could not add faction [${regionalFaction.id},${regionalFaction.name}] as it already exists!")
        }

        this.factions[regionalFaction.id] = regionalFaction

        // Ensure full coverage in relationship matrix
        for ((_,faction) in factions) {
            faction.getRelationshipScore(regionalFaction.id)
            regionalFaction.getRelationshipScore(faction.id)
        }

        return this
    }
    class FactionAlreadyExistsException(message: String): Exception(message)

    fun getFactionName(factionId: Int): String {
        return factions[factionId]?.name ?: throw FactionIdNotFoundException(factionId)
    }

    fun getRelationshipScore(leftFactionId: Int, rightFactionId: Int) : Double {
        val leftFaction = factions[leftFactionId] ?: throw FactionIdNotFoundException(leftFactionId)
        if (rightFactionId !in factions) throw FactionIdNotFoundException(rightFactionId)
        return leftFaction.getRelationshipScore(rightFactionId)
    }

    class FactionIdNotFoundException(factionId: Int): Exception("Faction $factionId does not exist in regional factions!")
}
