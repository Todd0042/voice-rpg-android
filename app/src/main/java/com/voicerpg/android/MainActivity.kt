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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.SaveManager
import com.voicerpg.android.model.GameScreen
import com.voicerpg.android.ui.combat.OptionsDialog
import com.voicerpg.android.ui.combat.RetroBattleScreen
import com.voicerpg.android.ui.creation.CharacterCreationScreen
import com.voicerpg.android.ui.setup.AudioSetupScreen
import com.voicerpg.android.ui.story.StoryScreen
import com.voicerpg.android.ui.theme.VoiceRPGTheme
import com.voicerpg.android.viewmodel.CombatViewModel
import com.voicerpg.android.viewmodel.StoryViewModel

class MainActivity : ComponentActivity() {

    private lateinit var speechManager: SpeechManager
    private lateinit var combatNarrator: CombatNarrator
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
        saveManager = SaveManager(this)

        combatViewModel = CombatViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator
        )
        storyViewModel = StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager
        )

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
            storyViewModel.onCombatVictory()
            storyViewModel.updatePartyStatsFromCombat(combatViewModel.state.value.party)
        }

        checkAudioPermission()

        setContent {
            val storyState by storyViewModel.state.collectAsState()
            val combatState by combatViewModel.state.collectAsState()

            val isEyesFreeMode by combatNarrator.isEyesFreeMode.collectAsState()
            val isNarrationEnabled by combatNarrator.isNarrationEnabled.collectAsState()
            val isReadChoicesEnabled by combatNarrator.isReadChoicesEnabled.collectAsState()
            val isCharacterPitchEnabled by combatNarrator.isCharacterPitchEnabled.collectAsState()
            val speechRate by combatNarrator.speechRate.collectAsState()
            val isAutoListen by speechManager.isAutoListen.collectAsState()
            val isChimeMuted by speechManager.isChimeMuted.collectAsState()

            LaunchedEffect(storyState.player) {
                combatViewModel.applyPlayerCustomization(
                    customization = storyState.player,
                    savedStats = storyState.partyStats
                )
            }

            LaunchedEffect(storyState.activeEncounter) {
                storyState.activeEncounter?.let { encounter ->
                    combatViewModel.startEncounter(encounter)
                }
            }

            VoiceRPGTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (storyState.gameScreen) {
                        GameScreen.AUDIO_SETUP -> {
                            AudioSetupScreen(
                                combatNarrator = combatNarrator,
                                speechManager = speechManager,
                                isFromGame = storyState.previousScreen != null,
                                onBack = {
                                    storyViewModel.returnFromAudioSetup()
                                },
                                onProceed = {
                                    if (storyState.previousScreen != null) {
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
                                    storyViewModel.onCombatVictory()
                                    storyViewModel.updatePartyStatsFromCombat(combatViewModel.state.value.party)
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
                        onOpenVoiceSettings = {
                            combatNarrator.openVoiceSettings(this@MainActivity)
                        },
                        onOpenVoiceAssignment = {
                            combatViewModel.closeOptions()
                            storyViewModel.openAudioSetup()
                        },
                        onClose = { combatViewModel.closeOptions() }
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
    }
}
