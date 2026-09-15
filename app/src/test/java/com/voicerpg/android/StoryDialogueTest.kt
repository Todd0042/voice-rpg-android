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

    @Test
    fun testChapter5BriarCageRescueFlow() {
        // Jump directly to Chapter 5 Intro
        val ch5Intro = StoryScript.ALL_NODES["ch5_intro"]!!
        val jumpChoice = com.voicerpg.android.model.DialogueChoice("jump_ch5", "Jump to Ch 5", emptyList(), ch5Intro.id)
        storyViewModel.selectChoice(jumpChoice)

        var state = storyViewModel.state.value
        assertEquals("scene_marsh_fane", state.currentScene.id)
        assertEquals("ch5_intro", state.currentNode.id)
        assertTrue(state.currentNode.text.contains("ACT II: THE SEVERED RESONANCE"))

        // Advance to tracks and then hub
        storyViewModel.advanceDialogue()
        assertEquals("ch5_tracks", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch5_hub", storyViewModel.state.value.currentNode.id)
        assertEquals(3, storyViewModel.state.value.currentNode.choices.size)

        // Sub-story 1: Scout creek
        val scoutChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch5_creek_choice" }
        storyViewModel.selectChoice(scoutChoice)
        assertEquals("ch5_scout_creek", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch5_creek_scouted"] == true)

        // Return to hub and inspect wards
        storyViewModel.advanceDialogue()
        assertEquals("ch5_hub", storyViewModel.state.value.currentNode.id)
        val wardsChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch5_wards_choice" }
        storyViewModel.selectChoice(wardsChoice)
        assertEquals("ch5_examine_wards", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch5_wards_examined"] == true)

        // Return to hub: both are complete, so it transitions to ch5_all_completed
        storyViewModel.advanceDialogue()
        state = storyViewModel.state.value
        assertEquals("ch5_all_completed", state.currentNode.id)
        assertTrue(state.currentNode.text.contains("briars", ignoreCase = true))

        // Launch rescue assault battle
        val assaultChoice = state.currentNode.choices.first { it.id == "ch5_assault_ready" }
        storyViewModel.selectChoice(assaultChoice)
        assertEquals("ch5_rescue_assault", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        // Verify combat screen & encounter
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        assertEquals("marsh_rescue", storyViewModel.state.value.activeEncounter?.id)
        assertEquals(2, storyViewModel.state.value.activeEncounter?.initialParty?.size)

        // Post-combat victory
        storyViewModel.onCombatVictory()
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)
        assertEquals("ch5_rescue_victory", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.text.contains("cage shatters"))

        // Advance through Lyra dialog
        storyViewModel.advanceDialogue()
        assertEquals("ch5_lyra_unbound", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch5_lyra_first_words", storyViewModel.state.value.currentNode.id)
        assertEquals("Lyra", storyViewModel.state.value.currentNode.speaker.name)

        storyViewModel.advanceDialogue()
        assertEquals("ch5_cedric_introduces", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch5_lyra_explains_crisis", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch5_rest_sanctuary", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch5_complete", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.choices.any { it.id == "ch6_start" })
    }

    @Test
    fun testChapter6LyraRecruitmentAndWillowCleansingFlow() {
        // Jump directly to Chapter 6 start choice from Chapter 5 completion
        val ch6Choice = com.voicerpg.android.model.DialogueChoice("ch6_start", "Start Ch 6", emptyList(), "ch6_intro")
        storyViewModel.selectChoice(ch6Choice)

        var state = storyViewModel.state.value
        assertEquals("scene_willow_sanctuary", state.currentScene.id)
        assertEquals("ch6_intro", state.currentNode.id)

        // Oath ceremony
        storyViewModel.advanceDialogue()
        assertEquals("ch6_oath_ceremony", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch6_oath_words", storyViewModel.state.value.currentNode.id)

        // Lyra officially joins party
        storyViewModel.advanceDialogue()
        state = storyViewModel.state.value
        assertEquals("ch6_party_joins", state.currentNode.id)
        assertTrue(state.narrativeFlags["lyra_recruited"] == true)
        assertEquals(3, state.partyStats.size)
        assertTrue(state.partyStats.any { it.id == "lyra" })

        // Enter Ch 6 hub
        storyViewModel.advanceDialogue()
        assertEquals("ch6_hub", storyViewModel.state.value.currentNode.id)

        // Sub-story 1: Lore of Veridian Chime
        val loreChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch6_lore_choice" }
        storyViewModel.selectChoice(loreChoice)
        assertEquals("ch6_lore_dialogue", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch6_lore_complete"] == true)

        // Return to hub and harvest spores
        storyViewModel.advanceDialogue()
        val sporesChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch6_spores_choice" }
        storyViewModel.selectChoice(sporesChoice)
        assertEquals("ch6_spores_dialogue", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch6_spores_complete"] == true)

        // Hub completes
        storyViewModel.advanceDialogue()
        assertEquals("ch6_all_completed", storyViewModel.state.value.currentNode.id)

        // Launch Willow Assault
        val willowChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch6_boss_ready" }
        storyViewModel.selectChoice(willowChoice)
        assertEquals("ch6_willow_assault", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        // In combat with Bog Behemoth, trio party active
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        assertEquals("swamp_behemoth", storyViewModel.state.value.activeEncounter?.id)
        assertEquals(3, storyViewModel.state.value.activeEncounter?.initialParty?.size)
        assertTrue(storyViewModel.state.value.activeEncounter?.initialParty?.any { it.id == "lyra" } == true)

        // Victory
        storyViewModel.onCombatVictory()
        state = storyViewModel.state.value
        assertEquals(GameScreen.STORY_EXPLORATION, state.gameScreen)
        assertEquals("ch6_willow_purified", state.currentNode.id)

        storyViewModel.advanceDialogue()
        assertEquals("ch6_chime_revealed", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch6_bell_inspection", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch6_act2_climax", storyViewModel.state.value.currentNode.id)
    }
}
