package com.voicerpg.engine.engine

import com.voicerpg.engine.model.Enemy
import com.voicerpg.engine.model.PartyMember
import com.voicerpg.engine.model.ParsedIntent
import com.voicerpg.engine.model.Spell
import com.voicerpg.engine.model.SpellSchool
import com.voicerpg.engine.model.TargetSelection

import com.voicerpg.engine.model.MetaCommand

object IntentParser {

    @Volatile
    var currentLocale: String = "en"

    /**
     * Dynamic party-member matching: a member is invoked when the utterance contains their
     * display name, their lore class, or any voiceAlias declared in their character data file.
     */
    private fun memberMatchesUtterance(member: PartyMember, lower: String): Boolean {
        if (member.name.isNotBlank() && lower.contains(member.name.lowercase())) return true
        if (member.loreClass.isNotBlank() && lower.contains(member.loreClass.lowercase())) return true
        return com.voicerpg.engine.content.GameContent.voiceAliasesFor(member.id)
            .any { it.isNotBlank() && lower.contains(it.lowercase()) }
    }

    fun parse(
        utterance: String,
        availableSpells: List<Spell> = emptyList(),
        activeEnemies: List<Enemy> = emptyList(),
        party: List<PartyMember> = emptyList(),
        locale: String = currentLocale
    ): ParsedIntent {
        val lower = utterance.lowercase().trim()
        val wordsInUtterance = lower.split(Regex("[^\\p{L}0-9]+")).filter { it.isNotBlank() }.toSet()

        // 0. Detect Meta Voice Commands via localized command catalog with English fallback
        val metaCommand = com.voicerpg.engine.content.GameContent.resolveMetaCommand(utterance, locale)

        if (metaCommand != MetaCommand.NONE) {
            return ParsedIntent(
                spell = defaultFallbackSpell(),
                target = TargetSelection.FIRST_ALIVE_ENEMY,
                rawUtterance = utterance,
                metaCommand = metaCommand
            )
        }

        val hasHealKeyword = com.voicerpg.engine.content.GameContent.hasAction(utterance, "heal", locale)

        var targetEnemyId: String? = null
        var targetHeroId: String? = null

        // 1. Detect target — fully dynamic: party members are matched by name or by the
        //    voiceAliases declared in their character data files. No hardcoded characters.
        var target: TargetSelection = when {
            party.any { memberMatchesUtterance(it, lower) } -> {
                val matchedHero = party.first { memberMatchesUtterance(it, lower) }
                targetHeroId = matchedHero.id
                TargetSelection.SPECIFIC_HERO
            }
            com.voicerpg.engine.content.GameContent.hasGroup(utterance, "self", locale) -> TargetSelection.SELF
            com.voicerpg.engine.content.GameContent.hasGroup(utterance, "party", locale) -> TargetSelection.PARTY_LOWEST
            com.voicerpg.engine.content.GameContent.hasGroup(utterance, "all", locale) -> TargetSelection.ALL_ENEMIES
            else -> TargetSelection.FIRST_ALIVE_ENEMY
        }

        // If target is not a party-specific target, check enemy targets dynamically
        if (target == TargetSelection.FIRST_ALIVE_ENEMY) {
            val aliveEnemies = activeEnemies.filter { it.isAlive }

            // A. Ordinal position matching via localized command catalog with English fallback
            val ordinalIndex = com.voicerpg.engine.content.GameContent.resolveOrdinal(utterance, locale)

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
                    target = TargetSelection.SPECIFIC_ENEMY
                    targetEnemyId = matchedByName.id
                } else {
                    // C. If an enemy is currently marked as targeted by the player, lock on to it
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

        // 2. Detect spell
        val matchedSpell = availableSpells.firstOrNull { spell ->
            lower.contains(spell.name.lowercase())
        } ?: availableSpells.firstOrNull { spell ->
            spell.aliases.any { alias -> lower.contains(alias.lowercase()) }
        } ?: availableSpells.firstOrNull { spell ->
            val nameWords = spell.name.lowercase().split(" ")
            nameWords.any { it.length >= 4 && lower.contains(it) }
        } ?: availableSpells.firstOrNull { spell ->
            // Breath/recovery high-priority: catch ASR-mangled breath commands by checking
            // whether ANY breath alias keyword appears in the utterance (fuzzy match).
            spell.manaRestorePct > 0f && spell.aliases.any { alias ->
                alias.split(" ").filter { it.length >= 4 }.any { word -> lower.contains(word) }
            }
        } ?: availableSpells.firstOrNull { spell ->
            spell.manaRestorePct == 0f && if (hasHealKeyword) spell.isHeal else (!spell.isHeal && spell.school != SpellSchool.HOLY)
        } ?: availableSpells.firstOrNull { spell ->
            spell.manaRestorePct == 0f && if (hasHealKeyword) spell.isHeal else !spell.isHeal
        } ?: availableSpells.firstOrNull { spell ->
            spell.manaRestorePct == 0f && if (hasHealKeyword) spell.isHeal else {
                val schoolKeywords = SpellThesaurus.getKeywordsForSchool(spell.school)
                schoolKeywords.any { lower.contains(it) }
            }
        } ?: availableSpells.firstOrNull { it.manaRestorePct == 0f } ?: availableSpells.firstOrNull() ?: defaultFallbackSpell()

        // If it's a heal or friendly cleanse spell and targeting was unset/first alive enemy/all enemies, default to party
        val isAllySpell = matchedSpell.isHeal || matchedSpell.cleansesDebuffs || matchedSpell.isCleanse
        if (isAllySpell && (target == TargetSelection.FIRST_ALIVE_ENEMY || target == TargetSelection.ALL_ENEMIES || target == TargetSelection.SPECIFIC_ENEMY)) {
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
            id = "fallback_strike",
            name = "Strike",
            school = SpellSchool.PHYSICAL,
            basePower = 45,
            mpCost = 0,
            description = "A basic template attack used when no spell matches.",
            exampleChant = "Strike the enemy",
            aliases = listOf("strike", "attack")
        )
    }
}
