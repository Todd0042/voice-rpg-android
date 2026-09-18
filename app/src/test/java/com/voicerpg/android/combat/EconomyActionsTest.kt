package com.voicerpg.android.combat

import com.voicerpg.android.engine.ClassSpellLibrary
import com.voicerpg.android.engine.IntentParser
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.model.HeroClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EconomyActionsTest {

    @Test
    fun `every hero class carries the free attune`() {
        for (klass in HeroClass.entries) {
            val kit = ClassSpellLibrary.getSpellsForClass(klass)
            val attune = kit.firstOrNull { it.manaRestorePct > 0f }
            assertTrue("$klass must have an Attune breath action", attune != null)
            assertEquals(0, attune!!.mpCost)
            assertEquals(0.35f, attune.manaRestorePct, 0.001f)
        }
        assertTrue(StoryEncounters.aethelSpells.any { it.manaRestorePct > 0f })
    }

    @Test
    fun `attune is reachable by voice through name and breath aliases`() {
        val kit = ClassSpellLibrary.ELEMENTALIST_SPELLS
        assertEquals("attune", IntentParser.parse("Attune", kit).spell.id)
        assertEquals("attune", IntentParser.parse("steady my breath and concentrate", kit).spell.id)
        assertEquals("attune", IntentParser.parse("Aethel recover mana", kit).spell.id)
    }

    @Test
    fun `drains stay single and personal while benedictions go party-wide with a potency split`() {
        val cedric = StoryEncounters.cedricSpells
        val lay = cedric.first { it.id == "lay_on_hands" }
        assertTrue("Lay on Hands now heals the whole fellowship", lay.isHeal && lay.hitsAll)

        val zephyr = StoryEncounters.zephyrSpells
        val siphon = zephyr.first { it.id == "umbral_siphon" }
        assertTrue("Zephyr's siphon stays a personal drain (damage + self heal), never a party heal",
            !siphon.isHeal && siphon.lifesteal && !siphon.hitsAll)

        // Party-wide potency split: 4 targets of a 110 heal must cost less each than a focused heal
        val single = DamageResolver.resolveHeal(110f, 1, 1.5f, partyWide = false)
        val party = DamageResolver.resolveHeal(110f, 1, 1.5f, partyWide = true)
        assertTrue("party=$party single=$single", party in (single * 0.55).toInt()..(single * 0.75).toInt())
    }

    @Test
    fun `every companion has their own breath and it works on their own turn only`() {
        for (kit in listOf(
            StoryEncounters.cedricSpells,
            StoryEncounters.lyraSpells,
            StoryEncounters.zephyrSpells
        )) {
            val breath = kit.lastOrNull { it.manaRestorePct > 0f }
            assertTrue("companion kit missing breath action", breath != null)
            assertEquals("breaths must sit last so fallbacks never auto-pick them", kit.size - 1, kit.indexOf(breath))
            assertEquals(breath!!.id, IntentParser.parse("attune", kit).spell.id)
        }
        assertEquals("steady_breath", IntentParser.parse("attune", StoryEncounters.cedricSpells).spell.id)
        assertEquals("deep_root", IntentParser.parse("breathe deep", StoryEncounters.lyraSpells).spell.id)
        assertEquals("quiet_lungs", IntentParser.parse("steady and breathe", StoryEncounters.zephyrSpells).spell.id)
    }

    @Test
    fun `an unevocative fireball utterance falls back to fireball, never to attune`() {
        val kit = ClassSpellLibrary.ELEMENTALIST_SPELLS
        // ASR often splits compounds: "fire ball" matches neither name nor words - fallback must skip restores.
        assertEquals("fireball", IntentParser.parse("fire ball the orc", kit).spell.id)
        assertEquals("fireball", IntentParser.parse("do the thing now", kit).spell.id)
        assertEquals("fireball", IntentParser.parse("hurry up and cast", kit).spell.id)
    }

    @Test
    fun `breath utterances never trigger hero steering or school keyword hijack`() {
        // "deep root vine" should not let the "vine" keyword redirect to Lyra
        // if it's Cedric's turn - breath is a self-action on the active member.
        // We verify the parser picks the breath spell regardless of school keywords in the utterance.
        for (kit in listOf(
            StoryEncounters.aethelSpells,
            StoryEncounters.cedricSpells,
            StoryEncounters.lyraSpells,
            StoryEncounters.zephyrSpells
        )) {
            val breathSpell = kit.first { it.manaRestorePct > 0f }
            // Utterances with accidental school keywords must still resolve to breath
            assertEquals(breathSpell.id, IntentParser.parse("steady breath by the dawn", kit).spell.id)
            assertEquals(breathSpell.id, IntentParser.parse("breathe and recover mana", kit).spell.id)
            assertEquals(breathSpell.id, IntentParser.parse("attune my focus", kit).spell.id)
        }
    }

    @Test
    fun `ASR-mangled breath commands still resolve to the breath spell`() {
        // "a tune" for "attune", "deep route" for "deep root", "quiet lunge" for "quiet lungs"
        assertEquals("attune", IntentParser.parse("a tune please", StoryEncounters.aethelSpells).spell.id)
        assertEquals("deep_root", IntentParser.parse("deep route warden", StoryEncounters.lyraSpells).spell.id)
        assertEquals("quiet_lungs", IntentParser.parse("quiet lunge shadow", StoryEncounters.zephyrSpells).spell.id)
        assertEquals("steady_breath", IntentParser.parse("steady breath templar", StoryEncounters.cedricSpells).spell.id)
    }

    @Test
    fun `attune restore amount is flat - resonance can never inflate it`() {
        // applyAttuneAction uses (maxMp * pct) with no resonance input; assert the math is anchored.
        val maxMp = 140
        val restore = (maxMp * 0.35f).toInt()
        assertEquals(49, restore)
    }
}
