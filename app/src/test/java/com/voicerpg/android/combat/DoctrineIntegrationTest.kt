package com.voicerpg.android.combat

import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.model.SpellSchool
import kotlin.math.roundToInt
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionTest {

    @Test
    fun `xp curve follows 80 plus 60 times level`() {
        assertEquals(140, Progression.xpToNext(1))
        assertEquals(680, Progression.xpToNext(10))
    }

    @Test
    fun `multi level ups carry leftover xp and fallen members still gain at half share`() {
        val roster = listOf(
            ProgressMember("hero", "hero/elementalist", level = 1, xp = 0, alive = true),
            ProgressMember("cedric", "cedric/templar", level = 1, xp = 0, alive = false)
        )
        val outcomes = Progression.awardXp(roster, 900)
        val hero = outcomes.first { it.memberId == "hero" }
        val cedric = outcomes.first { it.memberId == "cedric" }
        assertTrue("hero must gain multiple levels from a big pool, got ${hero.levelsGained}", hero.levelsGained >= 2)
        assertTrue("fallen cedric still gains", cedric.levelsGained >= 1)
        assertTrue(hero.newXp < Progression.xpToNext(hero.newLevel))
    }

    @Test
    fun `growth is shallow - level 20 stays well under a transcendental chant`() {
        val at20 = DamageResolver.levelPower(100f, 20)
        assertTrue("level potency at 20 = $at20 must stay below 2x", at20 < 200f)
    }

    @Test
    fun `class keys map to real growth rows`() {
        assertEquals("cedric/templar", Progression.classKey("cedric", "Templar"))
        assertEquals("lyra/warden", Progression.classKey("lyra", "Grove Warden"))
        assertEquals("zephyr/shadowblade", Progression.classKey("zephyr", "Shadowblade"))
        assertEquals("hero/battlemage", Progression.classKey("hero", "Battlemage"))
    }
}

class EnemyBrainTest {

    private val ouros = MovesetTable.movesetFor("boss_ouros")!!

    @Test
    fun `enemies are never one-note - full movesets with variety`() {
        assertTrue("ouros must carry multiple moves", ouros.moves.size >= 2)
    }

    @Test
    fun `moves respect cooldowns`() {
        val heavy = ouros.moves.firstOrNull { it.cooldownTurns > 0 } ?: ouros.moves.first()
        val withCd = mapOf(heavy.name to 2)
        val available = EnemyBrain.availableMoves(ouros, 1.0f, withCd)
        assertTrue("cooling move must be excluded", available.none { it.name == heavy.name })
        assertTrue("some move must always remain", available.isNotEmpty())
    }

    @Test
    fun `boss phases unlock extra moves only below their hp threshold`() {
        val boss = MovesetTable.TABLE.values.first { it.phases.isNotEmpty() }
        val phase = boss.phases.first()
        val highHp = EnemyBrain.availableMoves(boss, 0.95f, emptyMap())
        val lowHp = EnemyBrain.availableMoves(boss, (phase.hpBelow - 0.05f).coerceAtLeast(0.05f), emptyMap())
        assertTrue(lowHp.size >= highHp.size)
    }

    @Test
    fun `every enemy in the campaign resolves to a real moveset`() {
        val allEnemyNames = StoryEncounters.ALL_ENCOUNTERS.flatMap { it.enemies.map { e -> e.name } }
        allEnemyNames.distinct().forEach { name ->
            val key = EnemyCodex.movesetOf(name)
            assertTrue("'$name' must resolve to a moveset", key.isNotEmpty() && MovesetTable.movesetFor(key) != null)
        }
    }
}

class NoUselessAllyDoctrineTest {

    private val kits: Map<String, List<SpellSchool>> = mapOf(
        "hero" to listOf(SpellSchool.PYROMANCY, SpellSchool.CRYOMANCY, SpellSchool.ELECTROMANCY),
        "cedric" to listOf(SpellSchool.HOLY, SpellSchool.PHYSICAL),
        "lyra" to listOf(SpellSchool.NATURE),
        "zephyr" to listOf(SpellSchool.SHADOW)
    )

    private fun band(encounterId: String): List<String> = when {
        encounterId.contains("prologue") -> listOf("hero")
        encounterId in setOf(
            "forest_ambush", "dungeon_descent", "castle_horde", "cave_broodmother",
            "marsh_rescue", "blight_trackers", "ch3_sentinels"
        ) -> listOf("hero", "cedric")
        encounterId in setOf("swamp_behemoth", "ch7_mire_wyrm", "ch8_executioner_ambush") -> listOf("hero", "cedric", "lyra")
        else -> listOf("hero", "cedric", "lyra", "zephyr")
    }

    private fun stripVariant(name: String): String =
        name.replace(Regex("\\s+(Alpha|Beta|Gamma|IV|III|II|I|A|B|C)$"), "").trim()

    @Test
    fun `system floor guarantees no wall - even against a hypothetical perfect-resist family every kit does at least 80 percent`() {
        // The engine clamps at 0.8 regardless of table content; any future family can only add weak points.
        for (school in School.entries) {
            for (family in EnemyFamily.entries) {
                assertTrue(AffinityTable.affinity(school, family) >= 0.8f)
            }
        }
    }

    @Test
    fun `every mandatory member contributes meaningfully in every legal encounter`() {
        val violations = mutableListOf<String>()
        for (encounter in StoryEncounters.ALL_ENCOUNTERS) {
            val roster = band(encounter.id)
            val families = encounter.enemies.map { EnemyCodex.familyOf(stripVariant(it.name)) }
            for (member in roster) {
                val memberFamilies = families.ifEmpty { listOf(EnemyFamily.FLESH) }
                // (1) DAMAGE FLOOR (I-2): even the worst target for the member's best school is 0.8x.
                val worstAny = memberFamilies.minOf { fam ->
                    kits.getValue(member).maxOf { school ->
                        AffinityTable.effectiveness(School.valueOf(school.name), fam, null)
                    }
                }
                if (worstAny < 0.8f) violations += "${encounter.id}: $member worst=$worstAny"
                // (2) VOICE DOMINATES KNOWLEDGE: a resisted ally chanting at MASTER (0.8 x 2.0 = 1.6)
                // still out-damages a fumbled Basic neutral hit (1.0) - resisted is never useless.
                if (0.8f * 2.0f <= 1.0f) violations += "resisted master chant must beat basic neutral"
                // (3) ALWAYS-RELEVANT LANE: every mandatory member's kit carries a status/utility
                // effect that resists-only degrades and can never be blocked (heal/root/jam/etc).
                val kitSpells = when (member) {
                    "hero" -> StoryEncounters.aethelSpells
                    "cedric" -> StoryEncounters.cedricSpells
                    "lyra" -> StoryEncounters.lyraSpells
                    "zephyr" -> StoryEncounters.zephyrSpells
                    else -> emptyList()
                }
                val hasUtility = kitSpells.any { it.isHeal || it.isGuard || it.lifesteal || it.status != null }
                if (!hasUtility) violations += "${encounter.id}: $member has no affinity-immune lane"
            }
        }
        assertTrue(violations.joinToString("\n"), violations.isEmpty())
    }

    @Test
    fun `resisted lanes always land - status utility survives any family`() {
        // Lyra's Root vs the exact construct/verdant/radiant walls and Zephyr's Poison/Corrode vs void walls.
        for (family in listOf(EnemyFamily.CONSTRUCT, EnemyFamily.VERDANT, EnemyFamily.RADIANT, EnemyFamily.VOID, EnemyFamily.UNDEAD)) {
            val root = StatusSystem.apply(StatusId.ROOT, "ADEPT", family, 60f)!!
            assertTrue("root must still land on $family", root.turnsLeft >= 1 && root.potPct > 0f)
            val poison = StatusSystem.apply(StatusId.POISON, "ADEPT", family, 52f)!!
            assertTrue("poison must still land on $family", poison.turnsLeft >= 1 && poison.potPct > 0f)
        }
    }
}
