package com.voicerpg.android.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.IntentParser
import com.voicerpg.android.engine.SaveManager
import com.voicerpg.android.engine.StoryChoiceMatcher
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.engine.StoryScript
import com.voicerpg.android.model.DialogueChoice
import com.voicerpg.android.model.DialogueLogEntry
import com.voicerpg.android.model.DialogueNode
import com.voicerpg.android.model.DialogueSpeaker
import com.voicerpg.android.model.EncounterDefinition
import com.voicerpg.android.model.GameSaveData
import com.voicerpg.android.model.GameScreen
import com.voicerpg.android.model.MetaCommand
import com.voicerpg.android.model.PartyMember
import com.voicerpg.android.model.PlayerCustomization
import com.voicerpg.android.model.SaveSummary
import com.voicerpg.android.model.SavedCharacterStats
import com.voicerpg.android.model.StoryScene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoryState(
    val currentScene: StoryScene = StoryScript.SCENE_COTTAGE,
    val currentNode: DialogueNode = StoryScript.ALL_NODES["cottage_intro"]!!,
    val gameScreen: GameScreen = GameScreen.TITLE,
    val previousScreen: GameScreen? = null,
    val activeEncounter: EncounterDefinition? = null,
    val player: PlayerCustomization = PlayerCustomization(),
    val decisionsMade: List<String> = emptyList(),
    val narrativeFlags: Map<String, Boolean> = emptyMap(),
    val defeatedEncounters: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val partyStats: List<SavedCharacterStats> = emptyList(),
    val isNarratorSpeaking: Boolean = false,
    val isTypingComplete: Boolean = true,
    val hasExistingSave: Boolean = false,
    val saveSummary: SaveSummary? = null,
    val dialogueHistory: List<DialogueLogEntry> = emptyList(),
    val isBacklogOpen: Boolean = false,
    val isFastForwarding: Boolean = false
)

class StoryViewModel(
    val speechManager: SpeechManager,
    val combatNarrator: CombatNarrator,
    val saveManager: SaveManager = SaveManager(),
    val musicManager: com.voicerpg.android.audio.MusicManager? = null,
    private val scopeOverride: CoroutineScope? = null
) : ViewModel() {

    data class DebugChapterTarget(
        val label: String,
        val introNodeId: String,
        val chapterIndex: Int
    )

    companion object {
        val DEBUG_CHAPTER_TARGETS: List<DebugChapterTarget> = listOf(
            DebugChapterTarget("Prologue", "cottage_intro", 0),
            DebugChapterTarget("Chapter 1", "crossroads_intro", 1),
            DebugChapterTarget("Chapter 2", "camp_intro", 2),
            DebugChapterTarget("Chapter 3", "chapter3_intro", 3),
            DebugChapterTarget("Chapter 4", "chapter4_intro", 4),
            DebugChapterTarget("Chapter 5", "ch5_intro", 5),
            DebugChapterTarget("Chapter 6", "ch6_intro", 6),
            DebugChapterTarget("Chapter 7", "ch7_intro", 7),
            DebugChapterTarget("Chapter 8", "ch8_intro", 8),
            DebugChapterTarget("Chapter 9", "ch9_intro", 9),
            DebugChapterTarget("Chapter 10", "ch10_intro", 10),
            DebugChapterTarget("Chapter 11", "ch11_intro", 11),
            DebugChapterTarget("Chapter 12", "ch12_intro", 12),
            DebugChapterTarget("Chapter 13", "ch13_intro", 13),
            DebugChapterTarget("Chapter 14", "ch14_intro", 14),
            DebugChapterTarget("Chapter 15", "ch15_intro", 15),
            DebugChapterTarget("Chapter 16", "ch16_intro", 16)
        )
    }

    private val activeScope: CoroutineScope
        get() = scopeOverride ?: viewModelScope

    private val _state = MutableStateFlow(StoryState())
    val state: StateFlow<StoryState> = _state.asStateFlow()

    private var pendingAutoAdvanceJob: Job? = null

    fun cancelPendingAutoAdvance() {
        pendingAutoAdvanceJob?.cancel()
        pendingAutoAdvanceJob = null
    }

    private fun eligibleChoices(node: DialogueNode): List<DialogueChoice> =
        node.choices.filter { choice ->
            choice.completionFlag == null || _state.value.narrativeFlags[choice.completionFlag] != true
        }

    private fun effectiveDialogueChoices(node: DialogueNode): List<DialogueChoice> {
        val eligible = eligibleChoices(node)
        return if (node.choices.isNotEmpty() && eligible.size <= 1) emptyList() else node.choices
    }

    fun canAdvanceDialogue(): Boolean {
        val node = _state.value.currentNode
        if (effectiveDialogueChoices(node).isNotEmpty()) return false
        if (node.triggerBattleEncounterId != null) return false
        if (node.choices.isNotEmpty()) return true
        if (node.nextNodeId != null) return StoryScript.ALL_NODES.containsKey(node.nextNodeId)
        if (node.id.startsWith("ch3_") || node.id.startsWith("ch4_") || node.id.startsWith("ch5_") ||
            node.id.startsWith("ch6_") || node.id.startsWith("ch7_") || node.id.startsWith("ch8_") ||
            node.id.startsWith("ch9_") || node.id.startsWith("ch10_") || node.id.startsWith("ch11_") ||
            node.id.startsWith("ch12_") || node.id.startsWith("ch13_") || node.id.startsWith("ch14_") ||
            node.id.startsWith("ch15_") || node.id.startsWith("ch16_") || node.id.startsWith("epilogue_") ||
            node.id == "ch4_act1_complete"
        ) {
            return false
        }
        return StoryScript.ALL_NODES.containsKey("camp_intro") || StoryScript.ALL_NODES.containsKey("crossroads_intro")
    }

    fun schedulePocketModeAutoAdvance(node: DialogueNode) {
        cancelPendingAutoAdvance()
        if (!combatNarrator.isEyesFreeMode.value || !canAdvanceDialogue()) return
        pendingAutoAdvanceJob = activeScope.launch {
            delay(1500)
            if (combatNarrator.isEyesFreeMode.value &&
                _state.value.currentNode.id == node.id &&
                _state.value.gameScreen == GameScreen.STORY_EXPLORATION &&
                canAdvanceDialogue()
            ) {
                advanceDialogue()
            }
        }
    }

    init {
        // Observe Screenless Pocket Mode toggles: cancel or start auto-advance reactively
        activeScope.launch {
            combatNarrator.isEyesFreeMode.collect { isEyesFree ->
                if (!isEyesFree) {
                    cancelPendingAutoAdvance()
                } else if (_state.value.gameScreen == GameScreen.STORY_EXPLORATION && canAdvanceDialogue() && !combatNarrator.isSpeaking.value) {
                    schedulePocketModeAutoAdvance(_state.value.currentNode)
                }
            }
        }

        // Load persistent game save on boot
        val existingSave = saveManager.load()
        if (existingSave != null) {
            val restoredScene = StoryScript.ALL_SCENES[existingSave.currentSceneId] ?: StoryScript.SCENE_COTTAGE
            val rawRestoredNode = StoryScript.ALL_NODES[existingSave.currentNodeId] ?: StoryScript.ALL_NODES["cottage_intro"]!!
            val restoredNode = resolveEffectiveHubNode(rawRestoredNode, existingSave.narrativeFlags)

            val summary = SaveSummary(
                heroName = existingSave.player.name,
                heroClassTitle = existingSave.player.heroClass.title,
                chapterTitle = restoredScene.chapterTitle,
                sceneName = restoredScene.name,
                partySize = existingSave.partyStats.size.coerceAtLeast(1),
                timestamp = existingSave.saveTimestamp
            )

            val restoredHistory = if (existingSave.recentDialogueLog.isNotEmpty()) {
                existingSave.recentDialogueLog
            } else {
                listOf(
                    DialogueLogEntry(
                        speakerId = restoredNode.speaker.id,
                        speakerName = restoredNode.speaker.name,
                        speakerTitle = restoredNode.speaker.title,
                        text = restoredNode.text,
                        sceneName = restoredScene.name,
                        isNarrator = restoredNode.speaker == DialogueSpeaker.NARRATOR
                    )
                )
            }

            _state.value = StoryState(
                currentScene = restoredScene,
                currentNode = restoredNode,
                gameScreen = GameScreen.TITLE,
                player = existingSave.player,
                decisionsMade = existingSave.decisionsMade,
                narrativeFlags = existingSave.narrativeFlags,
                defeatedEncounters = existingSave.defeatedEncounters,
                achievements = existingSave.achievements,
                partyStats = existingSave.partyStats,
                hasExistingSave = true,
                saveSummary = summary,
                dialogueHistory = restoredHistory
            )
            combatNarrator.setEyesFreeMode(existingSave.isEyesFreeMode)
            combatNarrator.setNarrationEnabled(existingSave.isNarrationEnabled)
            combatNarrator.setReadChoicesEnabled(existingSave.isReadChoicesEnabled)
            combatNarrator.setSpeechRate(existingSave.speechRate)
            combatNarrator.setCharacterPitchEnabled(existingSave.isCharacterPitchEnabled)
            combatNarrator.setSpeakerAttributionEnabled(existingSave.isSpeakerAttributionEnabled)
            combatNarrator.setVoiceAssignments(existingSave.voiceAssignments)
            speechManager.setAutoListen(existingSave.isAutoListen)
            speechManager.setChimeMuted(existingSave.isChimeMuted)
            musicManager?.setMusicEnabled(existingSave.isMusicEnabled)
            musicManager?.setVolume(existingSave.musicVolume)
            musicManager?.playTrack(com.voicerpg.android.audio.MusicManager.TRACK_ACT1_FOREST)
        } else {
            // First time player: start at Title Screen
            musicManager?.playTrack(com.voicerpg.android.audio.MusicManager.TRACK_ACT1_FOREST)
            _state.value = StoryState(
                gameScreen = GameScreen.TITLE,
                hasExistingSave = false,
                saveSummary = null
            )
        }
    }

    /**
     * Resumes the active game save from the Title Screen.
     */
    fun continueGame() {
        val existingSave = saveManager.load() ?: return
        val restoredScene = StoryScript.ALL_SCENES[existingSave.currentSceneId] ?: StoryScript.SCENE_COTTAGE
        val rawRestoredNode = StoryScript.ALL_NODES[existingSave.currentNodeId] ?: StoryScript.ALL_NODES["cottage_intro"]!!
        val restoredNode = resolveEffectiveHubNode(rawRestoredNode, existingSave.narrativeFlags)

        val restoredHistory = if (existingSave.recentDialogueLog.isNotEmpty()) {
            existingSave.recentDialogueLog
        } else if (_state.value.dialogueHistory.isNotEmpty()) {
            _state.value.dialogueHistory
        } else {
            listOf(
                DialogueLogEntry(
                    speakerId = restoredNode.speaker.id,
                    speakerName = restoredNode.speaker.name,
                    speakerTitle = restoredNode.speaker.title,
                    text = restoredNode.text,
                    sceneName = restoredScene.name,
                    isNarrator = restoredNode.speaker == DialogueSpeaker.NARRATOR
                )
            )
        }

        _state.value = _state.value.copy(
            currentScene = restoredScene,
            currentNode = restoredNode,
            gameScreen = GameScreen.STORY_EXPLORATION,
            previousScreen = null,
            player = existingSave.player,
            decisionsMade = existingSave.decisionsMade,
            narrativeFlags = existingSave.narrativeFlags,
            defeatedEncounters = existingSave.defeatedEncounters,
            achievements = existingSave.achievements,
            partyStats = existingSave.partyStats,
            dialogueHistory = restoredHistory
        )
        musicManager?.playTrack(restoredScene.musicAsset)
        narrateCurrentNode()
    }

    /**
     * Starts the new game wizard flow from the Title Screen.
     */
    fun startNewGameFlow() {
        cancelPendingAutoAdvance()
        combatNarrator.stop()
        speechManager.cancel()
        _state.value = _state.value.copy(
            gameScreen = if (_state.value.hasExistingSave) GameScreen.CHARACTER_CREATION else GameScreen.AUDIO_SETUP,
            previousScreen = GameScreen.TITLE
        )
    }

    /**
     * Saves current campaign progress and returns to the Title Screen.
     */
    fun returnToTitle() {
        cancelPendingAutoAdvance()
        combatNarrator.stop()
        speechManager.cancel()
        persistCurrentState()
        val latestSave = saveManager.load()
        val summary = latestSave?.let { save ->
            val scene = StoryScript.ALL_SCENES[save.currentSceneId] ?: StoryScript.SCENE_COTTAGE
            SaveSummary(
                heroName = save.player.name,
                heroClassTitle = save.player.heroClass.title,
                chapterTitle = scene.chapterTitle,
                sceneName = scene.name,
                partySize = save.partyStats.size.coerceAtLeast(1),
                timestamp = save.saveTimestamp
            )
        }
        _state.value = _state.value.copy(
            gameScreen = GameScreen.TITLE,
            previousScreen = null,
            hasExistingSave = latestSave != null,
            saveSummary = summary
        )
        musicManager?.playTrack(com.voicerpg.android.audio.MusicManager.TRACK_ACT1_FOREST)
    }

    /**
     * Proceeds from initial Audio Setup to Character Creation.
     */
    fun proceedToCharacterCreation() {
        _state.value = _state.value.copy(
            gameScreen = GameScreen.CHARACTER_CREATION
        )
    }

    /**
     * Initializes a fresh game from Character Creation.
     */
    fun startNewGame(customization: PlayerCustomization) {
        cancelPendingAutoAdvance()
        val initialSave = saveManager.createInitialSave(customization)
        val initialNode = StoryScript.ALL_NODES["cottage_intro"]!!
        val initialScene = StoryScript.SCENE_COTTAGE
        val initialEntry = DialogueLogEntry(
            speakerId = initialNode.speaker.id,
            speakerName = initialNode.speaker.name,
            speakerTitle = initialNode.speaker.title,
            text = initialNode.text,
            sceneName = initialScene.name,
            isNarrator = initialNode.speaker == DialogueSpeaker.NARRATOR
        )
        _state.value = StoryState(
            currentScene = initialScene,
            currentNode = initialNode,
            gameScreen = GameScreen.STORY_EXPLORATION,
            player = customization,
            decisionsMade = emptyList(),
            narrativeFlags = emptyMap(),
            defeatedEncounters = emptyList(),
            achievements = initialSave.achievements,
            partyStats = initialSave.partyStats,
            hasExistingSave = true,
            dialogueHistory = listOf(initialEntry)
        )
        musicManager?.playTrack(StoryScript.SCENE_COTTAGE.musicAsset)
        persistCurrentState()
        narrateCurrentNode()
    }

    fun resetGame() {
        cancelPendingAutoAdvance()
        saveManager.deleteSave()
        _state.value = StoryState(
            gameScreen = GameScreen.TITLE,
            hasExistingSave = false,
            saveSummary = null
        )
    }

    /**
     * Debug-only helper: instantly jumps to a chapter's intro node, seeding the
     * companion roster and master spells valid as of that chapter. Only exposed
     * through the debug-build Options dialog (see BuildConfig.DEBUG_WARP_MENU).
     */
    fun debugWarpToChapter(introNodeId: String) {
        val target = DEBUG_CHAPTER_TARGETS.firstOrNull { it.introNodeId == introNodeId } ?: return
        val introNode = StoryScript.ALL_NODES[introNodeId] ?: return
        cancelPendingAutoAdvance()
        combatNarrator.stop()
        speechManager.cancel()

        val seededFlags = buildMap {
            if (target.chapterIndex >= 2) put("cedric_recruited", true)
            if (target.chapterIndex >= 6) put("lyra_recruited", true)
            if (target.chapterIndex >= 9) put("zephyr_recruited", true)
            if (target.chapterIndex >= 10) put("cedric_trial_complete", true)
            if (target.chapterIndex >= 11) put("lyra_trial_complete", true)
            if (target.chapterIndex >= 12) put("zephyr_trial_complete", true)
        }
        // Overwrite, not union, the roster and trial flags so a downward warp
        // cannot drag companions from later chapters back into the story.
        val rosterAndTrialFlags = setOf(
            "cedric_recruited", "lyra_recruited", "zephyr_recruited",
            "cedric_trial_complete", "lyra_trial_complete", "zephyr_trial_complete"
        )
        val clearedFlags = _state.value.narrativeFlags.filterKeys { it !in rosterAndTrialFlags }

        // Trim any companions who have not joined the fellowship as of this chapter.
        val allowedRosterIds = buildList {
            add("hero")
            if (target.chapterIndex >= 2) add("cedric")
            if (target.chapterIndex >= 6) add("lyra")
            if (target.chapterIndex >= 9) add("zephyr")
        }
        val trimmedParty = _state.value.partyStats.filter { it.id in allowedRosterIds }

        val introScene = StoryScript.ALL_SCENES.values.firstOrNull { it.initialNodeId == introNodeId }
            ?: _state.value.currentScene

        _state.value = _state.value.copy(
            currentScene = introScene,
            currentNode = introNode,
            gameScreen = GameScreen.STORY_EXPLORATION,
            activeEncounter = null,
            narrativeFlags = clearedFlags + seededFlags,
            partyStats = trimmedParty
        )
        musicManager?.playTrack(introScene.musicAsset)
        applyNodeTransition(introNode)
    }

    fun advanceDialogue() {
        if (_state.value.isFastForwarding) {
            stopFastForward()
            return
        }
        advanceDialogueInternal(suppressNarration = false)
    }

    private fun advanceDialogueInternal(suppressNarration: Boolean = false) {
        cancelPendingAutoAdvance()
        if (!suppressNarration) {
            combatNarrator.stop()
            speechManager.cancel()
        }
        val node = _state.value.currentNode
        if (effectiveDialogueChoices(node).isNotEmpty()) {
            return
        }

        if (node.choices.isNotEmpty()) {
            val sole = eligibleChoices(node).firstOrNull()
            if (sole != null) {
                val nextNode = StoryScript.ALL_NODES[sole.nextNodeId]
                if (nextNode != null) {
                    val updatedDecisions = _state.value.decisionsMade + sole.id
                    _state.value = _state.value.copy(decisionsMade = updatedDecisions)
                    applyNodeTransition(nextNode, suppressNarration = suppressNarration)
                }
            }
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
                applyNodeTransition(nextNode, suppressNarration = suppressNarration)
            }
        } else if (node.choices.isEmpty()) {
            if (node.id == "epilogue_credits" || node.setFlagOnEnter == "game_completed") {
                resetGame()
                return
            }
            if (node.id.startsWith("ch3_") || node.id.startsWith("ch4_") || node.id.startsWith("ch5_") || node.id.startsWith("ch6_") || node.id.startsWith("ch7_") || node.id.startsWith("ch8_") || node.id.startsWith("ch9_") || node.id.startsWith("ch10_") || node.id.startsWith("ch11_") || node.id.startsWith("ch12_") || node.id.startsWith("ch13_") || node.id.startsWith("ch14_") || node.id.startsWith("ch15_") || node.id.startsWith("ch16_") || node.id.startsWith("epilogue_") || node.id == "ch4_act1_complete") {
                return
            }
            val fallbackNode = StoryScript.ALL_NODES["camp_intro"] ?: StoryScript.ALL_NODES["crossroads_intro"]
            if (fallbackNode != null) {
                applyNodeTransition(fallbackNode, suppressNarration = suppressNarration)
            }
        }
    }

    fun selectChoice(choice: DialogueChoice) {
        if (_state.value.isFastForwarding) {
            stopFastForward()
        }
        cancelPendingAutoAdvance()
        if (choice.completionFlag != null && _state.value.narrativeFlags[choice.completionFlag] == true) {
            combatNarrator.speak("That objective has already been completed. Please select a remaining task.", force = true)
            return
        }

        combatNarrator.stop()
        speechManager.cancel()
        val nextNode = StoryScript.ALL_NODES[choice.nextNodeId]
        if (nextNode != null) {
            val updatedDecisions = _state.value.decisionsMade + choice.id
            _state.value = _state.value.copy(decisionsMade = updatedDecisions)
            applyNodeTransition(nextNode)
        }
    }

    private fun applyNodeTransition(newNode: DialogueNode, suppressNarration: Boolean = false) {
        val updatedFlags = if (newNode.setFlagOnEnter != null) {
            _state.value.narrativeFlags + (newNode.setFlagOnEnter to true)
        } else {
            _state.value.narrativeFlags
        }

        var effectiveNode = resolveEffectiveHubNode(newNode, updatedFlags)

        var updatedPartyStats = _state.value.partyStats
        if (newNode.setFlagOnEnter?.endsWith("_rest_complete") == true) {
            val isStoryMilestoneRest = newNode.setFlagOnEnter == "substory_rest_complete"
            updatedPartyStats = if (isStoryMilestoneRest) {
                // Camp Midnight Vigil milestone: the one true full restoration.
                updatedPartyStats.map { member ->
                    member.copy(currentHp = member.maxHp, currentMp = member.maxMp)
                }
            } else {
                // Ordinary camp rest: recover HALF of missing HP/MP (attrition keeps healing
                // and caution meaningful); a fallen comrade is revived at half vitals.
                updatedPartyStats.map { member ->
                    val missingHp = (member.maxHp - member.currentHp).coerceAtLeast(0)
                    val missingMp = (member.maxMp - member.currentMp).coerceAtLeast(0)
                    member.copy(
                        currentHp = if (member.currentHp <= 0) member.maxHp / 2 else (member.currentHp + missingHp / 2).coerceAtMost(member.maxHp),
                        currentMp = if (member.currentMp <= 0) member.maxMp / 2 else (member.currentMp + missingMp / 2).coerceAtMost(member.maxMp)
                    )
                }
            }
        }

        // Ensure Cedric is present in party stats from Chapter 1 Camp onward
        val isCedricPostRecruitNode = effectiveNode.id == "crossroads_camp_trans" ||
            effectiveNode.id.startsWith("camp_") || effectiveNode.id.startsWith("ch3_") ||
            effectiveNode.id.startsWith("ch4_") || effectiveNode.id.startsWith("ch5_") ||
            effectiveNode.id.startsWith("ch6_") || effectiveNode.id.startsWith("ch7_") ||
            effectiveNode.id.startsWith("ch8_") || effectiveNode.id.startsWith("ch9_") ||
            effectiveNode.id.startsWith("ch10_") || effectiveNode.id.startsWith("ch11_") ||
            effectiveNode.id.startsWith("ch12_") || effectiveNode.id.startsWith("ch13_") ||
            effectiveNode.id.startsWith("ch14_") || effectiveNode.id.startsWith("ch15_") ||
            effectiveNode.id.startsWith("ch16_") || effectiveNode.id.startsWith("epilogue_") ||
            updatedFlags["cedric_recruited"] == true
        if (isCedricPostRecruitNode && updatedPartyStats.none { it.id == "cedric" }) {
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
                spellIds = listOf("holy_smite", "lay_on_hands", "shield_wall")
            )
            updatedPartyStats = updatedPartyStats + cedricStats
        }

        // When Lyra is recruited, add her to party stats
        val isLyraPostRecruitNode = effectiveNode.id == "ch6_party_joins" || updatedFlags["lyra_recruited"] == true ||
            effectiveNode.id.startsWith("ch6_") || effectiveNode.id.startsWith("ch7_") ||
            effectiveNode.id.startsWith("ch8_") || effectiveNode.id.startsWith("ch9_") ||
            effectiveNode.id.startsWith("ch10_") || effectiveNode.id.startsWith("ch11_") ||
            effectiveNode.id.startsWith("ch12_") || effectiveNode.id.startsWith("ch13_") ||
            effectiveNode.id.startsWith("ch14_") || effectiveNode.id.startsWith("ch15_") ||
            effectiveNode.id.startsWith("ch16_") || effectiveNode.id.startsWith("epilogue_")
        if (isLyraPostRecruitNode && updatedPartyStats.none { it.id == "lyra" }) {
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
                spellIds = listOf("soothing_rain", "briar_entangle")
            )
            updatedPartyStats = updatedPartyStats + lyraStats
        }

        // When Zephyr is recruited (post-ambush in Ch 8 or Ch 9 onward), add him to party stats
        val isZephyrPostRecruitNode = effectiveNode.id == "ch8_executioner_victory" || effectiveNode.id == "ch8_hub" ||
            effectiveNode.id.startsWith("ch8_motives") || effectiveNode.id.startsWith("ch8_map") ||
            effectiveNode.id.startsWith("ch8_herbs") || effectiveNode.id.startsWith("ch8_all") ||
            effectiveNode.id.startsWith("ch9_") || effectiveNode.id.startsWith("ch10_") ||
            effectiveNode.id.startsWith("ch11_") || effectiveNode.id.startsWith("ch12_") ||
            effectiveNode.id.startsWith("ch13_") || effectiveNode.id.startsWith("ch14_") ||
            effectiveNode.id.startsWith("ch15_") || effectiveNode.id.startsWith("ch16_") ||
            effectiveNode.id.startsWith("epilogue_")
        if ((updatedFlags["zephyr_recruited"] == true || isZephyrPostRecruitNode) &&
            updatedPartyStats.none { it.id == "zephyr" }
        ) {
            val zephyrStats = SavedCharacterStats(
                id = "zephyr",
                name = "Zephyr",
                loreClass = "Shadowblade",
                currentHp = 250,
                maxHp = 250,
                currentMp = 90,
                maxMp = 90,
                speed = 85,
                level = 1,
                xp = 0,
                spellIds = listOf("shadow_strike", "venom_flurry")
            )
            updatedPartyStats = updatedPartyStats + zephyrStats
        }

        // Companion master spells awarded on trial completion
        if (updatedFlags["cedric_trial_complete"] == true) {
            updatedPartyStats = updatedPartyStats.map { member ->
                if (member.id == "cedric" && !member.spellIds.contains("aegis_dawn")) {
                    member.copy(spellIds = member.spellIds + "aegis_dawn")
                } else member
            }
        }
        if (updatedFlags["lyra_trial_complete"] == true) {
            updatedPartyStats = updatedPartyStats.map { member ->
                if (member.id == "lyra" && !member.spellIds.contains("verdant_cataclysm")) {
                    member.copy(spellIds = member.spellIds + "verdant_cataclysm")
                } else member
            }
        }
        if (updatedFlags["zephyr_trial_complete"] == true) {
            updatedPartyStats = updatedPartyStats.map { member ->
                if (member.id == "zephyr" && !member.spellIds.contains("umbral_siphon")) {
                    member.copy(spellIds = member.spellIds + "umbral_siphon")
                } else member
            }
        }

        val sceneIdToUse = effectiveNode.changeSceneId ?: _state.value.currentScene.id
        val targetScene = StoryScript.ALL_SCENES[sceneIdToUse] ?: _state.value.currentScene

        val entry = DialogueLogEntry(
            speakerId = effectiveNode.speaker.id,
            speakerName = effectiveNode.speaker.name,
            speakerTitle = effectiveNode.speaker.title,
            text = effectiveNode.text,
            sceneName = targetScene.name,
            isNarrator = effectiveNode.speaker == DialogueSpeaker.NARRATOR
        )
        val updatedHistory = (_state.value.dialogueHistory + entry).takeLast(100)

        _state.value = _state.value.copy(
            currentScene = targetScene,
            currentNode = effectiveNode,
            narrativeFlags = updatedFlags,
            partyStats = updatedPartyStats,
            dialogueHistory = updatedHistory
        )

        musicManager?.playTrack(targetScene.musicAsset)
        persistCurrentState()
        if (!suppressNarration) {
            narrateCurrentNode()
        }
    }

    private fun resolveEffectiveHubNode(node: DialogueNode, flags: Map<String, Boolean>): DialogueNode {
        var effective = node
        if (effective.id == "camp_intro" || effective.id == "camp_hub") {
            val allThree = flags["substory_blight_complete"] == true &&
                    flags["substory_towers_complete"] == true &&
                    flags["substory_rest_complete"] == true
            if (allThree) StoryScript.ALL_NODES["camp_all_completed"]?.let { effective = it }
        }
        if (effective.id == "chapter3_intro" || effective.id == "ch3_hub") {
            if (flags["ch3_sentinels_complete"] == true && flags["ch3_chime_complete"] == true) {
                StoryScript.ALL_NODES["ch3_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch5_intro" || effective.id == "ch5_hub") {
            if (flags["ch5_creek_scouted"] == true && flags["ch5_wards_examined"] == true) {
                StoryScript.ALL_NODES["ch5_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch6_intro" || effective.id == "ch6_hub") {
            if (flags["ch6_lore_complete"] == true && flags["ch6_spores_complete"] == true) {
                StoryScript.ALL_NODES["ch6_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch7_intro" || effective.id == "ch7_hub") {
            if (flags["ch7_tuning_complete"] == true && flags["ch7_stele_complete"] == true && flags["ch7_cedric_complete"] == true && flags["ch7_dagger_complete"] == true) {
                StoryScript.ALL_NODES["ch7_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch7_camp_intro" || effective.id == "ch7_camp_hub") {
            if (flags["ch7_camp_lyra_complete"] == true && flags["ch7_camp_cedric_complete"] == true && flags["ch7_camp_watch_complete"] == true) {
                StoryScript.ALL_NODES["ch7_camp_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch8_scout_hub") {
            if (flags["ch8_wire_complete"] == true && flags["ch8_herbs_scout_complete"] == true) {
                StoryScript.ALL_NODES["ch8_scout_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch8_hub") {
            if (flags["ch8_motives_complete"] == true && flags["ch8_map_complete"] == true && flags["ch8_herbs_complete"] == true) {
                StoryScript.ALL_NODES["ch8_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch9_intro" || effective.id == "ch9_hub") {
            if (flags["ch9_knights_complete"] == true && flags["ch9_altar_complete"] == true && flags["ch9_reliquary_complete"] == true) {
                StoryScript.ALL_NODES["ch9_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch10_intro" || effective.id == "ch10_hub") {
            if (flags["ch10_dryads_complete"] == true && flags["ch10_seed_complete"] == true && flags["ch10_hymn_complete"] == true) {
                StoryScript.ALL_NODES["ch10_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch11_intro" || effective.id == "ch11_hub") {
            if (flags["ch11_traps_complete"] == true && flags["ch11_vials_complete"] == true && flags["ch11_doctrine_complete"] == true) {
                StoryScript.ALL_NODES["ch11_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch12_intro" || effective.id == "ch12_hub") {
            if (flags["ch12_valves_complete"] == true && flags["ch12_cogs_complete"] == true && flags["ch12_grimoire_complete"] == true) {
                StoryScript.ALL_NODES["ch12_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch13_intro" || effective.id == "ch13_hub") {
            if (flags["ch13_gate_complete"] == true && flags["ch13_seal_complete"] == true && flags["ch13_banners_complete"] == true) {
                StoryScript.ALL_NODES["ch13_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch14_intro" || effective.id == "ch14_hub") {
            if (flags["ch14_archons_complete"] == true && flags["ch14_eddies_complete"] == true && flags["ch14_rites_complete"] == true) {
                StoryScript.ALL_NODES["ch14_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch15_intro" || effective.id == "ch15_hub") {
            if (flags["ch15_cedric_complete"] == true && flags["ch15_lyra_complete"] == true && flags["ch15_zephyr_complete"] == true && flags["ch15_bell_complete"] == true) {
                StoryScript.ALL_NODES["ch15_all_completed"]?.let { effective = it }
            }
        }
        return effective
    }

    var onOpenOptions: (() -> Unit)? = null
    var onCloseOptions: (() -> Unit)? = null

    fun triggerEncounter(encounterId: String) {
        cancelPendingAutoAdvance()
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
            "ch7_mire_wyrm" -> StoryEncounters.CH7_MIRE_WYRM
            "ch8_executioner_ambush" -> StoryEncounters.CH8_EXECUTIONER_AMBUSH
            "ch9_galahault_trial" -> StoryEncounters.CH9_GALAHAULT_TRIAL
            "ch10_broodmother_trial" -> StoryEncounters.CH10_BROODMOTHER_TRIAL
            "ch11_nocturne_trial" -> StoryEncounters.CH11_NOCTURNE_TRIAL
            "ch12_warmaster_ouros" -> StoryEncounters.CH12_WARMASTER_OUROS
            "ch13_commander_vaelor" -> StoryEncounters.CH13_COMMANDER_VAELOR
            "ch14_abyssal_leviathan" -> StoryEncounters.CH14_ABYSSAL_LEVIATHAN
            "ch15_archon_custodians" -> StoryEncounters.CH15_ARCHON_CUSTODIANS
            "ch16_malakor_finale" -> StoryEncounters.CH16_MALAKOR_FINALE
            "ch9_penitent_gate" -> StoryEncounters.CH9_PENITENT_GATE
            "ch10_thicket_guardians" -> StoryEncounters.CH10_THICKET_GUARDIANS
            "ch11_gorge_stalkers" -> StoryEncounters.CH11_GORGE_STALKERS
            "ch11_archive_enforcers" -> StoryEncounters.CH11_ARCHIVE_ENFORCERS
            "ch12_automaton_patrol" -> StoryEncounters.CH12_AUTOMATON_PATROL
            "ch13_plaza_legion" -> StoryEncounters.CH13_PLAZA_LEGION
            "ch14_shallows_husks" -> StoryEncounters.CH14_SHALLOWS_HUSKS
            "ch14_trench_leeches" -> StoryEncounters.CH14_TRENCH_LEECHES
            "ch15_trial_gold" -> StoryEncounters.CH15_TRIAL_GOLD
            "ch15_trial_grove" -> StoryEncounters.CH15_TRIAL_GROVE
            "ch15_trial_shadow" -> StoryEncounters.CH15_TRIAL_SHADOW
            "ch16_nullifier_gate" -> StoryEncounters.CH16_NULLIFIER_GATE
            "ch16_mirror_gauntlet" -> StoryEncounters.CH16_MIRROR_GAUNTLET
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
        cancelPendingAutoAdvance()
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
            "ch7_mire_wyrm" -> "ch7_wyrm_victory"
            "ch8_executioner_ambush" -> "ch8_executioner_victory"
            "ch9_galahault_trial" -> "ch9_galahault_victory"
            "ch10_broodmother_trial" -> "ch10_broodmother_victory"
            "ch11_nocturne_trial" -> "ch11_nocturne_victory"
            "ch12_warmaster_ouros" -> "ch12_warmaster_victory"
            "ch13_commander_vaelor" -> "ch13_vaelor_victory"
            "ch14_abyssal_leviathan" -> "ch14_leviathan_victory"
            "ch15_archon_custodians" -> "ch15_custodians_victory"
            "ch16_malakor_finale" -> "ch16_malakor_victory"
            "ch9_penitent_gate" -> "ch9_penitent_victory"
            "ch10_thicket_guardians" -> "ch10_thicket_victory"
            "ch11_gorge_stalkers" -> "ch11_first_strike_victory"
            "ch11_archive_enforcers" -> "ch11_archive_victory"
            "ch12_automaton_patrol" -> "ch12_patrol_victory"
            "ch13_plaza_legion" -> "ch13_plaza_victory"
            "ch14_shallows_husks" -> "ch14_shallows_victory"
            "ch14_trench_leeches" -> "ch14_trench_victory"
            "ch15_trial_gold" -> "ch15_trial_gold_victory"
            "ch15_trial_grove" -> "ch15_trial_grove_victory"
            "ch15_trial_shadow" -> "ch15_trial_shadow_victory"
            "ch16_nullifier_gate" -> "ch16_vest_victory"
            "ch16_mirror_gauntlet" -> "ch16_mirror_victory"
            else -> null
        }

        val updatedDefeated = if (encounterId !in _state.value.defeatedEncounters) {
            _state.value.defeatedEncounters + encounterId
        } else {
            _state.value.defeatedEncounters
        }

        val targetNode = (if (postBattleNodeId != null) StoryScript.ALL_NODES[postBattleNodeId] else null) ?: lastNode

        _state.value = _state.value.copy(
            gameScreen = GameScreen.STORY_EXPLORATION,
            activeEncounter = null,
            defeatedEncounters = updatedDefeated
        )

        applyNodeTransition(targetNode)
    }

    fun updatePartyStatsFromCombat(updatedParty: List<PartyMember>) {
        val mappedStats = updatedParty.map { member ->
            val existing = _state.value.partyStats.firstOrNull { it.id == member.id }
            val mergedSpells = (member.spells.map { it.id } + (existing?.spellIds ?: emptyList())).distinct()
            SavedCharacterStats(
                id = member.id,
                name = member.name,
                loreClass = member.loreClass,
                currentHp = member.currentHp,
                maxHp = member.maxHp,
                currentMp = member.currentMp,
                maxMp = member.maxMp,
                speed = member.speed,
                level = member.level,
                xp = member.xp,
                spellIds = mergedSpells
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
            recentDialogueLog = s.dialogueHistory.takeLast(60),
            isEyesFreeMode = combatNarrator.isEyesFreeMode.value,
            isAutoListen = speechManager.isAutoListen.value,
            isChimeMuted = speechManager.isChimeMuted.value,
            isNarrationEnabled = combatNarrator.isNarrationEnabled.value,
            isReadChoicesEnabled = combatNarrator.isReadChoicesEnabled.value,
            speechRate = combatNarrator.speechRate.value,
            isCharacterPitchEnabled = combatNarrator.isCharacterPitchEnabled.value,
            isSpeakerAttributionEnabled = combatNarrator.isSpeakerAttributionEnabled.value,
            isMusicEnabled = musicManager?.isMusicEnabled?.value ?: currentSave.isMusicEnabled,
            musicVolume = musicManager?.musicVolume?.value ?: currentSave.musicVolume,
            voiceAssignments = combatNarrator.getVoiceAssignments()
        )
        saveManager.save(updatedSave)
    }

    fun switchToCombat() {
        combatNarrator.stop()
        musicManager?.playCombatMusic()
        _state.value = _state.value.copy(gameScreen = GameScreen.COMBAT_ARENA)
    }

    fun switchToStory() {
        _state.value = _state.value.copy(gameScreen = GameScreen.STORY_EXPLORATION)
        musicManager?.playTrack(_state.value.currentScene.musicAsset)
        narrateCurrentNode()
    }

    fun openAudioSetup() {
        cancelPendingAutoAdvance()
        combatNarrator.stop()
        speechManager.cancel()
        _state.value = _state.value.copy(
            previousScreen = _state.value.gameScreen,
            gameScreen = GameScreen.AUDIO_SETUP
        )
    }

    fun returnFromAudioSetup() {
        val returnTarget = _state.value.previousScreen ?: GameScreen.TITLE
        _state.value = _state.value.copy(
            previousScreen = null,
            gameScreen = returnTarget
        )
        persistCurrentState()
        if (returnTarget == GameScreen.STORY_EXPLORATION) {
            musicManager?.playTrack(_state.value.currentScene.musicAsset)
            narrateCurrentNode()
        } else {
            musicManager?.playTrack(com.voicerpg.android.audio.MusicManager.TRACK_ACT1_FOREST)
        }
    }

    private var fastForwardJob: Job? = null

    fun startFastForward() {
        if (_state.value.isFastForwarding) return
        cancelPendingAutoAdvance()
        combatNarrator.stop()
        speechManager.cancel()
        _state.value = _state.value.copy(isFastForwarding = true)

        fastForwardJob = activeScope.launch {
            while (_state.value.isFastForwarding) {
                if (!canAdvanceDialogue()) {
                    stopFastForward()
                    break
                }
                advanceDialogueInternal(suppressNarration = true)
                delay(120)
            }
        }
    }

    fun stopFastForward() {
        fastForwardJob?.cancel()
        fastForwardJob = null
        if (_state.value.isFastForwarding) {
            _state.value = _state.value.copy(isFastForwarding = false)
            narrateCurrentNode()
        }
    }

    fun toggleFastForward() {
        if (_state.value.isFastForwarding) {
            stopFastForward()
        } else {
            startFastForward()
        }
    }

    fun openBacklog() {
        cancelPendingAutoAdvance()
        if (_state.value.isFastForwarding) {
            stopFastForward()
        }
        _state.value = _state.value.copy(isBacklogOpen = true)
        if (combatNarrator.isEyesFreeMode.value) {
            val last = _state.value.dialogueHistory.lastOrNull()
            val text = if (last != null) {
                "Dialogue history opened. Most recent line from ${last.speakerName}: ${last.text}. Say Close to return."
            } else {
                "Dialogue history opened. No previous lines. Say Close to return."
            }
            combatNarrator.speak(text, force = true)
        }
    }

    fun closeBacklog() {
        combatNarrator.stop()
        _state.value = _state.value.copy(isBacklogOpen = false)
        if (combatNarrator.isEyesFreeMode.value) {
            combatNarrator.speak("Resuming story.", force = true) {
                narrateCurrentNode()
            }
        }
    }

    fun toggleBacklog() {
        if (_state.value.isBacklogOpen) {
            closeBacklog()
        } else {
            openBacklog()
        }
    }

    fun replayDialogueEntry(entry: DialogueLogEntry) {
        val speaker = when (entry.speakerId) {
            "aethel" -> DialogueSpeaker.AETHEL
            "cedric" -> DialogueSpeaker.CEDRIC
            "lyra" -> DialogueSpeaker.LYRA
            "zephyr" -> DialogueSpeaker.ZEPHYR
            "malakor" -> DialogueSpeaker.MALAKOR
            "shadow_wisp" -> DialogueSpeaker.SHADOW_WISP
            "vaelor" -> DialogueSpeaker.VAELOR
            "galahault" -> DialogueSpeaker.GALAHAULT
            "nocturne" -> DialogueSpeaker.NOCTURNE
            "ouros" -> DialogueSpeaker.OUROS
            "dryad_matron" -> DialogueSpeaker.DRYAD_MATRON
            "voice_mote" -> DialogueSpeaker.VOICE_MOTE
            else -> DialogueSpeaker.NARRATOR
        }
        combatNarrator.stop()
        combatNarrator.narrateDialogue(
            speaker = speaker,
            text = entry.text,
            choices = emptyList()
        )
    }

    fun handleStoryVoiceInput(utterance: String) {
        val lower = utterance.lowercase().trim()
        val node = _state.value.currentNode

        // Intercept backlog dismissal when backlog is open
        if (_state.value.isBacklogOpen) {
            if (lower.contains("close") || lower == "back" || lower == "return" || lower == "dismiss" || lower.contains("exit")) {
                closeBacklog()
                return
            }
        }

        // Intercept fast-forward stop
        if (_state.value.isFastForwarding) {
            if (lower == "stop" || lower == "halt" || lower == "pause" || lower.contains("stop skip")) {
                stopFastForward()
                return
            }
        }

        // Handle voice navigation on Title Screen
        if (_state.value.gameScreen == GameScreen.TITLE) {
            when {
                lower.contains("continue") || lower.contains("resume") || lower.contains("load") -> {
                    if (_state.value.hasExistingSave) {
                        continueGame()
                    }
                    return
                }
                lower.contains("new game") || lower.contains("start game") || lower.contains("start new") || lower.contains("begin") -> {
                    startNewGameFlow()
                    return
                }
                lower.contains("audio setup") || lower.contains("voices") || lower.contains("calibrate") -> {
                    openAudioSetup()
                    return
                }
                lower.contains("options") || lower.contains("settings") -> {
                    onOpenOptions?.invoke()
                    return
                }
            }
            return
        }

        // In-game return to title voice command
        if (lower.contains("title screen") || lower.contains("main menu") || lower.contains("return to title")) {
            returnToTitle()
            return
        }

        // Intercept direct companion voice assignment voice command
        if (lower.contains("assign voice") || lower.contains("companion voice") || lower.contains("customize voice")) {
            openAudioSetup()
            return
        }

        // 0. Intercept Meta Voice Commands (Options, Narration, Choice Reading, Pocket Mode, Backlog, Fast-Forward)
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
            MetaCommand.OPEN_BACKLOG -> {
                openBacklog()
                return
            }
            MetaCommand.CLOSE_BACKLOG -> {
                closeBacklog()
                return
            }
            MetaCommand.FAST_FORWARD -> {
                startFastForward()
                return
            }
            MetaCommand.STOP_FAST_FORWARD -> {
                if (_state.value.isFastForwarding) {
                    stopFastForward()
                    return
                }
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
            MetaCommand.TOGGLE_MUSIC -> {
                val enabled = musicManager?.toggleMusic() ?: true
                val status = if (enabled) "Background music enabled." else "Background music muted."
                combatNarrator.speak(status, force = true)
                persistCurrentState()
                return
            }
            MetaCommand.HELP -> {
                combatNarrator.speak("Say 'Next' to advance dialogue. Say a choice keyword to select it. Say 'Log' or 'History' to review previous dialogue. Say 'Skip' to fast-forward. Say 'Options' for settings. Say 'Narration' to toggle dialogue speech.", force = true)
                return
            }
            else -> Unit
        }

        // 1. Epilogue restart command
        if (node.id == "epilogue_credits" || node.setFlagOnEnter == "game_completed") {
            val restartKeywords = listOf("play again", "new game", "start over", "restart", "awaken", "embark", "begin again")
            if (restartKeywords.any { lower.contains(it) }) {
                startNewGame(_state.value.player)
                return
            }
        }

        // 2. Battle trigger command
        if (node.triggerBattleEncounterId != null) {
            val combatKeywords = listOf("fight", "battle", "attack", "fireball", "commence", "strike", "charge", "to battle", "engage", "slay", "ready", "start", "draw blade")
            if (combatKeywords.any { lower.contains(it) }) {
                advanceDialogue()
                return
            }
        }

        // 3. Flexible choice selection with loud & nerdy roleplay matching
        val effective = effectiveDialogueChoices(node)
        if (effective.isNotEmpty()) {
            val matchedChoice = StoryChoiceMatcher.matchChoice(
                utterance = utterance,
                choices = effective,
                completedFlags = _state.value.narrativeFlags
            )
            if (matchedChoice != null) {
                if (matchedChoice.completionFlag != null && _state.value.narrativeFlags[matchedChoice.completionFlag] == true) {
                    combatNarrator.speak("That objective has already been completed. Please select a remaining task.", force = true)
                    return
                }
                selectChoice(matchedChoice)
                return
            }
        }

        // 4. Non-branching progression command (natural & nerdy progression phrases)
        if (effective.isEmpty() && StoryChoiceMatcher.isProgressionUtterance(utterance)) {
            advanceDialogue()
            return
        }

        // If in hands-free auto-listen mode and no action triggered, keep listening!
        if (speechManager.isAutoListen.value && _state.value.gameScreen == GameScreen.STORY_EXPLORATION) {
            activeScope.launch {
                delay(200)
                if (speechManager.isAutoListen.value && _state.value.gameScreen == GameScreen.STORY_EXPLORATION) {
                    speechManager.startListening(
                        onStandby = {
                            if (combatNarrator.isEyesFreeMode.value) {
                                combatNarrator.speak("Standing by. Tap the screen when you are ready to continue.", force = true)
                            }
                        },
                        onResult = { nextUtterance ->
                            handleStoryVoiceInput(nextUtterance)
                        }
                    )
                }
            }
        }
    }

    fun resumeVoiceListening() {
        if (combatNarrator.isEyesFreeMode.value) {
            combatNarrator.speak("Ready. What is your decision?", force = true) {
                activeScope.launch {
                    delay(100)
                    speechManager.startListening(
                        onStandby = {
                            if (combatNarrator.isEyesFreeMode.value) {
                                combatNarrator.speak("Standing by. Tap the screen when you are ready to continue.", force = true)
                            }
                        },
                        onResult = { utterance ->
                            handleStoryVoiceInput(utterance)
                        }
                    )
                }
            }
        } else {
            speechManager.startListening(
                onStandby = {
                    if (combatNarrator.isEyesFreeMode.value) {
                        combatNarrator.speak("Standing by. Tap the screen when you are ready to continue.", force = true)
                    }
                },
                onResult = { utterance ->
                    handleStoryVoiceInput(utterance)
                }
            )
        }
    }

    private fun narrateCurrentNode() {
        cancelPendingAutoAdvance()
        if (_state.value.gameScreen != GameScreen.STORY_EXPLORATION) return
        val node = _state.value.currentNode
        val effective = effectiveDialogueChoices(node)
        val narrationText = if (effective.isEmpty() && node.choices.isNotEmpty()) {
            val sole = eligibleChoices(node).firstOrNull()
            if (sole != null) node.text + "\n\nOne path remains. " + sole.text else node.text
        } else {
            node.text
        }
        combatNarrator.narrateDialogue(
            speaker = node.speaker,
            text = narrationText,
            choices = effective
        ) {
            val isPocketMode = combatNarrator.isEyesFreeMode.value
            val canAuto = canAdvanceDialogue()

            if (isPocketMode && canAuto) {
                schedulePocketModeAutoAdvance(node)
            } else if (speechManager.isAutoListen.value) {
                activeScope.launch {
                    delay(120)
                    speechManager.startListening(
                        onStandby = {
                            if (combatNarrator.isEyesFreeMode.value) {
                                combatNarrator.speak("Standing by. Tap the screen when you are ready to continue.", force = true)
                            }
                        },
                        onResult = { utterance ->
                            handleStoryVoiceInput(utterance)
                        }
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopFastForward()
        cancelPendingAutoAdvance()
    }
}
