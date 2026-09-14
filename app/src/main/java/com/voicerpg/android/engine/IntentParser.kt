package com.voicerpg.android.engine

import com.voicerpg.android.model.ParsedIntent
import com.voicerpg.android.model.Spell
import com.voicerpg.android.model.SpellSchool
import com.voicerpg.android.model.TargetSelection

object IntentParser {

    fun parse(utterance: String, availableSpells: List<Spell>): ParsedIntent {
        val lower = utterance.lowercase()

        // 1. Detect target
        var target = when {
            lower.contains("cedric") || lower.contains("templar") -> TargetSelection.CEDRIC
            lower.contains("lyra") || lower.contains("warden") || lower.contains("druid") -> TargetSelection.LYRA
            lower.contains("zephyr") || lower.contains("assassin") -> TargetSelection.ZEPHYR
            lower.contains("aethel") || lower.contains("mage") && (lower.contains("heal") || lower.contains("mend")) -> TargetSelection.HERO
            lower.contains("orc") || lower.contains("warrior") -> TargetSelection.ORC
            lower.contains("archer") || lower.contains("skeleton") -> TargetSelection.ARCHER
            lower.contains("shaman") || lower.contains("mage") || lower.contains("caster") -> TargetSelection.SHAMAN
            lower.contains("all") || lower.contains("everyone") || lower.contains("horde") -> TargetSelection.ALL_ENEMIES
            lower.contains("party") || lower.contains("allies") || lower.contains("team") -> TargetSelection.PARTY_LOWEST
            lower.contains("self") || lower.contains("me") -> TargetSelection.SELF
            else -> TargetSelection.FIRST_ALIVE_ENEMY
        }

        val hasHealKeyword = lower.contains("heal") || lower.contains("mend") || lower.contains("restore") || lower.contains("cure") || lower.contains("rain") || lower.contains("soothing")

        // 2. Detect spell
        val matchedSpell = availableSpells.firstOrNull { spell ->
            lower.contains(spell.name.lowercase())
        } ?: availableSpells.firstOrNull { spell ->
            val nameWords = spell.name.lowercase().split(" ")
            nameWords.any { it.length >= 4 && lower.contains(it) }
        } ?: availableSpells.firstOrNull { spell ->
            if (hasHealKeyword) spell.isHeal else (!spell.isHeal && spell.school != SpellSchool.HOLY)
        } ?: availableSpells.firstOrNull { spell ->
            if (hasHealKeyword) spell.isHeal else !spell.isHeal
        } ?: availableSpells.firstOrNull { spell ->
            val schoolKeywords = SpellThesaurus.getKeywordsForSchool(spell.school)
            schoolKeywords.any { lower.contains(it) }
        } ?: availableSpells.firstOrNull() ?: defaultFallbackSpell()

        // If it's a heal spell and targeting was unset/first alive enemy, default to party lowest or self
        if (matchedSpell.isHeal && (target == TargetSelection.FIRST_ALIVE_ENEMY || target == TargetSelection.ALL_ENEMIES)) {
            target = TargetSelection.PARTY_LOWEST
        }

        return ParsedIntent(
            spell = matchedSpell,
            target = target,
            rawUtterance = utterance
        )
    }

    private fun defaultFallbackSpell(): Spell {
        return Spell(
            id = "fireball_1",
            name = "Fireball",
            school = SpellSchool.PYROMANCY,
            basePower = 45,
            mpCost = 10,
            description = "Hurls a sphere of roaring flame at the target.",
            exampleChant = "Fireball archer"
        )
    }
}
