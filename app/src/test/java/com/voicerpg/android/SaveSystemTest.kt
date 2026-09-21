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
    fun testVoiceAssignmentsRoundTripSerialization() {
        val save = saveManager.createInitialSave(PlayerCustomization(name = "Lyra"))
        val withVoices = save.copy(
            voiceAssignments = mapOf(
                "cedric" to "en-gb-x-gbd-local",
                "aethel" to "en-gb-x-gba-local",
                "lyra" to "en-us-x-tpf-local",
                "zephyr" to "en-us-x-tpc-local",
                "malakor" to "en-us-x-tpd-local",
                "narrator" to "en-gb-x-rjs-local"
            )
        )

        assertTrue(saveManager.save(withVoices))

        val loaded = saveManager.load()
        assertNotNull(loaded)
        assertEquals("en-gb-x-gbd-local", loaded!!.voiceAssignments["cedric"])
        assertEquals("en-gb-x-gba-local", loaded.voiceAssignments["aethel"])
        assertEquals("en-us-x-tpf-local", loaded.voiceAssignments["lyra"])
        assertEquals("en-us-x-tpc-local", loaded.voiceAssignments["zephyr"])
        assertEquals("en-us-x-tpd-local", loaded.voiceAssignments["malakor"])
        assertEquals("en-gb-x-rjs-local", loaded.voiceAssignments["narrator"])
        assertEquals(6, loaded.voiceAssignments.size)
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
    fun testStoryViewModelFirstLaunchRoutesToAudioSetupAndProceedsToCharacterCreation() {
        val testScope = CoroutineScope(Dispatchers.Default)
        val storyVm = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )

        assertEquals(GameScreen.TITLE, storyVm.state.value.gameScreen)
        assertFalse(storyVm.state.value.hasExistingSave)

        storyVm.startNewGameFlow()
        assertEquals(GameScreen.AUDIO_SETUP, storyVm.state.value.gameScreen)
        storyVm.proceedToCharacterCreation()
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

        // Opens at Title Screen with save detected, ready to continue!
        assertEquals(GameScreen.TITLE, storyVm2.state.value.gameScreen)
        assertTrue(storyVm2.state.value.hasExistingSave)
        assertNotNull(storyVm2.state.value.saveSummary)
        assertEquals("Elora", storyVm2.state.value.saveSummary?.heroName)
        assertEquals(1, storyVm2.state.value.saveSummary?.partySize)

        // Continue chronicle resumes directly into story
        storyVm2.continueGame()
        assertEquals(GameScreen.STORY_EXPLORATION, storyVm2.state.value.gameScreen)
        assertEquals("scene_village", storyVm2.state.value.currentScene.id)
        assertEquals("village_intro", storyVm2.state.value.currentNode.id)
        assertEquals("Elora", storyVm2.state.value.player.name)
        assertEquals(HeroClass.CHANTER, storyVm2.state.value.player.heroClass)
        assertEquals(listOf("c1_speak", "jump"), storyVm2.state.value.decisionsMade)

        // Return to title persists and transitions back to Title Screen
        storyVm2.returnToTitle()
        assertEquals(GameScreen.TITLE, storyVm2.state.value.gameScreen)
        assertTrue(storyVm2.state.value.hasExistingSave)
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

    @Test
    fun testMultiSlotIndependentSaveAndLoad() {
        val hero1 = PlayerCustomization(name = "Aethel", heroClass = HeroClass.ELEMENTALIST)
        val hero2 = PlayerCustomization(name = "Cedric", heroClass = HeroClass.BATTLEMAGE)
        val hero3 = PlayerCustomization(name = "Lyra", heroClass = HeroClass.CHANTER)

        saveManager.createInitialSave(hero1, slot = 1)
        saveManager.createInitialSave(hero2, slot = 2)
        saveManager.createInitialSave(hero3, slot = 3)

        assertTrue(saveManager.hasSave(1))
        assertTrue(saveManager.hasSave(2))
        assertTrue(saveManager.hasSave(3))

        val loaded1 = saveManager.load(1)
        val loaded2 = saveManager.load(2)
        val loaded3 = saveManager.load(3)

        assertNotNull(loaded1)
        assertNotNull(loaded2)
        assertNotNull(loaded3)

        assertEquals("Aethel", loaded1!!.player.name)
        assertEquals(HeroClass.ELEMENTALIST, loaded1.player.heroClass)

        assertEquals("Cedric", loaded2!!.player.name)
        assertEquals(HeroClass.BATTLEMAGE, loaded2.player.heroClass)

        assertEquals("Lyra", loaded3!!.player.name)
        assertEquals(HeroClass.CHANTER, loaded3.player.heroClass)
    }

    @Test
    fun testMultiSlotDeletionAndSummary() {
        val hero1 = PlayerCustomization(name = "Sol", heroClass = HeroClass.BATTLEMAGE)
        val hero2 = PlayerCustomization(name = "Luna", heroClass = HeroClass.SHADOWWEAVER)

        saveManager.createInitialSave(hero1, slot = 1)
        saveManager.createInitialSave(hero2, slot = 2)

        val slotsBefore = saveManager.getAllSlotInfos()
        assertEquals(3, slotsBefore.size)
        assertFalse(slotsBefore[0].isEmpty)
        assertFalse(slotsBefore[1].isEmpty)
        assertTrue(slotsBefore[2].isEmpty)

        // Delete Slot 2
        assertTrue(saveManager.deleteSave(slot = 2))
        assertFalse(saveManager.hasSave(2))
        assertTrue(saveManager.hasSave(1))

        val slotsAfter = saveManager.getAllSlotInfos()
        assertEquals(3, slotsAfter.size)
        assertFalse(slotsAfter[0].isEmpty)
        assertTrue(slotsAfter[1].isEmpty)
        assertTrue(slotsAfter[2].isEmpty)
    }

    @Test
    fun testStoryViewModelMultiSlotFlow() {
        val testScope = CoroutineScope(Dispatchers.Default)
        val storyVm = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )

        // Start in Slot 2
        val heroCustom = PlayerCustomization(name = "Kael", heroClass = HeroClass.ELEMENTALIST)
        storyVm.startNewGame(heroCustom, slot = 2)

        assertEquals(2, storyVm.state.value.currentSlot)
        assertEquals("Kael", storyVm.state.value.player.name)
        assertTrue(storyVm.state.value.hasExistingSave)

        // Switch to Slot 1 (which is empty)
        storyVm.selectSlot(1)
        assertEquals(1, storyVm.state.value.currentSlot)
        assertFalse(storyVm.state.value.hasExistingSave)
        assertNull(storyVm.state.value.saveSummary)

        // Switch back to Slot 2 (which has Kael)
        storyVm.selectSlot(2)
        assertEquals(2, storyVm.state.value.currentSlot)
        assertTrue(storyVm.state.value.hasExistingSave)
        assertEquals("Kael", storyVm.state.value.saveSummary?.heroName)
    }

    @Test
    fun testSlotBoundaryClamping() {
        // Slot 0 is reserved for Pure Story Mode
        saveManager.currentSlot = SaveManager.STORY_MODE_SLOT
        assertEquals(SaveManager.STORY_MODE_SLOT, saveManager.currentSlot)

        // Negative numbers clamp to slot 1
        saveManager.currentSlot = -1
        assertEquals(1, saveManager.currentSlot)

        // Numbers exceeding MAX_SLOTS clamp to MAX_SLOTS
        saveManager.currentSlot = 99
        assertEquals(SaveManager.MAX_SLOTS, saveManager.currentSlot)
    }

    @Test
    fun testPureStoryModeDedicatedSlotIsolation() {
        val testScope = CoroutineScope(Dispatchers.Default)

        // Setup campaign saves in slot 1 and 2
        val custom1 = PlayerCustomization(name = "CampaignHero1", heroClass = HeroClass.ELEMENTALIST)
        val custom2 = PlayerCustomization(name = "CampaignHero2", heroClass = HeroClass.BATTLEMAGE)
        val save1 = saveManager.createInitialSave(custom1, slot = 1).copy(saveTimestamp = 1000L)
        saveManager.save(save1, slot = 1, updateTimestamp = false)
        val save2 = saveManager.createInitialSave(custom2, slot = 2).copy(saveTimestamp = 2000L)
        saveManager.save(save2, slot = 2, updateTimestamp = false)

        val storyVm = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )

        // Slot 2 has a newer timestamp, so it should be the active campaign slot initially
        assertEquals(2, storyVm.state.value.currentSlot)
        assertEquals("CampaignHero2", storyVm.state.value.saveSummary?.heroName)

        // Start Pure Story Mode
        storyVm.startPureStoryMode(fresh = true)
        assertTrue(storyVm.state.value.isPureStoryMode)
        assertEquals(SaveManager.STORY_MODE_SLOT, storyVm.state.value.currentSlot)
        assertTrue(saveManager.hasSave(SaveManager.STORY_MODE_SLOT))

        // Ensure campaign saves in slot 1 and slot 2 are pristine
        val slot1Data = saveManager.load(1)
        val slot2Data = saveManager.load(2)
        assertNotNull(slot1Data)
        assertNotNull(slot2Data)
        assertEquals("CampaignHero1", slot1Data?.player?.name)
        assertEquals("CampaignHero2", slot2Data?.player?.name)

        // Check Story Mode summary
        val storySummary = storyVm.getStoryModeSaveSummary()
        assertNotNull(storySummary)
        assertEquals(SaveManager.STORY_MODE_SLOT, storySummary?.slotIndex)

        // Return to title screen
        storyVm.returnToTitle()
        assertFalse(storyVm.state.value.isPureStoryMode)
        // Should have restored back to the most recently saved campaign slot (slot 2)
        assertEquals(2, storyVm.state.value.currentSlot)
        assertEquals("CampaignHero2", storyVm.state.value.saveSummary?.heroName)

        // Archives list must only contain slots 1..3, never slot 0
        val archives = saveManager.getAllSlotInfos()
        assertEquals(3, archives.size)
        assertEquals(listOf(1, 2, 3), archives.map { it.slotIndex })
    }

    @Test
    fun testAutoSelectsMostRecentlySavedCampaignSlot() {
        val testScope = CoroutineScope(Dispatchers.Default)

        // Create save 1 earlier
        val save1 = saveManager.createInitialSave(
            PlayerCustomization(name = "OldHero"),
            slot = 1
        ).copy(saveTimestamp = 1000L)
        saveManager.save(save1, slot = 1, updateTimestamp = false)

        // Create save 3 later
        val save3 = saveManager.createInitialSave(
            PlayerCustomization(name = "RecentHero"),
            slot = 3
        ).copy(saveTimestamp = 5000L)
        saveManager.save(save3, slot = 3, updateTimestamp = false)

        // Boot StoryViewModel
        val storyVm = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )

        // Should automatically choose slot 3 because its timestamp is newer
        assertEquals(3, storyVm.state.value.currentSlot)
        assertEquals("RecentHero", storyVm.state.value.saveSummary?.heroName)
    }
}
