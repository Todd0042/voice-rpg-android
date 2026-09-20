package com.voicerpg.android.viewmodel

import androidx.lifecycle.ViewModel
import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.ResonanceEngine
import com.voicerpg.android.engine.SpellThesaurus
import com.voicerpg.android.model.ResonanceResult
import com.voicerpg.android.model.SpellSchool
import com.voicerpg.android.ui.title.TutorialContent
import com.voicerpg.android.ui.title.TutorialSpellInfo
import com.voicerpg.android.ui.title.TutorialStep
import com.voicerpg.android.ui.title.TutorialSegment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

enum class TutorialPhase {
    NARRATOR,
    PRE_CAST,
    ANIMATING,
    POST_CAST,
    COMPLETED
}

data class TutorialCastResult(
    val spellName: String,
    val resonanceTier: String,
    val bonusPercent: Int,
    val damageMultiplier: Float,
    val damage: Int,
    val isHeal: Boolean,
    val isBreath: Boolean,
    val matchedRoots: List<String>,
    val isResonanceDemo: Boolean = false,
    val resonanceDemoIndex: Int = 0
)

data class TutorialUiState(
    val stepIndex: Int = 0,
    val phase: TutorialPhase = TutorialPhase.NARRATOR,
    val currentCastResult: TutorialCastResult? = null,
    val dummyHp: Int = 2000,
    val dummyMaxHp: Int = 2000,
    val playerMana: Int = 200,
    val playerMaxMana: Int = 200,
    val isListening: Boolean = false,
    val promptText: String = "",
    val resonanceDemoResults: List<TutorialCastResult> = emptyList()
)

class TutorialViewModel(
    private val speechManager: SpeechManager,
    private val combatNarrator: CombatNarrator
) : ViewModel() {

    private val resonanceEngine = ResonanceEngine()
    private var resonanceDemoIndex = 0

    private val _state = MutableStateFlow(TutorialUiState())
    val state: StateFlow<TutorialUiState> = _state.asStateFlow()

    val currentStep: TutorialStep?
        get() = TutorialContent.getStep(_state.value.stepIndex)

    val currentSegment: TutorialSegment?
        get() = currentStep?.let { TutorialContent.getSegment(it.segmentIndex) }

    val currentSpell: TutorialSpellInfo?
        get() = currentStep?.let { TutorialContent.getSpellForStep(it) }

    val currentSpeaker: com.voicerpg.android.model.DialogueSpeaker
        get() = currentStep?.let { TutorialContent.getSpeakerForStep(it) }
            ?: com.voicerpg.android.model.DialogueSpeaker.NARRATOR

    init {
        narrateCurrentStep()
    }

    fun onContinue() {
        val s = _state.value
        when (s.phase) {
            TutorialPhase.NARRATOR -> {
                val step = currentStep ?: return
                if (step.isCompletion) return
                if (step.isPractice) {
                    _state.value = s.copy(
                        phase = TutorialPhase.PRE_CAST,
                        promptText = "Say \"${currentSpell?.name ?: "the spell name"}\"..."
                    )
                } else {
                    advanceStep()
                }
            }
            TutorialPhase.POST_CAST -> {
                advanceAfterCast()
            }
            TutorialPhase.ANIMATING -> {
                _state.value = s.copy(phase = TutorialPhase.POST_CAST)
            }
            else -> Unit
        }
    }

    fun onSpeechResult(utterance: String) {
        val s = _state.value
        if (s.phase != TutorialPhase.PRE_CAST) return
        val step = currentStep ?: return
        if (!step.isPractice) return

        _state.value = s.copy(isListening = false)

        val spell = currentSpell ?: return
        if (!matchesSpell(utterance, spell, step.isResonanceDemo)) {
            _state.value = _state.value.copy(
                promptText = "Try again! Say \"${spell.name}\" or a related word."
            )
            return
        }

        resolveSpellCast(utterance, spell, step.isResonanceDemo)
    }

    fun startListening() {
        _state.value = _state.value.copy(isListening = true)
        speechManager.startListening(
            onResult = { utterance -> onSpeechResult(utterance) },
            onStandby = {}
        )
    }

    fun stopListening() {
        speechManager.stopListening()
        _state.value = _state.value.copy(isListening = false)
    }

    fun skipTutorial() {
        combatNarrator.stop()
        speechManager.cancel()
    }

    fun startNewGameFromTutorial(onStart: () -> Unit) {
        skipTutorial()
        onStart()
    }

    fun returnToTitleFromTutorial(onReturn: () -> Unit) {
        skipTutorial()
        onReturn()
    }

    fun getNarratorText(): String {
        val step = currentStep ?: return ""
        val base = step.narratorText
        return when (_state.value.phase) {
            TutorialPhase.PRE_CAST -> "$base\n\nSay \"${currentSpell?.name}\" to cast."
            TutorialPhase.POST_CAST -> buildPostCastNarration()
            else -> base
        }
    }

    fun isCompleted(): Boolean = currentStep?.isCompletion == true

    fun getCompletionActions(): List<Pair<String, () -> Unit>> = emptyList()

    private fun matchesSpell(utterance: String, spell: TutorialSpellInfo, isResonanceDemo: Boolean): Boolean {
        if (isResonanceDemo) {
            val demoIdx = resonanceDemoIndex
            if (demoIdx == 1) return true
            val lower = utterance.lowercase()
            return lower.contains("fireball") || lower.contains("fire") || lower.contains("flame")
        }
        val lower = utterance.lowercase()
        val spellWords = spell.name.lowercase().split(" ")
        if (spellWords.any { it.length >= 4 && lower.contains(it) }) return true
        val spokenWords = lower.split(Regex("\\s+")).filter { it.length >= 3 }
        if (spokenWords.any { w -> spell.aliases.any { a -> w.contains(a) || a.contains(w) } }) return true
        val schoolKeywords = SpellThesaurus.getKeywordsForSchool(spell.school)
        return spokenWords.any { w -> schoolKeywords.any { k -> w.contains(k) || k.contains(w) } }
    }

    private fun resolveSpellCast(utterance: String, spell: TutorialSpellInfo, isResonanceDemo: Boolean) {
        val s = _state.value

        if (spell.manaRestorePct > 0f) {
            val restored = (s.playerMaxMana * spell.manaRestorePct).toInt()
            val newMana = (s.playerMana + restored).coerceAtMost(s.playerMaxMana)
            val result = TutorialCastResult(
                spellName = spell.name,
                resonanceTier = "Breath",
                bonusPercent = 0,
                damageMultiplier = 1f,
                damage = restored,
                isHeal = false,
                isBreath = true,
                matchedRoots = emptyList()
            )
            _state.value = s.copy(
                phase = TutorialPhase.ANIMATING,
                currentCastResult = result,
                playerMana = newMana,
                promptText = "Mana restored by $restored!"
            )
            return
        }

        if (spell.mpCost > 0 && s.playerMana < spell.mpCost) {
            _state.value = s.copy(
                promptText = "Not enough mana! Try using Attune or a breath ability first."
            )
            return
        }

        val resonance = resonanceEngine.evaluate(
            utterance = utterance,
            school = spell.school,
            ignoreNoveltyDecay = true
        )

        val damage = calculateDamage(spell.basePower, resonance.damageMultiplier)
        val newMana = if (spell.mpCost > 0) (s.playerMana - spell.mpCost).coerceAtLeast(0) else s.playerMana
        val newDummyHp = if (!spell.isHeal && !spell.isGuard) {
            (s.dummyHp - damage).coerceAtLeast(1)
        } else s.dummyHp

        val result = TutorialCastResult(
            spellName = spell.name,
            resonanceTier = resonance.tier.title,
            bonusPercent = resonance.bonusPercent,
            damageMultiplier = resonance.damageMultiplier,
            damage = damage,
            isHeal = spell.isHeal,
            isBreath = false,
            matchedRoots = resonance.matchedThematicRoots,
            isResonanceDemo = isResonanceDemo,
            resonanceDemoIndex = resonanceDemoIndex
        )

        var newDemoResults = s.resonanceDemoResults
        if (isResonanceDemo) {
            newDemoResults = newDemoResults + result
            resonanceDemoIndex++
        }

        _state.value = s.copy(
            phase = TutorialPhase.ANIMATING,
            currentCastResult = result,
            dummyHp = newDummyHp,
            playerMana = newMana,
            promptText = "${spell.name}: ${resonance.tier.title} (+${resonance.bonusPercent}%) -- $damage damage!",
            resonanceDemoResults = newDemoResults
        )
    }

    private fun calculateDamage(basePower: Int, multiplier: Float): Int {
        val variance = 0.95f + (Math.random().toFloat() * 0.1f)
        return (basePower * multiplier * variance).roundToInt()
    }

    private fun advanceAfterCast() {
        val step = currentStep ?: return
        if (step.isResonanceDemo) {
            val seg = currentSegment
            if (seg != null && step.spellIndex < seg.spells.lastIndex) {
                val nextSpellIndex = step.spellIndex + 1
                val nextStep = TutorialContent.STEPS.firstOrNull {
                    it.segmentIndex == step.segmentIndex && it.spellIndex == nextSpellIndex
                }
                if (nextStep != null) {
                    val idx = TutorialContent.STEPS.indexOf(nextStep)
                    combatNarrator.stop()
                    _state.value = _state.value.copy(
                        stepIndex = idx,
                        phase = TutorialPhase.NARRATOR,
                        currentCastResult = null,
                        playerMana = _state.value.playerMaxMana,
                        dummyHp = _state.value.dummyMaxHp
                    )
                    narrateCurrentStep()
                    return
                }
            }
        }
        advanceStep()
    }

    private fun advanceStep() {
        val nextIndex = _state.value.stepIndex + 1
        if (nextIndex >= TutorialContent.totalSteps) return
        combatNarrator.stop()
        speechManager.cancel()
        resonanceDemoIndex = if (TutorialContent.getStep(nextIndex)?.isResonanceDemo == true) 0 else resonanceDemoIndex

        _state.value = _state.value.copy(
            stepIndex = nextIndex,
            phase = TutorialPhase.NARRATOR,
            currentCastResult = null,
            playerMana = _state.value.playerMaxMana,
            dummyHp = _state.value.dummyMaxHp,
            promptText = ""
        )
        val nextStep = TutorialContent.getStep(nextIndex)
        val currentSegIdx = currentStep?.segmentIndex ?: -1
        val nextSegIdx = nextStep?.segmentIndex ?: -1
        if (nextSegIdx != currentSegIdx && nextStep?.isResonanceDemo == true) {
            resonanceDemoIndex = 0
        }
        narrateCurrentStep()
    }

    private fun narrateCurrentStep() {
        val step = currentStep ?: return
        val speaker = currentSpeaker
        combatNarrator.narrateDialogue(
            speaker = speaker,
            text = step.narratorText,
            choices = emptyList()
        )
    }

    private fun buildPostCastNarration(): String {
        val result = _state.value.currentCastResult ?: return "Well done!"
        return when {
            result.isBreath -> "Well done. Your mana has been restored. Breath abilities are free and essential for sustaining your spells throughout a long battle."
            result.isResonanceDemo && result.resonanceDemoIndex == 0 -> {
                "That was a basic incantation: ${result.resonanceTier}, dealing ${result.damage} damage. Now try the same spell with elaborate, dramatic language and see the difference."
            }
            result.isResonanceDemo && result.resonanceDemoIndex == 1 -> {
                "See the difference? Your elaborate incantation achieved ${result.resonanceTier}, dealing ${result.damage} damage -- far more than the basic version."
            }
            else -> "Well done! That was ${result.resonanceTier} dealing ${result.damage} damage."
        }
    }
}
