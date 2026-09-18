package com.voicerpg.android.combat

/**
 * Shallow progression (SPEC §8): levels grow HP/MP/speed/defense + a 3-4.5%/level
 * potency multiplier on base power. Never a dominant ATK stat — resonance stays king.
 */
data class StatGrowth(
    val maxHp: Int,
    val maxMp: Int,
    val speed: Int,
    val defense: Int,
    val potencyPct: Float
)

data class ProgressMember(
    val id: String,
    val classKey: String,
    val level: Int,
    val xp: Int,
    val alive: Boolean
)

data class ProgressOutcome(
    val memberId: String,
    val levelsGained: Int,
    val newLevel: Int,
    val newXp: Int,
    val dMaxHp: Int,
    val dMaxMp: Int,
    val dSpeed: Int,
    val dDefense: Int
)

object Progression {
    const val MAX_LEVEL = 30
    const val FALLEN_SHARE = 0.5f

    fun xpToNext(level: Int): Int = 80 + 60 * level

    val GROWTH: Map<String, StatGrowth> = mapOf(
        "default" to StatGrowth(18, 8, 1, 1, 0.03f),
        "hero/elementalist" to StatGrowth(12, 14, 1, 1, 0.04f),
        "hero/battlemage" to StatGrowth(16, 10, 1, 2, 0.035f),
        "hero/chanter" to StatGrowth(14, 12, 1, 1, 0.03f),
        "hero/shadowweaver" to StatGrowth(11, 11, 2, 1, 0.04f),
        "cedric/templar" to StatGrowth(24, 6, 1, 3, 0.03f),
        "lyra/warden" to StatGrowth(15, 12, 1, 1, 0.035f),
        "zephyr/shadowblade" to StatGrowth(12, 9, 2, 1, 0.045f)
    )

    fun growthFor(classKey: String): StatGrowth = GROWTH[classKey] ?: GROWTH.getValue("default")

    fun classKey(memberId: String, loreClass: String): String = when {
        memberId == "hero" -> "hero/" + loreClass.lowercase().replace(" ", "")
        memberId == "cedric" -> "cedric/templar"
        memberId == "lyra" -> "lyra/warden"
        memberId == "zephyr" -> "zephyr/shadowblade"
        else -> "default"
    }

    /**
     * Even split among alive story-legal members; fallen members still gain at 50% share.
     * Multiple level-ups carry over. Returns an outcome per member (0 levels if none).
     */
    fun awardXp(members: List<ProgressMember>, totalXp: Int): List<ProgressOutcome> {
        if (members.isEmpty() || totalXp <= 0) return emptyList()
        val alive = members.count { it.alive }
        val weightSum = members.sumOf { if (it.alive) 1.0 else FALLEN_SHARE.toDouble() }
        val unit = if (weightSum > 0) totalXp.toDouble() / weightSum else 0.0

        return members.map { m ->
            val share = (if (m.alive) 1.0 else FALLEN_SHARE.toDouble()) * unit
            var level = m.level
            var xp = m.xp + share.toInt()
            var gained = 0
            var dHp = 0
            var dMp = 0
            var dSp = 0
            var dDef = 0
            while (level < MAX_LEVEL && xp >= xpToNext(level)) {
                xp -= xpToNext(level)
                level++
                gained++
                val g = growthFor(m.classKey)
                dHp += g.maxHp; dMp += g.maxMp; dSp += g.speed; dDef += g.defense
            }
            ProgressOutcome(m.id, gained, level, xp, dHp, dMp, dSp, dDef)
        }
    }
}
