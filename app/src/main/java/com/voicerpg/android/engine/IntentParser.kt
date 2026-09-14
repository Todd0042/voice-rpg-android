package com.voicerpg.android.engine

import com.voicerpg.android.model.Enemy
import com.voicerpg.android.model.PartyMember
import com.voicerpg.android.model.ParsedIntent
import com.voicerpg.android.model.Spell
import com.voicerpg.android.model.SpellSchool
import com.voicerpg.android.model.TargetSelection

import com.voicerpg.android.model.MetaCommand

object IntentParser {

    fun parse(
        utterance: String,
        availableSpells: List<Spell> = emptyList(),
        activeEnemies: List<Enemy> = emptyList(),
        party: List<PartyMember> = emptyList()
    ): ParsedIntent {
        val lower = utterance.lowercase().trim()
        val wordsInUtterance = lower.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotBlank() }.toSet()

        // 0. Detect Meta Voice Commands (Accessibility, Screenless / Pocket Mode, Status, Settings)
        val metaCommand = when {
            lower == "status" || lower == "report" || lower == "status report" || lower.contains("situation report") ||
                    lower == "check status" || lower == "battle status" || lower == "health" || lower == "hp" -> MetaCommand.STATUS_REPORT

            lower == "enemies" || lower == "check enemies" || lower == "monsters" || lower == "targets" ||
                    lower.contains("who is alive") || lower.contains("who is left") || lower == "target scan" -> MetaCommand.CHECK_ENEMIES

            lower == "party" || lower == "allies" || lower == "party status" || lower == "check party" ||
                    lower == "fellowship" || lower.contains("team status") || lower == "team health" -> MetaCommand.CHECK_PARTY

            lower.contains("eyes free") || lower.contains("pocket mode") || lower.contains("blind mode") ||
                    lower.contains("screenless") || lower.contains("audio mode") || lower.contains("toggle narrator") ||
                    lower.contains("toggle audio") || lower == "narrator" -> MetaCommand.TOGGLE_EYES_FREE

            lower.contains("auto listen") || lower.contains("hands free") || lower.contains("auto mic") ||
                    lower.contains("automatic listening") -> MetaCommand.TOGGLE_AUTO_LISTEN

            lower == "options" || lower == "settings" || lower == "menu" || lower.contains("open options") ||
                    lower.contains("open settings") || lower.contains("show options") -> MetaCommand.OPEN_OPTIONS

            lower.contains("close options") || lower.contains("close settings") || lower == "resume" ||
                    lower == "back" || lower == "close menu" -> MetaCommand.CLOSE_OPTIONS

            lower == "help" || lower.contains("what can i say") || lower == "commands" ||
                    lower == "voice commands" || lower == "help commands" -> MetaCommand.HELP

            else -> MetaCommand.NONE
        }

        if (metaCommand != MetaCommand.NONE) {
            return ParsedIntent(
                spell = defaultFallbackSpell(),
                target = TargetSelection.FIRST_ALIVE_ENEMY,
                rawUtterance = utterance,
                metaCommand = metaCommand
            )
        }

        val hasHealKeyword = lower.contains("heal") || lower.contains("mend") || lower.contains("restore") ||
                lower.contains("cure") || lower.contains("rain") || lower.contains("soothing")

        var targetEnemyId: String? = null
        var targetHeroId: String? = null

        // 1. Detect target
        var target: TargetSelection = when {
            // Check Party targets
            lower.contains("cedric") || lower.contains("templar") -> {
                targetHeroId = party.firstOrNull { it.id == "cedric" }?.id ?: "cedric"
                TargetSelection.CEDRIC
            }
            lower.contains("lyra") || lower.contains("warden") || lower.contains("druid") -> {
                targetHeroId = party.firstOrNull { it.id == "lyra" }?.id ?: "lyra"
                TargetSelection.LYRA
            }
            lower.contains("zephyr") || lower.contains("assassin") -> {
                targetHeroId = party.firstOrNull { it.id == "zephyr" }?.id ?: "zephyr"
                TargetSelection.ZEPHYR
            }
            lower.contains("aethel") || ((lower.contains("mage") || lower.contains("elementalist")) && hasHealKeyword) -> {
                targetHeroId = party.firstOrNull { it.id == "hero" }?.id ?: "hero"
                TargetSelection.HERO
            }
            // Check dynamic party names if not standard 4
            party.any { lower.contains(it.name.lowercase()) } -> {
                val matchedHero = party.first { lower.contains(it.name.lowercase()) }
                targetHeroId = matchedHero.id
                TargetSelection.SPECIFIC_HERO
            }
            "self" in wordsInUtterance || "me" in wordsInUtterance -> TargetSelection.SELF
            lower.contains("party") || lower.contains("allies") || lower.contains("team") -> TargetSelection.PARTY_LOWEST
            "all" in wordsInUtterance || "everyone" in wordsInUtterance || "horde" in wordsInUtterance ||
                    lower.contains("all enemies") || lower.contains("all foes") -> TargetSelection.ALL_ENEMIES
            else -> TargetSelection.FIRST_ALIVE_ENEMY
        }

        // If target is not a party-specific target, check enemy targets dynamically
        if (target == TargetSelection.FIRST_ALIVE_ENEMY) {
            val aliveEnemies = activeEnemies.filter { it.isAlive }

            // A. Ordinal position matching ("first", "second", "third", etc.)
            val ordinalIndex = when {
                lower.contains("first") || lower.contains("1st") || lower.contains("enemy 1") || lower.contains("monster 1") -> 0
                lower.contains("second") || lower.contains("2nd") || lower.contains("enemy 2") || lower.contains("monster 2") -> 1
                lower.contains("third") || lower.contains("3rd") || lower.contains("enemy 3") || lower.contains("monster 3") -> 2
                lower.contains("fourth") || lower.contains("4th") || lower.contains("enemy 4") || lower.contains("monster 4") -> 3
                lower.contains("fifth") || lower.contains("5th") || lower.contains("enemy 5") || lower.contains("monster 5") -> 4
                lower.contains("sixth") || lower.contains("6th") || lower.contains("enemy 6") || lower.contains("monster 6") -> 5
                else -> -1
            }

            if (ordinalIndex in aliveEnemies.indices) {
                val matched = aliveEnemies[ordinalIndex]
                target = TargetSelection.SPECIFIC_ENEMY
                targetEnemyId = matched.id
            } else {
                // B. Dynamic name and subtitle matching from active enemies with relevance scoring
                val scoredEnemies = aliveEnemies.map { enemy ->
                    val nameLower = enemy.name.lowercase()
                    val subtitleLower = enemy.subtitle.lowercase()
                    val idLower = enemy.id.lowercase()
                    var score = 0

                    if (lower.contains(nameLower)) score += 100
                    if (lower.contains(subtitleLower)) score += 30
                    if (lower.contains(idLower)) score += 40

                    val words = (nameLower.split(" ") + subtitleLower.split(" "))
                        .map { it.trim().trimEnd('!', '.', ',', '?') }
                        .filter { it.length >= 3 && it != "the" && it != "and" }

                    for (word in words) {
                        if (lower.contains(word)) {
                            score += if (word.length >= 4) 15 else 8
                        }
                    }
                    enemy to score
                }.filter { it.second > 0 }

                val matchedByName = scoredEnemies.maxByOrNull { it.second }?.first

                if (matchedByName != null) {
                    target = when (matchedByName.id) {
                        "orc" -> TargetSelection.ORC
                        "archer" -> TargetSelection.ARCHER
                        "shaman" -> TargetSelection.SHAMAN
                        else -> TargetSelection.SPECIFIC_ENEMY
                    }
                    targetEnemyId = matchedByName.id
                } else {
                    // C. Fallback to classic keyword matching (useful when activeEnemies list is empty or for presets)
                    when {
                        lower.contains("orc") || lower.contains("warrior") -> {
                            target = TargetSelection.ORC
                            targetEnemyId = "orc"
                        }
                        lower.contains("archer") || lower.contains("skeleton") -> {
                            target = TargetSelection.ARCHER
                            targetEnemyId = "archer"
                        }
                        lower.contains("shaman") || lower.contains("caster") || (lower.contains("mage") && !hasHealKeyword) -> {
                            target = TargetSelection.SHAMAN
                            targetEnemyId = "shaman"
                        }
                        else -> {
                            // D. If an enemy is currently marked as targeted by the player, lock on to it
                            val currentTargeted = activeEnemies.firstOrNull { it.isTargeted && it.isAlive }
                            if (currentTargeted != null) {
                                target = TargetSelection.SPECIFIC_ENEMY
                                targetEnemyId = currentTargeted.id
                            } else {
                                target = TargetSelection.FIRST_ALIVE_ENEMY
                                targetEnemyId = aliveEnemies.firstOrNull()?.id
                            }
                        }
                    }
                }
            }
        }

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

        // If it's a heal spell and targeting was unset/first alive enemy/all enemies, default to party lowest
        if (matchedSpell.isHeal && (target == TargetSelection.FIRST_ALIVE_ENEMY || target == TargetSelection.ALL_ENEMIES || target == TargetSelection.SPECIFIC_ENEMY)) {
            target = TargetSelection.PARTY_LOWEST
            targetEnemyId = null
        }

        return ParsedIntent(
            spell = matchedSpell,
            target = target,
            rawUtterance = utterance,
            targetEnemyId = targetEnemyId,
            targetHeroId = targetHeroId
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
