package com.voicerpg.android.ui.combat

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicerpg.android.model.CombatPhase
import com.voicerpg.android.model.FloatingCombatText
import com.voicerpg.android.ui.environment.BattleEnvironmentCanvas
import com.voicerpg.android.ui.theme.LogosGold
import com.voicerpg.android.ui.theme.LogosGlow
import com.voicerpg.android.ui.theme.RetroBlack
import com.voicerpg.android.ui.theme.RetroBorder
import com.voicerpg.android.ui.theme.RetroDeepSlate
import com.voicerpg.android.ui.vfx.ParticleCanvas
import com.voicerpg.android.ui.vfx.SpellVfxCanvas
import com.voicerpg.android.viewmodel.CombatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RetroBattleScreen(
    viewModel: CombatViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val speechState by viewModel.speechManager.speechState.collectAsState()
    val liveTranscript by viewModel.speechManager.liveTranscript.collectAsState()
    val isChimeMuted by viewModel.speechManager.isChimeMuted.collectAsState()
    val isAutoListen by viewModel.speechManager.isAutoListen.collectAsState()
    val rmsLevel by viewModel.speechManager.rmsLevel.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RetroBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .offset(x = state.screenShakeOffsetX.dp, y = state.screenShakeOffsetY.dp)
        ) {
            // Main Battle Arena Layout
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Initiative & Round Status with Active Hero Highlight
                InitiativeTrack(
                    phase = state.phase,
                    roundNumber = state.roundNumber,
                    party = state.party,
                    activePartyMemberId = state.activePartyMemberId,
                    modifier = Modifier.padding(top = 6.dp, start = 8.dp, end = 8.dp)
                )

                // Environment Switcher Bar (4-Frame Living Backgrounds)
                EnvironmentSwitcherBar(
                    currentEnvironment = state.currentEnvironment,
                    onSelectEnvironment = { viewModel.setEnvironment(it) },
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                // Middle: 32-bit Tactical Battle Arena (Left: Party, Right: Monsters)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, RetroBorder, RoundedCornerShape(8.dp))
                ) {
                    // 4-Frame Living Environmental Background Canvas
                    BattleEnvironmentCanvas(
                        environment = state.currentEnvironment,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Battle Grid
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Flank (Heroes / Party)
                        PartyFlank(
                            party = state.party,
                            activePartyMemberId = state.activePartyMemberId,
                            onSelectHero = { viewModel.selectPartyMember(it) },
                            modifier = Modifier.weight(1f)
                        )

                        // Right Flank (Enemies / Monsters)
                        EnemyFlank(
                            enemies = state.enemies,
                            onSelectEnemy = { viewModel.selectEnemy(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Spell Projectiles Layer
                    SpellVfxCanvas(
                        engine = viewModel.spellVfxEngine,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Hardware-Accelerated Particle Canvas Layer
                    ParticleCanvas(
                        emitter = viewModel.particleEmitter,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Floating Damage & Healing Numbers
                    state.floatingTexts.forEach { ft ->
                        FloatingNumberItem(fct = ft)
                    }

                    // Logos Golden Banner Overlay (Top-Center Notification Style)
                    LogosBanner(
                        resonance = state.lastResonance,
                        visible = state.isLogosBannerVisible,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp, start = 12.dp, end = 12.dp)
                    )

                    // Battle Won / Lost Overlay
                    if (state.phase == CombatPhase.BATTLE_WON || state.phase == CombatPhase.BATTLE_LOST) {
                        Box(modifier = Modifier.align(Alignment.Center)) {
                            BattleConclusionOverlay(
                                isVictory = state.phase == CombatPhase.BATTLE_WON,
                                onRestart = { viewModel.restartBattle() }
                            )
                        }
                    }
                }

                // Bottom: Command Console with Mic & Resonance Meter
                ResonanceConsole(
                    phase = state.phase,
                    speechState = speechState,
                    liveTranscript = liveTranscript,
                    lastResonance = state.lastResonance,
                    activePartyMember = state.activePartyMember,
                    onStartListening = { viewModel.startVoiceListening() },
                    onStopListening = { viewModel.stopVoiceListening() },
                    onSubmitChant = { chant, acoustic -> viewModel.submitTypedChant(chant, acoustic) },
                    onCycleHero = { viewModel.cycleNextPartyMember() },
                    isChimeMuted = isChimeMuted,
                    onToggleChimeMute = { viewModel.speechManager.toggleChimeMute() },
                    isAutoListen = isAutoListen,
                    onToggleAutoListen = { viewModel.speechManager.toggleAutoListen() },
                    rmsLevel = rmsLevel,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun BattleConclusionOverlay(
    isVictory: Boolean,
    onRestart: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xE60A0A10))
            .border(2.dp, if (isVictory) LogosGold else Color.Red, RoundedCornerShape(12.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isVictory) "⚔️ VICTORY ⚔️" else "💀 DEFEAT 💀",
                color = if (isVictory) LogosGlow else Color(0xFFFF5252),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isVictory) "The Logos resonates through the sanctum." else "Your voice fades into the Blight.",
                color = Color.LightGray,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVictory) LogosGold else Color(0xFFC62828),
                    contentColor = RetroBlack
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "RESTART BATTLE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FloatingNumberItem(fct: FloatingCombatText) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(fct.id) {
        launch {
            offsetY.animateTo(-50f, animationSpec = tween(950))
        }
        launch {
            delay(550)
            alpha.animateTo(0f, animationSpec = tween(400))
        }
    }

    Box(
        modifier = Modifier
            .offset(
                x = (fct.startX / 2.6f).dp,
                y = ((fct.startY / 2.6f) + offsetY.value).dp
            )
    ) {
        Text(
            text = fct.text,
            color = fct.color.copy(alpha = alpha.value),
            fontSize = if (fct.isCrit) 16.sp else 13.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )
    }
}

