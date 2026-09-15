package com.voicerpg.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.SaveManager
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.engine.StoryScript
import com.voicerpg.android.model.DialogueChoice
import com.voicerpg.android.model.DialogueNode
import com.voicerpg.android.model.EncounterDefinition
import com.voicerpg.android.model.GameSaveData
import com.voicerpg.android.model.GameScreen
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
            val restoredNode = StoryScript.ALL_NODES[existingSave.currentNodeId] ?: StoryScript.ALL_NODES["cottage_intro"]!!

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
        }
    }

    fun selectChoice(choice: DialogueChoice) {
        val nextNode = StoryScript.ALL_NODES[choice.nextNodeId]
        if (nextNode != null) {
            val updatedDecisions = _state.value.decisionsMade + choice.id
            _state.value = _state.value.copy(decisionsMade = updatedDecisions)
            applyNodeTransition(nextNode)
        }
    }

    private fun applyNodeTransition(newNode: DialogueNode) {
        val sceneIdToUse = newNode.changeSceneId ?: _state.value.currentNode.changeSceneId
        val targetScene = if (sceneIdToUse != null) {
            StoryScript.ALL_SCENES[sceneIdToUse] ?: _state.value.currentScene
        } else {
            _state.value.currentScene
        }

        _state.value = _state.value.copy(
            currentScene = targetScene,
            currentNode = newNode
        )

        persistCurrentState()
        narrateCurrentNode()
    }

    fun triggerEncounter(encounterId: String) {
        val encounter = when (encounterId) {
            "prologue_solo" -> StoryEncounters.PROLOGUE_SOLO
            "forest_ambush" -> StoryEncounters.FOREST_AMBUSH
            else -> StoryEncounters.PROLOGUE_SOLO
        }
        speechManager.cancel()
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
            else -> null
        }

        val updatedDefeated = if (encounterId !in _state.value.defeatedEncounters) {
            _state.value.defeatedEncounters + encounterId
        } else {
            _state.value.defeatedEncounters
        }

        val nextNode = if (postBattleNodeId != null) StoryScript.ALL_NODES[postBattleNodeId] else lastNode

        _state.value = _state.value.copy(
            gameScreen = GameScreen.STORY_EXPLORATION,
            activeEncounter = null,
            currentNode = nextNode ?: lastNode,
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
        val saveData = GameSaveData(
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
            isChimeMuted = speechManager.isChimeMuted.value
        )
        saveManager.save(saveData)
    }

    fun switchToCombat() {
        _state.value = _state.value.copy(gameScreen = GameScreen.COMBAT_ARENA)
    }

    fun switchToStory() {
        _state.value = _state.value.copy(gameScreen = GameScreen.STORY_EXPLORATION)
        narrateCurrentNode()
    }

    fun handleStoryVoiceInput(utterance: String) {
        val lower = utterance.lowercase().trim()
        val node = _state.value.currentNode

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
        val textToSpeak = "${node.speaker.name}: ${node.text}"

        combatNarrator.speak(textToSpeak, force = combatNarrator.isEyesFreeMode.value) {
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
