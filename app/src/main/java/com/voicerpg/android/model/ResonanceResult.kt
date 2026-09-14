package com.voicerpg.android.model

/**
 * Categorical resonance tiers as defined in DESIGN.md.
 */
enum class ResonanceTier(
    val title: String,
    val minScore: Float,
    val maxScore: Float,
    val bonusDamagePercent: Int,
    val particleMultiplier: Float,
    val baseParticleCount: Int
) {
    BASIC("Basic Chant", 0.0f, 0.29f, 0, 1.0f, 40),
    ADEPT("Adept Resonance", 0.3f, 0.59f, 8, 1.8f, 80),
    MASTER("Master Incantation", 0.6f, 0.89f, 15, 2.6f, 180),
    LOGOS("Logos Resonance", 0.9f, 1.0f, 20, 3.8f, 320);

    companion object {
        fun fromScore(score: Float): ResonanceTier = when {
            score >= 0.9f -> LOGOS
            score >= 0.6f -> MASTER
            score >= 0.3f -> ADEPT
            else -> BASIC
        }
    }
}

/**
 * Result of evaluating an incantation via the Incantation Resonance Engine.
 */
data class ResonanceResult(
    val rawText: String,
    val score: Float, // 0.0 to 1.0
    val tier: ResonanceTier,
    val damageMultiplier: Float, // 1.0 to 1.20
    val particleCount: Int,
    val matchedThematicRoots: List<String>,
    val syllableCount: Int,
    val isNovel: Boolean,
    val repetitionDecayApplied: Float = 1.0f // 1.0 = no decay, 0.5 = 50% decay, etc.
) {
    val bonusPercentText: String get() = "+${tier.bonusDamagePercent}%"
}
