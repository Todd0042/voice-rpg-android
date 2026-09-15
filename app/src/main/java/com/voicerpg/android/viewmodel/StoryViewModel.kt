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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoryState(
    val currentScene: StoryScene = StoryScript.SCENE_COTTAGE,
    val currentNode: DialogueNode = StoryScript.ALL_NODES["cottage_intro"]!!,
    val gameScreen: GameScreen = GameScreen.AUDIO_SETUP,
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

    private var pendingAutoAdvanceJob: Job? = null

    fun cancelPendingAutoAdvance() {
        pendingAutoAdvanceJob?.cancel()
        pendingAutoAdvanceJob = null
    }

    fun canAdvanceDialogue(): Boolean {
        val node = _state.value.currentNode
        if (node.choices.isNotEmpty()) return false
        if (node.triggerBattleEncounterId != null) return false
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
            // First time player: start at Audio Setup
            _state.value = StoryState(
                gameScreen = GameScreen.AUDIO_SETUP
            )
        }
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
        cancelPendingAutoAdvance()
        saveManager.deleteSave()
        _state.value = StoryState(
            gameScreen = GameScreen.AUDIO_SETUP
        )
    }

    fun advanceDialogue() {
        cancelPendingAutoAdvance()
        combatNarrator.stop()
        speechManager.cancel()
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
            if (node.id == "epilogue_credits" || node.setFlagOnEnter == "game_completed") {
                resetGame()
                return
            }
            if (node.id.startsWith("ch3_") || node.id.startsWith("ch4_") || node.id.startsWith("ch5_") || node.id.startsWith("ch6_") || node.id.startsWith("ch7_") || node.id.startsWith("ch8_") || node.id.startsWith("ch9_") || node.id.startsWith("ch10_") || node.id.startsWith("ch11_") || node.id.startsWith("ch12_") || node.id.startsWith("ch13_") || node.id.startsWith("ch14_") || node.id.startsWith("ch15_") || node.id.startsWith("ch16_") || node.id.startsWith("epilogue_") || node.id == "ch4_act1_complete") {
                return
            }
            val fallbackNode = StoryScript.ALL_NODES["camp_intro"] ?: StoryScript.ALL_NODES["crossroads_intro"]
            if (fallbackNode != null) {
                applyNodeTransition(fallbackNode)
            }
        }
    }

    fun selectChoice(choice: DialogueChoice) {
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

    private fun applyNodeTransition(newNode: DialogueNode) {
        val updatedFlags = if (newNode.setFlagOnEnter != null) {
            _state.value.narrativeFlags + (newNode.setFlagOnEnter to true)
        } else {
            _state.value.narrativeFlags
        }

        var effectiveNode = resolveEffectiveHubNode(newNode, updatedFlags)

        var updatedPartyStats = _state.value.partyStats
        if (newNode.setFlagOnEnter == "substory_rest_complete") {
            updatedPartyStats = updatedPartyStats.map { member ->
                member.copy(currentHp = member.maxHp, currentMp = member.maxMp)
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
                spellIds = listOf("shadow_strike", "venom_flurry", "umbral_oblivion")
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
                if (member.id == "zephyr" && !member.spellIds.contains("umbral_oblivion")) {
                    member.copy(spellIds = member.spellIds + "umbral_oblivion")
                } else member
            }
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
            if (flags["ch7_tuning_complete"] == true && flags["ch7_stele_complete"] == true && flags["ch7_cedric_complete"] == true) {
                StoryScript.ALL_NODES["ch7_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch8_hub") {
            if (flags["ch8_motives_complete"] == true && flags["ch8_map_complete"] == true && flags["ch8_herbs_complete"] == true) {
                StoryScript.ALL_NODES["ch8_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch9_intro" || effective.id == "ch9_hub") {
            if (flags["ch9_knights_complete"] == true && flags["ch9_altar_complete"] == true) {
                StoryScript.ALL_NODES["ch9_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch10_intro" || effective.id == "ch10_hub") {
            if (flags["ch10_dryads_complete"] == true && flags["ch10_seed_complete"] == true) {
                StoryScript.ALL_NODES["ch10_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch11_intro" || effective.id == "ch11_hub") {
            if (flags["ch11_traps_complete"] == true && flags["ch11_vials_complete"] == true) {
                StoryScript.ALL_NODES["ch11_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch12_intro" || effective.id == "ch12_hub") {
            if (flags["ch12_valves_complete"] == true && flags["ch12_cogs_complete"] == true) {
                StoryScript.ALL_NODES["ch12_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch13_intro" || effective.id == "ch13_hub") {
            if (flags["ch13_gate_complete"] == true && flags["ch13_seal_complete"] == true) {
                StoryScript.ALL_NODES["ch13_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch14_intro" || effective.id == "ch14_hub") {
            if (flags["ch14_archons_complete"] == true && flags["ch14_eddies_complete"] == true) {
                StoryScript.ALL_NODES["ch14_all_completed"]?.let { effective = it }
            }
        }
        if (effective.id == "ch15_intro" || effective.id == "ch15_hub") {
            if (flags["ch15_cedric_complete"] == true && flags["ch15_lyra_complete"] == true && flags["ch15_zephyr_complete"] == true) {
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
        if (node.choices.isNotEmpty()) {
            val matchedChoice = StoryChoiceMatcher.matchChoice(
                utterance = utterance,
                choices = node.choices,
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
        if (node.choices.isEmpty() && StoryChoiceMatcher.isProgressionUtterance(utterance)) {
            advanceDialogue()
            return
        }

        // If in hands-free auto-listen mode and no action triggered, keep listening!
        if (speechManager.isAutoListen.value && _state.value.gameScreen == GameScreen.STORY_EXPLORATION) {
            activeScope.launch {
                delay(200)
                if (speechManager.isAutoListen.value && _state.value.gameScreen == GameScreen.STORY_EXPLORATION) {
                    speechManager.startListening { nextUtterance ->
                        handleStoryVoiceInput(nextUtterance)
                    }
                }
            }
        }
    }

    private fun narrateCurrentNode() {
        cancelPendingAutoAdvance()
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
            val isPocketMode = combatNarrator.isEyesFreeMode.value
            val canAuto = canAdvanceDialogue()

            if (isPocketMode && canAuto) {
                schedulePocketModeAutoAdvance(node)
            } else if (speechManager.isAutoListen.value) {
                activeScope.launch {
                    delay(120)
                    speechManager.startListening { utterance ->
                        handleStoryVoiceInput(utterance)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelPendingAutoAdvance()
    }
}
