package com.voicerpg.android.engine

import com.voicerpg.android.model.HeroClass
import com.voicerpg.android.model.Spell
import com.voicerpg.android.model.SpellSchool

object ClassSpellLibrary {

    // Elementalist kit is delegated to StoryEncounters.aethelSpells — the canonical definition
    // used by the story's default hero. This is the SINGLE source of truth for the kit; keeping
    // the literals in one place (StoryEncounters) prevents the two copies from drifting.
    // (Battlemage/Chanter/Shadowweaver kits live here because they are only reachable through
    // character creation; the breath discipline for all classes is StoryEncounters.attuneSpell.)
    val ELEMENTALIST_SPELLS: List<Spell> = StoryEncounters.aethelSpells

    // 2. Battlemage Spells
    val BATTLEMAGE_SPELLS = listOf(
        Spell("flame_strike", "Flame Strike", SpellSchool.PYROMANCY, basePower = 72, mpCost = 14, status = "BURN", description = "Searing close-range flame slash", exampleChant = "Searing flame strike the orc!"),
        Spell("arcane_barrier", "Arcane Barrier", SpellSchool.PHYSICAL, basePower = 45, mpCost = 10, hitsAll = true, isGuard = true, status = "GUARD", description = "Absorbing vanguard protection", exampleChant = "Arcane barrier protect our line!"),
        Spell("thunder_cleave", "Thunder Cleave", SpellSchool.ELECTROMANCY, basePower = 60, mpCost = 16, hitsAll = true, status = "OVERLOAD", description = "Sweeping lightning sword cleave", exampleChant = "Thunder cleave shatter the horde!"),
        StoryEncounters.attuneSpell
    )

    // 3. Chanter Spells
    val CHANTER_SPELLS = listOf(
        Spell("temporal_stasis", "Temporal Stasis", SpellSchool.ELECTROMANCY, basePower = 55, mpCost = 12, status = "OVERLOAD", description = "Acoustic distortion freezing foe in time", exampleChant = "Temporal stasis freeze the archer!"),
        Spell("haste_cadence", "Haste Cadence", SpellSchool.HOLY, basePower = 65, mpCost = 15, isHeal = true, hitsAll = true, status = "BLESS", description = "Rhythmic breath healing and quickening hero ATB", exampleChant = "Swift rhythm hasten our fellowship!"),
        Spell("resonant_surge", "Resonant Surge", SpellSchool.PYROMANCY, basePower = 70, mpCost = 18, hitsAll = true, status = "BURN", description = "Multi-tonal acoustic shockwave", exampleChant = "Harmonic surge ignite all enemies!"),
        StoryEncounters.attuneSpell
    )

    // 4. Shadowweaver Spells
    val SHADOWWEAVER_SPELLS = listOf(
        Spell("shadow_spike", "Shadow Spike", SpellSchool.SHADOW, basePower = 78, mpCost = 12, status = "CORRODE", description = "Piercing needle from darkness", exampleChant = "Abyssal shadow spike the shaman!"),
        Spell("void_drain", "Void Drain", SpellSchool.SHADOW, basePower = 55, mpCost = 14, lifesteal = true, aliases = listOf("siphon", "drain"), status = "CORRODE", description = "Siphons life essence from enemy to caster", exampleChant = "Void siphon drain their life essence!"),
        Spell("umbral_veil", "Umbral Veil", SpellSchool.SHADOW, basePower = 52, mpCost = 16, hitsAll = true, status = "CORRODE", description = "Blinding smoke of dark matter", exampleChant = "Veil of night consume the battlefield!"),
        StoryEncounters.attuneSpell
    )

    fun getSpellsForClass(heroClass: HeroClass): List<Spell> = when (heroClass) {
        HeroClass.ELEMENTALIST -> ELEMENTALIST_SPELLS
        HeroClass.BATTLEMAGE -> BATTLEMAGE_SPELLS
        HeroClass.CHANTER -> CHANTER_SPELLS
        HeroClass.SHADOWWEAVER -> SHADOWWEAVER_SPELLS
    }
}
