package com.mtw.supplier.region

import kotlinx.serialization.Serializable

@Serializable
internal class RegionalFaction(
    val id: Int,
    val name: String,
    private val factionIdsToRelationshipScores: Map<Int, Double> = mutableMapOf()
) {
    fun getRelationshipScore(factionId: Int) : Double {
        return factionIdsToRelationshipScores[factionId]
            ?: throw UndefinedRelationshipException(
                "Faction $id has an undefined relationship with other faction $factionId!")
    }
    class UndefinedRelationshipException(message: String): Exception(message)
}
