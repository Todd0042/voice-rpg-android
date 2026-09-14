package com.voicerpg.android.model

import androidx.compose.ui.graphics.Color

enum class SpellSchool(val displayName: String, val themeColor: Color) {
    PYROMANCY("Pyromancy", Color(0xFFFF5722)),
    CRYOMANCY("Cryomancy", Color(0xFF00E5FF)),
    ELECTROMANCY("Electromancy", Color(0xFFE040FB)),
    HOLY("Holy Restoration", Color(0xFFFFD700)),
    SHADOW("Shadow Arts", Color(0xFF7C4DFF)),
    PHYSICAL("Martial", Color(0xFFCFD8DC))
}

data class Spell(
    val id: String,
    val name: String,
    val school: SpellSchool,
    val basePower: Int,
    val mpCost: Int,
    val isHeal: Boolean = false,
    val hitsAll: Boolean = false,
    val description: String,
    val exampleChant: String
)

enum class CharacterStance {
    READY,
    CASTING,
    DAMAGED,
    DEAD,
    VICTORY
}

data class PartyMember(
    val id: String,
    val name: String,
    val loreClass: String,
    val currentHp: Int,
    val maxHp: Int,
    val currentMp: Int,
    val maxMp: Int,
    val spells: List<Spell>,
    val stance: CharacterStance = CharacterStance.READY,
    val avatarTint: Color = Color(0xFF4FC3F7),
    val speed: Int = 60,
    val atbGauge: Float = 0f
) {
    val isAlive: Boolean get() = currentHp > 0 && stance != CharacterStance.DEAD
    val isTurnReady: Boolean get() = isAlive && atbGauge >= 1.0f
    val hpRatio: Float get() = if (isAlive) (currentHp.toFloat() / maxHp).coerceIn(0f, 1f) else 0f
    val mpRatio: Float get() = if (isAlive) (currentMp.toFloat() / maxMp).coerceIn(0f, 1f) else 0f
    val atbRatio: Float get() = if (isAlive) atbGauge.coerceIn(0f, 1f) else 0f
}

data class Enemy(
    val id: String,
    val name: String,
    val subtitle: String,
    val currentHp: Int,
    val maxHp: Int,
    val baseAttack: Int,
    val isBoss: Boolean = false,
    val isTargeted: Boolean = false,
    val isDamagedFlash: Boolean = false,
    val spriteTint: Color = Color(0xFFE57373),
    val speed: Int = 50,
    val atbGauge: Float = 0f
) {
    val isAlive: Boolean get() = currentHp > 0
    val isTurnReady: Boolean get() = isAlive && atbGauge >= 1.0f
    val hpRatio: Float get() = if (isAlive) (currentHp.toFloat() / maxHp).coerceIn(0f, 1f) else 0f
    val atbRatio: Float get() = if (isAlive) atbGauge.coerceIn(0f, 1f) else 0f
}

enum class TargetSelection {
    FIRST_ALIVE_ENEMY,
    SPECIFIC_ENEMY,
    ORC,
    ARCHER,
    SHAMAN,
    ALL_ENEMIES,
    PARTY_LOWEST,
    SELF,
    SPECIFIC_HERO,
    HERO,
    CEDRIC,
    LYRA,
    ZEPHYR
}

data class ParsedIntent(
    val spell: Spell,
    val target: TargetSelection,
    val rawUtterance: String,
    val targetEnemyId: String? = null,
    val targetHeroId: String? = null
)

data class EncounterDefinition(
    val id: String,
    val name: String,
    val description: String = "",
    val environment: BattleEnvironment,
    val enemies: List<Enemy>,
    val initialParty: List<PartyMember>? = null
)

data class FloatingCombatText(
    val id: Long = System.nanoTime(),
    val text: String,
    val color: Color,
    val startX: Float,
    val startY: Float,
    val isCrit: Boolean = false,
    val isHeal: Boolean = false
)

enum class CombatPhase {
    ATB_WAITING,
    PLAYER_INPUT,
    INCANTATION_RESOLVING,
    SPELL_VFX_PLAYING,
    ENEMY_ACTIONS,
    BATTLE_WON,
    BATTLE_LOST
}
