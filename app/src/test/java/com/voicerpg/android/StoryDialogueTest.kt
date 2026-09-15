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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test
    fun testChapter7TrioWyrmAndOptionEliminationFlow() {
        val ch7Choice = com.voicerpg.android.model.DialogueChoice("ch7_start", "Start Ch 7", emptyList(), "ch7_intro")
        storyViewModel.selectChoice(ch7Choice)

        var state = storyViewModel.state.value
        assertEquals("scene_sunken_catacombs", state.currentScene.id)
        assertEquals("ch7_intro", state.currentNode.id)

        // Advance to hub
        storyViewModel.advanceDialogue()
        assertEquals("ch7_hub", storyViewModel.state.value.currentNode.id)
        assertEquals(3, storyViewModel.state.value.currentNode.choices.size)

        // Choose Tuning
        val tuningChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch7_tuning_choice" }
        storyViewModel.selectChoice(tuningChoice)
        assertEquals("ch7_tuning_dialogue", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch7_tuning_complete"] == true)

        // Back to hub: verify attempting to select already completed choice is blocked
        storyViewModel.advanceDialogue()
        assertEquals("ch7_hub", storyViewModel.state.value.currentNode.id)
        storyViewModel.selectChoice(tuningChoice)
        assertEquals("ch7_hub", storyViewModel.state.value.currentNode.id)

        // Choose Stele
        val steleChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch7_stele_choice" }
        storyViewModel.selectChoice(steleChoice)
        assertEquals("ch7_stele_dialogue", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch7_stele_complete"] == true)

        // Back to hub
        storyViewModel.advanceDialogue()
        assertEquals("ch7_hub", storyViewModel.state.value.currentNode.id)

        // Choose Cedric
        val cedricChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch7_cedric_choice" }
        storyViewModel.selectChoice(cedricChoice)
        assertEquals("ch7_cedric_dialogue", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch7_cedric_complete"] == true)

        // Back to hub: all 3 done -> transitions to ch7_all_completed
        storyViewModel.advanceDialogue()
        assertEquals("ch7_all_completed", storyViewModel.state.value.currentNode.id)

        // Trigger Wyrm Battle
        val bossChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch7_boss_ready" }
        storyViewModel.selectChoice(bossChoice)
        assertEquals("ch7_wyrm_assault", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        assertEquals("ch7_mire_wyrm", storyViewModel.state.value.activeEncounter?.id)
        assertEquals(3, storyViewModel.state.value.activeEncounter?.initialParty?.size)

        // Victory
        storyViewModel.onCombatVictory()
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)
        assertEquals("ch7_wyrm_victory", storyViewModel.state.value.currentNode.id)

        storyViewModel.advanceDialogue()
        assertEquals("ch7_post_toll", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testChapter8AmbushZephyrDefectionAndHubFlow() {
        val ch8Choice = com.voicerpg.android.model.DialogueChoice("ch8_start", "Start Ch 8", emptyList(), "ch8_intro")
        storyViewModel.selectChoice(ch8Choice)

        var state = storyViewModel.state.value
        assertEquals("scene_shadowed_crags", state.currentScene.id)
        assertEquals("ch8_intro", state.currentNode.id)

        // Advance to ambush warning
        storyViewModel.advanceDialogue()
        assertEquals("ch8_ambush_warning", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch8_ambush_strike", storyViewModel.state.value.currentNode.id)

        val fightChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch8_fight_ambush" }
        storyViewModel.selectChoice(fightChoice)
        assertEquals("ch8_assassin_assault", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        // Combat arena
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        assertEquals("ch8_executioner_ambush", storyViewModel.state.value.activeEncounter?.id)

        // Victory: Zephyr recruited
        storyViewModel.onCombatVictory()
        state = storyViewModel.state.value
        assertEquals(GameScreen.STORY_EXPLORATION, state.gameScreen)
        assertEquals("ch8_executioner_victory", state.currentNode.id)
        assertTrue(state.narrativeFlags["zephyr_recruited"] == true)
        assertEquals(4, state.partyStats.size)
        assertTrue(state.partyStats.any { it.id == "zephyr" })

        // Enter Hub directly from victory dialogue
        storyViewModel.advanceDialogue()
        assertEquals("ch8_hub", storyViewModel.state.value.currentNode.id)

        // Sub-story 1: Zephyr Motives
        val motiveChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch8_motives_choice" }
        storyViewModel.selectChoice(motiveChoice)
        assertEquals("ch8_motives_dialogue", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch8_motives_complete"] == true)

        storyViewModel.advanceDialogue()
        assertEquals("ch8_hub", storyViewModel.state.value.currentNode.id)

        // Sub-story 2: Map
        val mapChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch8_map_choice" }
        storyViewModel.selectChoice(mapChoice)
        assertEquals("ch8_map_dialogue", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch8_map_complete"] == true)

        storyViewModel.advanceDialogue()
        assertEquals("ch8_hub", storyViewModel.state.value.currentNode.id)

        // Sub-story 3: Herbs
        val herbsChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch8_herbs_choice" }
        storyViewModel.selectChoice(herbsChoice)
        assertEquals("ch8_herbs_dialogue", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["ch8_herbs_complete"] == true)

        // Hub complete -> all completed
        storyViewModel.advanceDialogue()
        assertEquals("ch8_all_completed", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testCompanionTrialsCh9ThroughCh11MasterSpellsUnlocked() {
        // --- CHAPTER 9: Cedric's Trial ---
        val ch9Choice = com.voicerpg.android.model.DialogueChoice("ch9_start", "Start Ch 9", emptyList(), "ch9_intro")
        storyViewModel.selectChoice(ch9Choice)

        var state = storyViewModel.state.value
        assertEquals("scene_mausoleum", state.currentScene.id)
        assertEquals("ch9_intro", state.currentNode.id)

        // Advance to hub
        storyViewModel.advanceDialogue()
        assertEquals("ch9_hub", storyViewModel.state.value.currentNode.id)

        val knightsChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch9_knights_choice" }
        storyViewModel.selectChoice(knightsChoice)
        assertEquals("ch9_knights_dialogue", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        val altarChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch9_altar_choice" }
        storyViewModel.selectChoice(altarChoice)
        assertEquals("ch9_altar_dialogue", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        assertEquals("ch9_all_completed", storyViewModel.state.value.currentNode.id)
        val galahaultBossChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch9_boss_ready" }
        storyViewModel.selectChoice(galahaultBossChoice)
        assertEquals("ch9_galahault_assault", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        assertEquals("ch9_galahault_trial", storyViewModel.state.value.activeEncounter?.id)
        storyViewModel.onCombatVictory()
        state = storyViewModel.state.value
        assertTrue(state.narrativeFlags["cedric_trial_complete"] == true)
        val cedric = state.partyStats.first { it.id == "cedric" }
        assertTrue(cedric.spellIds.contains("aegis_dawn"))
        assertEquals("ch9_galahault_victory", state.currentNode.id)

        storyViewModel.advanceDialogue()
        assertEquals("ch9_post_victory", storyViewModel.state.value.currentNode.id)

        // --- CHAPTER 10: Lyra's Trial ---
        val ch10Choice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch9_to_lyra_trial" }
        storyViewModel.selectChoice(ch10Choice)
        assertEquals("scene_emerald_choir", storyViewModel.state.value.currentScene.id)
        assertEquals("ch10_intro", storyViewModel.state.value.currentNode.id)

        storyViewModel.advanceDialogue()
        assertEquals("ch10_hub", storyViewModel.state.value.currentNode.id)

        val dryadsChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch10_dryads_choice" }
        storyViewModel.selectChoice(dryadsChoice)
        assertEquals("ch10_dryads_dialogue", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        val seedChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch10_seed_choice" }
        storyViewModel.selectChoice(seedChoice)
        assertEquals("ch10_seed_dialogue", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        assertEquals("ch10_all_completed", storyViewModel.state.value.currentNode.id)
        val broodmotherBossChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch10_boss_ready" }
        storyViewModel.selectChoice(broodmotherBossChoice)
        assertEquals("ch10_broodmother_assault", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        assertEquals("ch10_broodmother_trial", storyViewModel.state.value.activeEncounter?.id)
        storyViewModel.onCombatVictory()
        state = storyViewModel.state.value
        assertTrue(state.narrativeFlags["lyra_trial_complete"] == true)
        val lyra = state.partyStats.first { it.id == "lyra" }
        assertTrue(lyra.spellIds.contains("verdant_cataclysm"))
        assertEquals("ch10_broodmother_victory", state.currentNode.id)

        storyViewModel.advanceDialogue()
        assertEquals("ch10_post_victory", storyViewModel.state.value.currentNode.id)

        // --- CHAPTER 11: Zephyr's Trial ---
        val ch11Choice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch10_to_zephyr_trial" }
        storyViewModel.selectChoice(ch11Choice)
        assertEquals("scene_blind_gorge", storyViewModel.state.value.currentScene.id)
        assertEquals("ch11_intro", storyViewModel.state.value.currentNode.id)

        storyViewModel.advanceDialogue()
        assertEquals("ch11_hub", storyViewModel.state.value.currentNode.id)

        val trapsChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch11_traps_choice" }
        storyViewModel.selectChoice(trapsChoice)
        assertEquals("ch11_traps_dialogue", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        val vialsChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch11_vials_choice" }
        storyViewModel.selectChoice(vialsChoice)
        assertEquals("ch11_vials_dialogue", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        assertEquals("ch11_all_completed", storyViewModel.state.value.currentNode.id)
        val nocturneBossChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch11_boss_ready" }
        storyViewModel.selectChoice(nocturneBossChoice)
        assertEquals("ch11_nocturne_assault", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        assertEquals("ch11_nocturne_trial", storyViewModel.state.value.activeEncounter?.id)
        storyViewModel.onCombatVictory()
        state = storyViewModel.state.value
        assertTrue(state.narrativeFlags["zephyr_trial_complete"] == true)
        val zephyr = state.partyStats.first { it.id == "zephyr" }
        assertTrue(zephyr.spellIds.contains("umbral_oblivion"))
        assertEquals("ch11_nocturne_victory", state.currentNode.id)

        storyViewModel.advanceDialogue()
        assertEquals("ch11_post_victory", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testCitadelClimaxAndEpilogueCh12ThroughCh16() {
        // Jump to Chapter 16 Final Boss
        val ch16Choice = com.voicerpg.android.model.DialogueChoice("ch16_start", "Start Ch 16", emptyList(), "ch16_intro")
        storyViewModel.selectChoice(ch16Choice)

        var state = storyViewModel.state.value
        assertEquals("scene_final_summit", state.currentScene.id)
        assertEquals("ch16_intro", state.currentNode.id)

        // Advance through Malakor confrontation
        storyViewModel.advanceDialogue()
        assertEquals("ch16_confrontation", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("ch16_fellowship_reply", storyViewModel.state.value.currentNode.id)

        val bossChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch16_boss_ready" }
        storyViewModel.selectChoice(bossChoice)
        assertEquals("ch16_malakor_assault", storyViewModel.state.value.currentNode.id)

        // Trigger Grand Finale Encounter
        storyViewModel.advanceDialogue()
        assertEquals(GameScreen.COMBAT_ARENA, storyViewModel.state.value.gameScreen)
        assertEquals("ch16_malakor_finale", storyViewModel.state.value.activeEncounter?.id)
        assertEquals(4, storyViewModel.state.value.activeEncounter?.initialParty?.size)
        assertEquals(5, storyViewModel.state.value.activeEncounter?.enemies?.size) // Malakor + 4 Echo Nullifiers

        // Final Victory
        storyViewModel.onCombatVictory()
        state = storyViewModel.state.value
        assertEquals(GameScreen.STORY_EXPLORATION, state.gameScreen)
        assertEquals("ch16_malakor_victory", state.currentNode.id)

        storyViewModel.advanceDialogue()
        assertEquals("ch16_toll_bell", storyViewModel.state.value.currentNode.id)

        val ringChoice = storyViewModel.state.value.currentNode.choices.first { it.id == "ch16_ring_bell" }
        storyViewModel.selectChoice(ringChoice)

        // Transition to Epilogue!
        state = storyViewModel.state.value
        assertEquals("scene_epilogue", state.currentScene.id)
        assertEquals("epilogue_awakening", state.currentNode.id)

        storyViewModel.advanceDialogue()
        assertEquals("epilogue_village", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("epilogue_destinies", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("epilogue_invocator", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("epilogue_credits", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.currentNode.text.contains("CONGRATULATIONS"))
    }

    @After
    fun tearDown() {
        storyViewModel.cancelPendingAutoAdvance()
    }

    @Test
    fun testScreenlessPocketModeAutoAdvanceOnDialogueWithoutChoices() {
        // Move to cottage_voice (no choices, nextNodeId = cottage_sparks)
        val voiceNode = StoryScript.ALL_NODES["cottage_voice"]!!
        storyViewModel.selectChoice(com.voicerpg.android.model.DialogueChoice("to_voice", "To Voice", emptyList(), voiceNode.id))

        assertEquals("cottage_voice", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.canAdvanceDialogue())

        // Enable Screenless Pocket Mode
        dummyNarrator.toggleEyesFreeMode()
        assertTrue(dummyNarrator.isEyesFreeMode.value)

        // Give the 1.5s auto advance job time to complete
        Thread.sleep(1700)

        // Dialogue must have automatically advanced to cottage_sparks
        assertEquals("cottage_sparks", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testScreenlessPocketModeDoesNotAutoAdvanceWhenChoicesPresent() {
        // cottage_intro has 3 choices
        assertEquals("cottage_intro", storyViewModel.state.value.currentNode.id)
        assertFalse(storyViewModel.canAdvanceDialogue())

        // Enable Screenless Pocket Mode
        dummyNarrator.toggleEyesFreeMode()
        assertTrue(dummyNarrator.isEyesFreeMode.value)

        // Wait beyond 1.5s
        Thread.sleep(1700)

        // It must NOT advance because user decision is required
        assertEquals("cottage_intro", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testScreenlessPocketModeAutoAdvanceCancelledOnUserAction() {
        val voiceNode = StoryScript.ALL_NODES["cottage_voice"]!!
        storyViewModel.selectChoice(com.voicerpg.android.model.DialogueChoice("to_voice", "To Voice", emptyList(), voiceNode.id))
        assertEquals("cottage_voice", storyViewModel.state.value.currentNode.id)

        // Enable Pocket Mode which triggers auto-advance countdown
        dummyNarrator.toggleEyesFreeMode()
        assertTrue(dummyNarrator.isEyesFreeMode.value)

        // Ensure background collector scheduled the job
        Thread.sleep(100)

        // Cancel pending job (simulating options menu open or user cancel)
        storyViewModel.cancelPendingAutoAdvance()

        // Wait beyond 1.5s
        Thread.sleep(1700)

        // Dialogue must remain at cottage_voice because auto-advance was cancelled
        assertEquals("cottage_voice", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testOpenAndReturnFromAudioSetup() {
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)
        assertNull(storyViewModel.state.value.previousScreen)

        // Open audio setup from in-game options
        storyViewModel.openAudioSetup()
        assertEquals(GameScreen.AUDIO_SETUP, storyViewModel.state.value.gameScreen)
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.previousScreen)

        // Return from audio setup back to game
        storyViewModel.returnFromAudioSetup()
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)
        assertNull(storyViewModel.state.value.previousScreen)
    }

    @Test
    fun testVoiceCommandOpensAudioSetup() {
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)

        // Utter voice command to assign voices
        storyViewModel.handleStoryVoiceInput("assign voices")
        assertEquals(GameScreen.AUDIO_SETUP, storyViewModel.state.value.gameScreen)
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.previousScreen)

        // Return
        storyViewModel.returnFromAudioSetup()
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)
    }
}

