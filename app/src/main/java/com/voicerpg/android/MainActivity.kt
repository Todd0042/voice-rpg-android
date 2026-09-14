package com.voicerpg.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.ui.combat.RetroBattleScreen
import com.voicerpg.android.ui.theme.VoiceRPGTheme
import com.voicerpg.android.viewmodel.CombatViewModel

class MainActivity : ComponentActivity() {

    private lateinit var speechManager: SpeechManager
    private lateinit var combatViewModel: CombatViewModel

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
        combatViewModel = CombatViewModel(speechManager)

        checkAudioPermission()

        setContent {
            VoiceRPGTheme {
                RetroBattleScreen(viewModel = combatViewModel)
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
        speechManager.destroy()
    }
}
