package com.voicerpg.android.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.IntentParser
import com.voicerpg.android.engine.SaveManager
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.engine.StoryScript
import com.voicerpg.android.model.DialogueChoice
import com.voicerpg.android.model.DialogueNode
import com.voicerpg.android.model.EncounterDefinition
import com.voicerpg.android.model.GameSaveData
import com.voicerpg.android.model.GameScreen
import com.voicerpg.android.model.MetaCommand
import com.voicerpg.android.model.PartyMember
import com.voicerpg.android.model.PlayerCustomization
import com.voicerpg.android.model.SavedCharacterStats
import com.voicerpg.android.model.StoryScene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoryState(
    val currentScene: StoryScene = StoryScript.SCENE_COTTAGE,
    val currentNode: DialogueNode = StoryScript.ALL_NODES["cottage_intro"]!!,
    val gameScreen: GameScreen = GameScreen.CHARACTER_CREATION,
    val activeEncounter: EncounterDefinition? = null,
    val player: PlayerCustomization = PlayerCustomization(),
    val decisionsMade: List<String> = emptyList(),
    val narrativeFlags: Map<String, Boolean> = emptyMap(),
    val defeatedEncounters: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val partyStats: List<SavedCharacterStats> = emptyList(),
    val isNarratorSpeaking: Boolean = false,
    val isTypingComplete: Boolean = true
)

class StoryViewModel(
    val speechManager: SpeechManager,
    val combatNarrator: CombatNarrator,
    val saveManager: SaveManager = SaveManager(),
    private val scopeOverride: CoroutineScope? = null
) : ViewModel() {

    private val activeScope: CoroutineScope
        get() = scopeOverride ?: viewModelScope

    private val _state = MutableStateFlow(StoryState())
    val state: StateFlow<StoryState> = _state.asStateFlow()

    init {
        // Load persistent game save on boot
        val existingSave = saveManager.load()
        if (existingSave != null) {
            val restoredScene = StoryScript.ALL_SCENES[existingSave.currentSceneId] ?: StoryScript.SCENE_COTTAGE
            var restoredNode = StoryScript.ALL_NODES[existingSave.currentNodeId] ?: StoryScript.ALL_NODES["cottage_intro"]!!

            if (restoredNode.id == "camp_intro") {
                val allThreeComplete = existingSave.narrativeFlags["substory_blight_complete"] == true &&
                        existingSave.narrativeFlags["substory_towers_complete"] == true &&
                        existingSave.narrativeFlags["substory_rest_complete"] == true
                if (allThreeComplete) {
                    StoryScript.ALL_NODES["camp_all_completed"]?.let { restoredNode = it }
                }
            }

            if (restoredNode.id == "chapter3_intro" || restoredNode.id == "ch3_hub") {
                val ch3BothComplete = existingSave.narrativeFlags["ch3_sentinels_complete"] == true &&
                        existingSave.narrativeFlags["ch3_chime_complete"] == true
                if (ch3BothComplete) {
                    StoryScript.ALL_NODES["ch3_all_completed"]?.let { restoredNode = it }
                }
            }

            if (restoredNode.id == "ch5_intro" || restoredNode.id == "ch5_hub") {
                val ch5BothComplete = existingSave.narrativeFlags["ch5_creek_scouted"] == true &&
                        existingSave.narrativeFlags["ch5_wards_examined"] == true
                if (ch5BothComplete) {
                    StoryScript.ALL_NODES["ch5_all_completed"]?.let { restoredNode = it }
                }
            }

            if (restoredNode.id == "ch6_intro" || restoredNode.id == "ch6_hub") {
                val ch6BothComplete = existingSave.narrativeFlags["ch6_lore_complete"] == true &&
                        existingSave.narrativeFlags["ch6_spores_complete"] == true
                if (ch6BothComplete) {
                    StoryScript.ALL_NODES["ch6_all_completed"]?.let { restoredNode = it }
                }
            }

            _state.value = StoryState(
                currentScene = restoredScene,
                currentNode = restoredNode,
                gameScreen = GameScreen.STORY_EXPLORATION,
                player = existingSave.player,
                decisionsMade = existingSave.decisionsMade,
                narrativeFlags = existingSave.narrativeFlags,
                defeatedEncounters = existingSave.defeatedEncounters,
                achievements = existingSave.achievements,
                partyStats = existingSave.partyStats
            )
            combatNarrator.setEyesFreeMode(existingSave.isEyesFreeMode)
            combatNarrator.setNarrationEnabled(existingSave.isNarrationEnabled)
            combatNarrator.setReadChoicesEnabled(existingSave.isReadChoicesEnabled)
            combatNarrator.setSpeechRate(existingSave.speechRate)
            combatNarrator.setCharacterPitchEnabled(existingSave.isCharacterPitchEnabled)
            speechManager.setAutoListen(existingSave.isAutoListen)
            speechManager.setChimeMuted(existingSave.isChimeMuted)
            narrateCurrentNode()
        } else {
            // First time player: start at Character Creation
            _state.value = StoryState(
                gameScreen = GameScreen.CHARACTER_CREATION
            )
        }
    }

    /**
     * Initializes a fresh game from Character Creation.
     */
    fun startNewGame(customization: PlayerCustomization) {
        val initialSave = saveManager.createInitialSave(customization)
        _state.value = StoryState(
            currentScene = StoryScript.SCENE_COTTAGE,
            currentNode = StoryScript.ALL_NODES["cottage_intro"]!!,
            gameScreen = GameScreen.STORY_EXPLORATION,
            player = customization,
            decisionsMade = emptyList(),
            narrativeFlags = emptyMap(),
            defeatedEncounters = emptyList(),
            achievements = initialSave.achievements,
            partyStats = initialSave.partyStats
        )
        persistCurrentState()
        narrateCurrentNode()
    }

    fun resetGame() {
        saveManager.deleteSave()
        _state.value = StoryState(
            gameScreen = GameScreen.CHARACTER_CREATION
        )
    }

    fun advanceDialogue() {
        combatNarrator.stop()
        val node = _state.value.currentNode
        if (node.choices.isNotEmpty()) {
            return
        }

        if (node.triggerBattleEncounterId != null) {
            triggerEncounter(node.triggerBattleEncounterId)
            return
        }

        val nextId = node.nextNodeId
        if (nextId != null) {
            val nextNode = StoryScript.ALL_NODES[nextId]
            if (nextNode != null) {
                applyNodeTransition(nextNode)
            }
        } else if (node.choices.isEmpty()) {
            if (node.id.startsWith("ch3_") || node.id.startsWith("ch4_") || node.id.startsWith("ch5_") || node.id.startsWith("ch6_") || node.id == "ch4_act1_complete") {
                return
            }
            val fallbackNode = StoryScript.ALL_NODES["camp_intro"] ?: StoryScript.ALL_NODES["crossroads_intro"]
            if (fallbackNode != null) {
                applyNodeTransition(fallbackNode)
            }
        }
    }

    fun selectChoice(choice: DialogueChoice) {
        if (choice.completionFlag != null && _state.value.narrativeFlags[choice.completionFlag] == true) {
            combatNarrator.speak("That objective has already been completed. Please select a remaining task.", force = true)
            return
        }

        combatNarrator.stop()
        val nextNode = StoryScript.ALL_NODES[choice.nextNodeId]
        if (nextNode != null) {
            val updatedDecisions = _state.value.decisionsMade + choice.id
            _state.value = _state.value.copy(decisionsMade = updatedDecisions)
            applyNodeTransition(nextNode)
        }
    }

    private fun applyNodeTransition(newNode: DialogueNode) {
        var effectiveNode = newNode
        val updatedFlags = if (newNode.setFlagOnEnter != null) {
            _state.value.narrativeFlags + (newNode.setFlagOnEnter to true)
        } else {
            _state.value.narrativeFlags
        }

        var updatedPartyStats = _state.value.partyStats
        if (newNode.setFlagOnEnter == "substory_rest_complete") {
            updatedPartyStats = updatedPartyStats.map { member ->
                member.copy(currentHp = member.maxHp, currentMp = member.maxMp)
            }
        }

        // If transitioning to camp_intro, check if all 3 camp sub-stories are completed
        if (effectiveNode.id == "camp_intro") {
            val allThreeComplete = updatedFlags["substory_blight_complete"] == true &&
                    updatedFlags["substory_towers_complete"] == true &&
                    updatedFlags["substory_rest_complete"] == true
            if (allThreeComplete) {
                StoryScript.ALL_NODES["camp_all_completed"]?.let { effectiveNode = it }
            }
        }

        // If transitioning to chapter3_intro or ch3_hub, check if both objectives are completed
        if (effectiveNode.id == "chapter3_intro" || effectiveNode.id == "ch3_hub") {
            val ch3BothComplete = updatedFlags["ch3_sentinels_complete"] == true &&
                    updatedFlags["ch3_chime_complete"] == true
            if (ch3BothComplete) {
                StoryScript.ALL_NODES["ch3_all_completed"]?.let { effectiveNode = it }
            }
        }

        // If transitioning to ch5_intro or ch5_hub, check if both scouting objectives are completed
        if (effectiveNode.id == "ch5_intro" || effectiveNode.id == "ch5_hub") {
            val ch5BothComplete = updatedFlags["ch5_creek_scouted"] == true &&
                    updatedFlags["ch5_wards_examined"] == true
            if (ch5BothComplete) {
                StoryScript.ALL_NODES["ch5_all_completed"]?.let { effectiveNode = it }
            }
        }

        // If transitioning to ch6_intro or ch6_hub, check if both preparation objectives are completed
        if (effectiveNode.id == "ch6_intro" || effectiveNode.id == "ch6_hub") {
            val ch6BothComplete = updatedFlags["ch6_lore_complete"] == true &&
                    updatedFlags["ch6_spores_complete"] == true
            if (ch6BothComplete) {
                StoryScript.ALL_NODES["ch6_all_completed"]?.let { effectiveNode = it }
            }
        }

        // Ensure Cedric is present in party stats from Chapter 1 Camp onward
        if ((effectiveNode.id == "crossroads_camp_trans" || effectiveNode.id.startsWith("camp_") || effectiveNode.id.startsWith("ch3_") || effectiveNode.id.startsWith("ch4_") || effectiveNode.id.startsWith("ch5_") || effectiveNode.id.startsWith("ch6_") || updatedFlags["cedric_recruited"] == true) &&
            updatedPartyStats.none { it.id == "cedric" }
        ) {
            val cedricStats = SavedCharacterStats(
                id = "cedric",
                name = "Sir Cedric",
                loreClass = "Templar",
                currentHp = 310,
                maxHp = 420,
                currentMp = 80,
                maxMp = 80,
                speed = 55,
                level = 1,
                xp = 0,
                spellIds = listOf("holy_smite", "aegis_shield", "radiant_cleave")
            )
            updatedPartyStats = updatedPartyStats + cedricStats
        }

        // When Lyra is recruited, add her to party stats
        if ((effectiveNode.id == "ch6_party_joins" || updatedFlags["lyra_recruited"] == true) &&
            updatedPartyStats.none { it.id == "lyra" }
        ) {
            val lyraStats = SavedCharacterStats(
                id = "lyra",
                name = "Lyra",
                loreClass = "Grove Warden",
                currentHp = 240,
                maxHp = 280,
                currentMp = 120,
                maxMp = 120,
                speed = 65,
                level = 1,
                xp = 0,
                spellIds = listOf("soothing_rain", "briar_entangle", "grounded_mend")
            )
            updatedPartyStats = updatedPartyStats + lyraStats
        }

        val sceneIdToUse = effectiveNode.changeSceneId ?: _state.value.currentScene.id
        val targetScene = StoryScript.ALL_SCENES[sceneIdToUse] ?: _state.value.currentScene

        _state.value = _state.value.copy(
            currentScene = targetScene,
            currentNode = effectiveNode,
            narrativeFlags = updatedFlags,
            partyStats = updatedPartyStats
        )

        persistCurrentState()
        narrateCurrentNode()
    }

    var onOpenOptions: (() -> Unit)? = null
    var onCloseOptions: (() -> Unit)? = null

    fun triggerEncounter(encounterId: String) {
        val encounter = when (encounterId) {
            "prologue_solo" -> StoryEncounters.PROLOGUE_SOLO
            "forest_ambush" -> StoryEncounters.FOREST_AMBUSH
            "blight_trackers" -> StoryEncounters.BLIGHT_TRACKERS
            "ch3_sentinels" -> StoryEncounters.CH3_SENTINELS
            "cave_broodmother" -> StoryEncounters.CAVE_BROODMOTHER
            "dungeon_descent" -> StoryEncounters.DUNGEON_DESCENT
            "castle_horde" -> StoryEncounters.CASTLE_HORDE
            "marsh_rescue" -> StoryEncounters.MARSH_RESCUE
            "swamp_behemoth" -> StoryEncounters.SWAMP_BEHEMOTH
            else -> StoryEncounters.ALL_ENCOUNTERS.firstOrNull { it.id == encounterId } ?: StoryEncounters.PROLOGUE_SOLO
        }
        speechManager.cancel()
        combatNarrator.stop()
        _state.value = _state.value.copy(
            gameScreen = GameScreen.COMBAT_ARENA,
            activeEncounter = encounter
        )
    }

    fun onCombatVictory() {
        val lastNode = _state.value.currentNode
        val encounterId = lastNode.triggerBattleEncounterId ?: _state.value.activeEncounter?.id ?: "unknown"
        val postBattleNodeId = when (encounterId) {
            "prologue_solo" -> "village_post_battle"
            "forest_ambush" -> "crossroads_post_battle"
            "blight_trackers" -> "camp_scout_victory"
            "ch3_sentinels" -> "ch3_sentinels_victory"
            "cave_broodmother" -> "ch3_boss_victory"
            "dungeon_descent" -> "ch4_crypt_victory"
            "castle_horde" -> "ch4_tower_victory"
            "marsh_rescue" -> "ch5_rescue_victory"
            "swamp_behemoth" -> "ch6_willow_purified"
            else -> null
        }

        val updatedDefeated = if (encounterId !in _state.value.defeatedEncounters) {
            _state.value.defeatedEncounters + encounterId
        } else {
            _state.value.defeatedEncounters
        }

        val targetNode = (if (postBattleNodeId != null) StoryScript.ALL_NODES[postBattleNodeId] else null) ?: lastNode

        val updatedFlags = if (targetNode.setFlagOnEnter != null) {
            _state.value.narrativeFlags + (targetNode.setFlagOnEnter to true)
        } else {
            _state.value.narrativeFlags
        }

        val sceneIdToUse = targetNode.changeSceneId ?: _state.value.currentScene.id
        val targetScene = StoryScript.ALL_SCENES[sceneIdToUse] ?: _state.value.currentScene

        _state.value = _state.value.copy(
            gameScreen = GameScreen.STORY_EXPLORATION,
            activeEncounter = null,
            currentScene = targetScene,
            currentNode = targetNode,
            narrativeFlags = updatedFlags,
            defeatedEncounters = updatedDefeated
        )

        persistCurrentState()
        narrateCurrentNode()
    }

    fun updatePartyStatsFromCombat(updatedParty: List<PartyMember>) {
        val mappedStats = updatedParty.map { member ->
            SavedCharacterStats(
                id = member.id,
                name = member.name,
                loreClass = member.loreClass,
                currentHp = member.currentHp,
                maxHp = member.maxHp,
                currentMp = member.currentMp,
                maxMp = member.maxMp,
                speed = member.speed,
                spellIds = member.spells.map { it.id }
            )
        }
        _state.value = _state.value.copy(partyStats = mappedStats)
        persistCurrentState()
    }

    fun persistCurrentState() {
        val s = _state.value
        val currentSave = saveManager.load() ?: GameSaveData()
        val updatedSave = currentSave.copy(
            player = s.player,
            currentSceneId = s.currentScene.id,
            currentNodeId = s.currentNode.id,
            decisionsMade = s.decisionsMade,
            narrativeFlags = s.narrativeFlags,
            partyStats = s.partyStats,
            defeatedEncounters = s.defeatedEncounters,
            achievements = s.achievements,
            isEyesFreeMode = combatNarrator.isEyesFreeMode.value,
            isAutoListen = speechManager.isAutoListen.value,
            isChimeMuted = speechManager.isChimeMuted.value,
            isNarrationEnabled = combatNarrator.isNarrationEnabled.value,
            isReadChoicesEnabled = combatNarrator.isReadChoicesEnabled.value,
            speechRate = combatNarrator.speechRate.value,
            isCharacterPitchEnabled = combatNarrator.isCharacterPitchEnabled.value
        )
        saveManager.save(updatedSave)
    }

    fun switchToCombat() {
        combatNarrator.stop()
        _state.value = _state.value.copy(gameScreen = GameScreen.COMBAT_ARENA)
    }

    fun switchToStory() {
        _state.value = _state.value.copy(gameScreen = GameScreen.STORY_EXPLORATION)
        narrateCurrentNode()
    }

    fun handleStoryVoiceInput(utterance: String) {
        val lower = utterance.lowercase().trim()
        val node = _state.value.currentNode

        // 0. Intercept Meta Voice Commands (Options, Narration, Choice Reading, Pocket Mode)
        val peek = IntentParser.parse(utterance, emptyList(), emptyList(), emptyList())
        when (peek.metaCommand) {
            MetaCommand.OPEN_OPTIONS -> {
                onOpenOptions?.invoke()
                return
            }
            MetaCommand.CLOSE_OPTIONS -> {
                onCloseOptions?.invoke()
                return
            }
            MetaCommand.TOGGLE_NARRATION -> {
                val enabled = combatNarrator.toggleNarration()
                val status = if (enabled) "Story dialogue narration enabled." else "Story dialogue narration muted."
                combatNarrator.speak(status, force = true)
                persistCurrentState()
                return
            }
            MetaCommand.TOGGLE_READ_CHOICES -> {
                val enabled = combatNarrator.toggleReadChoices()
                val status = if (enabled) "Choice reading enabled." else "Choice reading disabled."
                combatNarrator.speak(status, force = true)
                persistCurrentState()
                return
            }
            MetaCommand.TOGGLE_EYES_FREE -> {
                val enabled = combatNarrator.toggleEyesFreeMode()
                val status = if (enabled) "Eyes free mode enabled." else "Eyes free mode disabled."
                combatNarrator.speak(status, force = true)
                persistCurrentState()
                return
            }
            MetaCommand.TOGGLE_AUTO_LISTEN -> {
                speechManager.toggleAutoListen()
                val enabled = speechManager.isAutoListen.value
                val status = if (enabled) "Hands free auto listen enabled." else "Auto listen disabled."
                combatNarrator.speak(status, force = true)
                persistCurrentState()
                return
            }
            MetaCommand.HELP -> {
                combatNarrator.speak("Say 'Next' to advance dialogue. Say a choice keyword to select it. Say 'Options' for settings. Say 'Narration' to toggle dialogue speech. Say 'Read choices' to toggle options reading.", force = true)
                return
            }
            else -> Unit
        }

        // 1. Advance command
        if (lower == "next" || lower == "continue" || lower == "proceed" || lower.contains("go on")) {
            if (node.choices.isEmpty()) {
                advanceDialogue()
                return
            }
        }

        // 2. Choice selection by voice keywords
        if (node.choices.isNotEmpty()) {
            val matchedChoice = node.choices.firstOrNull { choice ->
                choice.voiceKeywords.any { keyword -> lower.contains(keyword.lowercase()) } ||
                        lower.contains(choice.text.lowercase())
            }
            if (matchedChoice != null) {
                if (matchedChoice.completionFlag != null && _state.value.narrativeFlags[matchedChoice.completionFlag] == true) {
                    combatNarrator.speak("That objective has already been completed. Please select a remaining task.", force = true)
                    return
                }
                selectChoice(matchedChoice)
                return
            }
        }

        // 3. Battle trigger command
        if (node.triggerBattleEncounterId != null) {
            if (lower.contains("fight") || lower.contains("battle") || lower.contains("attack") || lower.contains("fireball")) {
                advanceDialogue()
                return
            }
        }
    }

    private fun narrateCurrentNode() {
        if (_state.value.gameScreen != GameScreen.STORY_EXPLORATION) return
        val node = _state.value.currentNode
        val uncompletedChoices = node.choices.filter { choice ->
            choice.completionFlag == null || _state.value.narrativeFlags[choice.completionFlag] != true
        }
        combatNarrator.narrateDialogue(
            speaker = node.speaker,
            text = node.text,
            choices = uncompletedChoices
        ) {
            if (speechManager.isAutoListen.value) {
                activeScope.launch {
                    delay(120)
                    speechManager.startListening { utterance ->
                        handleStoryVoiceInput(utterance)
                    }
                }
            }
        }
    }
}
