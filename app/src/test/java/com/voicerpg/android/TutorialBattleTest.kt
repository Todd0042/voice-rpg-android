package com.voicerpg.android

import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.TutorialBattleContent
import com.voicerpg.android.engine.TutorialBattleStep
import com.voicerpg.android.model.CharacterStance
import com.voicerpg.android.model.ResonanceTier
import com.voicerpg.android.viewmodel.ModalCardType
import com.voicerpg.android.viewmodel.TutorialBattleViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TutorialBattleTest {

    private lateinit var viewModel: TutorialBattleViewModel
    private lateinit var dummySpeech: SpeechManager
    private lateinit var dummyNarrator: CombatNarrator
    private val testScope = CoroutineScope(Dispatchers.Default)

    @Before
    fun setUp() {
        dummySpeech = SpeechManager()
        dummyNarrator = CombatNarrator()
        viewModel = TutorialBattleViewModel(
            speechManager = dummySpeech,
            combatNarrator = dummyNarrator,
            scopeOverride = testScope
        )
    }

    @Test
    fun testInitialTutorialState() {
        val state = viewModel.state.value
        assertEquals(0, state.stepIndex)
        assertTrue(state.isModalVisible)
        assertEquals(ModalCardType.NARRATOR_INTRO, state.modalType)
        assertEquals(1, state.party.size)
        assertEquals("aethel", state.party[0].id)
        assertEquals(3, state.enemies.size)
        assertEquals("dummy_alpha", state.enemies[0].id)
        assertEquals("dummy_beta", state.enemies[1].id)
        assertEquals("dummy_gamma", state.enemies[2].id)
        assertEquals("dummy_alpha", state.targetedEnemyId)
    }

    @Test
    fun testStepProgressionAndFellowshipJoin() {
        // Step 0: Intro 1
        assertEquals(0, viewModel.state.value.stepIndex)
        viewModel.advanceModal()

        // Step 1: Intro 2
        assertEquals(1, viewModel.state.value.stepIndex)
        viewModel.advanceModal()

        // Step 2: Aethel Join
        assertEquals(2, viewModel.state.value.stepIndex)
        assertEquals(ModalCardType.CHARACTER_JOIN, viewModel.state.value.modalType)
        assertEquals(1, viewModel.state.value.party.size)
        viewModel.advanceModal()

        // Step 3: Aethel Spell Overview
        assertEquals(3, viewModel.state.value.stepIndex)
        assertEquals(ModalCardType.SPELL_OVERVIEW, viewModel.state.value.modalType)
        viewModel.advanceModal()

        // Step 4: Practice Fireball
        assertEquals(4, viewModel.state.value.stepIndex)
        assertEquals(ModalCardType.PRACTICE_CAST_PROMPT, viewModel.state.value.modalType)
    }

    @Test
    fun testPracticeCastExecutionAndDamage() = runBlocking {
        // Fast-forward to step 4 (Practice Cast Fireball)
        while (viewModel.state.value.stepIndex < 4) {
            viewModel.advanceModal()
        }

        val initialDummyHp = viewModel.state.value.enemies.first { it.id == "dummy_alpha" }.currentHp
        val initialAethelMp = viewModel.state.value.party.first { it.id == "aethel" }.currentMp

        viewModel.castSpell("Fireball")

        // Wait for animation & resolution
        delay(1800)

        val updatedState = viewModel.state.value
        val damagedDummy = updatedState.enemies.first { it.id == "dummy_alpha" }
        val updatedAethel = updatedState.party.first { it.id == "aethel" }

        assertTrue("Dummy HP should be reduced", damagedDummy.currentHp < initialDummyHp)
        assertTrue("Aethel MP should be deducted", updatedAethel.currentMp < initialAethelMp)
        assertEquals(ModalCardType.CAST_RESULT, updatedState.modalType)
        assertTrue(updatedState.isModalVisible)
        assertNotNull(updatedState.lastCastRecord)
    }

    @Test
    fun testResonanceComparisonDemo() = runBlocking {
        // Navigate to Resonance Demo Step 1 (Simple Fireball at step 21)
        while (viewModel.state.value.stepIndex < 21) {
            val curStep = viewModel.state.value.currentStep
            if (curStep is TutorialBattleStep.PracticeCast) {
                viewModel.castSpell(curStep.spellName)
                delay(1700)
                viewModel.advanceModal()
            } else if (curStep is TutorialBattleStep.AutoAction) {
                viewModel.advanceModal()
                delay(1200)
                viewModel.advanceModal()
            } else {
                viewModel.advanceModal()
            }
        }

        assertEquals(21, viewModel.state.value.stepIndex)
        val step21 = viewModel.state.value.currentStep as TutorialBattleStep.PracticeCast
        assertTrue(step21.isResonanceDemo)
        assertEquals(0, step21.resonanceDemoIndex)

        // Cast simple Fireball
        viewModel.castSpell("Fireball")
        delay(1800)

        val simpleRecord = viewModel.state.value.lastCastRecord
        assertNotNull(simpleRecord)
        assertEquals(ResonanceTier.BASIC, simpleRecord!!.resonanceTier)
        assertEquals(0, simpleRecord.bonusPercent)
        val simpleDamage = simpleRecord.damage

        // Advance to elaborate cast (step 22)
        viewModel.advanceModal()
        assertEquals(22, viewModel.state.value.stepIndex)
        val step22 = viewModel.state.value.currentStep as TutorialBattleStep.PracticeCast
        assertTrue(step22.isResonanceDemo)
        assertEquals(1, step22.resonanceDemoIndex)

        // Cast elaborate Fireball
        viewModel.castSpell("O primordial flame of creation, incinerate this practice target!")
        delay(1800)

        val elaborateRecord = viewModel.state.value.lastCastRecord
        assertNotNull(elaborateRecord)
        assertEquals(ResonanceTier.MASTER, elaborateRecord!!.resonanceTier)
        assertEquals(60, elaborateRecord.bonusPercent)
        val elaborateDamage = elaborateRecord.damage

        assertTrue(
            "Elaborate chant ($elaborateDamage dmg) must deal significantly more damage than simple chant ($simpleDamage dmg)",
            elaborateDamage > simpleDamage
        )
    }

    @Test
    fun testAutoActionBreathRestoresMana() = runBlocking {
        // Fast-forward to step 7 (Aethel Attune AutoAction)
        while (viewModel.state.value.stepIndex < 7) {
            val cur = viewModel.state.value.currentStep
            if (cur is TutorialBattleStep.PracticeCast) {
                viewModel.castSpell(cur.spellName)
                delay(1700)
                viewModel.advanceModal()
            } else {
                viewModel.advanceModal()
            }
        }

        assertEquals(7, viewModel.state.value.stepIndex)
        assertTrue(viewModel.state.value.currentStep is TutorialBattleStep.AutoAction)

        // Execute auto action
        viewModel.advanceModal()
        delay(1300)

        val state = viewModel.state.value
        assertEquals(ModalCardType.ACTION_RESULT, state.modalType)
        val aethel = state.party.first { it.id == "aethel" }
        assertEquals(aethel.maxMp, aethel.currentMp)
    }

    @Test
    fun testEnemyCounterAttackDemo() = runBlocking {
        // Fast-forward to step 24 (EnemyAttackDemo)
        while (viewModel.state.value.stepIndex < 24) {
            val cur = viewModel.state.value.currentStep
            if (cur is TutorialBattleStep.PracticeCast) {
                viewModel.castSpell(cur.spellName)
                delay(1700)
                viewModel.advanceModal()
            } else if (cur is TutorialBattleStep.AutoAction) {
                viewModel.advanceModal()
                delay(1200)
                viewModel.advanceModal()
            } else {
                viewModel.advanceModal()
            }
        }

        assertEquals(24, viewModel.state.value.stepIndex)
        assertTrue(viewModel.state.value.currentStep is TutorialBattleStep.EnemyAttackDemo)

        viewModel.advanceModal()
        delay(2200)

        val state = viewModel.state.value
        assertEquals(ModalCardType.ENEMY_ATTACK_RESULT, state.modalType)
        val targetHero = state.party.firstOrNull { it.id == "cedric" } ?: state.party.first()
        assertTrue("Hero should have taken counterattack damage", targetHero.currentHp < targetHero.maxHp)
    }

    @Test
    fun testFullResetTutorial() {
        // Advance a few steps and add Cedric
        for (i in 0 until 10) {
            viewModel.advanceModal()
        }
        assertTrue(viewModel.state.value.stepIndex > 0)

        viewModel.resetTutorial()

        val resetState = viewModel.state.value
        assertEquals(0, resetState.stepIndex)
        assertEquals(1, resetState.party.size)
        assertEquals("aethel", resetState.party[0].id)
        assertEquals(3, resetState.enemies.size)
        assertEquals(ModalCardType.NARRATOR_INTRO, resetState.modalType)
    }
}
