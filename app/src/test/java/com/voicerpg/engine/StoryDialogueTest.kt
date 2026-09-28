package com.voicerpg.engine

import com.voicerpg.engine.audio.CombatNarrator
import com.voicerpg.engine.audio.SpeechManager
import com.voicerpg.engine.content.GameContent
import com.voicerpg.engine.model.GameScreen
import com.voicerpg.engine.model.PartyMember
import com.voicerpg.engine.model.SpeakerSide
import com.voicerpg.engine.viewmodel.StoryViewModel
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
        storyViewModel.startNewGame(com.voicerpg.engine.model.PlayerCustomization(name = "Kaelen"))
    }

    @After
    fun tearDown() {
        storyViewModel.cancelPendingAutoAdvance()
    }

    // ---------------------------------------------------------------------
    // Shared drive helpers (content-agnostic: they walk nodes declared in the
    // bundled story graph, never references specific fiction).
    // ---------------------------------------------------------------------

    private fun selectChoice(choiceId: String) {
        val choice = storyViewModel.state.value.currentNode.choices.first { it.id == choiceId }
        storyViewModel.selectChoice(choice)
    }

    private fun settle() {
        Thread.sleep(15)
    }

    /** cottage_intro → c1_speak → cottage_voice → advance → cottage_sparks → c2_outside → cottage_to_village (scene_village) → advance → village_intro */
    private fun driveToVillageIntro() {
        selectChoice("c1_speak")
        storyViewModel.advanceDialogue()
        selectChoice("c2_outside")
        var state = storyViewModel.state.value
        assertEquals("cottage_to_village", state.currentNode.id)
        assertEquals("scene_village", state.currentScene.id)
        storyViewModel.advanceDialogue()
        assertEquals("village_intro", storyViewModel.state.value.currentNode.id)
    }

    /** Drive from cottage_intro to the village battle trigger (pending, pre-combat). */
    private fun driveToVillageBattleTrigger() {
        driveToVillageIntro()
        selectChoice("v1_inspect")
        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()
        assertEquals("village_battle_trigger", storyViewModel.state.value.currentNode.id)
    }

    /** village_battle_trigger → COMBAT → victory → village_post_battle (scene_crossroads). */
    private fun fightVillageBattleAndContinueToCrossroadsBattleTrigger() {
        storyViewModel.advanceDialogue()
        settle()
        var state = storyViewModel.state.value
        assertEquals(GameScreen.COMBAT_ARENA, state.gameScreen)
        storyViewModel.onCombatVictory()
        settle()
        state = storyViewModel.state.value
        assertEquals("village_post_battle", state.currentNode.id)
        assertEquals("scene_crossroads", state.currentScene.id)
        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()
        selectChoice("cr_yes")
        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()
        assertEquals("crossroads_battle_trigger", storyViewModel.state.value.currentNode.id)
    }

    /** Drive from cottage_intro all the way to the camp hub (scene_camp, cedric recruited). */
    private fun driveToCampHub() {
        driveToVillageBattleTrigger()
        fightVillageBattleAndContinueToCrossroadsBattleTrigger()
        storyViewModel.advanceDialogue()
        settle()
        var state = storyViewModel.state.value
        assertEquals(GameScreen.COMBAT_ARENA, state.gameScreen)
        storyViewModel.onCombatVictory()
        settle()
        state = storyViewModel.state.value
        assertEquals("crossroads_post_battle", state.currentNode.id)
        selectChoice("cr_ask_towers")
        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testTemplateInitialStoryState() {
        val state = storyViewModel.state.value
        assertEquals("scene_cottage", state.currentScene.id)
        assertEquals("Aethel's Cottage", state.currentScene.name)
        assertEquals("cottage_intro", state.currentNode.id)
        assertEquals(SpeakerSide.CENTER_NARRATOR, state.currentNode.side)
        assertEquals(3, state.currentNode.choices.size)
        assertEquals(GameScreen.STORY_EXPLORATION, state.gameScreen)
        assertFalse(storyViewModel.canAdvanceDialogue())
    }

    @Test
    fun testTemplateIntroAdvancesToChoiceHub() {
        selectChoice("c1_speak")
        var state = storyViewModel.state.value
        assertEquals("cottage_voice", state.currentNode.id)
        assertEquals("hero", state.currentNode.speaker.id)
        assertEquals("Kaelen", state.currentNode.speaker.name)
        assertEquals(SpeakerSide.LEFT, state.currentNode.side)

        storyViewModel.advanceDialogue()
        state = storyViewModel.state.value
        assertEquals("cottage_sparks", state.currentNode.id)
        assertEquals(2, state.currentNode.choices.size)
        assertFalse(storyViewModel.canAdvanceDialogue())
    }

    @Test
    fun testTemplateVoiceKeywordsEliminateCompletedHubChoices() {
        driveToCampHub()
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)

        // Voice command selects the rest branch; its completion flag is set at selection.
        storyViewModel.handleStoryVoiceInput("rest")
        var state = storyViewModel.state.value
        assertEquals("camp_vigil_entry", state.currentNode.id)
        assertTrue(state.narrativeFlags["substory_rest_complete"] == true)

        // Finish the vigil chain back to the camp hub.
        selectChoice("c_vigil_meditate")
        assertEquals("camp_vigil_meditate", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("camp_vigil_restored", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        assertEquals("camp_return_hub", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()

        // Back at the hub: rest is complete, three choices still surface, hub is not auto-advancing.
        state = storyViewModel.state.value
        assertEquals("camp_intro", state.currentNode.id)
        assertTrue(state.narrativeFlags["substory_rest_complete"] == true)
        assertEquals(3, state.currentNode.choices.size)
        assertFalse(storyViewModel.canAdvanceDialogue())

        // Completed choice can no longer be re-selected or re-uttered.
        selectChoice("c_rest")
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)
        storyViewModel.handleStoryVoiceInput("rest")
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)

        // Remaining branch still selectable.
        selectChoice("c_towers")
        assertEquals("camp_shrine_entry", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testTemplateEchoHallSceneSwitchAndRestHealFlow() {
        driveToCampHub()

        // Damage the whole fellowship before resting.
        val damagedParty = storyViewModel.state.value.partyStats.map { member ->
            PartyMember(
                id = member.id,
                name = member.name,
                loreClass = member.loreClass,
                currentHp = 10,
                maxHp = member.maxHp,
                currentMp = 5,
                maxMp = member.maxMp,
                speed = member.speed,
                spells = emptyList()
            )
        }
        storyViewModel.updatePartyStatsFromCombat(damagedParty)
        assertEquals(10, storyViewModel.state.value.partyStats.first().currentHp)

        // Rest branch heals the party fully and records the substory flag.
        selectChoice("c_rest")
        assertEquals("camp_vigil_entry", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["substory_rest_complete"] == true)

        selectChoice("c_vigil_meditate")
        storyViewModel.advanceDialogue()
        var state = storyViewModel.state.value
        assertEquals("camp_vigil_restored", state.currentNode.id)
        assertTrue(state.narrativeFlags["substory_rest_complete"] == true)
        assertEquals(state.partyStats.first().maxHp, state.partyStats.first().currentHp)
        assertEquals(state.partyStats.first().maxMp, state.partyStats.first().currentMp)

        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)

        // Complete substory rest choice can no longer be re-selected.
        selectChoice("c_rest")
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)
    }

    @Test
    fun testTemplateBossBattlePendingAndHubCompletionRedirect() {
        driveToVillageBattleTrigger()

        // Battle trigger is pending within the story screen.
        var state = storyViewModel.state.value
        assertEquals("village_battle_trigger", state.currentNode.id)
        assertEquals("prologue_solo", state.currentNode.triggerBattleEncounterId)
        assertEquals(GameScreen.STORY_EXPLORATION, state.gameScreen)
        assertNull(state.activeEncounter)

        // Advancing prompts the encounter.
        storyViewModel.advanceDialogue()
        state = storyViewModel.state.value
        assertEquals(GameScreen.COMBAT_ARENA, state.gameScreen)
        assertEquals("prologue_solo", state.activeEncounter?.id)
        assertEquals(1, state.activeEncounter?.initialParty?.size)
        assertEquals("hero", state.activeEncounter?.initialParty?.first()?.id)

        settle()
        storyViewModel.onCombatVictory()
        settle()

        // Victory resolves to the declared post-battle node within the crossroads scene.
        state = storyViewModel.state.value
        assertEquals(GameScreen.STORY_EXPLORATION, state.gameScreen)
        assertNull(state.activeEncounter)
        assertEquals("village_post_battle", state.currentNode.id)
        assertEquals("hero", state.currentNode.speaker.id)
        assertEquals(SpeakerSide.LEFT, state.currentNode.side)
        assertEquals("scene_crossroads", state.currentScene.id)
        assertTrue(state.defeatedEncounters.contains("prologue_solo"))
    }

    @Test
    fun testTemplateSoloBattlePendingAndPostVictoryFlow() {
        driveToCampHub()

        // Sub-story 1: Scout the woods (blight tracker ambush).
        selectChoice("c_lore")
        assertEquals("camp_scout_entry", storyViewModel.state.value.currentNode.id)
        selectChoice("c_scout_tracks")
        storyViewModel.advanceDialogue()
        var state = storyViewModel.state.value
        assertEquals("camp_scout_ambush", state.currentNode.id)
        assertEquals("blight_trackers", state.currentNode.triggerBattleEncounterId)
        assertEquals(GameScreen.STORY_EXPLORATION, state.gameScreen)
        assertNull(state.activeEncounter)

        storyViewModel.advanceDialogue()
        settle()
        storyViewModel.onCombatVictory()
        settle()
        state = storyViewModel.state.value
        assertEquals("camp_scout_victory", state.currentNode.id)
        assertTrue(state.narrativeFlags["substory_blight_complete"] == true)
        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)

        // Sub-story 2: Inspect the shrine ruins.
        selectChoice("c_towers")
        assertEquals("camp_shrine_entry", storyViewModel.state.value.currentNode.id)
        selectChoice("c_shrine_chant")
        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()
        state = storyViewModel.state.value
        assertEquals("camp_shrine_lore", state.currentNode.id)
        assertTrue(state.narrativeFlags["substory_towers_complete"] == true)
        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()
        assertEquals("camp_intro", storyViewModel.state.value.currentNode.id)

        // Sub-story 3: Midnight vigil. Completing the last sub-story auto-redirects
        // the hub to its declared completion node.
        selectChoice("c_rest")
        assertEquals("camp_vigil_entry", storyViewModel.state.value.currentNode.id)
        assertTrue(storyViewModel.state.value.narrativeFlags["substory_rest_complete"] == true)
        selectChoice("c_vigil_meditate")
        storyViewModel.advanceDialogue()
        assertEquals("camp_vigil_restored", storyViewModel.state.value.currentNode.id)
        storyViewModel.advanceDialogue()
        storyViewModel.advanceDialogue()

        state = storyViewModel.state.value
        assertEquals("camp_all_completed", state.currentNode.id)
        assertTrue(state.narrativeFlags["substory_blight_complete"] == true)
        assertTrue(state.narrativeFlags["substory_towers_complete"] == true)
        assertTrue(state.narrativeFlags["substory_rest_complete"] == true)
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

    @Test
    fun testBacklogOpenCloseAndVoiceCommands() {
        assertFalse(storyViewModel.state.value.isBacklogOpen)

        storyViewModel.handleStoryVoiceInput("log")
        assertTrue(storyViewModel.state.value.isBacklogOpen)

        storyViewModel.handleStoryVoiceInput("close")
        assertFalse(storyViewModel.state.value.isBacklogOpen)

        storyViewModel.handleStoryVoiceInput("history")
        assertTrue(storyViewModel.state.value.isBacklogOpen)

        storyViewModel.closeBacklog()
        assertFalse(storyViewModel.state.value.isBacklogOpen)
    }

    @Test
    fun testFastForwardControls() {
        assertFalse(storyViewModel.state.value.isFastForwarding)

        storyViewModel.startFastForward()
        assertTrue(storyViewModel.state.value.isFastForwarding)

        storyViewModel.stopFastForward()
        assertFalse(storyViewModel.state.value.isFastForwarding)

        storyViewModel.startNewGame(com.voicerpg.engine.model.PlayerCustomization(name = "Kaelen"))
        storyViewModel.handleStoryVoiceInput("skip")
        assertTrue(storyViewModel.state.value.isFastForwarding)

        storyViewModel.handleStoryVoiceInput("stop")
        assertFalse(storyViewModel.state.value.isFastForwarding)
    }

    @Test
    fun testNewGameFlowAudioSetupToCharacterCreation() {
        storyViewModel.returnToTitle()
        assertEquals(GameScreen.TITLE, storyViewModel.state.value.gameScreen)
        assertFalse(storyViewModel.state.value.isNewGameFlow)

        // User taps NEW GAME
        storyViewModel.startNewGameFlow()
        assertEquals(GameScreen.AUDIO_SETUP, storyViewModel.state.value.gameScreen)
        assertEquals(GameScreen.TITLE, storyViewModel.state.value.previousScreen)
        assertTrue(storyViewModel.state.value.isNewGameFlow)

        // Audio Setup "PROCEED" advances to Character Creation
        storyViewModel.proceedToCharacterCreation()
        assertEquals(GameScreen.CHARACTER_CREATION, storyViewModel.state.value.gameScreen)
        assertEquals(GameScreen.AUDIO_SETUP, storyViewModel.state.value.previousScreen)
        assertTrue(storyViewModel.state.value.isNewGameFlow)

        // Character Creation "EMBARK" starts new game
        storyViewModel.startNewGame(com.voicerpg.engine.model.PlayerCustomization(name = "Rowan"))
        assertEquals(GameScreen.STORY_EXPLORATION, storyViewModel.state.value.gameScreen)
        assertEquals("Rowan", storyViewModel.state.value.player.name)
        assertFalse(storyViewModel.state.value.isNewGameFlow)
    }

    @Test
    fun testAudioSetupFromTitleAndReturn() {
        storyViewModel.returnToTitle()
        assertEquals(GameScreen.TITLE, storyViewModel.state.value.gameScreen)

        // User taps AUDIO SETUP from Title screen
        storyViewModel.openAudioSetup()
        assertEquals(GameScreen.AUDIO_SETUP, storyViewModel.state.value.gameScreen)
        assertEquals(GameScreen.TITLE, storyViewModel.state.value.previousScreen)
        assertFalse(storyViewModel.state.value.isNewGameFlow)

        // Returning from Audio Setup goes back to Title
        storyViewModel.returnFromAudioSetup()
        assertEquals(GameScreen.TITLE, storyViewModel.state.value.gameScreen)
        assertFalse(storyViewModel.state.value.isNewGameFlow)
    }
}

/**
 * Content-agnostic invariants over the active game's story graph. These pass for ANY
 * JSON content under assets/game/story/, proving the engine's data wiring is sound.
 */
class StoryGraphSchemaTest {

    @Test
    fun everyStoryLinkResolves() {
        assertTrue("Game content must ship at least one story node", GameContent.nodes.isNotEmpty())
        for (node in GameContent.nodes.values) {
            node.nextNodeId?.let { next ->
                assertTrue(
                    "Node '${node.id}' nextNodeId '$next' does not resolve",
                    GameContent.nodes.containsKey(next)
                )
            }
            for (choice in node.choices) {
                assertTrue(
                    "Choice '${choice.id}' in node '${node.id}' nextNodeId '${choice.nextNodeId}' does not resolve",
                    GameContent.nodes.containsKey(choice.nextNodeId)
                )
            }
            node.hubCompletion?.let { hub ->
                assertTrue(
                    "Hub '${node.id}' redirectToNodeId '${hub.redirectToNodeId}' does not resolve",
                    GameContent.nodes.containsKey(hub.redirectToNodeId)
                )
            }
            node.changeSceneId?.let { sceneId ->
                assertTrue(
                    "Node '${node.id}' changeSceneId '$sceneId' does not resolve",
                    GameContent.scenes.containsKey(sceneId)
                )
            }
        }
    }

    @Test
    fun everyChoiceCarriesAtLeastOneVoiceKeyword() {
        for (node in GameContent.nodes.values) {
            for (choice in node.choices) {
                assertTrue(
                    "Choice '${choice.id}' in node '${node.id}' has no non-blank voiceKeyword",
                    choice.voiceKeywords.any { it.isNotBlank() }
                )
            }
        }
    }

    @Test
    fun everyBattleTriggerResolvesToAnEncounter() {
        for (node in GameContent.nodes.values) {
            node.triggerBattleEncounterId?.let { encounterId ->
                assertTrue(
                    "Node '${node.id}' triggerBattleEncounterId '$encounterId' does not resolve",
                    GameContent.encounters.containsKey(encounterId)
                )
            }
        }
    }

    @Test
    fun everySceneInitialNodeResolvesToANode() {
        assertTrue("Game content must ship at least one scene", GameContent.scenes.isNotEmpty())
        for (scene in GameContent.scenes.values) {
            assertTrue(
                "Scene '${scene.id}' initialNodeId '${scene.initialNodeId}' does not resolve",
                GameContent.nodes.containsKey(scene.initialNodeId)
            )
        }
    }

    @Test
    fun everyFlagNameIsNonBlank() {
        for (node in GameContent.nodes.values) {
            node.setFlagOnEnter?.let { flag ->
                assertTrue("Node '${node.id}' has a blank setFlagOnEnter", flag.isNotBlank())
            }
            node.hubCompletion?.requiredFlags?.forEach { flag ->
                assertTrue("Hub '${node.id}' has a blank requiredFlag", flag.isNotBlank())
            }
            for (choice in node.choices) {
                choice.completionFlag?.let { flag ->
                    assertTrue("Choice '${choice.id}' has a blank completionFlag", flag.isNotBlank())
                }
            }
        }
    }

    @Test
    fun everyNodeIsReachableFromTheInitialScene() {
        val startId = GameContent.initialScene.initialNodeId
        assertTrue("Initial scene '${GameContent.initialScene.id}' entry node does not resolve", GameContent.nodes.containsKey(startId))

        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add(startId)
        while (queue.isNotEmpty()) {
            val id = queue.removeFirst()
            if (!visited.add(id)) continue
            val node = GameContent.nodes[id] ?: continue
            node.nextNodeId?.let { queue.add(it) }
            node.choices.forEach { queue.add(it.nextNodeId) }
            node.hubCompletion?.let { queue.add(it.redirectToNodeId) }
            node.changeSceneId?.let { sceneId ->
                GameContent.scenes[sceneId]?.let { queue.add(it.initialNodeId) }
            }
        }

        val unreachable = GameContent.nodes.keys - visited
        assertTrue("Unreachable story nodes: $unreachable", unreachable.isEmpty())
    }

    @Test
    fun onlyGameCompletedNodesAreTerminal() {
        val terminals = GameContent.nodes.values.filter { it.nextNodeId == null && it.choices.isEmpty() }
        assertTrue("Expected at least one terminal story node", terminals.isNotEmpty())
        for (node in terminals) {
            assertEquals(
                "Terminal node '${node.id}' must set ${StoryViewModel.GAME_COMPLETED_FLAG}",
                StoryViewModel.GAME_COMPLETED_FLAG,
                node.setFlagOnEnter
            )
        }
    }
}
