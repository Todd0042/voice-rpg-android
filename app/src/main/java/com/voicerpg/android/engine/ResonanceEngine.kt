package com.voicerpg.android.engine

import com.voicerpg.android.model.AcousticProfile
import com.voicerpg.android.model.ResonanceResult
import com.voicerpg.android.model.ResonanceTier
import com.voicerpg.android.model.SpellSchool

class ResonanceEngine(
    val noveltyCache: NoveltyCache = NoveltyCache()
) {

    /**
     * Evaluates a recognized chant string and acoustic performance for a given spell school.
     * Grades across 5 distinct dimensions to yield up to a +200% damage multiplier:
     * 1. Thematic Vocabulary (Up to +45%)
     * 2. Lexical Complexity & Syllables (Up to +45%)
     * 3. Acoustic Vocal Volume Projection (Up to +40%)
     * 4. Vocal Inflection, Tone & Crescendo (Up to +40%)
     * 5. Fresh Novelty Surge (Up to +30%)
     */
    fun evaluate(
        utterance: String,
        school: SpellSchool,
        acousticProfile: AcousticProfile = AcousticProfile()
    ): ResonanceResult {
        val trimmed = utterance.trim()
        if (trimmed.isBlank()) {
            return buildResult(
                rawText = utterance,
                bonusPercent = 0,
                matched = emptyList(),
                syllables = 0,
                acousticProfile = acousticProfile,
                isNovel = true,
                decay = 1.0f
            )
        }

        // 1. Lexical and Syllabic Analysis (Up to +45% bonus)
        val words = trimmed.lowercase().replace(Regex("[^a-z0-9\\s]"), "").split("\\s+".toRegex())
        val wordCount = words.size
        val syllableCount = words.sumOf { countSyllables(it) }

        val lexicalPoints = when {
            wordCount <= 2 -> 0f
            wordCount <= 4 -> 10f
            wordCount <= 7 -> 20f
            wordCount <= 11 -> 32f
            else -> 45f // Sustained multi-clause invocation
        }

        // 2. Thematic Root Matching (Up to +45% bonus)
        val matchedRoots = SpellThesaurus.findMatches(trimmed, school)
        val matchedCount = matchedRoots.size

        val thematicPoints = when (matchedCount) {
            0 -> 0f
            1 -> 10f // Base spell name
            2 -> 22f // Good elemental evocative language
            3 -> 35f // Deep arcane roots
            else -> 45f // Mythic elemental invocation
        }

        // Poetic phrasing bonus (archaic/invocation structure)
        val poeticBonus = if (hasPoeticIndicators(trimmed)) 12f else 0f

        // 3. Acoustic Vocal Volume & Projection (Up to +40% bonus)
        val volumePoints = acousticProfile.volumeScore * 100f // 0f to 40f

        // 4. Vocal Inflection, Tone, Pitch & Crescendo (Up to +40% bonus)
        val inflectionPoints = acousticProfile.inflectionScore * 100f // 0f to 40f

        // 5. Anti-Repetition Novelty Cache & Surge (Up to +30% bonus)
        val noveltyResult = noveltyCache.evaluateAndRecord(trimmed)
        val noveltyPoints = when {
            !noveltyResult.isNovel -> 0f
            wordCount <= 2 -> 0f
            wordCount <= 5 -> 10f
            wordCount in 6..8 && matchedCount >= 2 -> 20f
            wordCount >= 9 && matchedCount >= 2 -> 30f
            else -> 10f
        }

        // Calculate Raw Total Points (0 to 200+)
        val subtotal = lexicalPoints + thematicPoints + poeticBonus + volumePoints + inflectionPoints + noveltyPoints

        // Apply repetition decay if player repeated the chant
        val finalPoints = (subtotal * noveltyResult.decayFactor).toInt().coerceIn(0, 200)

        return buildResult(
            rawText = utterance,
            bonusPercent = finalPoints,
            matched = matchedRoots,
            syllables = syllableCount,
            acousticProfile = acousticProfile,
            isNovel = noveltyResult.isNovel,
            decay = noveltyResult.decayFactor
        )
    }

    private fun buildResult(
        rawText: String,
        bonusPercent: Int,
        matched: List<String>,
        syllables: Int,
        acousticProfile: AcousticProfile,
        isNovel: Boolean,
        decay: Float
    ): ResonanceResult {
        val tier = ResonanceTier.fromBonusPercent(bonusPercent)
        // Multiplier ranges from 1.0x (0% bonus) to 3.0x (200% bonus)
        val damageMultiplier = 1.0f + (bonusPercent / 100.0f)

        // Dynamic particle count scaled by bonus percent (40 to 450+ particles)
        val particleCount = (tier.baseParticleCount * (0.85f + 0.3f * (bonusPercent / 200.0f))).toInt().coerceIn(40, 500)

        val normalizedScore = (bonusPercent / 200.0f).coerceIn(0f, 1f)

        return ResonanceResult(
            rawText = rawText,
            score = normalizedScore,
            bonusPercent = bonusPercent,
            tier = tier,
            damageMultiplier = damageMultiplier,
            particleCount = particleCount,
            matchedThematicRoots = matched,
            syllableCount = syllables,
            acousticProfile = acousticProfile,
            isNovel = isNovel,
            repetitionDecayApplied = decay
        )
    }

    private fun hasPoeticIndicators(text: String): Boolean {
        val lower = text.lowercase()
        val poeticTerms = listOf(
            "o ", "spirits of", "descend", "arise", "by the power",
            "unto", "thou", "hark", "awaken", "primordial", "celestial",
            "hearken", "from the void", "unleash", "shall burn", "solar core"
        )
        return poeticTerms.any { lower.contains(it) }
    }

    private fun countSyllables(word: String): Int {
        val clean = word.lowercase().replace(Regex("[^a-z]"), "")
        if (clean.length <= 3) return 1
        var count = 0
        var prevIsVowel = false
        val vowels = "aeiouy"

        for (ch in clean) {
            val isVowel = ch in vowels
            if (isVowel && !prevIsVowel) {
                count++
            }
            prevIsVowel = isVowel
        }

        if (clean.endsWith("e") && !clean.endsWith("le") && count > 1) {
            count--
        }
        return count.coerceAtLeast(1)
    }
}
