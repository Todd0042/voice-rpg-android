package com.voicerpg.android.model

import androidx.compose.ui.graphics.Color
import com.voicerpg.android.model.SpellSchool

enum class HeroClass(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val startingHp: Int,
    val startingMp: Int,
    val startingSpeed: Int,
    val startingBaseAttack: Int,
    val preferredSchool: SpellSchool,
    val starterSpellIds: List<String>
) {
    ELEMENTALIST(
        id = "elementalist",
        title = "Elementalist",
        subtitle = "Master of Primordial Elements",
        description = "Wields blistering fire, piercing frost, and crackling storm incantations. High magical burst damage.",
        startingHp = 240,
        startingMp = 160,
        startingSpeed = 70,
        startingBaseAttack = 22,
        preferredSchool = SpellSchool.PYROMANCY,
        starterSpellIds = listOf("fireball", "frost_spike", "chain_lightning")
    ),
    BATTLEMAGE(
        id = "battlemage",
        title = "Battlemage",
        subtitle = "Frontline Spellblade Vanguard",
        description = "Combines heavy martial fortitude with devastating close-range elemental inflections. High vitality and defense.",
        startingHp = 320,
        startingMp = 110,
        startingSpeed = 60,
        startingBaseAttack = 28,
        preferredSchool = SpellSchool.PHYSICAL,
        starterSpellIds = listOf("flame_strike", "arcane_barrier", "thunder_cleave")
    ),
    CHANTER(
        id = "chanter",
        title = "Chanter",
        subtitle = "Acoustic Chronomancer",
        description = "Manipulates battle cadence, accelerates turn gauges, and doubles vocal resonance bonuses. High agility and tactical speed.",
        startingHp = 220,
        startingMp = 180,
        startingSpeed = 80,
        startingBaseAttack = 18,
        preferredSchool = SpellSchool.ELECTROMANCY,
        starterSpellIds = listOf("temporal_stasis", "haste_cadence", "resonant_surge")
    ),
    SHADOWWEAVER(
        id = "shadowweaver",
        title = "Shadowweaver",
        subtitle = "Umbral Occultist",
        description = "Strikes from the blind spots of the Blight with precision critical strikes and dark life-draining sibilants.",
        startingHp = 230,
        startingMp = 140,
        startingSpeed = 85,
        startingBaseAttack = 26,
        preferredSchool = SpellSchool.SHADOW,
        starterSpellIds = listOf("shadow_spike", "void_drain", "umbral_veil")
    )
}

enum class AuraColor(
    val id: String,
    val displayName: String,
    val hexColor: String,
    val description: String
) {
    SAPPHIRE_FROST("frost", "Sapphire Frost", "#00E5FF", "Cool cyan luminescence"),
    SOLAR_GOLD("gold", "Solar Gold", "#FFD700", "Radiant dawn brilliance"),
    CRIMSON_PYRE("fire", "Crimson Pyre", "#FF5252", "Fierce incandescent flame"),
    EMERALD_GROVE("verdant", "Emerald Grove", "#69F0AE", "Gentle living earth"),
    AMETHYST_VOID("shadow", "Amethyst Void", "#E040FB", "Mysterious umbral frequency")
}

data class PlayerCustomization(
    val name: String = "Aethel",
    val heroClass: HeroClass = HeroClass.ELEMENTALIST,
    val title: String = "The Awakened Invocator",
    val auraColor: AuraColor = AuraColor.SAPPHIRE_FROST,
    val voiceAffinityBonusSchool: SpellSchool = SpellSchool.PYROMANCY
)

data class SavedCharacterStats(
    val id: String,
    val name: String,
    val loreClass: String,
    val currentHp: Int,
    val maxHp: Int,
    val currentMp: Int,
    val maxMp: Int,
    val speed: Int,
    val level: Int = 1,
    val xp: Int = 0,
    val spellIds: List<String> = emptyList()
) {
    companion object {
        fun fromPartyMember(member: PartyMember): SavedCharacterStats {
            return SavedCharacterStats(
                id = member.id,
                name = member.name,
                loreClass = member.loreClass,
                currentHp = member.currentHp,
                maxHp = member.maxHp,
                currentMp = member.currentMp,
                maxMp = member.maxMp,
                speed = member.speed,
                level = member.level,
                xp = member.xp,
                spellIds = member.spells.map { it.id }
            )
        }
    }
}

data class GameSaveData(
    val saveVersion: Int = 1,
    val saveTimestamp: Long = System.currentTimeMillis(),
    val totalPlaytimeSeconds: Long = 0L,

    // 1. Player Profile & Initial Loadout
    val player: PlayerCustomization = PlayerCustomization(),

    // 2. Story Progress & Current Scene/Dialogue
    val currentSceneId: String = "scene_cottage",
    val currentNodeId: String = "cottage_intro",

    // 3. Complete Decision History (every choice ever selected by player)
    val decisionsMade: List<String> = emptyList(),
    val narrativeFlags: Map<String, Boolean> = emptyMap(),

    // 4. Current Stats for all Party Members
    val partyStats: List<SavedCharacterStats> = emptyList(),

    // 5. Accomplishments & Milestones
    val defeatedEncounters: List<String> = emptyList(),
    val unlockedCompanions: List<String> = listOf("hero"),
    val achievements: List<String> = emptyList(),
    val totalDamageDealt: Long = 0L,
    val totalBattlesWon: Int = 0,
    val highestResonanceTier: String = "BASIC",

    // 6. User Accessibility & Audio Options
    val isEyesFreeMode: Boolean = false,
    val isAutoListen: Boolean = false,
    val isChimeMuted: Boolean = true,
    val isNarrationEnabled: Boolean = true,
    val isReadChoicesEnabled: Boolean = true,
    val speechRate: Float = 1.05f,
    val isCharacterPitchEnabled: Boolean = true,
    val isSpeakerAttributionEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val musicVolume: Float = 0.55f,

    // 7. Per-speaker TTS voice assignments (installed Android voice model names keyed by speaker id)
    val voiceAssignments: Map<String, String> = emptyMap()
)
