package com.voicerpg.android

import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.ClassSpellLibrary
import com.voicerpg.android.engine.IntentParser
import com.voicerpg.android.engine.SaveManager
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.engine.StoryScript
import com.voicerpg.android.model.AuraColor
import com.voicerpg.android.model.GameSaveData
import com.voicerpg.android.model.GameScreen
import com.voicerpg.android.model.HeroClass
import com.voicerpg.android.model.PlayerCustomization
import com.voicerpg.android.model.SavedCharacterStats
import com.voicerpg.android.viewmodel.CombatViewModel
import com.voicerpg.android.viewmodel.StoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveSystemTest {

    private lateinit var saveManager: SaveManager
    private lateinit var speechManager: SpeechManager
    private lateinit var combatNarrator: CombatNarrator

    @Before
    fun setUp() {
        saveManager = SaveManager(context = null) // Headless in-memory persistence
        speechManager = SpeechManager()
        combatNarrator = CombatNarrator()
    }

    @Test
    fun testInitialSaveCreationForDifferentClasses() {
        // Elementalist
        val elemCustom = PlayerCustomization(
            name = "Lyra",
            heroClass = HeroClass.ELEMENTALIST,
            title = "Acolyte of Embers",
            auraColor = AuraColor.CRIMSON_PYRE
        )
        val elemSave = saveManager.createInitialSave(elemCustom)
        assertEquals("Lyra", elemSave.player.name)
        assertEquals(HeroClass.ELEMENTALIST, elemSave.player.heroClass)
        assertEquals(240, elemSave.partyStats.first().maxHp)
        assertEquals(160, elemSave.partyStats.first().maxMp)
        assertTrue(elemSave.partyStats.first().spellIds.contains("fireball"))

        // Battlemage
        val battleCustom = PlayerCustomization(
            name = "Thorin",
            heroClass = HeroClass.BATTLEMAGE,
            title = "Runeguard",
            auraColor = AuraColor.SOLAR_GOLD
        )
        val battleSave = saveManager.createInitialSave(battleCustom)
        assertEquals(320, battleSave.partyStats.first().maxHp)
        assertEquals(110, battleSave.partyStats.first().maxMp)
        assertTrue(battleSave.partyStats.first().spellIds.contains("flame_strike"))

        // Chanter
        val chanterCustom = PlayerCustomization(
            name = "Aria",
            heroClass = HeroClass.CHANTER,
            title = "Voice of the Grove",
            auraColor = AuraColor.EMERALD_GROVE
        )
        val chanterSave = saveManager.createInitialSave(chanterCustom)
        assertEquals(220, chanterSave.partyStats.first().maxHp)
        assertEquals(180, chanterSave.partyStats.first().maxMp)
        assertTrue(chanterSave.partyStats.first().spellIds.contains("temporal_stasis"))

        // Shadowweaver
        val shadowCustom = PlayerCustomization(
            name = "Vesper",
            heroClass = HeroClass.SHADOWWEAVER,
            title = "Voidstrider",
            auraColor = AuraColor.AMETHYST_VOID
        )
        val shadowSave = saveManager.createInitialSave(shadowCustom)
        assertEquals(230, shadowSave.partyStats.first().maxHp)
        assertEquals(140, shadowSave.partyStats.first().maxMp)
        assertTrue(shadowSave.partyStats.first().spellIds.contains("shadow_spike"))
    }

    @Test
    fun testSaveAndLoadRoundTripSerialization() {
        val custom = PlayerCustomization(
            name = "Zephyr",
            heroClass = HeroClass.ELEMENTALIST,
            title = "Stormcaller",
            auraColor = AuraColor.SAPPHIRE_FROST
        )
        val initialSave = saveManager.createInitialSave(custom)

        val updatedSave = initialSave.copy(
            currentSceneId = "scene_crossroads",
            currentNodeId = "crossroads_cedric_first",
            decisionsMade = listOf("c1_speak", "c_village_help", "c_crossroads_join"),
            narrativeFlags = mapOf("met_cedric" to true, "cleared_prologue" to true),
            defeatedEncounters = listOf("enc_prologue_solo"),
            achievements = listOf("first_step", "voice_awakened", "shadow_slayer"),
            isEyesFreeMode = true
        )

        val saveResult = saveManager.save(updatedSave)
        assertTrue(saveResult)
        assertTrue(saveManager.hasSave())

        val loaded = saveManager.load()
        assertNotNull(loaded)
        assertEquals("Zephyr", loaded!!.player.name)
        assertEquals(HeroClass.ELEMENTALIST, loaded.player.heroClass)
        assertEquals(AuraColor.SAPPHIRE_FROST, loaded.player.auraColor)
        assertEquals("scene_crossroads", loaded.currentSceneId)
        assertEquals("crossroads_cedric_first", loaded.currentNodeId)
        assertEquals(3, loaded.decisionsMade.size)
        assertEquals("c_crossroads_join", loaded.decisionsMade.last())
        assertEquals(true, loaded.narrativeFlags["met_cedric"])
        assertEquals(true, loaded.narrativeFlags["cleared_prologue"])
        assertEquals(1, loaded.defeatedEncounters.size)
        assertEquals("enc_prologue_solo", loaded.defeatedEncounters.first())
        assertEquals(3, loaded.achievements.size)
        assertTrue(loaded.isEyesFreeMode)
    }

    @Test
    fun testDeleteSave() {
        val custom = PlayerCustomization(name = "Kael")
        val save = saveManager.createInitialSave(custom)
        saveManager.save(save)
        assertTrue(saveManager.hasSave())

        val deleted = saveManager.deleteSave()
        assertTrue(deleted)
        assertFalse(saveManager.hasSave())
        assertNull(saveManager.load())
    }

    @Test
    fun testStoryViewModelFirstLaunchRoutesToCharacterCreation() {
        val testScope = CoroutineScope(Dispatchers.Default)
        val storyVm = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )

        assertEquals(GameScreen.CHARACTER_CREATION, storyVm.state.value.gameScreen)
    }

    @Test
    fun testStoryViewModelCharacterCreationAndStateProgression() {
        val testScope = CoroutineScope(Dispatchers.Default)
        val storyVm = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )

        // Player customizes hero and starts game
        val custom = PlayerCustomization(
            name = "Rowan",
            heroClass = HeroClass.BATTLEMAGE,
            title = "Acolyte of Embers",
            auraColor = AuraColor.CRIMSON_PYRE
        )
        storyVm.startNewGame(custom)

        assertEquals(GameScreen.STORY_EXPLORATION, storyVm.state.value.gameScreen)
        assertEquals("scene_cottage", storyVm.state.value.currentScene.id)
        assertEquals("cottage_intro", storyVm.state.value.currentNode.id)
        assertEquals("Rowan", storyVm.state.value.player.name)

        // Make decision 1: "Try to speak"
        val speakChoice = storyVm.state.value.currentNode.choices.first { it.id == "c1_speak" }
        storyVm.selectChoice(speakChoice)

        assertEquals("cottage_voice", storyVm.state.value.currentNode.id)
        assertEquals(listOf("c1_speak"), storyVm.state.value.decisionsMade)

        // Verify save was written to disk/memory
        val autoSavedData = saveManager.load()
        assertNotNull(autoSavedData)
        assertEquals("cottage_voice", autoSavedData!!.currentNodeId)
        assertEquals(listOf("c1_speak"), autoSavedData.decisionsMade)
        assertEquals("Rowan", autoSavedData.player.name)
    }

    @Test
    fun testStateRestorationOnAppReload() {
        val testScope = CoroutineScope(Dispatchers.Default)

        // Session 1: Play and progress
        val storyVm1 = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )

        val custom = PlayerCustomization(
            name = "Elora",
            heroClass = HeroClass.CHANTER,
            title = "Voice of the Grove",
            auraColor = AuraColor.EMERALD_GROVE
        )
        storyVm1.startNewGame(custom)

        // Make choice
        val choice = storyVm1.state.value.currentNode.choices.first { it.id == "c1_speak" }
        storyVm1.selectChoice(choice)

        // Advance to village
        val villageTransNode = StoryScript.ALL_NODES["cottage_to_village"]!!
        val jumpChoice = com.voicerpg.android.model.DialogueChoice("jump", "Go to village", emptyList(), villageTransNode.id)
        storyVm1.selectChoice(jumpChoice)
        storyVm1.advanceDialogue()

        assertEquals("scene_village", storyVm1.state.value.currentScene.id)
        assertEquals("village_intro", storyVm1.state.value.currentNode.id)
        assertEquals(listOf("c1_speak", "jump"), storyVm1.state.value.decisionsMade)

        // Session 2: Fresh launch of the app with existing save
        val storyVm2 = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )

        // Automatically resumes at village_intro without going to Character Creation!
        assertEquals(GameScreen.STORY_EXPLORATION, storyVm2.state.value.gameScreen)
        assertEquals("scene_village", storyVm2.state.value.currentScene.id)
        assertEquals("village_intro", storyVm2.state.value.currentNode.id)
        assertEquals("Elora", storyVm2.state.value.player.name)
        assertEquals(HeroClass.CHANTER, storyVm2.state.value.player.heroClass)
        assertEquals(listOf("c1_speak", "jump"), storyVm2.state.value.decisionsMade)
    }

    @Test
    fun testCombatVictoryUpdatesAccomplishmentsAndPersists() {
        val testScope = CoroutineScope(Dispatchers.Default)
        val storyVm = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )
        storyVm.startNewGame(PlayerCustomization(name = "Valen"))

        // Trigger encounter
        storyVm.triggerEncounter(StoryEncounters.PROLOGUE_SOLO.id)
        assertEquals(GameScreen.COMBAT_ARENA, storyVm.state.value.gameScreen)

        // Win encounter
        storyVm.onCombatVictory()
        assertEquals(GameScreen.STORY_EXPLORATION, storyVm.state.value.gameScreen)
        assertTrue(storyVm.state.value.defeatedEncounters.contains(StoryEncounters.PROLOGUE_SOLO.id))

        // Check save
        val save = saveManager.load()
        assertNotNull(save)
        assertTrue(save!!.defeatedEncounters.contains(StoryEncounters.PROLOGUE_SOLO.id))
    }

    @Test
    fun testCombatViewModelCustomHeroIntegration() {
        val combatVm = CombatViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            scopeOverride = CoroutineScope(Dispatchers.Default)
        )
        val custom = PlayerCustomization(
            name = "Kaelen",
            heroClass = HeroClass.SHADOWWEAVER,
            title = "Voidstrider",
            auraColor = AuraColor.AMETHYST_VOID
        )

        combatVm.applyPlayerCustomization(custom)

        val hero = combatVm.state.value.party.first()
        assertEquals("Kaelen", hero.name)
        assertEquals(230, hero.maxHp)
        assertEquals(140, hero.maxMp)
        assertTrue(hero.spells.any { it.id == "shadow_spike" })

        // Verify stats serialization from party member
        val savedHero = SavedCharacterStats.fromPartyMember(hero)
        assertEquals("Kaelen", savedHero.name)
        assertEquals(230, savedHero.maxHp)
        assertEquals(140, savedHero.maxMp)
        assertEquals(hero.spells.size, savedHero.spellIds.size)
    }

    @Test
    fun testVoiceIncantationMatchesCustomHeroName() {
        val combatVm = CombatViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            scopeOverride = CoroutineScope(Dispatchers.Default)
        )
        val custom = PlayerCustomization(
            name = "Ignis",
            heroClass = HeroClass.BATTLEMAGE
        )
        combatVm.applyPlayerCustomization(custom)

        val hero = combatVm.state.value.party.first()
        assertEquals("Ignis", hero.name)

        val parsed = IntentParser.parse(
            utterance = "Flame strike the orc with fury!",
            availableSpells = hero.spells,
            activeEnemies = combatVm.state.value.enemies,
            party = combatVm.state.value.party
        )

        assertEquals("flame_strike", parsed.spell.id)
        assertEquals("orc", parsed.targetEnemyId)
    }
}
