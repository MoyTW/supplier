package com.mtw.supplier.encounter.rulebook

import com.mtw.supplier.ecs.Entity
import com.mtw.supplier.ecs.components.EncounterLocationComponent
import com.mtw.supplier.ecs.components.FighterComponent
import com.mtw.supplier.ecs.components.HpComponent
import com.mtw.supplier.encounter.map.EncounterMap
import com.mtw.supplier.encounter.rulebook.actions.AttackAction
import com.mtw.supplier.encounter.rulebook.actions.MoveAction
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt

object Rulebook {
    private val logger = LoggerFactory.getLogger(Rulebook::class.java)

    fun resolveAction(action: Action, encounterMap: EncounterMap) {
        when (action.actionType) {
            ActionType.MOVE -> resolveMove(action as MoveAction, encounterMap)
            ActionType.ATTACK -> resolveAttack(action as AttackAction, encounterMap)
            ActionType.USE_ITEM -> TODO()
            ActionType.WAIT -> TODO()
        }
    }

    private fun resolveMove(action: MoveAction, encounterMap: EncounterMap) {
        val currentNodeId = action.actor
            .getComponent(EncounterLocationComponent::class)
            .locationNodeId

        val targetNodeSameAsCurrentNode = currentNodeId == action.targetNodeId
        val targetNodeHasRoom = encounterMap.getNodeHasRoom(action.actor, action.targetNodeId)
        val targetNodeReachable = encounterMap.getNodeDirectlyConnected(currentNodeId, action.targetNodeId)

        if (targetNodeSameAsCurrentNode) {
            logger.info("[MOVE]:[INVALID] Target node [${encounterMap.getNodeName(action.targetNodeId)}, ${action.targetNodeId}] and source node are identical!")
        } else if (!targetNodeHasRoom) {
            logger.info("[MOVE]:[INVALID] Target node [${encounterMap.getNodeName(action.targetNodeId)}, ${action.targetNodeId}] full!")
        } else if (!targetNodeReachable) {
            logger.info("[MOVE]:[INVALID] Target node [${encounterMap.getNodeName(action.targetNodeId)}, ${action.targetNodeId}] not adjacent!")
        } else {
            encounterMap.relocateEntity(action.actor, action.targetNodeId)
            logger.info("[MOVE]:[SUCCESS] [${encounterMap.getNodeName(currentNodeId)}, $currentNodeId] to [${encounterMap.getNodeName(action.targetNodeId)}, ${action.targetNodeId}]")
        }
    }

    private fun resolveAttack(action: AttackAction, encounterMap: EncounterMap) {
        val attacker = action.actor
        val attackerNodeId = attacker.getComponent(EncounterLocationComponent::class).locationNodeId

        val defender = action.target
        val defenderNodeId = defender.getComponent(EncounterLocationComponent::class).locationNodeId

        // TODO: Range & visibility & such
        if (!encounterMap.getNodeDirectlyConnected(attackerNodeId, defenderNodeId)) {
            logger.info("[ATTACK]:[INVALID] [${action.actor.name}] cannot reach [${action.target.name}]")
        } else {
            val attackerFighter = attacker.getComponent(FighterComponent::class)
            val defenderFighter = defender.getComponent(FighterComponent::class)

            // TODO: Properly controlled randomness
            val r = Random(4)
            val d100Roll = r.nextInt(100) + 1

            // TODO: Shamelessly stealing POE because why not but maybe actually consider mechanics
            val modifiedAttackRoll = d100Roll + attackerFighter.toHit - defenderFighter.toDodge
            when {
                modifiedAttackRoll < 30 -> {
                    logger.info("[ATTACK]:[MISS] (raw=$d100Roll,final=$modifiedAttackRoll) [${action.actor.name}] missed [${action.target.name}]")
                }
                modifiedAttackRoll in 31..50 -> {
                    val damage = ceil(attackerFighter.hitDamage * .5).roundToInt()
                    defender.getComponent(HpComponent::class).removeHp(damage)
                    logger.info("[ATTACK]:[GRAZE] (raw=$d100Roll,final=$modifiedAttackRoll) [${action.actor.name}] grazed [${action.target.name}] for $damage damage!")
                }
                modifiedAttackRoll in 51..100 -> {
                    val damage = attackerFighter.hitDamage
                    defender.getComponent(HpComponent::class).removeHp(damage)
                    logger.info("[ATTACK]:[HIT] (raw=$d100Roll,final=$modifiedAttackRoll) [${action.actor.name}] hit [${action.target.name}] for $damage damage!")
                }
                modifiedAttackRoll > 100 -> {
                    val damage = ceil(attackerFighter.hitDamage * 1.25).roundToInt()
                    defender.getComponent(HpComponent::class).removeHp(damage)
                    logger.info("[ATTACK]:[CRIT] (raw=$d100Roll,final=$modifiedAttackRoll) [${action.actor.name}] critically hit [${action.target.name}] for $damage damage!")
                }
            }
        }
    }
}