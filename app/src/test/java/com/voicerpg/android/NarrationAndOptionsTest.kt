package com.voicerpg.android

import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
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
    fun testStoryContinuationIntoCampfireAndChapter2() {
        // Jump directly to crossroads_conclusion
        val conclusionNode = StoryScript.ALL_NODES["crossroads_conclusion"]!!
        storyViewModel.selectChoice(DialogueChoice("test", "test", emptyList(), conclusionNode.id))
        assertEquals("crossroads_conclusion", storyViewModel.state.value.currentNode.id)

        // Advance to campfire transition
        storyViewModel.advanceDialogue()
        assertEquals("crossroads_camp_trans", storyViewModel.state.value.currentNode.id)
        assertEquals("scene_camp", storyViewModel.state.value.currentScene.id)
        assertEquals("Camp of the Fellowship", storyViewModel.state.value.currentScene.name)

        // Advance to camp intro
        storyViewModel.advanceDialogue()
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)
        assertEquals(3, storyViewModel.state.value.currentNode.choices.size)

        // Pick lore choice about Bell Towers
        val towerChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_towers" }
        storyViewModel.selectChoice(towerChoice)
        assertEquals("camp_lore_towers", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.text.contains("Four Great Bell Towers"))

        // Advance to morning fork
        storyViewModel.advanceDialogue()
        assertEquals("camp_next_morning", storyViewModel.state.value.currentNode.id)
        assertEquals(2, storyViewModel.state.value.currentNode.choices.size)

        // Choose Whispering Caverns branch
        val cavernChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c_cavern" }
        storyViewModel.selectChoice(cavernChoice)
        assertEquals("cavern_entry", storyViewModel.state.value.currentNode.id)
        assertEquals("scene_cave", storyViewModel.state.value.currentScene.id)
        assertEquals("The Whispering Caverns", storyViewModel.state.value.currentScene.name)

        // Advance into cavern exploration and trigger encounter
        storyViewModel.advanceDialogue()
        assertEquals("cavern_exploration", storyViewModel.state.value.currentNode.id)
        assertEquals("cave_broodmother", storyViewModel.state.value.currentNode.triggerBattleEncounterId)

        // Enter battle
        storyViewModel.advanceDialogue()
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        assertEquals(StoryEncounters.CAVE_BROODMOTHER.id, storyViewModel.state.value.activeEncounter?.id)

        // Win battle and return to story post-battle
        storyViewModel.onCombatVictory()
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)
        assertEquals("cavern_post_battle", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.defeatedEncounters.contains("cave_broodmother"))

        // Advance to chapter 2 conclusion
        storyViewModel.advanceDialogue()
        assertEquals("chapter2_conclusion", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.choices.isNotEmpty())
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
}
