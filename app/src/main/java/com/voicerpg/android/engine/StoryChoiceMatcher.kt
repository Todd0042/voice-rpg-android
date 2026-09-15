package com.voicerpg.android.engine

import com.voicerpg.android.model.DialogueChoice

object StoryChoiceMatcher {

    private val STOP_WORDS = setOf(
        "the", "a", "an", "and", "or", "to", "of", "in", "on", "at", "for", "with",
        "from", "by", "about", "as", "into", "like", "through", "after", "over",
        "between", "out", "against", "during", "without", "before", "under", "around",
        "among", "is", "are", "was", "were", "be", "been", "being", "have", "has",
        "had", "do", "does", "did", "i", "we", "you", "they", "he", "she", "it",
        "my", "our", "your", "their", "his", "her", "its", "me", "us", "them",
        "him", "that", "this", "these", "those", "what", "which", "who", "whom",
        "whose", "let", "lets", "shall", "will", "would", "could", "should",
        "can", "must", "there", "here", "just", "now", "so", "then", "if", "but"
    )

    private val SEARCH_CLUSTER = setOf(
        "examine", "inspect", "investigate", "study", "look", "search", "explore",
        "scout", "check", "peer", "observe", "view", "watch", "scan", "read",
        "gaze", "survey", "find", "tracks", "trail", "vaults", "ruins", "shrine", "stones"
    )

    private val COMBAT_CLUSTER = setOf(
        "attack", "fight", "strike", "charge", "assault", "battle", "slay", "destroy",
        "kill", "smite", "blast", "fireball", "confront", "rush", "breach", "clash",
        "rend", "combat", "draw", "weapon", "blade", "challenge", "phalanx", "behemoth"
    )

    private val TALK_CLUSTER = setOf(
        "ask", "talk", "speak", "inquire", "question", "demand", "proclaim", "shout",
        "call", "greet", "converse", "tell", "discuss", "who", "why", "how", "identity"
    )

    private val MAGIC_CLUSTER = setOf(
        "chant", "cast", "sing", "incantation", "spell", "prayer", "radiance", "harmonic",
        "mana", "flame", "spark", "magic", "holy", "sacred", "resonate", "resonance",
        "chime", "bell", "dial", "toll", "dawn", "elemental"
    )

    private val REST_CLUSTER = setOf(
        "rest", "sleep", "meditate", "vigil", "campfire", "camp", "restore", "relax",
        "breathe", "heal", "peace"
    )

    private val MOVE_CLUSTER = setOf(
        "advance", "descend", "ascend", "enter", "leave", "hurry", "march", "proceed",
        "go", "move", "cross", "climb", "venture", "step", "outside", "forward",
        "southwest", "head", "onward"
    )

    private val RESCUE_CLUSTER = setOf(
        "rescue", "save", "free", "aid", "help", "release", "unshackle", "liberate", "cage"
    )

    private val HARVEST_CLUSTER = setOf(
        "harvest", "gather", "collect", "spores", "cleanse", "sample", "pick"
    )

    private val ALL_CLUSTERS = listOf(
        SEARCH_CLUSTER, COMBAT_CLUSTER, TALK_CLUSTER, MAGIC_CLUSTER,
        REST_CLUSTER, MOVE_CLUSTER, RESCUE_CLUSTER, HARVEST_CLUSTER
    )

    private val PROPER_NOUN_BOOST = setOf(
        "lyra", "cedric", "malakor", "solaria", "zephyr", "behemoth", "willow",
        "acolyte", "bell", "chime", "fireball", "phalanx", "briar", "fane", "aethel"
    )

    /**
     * Scores all available choices against the spoken utterance and returns the best matching
     * choice. Allows loud, nerdy, spell-like roleplay sentences as well as casual phrasing.
     */
    fun matchChoice(
        utterance: String,
        choices: List<DialogueChoice>,
        completedFlags: Map<String, Boolean> = emptyMap()
    ): DialogueChoice? {
        val lower = utterance.lowercase().trim()
        if (lower.isBlank() || choices.isEmpty()) return null

        val tokens = lower.split(Regex("[^a-zA-Z0-9]+"))
            .map { it.trim() }
            .filter { it.length >= 2 && it !in STOP_WORDS }

        // Filter out choices that have already been completed if there are other uncompleted choices
        val validChoices = choices.filter { choice ->
            choice.completionFlag == null || completedFlags[choice.completionFlag] != true
        }.ifEmpty { choices }

        val scoredChoices = validChoices.map { choice ->
            var score = 0
            val choiceTextLower = choice.text.lowercase()

            // 1. Direct whole-text or exact keyword match
            if (lower.contains(choiceTextLower)) {
                score += 150
            }

            for (keyword in choice.voiceKeywords) {
                val kwLower = keyword.lowercase()
                if (lower.contains(kwLower)) {
                    score += if (kwLower.contains(" ")) 120 else 100
                }
            }

            val choiceWords = choiceTextLower.split(Regex("[^a-zA-Z0-9]+"))
                .map { it.trim() }
                .filter { it.length >= 2 && it !in STOP_WORDS }

            val keywordWords = choice.voiceKeywords.flatMap { kw ->
                kw.lowercase().split(Regex("[^a-zA-Z0-9]+"))
            }.filter { it.length >= 2 && it !in STOP_WORDS }

            // 2. Token overlap with choice text and keywords
            for (token in tokens) {
                if (choiceWords.contains(token)) {
                    score += if (token.length >= 5) 35 else 25
                } else if (choiceWords.any { it.contains(token) || (token.contains(it) && it.length >= 4) }) {
                    score += 20
                }

                if (keywordWords.contains(token)) {
                    score += 30
                }

                // 3. Proper noun or thematic landmark boost
                if (token in PROPER_NOUN_BOOST && (choiceWords.contains(token) || keywordWords.contains(token))) {
                    score += 45
                }

                // 4. Action / semantic cluster match
                for (cluster in ALL_CLUSTERS) {
                    if (token in cluster) {
                        val matchesClusterInChoice = choiceWords.any { it in cluster } || keywordWords.any { it in cluster }
                        if (matchesClusterInChoice) {
                            score += 20
                        }
                    }
                }
            }

            choice to score
        }

        val best = scoredChoices.maxByOrNull { it.second }
        // Minimum score threshold of 25 guarantees a meaningful match while tolerating nerdy additions
        return if (best != null && best.second >= 25) best.first else null
    }

    /**
     * Determines whether an utterance represents a progression command when in a non-branching node.
     */
    fun isProgressionUtterance(utterance: String): Boolean {
        val lower = utterance.lowercase().trim()
        if (lower.isBlank()) return false

        val progressionPhrases = listOf(
            "next", "continue", "proceed", "go on", "forward", "onward", "advance",
            "ahead", "let's go", "lets go", "lead on", "lead the way", "tell me more",
            "what happened", "and then", "yes", "okay", "alright", "sure", "step forward",
            "enter", "listen", "keep going", "glory awaits", "march on", "press forward",
            "venture forth", "let us go", "let us proceed", "we ride", "to battle",
            "into the dark", "let's do it", "ready", "i am ready", "we are ready",
            "commence", "start", "agreed", "understood", "as you say", "i will",
            "we will", "indeed", "speak on", "i am listening", "what lies ahead",
            "very well", "make haste", "into the fray", "fight", "attack", "strike"
        )

        if (progressionPhrases.any { lower.contains(it) }) return true

        // Roleplay utterances in a progression node (e.g. "We must be careful", "The blight will pay")
        val words = lower.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotBlank() }
        val isNonMetaRoleplay = words.size >= 2 && !lower.contains("options") && !lower.contains("settings") &&
                !lower.contains("status") && !lower.contains("help") && !lower.contains("menu")

        return isNonMetaRoleplay
    }

    /**
     * Determines whether an utterance in the Battle Victory/Defeat screen means "Commence Story" or "Restart Battle".
     */
    fun parseBattleConclusionIntent(utterance: String): ConclusionAction {
        val lower = utterance.lowercase().trim()
        if (lower.isBlank()) return ConclusionAction.NONE

        val continueKeywords = listOf(
            "commence story", "commence", "story", "continue", "onward", "proceed", "advance",
            "next", "glory", "glory awaits", "forward", "press on", "victory", "venture forth",
            "let's go", "leave", "done", "journey", "march", "we ride", "head out", "explore",
            "continue story", "next chapter", "march onward", "press forward", "lead on",
            "to the next", "venture onward", "awaken"
        )

        val restartKeywords = listOf(
            "restart battle", "restart", "rematch", "retry", "fight again", "again", "redo",
            "once more", "run it back", "try again", "arise", "rebattle", "face them again",
            "challenge again", "not dead yet", "rise again", "strike again", "re-battle",
            "one more time", "restart fight", "reset"
        )

        var continueScore = 0
        var restartScore = 0

        for (kw in continueKeywords) {
            if (lower.contains(kw)) {
                continueScore += if (kw.contains(" ")) 100 else 40
            }
        }

        for (kw in restartKeywords) {
            if (lower.contains(kw)) {
                restartScore += if (kw.contains(" ")) 100 else 40
            }
        }

        return when {
            continueScore > restartScore && continueScore >= 40 -> ConclusionAction.CONTINUE_STORY
            restartScore > continueScore && restartScore >= 40 -> ConclusionAction.RESTART_BATTLE
            else -> ConclusionAction.NONE
        }
    }

    enum class ConclusionAction {
        NONE,
        CONTINUE_STORY,
        RESTART_BATTLE
    }
}
