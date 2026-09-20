package com.voicerpg.android.ui.title

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicerpg.android.model.DialogueSpeaker
import com.voicerpg.android.ui.theme.FrostCyan
import com.voicerpg.android.ui.theme.HolyYellow
import com.voicerpg.android.ui.theme.LogosGold
import com.voicerpg.android.ui.theme.RetroBlack
import com.voicerpg.android.ui.theme.RetroBorder
import com.voicerpg.android.ui.theme.RetroBorderGold
import com.voicerpg.android.ui.theme.RetroDeepSlate
import com.voicerpg.android.ui.theme.RetroPanel
import com.voicerpg.android.viewmodel.TutorialPhase
import com.voicerpg.android.viewmodel.TutorialUiState
import com.voicerpg.android.viewmodel.TutorialViewModel

@Composable
fun TutorialScreen(
    viewModel: TutorialViewModel,
    onBack: () -> Unit,
    onStartNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val step = TutorialContent.getStep(state.stepIndex)

    LaunchedEffect(state.stepIndex, state.phase) {
        if (state.phase == TutorialPhase.PRE_CAST && !state.isListening) {
            viewModel.startListening()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RetroBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        TutorialHeader(
            stepIndex = state.stepIndex,
            totalSteps = TutorialContent.totalSteps,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            NarratorPanel(
                text = viewModel.getNarratorText(),
                speaker = viewModel.currentSpeaker
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                step == null -> Unit
                step.isCompletion -> CompletionSection(
                    onBeginJourney = { viewModel.startNewGameFromTutorial(onStartNewGame) },
                    onReturnToTitle = { viewModel.returnToTitleFromTutorial(onBack) }
                )
                step.isResonanceDemo && state.phase == TutorialPhase.ANIMATING -> ResonanceDemoResult(
                    state = state,
                    viewModel = viewModel
                )
                step.isResonanceDemo && state.phase == TutorialPhase.POST_CAST -> ResonanceDemoResult(
                    state = state,
                    viewModel = viewModel
                )
                step.isPractice -> PracticeSection(
                    state = state,
                    viewModel = viewModel
                )
                else -> {
                    val segment = TutorialContent.getSegment(step.segmentIndex)
                    if (segment != null && !segment.isResonanceDemo) {
                        SpellListSection(segment = segment)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        TutorialPromptBar(
            state = state,
            viewModel = viewModel
        )
    }
}

@Composable
private fun TutorialHeader(
    stepIndex: Int,
    totalSteps: Int,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = RetroDeepSlate),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .border(1.dp, RetroBorder, RoundedCornerShape(8.dp))
                .height(36.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back",
                tint = LogosGold,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "CHRONICLE GUIDE",
            color = LogosGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${stepIndex + 1}/$totalSteps",
            color = Color.LightGray.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }

    Spacer(modifier = Modifier.height(6.dp))

    LinearProgressIndicator(
        progress = { ((stepIndex + 1).toFloat() / totalSteps).coerceIn(0f, 1f) },
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp)),
        color = LogosGold,
        trackColor = RetroBorder
    )
}

@Composable
private fun NarratorPanel(
    text: String,
    speaker: DialogueSpeaker
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, RetroBorderGold.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .background(RetroDeepSlate.copy(alpha = 0.95f))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(speaker.themeColor.copy(alpha = 0.25f))
                .border(2.dp, speaker.themeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = speaker.name.first().toString(),
                color = speaker.themeColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = speaker.name,
                color = speaker.themeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun SpellListSection(segment: TutorialSegment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, segment.characterColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .background(RetroPanel.copy(alpha = 0.4f))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(segment.characterColor.copy(alpha = 0.2f))
                    .border(1.dp, segment.characterColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = segment.characterName.first().toString(),
                    color = segment.characterColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = segment.characterName,
                    color = segment.characterColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = segment.characterTitle,
                    color = segment.characterColor.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(segment.characterColor.copy(alpha = 0.3f))
        )

        Spacer(modifier = Modifier.height(8.dp))

        segment.spells.forEach { spell ->
            SpellInfoCard(spell = spell, compact = true)
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun PracticeSection(
    state: TutorialUiState,
    viewModel: TutorialViewModel
) {
    val spell = viewModel.currentSpell ?: return
    val segment = viewModel.currentSegment

    Column(modifier = Modifier.fillMaxWidth()) {
        if (segment != null) {
            ActiveCharacterBadge(
                name = segment.characterName,
                color = segment.characterColor
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        SpellInfoCard(spell = spell, compact = false)

        Spacer(modifier = Modifier.height(10.dp))

        TrainingDummyCard(
            hp = state.dummyHp,
            maxHp = state.dummyMaxHp,
            castResult = state.currentCastResult
        )

        if (state.currentCastResult != null && state.phase == TutorialPhase.POST_CAST) {
            Spacer(modifier = Modifier.height(8.dp))
            CastResultCard(result = state.currentCastResult)
        }
    }
}

@Composable
private fun ActiveCharacterBadge(name: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "CASTING AS:",
            color = Color.LightGray.copy(alpha = 0.5f),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = name,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun SpellInfoCard(spell: TutorialSpellInfo, compact: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, spell.school.themeColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .background(RetroDeepSlate.copy(alpha = 0.8f))
            .padding(if (compact) 8.dp else 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = spell.name,
                color = spell.school.themeColor,
                fontSize = if (compact) 12.sp else 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = when {
                    spell.manaRestorePct > 0f -> "FREE"
                    spell.isHeal -> "HEAL"
                    spell.isGuard -> "GUARD"
                    spell.hitsAll -> "AOE"
                    else -> "${spell.mpCost} MP"
                },
                color = when {
                    spell.manaRestorePct > 0f -> Color(0xFF81C784)
                    spell.isHeal -> Color(0xFF4CAF50)
                    spell.isGuard -> Color(0xFF42A5F5)
                    spell.hitsAll -> Color(0xFFFFB74D)
                    else -> Color(0xFF64B5F6)
                },
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        if (!compact) {
            Text(
                text = spell.school.displayName,
                color = spell.school.themeColor.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = spell.description,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            )
            if (spell.aliases.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Voice: ${spell.aliases.take(3).joinToString(", ")}",
                    color = Color.LightGray.copy(alpha = 0.5f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun TrainingDummyCard(
    hp: Int,
    maxHp: Int,
    castResult: com.voicerpg.android.viewmodel.TutorialCastResult?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, RetroBorder, RoundedCornerShape(8.dp))
            .background(RetroPanel.copy(alpha = 0.5f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TRAINING DUMMY",
            color = Color.LightGray.copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF795548).copy(alpha = 0.4f))
                .border(2.dp, Color(0xFF795548), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\uD83C\uDFAF",
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val hpRatio = (hp.toFloat() / maxHp).coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { hpRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (hpRatio > 0.5f) Color(0xFF4CAF50) else if (hpRatio > 0.25f) Color(0xFFFFB300) else Color(0xFFE53935),
            trackColor = RetroDeepSlate
        )
        Text(
            text = "$hp / $maxHp HP",
            color = Color.LightGray.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 2.dp)
        )

        if (castResult != null && !castResult.isBreath) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "-${castResult.damage}",
                color = Color(0xFFEF5350),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
        if (castResult != null && castResult.isBreath) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "+${castResult.damage} MP",
                color = Color(0xFF64B5F6),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun CastResultCard(
    result: com.voicerpg.android.viewmodel.TutorialCastResult
) {
    val tierColor = when {
        result.resonanceTier.contains("Transcendental") -> Color(0xFFFFD700)
        result.resonanceTier.contains("Mythic") -> Color(0xFFE040FB)
        result.resonanceTier.contains("Master") -> Color(0xFFFF5722)
        result.resonanceTier.contains("Adept") -> Color(0xFF4CAF50)
        result.resonanceTier == "Breath" -> Color(0xFF64B5F6)
        else -> Color.LightGray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, tierColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .background(tierColor.copy(alpha = 0.08f))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = result.resonanceTier,
                color = tierColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            if (!result.isBreath) {
                Text(
                    text = "+${result.bonusPercent}% (${String.format("%.1f", result.damageMultiplier)}x)",
                    color = tierColor.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        if (result.matchedRoots.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Matched: ${result.matchedRoots.joinToString(", ")}",
                color = Color.LightGray.copy(alpha = 0.6f),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun ResonanceDemoResult(
    state: TutorialUiState,
    viewModel: TutorialViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ActiveCharacterBadge(
            name = "Aethel",
            color = DialogueSpeaker.AETHEL.themeColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (state.resonanceDemoResults.isNotEmpty()) {
            state.resonanceDemoResults.forEachIndexed { index, result ->
                val label = if (index == 0) "Basic Incantation" else "Elaborate Incantation"
                ResonanceComparisonCard(label = label, result = result)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (state.currentCastResult != null && state.phase == TutorialPhase.ANIMATING) {
            TrainingDummyCard(
                hp = state.dummyHp,
                maxHp = state.dummyMaxHp,
                castResult = state.currentCastResult
            )
        }
    }
}

@Composable
private fun ResonanceComparisonCard(
    label: String,
    result: com.voicerpg.android.viewmodel.TutorialCastResult
) {
    val tierColor = when {
        result.resonanceTier.contains("Transcendental") -> Color(0xFFFFD700)
        result.resonanceTier.contains("Mythic") -> Color(0xFFE040FB)
        result.resonanceTier.contains("Master") -> Color(0xFFFF5722)
        result.resonanceTier.contains("Adept") -> Color(0xFF4CAF50)
        else -> Color.LightGray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, tierColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .background(RetroDeepSlate.copy(alpha = 0.85f))
            .padding(12.dp)
    ) {
        Text(
            text = label,
            color = Color.LightGray.copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = result.resonanceTier,
                color = tierColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${result.damage} damage",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "+${result.bonusPercent}% (${String.format("%.1f", result.damageMultiplier)}x)",
                    color = tierColor.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun CompletionSection(
    onBeginJourney: () -> Unit,
    onReturnToTitle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBeginJourney,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .border(2.dp, RetroBorderGold, RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B3B2B)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = HolyYellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BEGIN YOUR JOURNEY",
                    color = HolyYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onReturnToTitle,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .border(1.dp, RetroBorder, RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = RetroDeepSlate
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "RETURN TO TITLE",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun TutorialPromptBar(
    state: TutorialUiState,
    viewModel: TutorialViewModel
) {
    val step = TutorialContent.getStep(state.stepIndex) ?: return

    when (state.phase) {
        TutorialPhase.NARRATOR -> {
            if (!step.isCompletion) {
                Button(
                    onClick = { viewModel.onContinue() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RetroBorderGold, RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = RetroPanel),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "CONTINUE",
                        color = LogosGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
        TutorialPhase.PRE_CAST -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (state.isListening) Color(0xFF1B5E20) else RetroPanel.copy(alpha = 0.92f))
                    .border(
                        1.dp,
                        if (state.isListening) Color(0xFF81C784) else RetroBorderGold,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        if (state.isListening) viewModel.stopListening() else viewModel.startListening()
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (state.isListening) Color.White else LogosGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isListening) "Listening..." else state.promptText,
                        color = if (state.isListening) Color.White else Color.LightGray,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (state.isListening) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        TutorialPhase.ANIMATING -> {
            Button(
                onClick = { viewModel.onContinue() },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, FrostCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = FrostCyan.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "SEE RESULT",
                    color = FrostCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        TutorialPhase.POST_CAST -> {
            Button(
                onClick = { viewModel.onContinue() },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RetroBorderGold, RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = RetroPanel),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "CONTINUE",
                    color = LogosGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        TutorialPhase.COMPLETED -> Unit
    }
}
