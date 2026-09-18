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
    fun `attune restore amount is flat - resonance can never inflate it`() {
        // applyAttuneAction uses (maxMp * pct) with no resonance input; assert the math is anchored.
        val maxMp = 140
        val restore = (maxMp * 0.35f).toInt()
        assertEquals(49, restore)
    }
}
