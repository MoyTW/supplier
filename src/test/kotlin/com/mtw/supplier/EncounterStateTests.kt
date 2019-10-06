package com.mtw.supplier

import com.mtw.supplier.ecs.Entity
import com.mtw.supplier.ecs.components.AIComponent
import com.mtw.supplier.ecs.components.FactionComponent
import com.mtw.supplier.ecs.components.FighterComponent
import com.mtw.supplier.ecs.components.HpComponent
import com.mtw.supplier.encounter.state.EncounterState
import com.mtw.supplier.encounter.state.EncounterNode
import com.mtw.supplier.encounter.EncounterRunner
import com.mtw.supplier.region.RegionalFaction
import com.mtw.supplier.region.RegionalFactionRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class EncounterStateTests {

    @Test
    fun doesStuff() {
        val registry = RegionalFactionRegistry()
            .addFaction(RegionalFaction(0, "beasts", mutableMapOf(0 to 100.0, 1 to 10.0, 2 to -100.0)))
            .addFaction(RegionalFaction(1, "druids", mutableMapOf(0 to 100.0, 1 to 100.0, 2 to -100.0)))
            .addFaction(RegionalFaction(2, "mercenaries", mutableMapOf(0 to -100.0, 1 to -100.0, 2 to 10.0)))

        val fighterOne = Entity(1, "wolf")
            .addComponent(AIComponent())
            .addComponent(HpComponent(5, 5))
            .addComponent(FighterComponent(5, 5, 5))
            .addComponent(FactionComponent(0))
        val fighterTwo = Entity(2, "strongMercenary")
            .addComponent(AIComponent())
            .addComponent(HpComponent(5, 5))
            .addComponent(FighterComponent(5, 100, 100))
            .addComponent(FactionComponent(2))

        // Build nodes
        val temple = EncounterNode(111, "temple", 3)
        val templeBridge = EncounterNode(222, "templeBridge", 2)
        val centerBridge = EncounterNode(333, "centerBridge", 2)
        val plainsBridge = EncounterNode(444, "plainsBridge", 2)
        val plains = EncounterNode(555, "plains", 8)

        // Link nodes
        // TODO: private exits
        temple.exits.add(templeBridge)
        templeBridge.exits.add(temple)

        templeBridge.exits.add(centerBridge)
        centerBridge.exits.add(templeBridge)

        centerBridge.exits.add(plainsBridge)
        plainsBridge.exits.add(centerBridge)

        plainsBridge.exits.add(plains)
        plains.exits.add(plainsBridge)

        val encounterState = EncounterState(registry)
            .addNode(temple)
            .addNode(templeBridge)
            .addNode(centerBridge)
            .addNode(plainsBridge)
            .addNode(plains)
            .placeEntity(fighterOne, temple.id)
            .placeEntity(fighterTwo, plains.id)
        val encounterRunner = EncounterRunner()
        encounterRunner.runEncounter(encounterState)
    }
}
