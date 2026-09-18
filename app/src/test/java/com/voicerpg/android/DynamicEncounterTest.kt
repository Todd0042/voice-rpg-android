package com.voicerpg.android

import androidx.compose.ui.graphics.Color
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.IntentParser
import com.voicerpg.android.engine.NoveltyCache
import com.voicerpg.android.engine.ResonanceEngine
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.model.BattleEnvironment
import com.voicerpg.android.model.CharacterStance
import com.voicerpg.android.model.CombatPhase
import com.voicerpg.android.model.Enemy
import com.voicerpg.android.model.PartyMember
import com.voicerpg.android.model.SavedCharacterStats
import com.voicerpg.android.model.Spell
import com.voicerpg.android.model.SpellSchool
import com.voicerpg.android.model.TargetSelection
import com.voicerpg.android.viewmodel.CombatViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class DynamicEncounterTest {

    private lateinit var viewModel: CombatViewModel

    @Before
    fun setUp() {
        val dummySpeech = SpeechManager()
        val testScope = CoroutineScope(Dispatchers.Default)
        viewModel = CombatViewModel(
            speechManager = dummySpeech,
            resonanceEngine = ResonanceEngine(NoveltyCache()),
            scopeOverride = testScope
        )
    }

    @After
    fun tearDown() {
        viewModel.cleanup()
    }

    @Test
    fun testDeveloperToolsToggleDefaultsHidden() {
        // Battle test controls must be hidden by default in every build
        assertFalse(viewModel.isDeveloperToolsEnabled.value)

        // Toggle on reveals them, toggle again hides them
        viewModel.toggleDeveloperTools()
        assertTrue(viewModel.isDeveloperToolsEnabled.value)
        viewModel.toggleDeveloperTools()
        assertFalse(viewModel.isDeveloperToolsEnabled.value)

        // Survives encounter start/restart state reconstructions
        viewModel.startEncounter(StoryEncounters.FOREST_AMBUSH)
        viewModel.toggleDeveloperTools()
        assertTrue(viewModel.isDeveloperToolsEnabled.value)
        viewModel.restartBattle()
        assertTrue(viewModel.isDeveloperToolsEnabled.value)
    }

    @Test
    fun testSoloPrologue1Hero2Enemies() {
        viewModel.startEncounter(StoryEncounters.PROLOGUE_SOLO)
        val state = viewModel.state.value

        assertEquals(1, state.party.size)
        assertEquals("hero", state.party[0].id)
        assertEquals(2, state.enemies.size)
        assertEquals(BattleEnvironment.FOREST, state.currentEnvironment)

        // Dynamic voice targeting by custom enemy name
        val intent = IntentParser.parse(
            utterance = "Fireball the shadow wisp beta!",
            availableSpells = state.party[0].spells,
            activeEnemies = state.enemies,
            party = state.party
        )

        assertEquals("fireball", intent.spell.id)
        assertEquals("wisp_2", intent.targetEnemyId)
    }

    @Test
    fun testFull6EnemyHordeEncounterAndOrdinalTargeting() {
        viewModel.startEncounter(StoryEncounters.CASTLE_HORDE)
        val state = viewModel.state.value

        assertEquals(2, state.party.size)
        assertEquals(6, state.enemies.size)
        assertEquals(BattleEnvironment.CASTLE, state.currentEnvironment)

        // Target by specific title/role
        val intentBoss = IntentParser.parse(
            utterance = "Frost spike the gate captain!",
            availableSpells = StoryEncounters.aethelSpells,
            activeEnemies = state.enemies,
            party = state.party
        )
        assertEquals("gate_captain", intentBoss.targetEnemyId)

        // Target by ordinal (first, second, third, etc.)
        val intentOrdinal = IntentParser.parse(
            utterance = "Strike enemy 2 with lightning",
            availableSpells = StoryEncounters.aethelSpells,
            activeEnemies = state.enemies,
            party = state.party
        )
        // Enemy at index 1 is "ironclad_2"
        assertEquals(state.enemies[1].id, intentOrdinal.targetEnemyId)
    }

    @Test
    fun testMidBattleReinforcementsStrict6EnemyCap() {
        // Start encounter with 4 enemies
        val initialEnemies = listOf(
            StoryEncounters.createMinion("1", "Minion 1"),
            StoryEncounters.createMinion("2", "Minion 2"),
            StoryEncounters.createMinion("3", "Minion 3"),
            StoryEncounters.createMinion("4", "Minion 4")
        )
        viewModel.startEncounter(
            party = StoryEncounters.createStandardParty(),
            enemies = initialEnemies,
            environment = BattleEnvironment.DUNGEON
        )

        assertEquals(4, viewModel.state.value.enemies.size)

        // Attempt to summon 4 reinforcements (would be 8 total, exceeding cap of 6)
        val incoming = listOf(
            StoryEncounters.createMinion("5", "Minion 5"),
            StoryEncounters.createMinion("6", "Minion 6"),
            StoryEncounters.createMinion("7", "Minion 7"),
            StoryEncounters.createMinion("8", "Minion 8")
        )

        val admitted = viewModel.summonReinforcements(incoming)

        // Exactly 2 enemies should be admitted to reach the hard maximum of 6
        assertEquals(2, admitted)
        assertEquals(6, viewModel.state.value.enemies.size)
        assertEquals(6, viewModel.state.value.enemies.count { it.isAlive })

        // A subsequent summon while at 6 alive enemies must admit 0
        val extra = viewModel.summonReinforcements(listOf(StoryEncounters.createMinion("9", "Minion 9")))
        assertEquals(0, extra)
        assertEquals(6, viewModel.state.value.enemies.size)
    }

    @Test
    fun testReinforcementsAdmittedWhenEnemiesAreDefeated() {
        val initialEnemies = listOf(
            StoryEncounters.createMinion("1", "Minion 1", hp = 100),
            StoryEncounters.createMinion("2", "Minion 2", hp = 0), // Already defeated!
            StoryEncounters.createMinion("3", "Minion 3", hp = 100),
            StoryEncounters.createMinion("4", "Minion 4", hp = 0)  // Already defeated!
        )
        viewModel.startEncounter(
            party = StoryEncounters.createStandardParty(),
            enemies = initialEnemies,
            environment = BattleEnvironment.CAVE
        )

        // 2 alive enemies out of 4 total on field
        val currentAlive = viewModel.state.value.enemies.count { it.isAlive }
        assertEquals(2, currentAlive)

        // (6 - 2) = 4 available slots
        val reinforcements = listOf(
            StoryEncounters.createMinion("r1", "Reinforcement 1"),
            StoryEncounters.createMinion("r2", "Reinforcement 2"),
            StoryEncounters.createMinion("r3", "Reinforcement 3"),
            StoryEncounters.createMinion("r4", "Reinforcement 4"),
            StoryEncounters.createMinion("r5", "Reinforcement 5")
        )

        val admitted = viewModel.summonReinforcements(reinforcements)
        assertEquals(4, admitted) // Only 4 admitted to reach cap of 6 alive
        assertEquals(6, viewModel.state.value.enemies.count { it.isAlive })
    }

    @Test
    fun testDynamicPartyHealingTargetResolution() {
        val party = StoryEncounters.createStandardParty()
        val spells = StoryEncounters.lyraSpells

        val healIntentCedric = IntentParser.parse(
            utterance = "Spirits of the grove, mend Sir Cedric's wounds!",
            availableSpells = spells,
            activeEnemies = emptyList(),
            party = party
        )
        assertEquals("soothing_rain", healIntentCedric.spell.id)
        assertEquals("cedric", healIntentCedric.targetHeroId)

        val healIntentParty = IntentParser.parse(
            utterance = "Soothing rain upon our entire party",
            availableSpells = spells,
            activeEnemies = emptyList(),
            party = party
        )
        assertEquals(TargetSelection.PARTY_LOWEST, healIntentParty.target)
    }

    @Test
    fun testPlayerIncantationAgainstReinforcedMinion() {
        viewModel.startEncounter(StoryEncounters.DUNGEON_DESCENT)

        // Spawn a reinforcement mid-battle
        val summonSuccess = viewModel.spawnEnemy(
            StoryEncounters.createMinion("bone_guard", "Bone Vanguard", hp = 200)
        )
        assertTrue(summonSuccess)

        val activeEnemies = viewModel.state.value.enemies
        assertTrue(activeEnemies.any { it.name == "Bone Vanguard" })

        // Target the summoned minion by voice
        val intent = IntentParser.parse(
            utterance = "Holy smite the bone vanguard!",
            availableSpells = StoryEncounters.cedricSpells,
            activeEnemies = activeEnemies,
            party = viewModel.state.value.party
        )

        assertEquals("holy_smite", intent.spell.id)
        assertEquals("minion_bone_guard", intent.targetEnemyId)
    }

    @Test
    fun testActIEncountersOnlyIncludeDuoPartyAndExcludeLyra() {
        val actIEncounters = listOf(
            StoryEncounters.FOREST_AMBUSH,
            StoryEncounters.DUNGEON_DESCENT,
            StoryEncounters.CASTLE_HORDE,
            StoryEncounters.CAVE_BROODMOTHER,
            StoryEncounters.BLIGHT_TRACKERS,
            StoryEncounters.CH3_SENTINELS
        )

        for (encounter in actIEncounters) {
            val party = encounter.initialParty ?: emptyList()
            assertEquals("Encounter ${encounter.id} should have exactly 2 heroes in Act I", 2, party.size)
            assertTrue("Encounter ${encounter.id} must not contain Lyra before Chapter 5", party.none { it.id == "lyra" })
            assertTrue("Encounter ${encounter.id} should contain Aethel", party.any { it.id == "hero" })
            assertTrue("Encounter ${encounter.id} should contain Sir Cedric", party.any { it.id == "cedric" })
        }

        // MARSH_RESCUE is Chapter 5 and is a duo rescue mission before Lyra joins
        val ch5RescueParty = StoryEncounters.MARSH_RESCUE.initialParty ?: emptyList()
        assertEquals(2, ch5RescueParty.size)
        assertTrue(ch5RescueParty.none { it.id == "lyra" })

        // SWAMP_BEHEMOTH is Chapter 6 and introduces Lyra into the active combat team
        val ch6Party = StoryEncounters.SWAMP_BEHEMOTH.initialParty ?: emptyList()
        assertEquals(3, ch6Party.size)
        assertTrue(ch6Party.any { it.id == "lyra" })
    }

    @Test
    fun testActIIAndBeyondPartyRosterIntegrity() {
        // Chapter 7: Trio party (Aethel, Cedric, Lyra)
        val ch7Party = StoryEncounters.CH7_MIRE_WYRM.initialParty ?: emptyList()
        assertEquals(3, ch7Party.size)
        assertTrue(ch7Party.any { it.id == "hero" })
        assertTrue(ch7Party.any { it.id == "cedric" })
        assertTrue(ch7Party.any { it.id == "lyra" })
        assertTrue(ch7Party.none { it.id == "zephyr" })

        // Chapter 8: Ambush begins as Trio before Zephyr defects
        val ch8Party = StoryEncounters.CH8_EXECUTIONER_AMBUSH.initialParty ?: emptyList()
        assertEquals(3, ch8Party.size)
        assertTrue(ch8Party.none { it.id == "zephyr" })

        // Chapters 9 through 16: Full Quad Party (Aethel, Cedric, Lyra, Zephyr)
        val quadEncounters = listOf(
            StoryEncounters.CH9_GALAHAULT_TRIAL,
            StoryEncounters.CH10_BROODMOTHER_TRIAL,
            StoryEncounters.CH11_NOCTURNE_TRIAL,
            StoryEncounters.CH12_WARMASTER_OUROS,
            StoryEncounters.CH13_COMMANDER_VAELOR,
            StoryEncounters.CH14_ABYSSAL_LEVIATHAN,
            StoryEncounters.CH15_ARCHON_CUSTODIANS,
            StoryEncounters.CH16_MALAKOR_FINALE
        )

        for (enc in quadEncounters) {
            val party = enc.initialParty ?: emptyList()
            assertEquals("Encounter ${enc.id} must feature the full 4-hero party", 4, party.size)
            assertTrue("Encounter ${enc.id} must include Aethel", party.any { it.id == "hero" })
            assertTrue("Encounter ${enc.id} must include Cedric", party.any { it.id == "cedric" })
            assertTrue("Encounter ${enc.id} must include Lyra", party.any { it.id == "lyra" })
            assertTrue("Encounter ${enc.id} must include Zephyr", party.any { it.id == "zephyr" })
        }
    }

    @Test
    fun testMidBattleZephyrDefectionAndRecruitment() {
        viewModel.startEncounter(StoryEncounters.CH8_EXECUTIONER_AMBUSH)
        val initialParty = viewModel.state.value.party
        assertEquals(3, initialParty.size)
        assertFalse(viewModel.isZephyrRecruitedMidBattle)

        // Mid-battle trigger: Zephyr turns his daggers against Executioner Kaelen
        viewModel.recruitZephyrMidBattle()

        assertTrue(viewModel.isZephyrRecruitedMidBattle)
        val updatedParty = viewModel.state.value.party
        assertEquals(4, updatedParty.size)
        val zephyr = updatedParty.firstOrNull { it.id == "zephyr" }
        assertNotNull(zephyr)
        assertEquals("Zephyr", zephyr?.name)
        assertEquals(1.0f, zephyr?.atbGauge)
    }

    @Test
    fun testFinalBossPhase3DeathOfVoiceAndPrimordialSyllable() {
        viewModel.startEncounter(StoryEncounters.CH16_MALAKOR_FINALE)
        assertEquals("ch16_malakor_finale", viewModel.currentEncounterId)
        assertFalse(viewModel.isPhase3Triggered)

        // Phase 3 trigger: Malakor suppresses all sound
        viewModel.triggerPhase3DeathOfVoice()
        assertTrue(viewModel.isPhase3Triggered)
        assertTrue(viewModel.sfxManager.isMuted)

        // Set player turn ready
        viewModel.setPlayerInputPhaseForTesting("hero")

        // When the player speaks the Primordial Incantation in unison:
        viewModel.processIncantation("Morning star cataclysm oblivion primordial syllable")

        // Wait briefly for coroutine launch
        Thread.sleep(150)

        // Silence is shattered!
        assertFalse(viewModel.sfxManager.isMuted)
        val resonance = viewModel.state.value.lastResonance
        assertNotNull(resonance)
        assertEquals(200, resonance?.bonusPercent)
        assertEquals(com.voicerpg.android.model.ResonanceTier.TRANSCENDENTAL, resonance?.tier)
    }

    @Test
    fun testCompanionStarterSpellKitsDoNotIncludeMasterTrialSpells() {
        val duo = StoryEncounters.createDuoParty()
        val cedric = duo.first { it.id == "cedric" }
        assertFalse("Cedric starter kit should not include Aegis of Dawn", cedric.spells.any { it.id == "aegis_dawn" })
        assertTrue("Cedric starter kit should include Holy Smite", cedric.spells.any { it.id == "holy_smite" })
        assertTrue("Cedric starter kit should include Steady Breath", cedric.spells.any { it.id == "steady_breath" })

        val trio = StoryEncounters.createTrioParty()
        val lyra = trio.first { it.id == "lyra" }
        assertFalse("Lyra starter kit should not include Verdant Cataclysm", lyra.spells.any { it.id == "verdant_cataclysm" })
        assertTrue("Lyra starter kit should include Soothing Rain", lyra.spells.any { it.id == "soothing_rain" })
        assertTrue("Lyra starter kit should include Deep Root", lyra.spells.any { it.id == "deep_root" })

        val zephyr = StoryEncounters.createZephyrMember()
        assertFalse("Zephyr starter kit should not include Umbral Siphon", zephyr.spells.any { it.id == "umbral_siphon" })
        assertTrue("Zephyr starter kit should include Shadow Strike", zephyr.spells.any { it.id == "shadow_strike" })
        assertTrue("Zephyr starter kit should include Quiet Lungs", zephyr.spells.any { it.id == "quiet_lungs" })
    }

    @Test
    fun testRoundNumberAdvancesWhenAllCombatantsAct() {
        val party = StoryEncounters.createDuoParty()
        val enemy = StoryEncounters.createMinion("dummy", "Dummy Target", hp = 500)
        viewModel.startEncounter(party = party, enemies = listOf(enemy), environment = BattleEnvironment.DUNGEON)

        assertEquals(1, viewModel.state.value.roundNumber)

        // Hero acts
        viewModel.registerTurnCompleted()
        assertEquals(1, viewModel.state.value.roundNumber)

        // Cedric acts
        viewModel.registerTurnCompleted()
        assertEquals(1, viewModel.state.value.roundNumber)

        // Enemy acts -> total alive combatants is 3 (hero, cedric, enemy)
        viewModel.registerTurnCompleted()
        assertEquals(2, viewModel.state.value.roundNumber)
    }

    @Test
    fun testApplyImportedProgressionMapsSpellsAndAppendsBreath() {
        val savedParty = listOf(
            SavedCharacterStats(
                id = "cedric",
                name = "Sir Cedric",
                loreClass = "Templar",
                currentHp = 100,
                maxHp = 120,
                currentMp = 40,
                maxMp = 50,
                speed = 40,
                level = 10,
                xp = 100,
                spellIds = listOf("holy_smite", "shield_slam", "lay_on_hands", "aegis_dawn")
            )
        )
        viewModel.applySavedStats(savedParty)
        viewModel.startEncounter(StoryEncounters.FOREST_AMBUSH)

        val cedric = viewModel.state.value.party.first { it.id == "cedric" }
        assertEquals(10, cedric.level)
        assertTrue(cedric.spells.any { it.id == "aegis_dawn" })
        assertEquals("steady_breath", cedric.spells.last().id)
    }
}
