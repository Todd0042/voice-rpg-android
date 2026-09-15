package com.voicerpg.android.ui.combat

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.model.CombatPhase
import com.voicerpg.android.model.FloatingCombatText
import com.voicerpg.android.ui.environment.BattleEnvironmentCanvas
import com.voicerpg.android.ui.theme.LogosGold
import com.voicerpg.android.ui.theme.LogosGlow
import com.voicerpg.android.ui.theme.RetroBlack
import com.voicerpg.android.ui.theme.RetroBorder
import com.voicerpg.android.ui.theme.RetroDeepSlate
import com.voicerpg.android.ui.theme.RetroPanel
import com.voicerpg.android.ui.vfx.ParticleCanvas
import com.voicerpg.android.ui.vfx.SpellVfxCanvas
import com.voicerpg.android.viewmodel.CombatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RetroBattleScreen(
    viewModel: CombatViewModel,
    onReturnToStory: (() -> Unit)? = null,
    onContinueStory: (() -> Unit)? = null,
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
                .then(
                    if (speechState is com.voicerpg.android.audio.SpeechState.Standby && state.phase == CombatPhase.PLAYER_INPUT) {
                        Modifier.clickable { viewModel.resumeVoiceListening() }
                    } else Modifier
                )
        ) {
            // Main Battle Arena Layout
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Initiative & Round Status with Quick Options / Pocket Mode Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    InitiativeTrack(
                        phase = state.phase,
                        roundNumber = state.roundNumber,
                        party = state.party,
                        activePartyMemberId = state.activePartyMemberId,
                        modifier = Modifier.weight(1f)
                    )

                    // Return to Story Mode button (if provided)
                    if (onReturnToStory != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1B5E20))
                                .border(1.dp, Color(0xFF66BB6A), RoundedCornerShape(8.dp))
                                .clickable { onReturnToStory() }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📖 STORY",
                                color = Color(0xFFE8F5E9),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Quick Settings Button (Options modal & Pocket Mode status)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (state.isEyesFreeMode) Color(0xFF1565C0) else RetroPanel.copy(alpha = 0.9f))
                            .border(1.dp, if (state.isEyesFreeMode) Color(0xFF64B5F6) else RetroBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.openOptions() }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.isEyesFreeMode) "🎧 POCKET" else "⚙️ OPTIONS",
                            color = if (state.isEyesFreeMode) Color(0xFFE3F2FD) else LogosGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Environment & Story Encounter Switcher Bar
                EnvironmentSwitcherBar(
                    currentEnvironment = state.currentEnvironment,
                    onSelectEnvironment = { viewModel.setEnvironment(it) },
                    onSelectEncounter = { viewModel.startEncounter(it) },
                    onSummonMinion = {
                        val minionNum = (state.enemies.size + 1)
                        val minion = StoryEncounters.createMinion(
                            idSuffix = "$minionNum",
                            name = "Blighted Minion $minionNum",
                            hp = 180
                        )
                        viewModel.summonReinforcements(listOf(minion))
                    },
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
                        partyCount = state.party.size,
                        enemyCount = state.enemies.size,
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
                                onRestart = { viewModel.restartBattle() },
                                onContinueStory = onContinueStory
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
                    onStartListening = { viewModel.resumeVoiceListening() },
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
    onRestart: () -> Unit,
    onContinueStory: (() -> Unit)? = null
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

            if (isVictory && onContinueStory != null) {
                Button(
                    onClick = onContinueStory,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogosGold,
                        contentColor = RetroBlack
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "CONTINUE STORY ➔",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVictory) Color(0xFF37474F) else Color(0xFFC62828),
                    contentColor = if (isVictory) Color.White else RetroBlack
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "RESTART BATTLE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isVictory) "🎤 Say \"Commence Story\" or \"Restart Battle\"" else "🎤 Say \"Restart Battle\" to fight again",
                color = LogosGold.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
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

