package com.voicerpg.android.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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

    private val _isEyesFreeMode = MutableStateFlow(false)
    val isEyesFreeMode: StateFlow<Boolean> = _isEyesFreeMode.asStateFlow()

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
                            _isSpeaking.value = false
                            val cb = pendingSpeechOnDone
                            pendingSpeechOnDone = null
                            cb?.invoke()
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                            val cb = pendingSpeechOnDone
                            pendingSpeechOnDone = null
                            cb?.invoke()
                        }
                    })
                    isTtsInitialized = true
                }
            }
        }
    }

    fun setEyesFreeMode(enabled: Boolean) {
        _isEyesFreeMode.value = enabled
    }

    fun toggleEyesFreeMode(): Boolean {
        _isEyesFreeMode.value = !_isEyesFreeMode.value
        return _isEyesFreeMode.value
    }

    /**
     * Speaks text aloud if eyes-free mode is active, or if force is true (e.g. for status queries).
     */
    fun speak(text: String, force: Boolean = false, onDone: (() -> Unit)? = null) {
        if (!force && !_isEyesFreeMode.value) {
            onDone?.invoke()
            return
        }

        if (tts == null || !isTtsInitialized) {
            onDone?.invoke()
            return
        }

        pendingSpeechOnDone = onDone
        val utteranceId = "combat_tts_${System.currentTimeMillis()}"
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        pendingSpeechOnDone = null
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

    fun narrateConclusion(isVictory: Boolean) {
        if (!_isEyesFreeMode.value) return
        val text = if (isVictory) {
            "Victory achieved! The Logos resonates through the sanctum."
        } else {
            "Defeat. Your voice fades into the Silent Blight."
        }
        speak(text, force = true)
    }

    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
