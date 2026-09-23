package com.voicerpg.android

import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.StoryChoiceMatcher
import com.voicerpg.android.engine.StoryScript
import com.voicerpg.android.model.CombatPhase
import com.voicerpg.android.model.DialogueChoice
import com.voicerpg.android.viewmodel.CombatViewModel
import com.voicerpg.android.viewmodel.StoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StoryChoiceMatcherTest {

    private lateinit var dummySpeech: SpeechManager
    private lateinit var dummyNarrator: CombatNarrator
    private lateinit var combatViewModel: CombatViewModel
    private lateinit var storyViewModel: StoryViewModel

    @Before
    fun setUp() {
        dummySpeech = SpeechManager()
        dummyNarrator = CombatNarrator()
        val scope = CoroutineScope(Dispatchers.Default)
        combatViewModel = CombatViewModel(
            speechManager = dummySpeech,
            combatNarrator = dummyNarrator,
            scopeOverride = scope
        )
        storyViewModel = StoryViewModel(
            speechManager = dummySpeech,
            combatNarrator = dummyNarrator,
            scopeOverride = scope
        )
    }

    @Test
    fun testNerdyRoleplayUtterancesForStoryChoices() {
        val nodeChoices = listOf(
            DialogueChoice("c1_look", "Examine the cold fireplace", listOf("fireplace", "hearth", "examine", "cold"), "cottage_fireplace"),
            DialogueChoice("c1_window", "Look out the window", listOf("window", "look", "outside", "pines"), "cottage_window"),
            DialogueChoice("c1_speak", "Try to speak", listOf("speak", "voice", "talk", "hello"), "cottage_voice")
        )

        // 1. Loud and nerdy fireplace chant/inspection
        val nerdyFireplace = "By the fading embers of the hearth, I shall inspect the cold fireplace!"
        val matched1 = StoryChoiceMatcher.matchChoice(nerdyFireplace, nodeChoices)
        assertNotNull(matched1)
        assertEquals("c1_look", matched1?.id)

        // 2. Thematic roleplay window observation
        val nerdyWindow = "I step toward the frost-covered window pane and gaze outside into the pines"
        val matched2 = StoryChoiceMatcher.matchChoice(nerdyWindow, nodeChoices)
        assertNotNull(matched2)
        assertEquals("c1_window", matched2?.id)

        // 3. Spoken voice attempt
        val nerdySpeak = "I clear my throat and try to speak aloud into the eerie silence"
        val matched3 = StoryChoiceMatcher.matchChoice(nerdySpeak, nodeChoices)
        assertNotNull(matched3)
        assertEquals("c1_speak", matched3?.id)
    }

    @Test
    fun testSpellChantInStoryModeMatchesSpellChoice() {
        val nodeChoices = listOf(
            DialogueChoice("c2_chant", "Chant 'Fireball!' to test your flame", listOf("fireball", "flame", "fire", "chant", "test"), "cottage_flame_test"),
            DialogueChoice("c2_outside", "Hurry outside into the village", listOf("outside", "village", "hurry", "leave", "step"), "cottage_to_village")
        )

        // Nerdy dramatic spell incantation
        val spellUtterance = "I summon the primordial incandescent wrath of the elements, Fireball!"
        val matched = StoryChoiceMatcher.matchChoice(spellUtterance, nodeChoices)
        assertNotNull(matched)
        assertEquals("c2_chant", matched?.id)
    }

    @Test
    fun testCompletedChoiceStillMatchedForFeedbackInsteadOfSilentNoop() {
        val nodeChoices = listOf(
            DialogueChoice("c1_look", "Examine the cold fireplace", listOf("fireplace", "hearth", "examine", "cold"), "cottage_fireplace", completionFlag = "cottage_fireplace"),
            DialogueChoice("c1_window", "Look out the window", listOf("window", "look", "outside", "pines"), "cottage_window"),
            DialogueChoice("c1_speak", "Try to speak", listOf("speak", "voice", "talk", "hello"), "cottage_voice")
        )
        val completed = mapOf("cottage_fireplace" to true)

        // Re-speaking the completed option must STILL match it (so the caller can give
        // "already completed" feedback) — never silently no-op, and never misfire a neighbor.
        val matched = StoryChoiceMatcher.matchChoice(
            "By the fading embers of the hearth, I shall inspect the cold fireplace!",
            nodeChoices,
            completedFlags = completed
        )
        assertNotNull("completed choice must remain matchable", matched)
        assertEquals("completed choice should be returned for feedback", "c1_look", matched?.id)
    }

    @Test
    fun testUncompletedChoiceTakesPriorityOnScoreTie() {
        // Real ch5_hub sub-story: creek (completed) and warding stones (remaining) tie on
        // a shared utterance; the remaining choice must win so ambiguity never blocks on feedback.
        val hubChoices = listOf(
            DialogueChoice("ch5_creek_choice", "Scout the poisoned creek bed for warden tracks", listOf("scout", "creek", "tracks", "warden"), "ch5_scout_creek", completionFlag = "ch5_creek_scouted"),
            DialogueChoice("ch5_wards_choice", "Inspect the pulsing obsidian warding stones", listOf("inspect", "examine", "stones", "wards", "obsidian"), "ch5_examine_wards", completionFlag = "ch5_wards_examined")
        )
        val completed = mapOf("ch5_creek_scouted" to true)

        val matched = StoryChoiceMatcher.matchChoice("scout inspect", hubChoices, completedFlags = completed)

        assertNotNull(matched)
        assertEquals("remaining choice must win the tie", "ch5_wards_choice", matched?.id)

        // Sanity on the matching baseline: with NO completion flags both choices are equally
        // eligible; order-stability means the first-highest (creek) wins by list order.
        val noCompletionMatch = StoryChoiceMatcher.matchChoice("scout inspect", hubChoices)
        assertNotNull(noCompletionMatch)
        assertEquals("ch5_creek_choice", noCompletionMatch?.id)
    }

    @Test
    fun testAllCompletedHubChoicesReturnBestMatchForFeedback() {
        // When every sub-story objective is completed, speaking one must still resolve to that
        // specific choice (for feedback) and never misfire the unflagged assault/advance choice.
        val hubChoices = listOf(
            DialogueChoice("ch5_creek_choice", "Scout the poisoned creek bed for warden tracks", listOf("scout", "creek", "tracks", "warden"), "ch5_scout_creek", completionFlag = "ch5_creek_scouted"),
            DialogueChoice("ch5_wards_choice", "Inspect the pulsing obsidian warding stones", listOf("inspect", "examine", "stones", "wards", "obsidian"), "ch5_examine_wards", completionFlag = "ch5_wards_examined"),
            DialogueChoice("ch5_assault_choice", "Charge the sunken altar and breach Lyra's Briar Cage!", listOf("charge", "assault", "breach", "cage", "rescue"), "ch5_rescue_assault")
        )
        val completed = mapOf("ch5_creek_scouted" to true, "ch5_wards_examined" to true)

        val matched = StoryChoiceMatcher.matchChoice(
            "Scout the poisoned creek bed for warden tracks",
            hubChoices,
            completedFlags = completed
        )
        assertNotNull(matched)
        assertEquals("must return the completed creek choice for feedback", "ch5_creek_choice", matched?.id)
    }

    @Test
    fun testHubVoiceInputGivesCompletedFeedbackWithoutReentering() {
        // Full hub flow: warping to Chapter 5, completing the creek objective, then speaking it
        // again must give "already completed" feedback WITHOUT re-entering the branch or
        // mis-firing the remaining warding-stones objective.
        storyViewModel.debugWarpToChapter("ch5_intro")
        val hubNode = StoryScript.ALL_NODES["ch5_hub"] ?: error("ch5_hub missing")
        val stateField = storyViewModel.javaClass.getDeclaredField("_state")
        stateField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = stateField.get(storyViewModel) as kotlinx.coroutines.flow.MutableStateFlow<com.voicerpg.android.viewmodel.StoryState>
        stateFlow.value = stateFlow.value.copy(currentNode = hubNode)

        // Complete the creek objective through the normal choice path, then advance back to the hub
        val creekChoice = hubNode.choices.first { it.id == "ch5_creek_choice" }
        storyViewModel.selectChoice(creekChoice)
        storyViewModel.advanceDialogue()
        assertEquals("ch5_hub", storyViewModel.state.value.currentNode.id)
        assertEquals(true, storyViewModel.state.value.narrativeFlags["ch5_creek_scouted"])
        val decisionsBefore = storyViewModel.state.value.decisionsMade.size

        // Re-speaking the completed objective
        storyViewModel.handleStoryVoiceInput("Scout the poisoned creek bed for warden tracks")

        // Must remain at the hub, flag unchanged, no mis-fired wards objective, no re-entry
        assertEquals("ch5_hub", storyViewModel.state.value.currentNode.id)
        assertEquals(true, storyViewModel.state.value.narrativeFlags["ch5_creek_scouted"])
        assertTrue("wards objective must not be mis-fired", storyViewModel.state.value.narrativeFlags["ch5_wards_examined"] != true)
        assertEquals(decisionsBefore, storyViewModel.state.value.decisionsMade.size)
    }

    @Test
    fun testBattleConclusionIntentParsing() {
        // Continue Story / Commence Story variations
        assertEquals(
            StoryChoiceMatcher.ConclusionAction.CONTINUE_STORY,
            StoryChoiceMatcher.parseBattleConclusionIntent("commence story")
        )
        assertEquals(
            StoryChoiceMatcher.ConclusionAction.CONTINUE_STORY,
            StoryChoiceMatcher.parseBattleConclusionIntent("Glory awaits, let us commence the story and advance!")
        )
        assertEquals(
            StoryChoiceMatcher.ConclusionAction.CONTINUE_STORY,
            StoryChoiceMatcher.parseBattleConclusionIntent("Onward to victory and the next chapter!")
        )
        assertEquals(
            StoryChoiceMatcher.ConclusionAction.CONTINUE_STORY,
            StoryChoiceMatcher.parseBattleConclusionIntent("We press forward into the ruins!")
        )

        // Restart Battle / Rematch variations
        assertEquals(
            StoryChoiceMatcher.ConclusionAction.RESTART_BATTLE,
            StoryChoiceMatcher.parseBattleConclusionIntent("restart battle")
        )
        assertEquals(
            StoryChoiceMatcher.ConclusionAction.RESTART_BATTLE,
            StoryChoiceMatcher.parseBattleConclusionIntent("I shall not falter, run it back!")
        )
        assertEquals(
            StoryChoiceMatcher.ConclusionAction.RESTART_BATTLE,
            StoryChoiceMatcher.parseBattleConclusionIntent("Arise! We fight again!")
        )
        assertEquals(
            StoryChoiceMatcher.ConclusionAction.RESTART_BATTLE,
            StoryChoiceMatcher.parseBattleConclusionIntent("Rematch these shadow fiends once more!")
        )
    }

    @Test
    fun testCombatViewModelVictoryVoiceContinue() {
        var storyContinued = false
        combatViewModel.onContinueStory = {
            storyContinued = true
        }

        // Put combat into BATTLE_WON phase
        val wonField = combatViewModel.javaClass.getDeclaredField("_state")
        wonField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = wonField.get(combatViewModel) as kotlinx.coroutines.flow.MutableStateFlow<com.voicerpg.android.viewmodel.CombatState>
        stateFlow.value = stateFlow.value.copy(phase = CombatPhase.BATTLE_WON)

        // Process incantation "Commence story"
        combatViewModel.processIncantation("Commence story")
        assertTrue("onContinueStory should be triggered by voice in BATTLE_WON", storyContinued)
    }

    @Test
    fun testCombatViewModelDefeatVoiceRestart() {
        // Put combat into BATTLE_LOST phase
        val wonField = combatViewModel.javaClass.getDeclaredField("_state")
        wonField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = wonField.get(combatViewModel) as kotlinx.coroutines.flow.MutableStateFlow<com.voicerpg.android.viewmodel.CombatState>
        stateFlow.value = stateFlow.value.copy(phase = CombatPhase.BATTLE_LOST)

        // Process incantation "Restart battle"
        combatViewModel.processIncantation("Restart battle")
        assertEquals(CombatPhase.ATB_WAITING, combatViewModel.state.value.phase)
    }

    @Test
    fun testProgressionUtterancesInNonBranchingStoryNodes() {
        assertTrue(StoryChoiceMatcher.isProgressionUtterance("next"))
        assertTrue(StoryChoiceMatcher.isProgressionUtterance("continue"))
        assertTrue(StoryChoiceMatcher.isProgressionUtterance("onward"))
        assertTrue(StoryChoiceMatcher.isProgressionUtterance("lead the way"))
        assertTrue(StoryChoiceMatcher.isProgressionUtterance("tell me more"))
        assertTrue(StoryChoiceMatcher.isProgressionUtterance("what happened next"))
        assertTrue(StoryChoiceMatcher.isProgressionUtterance("we ride to battle"))
        assertTrue(StoryChoiceMatcher.isProgressionUtterance("I hear you, Sir Cedric, let us proceed"))
    }
}
