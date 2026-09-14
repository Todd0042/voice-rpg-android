package com.voicerpg.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.engine.StoryScript
import com.voicerpg.android.model.DialogueChoice
import com.voicerpg.android.model.DialogueNode
import com.voicerpg.android.model.EncounterDefinition
import com.voicerpg.android.model.GameScreen
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
    val gameScreen: GameScreen = GameScreen.STORY_EXPLORATION,
    val activeEncounter: EncounterDefinition? = null,
    val isNarratorSpeaking: Boolean = false,
    val isTypingComplete: Boolean = true
)

class StoryViewModel(
    val speechManager: SpeechManager,
    val combatNarrator: CombatNarrator,
    private val scopeOverride: CoroutineScope? = null
) : ViewModel() {

    private val activeScope: CoroutineScope
        get() = scopeOverride ?: viewModelScope

    private val _state = MutableStateFlow(StoryState())
    val state: StateFlow<StoryState> = _state.asStateFlow()

    init {
        // Narrate initial scene on load
        narrateCurrentNode()
    }

    fun advanceDialogue() {
        val node = _state.value.currentNode
        if (node.choices.isNotEmpty()) {
            // Cannot blind advance when choices are waiting; player must select a choice
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
            applyNodeTransition(nextNode)
        }
    }

    private fun applyNodeTransition(newNode: DialogueNode) {
        // Check if current or target node triggers scene transition
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
        // Return to exploration mode and progress story
        val lastNode = _state.value.currentNode
        val postBattleNodeId = when (lastNode.triggerBattleEncounterId) {
            "prologue_solo" -> "village_post_battle"
            "forest_ambush" -> "crossroads_post_battle"
            else -> null
        }

        val nextNode = if (postBattleNodeId != null) StoryScript.ALL_NODES[postBattleNodeId] else lastNode

        _state.value = _state.value.copy(
            gameScreen = GameScreen.STORY_EXPLORATION,
            activeEncounter = null,
            currentNode = nextNode ?: lastNode
        )

        narrateCurrentNode()
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
