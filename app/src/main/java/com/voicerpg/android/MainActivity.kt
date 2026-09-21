package com.voicerpg.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.MusicManager
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.SaveManager
import kotlinx.coroutines.launch
import com.voicerpg.android.model.GameScreen
import com.voicerpg.android.ui.combat.DebugWarpDialog
import com.voicerpg.android.ui.combat.OptionsDialog
import com.voicerpg.android.ui.combat.PlaystylePreset
import com.voicerpg.android.ui.combat.RetroBattleScreen
import com.voicerpg.android.ui.creation.CharacterCreationScreen
import com.voicerpg.android.ui.pocket.PocketHeroVitals
import com.voicerpg.android.ui.pocket.PocketModeTouchGuard
import com.voicerpg.android.ui.pocket.PocketModeUnlockedBanner
import com.voicerpg.android.ui.setup.AudioSetupScreen
import com.voicerpg.android.ui.setup.MicPermissionRationaleDialog
import com.voicerpg.android.ui.story.StoryScreen
import com.voicerpg.android.ui.theme.VoiceRPGTheme
import com.voicerpg.android.ui.title.TitleScreen
import com.voicerpg.android.ui.tutorial.TutorialBattleScreen
import com.voicerpg.android.viewmodel.CombatViewModel
import com.voicerpg.android.viewmodel.StoryViewModel
import com.voicerpg.android.viewmodel.TutorialBattleViewModel

class MainActivity : ComponentActivity() {

    private lateinit var speechManager: SpeechManager
    private lateinit var combatNarrator: CombatNarrator
    private lateinit var musicManager: MusicManager
    private lateinit var saveManager: SaveManager
    private lateinit var combatViewModel: CombatViewModel
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var tutorialBattleViewModel: TutorialBattleViewModel

    private val isPermanentlyDeniedState = mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (combatViewModel.speechManager.isAutoListen.value) {
                combatViewModel.startVoiceListening()
            }
        } else {
            val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
            if (!shouldShow) {
                isPermanentlyDeniedState.value = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        speechManager = SpeechManager(this)
        combatNarrator = CombatNarrator(this)
        musicManager = MusicManager(this)
        saveManager = SaveManager(this)

        combatViewModel = CombatViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            musicManager = musicManager
        )
        storyViewModel = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            musicManager = musicManager
        )
        tutorialBattleViewModel = TutorialBattleViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            musicManager = musicManager
        )

        // Connect automatic speech ducking: BGM ducks during narration, smoothly restoring after
        lifecycleScope.launch {
            combatNarrator.isSpeaking.collect { speaking ->
                if (speaking) {
                    musicManager.duckForSpeech()
                } else {
                    musicManager.restoreFromSpeech()
                }
            }
        }

        // Apply saved or initial character customization to combat
        combatViewModel.applyPlayerCustomization(
            customization = storyViewModel.state.value.player,
            savedStats = storyViewModel.state.value.partyStats
        )

        // Connect options modal triggers between story and combat
        storyViewModel.onOpenOptions = {
            storyViewModel.cancelPendingAutoAdvance()
            combatViewModel.openOptions()
        }
        storyViewModel.onCloseOptions = { combatViewModel.closeOptions() }
        combatViewModel.onContinueStory = {
            storyViewModel.updatePartyStatsFromCombat(combatViewModel.state.value.party)
            storyViewModel.onCombatVictory()
        }

        setContent {
            val storyState by storyViewModel.state.collectAsState()
            val combatState by combatViewModel.state.collectAsState()

            var showMicRationale by remember {
                mutableStateOf(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            }
            val isPermanentlyDenied by isPermanentlyDeniedState

            val isEyesFreeMode by combatNarrator.isEyesFreeMode.collectAsState()
            val isPocketGuardLocked by combatNarrator.isPocketGuardLocked.collectAsState()
            val isPocketGuardEnabled by combatNarrator.isPocketGuardEnabled.collectAsState()
            val isCombatNarrationEnabled by combatNarrator.isCombatNarrationEnabled.collectAsState()
            val speechState by speechManager.speechState.collectAsState()
            val rmsLevel by speechManager.rmsLevel.collectAsState()
            val isTtsSpeaking by combatNarrator.isSpeaking.collectAsState()
            val isNarrationEnabled by combatNarrator.isNarrationEnabled.collectAsState()
            val isReadChoicesEnabled by combatNarrator.isReadChoicesEnabled.collectAsState()
            val isCharacterPitchEnabled by combatNarrator.isCharacterPitchEnabled.collectAsState()
            val isSpeakerAttributionEnabled by combatNarrator.isSpeakerAttributionEnabled.collectAsState()
            val speechRate by combatNarrator.speechRate.collectAsState()
            val isAutoListen by speechManager.isAutoListen.collectAsState()
            val isChimeMuted by speechManager.isChimeMuted.collectAsState()
            val isMusicEnabled by musicManager.isMusicEnabled.collectAsState()
            val musicVolume by musicManager.musicVolume.collectAsState()
            var showDebugWarp by remember { mutableStateOf(false) }
            val isDeveloperToolsEnabled by combatViewModel.isDeveloperToolsEnabled.collectAsState()

            // Keep screen on during Pocket Mode and dim AMOLED display to save power if touch guard enabled
            DisposableEffect(isEyesFreeMode, isPocketGuardLocked, isPocketGuardEnabled) {
                if (isEyesFreeMode) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    if (isPocketGuardLocked && isPocketGuardEnabled) {
                        val lp = window.attributes
                        lp.screenBrightness = 0.01f
                        window.attributes = lp
                    } else {
                        val lp = window.attributes
                        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                        window.attributes = lp
                    }
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    val lp = window.attributes
                    lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                    window.attributes = lp
                }
                onDispose {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    val lp = window.attributes
                    lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                    window.attributes = lp
                }
            }

            LaunchedEffect(storyState.player) {
                combatViewModel.applyPlayerCustomization(
                    customization = storyState.player,
                    savedStats = storyState.partyStats
                )
            }

            LaunchedEffect(storyState.activeEncounter) {
                storyState.activeEncounter?.let { encounter ->
                    combatViewModel.applySavedStats(storyState.partyStats)
                    combatViewModel.startEncounter(encounter)
                    combatViewModel.setPureStoryMode(storyState.isPureStoryMode)
                }
            }

            LaunchedEffect(storyState.isPureStoryMode) {
                combatViewModel.setPureStoryMode(storyState.isPureStoryMode)
            }

            LaunchedEffect(storyState.currentScene.id, storyState.gameScreen) {
                if (storyState.gameScreen == GameScreen.COMBAT_ARENA) {
                    musicManager.playCombatMusic()
                } else if (storyState.gameScreen == GameScreen.STORY_EXPLORATION) {
                    musicManager.playTrack(storyState.currentScene.musicAsset)
                } else if (storyState.gameScreen == GameScreen.TITLE || storyState.gameScreen == GameScreen.TUTORIAL) {
                    musicManager.playTrack(MusicManager.TRACK_TITLE)
                } else {
                    musicManager.playTrack(MusicManager.TRACK_ACT1_FOREST)
                }
            }

            VoiceRPGTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (storyState.gameScreen) {
                        GameScreen.TITLE -> {
                            val speechState by speechManager.speechState.collectAsState()
                            TitleScreen(
                                hasSave = storyState.hasExistingSave,
                                saveSummary = storyState.saveSummary,
                                saveSlots = storyState.saveSlots,
                                currentSlot = storyState.currentSlot,
                                speechState = speechState,
                                isAutoListen = isAutoListen,
                                onContinue = { storyViewModel.continueGame() },
                                onNewGame = { storyViewModel.startNewGameFlow() },
                                onSelectSlot = { slot -> storyViewModel.selectSlot(slot) },
                                onDeleteSlot = { slot -> storyViewModel.deleteSlot(slot) },
                                onContinueSlot = { slot -> storyViewModel.continueGame(slot) },
                                onNewGameInSlot = { slot -> storyViewModel.startNewGameFlow(slot) },
                                onAudioSetup = { storyViewModel.openAudioSetup() },
                                onOptions = { combatViewModel.openOptions() },
                                storyModeSummary = storyViewModel.getStoryModeSaveSummary(),
                                onPureStoryMode = { fresh -> storyViewModel.startPureStoryMode(fresh) },
                                onTutorial = {
                                    tutorialBattleViewModel.resetTutorial()
                                    storyViewModel.openTutorial()
                                },
                                onStartListening = {
                                    speechManager.startListening(
                                        onResult = { storyViewModel.handleStoryVoiceInput(it) }
                                    )
                                },
                                onStopListening = { speechManager.stopListening() }
                            )
                        }
                        GameScreen.TUTORIAL -> {
                            TutorialBattleScreen(
                                viewModel = tutorialBattleViewModel,
                                onBack = { storyViewModel.returnFromTutorial() },
                                onStartNewGame = { storyViewModel.startNewGameFlow() }
                            )
                        }
                        GameScreen.AUDIO_SETUP -> {
                            AudioSetupScreen(
                                combatNarrator = combatNarrator,
                                speechManager = speechManager,
                                isFromGame = storyState.previousScreen != null && !storyState.isNewGameFlow && storyState.previousScreen != GameScreen.TITLE,
                                isNewGameFlow = storyState.isNewGameFlow,
                                onBack = {
                                    storyViewModel.returnFromAudioSetup()
                                },
                                onVoiceChanged = {
                                    storyViewModel.persistCurrentState()
                                },
                                onProceed = {
                                    if (storyState.isNewGameFlow) {
                                        storyViewModel.proceedToCharacterCreation()
                                    } else {
                                        storyViewModel.returnFromAudioSetup()
                                    }
                                }
                            )
                        }
                        GameScreen.CHARACTER_CREATION -> {
                            CharacterCreationScreen(
                                initialCustomization = storyState.player,
                                speechManager = speechManager,
                                combatNarrator = combatNarrator,
                                onBack = {
                                    if (storyState.isNewGameFlow) {
                                        storyViewModel.startNewGameFlow(storyState.currentSlot)
                                    } else {
                                        storyViewModel.returnToTitle()
                                    }
                                },
                                onConfirmCharacter = { customization ->
                                    storyViewModel.startNewGame(customization, storyState.currentSlot)
                                    combatViewModel.applyPlayerCustomization(customization)
                                }
                            )
                        }
                        GameScreen.STORY_EXPLORATION -> {
                            StoryScreen(
                                storyViewModel = storyViewModel,
                                combatViewModel = combatViewModel,
                                onOpenOptions = { combatViewModel.openOptions() }
                            )
                        }
                        GameScreen.COMBAT_ARENA -> {
                            RetroBattleScreen(
                                viewModel = combatViewModel,
                                onReturnToStory = { storyViewModel.switchToStory() },
                                onContinueStory = {
                                    storyViewModel.updatePartyStatsFromCombat(combatViewModel.state.value.party)
                                    storyViewModel.onCombatVictory()
                                }
                            )
                        }
                    }

                    // Global Universal Options & Narration Modal
                    OptionsDialog(
                        isOpen = combatState.isOptionsOpen,
                        isEyesFreeMode = isEyesFreeMode,
                        isNarrationEnabled = isNarrationEnabled,
                        isCombatNarrationEnabled = isCombatNarrationEnabled,
                        isPocketGuardEnabled = isPocketGuardEnabled,
                        isReadChoicesEnabled = isReadChoicesEnabled,
                        isCharacterPitchEnabled = isCharacterPitchEnabled,
                        isSpeakerAttributionEnabled = isSpeakerAttributionEnabled,
                        speechRate = speechRate,
                        isAutoListen = isAutoListen,
                        isChimeMuted = isChimeMuted,
                        onToggleEyesFreeMode = {
                            val enabled = combatNarrator.toggleEyesFreeMode()
                            if (enabled) {
                                speechManager.setAutoListen(true)
                            }
                            storyViewModel.persistCurrentState()
                        },
                        onToggleNarration = {
                            combatNarrator.toggleNarration()
                            storyViewModel.persistCurrentState()
                        },
                        onToggleCombatNarration = {
                            combatNarrator.toggleCombatNarrationEnabled()
                            storyViewModel.persistCurrentState()
                        },
                        onTogglePocketGuard = {
                            combatNarrator.togglePocketGuardEnabled()
                            storyViewModel.persistCurrentState()
                        },
                        onApplyPreset = { preset ->
                            when (preset) {
                                PlaystylePreset.POCKET_WALK -> {
                                    combatNarrator.setPocketGuardEnabled(true)
                                    combatNarrator.setCombatNarrationEnabled(true)
                                    combatNarrator.setNarrationEnabled(true)
                                    combatNarrator.setReadChoicesEnabled(true)
                                    combatNarrator.setEyesFreeMode(true, lockGuard = true)
                                    speechManager.setAutoListen(true)
                                    speechManager.setChimeMuted(true)
                                }
                                PlaystylePreset.STORYBOOK -> {
                                    combatNarrator.setPocketGuardEnabled(false)
                                    combatNarrator.setCombatNarrationEnabled(true)
                                    combatNarrator.setNarrationEnabled(true)
                                    combatNarrator.setReadChoicesEnabled(true)
                                    combatNarrator.setEyesFreeMode(true, lockGuard = false)
                                    speechManager.setAutoListen(true)
                                    speechManager.setChimeMuted(false)
                                }
                                PlaystylePreset.CLASSIC_TACTICAL -> {
                                    combatNarrator.setPocketGuardEnabled(false)
                                    combatNarrator.setCombatNarrationEnabled(false)
                                    combatNarrator.setNarrationEnabled(true)
                                    combatNarrator.setReadChoicesEnabled(false)
                                    combatNarrator.setEyesFreeMode(false, lockGuard = false)
                                    speechManager.setAutoListen(false)
                                    speechManager.setChimeMuted(false)
                                }
                                PlaystylePreset.CUSTOM -> {}
                            }
                            storyViewModel.persistCurrentState()
                        },
                        onToggleReadChoices = {
                            combatNarrator.toggleReadChoices()
                            storyViewModel.persistCurrentState()
                        },
                        onToggleCharacterPitch = {
                            combatNarrator.setCharacterPitchEnabled(!isCharacterPitchEnabled)
                            storyViewModel.persistCurrentState()
                        },
                        onToggleSpeakerAttribution = {
                            combatNarrator.toggleSpeakerAttribution()
                            storyViewModel.persistCurrentState()
                        },
                        onSpeechRateChange = { rate ->
                            combatNarrator.setSpeechRate(rate)
                            storyViewModel.persistCurrentState()
                        },
                        onToggleAutoListen = {
                            speechManager.toggleAutoListen()
                            storyViewModel.persistCurrentState()
                        },
                        onToggleChimeMute = {
                            speechManager.toggleChimeMute()
                            storyViewModel.persistCurrentState()
                        },
                        isMusicEnabled = isMusicEnabled,
                        musicVolume = musicVolume,
                        onToggleMusic = {
                            musicManager.toggleMusic()
                            storyViewModel.persistCurrentState()
                        },
                        onMusicVolumeChange = { vol ->
                            musicManager.setVolume(vol)
                            storyViewModel.persistCurrentState()
                        },
                        onOpenVoiceSettings = {
                            combatNarrator.openVoiceSettings(this@MainActivity)
                        },
                        onOpenVoiceAssignment = {
                            combatViewModel.closeOptions()
                            storyViewModel.openAudioSetup()
                        },
                        onTriggerCheaterDialogue = {
                            combatNarrator.speak(
                                "Cheater detected! You have invoked the ancient Konami incantation to bend time and space. Do you boldly embrace your dishonor?",
                                force = true
                            )
                        },
                        onOpenDebugWarp = { showDebugWarp = true },
                        onReturnToTitle = {
                            combatViewModel.closeOptions()
                            storyViewModel.returnToTitle()
                        },
                        onClose = { combatViewModel.closeOptions() }
                    )

                    // Universal chapter warp & developer tools dialog
                    DebugWarpDialog(
                        isOpen = showDebugWarp,
                        targets = StoryViewModel.DEBUG_CHAPTER_TARGETS,
                        developerToolsEnabled = isDeveloperToolsEnabled,
                        onToggleDeveloperTools = { combatViewModel.toggleDeveloperTools() },
                        onWarpTo = { nodeId ->
                            showDebugWarp = false
                            combatViewModel.closeOptions()
                            storyViewModel.debugWarpToChapter(nodeId)
                        },
                        onClose = { showDebugWarp = false }
                    )

                    // Compute glanceable party status and current location for AMOLED Pocket Lock
                    val heroVitals = remember(storyState.gameScreen, combatState.party, storyState.partyStats, storyState.player) {
                        if (storyState.gameScreen == GameScreen.COMBAT_ARENA && combatState.party.isNotEmpty()) {
                            combatState.party.map {
                                PocketHeroVitals(
                                    name = it.name,
                                    loreClass = it.loreClass,
                                    currentHp = it.currentHp,
                                    maxHp = it.maxHp,
                                    currentMp = it.currentMp,
                                    maxMp = it.maxMp
                                )
                            }
                        } else if (storyState.partyStats.isNotEmpty()) {
                            storyState.partyStats.map {
                                PocketHeroVitals(
                                    name = it.name,
                                    loreClass = it.loreClass,
                                    currentHp = it.currentHp,
                                    maxHp = it.maxHp,
                                    currentMp = it.currentMp,
                                    maxMp = it.maxMp
                                )
                            }
                        } else {
                            listOf(
                                PocketHeroVitals(
                                    name = storyState.player.name,
                                    loreClass = storyState.player.heroClass.title,
                                    currentHp = storyState.player.heroClass.startingHp,
                                    maxHp = storyState.player.heroClass.startingHp,
                                    currentMp = storyState.player.heroClass.startingMp,
                                    maxMp = storyState.player.heroClass.startingMp
                                )
                            )
                        }
                    }

                    val locationTitle: String = remember(storyState.gameScreen, storyState.currentScene, storyState.activeEncounter, combatState.enemies) {
                        if (storyState.gameScreen == GameScreen.COMBAT_ARENA) {
                            storyState.activeEncounter?.name?.let { "Battle: $it" }
                                ?: (combatState.enemies.firstOrNull()?.let { "Battle: ${it.name}" } ?: "Combat Arena")
                        } else if (storyState.gameScreen == GameScreen.STORY_EXPLORATION) {
                            "${storyState.currentScene.chapterTitle} • ${storyState.currentScene.name}"
                        } else if (storyState.gameScreen == GameScreen.TITLE) {
                            "Title Screen"
                        } else if (storyState.gameScreen == GameScreen.TUTORIAL) {
                            "Chronicle Guide"
                        } else {
                            "Echoes of the Logos"
                        }
                    }

                    // Floating banner when pocket mode is active but screen is temporarily unlocked (and pocket guard is enabled)
                    if (isEyesFreeMode && !isPocketGuardLocked && isPocketGuardEnabled) {
                        PocketModeUnlockedBanner(
                            onLock = {
                                combatNarrator.lockPocketGuard()
                                combatNarrator.speak("Screen locked. Pocket mode active.", force = true)
                            },
                            onExitPocketMode = {
                                combatNarrator.setEyesFreeMode(false)
                                combatNarrator.speak("Pocket mode disabled.", force = true)
                                storyViewModel.persistCurrentState()
                            },
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }

                    // Full-screen AMOLED true-black touch guard when pocket mode is active and locked (and pocket guard is enabled)
                    if (isEyesFreeMode && isPocketGuardLocked && isPocketGuardEnabled) {
                        PocketModeTouchGuard(
                            party = heroVitals,
                            locationTitle = locationTitle,
                            speechState = speechState,
                            rmsLevel = rmsLevel,
                            isTtsSpeaking = isTtsSpeaking,
                            onUnlock = {
                                combatNarrator.unlockPocketGuard()
                                combatNarrator.speak("Screen unlocked.", force = true)
                            },
                            onExitPocketMode = {
                                combatNarrator.setEyesFreeMode(false)
                                combatNarrator.speak("Pocket mode disabled.", force = true)
                                storyViewModel.persistCurrentState()
                            }
                        )
                    }

                    // Educational Pre-Permission Rationale Modal Dialog
                    if (showMicRationale) {
                        MicPermissionRationaleDialog(
                            isPermanentlyDenied = isPermanentlyDenied,
                            onRequestPermission = {
                                val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.RECORD_AUDIO)
                                if (!shouldShow && isPermanentlyDenied) {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", packageName, null)
                                    }
                                    startActivity(intent)
                                } else {
                                    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                                showMicRationale = false
                            },
                            onOpenSettings = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                }
                                startActivity(intent)
                                showMicRationale = false
                            },
                            onDismiss = {
                                showMicRationale = false
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        storyViewModel.persistCurrentState()
        speechManager.destroy()
        combatNarrator.destroy()
        musicManager.destroy()
    }
}
