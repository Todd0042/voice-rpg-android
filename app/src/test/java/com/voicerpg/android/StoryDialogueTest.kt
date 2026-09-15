package com.voicerpg.android

import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.engine.StoryScript
import com.voicerpg.android.model.GameScreen
import com.voicerpg.android.model.SpeakerSide
import com.voicerpg.android.viewmodel.StoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StoryDialogueTest {

    private lateinit var storyViewModel: StoryViewModel
    private lateinit var dummySpeech: SpeechManager
    private lateinit var dummyNarrator: CombatNarrator

    @Before
    fun setUp() {
        dummySpeech = SpeechManager()
        dummyNarrator = CombatNarrator()
        val testScope = CoroutineScope(Dispatchers.Default)
        storyViewModel = StoryViewModel(
            speechManager = dummySpeech,
            combatNarrator = dummyNarrator,
            scopeOverride = testScope
        )
        storyViewModel.startNewGame(com.voicerpg.android.model.PlayerCustomization(name = "Kaelen"))
    }

    @Test
    fun testInitialStoryState() {
        val state = storyViewModel.state.value
        assertEquals("scene_cottage", state.currentScene.id)
        assertEquals("Aethel's Cottage", state.currentScene.name)
        assertEquals("cottage_intro", state.currentNode.id)
        assertEquals(SpeakerSide.CENTER_NARRATOR, state.currentNode.side)
        assertEquals(3, state.currentNode.choices.size)
        assertEquals(GameScreen.STORY_EXPLORATION, state.gameScreen)
    }

    @Test
    fun testDialogueChoiceSelection() {
        // Choose "Try to speak"
        val speakChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "c1_speak" }
        storyViewModel.selectChoice(speakChoice)

        val state = storyViewModel.state.value
        assertEquals("cottage_voice", state.currentNode.id)
        assertEquals("Aethel", state.currentNode.speaker.name)
        assertEquals(SpeakerSide.LEFT, state.currentNode.side)
        assertTrue(state.currentNode.text.contains("Hello"))
    }

    @Test
    fun testVoiceKeywordChoiceResolution() {
        // Say "window" to look out the window
        storyViewModel.handleStoryVoiceInput("look out the window")
        assertEquals("cottage_window", storyViewModel.state.value.currentNode.id)
        assertEquals(SpeakerSide.LEFT, storyViewModel.state.value.currentNode.side)

        // Advance to voice discovery
        storyViewModel.handleStoryVoiceInput("next")
        assertEquals("cottage_voice", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testSceneTransitionToVillage() {
        // Jump directly to transition node
        val transitionNode = StoryScript.ALL_NODES["cottage_to_village"]!!
        val choice = com.voicerpg.android.model.DialogueChoice("test", "test", emptyList(), transitionNode.id)
        storyViewModel.selectChoice(choice)

        assertEquals("cottage_to_village", storyViewModel.state.value.currentNode.id)

        // Advance to village
        storyViewModel.advanceDialogue()
        val state = storyViewModel.state.value
        assertEquals("scene_village", state.currentScene.id)
        assertEquals("Whispering Pines", state.currentScene.name)
        assertEquals("village_intro", state.currentNode.id)
    }

    @Test
    fun testBattleTriggerAndPostBattleResumption() {
        // Set node to battle trigger
        val battleNode = StoryScript.ALL_NODES["village_battle_trigger"]!!
        val choice = com.voicerpg.android.model.DialogueChoice("test", "test", emptyList(), battleNode.id)
        storyViewModel.selectChoice(choice)

        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)

        // Advance into combat
        storyViewModel.advanceDialogue()
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        assertEquals(StoryEncounters.PROLOGUE_SOLO.id, storyViewModel.state.value.activeEncounter?.id)

        // Win combat and return to story
        storyViewModel.onCombatVictory()
        val postBattleState = storyViewModel.state.value
        assertEquals(GameScreen.STORY_EXPLORATION, postBattleState.gameScreen)
        assertEquals("village_post_battle", postBattleState.currentNode.id)
        assertNull(postBattleState.activeEncounter)

        // Advance to crossroads
        storyViewModel.advanceDialogue()
        assertEquals("scene_crossroads", storyViewModel.state.value.currentScene.id)
        assertEquals("The Sun Shrine Crossroads", storyViewModel.state.value.currentScene.name)
        assertEquals("crossroads_intro", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testCedricMeetingDialogueAndDuoBattle() {
        // Transition to Cedric dialogue
        val cedricIntroNode = StoryScript.ALL_NODES["crossroads_cedric_first"]!!
        val choice = com.voicerpg.android.model.DialogueChoice("test", "test", emptyList(), cedricIntroNode.id)
        storyViewModel.selectChoice(choice)

        val state = storyViewModel.state.value
        assertEquals("Sir Cedric", state.currentNode.speaker.name)
        assertEquals(SpeakerSide.RIGHT, state.currentNode.side)
        assertTrue(state.currentNode.choices.isNotEmpty())

        // Voice command: "yes" to answer Cedric
        storyViewModel.handleStoryVoiceInput("Yes! My voice commands the flame!")
        assertEquals("crossroads_cedric_reply_mage", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.text.contains("Awakened Invocator"))
    }
}
