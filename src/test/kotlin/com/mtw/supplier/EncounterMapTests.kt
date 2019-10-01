package com.mtw.supplier

import com.mtw.supplier.ecs.Entity
import com.mtw.supplier.ecs.components.AIComponent
import com.mtw.supplier.ecs.components.FighterComponent
import com.mtw.supplier.ecs.components.HpComponent
import com.mtw.supplier.encounter.map.EncounterMap
import com.mtw.supplier.encounter.map.EncounterNode
import com.mtw.supplier.encounter.EncounterRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
class EncounterMapTests {

    @Test
    fun doesStuff() {
        val fighterOne = Entity(UUID.randomUUID())
            .addComponent(AIComponent())
            .addComponent(HpComponent(5, 5))
            .addComponent(FighterComponent(5, 5))
        val fighterTwo = Entity(UUID.randomUUID())
            .addComponent(AIComponent())
            .addComponent(HpComponent(5, 5))
            .addComponent(FighterComponent(100, 100))

        // Build nodes
        val temple = EncounterNode(1, "temple", 3)
        val bridge = EncounterNode(2, "bridge", 2)
        val plains = EncounterNode(3, "plains", 8)

        // Link nodes
        // TODO: private exits
        temple.exits.add(bridge)
        bridge.exits.add(temple)

        plains.exits.add(bridge)
        bridge.exits.add(plains)

        val encounterMap = EncounterMap()
            .addNode(temple)
            .addNode(bridge)
            .addNode(plains)
            .placeEntity(fighterOne, temple.id)
            .placeEntity(fighterTwo, bridge.id)
        val encounterRunner = EncounterRunner(encounterMap)
        encounterRunner.runTurn()
        System.out.println("#####")
    }

}
