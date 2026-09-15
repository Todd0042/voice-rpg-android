package com.voicerpg.android.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.voicerpg.android.model.DialogueChoice
import com.voicerpg.android.model.DialogueSpeaker
import com.voicerpg.android.model.Enemy
import com.voicerpg.android.model.PartyMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Audio-first Screenless / Pocket Mode Combat Narrator.
 * Powered by Android's on-device TextToSpeech engine.
 * Allows playing the entire game hands-free and eyes-free (e.g. while walking, with phone in pocket,
 * or for visually impaired / blind players).
 */
class CombatNarrator(
    private val context: Context? = null
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var pendingSpeechOnDone: (() -> Unit)? = null
    private var activeUtteranceId: String? = null

    // Multi-voice character profile mapping
    private var defaultVoice: Voice? = null
    private var cedricVoice: Voice? = null
    private var aethelVoice: Voice? = null
    private var lyraVoice: Voice? = null
    private var zephyrVoice: Voice? = null
    private var malakorVoice: Voice? = null
    private var shadowWispVoice: Voice? = null
    private var narratorVoice: Voice? = null

    private val _availableVoiceCount = MutableStateFlow(0)
    val availableVoiceCount: StateFlow<Int> = _availableVoiceCount.asStateFlow()

    private val _installedVoiceCount = MutableStateFlow(0)
    val installedVoiceCount: StateFlow<Int> = _installedVoiceCount.asStateFlow()

    private val _isEyesFreeMode = MutableStateFlow(false)
    val isEyesFreeMode: StateFlow<Boolean> = _isEyesFreeMode.asStateFlow()

    private val _isNarrationEnabled = MutableStateFlow(true)
    val isNarrationEnabled: StateFlow<Boolean> = _isNarrationEnabled.asStateFlow()

    private val _isReadChoicesEnabled = MutableStateFlow(true)
    val isReadChoicesEnabled: StateFlow<Boolean> = _isReadChoicesEnabled.asStateFlow()

    private val _speechRate = MutableStateFlow(1.05f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _isCharacterPitchEnabled = MutableStateFlow(true)
    val isCharacterPitchEnabled: StateFlow<Boolean> = _isCharacterPitchEnabled.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        if (context != null) {
            try {
                tts = TextToSpeech(context, this)
            } catch (_: Exception) {
                // Headless/test fallback
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setSpeechRate(1.15f) // Crisp, brisk pacing for combat flow
                    engine.setPitch(1.0f)
                    engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            if (utteranceId != null && utteranceId == activeUtteranceId) {
                                _isSpeaking.value = false
                                activeUtteranceId = null
                                val cb = pendingSpeechOnDone
                                pendingSpeechOnDone = null
                                cb?.invoke()
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            onError(utteranceId, TextToSpeech.ERROR)
                        }

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            if (utteranceId != null && utteranceId == activeUtteranceId) {
                                try {
                                    defaultVoice?.let { tts?.voice = it }
                                } catch (_: Exception) {}
                                _isSpeaking.value = false
                                activeUtteranceId = null
                                val cb = pendingSpeechOnDone
                                pendingSpeechOnDone = null
                                cb?.invoke()
                            }
                        }
                    })
                    assignCharacterVoices(engine)
                    isTtsInitialized = true
                }
            }
        }
    }

    private fun isVoiceInstalledAndUsable(engine: TextToSpeech, voice: Voice?): Boolean {
        if (voice == null) return false
        val features = voice.features ?: emptySet()
        val isNotInstalled = features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED) ||
                features.contains("notInstalled")
        if (isNotInstalled) return false
        if (voice.isNetworkConnectionRequired) return false
        return try {
            engine.isLanguageAvailable(voice.locale) >= TextToSpeech.LANG_AVAILABLE
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Programmatically discovers installed device voices and maps distinct voice models
     * to different characters (e.g. distinct male/female or tone models).
     * Strictly verifies that voices are downloaded and installed offline on the device
     * before assigning, safely falling back to defaultVoice to prevent speech skipping.
     */
    private fun assignCharacterVoices(engine: TextToSpeech) {
        try {
            val voices = engine.voices ?: emptySet()
            _availableVoiceCount.value = voices.size
            defaultVoice = engine.voice

            // Only consider voices that are verified to be installed and available offline on this device
            val installedVoices = voices.filter { isVoiceInstalledAndUsable(engine, it) }
            _installedVoiceCount.value = installedVoices.size

            val installedEnglishVoices = installedVoices.filter {
                it.locale.language.equals(Locale.ENGLISH.language, ignoreCase = true)
            }.ifEmpty {
                installedVoices
            }

            if (installedEnglishVoices.isNotEmpty()) {
                val maleVoices = installedEnglishVoices.filter { v ->
                    val name = v.name.lowercase(Locale.ROOT)
                    (name.contains("male") && !name.contains("female")) ||
                            name.contains("#m") || name.contains("-m-") || name.contains("_male")
                }

                val femaleVoices = installedEnglishVoices.filter { v ->
                    val name = v.name.lowercase(Locale.ROOT)
                    name.contains("female") || name.contains("#f") || name.contains("-f-") || name.contains("_female")
                }

                cedricVoice = maleVoices.firstOrNull() ?: defaultVoice
                aethelVoice = femaleVoices.firstOrNull() ?: defaultVoice

                // Only assign a separate voice to Lyra if there is a distinct, confirmed INSTALLED second female voice;
                // otherwise fallback safely to defaultVoice so speech is never skipped!
                lyraVoice = if (femaleVoices.size > 1 && femaleVoices[1] != aethelVoice) {
                    femaleVoices[1]
                } else {
                    defaultVoice
                }

                narratorVoice = defaultVoice ?: installedEnglishVoices.firstOrNull()

                shadowWispVoice = if (maleVoices.size > 1) {
                    maleVoices[1]
                } else {
                    defaultVoice
                }

                zephyrVoice = if (maleVoices.size > 2) {
                    maleVoices[2]
                } else if (femaleVoices.size > 2) {
                    femaleVoices[2]
                } else {
                    defaultVoice
                }

                malakorVoice = if (maleVoices.size > 3) {
                    maleVoices[3]
                } else if (maleVoices.size > 1) {
                    maleVoices[1]
                } else {
                    defaultVoice
                }
            } else {
                cedricVoice = defaultVoice
                aethelVoice = defaultVoice
                lyraVoice = defaultVoice
                zephyrVoice = defaultVoice
                malakorVoice = defaultVoice
                narratorVoice = defaultVoice
                shadowWispVoice = defaultVoice
            }
        } catch (_: Exception) {
            cedricVoice = defaultVoice
            aethelVoice = defaultVoice
            lyraVoice = defaultVoice
            zephyrVoice = defaultVoice
            malakorVoice = defaultVoice
            narratorVoice = defaultVoice
            shadowWispVoice = defaultVoice
        }
    }

    fun setEyesFreeMode(enabled: Boolean) {
        _isEyesFreeMode.value = enabled
    }

    fun toggleEyesFreeMode(): Boolean {
        _isEyesFreeMode.value = !_isEyesFreeMode.value
        return _isEyesFreeMode.value
    }

    fun setNarrationEnabled(enabled: Boolean) {
        _isNarrationEnabled.value = enabled
        if (!enabled) stop()
    }

    fun toggleNarration(): Boolean {
        _isNarrationEnabled.value = !_isNarrationEnabled.value
        if (!_isNarrationEnabled.value) stop()
        return _isNarrationEnabled.value
    }

    fun setReadChoicesEnabled(enabled: Boolean) {
        _isReadChoicesEnabled.value = enabled
    }

    fun toggleReadChoices(): Boolean {
        _isReadChoicesEnabled.value = !_isReadChoicesEnabled.value
        return _isReadChoicesEnabled.value
    }

    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate
        tts?.setSpeechRate(rate)
    }

    fun setCharacterPitchEnabled(enabled: Boolean) {
        _isCharacterPitchEnabled.value = enabled
    }

    fun getInstalledVoices(): Set<Voice> = tts?.voices ?: emptySet()

    fun refreshInstalledVoices() {
        tts?.let { engine ->
            assignCharacterVoices(engine)
        }
    }

    fun openVoiceSettings(context: Context) {
        val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val ttsSettingsIntent = Intent("com.android.settings.TTS_SETTINGS").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val generalSettingsIntent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(installIntent)
        } catch (_: Exception) {
            try {
                context.startActivity(ttsSettingsIntent)
            } catch (_: Exception) {
                try {
                    context.startActivity(generalSettingsIntent)
                } catch (_: Exception) {}
            }
        }
    }

    fun getVoiceForSpeaker(speaker: DialogueSpeaker): Voice? = when (speaker.id) {
        DialogueSpeaker.CEDRIC.id -> cedricVoice
        DialogueSpeaker.AETHEL.id -> aethelVoice
        DialogueSpeaker.LYRA.id -> lyraVoice
        DialogueSpeaker.ZEPHYR.id -> zephyrVoice
        DialogueSpeaker.MALAKOR.id -> malakorVoice
        DialogueSpeaker.SHADOW_WISP.id -> shadowWispVoice
        DialogueSpeaker.NARRATOR.id -> narratorVoice
        else -> narratorVoice
    }

    fun setSpeakerVoice(speaker: DialogueSpeaker, voice: Voice) {
        when (speaker.id) {
            DialogueSpeaker.CEDRIC.id -> cedricVoice = voice
            DialogueSpeaker.AETHEL.id -> aethelVoice = voice
            DialogueSpeaker.LYRA.id -> lyraVoice = voice
            DialogueSpeaker.ZEPHYR.id -> zephyrVoice = voice
            DialogueSpeaker.MALAKOR.id -> malakorVoice = voice
            DialogueSpeaker.SHADOW_WISP.id -> shadowWispVoice = voice
            DialogueSpeaker.NARRATOR.id -> narratorVoice = voice
            else -> narratorVoice = voice
        }
    }

    /**
     * Narrates a story dialogue node, including the speaker line and optionally
     * reading available choices/options aloud.
     */
    fun narrateDialogue(
        speaker: DialogueSpeaker,
        text: String,
        choices: List<DialogueChoice> = emptyList(),
        onDone: () -> Unit = {}
    ) {
        // If narration is turned off and we are not in Eyes-Free mode, skip TTS
        if (!_isNarrationEnabled.value && !_isEyesFreeMode.value) {
            onDone()
            return
        }

        if (tts == null || !isTtsInitialized) {
            onDone()
            return
        }

        // Dynamically assign physical voice model per speaker if verified installed
        val speakerVoice = getVoiceForSpeaker(speaker)
        if (speakerVoice != null && isVoiceInstalledAndUsable(tts!!, speakerVoice)) {
            try {
                tts?.voice = speakerVoice
            } catch (_: Exception) {
                try { defaultVoice?.let { tts?.voice = it } } catch (_: Exception) {}
            }
        } else {
            try { defaultVoice?.let { tts?.voice = it } } catch (_: Exception) {}
        }

        // Apply character vocal inflection/pitch if enabled
        if (_isCharacterPitchEnabled.value) {
            when (speaker.id) {
                DialogueSpeaker.CEDRIC.id -> {
                    tts?.setPitch(0.82f) // Deep baritone noble knight
                    tts?.setSpeechRate(_speechRate.value * 0.95f)
                }
                DialogueSpeaker.AETHEL.id -> {
                    tts?.setPitch(1.08f) // Clear, spirited invocator
                    tts?.setSpeechRate(_speechRate.value)
                }
                DialogueSpeaker.LYRA.id -> {
                    tts?.setPitch(1.20f) // Lyrical, soothing grove warden
                    tts?.setSpeechRate(_speechRate.value * 0.98f)
                }
                DialogueSpeaker.ZEPHYR.id -> {
                    tts?.setPitch(0.95f) // Jaunty, agile rogue
                    tts?.setSpeechRate(_speechRate.value * 1.05f)
                }
                DialogueSpeaker.MALAKOR.id -> {
                    tts?.setPitch(0.72f) // Dark, brooding inquisitor
                    tts?.setSpeechRate(_speechRate.value * 0.90f)
                }
                DialogueSpeaker.SHADOW_WISP.id -> {
                    tts?.setPitch(0.70f) // Raspy sibilant phantom
                    tts?.setSpeechRate(_speechRate.value * 0.88f)
                }
                DialogueSpeaker.NARRATOR.id -> {
                    tts?.setPitch(1.0f) // Measured storyteller cadence
                    tts?.setSpeechRate(_speechRate.value)
                }
                else -> {
                    tts?.setPitch(1.0f)
                    tts?.setSpeechRate(_speechRate.value)
                }
            }
        } else {
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(_speechRate.value)
        }

        val cleanedText = text.replace("...", ". ").trim()
        val speakerPrefix = if (speaker == DialogueSpeaker.NARRATOR) "" else "${speaker.name} says: "

        val fullScript = StringBuilder()
        fullScript.append("$speakerPrefix$cleanedText")

        // Read choices aloud if setting enabled
        if (_isReadChoicesEnabled.value && choices.isNotEmpty()) {
            fullScript.append(". Your options are: ")
            choices.forEachIndexed { index, choice ->
                fullScript.append("Option ${index + 1}: ${choice.text}. ")
            }
            fullScript.append("What is your command?")
        }

        speak(fullScript.toString(), force = true, preserveVoice = true, onDone = onDone)
    }

    /**
     * Speaks text aloud if eyes-free mode is active, or if force is true (e.g. for status queries).
     */
    fun speak(text: String, force: Boolean = false, preserveVoice: Boolean = false, onDone: (() -> Unit)? = null) {
        if (!force && !_isEyesFreeMode.value) {
            onDone?.invoke()
            return
        }

        if (tts == null || !isTtsInitialized) {
            onDone?.invoke()
            return
        }

        if (!preserveVoice) {
            // Reset to Narrator voice for system / tactical announcements
            narratorVoice?.let {
                try {
                    tts?.voice = it
                } catch (_: Exception) {}
            }
        }

        val utteranceId = "combat_tts_${System.currentTimeMillis()}"
        activeUtteranceId = utteranceId
        pendingSpeechOnDone = onDone

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }
        var result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            // Safety fallback: if custom voice failed, reset to defaultVoice and retry immediately
            try {
                defaultVoice?.let { tts?.voice = it }
                result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            } catch (_: Exception) {}
        }

        if (result != TextToSpeech.SUCCESS) {
            _isSpeaking.value = false
            activeUtteranceId = null
            val cb = pendingSpeechOnDone
            pendingSpeechOnDone = null
            cb?.invoke()
        }
    }

    fun stop() {
        activeUtteranceId = null
        pendingSpeechOnDone = null
        try {
            tts?.stop()
        } catch (_: Exception) {}
        _isSpeaking.value = false
    }

    // =========================================================================
    // Tactical Combat Narration
    // =========================================================================

    fun narratePlayerTurn(hero: PartyMember, enemies: List<Enemy>, onDone: () -> Unit) {
        if (!_isEyesFreeMode.value) {
            onDone()
            return
        }
        val aliveEnemies = enemies.filter { it.isAlive }
        val targeted = aliveEnemies.firstOrNull { it.isTargeted } ?: aliveEnemies.firstOrNull()
        val enemyCountDesc = when (aliveEnemies.size) {
            1 -> "One enemy remains"
            else -> "${aliveEnemies.size} enemies on field"
        }
        val targetDesc = if (targeted != null) ", target is ${targeted.name}" else ""
        val text = "${hero.name}'s turn. $enemyCountDesc$targetDesc. Ready to chant."
        speak(text, force = true, onDone = onDone)
    }

    fun narrateSpellCast(
        heroName: String,
        spellName: String,
        targetName: String,
        amount: Int,
        isHeal: Boolean,
        tierTitle: String?,
        isDefeated: Boolean = false
    ) {
        if (!_isEyesFreeMode.value) return
        val prefix = if (tierTitle != null && (tierTitle.contains("Transcendental", ignoreCase = true) || tierTitle.contains("Mythic", ignoreCase = true))) {
            "$tierTitle resonance! "
        } else ""

        val action = if (isHeal) {
            "${prefix}$heroName mends $targetName for $amount health."
        } else {
            "${prefix}$heroName strikes $targetName with $spellName for $amount damage."
        }

        val defeatText = if (isDefeated) " $targetName is defeated!" else ""
        speak("$action$defeatText")
    }

    fun narrateEnemyAction(
        enemyName: String,
        targetHeroName: String,
        damage: Int,
        isFallen: Boolean = false
    ) {
        if (!_isEyesFreeMode.value) return
        val fallenDesc = if (isFallen) " $targetHeroName has fallen!" else ""
        speak("$enemyName attacks $targetHeroName for $damage damage.$fallenDesc")
    }

    fun narrateReinforcements(count: Int, enemyNames: List<String>) {
        if (!_isEyesFreeMode.value) return
        val text = if (count == 1) {
            "Reinforcement arrived! ${enemyNames.firstOrNull() ?: "An enemy"} joined the battle."
        } else {
            "Reinforcements arrived! $count enemies joined the battle."
        }
        speak(text)
    }

    fun narrateStatus(party: List<PartyMember>, enemies: List<Enemy>, onDone: (() -> Unit)? = null) {
        val aliveHeroes = party.filter { it.isAlive }
        val partyStatus = aliveHeroes.joinToString(", ") { "${it.name} at ${it.currentHp} HP" }
        val aliveEnemies = enemies.filter { it.isAlive }
        val enemyStatus = aliveEnemies.joinToString(", ") { "${it.name} at ${it.currentHp} HP" }
        val targeted = aliveEnemies.firstOrNull { it.isTargeted }?.name ?: "none"

        val text = "Situation report. Party: $partyStatus. Enemies: $enemyStatus. Targeted foe: $targeted."
        speak(text, force = true, onDone = onDone)
    }

    fun narrateEnemies(enemies: List<Enemy>, onDone: (() -> Unit)? = null) {
        val alive = enemies.filter { it.isAlive }
        val targeted = alive.firstOrNull { it.isTargeted }?.name ?: alive.firstOrNull()?.name
        val list = alive.mapIndexed { idx, e -> "Enemy ${idx + 1}, ${e.name} with ${e.currentHp} HP" }.joinToString(". ")
        val text = "${alive.size} enemies remaining. $list. Current target is $targeted."
        speak(text, force = true, onDone = onDone)
    }

    fun narrateParty(party: List<PartyMember>, onDone: (() -> Unit)? = null) {
        val members = party.map {
            if (it.isAlive) "${it.name} the ${it.loreClass}, ${it.currentHp} HP" else "${it.name} has fallen"
        }.joinToString(". ")
        val text = "Fellowship status. $members."
        speak(text, force = true, onDone = onDone)
    }

    fun narrateHelp(onDone: (() -> Unit)? = null) {
        val text = "Voice commands. Say your spell name to cast. Say 'Status' for situation report. " +
                "Say 'Enemies' for target scan. Say 'Pocket mode' to toggle audio guidance. " +
                "Say 'Auto listen' to toggle hands-free mic. Say 'Options' to open settings."
        speak(text, force = true, onDone = onDone)
    }

    fun narrateConclusion(isVictory: Boolean, onDone: (() -> Unit)? = null) {
        if (!_isEyesFreeMode.value) {
            onDone?.invoke()
            return
        }
        val text = if (isVictory) {
            "Victory achieved! The Logos resonates through the sanctum."
        } else {
            "Defeat. Your voice fades into the Silent Blight."
        }
        speak(text, force = true, onDone = onDone)
    }

    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
