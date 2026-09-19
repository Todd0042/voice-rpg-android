package com.voicerpg.android

import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.IntentParser
import com.voicerpg.android.engine.NoveltyCache
import com.voicerpg.android.engine.ResonanceEngine
import com.voicerpg.android.model.CombatPhase
import com.voicerpg.android.model.MetaCommand
import com.voicerpg.android.viewmodel.CombatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EyesFreeAccessibilityTest {

    private lateinit var viewModel: CombatViewModel
    private lateinit var dummySpeech: SpeechManager
    private lateinit var dummyNarrator: CombatNarrator

    @Before
    fun setUp() {
        dummySpeech = SpeechManager()
        dummyNarrator = CombatNarrator()
        val testScope = CoroutineScope(Dispatchers.Default)
        viewModel = CombatViewModel(
            speechManager = dummySpeech,
            resonanceEngine = ResonanceEngine(NoveltyCache()),
            combatNarrator = dummyNarrator,
            scopeOverride = testScope
        )
    }

    @Test
    fun testMetaCommandParsingStatusAndEnemies() {
        val statusQueries = listOf("status", "report", "status report", "check status", "battle status", "health", "hp")
        for (q in statusQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected STATUS_REPORT for '$q'", MetaCommand.STATUS_REPORT, intent.metaCommand)
        }

        val enemyQueries = listOf("enemies", "check enemies", "monsters", "targets", "who is alive", "who is left", "target scan")
        for (q in enemyQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected CHECK_ENEMIES for '$q'", MetaCommand.CHECK_ENEMIES, intent.metaCommand)
        }

        val partyQueries = listOf("party", "allies", "party status", "check party", "fellowship", "team status")
        for (q in partyQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected CHECK_PARTY for '$q'", MetaCommand.CHECK_PARTY, intent.metaCommand)
        }
    }

    @Test
    fun testMetaCommandParsingTogglesAndOptions() {
        val pocketQueries = listOf("pocket mode", "eyes free", "blind mode", "screenless", "audio mode", "toggle narrator")
        for (q in pocketQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected TOGGLE_EYES_FREE for '$q'", MetaCommand.TOGGLE_EYES_FREE, intent.metaCommand)
        }

        val autoListenQueries = listOf("auto listen", "hands free", "auto mic", "automatic listening")
        for (q in autoListenQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected TOGGLE_AUTO_LISTEN for '$q'", MetaCommand.TOGGLE_AUTO_LISTEN, intent.metaCommand)
        }

        val optionsQueries = listOf("options", "settings", "menu", "open options", "show options")
        for (q in optionsQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected OPEN_OPTIONS for '$q'", MetaCommand.OPEN_OPTIONS, intent.metaCommand)
        }

        val closeQueries = listOf("close options", "close settings", "resume", "close menu")
        for (q in closeQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected CLOSE_OPTIONS for '$q'", MetaCommand.CLOSE_OPTIONS, intent.metaCommand)
        }

        val helpQueries = listOf("help", "what can i say", "commands", "voice commands")
        for (q in helpQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected HELP for '$q'", MetaCommand.HELP, intent.metaCommand)
        }
    }

    @Test
    fun testNoFalsePositiveOnSpells() {
        val normalSpells = listOf(
            "Fireball the blighted orc",
            "Frost spike archer",
            "Tempest chain lightning strike all",
            "Holy smite the shaman",
            "Soothing rain heal our party",
            "Shadow strike the rogue"
        )
        for (s in normalSpells) {
            val intent = IntentParser.parse(s)
            assertEquals("Normal incantation should not be meta command: '$s'", MetaCommand.NONE, intent.metaCommand)
        }
    }

    @Test
    fun testViewModelToggleEyesFreeMode() {
        assertFalse("Eyes-free should start disabled", viewModel.state.value.isEyesFreeMode)

        val enabled = viewModel.toggleEyesFreeMode()
        assertTrue("toggleEyesFreeMode() should return true", enabled)
        assertTrue("State should reflect isEyesFreeMode = true", viewModel.state.value.isEyesFreeMode)
        assertTrue("CombatNarrator should reflect isEyesFreeMode = true", dummyNarrator.isEyesFreeMode.value)

        val disabled = viewModel.toggleEyesFreeMode()
        assertFalse("toggleEyesFreeMode() should return false", disabled)
        assertFalse("State should reflect isEyesFreeMode = false", viewModel.state.value.isEyesFreeMode)
    }

    @Test
    fun testViewModelOpenAndCloseOptions() {
        assertFalse("Options dialog should start closed", viewModel.state.value.isOptionsOpen)

        viewModel.openOptions()
        assertTrue("Options dialog should be open", viewModel.state.value.isOptionsOpen)

        viewModel.closeOptions()
        assertFalse("Options dialog should be closed", viewModel.state.value.isOptionsOpen)
    }

    @Test
    fun testVoiceTriggerHandlesMetaCommandWithoutCasting() {
        assertFalse(viewModel.state.value.isEyesFreeMode)
        assertFalse(viewModel.state.value.isOptionsOpen)

        // Voice command to toggle pocket mode
        viewModel.processIncantation("pocket mode")
        assertTrue("Pocket mode should now be enabled via voice utterance", viewModel.state.value.isEyesFreeMode)

        // Voice command to open options menu
        viewModel.processIncantation("options")
        assertTrue("Options menu should now be opened via voice utterance", viewModel.state.value.isOptionsOpen)

        // Voice command to close options menu
        viewModel.processIncantation("close options")
        assertFalse("Options menu should now be closed via voice utterance", viewModel.state.value.isOptionsOpen)

        // Voice command to toggle auto listen
        val initialAuto = dummySpeech.isAutoListen.value
        viewModel.processIncantation("auto listen")
        assertEquals(!initialAuto, dummySpeech.isAutoListen.value)
    }

    @Test
    fun testCombatNarratorSpeakingStateAndTestingHelper() {
        assertFalse(dummyNarrator.isSpeaking.value)
        dummyNarrator.setSpeakingForTesting(true)
        assertTrue(dummyNarrator.isSpeaking.value)
        dummyNarrator.setSpeakingForTesting(false)
        assertFalse(dummyNarrator.isSpeaking.value)
    }

    @Test
    fun testNarrateSuspendMethodsReturnCleanlyWhenHeadless() = runBlocking {
        // When running in unit tests without hardware TTS, suspend functions return immediately without hanging
        dummyNarrator.setEyesFreeMode(true)
        dummyNarrator.speakSuspend("Testing speech suspend")
        dummyNarrator.narrateEnemyActionSuspend("Blighted Orc", "Aethel", 25, false)
        dummyNarrator.narrateSpellCastSuspend("Aethel", "Fireball", "Blighted Orc", 65, false, "Solar")
        dummyNarrator.narrateReinforcementsSuspend(1, listOf("Minion"))
        dummyNarrator.narrateConclusionSuspend(true)
        assertFalse(dummyNarrator.isSpeaking.value)
    }

    @Test
    fun testAtbLoopPausesWhenEyesFreeModeAndSpeakingActive() = runBlocking {
        dummyNarrator.setEyesFreeMode(true)
        dummyNarrator.setSpeakingForTesting(true)

        // Capture initial gauge
        val initialHeroGauge = viewModel.state.value.party[0].atbGauge

        // Wait 150ms while narrator is simulated speaking in eyes-free mode
        delay(150)

        // ATB gauges should NOT have advanced because combatNarrator.isSpeaking is true in eyes-free mode
        val newHeroGauge = viewModel.state.value.party[0].atbGauge
        assertEquals(initialHeroGauge, newHeroGauge, 0.001f)

        // Now clear speaking
        dummyNarrator.setSpeakingForTesting(false)
        delay(150)

        // Gauges should now advance
        val resumedHeroGauge = viewModel.state.value.party[0].atbGauge
        assertTrue("ATB gauges should advance once speaking completes", resumedHeroGauge > initialHeroGauge)
    }

    @Test
    fun testAtbLoopDoesNotPauseWhenEyesFreeModeIsDisabled() = runBlocking {
        dummyNarrator.setEyesFreeMode(false)
        dummyNarrator.setSpeakingForTesting(true)

        // Capture initial gauge
        val initialHeroGauge = viewModel.state.value.party[0].atbGauge

        // Wait 150ms while speaking is true but eyes-free mode is OFF
        delay(150)

        // In normal mode (eyes-free OFF), ATB continues fast and fluidly without pause
        val newHeroGauge = viewModel.state.value.party[0].atbGauge
        assertTrue("ATB gauges should advance when eyes-free mode is disabled", newHeroGauge > initialHeroGauge)
    }

    @Test
    fun testStandbyTransitionAndCallback() {
        var standbyCalled = false
        dummySpeech.startListening(
            onStandby = { standbyCalled = true },
            onResult = {}
        )
        dummySpeech.triggerStandbyForTesting()
        assertTrue("onStandby callback should be called", standbyCalled)
        assertEquals("SpeechState should be Standby", com.voicerpg.android.audio.SpeechState.Standby, dummySpeech.speechState.value)
        assertFalse("Session should no longer be active", dummySpeech.isSessionActive)
    }

    @Test
    fun testResumeVoiceListeningResetsState() {
        dummySpeech.triggerStandbyForTesting()
        assertEquals(com.voicerpg.android.audio.SpeechState.Standby, dummySpeech.speechState.value)

        dummySpeech.resumeListening()
        assertEquals(0, dummySpeech.getAutoListenAttemptCount())
    }

    @Test
    fun testCombatViewModelResumeVoiceListening() {
        viewModel.toggleEyesFreeMode()
        dummySpeech.triggerStandbyForTesting()
        assertEquals(com.voicerpg.android.audio.SpeechState.Standby, dummySpeech.speechState.value)

        viewModel.resumeVoiceListening()
        // resumeVoiceListening resets attempt count and requests speech recognition
        assertEquals(0, dummySpeech.getAutoListenAttemptCount())
    }

    @Test
    fun testMetaCommandParsingUnlockAndLockScreen() {
        val unlockQueries = listOf("unlock", "unlock screen", "show screen", "turn on screen", "open screen", "wake up", "wake screen", "dismiss lock", "resume screen")
        for (q in unlockQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected UNLOCK_SCREEN for '$q'", MetaCommand.UNLOCK_SCREEN, intent.metaCommand)
        }

        val lockQueries = listOf("lock", "lock screen", "lock display", "pocket lock", "blank screen", "hide screen", "dim screen")
        for (q in lockQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected LOCK_SCREEN for '$q'", MetaCommand.LOCK_SCREEN, intent.metaCommand)
        }
    }

    @Test
    fun testMetaCommandParsingEnableAndDisableEyesFree() {
        val disableQueries = listOf("exit pocket mode", "disable pocket mode", "turn off pocket mode", "stop pocket mode", "leave pocket mode", "exit eyes free", "disable eyes free")
        for (q in disableQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected DISABLE_EYES_FREE for '$q'", MetaCommand.DISABLE_EYES_FREE, intent.metaCommand)
        }

        val enableQueries = listOf("enable pocket mode", "turn on pocket mode", "start pocket mode", "enable eyes free", "turn on eyes free")
        for (q in enableQueries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected ENABLE_EYES_FREE for '$q'", MetaCommand.ENABLE_EYES_FREE, intent.metaCommand)
        }
    }

    @Test
    fun testPocketGuardLockStateAndTransitions() {
        dummyNarrator.setEyesFreeMode(false)
        assertFalse(dummyNarrator.isEyesFreeMode.value)
        assertFalse(dummyNarrator.isPocketGuardLocked.value)

        // Toggling on enables eyes-free and locks the touch guard
        val enabled = dummyNarrator.toggleEyesFreeMode()
        assertTrue(enabled)
        assertTrue(dummyNarrator.isEyesFreeMode.value)
        assertTrue(dummyNarrator.isPocketGuardLocked.value)

        // Temporarily unlocking touch guard leaves eyes free active
        dummyNarrator.unlockPocketGuard()
        assertFalse(dummyNarrator.isPocketGuardLocked.value)
        assertTrue(dummyNarrator.isEyesFreeMode.value)

        // Re-locking touch guard
        dummyNarrator.lockPocketGuard()
        assertTrue(dummyNarrator.isPocketGuardLocked.value)

        // Disabling eyes free unlocks the guard as well
        dummyNarrator.setEyesFreeMode(false)
        assertFalse(dummyNarrator.isEyesFreeMode.value)
        assertFalse(dummyNarrator.isPocketGuardLocked.value)
    }

    @Test
    fun testCombatViewModelUnlockAndLockVoiceCommands() {
        assertFalse(viewModel.state.value.isEyesFreeMode)

        // Say "pocket mode" -> enables pocket mode, locks guard, enables auto-listen
        viewModel.processIncantation("pocket mode")
        assertTrue(viewModel.state.value.isEyesFreeMode)
        assertTrue(dummyNarrator.isPocketGuardLocked.value)
        assertTrue(dummySpeech.isAutoListen.value)

        // Say "unlock" -> screen unlocked
        viewModel.processIncantation("unlock")
        assertFalse(dummyNarrator.isPocketGuardLocked.value)
        assertTrue(viewModel.state.value.isEyesFreeMode)

        // Say "lock" -> screen locked
        viewModel.processIncantation("lock")
        assertTrue(dummyNarrator.isPocketGuardLocked.value)

        // Say "disable pocket mode" -> pocket mode disabled completely
        viewModel.processIncantation("disable pocket mode")
        assertFalse(dummyNarrator.isEyesFreeMode.value)
        assertFalse(dummyNarrator.isPocketGuardLocked.value)
    }
}
