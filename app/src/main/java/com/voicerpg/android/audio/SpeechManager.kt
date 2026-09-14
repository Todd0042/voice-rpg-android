package com.voicerpg.android.audio

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SpeechState {
    object Idle : SpeechState()
    object Listening : SpeechState()
    object Processing : SpeechState()
    data class Error(val message: String) : SpeechState()
}

class SpeechManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var speechRecognizer: SpeechRecognizer? = null

    private val _speechState = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val speechState: StateFlow<SpeechState> = _speechState.asStateFlow()

    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript: StateFlow<String> = _liveTranscript.asStateFlow()

    // Default to chime MUTED as requested by user
    private val _isChimeMuted = MutableStateFlow(true)
    val isChimeMuted: StateFlow<Boolean> = _isChimeMuted.asStateFlow()

    // Default to false: Tap-to-Speak mode (no unexpected mic opening or chime)
    private val _isAutoListen = MutableStateFlow(false)
    val isAutoListen: StateFlow<Boolean> = _isAutoListen.asStateFlow()

    // Real-time audio amplitude for live visual feedback (0f to 1f)
    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private val pitchDetector = PitchDetector()
    private val rmsSamples = mutableListOf<Float>()
    private var speechStartTimeMs = 0L

    private val _lastAcousticProfile = MutableStateFlow(com.voicerpg.android.model.AcousticProfile())
    val lastAcousticProfile: StateFlow<com.voicerpg.android.model.AcousticProfile> = _lastAcousticProfile.asStateFlow()

    private var isMutedTemporary = false
    private var onFinalResultCallback: ((String) -> Unit)? = null

    fun getLatestAcousticProfile(): com.voicerpg.android.model.AcousticProfile = _lastAcousticProfile.value

    val isAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    fun toggleChimeMute() {
        _isChimeMuted.value = !_isChimeMuted.value
    }

    fun toggleAutoListen() {
        _isAutoListen.value = !_isAutoListen.value
    }

    /**
     * Momentarily mutes system/notification/music streams so OS speech beeps/chimes are silent.
     */
    private fun suppressChime() {
        if (!_isChimeMuted.value) return
        val am = audioManager ?: return
        isMutedTemporary = true
        try { am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0) } catch (_: Exception) {}
        try { am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0) } catch (_: Exception) {}
        try { am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0) } catch (_: Exception) {}
    }

    /**
     * Restores streams after the chime window has elapsed.
     */
    private fun restoreVolume(delayMs: Long = 250L) {
        if (!isMutedTemporary) return
        CoroutineScope(Dispatchers.Main).launch {
            if (delayMs > 0) delay(delayMs)
            val am = audioManager ?: return@launch
            try { am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0) } catch (_: Exception) {}
            try { am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0) } catch (_: Exception) {}
            try { am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0) } catch (_: Exception) {}
            isMutedTemporary = false
        }
    }

    fun startListening(onResult: (String) -> Unit) {
        if (!isAvailable) {
            _speechState.value = SpeechState.Error("Speech recognition is not available on this device.")
            return
        }

        onFinalResultCallback = onResult
        _liveTranscript.value = ""

        try {
            suppressChime()
            try {
                speechRecognizer?.cancel()
                speechRecognizer?.destroy()
            } catch (_: Exception) {}

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1800L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1200L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000L)
            }

            speechRecognizer?.startListening(intent)
            _speechState.value = SpeechState.Listening
            restoreVolume(400L)
        } catch (e: Exception) {
            restoreVolume(0L)
            _speechState.value = SpeechState.Error(e.localizedMessage ?: "Failed to start speech recognizer")
        }
    }

    fun stopListening() {
        suppressChime()
        try {
            speechRecognizer?.stopListening()
            _speechState.value = SpeechState.Processing
        } catch (_: Exception) {
            _speechState.value = SpeechState.Idle
        }
        restoreVolume(400L)
    }

    fun cancel() {
        suppressChime()
        try {
            speechRecognizer?.cancel()
        } catch (_: Exception) {}
        _speechState.value = SpeechState.Idle
        _liveTranscript.value = ""
        _rmsLevel.value = 0f
        restoreVolume(400L)
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        restoreVolume(0L)
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _speechState.value = SpeechState.Listening
                restoreVolume(150L)
            }

            override fun onBeginningOfSpeech() {
                _speechState.value = SpeechState.Listening
                speechStartTimeMs = System.currentTimeMillis()
                rmsSamples.clear()
                pitchDetector.reset()
            }

            override fun onRmsChanged(rmsdB: Float) {
                val clamped = rmsdB.coerceIn(0f, 12f)
                rmsSamples.add(clamped)
                _rmsLevel.value = (clamped / 10f).coerceIn(0f, 1f)
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                buffer?.let { pitchDetector.processPcmBuffer(it) }
            }

            override fun onEndOfSpeech() {
                _rmsLevel.value = 0f
                finalizeAcousticProfile()
                suppressChime()
                _speechState.value = SpeechState.Processing
                restoreVolume(400L)
            }

            override fun onError(error: Int) {
                _rmsLevel.value = 0f
                suppressChime()
                restoreVolume(350L)

                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_NO_MATCH) {
                    // Do NOT loop re-triggering speech recognizer (which played repetitive beeps)
                    _speechState.value = SpeechState.Idle
                    return
                }

                if (error == SpeechRecognizer.ERROR_CLIENT || 
                    error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY || 
                    error == 10 || // ERROR_TOO_MANY_REQUESTS
                    error == 11    // ERROR_SERVER_DISCONNECTED
                ) {
                    try {
                        speechRecognizer?.cancel()
                    } catch (_: Exception) {}
                    _speechState.value = SpeechState.Idle
                    return
                }

                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                    10 -> "Speech daemon busy"
                    11 -> "Speech daemon reconnecting"
                    else -> "Unknown error ($error)"
                }
                _speechState.value = SpeechState.Error(errorMsg)
            }

            override fun onResults(results: Bundle?) {
                _rmsLevel.value = 0f
                finalizeAcousticProfile()
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val finalTrans = matches?.firstOrNull() ?: ""
                _liveTranscript.value = finalTrans
                _speechState.value = SpeechState.Idle
                if (finalTrans.isNotBlank()) {
                    onFinalResultCallback?.invoke(finalTrans)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull() ?: ""
                if (partial.isNotBlank()) {
                    _liveTranscript.value = partial
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun finalizeAcousticProfile() {
        if (rmsSamples.isEmpty()) return
        val peakDb = rmsSamples.maxOrNull() ?: 5f
        val avgDb = rmsSamples.average().toFloat()
        val minDb = rmsSamples.minOrNull() ?: 0f
        val dynRange = (peakDb - minDb).coerceAtLeast(0f)

        val half = rmsSamples.size / 2
        val slope = if (half > 2) {
            val firstHalfAvg = rmsSamples.take(half).average().toFloat()
            val secondHalfAvg = rmsSamples.drop(half).average().toFloat()
            (secondHalfAvg - firstHalfAvg)
        } else 0f

        pitchDetector.recordRmsModulation(rmsSamples)

        val profile = com.voicerpg.android.model.AcousticProfile(
            peakVolumeDb = peakDb,
            averageVolumeDb = avgDb,
            volumeDynamicRange = dynRange,
            volumeCrescendoSlope = slope,
            pitchVarianceHz = pitchDetector.getPitchVarianceHz(),
            estimatedPitchHz = pitchDetector.getAveragePitchHz(),
            durationMs = (System.currentTimeMillis() - speechStartTimeMs).coerceAtLeast(0L),
            sampleCount = rmsSamples.size
        )
        _lastAcousticProfile.value = profile
    }
}

