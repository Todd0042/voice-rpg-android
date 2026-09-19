package com.voicerpg.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.voicerpg.android.ui.combat.RetroBattleScreen
import com.voicerpg.android.ui.creation.CharacterCreationScreen
import com.voicerpg.android.ui.setup.AudioSetupScreen
import com.voicerpg.android.ui.story.StoryScreen
import com.voicerpg.android.ui.theme.VoiceRPGTheme
import com.voicerpg.android.ui.title.TitleScreen
import com.voicerpg.android.viewmodel.CombatViewModel
import com.voicerpg.android.viewmodel.StoryViewModel

class MainActivity : ComponentActivity() {

    private lateinit var speechManager: SpeechManager
    private lateinit var combatNarrator: CombatNarrator
    private lateinit var musicManager: MusicManager
    private lateinit var saveManager: SaveManager
    private lateinit var combatViewModel: CombatViewModel
    private lateinit var storyViewModel: StoryViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && combatViewModel.speechManager.isAutoListen.value) {
            combatViewModel.startVoiceListening()
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

        checkAudioPermission()

        setContent {
            val storyState by storyViewModel.state.collectAsState()
            val combatState by combatViewModel.state.collectAsState()

            val isEyesFreeMode by combatNarrator.isEyesFreeMode.collectAsState()
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
                }
            }

            LaunchedEffect(storyState.currentScene.id, storyState.gameScreen) {
                if (storyState.gameScreen == GameScreen.COMBAT_ARENA) {
                    musicManager.playCombatMusic()
                } else if (storyState.gameScreen == GameScreen.STORY_EXPLORATION) {
                    musicManager.playTrack(storyState.currentScene.musicAsset)
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
                                speechState = speechState,
                                isAutoListen = isAutoListen,
                                onContinue = { storyViewModel.continueGame() },
                                onNewGame = { storyViewModel.startNewGameFlow() },
                                onAudioSetup = { storyViewModel.openAudioSetup() },
                                onOptions = { combatViewModel.openOptions() },
                                onStartListening = {
                                    speechManager.startListening(
                                        onResult = { storyViewModel.handleStoryVoiceInput(it) }
                                    )
                                },
                                onStopListening = { speechManager.stopListening() }
                            )
                        }
                        GameScreen.AUDIO_SETUP -> {
                            AudioSetupScreen(
                                combatNarrator = combatNarrator,
                                speechManager = speechManager,
                                isFromGame = storyState.previousScreen != null,
                                onBack = {
                                    storyViewModel.returnFromAudioSetup()
                                },
                                onVoiceChanged = {
                                    storyViewModel.persistCurrentState()
                                },
                                onProceed = {
                                    if (storyState.previousScreen == GameScreen.TITLE) {
                                        storyViewModel.returnFromAudioSetup()
                                    } else if (storyState.previousScreen != null) {
                                        storyViewModel.returnFromAudioSetup()
                                    } else {
                                        storyViewModel.proceedToCharacterCreation()
                                    }
                                }
                            )
                        }
                        GameScreen.CHARACTER_CREATION -> {
                            CharacterCreationScreen(
                                initialCustomization = storyState.player,
                                speechManager = speechManager,
                                combatNarrator = combatNarrator,
                                onConfirmCharacter = { customization ->
                                    storyViewModel.startNewGame(customization)
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
                        isReadChoicesEnabled = isReadChoicesEnabled,
                        isCharacterPitchEnabled = isCharacterPitchEnabled,
                        isSpeakerAttributionEnabled = isSpeakerAttributionEnabled,
                        speechRate = speechRate,
                        isAutoListen = isAutoListen,
                        isChimeMuted = isChimeMuted,
                        onToggleEyesFreeMode = {
                            combatNarrator.toggleEyesFreeMode()
                            storyViewModel.persistCurrentState()
                        },
                        onToggleNarration = {
                            combatNarrator.toggleNarration()
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
                        isDebugWarpEnabled = BuildConfig.DEBUG_WARP_MENU,
                        onOpenDebugWarp = { showDebugWarp = true },
                        onReturnToTitle = {
                            combatViewModel.closeOptions()
                            storyViewModel.returnToTitle()
                        },
                        onClose = { combatViewModel.closeOptions() }
                    )

                    // Debug-only chapter warp & developer tools dialog (never shown in release builds)
                    DebugWarpDialog(
                        isOpen = showDebugWarp,
                        targets = StoryViewModel.DEBUG_CHAPTER_TARGETS,
                        developerToolsEnabled = BuildConfig.DEBUG_WARP_MENU && isDeveloperToolsEnabled,
                        onToggleDeveloperTools = { combatViewModel.toggleDeveloperTools() },
                        onWarpTo = { nodeId ->
                            showDebugWarp = false
                            combatViewModel.closeOptions()
                            storyViewModel.debugWarpToChapter(nodeId)
                        },
                        onClose = { showDebugWarp = false }
                    )
                }
            }
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
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
