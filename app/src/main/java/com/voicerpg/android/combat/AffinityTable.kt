package com.voicerpg.android.combat

object AffinityTable {
    // GENERATED from docs/combat-design/data/affinity-matrix.json - do not hand-edit; regenerate.
    // INVARIANT I-2: hard floor 0.8 (impaired, never blocked).
    const val FLOOR = 0.8f
    const val CEILING = 2.0f
    const val SAME_ELEMENT_IMPAIRMENT = 0.8f

    private val MATRIX: Map<String, Map<String, Float>> = mapOf(
        "PYROMANCY" to mapOf("FLESH" to 1.2f, "UNDEAD" to 1.3f, "CONSTRUCT" to 1.0f, "VERDANT" to 2.0f, "VOID" to 1.0f, "RADIANT" to 0.8f, "SILENCE" to 1.0f),
        "CRYOMANCY" to mapOf("FLESH" to 1.0f, "UNDEAD" to 1.0f, "CONSTRUCT" to 1.2f, "VERDANT" to 1.1f, "VOID" to 1.0f, "RADIANT" to 0.8f, "SILENCE" to 1.2f),
        "ELECTROMANCY" to mapOf("FLESH" to 1.2f, "UNDEAD" to 1.1f, "CONSTRUCT" to 2.0f, "VERDANT" to 0.8f, "VOID" to 1.0f, "RADIANT" to 1.0f, "SILENCE" to 1.0f),
        "NATURE" to mapOf("FLESH" to 1.2f, "UNDEAD" to 1.5f, "CONSTRUCT" to 0.8f, "VERDANT" to 0.8f, "VOID" to 1.0f, "RADIANT" to 0.8f, "SILENCE" to 1.0f),
        "HOLY" to mapOf("FLESH" to 1.0f, "UNDEAD" to 2.0f, "CONSTRUCT" to 1.0f, "VERDANT" to 0.8f, "VOID" to 1.5f, "RADIANT" to 0.8f, "SILENCE" to 1.1f),
        "SHADOW" to mapOf("FLESH" to 1.0f, "UNDEAD" to 0.8f, "CONSTRUCT" to 1.2f, "VERDANT" to 1.0f, "VOID" to 0.8f, "RADIANT" to 1.5f, "SILENCE" to 1.0f),
        "PHYSICAL" to mapOf("FLESH" to 1.2f, "UNDEAD" to 1.0f, "CONSTRUCT" to 1.3f, "VERDANT" to 1.1f, "VOID" to 0.8f, "RADIANT" to 1.0f, "SILENCE" to 1.0f),
    )

    fun affinity(school: School, family: EnemyFamily): Float {
        val row = MATRIX[school.name] ?: return 1.0f
        return (row[family.name] ?: 1.0f).coerceIn(FLOOR, CEILING)
    }

    fun effectiveness(school: School, family: EnemyFamily, selfElement: School?): Float {
        val base = affinity(school, family)
        return if (selfElement != null && selfElement == school) minOf(SAME_ELEMENT_IMPAIRMENT, base) else base
    }

    fun label(eff: Float): String = when {
        eff >= 1.5f -> "SUPER_EFFECTIVE"
        eff > 1.05f -> "EFFECTIVE"
        eff < 0.95f -> "IMPAIRED"
        else -> "NEUTRAL"
    }

    fun narrationLine(school: School, enemyName: String, eff: Float): String? = when (label(eff)) {
        "SUPER_EFFECTIVE" -> enemyName + " screams - " + school.displayName.lowercase() + " devastates it!"
        "EFFECTIVE" -> enemyName + " reels - " + school.displayName.lowercase() + " bites deep!"
        "IMPAIRED" -> enemyName + " shrugs off most of the " + school.displayName.lowercase() + "..."
        else -> null
    }
}
