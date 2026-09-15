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
import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.SaveManager
import com.voicerpg.android.model.GameScreen
import com.voicerpg.android.ui.combat.RetroBattleScreen
import com.voicerpg.android.ui.creation.CharacterCreationScreen
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

        checkAudioPermission()

        setContent {
            val storyState by storyViewModel.state.collectAsState()

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
                when (storyState.gameScreen) {
                    GameScreen.CHARACTER_CREATION -> {
                        CharacterCreationScreen(
                            initialCustomization = storyState.player,
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
