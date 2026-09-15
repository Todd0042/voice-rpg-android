package com.voicerpg.android.ui.story

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicerpg.android.model.DialogueChoice
import com.voicerpg.android.model.DialogueNode
import com.voicerpg.android.model.DialogueSpeaker
import com.voicerpg.android.model.SpeakerSide
import com.voicerpg.android.ui.combat.OptionsDialog
import com.voicerpg.android.ui.theme.LogosGold
import com.voicerpg.android.ui.theme.LogosGlow
import com.voicerpg.android.ui.theme.RetroBlack
import com.voicerpg.android.ui.theme.RetroBorder
import com.voicerpg.android.ui.theme.RetroBorderGold
import com.voicerpg.android.ui.theme.RetroPanel
import com.voicerpg.android.viewmodel.CombatViewModel
import com.voicerpg.android.viewmodel.StoryViewModel
import kotlin.math.sin

@Composable
fun StoryScreen(
    storyViewModel: StoryViewModel,
    combatViewModel: CombatViewModel,
    onOpenOptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val storyState by storyViewModel.state.collectAsState()
    val combatState by combatViewModel.state.collectAsState()
    val speechState by storyViewModel.speechManager.speechState.collectAsState()
    val isEyesFreeMode by combatViewModel.combatNarrator.isEyesFreeMode.collectAsState()
    val isAutoListen by storyViewModel.speechManager.isAutoListen.collectAsState()
    val isChimeMuted by storyViewModel.speechManager.isChimeMuted.collectAsState()

    val context = LocalContext.current
    val backgroundBitmap = remember(storyState.currentScene.backgroundAsset) {
        StoryAssetLoader.loadBitmap(context, storyState.currentScene.backgroundAsset)
    }
    val aethelBitmap = remember {
        StoryAssetLoader.loadBitmap(context, "portraits/aethel.jpg")
    }
    val cedricBitmap = remember {
        StoryAssetLoader.loadBitmap(context, "portraits/cedric.jpg")
    }

    // Gentle breathing & bobbing animation for speaking busts
    val infiniteTransition = rememberInfiniteTransition(label = "BustBobbing")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "BobSine"
    )
    val floatY = (sin(bobOffset) * 3f)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RetroBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Room / Exploration Scene Background Image
            if (backgroundBitmap != null) {
                Image(
                    bitmap = backgroundBitmap,
                    contentDescription = storyState.currentScene.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF14141E))
                )
            }

            // Atmospheric dark vignette and CRT scanline overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xCC08080C),
                                Color(0x3308080C),
                                Color(0x3308080C),
                                Color(0xE608080C)
                            )
                        )
                    )
            )

            // 2. Top Header: Chapter & Scene Location Banner on Left + Stacked Controls on Right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Title Banner Card (with set flex width and clean multi-line wrapping)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RetroPanel.copy(alpha = 0.92f))
                        .border(1.dp, RetroBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = storyState.currentScene.chapterTitle.uppercase(),
                            color = LogosGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            softWrap = true,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 14.sp
                        )
                        Text(
                            text = "📍 ${storyState.currentScene.name}",
                            color = Color.LightGray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Stacked Action Buttons (Outside the banner: Options above Battle)
                Column(
                    modifier = Modifier.width(96.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // Top: Options / Pocket Mode Toggle Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isEyesFreeMode) Color(0xFF1565C0) else RetroPanel.copy(alpha = 0.92f))
                            .border(1.dp, if (isEyesFreeMode) Color(0xFF64B5F6) else RetroBorder, RoundedCornerShape(6.dp))
                            .clickable { onOpenOptions() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEyesFreeMode) "🎧 POCKET" else "⚙️ OPTIONS",
                            color = if (isEyesFreeMode) Color(0xFFE3F2FD) else LogosGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }

                    // Bottom: Switch to Combat Sandbox Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF37474F).copy(alpha = 0.92f))
                            .border(1.dp, Color(0xFF78909C), RoundedCornerShape(6.dp))
                            .clickable { storyViewModel.switchToCombat() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚔️ BATTLE",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }
            }

            // 3. Characters on Left and Right Sides of Screen with minimal breathing animation
            val currentNode = storyState.currentNode
            val isAethelSpeaking = currentNode.side == SpeakerSide.LEFT
            val isRightSpeaking = currentNode.side == SpeakerSide.RIGHT

            // Left Character (Aethel)
            CharacterPortraitBust(
                bitmap = aethelBitmap,
                speaker = DialogueSpeaker.AETHEL,
                isSpeaking = isAethelSpeaking,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 12.dp, y = (-70 + (if (isAethelSpeaking) floatY else 0f)).dp)
            )

            // Right Character (Companion / NPC - e.g. Sir Cedric or Shadow Wisp)
            if (currentNode.speaker == DialogueSpeaker.CEDRIC || currentNode.id.contains("cedric") || currentNode.id.contains("crossroads") || currentNode.id.startsWith("camp_") || currentNode.id.startsWith("ch3_") || currentNode.id.startsWith("ch4_") || currentNode.id.startsWith("chapter3") || currentNode.id.startsWith("chapter4")) {
                CharacterPortraitBust(
                    bitmap = cedricBitmap,
                    speaker = DialogueSpeaker.CEDRIC,
                    isSpeaking = isRightSpeaking,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-12).dp, y = (-70 + (if (isRightSpeaking) floatY else 0f)).dp)
                )
            } else if (currentNode.speaker == DialogueSpeaker.SHADOW_WISP) {
                // Eerie Shadow Wisp Silhouette on the right side
                ShadowWispBust(
                    isSpeaking = isRightSpeaking,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-16).dp, y = (-75 + floatY).dp)
                )
            }

            // 4. Retro Dialogue Chat Bubble & Choices
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RetroSpeechBubble(
                    node = currentNode,
                    onTapToAdvance = {
                        if (currentNode.choices.isEmpty()) {
                            storyViewModel.advanceDialogue()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Choice Buttons (if node has branching choices)
                if (currentNode.choices.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentNode.choices.forEachIndexed { index, choice ->
                            val isCompleted = choice.completionFlag != null && storyState.narrativeFlags[choice.completionFlag] == true
                            DialogueChoiceItem(
                                index = index + 1,
                                choice = choice,
                                isCompleted = isCompleted,
                                onSelect = { storyViewModel.selectChoice(choice) }
                            )
                        }
                    }
                }

                // Battle Trigger Prompt if node triggers combat
                if (currentNode.triggerBattleEncounterId != null) {
                    Button(
                        onClick = { storyViewModel.advanceDialogue() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            text = "⚔️ COMMENCE BATTLE (TAP OR SAY 'FIREBALL')",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Ambient Audio & Voice Mic Prompt Indicator Bar
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentNode.choices.isEmpty()) "Tap or say 'Next' to continue" else "Tap choice or speak your decision",
                        color = Color.Gray,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    // Hands-free Mic Quick Tap
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (speechState is com.voicerpg.android.audio.SpeechState.Listening) Color(0xFFEF5350) else RetroPanel)
                            .border(1.dp, LogosGold, CircleShape)
                            .clickable {
                                storyViewModel.speechManager.startListening { utterance ->
                                    storyViewModel.handleStoryVoiceInput(utterance)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (speechState is com.voicerpg.android.audio.SpeechState.Listening) "🎙️ LISTENING..." else "🎤 SPEAK",
                            color = LogosGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

/**
 * Character Bust Portrait on Left or Right of the screen with speaking bounce & dimming.
 */
@Composable
private fun CharacterPortraitBust(
    bitmap: ImageBitmap?,
    speaker: DialogueSpeaker,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val scale = if (isSpeaking) 1.05f else 0.95f
    val alpha = if (isSpeaking) 1.0f else 0.60f
    val borderColor = if (isSpeaking) speaker.themeColor else RetroBorder

    Column(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(105.dp)
                .shadow(if (isSpeaking) 12.dp else 4.dp, RoundedCornerShape(12.dp), spotColor = speaker.themeColor)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F0F1A))
                .border(if (isSpeaking) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = speaker.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(speaker.themeColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = speaker.name.first().toString(),
                        color = speaker.themeColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Name Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isSpeaking) speaker.themeColor.copy(alpha = 0.9f) else Color(0xCC181824))
                .border(1.dp, if (isSpeaking) LogosGold else RetroBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = speaker.name,
                color = if (isSpeaking) RetroBlack else Color.LightGray,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Silhouette avatar for Shadow Wisps when encountered in dialogue.
 */
@Composable
private fun ShadowWispBust(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val scale = if (isSpeaking) 1.05f else 0.95f
    val alpha = if (isSpeaking) 1.0f else 0.65f

    Column(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(95.dp)
                .shadow(8.dp, CircleShape, spotColor = Color(0xFF7E57C2))
                .clip(CircleShape)
                .background(Color(0xCC1A0033))
                .border(if (isSpeaking) 2.dp else 1.dp, Color(0xFFB39DDB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👁️",
                fontSize = 36.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xCC2A0845))
                .border(1.dp, Color(0xFF9575CD), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Shadow Wisp",
                color = Color(0xFFE1BEE7),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * 16-Bit Retro Speech Bubble anchored dynamically towards the speaking character.
 */
@Composable
private fun RetroSpeechBubble(
    node: DialogueNode,
    onTapToAdvance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val speakerColor = node.speaker.themeColor

    // Subtle breathing pulse for advance arrow
    val infiniteTransition = rememberInfiniteTransition(label = "ArrowPulse")
    val arrowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ArrowAlpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xF00D0D18))
            .border(2.dp, if (node.side == SpeakerSide.CENTER_NARRATOR) LogosGold else speakerColor.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
            .clickable { onTapToAdvance() }
            .padding(14.dp)
    ) {
        Column {
            // Speaker Name Badge Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💬 ${node.speaker.name.uppercase()}",
                        color = speakerColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${node.speaker.title}",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (node.choices.isEmpty()) {
                    val promptText = when {
                        node.triggerBattleEncounterId != null -> "⚔️ TO BATTLE"
                        node.nextNodeId != null -> "▼ NEXT"
                        else -> "⭐ NEXT (CAMP)"
                    }
                    Text(
                        text = promptText,
                        color = LogosGold.copy(alpha = arrowAlpha),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Spoken Dialogue Content
            Text(
                text = node.text,
                color = Color.White,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

/**
 * Clickable and voice-triggerable dialogue choice button.
 */
@Composable
private fun DialogueChoiceItem(
    index: Int,
    choice: DialogueChoice,
    isCompleted: Boolean = false,
    onSelect: () -> Unit
) {
    val backgroundColor = if (isCompleted) Color(0xCC1A1C23) else RetroPanel.copy(alpha = 0.95f)
    val borderColor = if (isCompleted) Color(0xFF37474F) else RetroBorderGold
    val textColor = if (isCompleted) Color(0xFF78909C) else Color.White
    val indexColor = if (isCompleted) Color(0xFF546E7A) else LogosGold

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .then(if (!isCompleted) Modifier.clickable { onSelect() } else Modifier)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "[$index]",
                    color = indexColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = choice.text,
                    color = textColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }

            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x334CAF50))
                        .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "✓ COMPLETED",
                        color = Color(0xFF81C784),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Spoken keyword cue
                val keywordHint = choice.voiceKeywords.firstOrNull() ?: ""
                if (keywordHint.isNotBlank()) {
                    Text(
                        text = "Say \"$keywordHint\"",
                        color = Color(0xFF80D8FF),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
