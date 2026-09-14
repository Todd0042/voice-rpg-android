package com.voicerpg.android

import com.voicerpg.android.engine.IntentParser
import com.voicerpg.android.engine.NoveltyCache
import com.voicerpg.android.engine.ResonanceEngine
import com.voicerpg.android.model.ResonanceTier
import com.voicerpg.android.model.Spell
import com.voicerpg.android.model.SpellSchool
import com.voicerpg.android.model.TargetSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ResonanceEngineTest {

    private lateinit var engine: ResonanceEngine
    private lateinit var testSpells: List<Spell>

    @Before
    fun setUp() {
        engine = ResonanceEngine(NoveltyCache())
        testSpells = listOf(
            Spell("fireball", "Fireball", SpellSchool.PYROMANCY, 55, 15, false, false, "Fireball", "Fireball archer"),
            Spell("frost_spike", "Frost Spike", SpellSchool.CRYOMANCY, 50, 12, false, false, "Frost", "Frost spike orc"),
            Spell("chain_lightning", "Chain Lightning", SpellSchool.ELECTROMANCY, 40, 20, false, true, "Lightning", "Lightning all"),
            Spell("divine_heal", "Divine Heal", SpellSchool.HOLY, 70, 18, true, false, "Heal", "Heal party")
        )
    }

    @Test
    fun testBasicChantScoring() {
        val result = engine.evaluate("Fireball archer", SpellSchool.PYROMANCY)
        assertEquals(ResonanceTier.BASIC, result.tier)
        assertEquals(0, result.tier.bonusDamagePercent)
        assertTrue("Score should be < 0.3 for basic chant", result.score < 0.30f)
    }

    @Test
    fun testAdeptChantScoring() {
        val result = engine.evaluate("Burn the archer with blazing flames!", SpellSchool.PYROMANCY)
        assertTrue("Score should be >= 0.3 for adept chant", result.score >= 0.30f)
        assertTrue(result.matchedThematicRoots.isNotEmpty())
    }

    @Test
    fun testMasterChantScoring() {
        val result = engine.evaluate("Spirits of the cinder, engulf the archer in an inferno!", SpellSchool.PYROMANCY)
        assertTrue("Score should be >= 0.60 for master chant", result.score >= 0.60f)
        assertTrue("Should match cinder and inferno", result.matchedThematicRoots.size >= 2)
    }

    @Test
    fun testLogosLegendaryChantScoring() {
        val chant = "O primordial flame of the solar core, descend from the heavens and reduce that wretched archer to ash!"
        val result = engine.evaluate(chant, SpellSchool.PYROMANCY)

        assertEquals(ResonanceTier.LOGOS, result.tier)
        assertEquals(20, result.tier.bonusDamagePercent)
        assertTrue("Particle count should be >= 300 for Logos", result.particleCount >= 300)
        assertTrue("Score should be >= 0.90", result.score >= 0.90f)
    }

    @Test
    fun testNoveltyCacheDecay() {
        val chant = "Engulf the archer in an inferno of blazing embers!"

        val first = engine.evaluate(chant, SpellSchool.PYROMANCY)
        assertEquals(1.0f, first.repetitionDecayApplied, 0.01f)
        assertTrue(first.isNovel)

        val second = engine.evaluate(chant, SpellSchool.PYROMANCY)
        assertEquals(0.5f, second.repetitionDecayApplied, 0.01f)
        assertFalse(second.isNovel)
        assertTrue(second.score < first.score)

        val third = engine.evaluate(chant, SpellSchool.PYROMANCY)
        assertEquals(0.25f, third.repetitionDecayApplied, 0.01f)
        assertTrue(third.score < second.score)
    }

    @Test
    fun testIntentParserTargetAndSpell() {
        val intent1 = IntentParser.parse("Frost spike the orc", testSpells)
        assertEquals("frost_spike", intent1.spell.id)
        assertEquals(TargetSelection.ORC, intent1.target)

        val intent2 = IntentParser.parse("Chain lightning all enemies", testSpells)
        assertEquals("chain_lightning", intent2.spell.id)
        assertEquals(TargetSelection.ALL_ENEMIES, intent2.target)

        val intent3 = IntentParser.parse("Celestial radiance and blessing, heal the party", testSpells)
        assertEquals("divine_heal", intent3.spell.id)
        assertEquals(TargetSelection.PARTY_LOWEST, intent3.target)
    }

    @Test
    fun testCedricLayOnHandsAndLyraSoothingRain() {
        val cedricSpells = listOf(
            Spell("holy_smite", "Holy Smite", SpellSchool.HOLY, 62, 14, false, false, "Smite", "Smite heretic"),
            Spell("lay_on_hands", "Lay on Hands", SpellSchool.HOLY, 75, 16, true, false, "Heal", "Mend Cedric")
        )
        val intentCedric = IntentParser.parse("Sacred radiance mend Cedric's wounds", cedricSpells)
        assertEquals("lay_on_hands", intentCedric.spell.id)
        assertEquals(TargetSelection.CEDRIC, intentCedric.target)
        assertTrue(intentCedric.spell.isHeal)

        val lyraSpells = listOf(
            Spell("soothing_rain", "Soothing Rain", SpellSchool.HOLY, 55, 18, true, true, "Heal", "Soothing rain party"),
            Spell("briar_entangle", "Briar Entangle", SpellSchool.HOLY, 52, 12, false, false, "Snare", "Briar archer")
        )
        val intentLyra = IntentParser.parse("Spirits of the grove grant soothing rain upon our party", lyraSpells)
        assertEquals("soothing_rain", intentLyra.spell.id)
        assertEquals(TargetSelection.PARTY_LOWEST, intentLyra.target)
        assertTrue(intentLyra.spell.isHeal)
    }

    @Test
    fun testAethelChainLightningAndZephyrShadowStrike() {
        val aethelSpells = listOf(
            Spell("fireball", "Fireball", SpellSchool.PYROMANCY, 65, 15, false, false, "Fireball", "Fireball archer"),
            Spell("chain_lightning", "Chain Lightning", SpellSchool.ELECTROMANCY, 48, 20, false, true, "Lightning", "Tempest lightning strike all enemies!")
        )
        val intentAethel = IntentParser.parse("Tempest lightning strike all enemies!", aethelSpells)
        assertEquals("chain_lightning", intentAethel.spell.id)
        assertEquals(TargetSelection.ALL_ENEMIES, intentAethel.target)

        val zephyrSpells = listOf(
            Spell("shadow_strike", "Shadow Strike", SpellSchool.SHADOW, 75, 12, false, false, "Backstab", "Strike from umbra"),
            Spell("venom_flurry", "Venom Flurry", SpellSchool.SHADOW, 50, 15, false, true, "Poison", "Abyssal venom coat my blades")
        )
        val intentZephyr = IntentParser.parse("From the silent umbra, strike the shaman's throat!", zephyrSpells)
        assertEquals("shadow_strike", intentZephyr.spell.id)
        assertEquals(TargetSelection.SHAMAN, intentZephyr.target)
    }

    @Test
    fun testDeadCombatantsZeroResources() {
        val deadHero = com.voicerpg.android.model.PartyMember(
            id = "hero",
            name = "Aethel",
            loreClass = "Elementalist",
            currentHp = 0,
            maxHp = 240,
            currentMp = 140, // Even if raw int is 140
            maxMp = 140,
            spells = emptyList(),
            atbGauge = 0.95f // Even if raw float is 0.95f
        )
        assertFalse(deadHero.isAlive)
        assertEquals(0f, deadHero.hpRatio)
        assertEquals(0f, deadHero.mpRatio)
        assertEquals(0f, deadHero.atbRatio)
        assertFalse(deadHero.isTurnReady)

        val deadEnemy = com.voicerpg.android.model.Enemy(
            id = "orc",
            name = "Orc",
            subtitle = "Vanguard",
            currentHp = 0,
            maxHp = 340,
            baseAttack = 22,
            atbGauge = 0.85f
        )
        assertFalse(deadEnemy.isAlive)
        assertEquals(0f, deadEnemy.hpRatio)
        assertEquals(0f, deadEnemy.atbRatio)
        assertFalse(deadEnemy.isTurnReady)
    }
}
