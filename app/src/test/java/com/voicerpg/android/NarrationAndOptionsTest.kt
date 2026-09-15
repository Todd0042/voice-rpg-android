package com.voicerpg.android

import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.audio.SpeechState
import com.voicerpg.android.engine.IntentParser
import com.voicerpg.android.engine.SaveManager
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.engine.StoryScript
import com.voicerpg.android.model.DialogueChoice
import com.voicerpg.android.model.DialogueSpeaker
import com.voicerpg.android.model.GameSaveData
import com.voicerpg.android.model.GameScreen
import com.voicerpg.android.model.MetaCommand
import com.voicerpg.android.model.PlayerCustomization
import com.voicerpg.android.viewmodel.StoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NarrationAndOptionsTest {

    private lateinit var narrator: CombatNarrator
    private lateinit var speechManager: SpeechManager
    private lateinit var saveManager: SaveManager
    private lateinit var storyViewModel: StoryViewModel

    @Before
    fun setUp() {
        narrator = CombatNarrator(context = null)
        speechManager = SpeechManager(context = null)
        saveManager = SaveManager(context = null)
        val testScope = CoroutineScope(Dispatchers.Default)
        storyViewModel = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = narrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )
        storyViewModel.startNewGame(PlayerCustomization(name = "Kaelen"))
    }

    @Test
    fun testIntentParserRecognizesNarrationMetaCommands() {
        val intentNarration = IntentParser.parse("Please toggle narration now")
        assertEquals(MetaCommand.TOGGLE_NARRATION, intentNarration.metaCommand)

        val intentChoices = IntentParser.parse("read choices aloud")
        assertEquals(MetaCommand.TOGGLE_READ_CHOICES, intentChoices.metaCommand)

        val intentOptions = IntentParser.parse("open options")
        assertEquals(MetaCommand.OPEN_OPTIONS, intentOptions.metaCommand)

        val intentClose = IntentParser.parse("close options")
        assertEquals(MetaCommand.CLOSE_OPTIONS, intentClose.metaCommand)
    }

    @Test
    fun testCombatNarratorToggles() {
        // Narration toggle
        assertTrue(narrator.isNarrationEnabled.value)
        narrator.toggleNarration()
        assertFalse(narrator.isNarrationEnabled.value)
        narrator.setNarrationEnabled(true)
        assertTrue(narrator.isNarrationEnabled.value)

        // Read choices toggle
        assertTrue(narrator.isReadChoicesEnabled.value)
        narrator.toggleReadChoices()
        assertFalse(narrator.isReadChoicesEnabled.value)
        narrator.setReadChoicesEnabled(true)
        assertTrue(narrator.isReadChoicesEnabled.value)

        // Speech rate
        narrator.setSpeechRate(1.25f)
        assertEquals(1.25f, narrator.speechRate.value, 0.01f)

        // Character pitch
        assertTrue(narrator.isCharacterPitchEnabled.value)
        narrator.setCharacterPitchEnabled(false)
        assertFalse(narrator.isCharacterPitchEnabled.value)
    }

    @Test
    fun testCombatNarratorMultiVoiceConfiguration() {
        // When running in headless context, installed voices default safely to empty
        assertTrue(narrator.getInstalledVoices().isEmpty())
        assertEquals(0, narrator.availableVoiceCount.value)

        // Verifying speaker voice mapping getters return gracefully without throwing
        val cedricVoice = narrator.getVoiceForSpeaker(DialogueSpeaker.CEDRIC)
        val aethelVoice = narrator.getVoiceForSpeaker(DialogueSpeaker.AETHEL)
        val narratorVoice = narrator.getVoiceForSpeaker(DialogueSpeaker.NARRATOR)

        // Without hardware TTS engine, speaker voices are null
        assertEquals(null, cedricVoice)
        assertEquals(null, aethelVoice)
        assertEquals(null, narratorVoice)
    }

    @Test
    fun testNarrateDialogueInvokesCompletionCallbackEvenWhenMuted() {
        narrator.setNarrationEnabled(false)
        narrator.setEyesFreeMode(false)

        var completed = false
        val testChoices = listOf(
            DialogueChoice("1", "Look outside", emptyList(), "next_node")
        )
        narrator.narrateDialogue(DialogueSpeaker.CEDRIC, "Halt!", testChoices) {
            completed = true
        }

        assertTrue(completed)
    }

    @Test
    fun testVoiceCommandsInStoryViewModelToggleSettingsAndPersist() {
        // Initial state
        assertTrue(narrator.isNarrationEnabled.value)
        assertTrue(narrator.isReadChoicesEnabled.value)

        // Turn off narration via voice command
        storyViewModel.handleStoryVoiceInput("toggle narration")
        assertFalse(narrator.isNarrationEnabled.value)

        // Verify save data updated
        var save = saveManager.load()
        assertNotNull(save)
        assertFalse(save!!.isNarrationEnabled)

        // Turn off reading choices via voice command
        storyViewModel.handleStoryVoiceInput("toggle choices")
        assertFalse(narrator.isReadChoicesEnabled.value)

        save = saveManager.load()
        assertNotNull(save)
        assertFalse(save!!.isReadChoicesEnabled)
    }

    @Test
    fun testOpenAndCloseOptionsCallbacks() {
        var openCalled = false
        var closeCalled = false
        storyViewModel.onOpenOptions = { openCalled = true }
        storyViewModel.onCloseOptions = { closeCalled = true }

        storyViewModel.handleStoryVoiceInput("open options")
        assertTrue(openCalled)

        storyViewModel.handleStoryVoiceInput("close options")
        assertTrue(closeCalled)
    }

    @Test
    fun testCampSubStoriesProgressionEliminationAndChapter3Transition() {
        // Jump directly to campfire transition
        val campTransNode = StoryScript.ALL_NODES["crossroads_camp_trans"]!!
        storyViewModel.selectChoice(DialogueChoice("test", "test", emptyList(), campTransNode.id))
        storyViewModel.advanceDialogue()

        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)
        assertEquals(3, storyViewModel.state.value.currentNode.choices.size)

        // --- SUB-STORY X: Blight Trackers Scouting & Combat ---
        val scoutChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_lore" }
        assertEquals("substory_blight_complete", scoutChoice.completionFlag)
        storyViewModel.selectChoice(scoutChoice)
        assertEquals("camp_scout_entry", storyViewModel.state.value.currentNode.id)

        // Choose to cast azure spark
        val sparkChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_scout_tracks" }
        storyViewModel.selectChoice(sparkChoice)
        assertEquals("camp_scout_tracks", storyViewModel.state.value.currentNode.id)

        // Advance to ambush battle trigger
        storyViewModel.advanceDialogue()
        assertEquals("camp_scout_ambush", storyViewModel.state.value.currentNode.id)
        assertEquals("blight_trackers", storyViewModel.state.value.currentNode.triggerBattleEncounterId)

        // Trigger battle
        storyViewModel.advanceDialogue()
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        assertEquals("blight_trackers", storyViewModel.state.value.activeEncounter?.id)

        // Win battle -> transitions to camp_scout_victory and sets substory_blight_complete flag
        storyViewModel.onCombatVictory()
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)
        assertEquals("camp_scout_victory", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["substory_blight_complete"] == true)

        // Advance back to camp
        storyViewModel.advanceDialogue() // to camp_return_hub
        assertEquals("camp_return_hub", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue() // back to camp_intro
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)

        // Verify trying to select the completed scout choice is blocked
        val completedScoutChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_lore" }
        storyViewModel.selectChoice(completedScoutChoice)
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id) // stays at camp_intro

        // Verify voice attempt on completed choice is blocked
        storyViewModel.handleStoryVoiceInput("scout the woods for trackers")
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id) // stays at camp_intro

        // --- SUB-STORY Y: Sun Shrine Lore & Echo Chime Relic ---
        val shrineChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_towers" }
        assertEquals("substory_towers_complete", shrineChoice.completionFlag)
        storyViewModel.selectChoice(shrineChoice)
        assertEquals("camp_shrine_entry", storyViewModel.state.value.currentNode.id)

        // Align astrological dial
        val dialChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_shrine_dial" }
        storyViewModel.selectChoice(dialChoice)
        assertEquals("camp_shrine_dial", storyViewModel.state.value.currentNode.id)

        // Advance to relic reveal
        storyViewModel.advanceDialogue()
        assertEquals("camp_shrine_relic", storyViewModel.state.value.currentNode.id)

        // Advance to lore explanation which sets substory_towers_complete
        storyViewModel.advanceDialogue()
        assertEquals("camp_shrine_lore", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["substory_towers_complete"] == true)

        // Return to camp
        storyViewModel.advanceDialogue() // camp_return_hub
        storyViewModel.advanceDialogue() // camp_intro
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)

        // --- SUB-STORY Z: Midnight Vigil & Full Party Restoration ---
        // Damage party member first to test restoration
        val damagedMember = storyViewModel.state.value.partyStats.first()
        val damagedParty = listOf(damagedMember.copy(currentHp = 10, currentMp = 5))
        storyViewModel.updatePartyStatsFromCombat(damagedParty.map { saved ->
            com.voicerpg.android.model.PartyMember(
                id = saved.id,
                name = saved.name,
                loreClass = saved.loreClass,
                currentHp = 10,
                maxHp = saved.maxHp,
                currentMp = 5,
                maxMp = saved.maxMp,
                speed = saved.speed,
                spells = emptyList()
            )
        })
        assertEquals(10, storyViewModel.state.value.partyStats.first().currentHp)

        val restChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_rest" }
        assertEquals("substory_rest_complete", restChoice.completionFlag)
        storyViewModel.selectChoice(restChoice)
        assertEquals("camp_vigil_entry", storyViewModel.state.value.currentNode.id)

        // Choose meditation
        val meditateChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_vigil_meditate" }
        storyViewModel.selectChoice(meditateChoice)
        assertEquals("camp_vigil_meditate", storyViewModel.state.value.currentNode.id)

        // Advance to restored state (which sets substory_rest_complete and restores HP/MP)
        storyViewModel.advanceDialogue()
        assertEquals("camp_vigil_restored", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["substory_rest_complete"] == true)
        val restoredMember = storyViewModel.state.value.partyStats.first()
        assertEquals(restoredMember.maxHp, restoredMember.currentHp)
        assertEquals(restoredMember.maxMp, restoredMember.currentMp)

        // --- ALL 3 SUB-STORIES COMPLETE: Automatic Chapter 3 Commencement ---
        storyViewModel.advanceDialogue() // to camp_return_hub
        assertEquals("camp_return_hub", storyViewModel.state.value.currentNode.id)

        // Advancing from camp_return_hub towards camp_intro automatically redirects to camp_all_completed!
        storyViewModel.advanceDialogue()
        assertEquals("camp_all_completed", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.text.contains("preparations are complete"))

        // Advance into Chapter 3!
        storyViewModel.advanceDialogue()
        assertEquals("chapter3_intro", storyViewModel.state.value.currentNode.id)
        assertEquals("The Aqueducts of Solaria", storyViewModel.state.value.currentScene.name)
        assertTrue(storyViewModel.state.value.currentNode.text.contains("CHAPTER 3: THE ASCENT OF SOLARIA"))

        // Advance past Cedric's initial assessment to ch3_hub
        storyViewModel.advanceDialogue()
        assertEquals("ch3_cedric_assessment", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch3_hub", storyViewModel.state.value.currentNode.id)
        assertEquals(2, storyViewModel.state.value.currentNode.choices.size)

        // --- CHAPTER 3 / OBJECTIVE 1: Scout Sentinels & Discover Acoustic Flaw ---
        val scoutSentinelsChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch3_scout" }
        assertEquals("ch3_sentinels_complete", scoutSentinelsChoice.completionFlag)
        storyViewModel.selectChoice(scoutSentinelsChoice)
        assertEquals("ch3_scout_approach", storyViewModel.state.value.currentNode.id)

        // Listen from shadows
        val listenChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_scout_listen" }
        storyViewModel.selectChoice(listenChoice)
        assertEquals("ch3_scout_listen", storyViewModel.state.value.currentNode.id)

        // Advance to sentinel ambush
        storyViewModel.advanceDialogue()
        assertEquals("ch3_scout_ambush", storyViewModel.state.value.currentNode.id)
        assertEquals("ch3_sentinels", storyViewModel.state.value.currentNode.triggerBattleEncounterId)

        // Battle and defeat sentinels
        storyViewModel.advanceDialogue()
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        assertEquals("ch3_sentinels", storyViewModel.state.value.activeEncounter?.id)
        storyViewModel.onCombatVictory()

        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)
        assertEquals("ch3_sentinels_victory", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch3_sentinels_complete"] == true)

        // Return to hub
        storyViewModel.advanceDialogue() // to ch3_return_hub
        assertEquals("ch3_return_hub", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue() // back to ch3_hub
        assertEquals("ch3_hub", storyViewModel.state.value.currentNode.id)

        // Verify that completed sentinels choice is blocked from selection
        val completedScoutChoice2 = storyViewModel.state.value.currentNode.choices.first { it.id == "ch3_scout" }
        storyViewModel.selectChoice(completedScoutChoice2)
        assertEquals("ch3_hub", storyViewModel.state.value.currentNode.id)

        // --- CHAPTER 3 / OBJECTIVE 2: Channel Echo Chime & Shatter Death Wards ---
        val chimeGateChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch3_chime" }
        assertEquals("ch3_chime_complete", chimeGateChoice.completionFlag)
        storyViewModel.selectChoice(chimeGateChoice)
        assertEquals("ch3_chime_approach", storyViewModel.state.value.currentNode.id)

        // Align chime
        val alignChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_chime_align" }
        storyViewModel.selectChoice(alignChoice)
        assertEquals("ch3_chime_align", storyViewModel.state.value.currentNode.id)

        // Advance through ward shattering
        storyViewModel.advanceDialogue()
        assertEquals("ch3_chime_shatter", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch3_chime_complete_node", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch3_chime_complete"] == true)

        // Advance to return hub
        storyViewModel.advanceDialogue()
        assertEquals("ch3_return_hub", storyViewModel.state.value.currentNode.id)

        // --- BOTH CHAPTER 3 OBJECTIVES COMPLETE: Automatic Grand Breach & Boss Battle ---
        storyViewModel.advanceDialogue()
        assertEquals("ch3_all_completed", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.text.contains("queen descends"))

        // Advance to Broodmother boss fight
        storyViewModel.advanceDialogue()
        assertEquals("ch3_aqueduct_boss_trigger", storyViewModel.state.value.currentNode.id)
        assertEquals("cave_broodmother", storyViewModel.state.value.currentNode.triggerBattleEncounterId)

        // Engage boss in arena and win
        storyViewModel.advanceDialogue()
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        storyViewModel.onCombatVictory()

        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)
        assertEquals("ch3_boss_victory", storyViewModel.state.value.currentNode.id)

        // Advance to Chapter 4 transition
        storyViewModel.advanceDialogue()
        assertEquals("ch3_to_ch4", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        // --- CHAPTER 4: Ascent of Solaria Bell Tower ---
        assertEquals("chapter4_intro", storyViewModel.state.value.currentNode.id)
        assertEquals("scene_dungeon", storyViewModel.state.value.currentScene.id)
        assertEquals("Crypt of the Foundation", storyViewModel.state.value.currentScene.name)

        // Investigate crypt and confront Bone Acolyte
        val investChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch4_investigate" }
        storyViewModel.selectChoice(investChoice)
        assertEquals("ch4_crypt_cedric", storyViewModel.state.value.currentNode.id)

        val confrontChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch4_confront_acolyte" }
        storyViewModel.selectChoice(confrontChoice)
        assertEquals("ch4_crypt_confront", storyViewModel.state.value.currentNode.id)

        // Battle Bone Acolyte and crypt guards
        storyViewModel.advanceDialogue()
        assertEquals("ch4_crypt_battle_trigger", storyViewModel.state.value.currentNode.id)
        assertEquals("dungeon_descent", storyViewModel.state.value.currentNode.triggerBattleEncounterId)

        storyViewModel.advanceDialogue()
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        storyViewModel.onCombatVictory()

        assertEquals("ch4_crypt_victory", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.text.contains("Sun-Iron Bell Clapper"))

        // Ascend to high Bell Chamber (castle scene)
        storyViewModel.advanceDialogue()
        assertEquals("ch4_tower_ascent", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch4_chamber_confrontation", storyViewModel.state.value.currentNode.id)
        assertEquals("scene_tower", storyViewModel.state.value.currentScene.id)
        assertEquals("The Solaria Bell Chamber", storyViewModel.state.value.currentScene.name)

        // Confront castle vanguard
        val challengeChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch4_vanguard_challenge" }
        storyViewModel.selectChoice(challengeChoice)
        assertEquals("ch4_vanguard_charge", storyViewModel.state.value.currentNode.id)

        // Battle garrison horde
        storyViewModel.advanceDialogue()
        assertEquals("ch4_tower_battle_trigger", storyViewModel.state.value.currentNode.id)
        assertEquals("castle_horde", storyViewModel.state.value.currentNode.triggerBattleEncounterId)

        storyViewModel.advanceDialogue()
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        storyViewModel.onCombatVictory()

        assertEquals("ch4_tower_victory", storyViewModel.state.value.currentNode.id)

        // Climax: Ring the First Great Bell of Solaria!
        storyViewModel.advanceDialogue()
        assertEquals("ch4_bell_climax", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch4_bell_ringing", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.text.contains("BONGGGGGG"))

        // Epilogue
        storyViewModel.advanceDialogue()
        assertEquals("ch4_epilogue", storyViewModel.state.value.currentNode.id)
        val reflectChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch4_reflect" }
        storyViewModel.selectChoice(reflectChoice)
        assertEquals("ch4_reflect_dialogue", storyViewModel.state.value.currentNode.id)

        // Final completion screen
        storyViewModel.advanceDialogue()
        assertEquals("ch4_act1_complete", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.text.contains("ACT I CONCLUDED"))
    }

    @Test
    fun testNarrationSettingsPersistenceRoundTrip() {
        val custom = PlayerCustomization(name = "Lyra")
        val initialSave = saveManager.createInitialSave(custom)

        val updatedSave = initialSave.copy(
            isNarrationEnabled = false,
            isReadChoicesEnabled = true,
            speechRate = 1.30f,
            isCharacterPitchEnabled = false
        )
        saveManager.save(updatedSave)

        val loaded = saveManager.load()
        assertNotNull(loaded)
        assertFalse(loaded!!.isNarrationEnabled)
        assertTrue(loaded.isReadChoicesEnabled)
        assertEquals(1.30f, loaded.speechRate, 0.01f)
        assertFalse(loaded.isCharacterPitchEnabled)
    }

    @Test
    fun testHandsFreeAutoListenSessionStateAndCancellation() {
        assertFalse(speechManager.isAutoListen.value)
        assertFalse(speechManager.isSessionActive)

        speechManager.setAutoListen(true)
        assertTrue(speechManager.isAutoListen.value)

        var recognizedText = ""
        speechManager.startListening { result ->
            recognizedText = result
        }

        // In headless unit test with context = null, isAvailable is false and SpeechState.Error is set
        assertFalse(speechManager.isAvailable)
        assertTrue(speechManager.speechState.value is SpeechState.Error)

        // Cancel cleanly resets to Idle
        speechManager.cancel()
        assertFalse(speechManager.isSessionActive)
        assertEquals(SpeechState.Idle, speechManager.speechState.value)
        assertEquals("", recognizedText)
    }

    @Test
    fun testCombatNarratorDialoguePreservesVoiceAndUtteranceTracking() {
        var callbackFired = false
        narrator.narrateDialogue(
            speaker = DialogueSpeaker.CEDRIC,
            text = "By the holy light, we stand firm!",
            choices = emptyList()
        ) {
            callbackFired = true
        }

        // Without initialized engine in unit tests, callback executes cleanly via fallback
        assertTrue(callbackFired)

        // Stop cleanly resets active utterance
        narrator.stop()
        assertFalse(narrator.isSpeaking.value)
    }

    @Test
    fun testAudioSetupScreenAndCompanionVoiceState() {
        // Verify installedVoiceCount flow exists and starts at 0 in headless test
        assertEquals(0, narrator.installedVoiceCount.value)
        assertEquals(0, narrator.availableVoiceCount.value)

        // Rescan / refresh voices runs gracefully
        narrator.refreshInstalledVoices()
        assertEquals(0, narrator.installedVoiceCount.value)

        // Verify speaker mapping returns null safely without throwing
        assertEquals(null, narrator.getVoiceForSpeaker(DialogueSpeaker.ZEPHYR))
        assertEquals(null, narrator.getVoiceForSpeaker(DialogueSpeaker.MALAKOR))
        assertEquals(null, narrator.getVoiceForSpeaker(DialogueSpeaker.LYRA))

        // Verify fresh story state begins at AUDIO_SETUP
        val freshVm = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = narrator,
            saveManager = SaveManager(context = null),
            scopeOverride = CoroutineScope(Dispatchers.Default)
        )
        assertEquals(GameScreen.AUDIO_SETUP, freshVm.state.value.gameScreen)

        // Player proceeds to CHARACTER_CREATION
        freshVm.proceedToCharacterCreation()
        assertEquals(GameScreen.CHARACTER_CREATION, freshVm.state.value.gameScreen)

        // Reset returns to AUDIO_SETUP
        freshVm.resetGame()
        assertEquals(GameScreen.AUDIO_SETUP, freshVm.state.value.gameScreen)
    }
}

