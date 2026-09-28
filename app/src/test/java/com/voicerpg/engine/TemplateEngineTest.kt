package com.voicerpg.engine

import com.voicerpg.engine.content.GameContent
import com.voicerpg.engine.model.SpellSchool
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TemplateEngineTest {

    @Test
    fun testTemplateHeroInitialization() {
        val hero = GameContent.heroCharacter!!.toPartyMember()!!
        assertEquals("Aethel", hero.name)
        assertEquals(240, hero.maxHp)
        assertEquals(140, hero.maxMp)
        assertTrue(hero.isAlive)
        assertEquals(4, hero.spells.size)

        val fireball = hero.spells.first { it.id == "fireball" }
        assertEquals(SpellSchool.PYROMANCY, fireball.school)
        assertTrue(fireball.aliases.isEmpty())

        val frost = hero.spells.first { it.id == "frost_spike" }
        assertEquals(SpellSchool.CRYOMANCY, frost.school)
        assertTrue(frost.aliases.isEmpty())

        val chain = hero.spells.first { it.id == "chain_lightning" }
        assertEquals(SpellSchool.ELECTROMANCY, chain.school)
        assertTrue(chain.aliases.isEmpty())

        val attune = hero.spells.first { it.id == "attune" }
        assertEquals(SpellSchool.PHYSICAL, attune.school)
        assertTrue(attune.aliases.contains("attune"))
    }

    @Test
    fun testTemplateEnemySpawningSingleAndSquad() {
        val soloEnemy = GameContent.enemyTemplates.getValue("shadow_wisp").toEnemy(
            instanceId = "wisp_test",
            nameOverride = "Test Wisp",
            hpOverride = 100
        )
        assertEquals("wisp_test", soloEnemy.id)
        assertEquals(100, soloEnemy.maxHp)
        assertTrue(soloEnemy.isAlive)

        // Test spawning a squad of 3 minions with unique IDs
        val squad = (1..3).map { idx ->
            GameContent.createMinion("combat_drone_$idx", "Combat Drone $idx")
        }
        assertEquals(3, squad.size)
        val ids = squad.map { it.id }.toSet()
        assertEquals(3, ids.size) // IDs must be unique
        assertTrue(squad.all { it.isAlive })
        assertTrue(squad.all { it.name.startsWith("Combat Drone") })
    }

    @Test
    fun testTemplateEncountersConfigured() {
        val solo = GameContent.encounters.getValue("prologue_solo")
        assertEquals(2, solo.enemies.size)
        assertNotNull(solo.initialParty)
        assertEquals(1, solo.initialParty?.size)
        val soloIds = solo.enemies.map { it.id }.toSet()
        assertEquals(2, soloIds.size)

        val trio = GameContent.encounters.getValue("ch9_penitent_gate")
        assertEquals(3, trio.enemies.size)
        val enemyIds = trio.enemies.map { it.id }.toSet()
        assertEquals(3, enemyIds.size)
    }

    @Test
    fun testTemplateStoryGraphIntegrity() {
        val hub = GameContent.nodes.getValue("camp_belfry_hub")
        assertTrue(hub.choices.size >= 4)

        // Verify choices have voice keywords and valid next nodes
        hub.choices.forEach { choice ->
            assertTrue(choice.voiceKeywords.isNotEmpty())
            assertTrue(GameContent.nodes.containsKey(choice.nextNodeId))
        }

        // Verify choice elimination flags exist on the bell and lyra branches
        val bellChoice = hub.choices.first { it.id == "camp_belfry_c_bell" }
        assertNotNull(bellChoice.completionFlag)
        assertEquals("camp_belfry_bell_complete", bellChoice.completionFlag)

        val lyraChoice = hub.choices.first { it.id == "camp_belfry_c_lyra" }
        assertNotNull(lyraChoice.completionFlag)
        assertEquals("camp_belfry_lyra_complete", lyraChoice.completionFlag)

        // Verify combat triggers lead to encounters
        val combatNode = GameContent.nodes.getValue("village_battle_trigger")
        assertEquals("prologue_solo", combatNode.triggerBattleEncounterId)
        assertEquals("village_post_battle", combatNode.nextNodeId)
    }
}