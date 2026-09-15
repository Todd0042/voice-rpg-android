package com.voicerpg.android.engine

import com.voicerpg.android.model.HeroClass
import com.voicerpg.android.model.Spell
import com.voicerpg.android.model.SpellSchool

object ClassSpellLibrary {

    // 1. Elementalist Spells
    val ELEMENTALIST_SPELLS = listOf(
        Spell("fireball", "Fireball", SpellSchool.PYROMANCY, basePower = 65, mpCost = 15, description = "Roaring sphere of flame", exampleChant = "Fireball archer"),
        Spell("frost_spike", "Frost Spike", SpellSchool.CRYOMANCY, basePower = 58, mpCost = 12, description = "Piercing icicle", exampleChant = "Glacial frost spike the orc!"),
        Spell("chain_lightning", "Chain Lightning", SpellSchool.ELECTROMANCY, basePower = 48, mpCost = 20, hitsAll = true, description = "Arcing lightning storm", exampleChant = "Tempest lightning strike all enemies!")
    )

    // 2. Battlemage Spells
    val BATTLEMAGE_SPELLS = listOf(
        Spell("flame_strike", "Flame Strike", SpellSchool.PYROMANCY, basePower = 72, mpCost = 14, description = "Searing close-range flame slash", exampleChant = "Searing flame strike the orc!"),
        Spell("arcane_barrier", "Arcane Barrier", SpellSchool.PHYSICAL, basePower = 45, mpCost = 10, hitsAll = true, description = "Absorbing vanguard protection", exampleChant = "Arcane barrier protect our line!"),
        Spell("thunder_cleave", "Thunder Cleave", SpellSchool.ELECTROMANCY, basePower = 60, mpCost = 16, hitsAll = true, description = "Sweeping lightning sword cleave", exampleChant = "Thunder cleave shatter the horde!")
    )

    // 3. Chanter Spells
    val CHANTER_SPELLS = listOf(
        Spell("temporal_stasis", "Temporal Stasis", SpellSchool.ELECTROMANCY, basePower = 55, mpCost = 12, description = "Acoustic distortion freezing foe in time", exampleChant = "Temporal stasis freeze the archer!"),
        Spell("haste_cadence", "Haste Cadence", SpellSchool.HOLY, basePower = 65, mpCost = 15, isHeal = true, description = "Rhythmic breath healing and quickening hero ATB", exampleChant = "Swift rhythm hasten our fellowship!"),
        Spell("resonant_surge", "Resonant Surge", SpellSchool.PYROMANCY, basePower = 70, mpCost = 18, hitsAll = true, description = "Multi-tonal acoustic shockwave", exampleChant = "Harmonic surge ignite all enemies!")
    )

    // 4. Shadowweaver Spells
    val SHADOWWEAVER_SPELLS = listOf(
        Spell("shadow_spike", "Shadow Spike", SpellSchool.SHADOW, basePower = 78, mpCost = 12, description = "Piercing needle from darkness", exampleChant = "Abyssal shadow spike the shaman!"),
        Spell("void_drain", "Void Drain", SpellSchool.SHADOW, basePower = 55, mpCost = 14, isHeal = true, description = "Siphons life essence from enemy to caster", exampleChant = "Void siphon drain their life essence!"),
        Spell("umbral_veil", "Umbral Veil", SpellSchool.SHADOW, basePower = 52, mpCost = 16, hitsAll = true, description = "Blinding smoke of dark matter", exampleChant = "Veil of night consume the battlefield!")
    )

    fun getSpellsForClass(heroClass: HeroClass): List<Spell> = when (heroClass) {
        HeroClass.ELEMENTALIST -> ELEMENTALIST_SPELLS
        HeroClass.BATTLEMAGE -> BATTLEMAGE_SPELLS
        HeroClass.CHANTER -> CHANTER_SPELLS
        HeroClass.SHADOWWEAVER -> SHADOWWEAVER_SPELLS
    }
}
