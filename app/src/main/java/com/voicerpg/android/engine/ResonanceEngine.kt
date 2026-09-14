package com.voicerpg.android.engine

import com.voicerpg.android.model.ResonanceResult
import com.voicerpg.android.model.ResonanceTier
import com.voicerpg.android.model.SpellSchool

class ResonanceEngine(
    val noveltyCache: NoveltyCache = NoveltyCache()
) {

    /**
     * Evaluates a recognized chant string for a given spell school.
     */
    fun evaluate(utterance: String, school: SpellSchool): ResonanceResult {
        val trimmed = utterance.trim()
        if (trimmed.isBlank()) {
            return buildResult(
                rawText = utterance,
                score = 0.0f,
                matched = emptyList(),
                syllables = 0,
                isNovel = true,
                decay = 1.0f
            )
        }

        // 1. Lexical and Syllabic Analysis
        val words = trimmed.lowercase().replace(Regex("[^a-z0-9\\s]"), "").split("\\s+".toRegex())
        val wordCount = words.size
        val syllableCount = words.sumOf { countSyllables(it) }

        // 2. Thematic Root Matching
        val matchedRoots = SpellThesaurus.findMatches(trimmed, school)
        val matchedCount = matchedRoots.size

        // 3. Multi-factor raw score formulation
        // Word count & syllable cadence: rewards multi-clause evocative phrasing
        val cadenceScore = when {
            wordCount <= 2 -> 0.05f
            wordCount <= 4 -> 0.12f
            wordCount <= 7 -> 0.22f
            else -> (syllableCount / 26.0f).coerceIn(0.25f, 0.42f)
        }

        // Thematic density: rewards using rich evocative spell vocabulary
        // 1 match (e.g. just base spell name) = 0.12, 2 matches = 0.28, 3+ matches = 0.45
        val thematicScore = when (matchedCount) {
            0 -> 0.02f
            1 -> 0.12f
            2 -> 0.28f
            else -> 0.45f
        }

        // Sentence structure / complexity bonus (use of poetic connectors, commas, exclamations)
        val poeticBonus = if (hasPoeticIndicators(trimmed)) 0.12f else 0.0f

        val rawScore = (cadenceScore + thematicScore + poeticBonus).coerceIn(0.05f, 1.0f)

        // 4. Novelty and anti-repetition decay
        val noveltyResult = noveltyCache.evaluateAndRecord(trimmed)
        val finalScore = (rawScore * noveltyResult.decayFactor).coerceIn(0.0f, 1.0f)

        return buildResult(
            rawText = utterance,
            score = finalScore,
            matched = matchedRoots,
            syllables = syllableCount,
            isNovel = noveltyResult.isNovel,
            decay = noveltyResult.decayFactor
        )
    }

    private fun buildResult(
        rawText: String,
        score: Float,
        matched: List<String>,
        syllables: Int,
        isNovel: Boolean,
        decay: Float
    ): ResonanceResult {
        val tier = ResonanceTier.fromScore(score)
        val damageMultiplier = 1.0f + (0.20f * score)
        val particleCount = (tier.baseParticleCount * (0.8f + 0.4f * (score / tier.maxScore.coerceAtLeast(0.1f)))).toInt()

        return ResonanceResult(
            rawText = rawText,
            score = score,
            tier = tier,
            damageMultiplier = damageMultiplier,
            particleCount = particleCount,
            matchedThematicRoots = matched,
            syllableCount = syllables,
            isNovel = isNovel,
            repetitionDecayApplied = decay
        )
    }

    private fun hasPoeticIndicators(text: String): Boolean {
        val lower = text.lowercase()
        val poeticTerms = listOf("o ", "spirits of", "descend", "arise", "by the power", "unto", "thou", "hark", "awaken")
        return poeticTerms.any { lower.contains(it) } || text.contains("!") || text.contains(",")
    }

    /**
     * Heuristic syllable counter for English words.
     */
    fun countSyllables(word: String): Int {
        val clean = word.lowercase().replace(Regex("[^a-z]"), "")
        if (clean.length <= 3) return 1

        var count = 0
        var prevIsVowel = false
        val vowels = setOf('a', 'e', 'i', 'o', 'u', 'y')

        for (i in clean.indices) {
            val isVowel = vowels.contains(clean[i])
            if (isVowel && !prevIsVowel) {
                count++
            }
            prevIsVowel = isVowel
        }

        // Silent 'e' at end
        if (clean.endsWith("e") && !clean.endsWith("le") && count > 1) {
            count--
        }

        return count.coerceAtLeast(1)
    }
}
