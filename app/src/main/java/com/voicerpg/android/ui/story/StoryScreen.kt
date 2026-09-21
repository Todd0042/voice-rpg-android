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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import android.content.res.Configuration
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val backgroundBitmap = remember(storyState.currentScene.backgroundAsset) {
        StoryAssetLoader.loadBitmap(context, storyState.currentScene.backgroundAsset)
    }
    val aethelBitmap = remember {
        StoryAssetLoader.loadBitmap(context, "portraits/aethel.jpg")
    }

    val currentNode = storyState.currentNode
    val isNarratorSpeaking = (currentNode.speaker == DialogueSpeaker.NARRATOR)

    val narratorAsset = remember(currentNode.id) {
        DialogueSpeaker.NARRATOR.effectivePortraitAsset()
    }
    val narratorBitmap = remember(narratorAsset) {
        narratorAsset?.let { StoryAssetLoader.loadBitmap(context, it) }
    }

    val leftSpeaker = if (isNarratorSpeaking) DialogueSpeaker.NARRATOR else DialogueSpeaker.AETHEL
    val leftBitmap = if (isNarratorSpeaking) narratorBitmap else aethelBitmap
    val isLeftSpeaking = if (isNarratorSpeaking) true else (currentNode.speaker == DialogueSpeaker.AETHEL || currentNode.side == SpeakerSide.LEFT)

    val rightSpeaker: DialogueSpeaker? = remember(currentNode.id, currentNode.speaker.id, storyState.currentScene.name) {
        when {
            currentNode.speaker == DialogueSpeaker.NARRATOR -> {
                null
            }
            currentNode.speaker != DialogueSpeaker.AETHEL -> {
                currentNode.speaker
            }
            storyState.currentScene.chapterTitle.contains("Prologue", ignoreCase = true) ||
            storyState.currentScene.name.contains("Cottage", ignoreCase = true) -> {
                null
            }
            currentNode.id.contains("lyra", ignoreCase = true) ||
            storyState.partyStats.any { it.id == "lyra" } ||
            storyState.currentScene.chapterTitle.contains("Chapter 5", ignoreCase = true) ||
            storyState.currentScene.chapterTitle.contains("Chapter 6", ignoreCase = true) -> {
                DialogueSpeaker.LYRA
            }
            currentNode.id.contains("zephyr", ignoreCase = true) ||
            storyState.partyStats.any { it.id == "zephyr" } ||
            storyState.currentScene.chapterTitle.contains("Chapter 7", ignoreCase = true) ||
            storyState.currentScene.chapterTitle.contains("Chapter 8", ignoreCase = true) -> {
                DialogueSpeaker.ZEPHYR
            }
            currentNode.id.contains("malakor", ignoreCase = true) ||
            storyState.partyStats.any { it.id == "malakor" } ||
            storyState.currentScene.chapterTitle.contains("Chapter 9", ignoreCase = true) ||
            storyState.currentScene.chapterTitle.contains("Chapter 10", ignoreCase = true) -> {
                DialogueSpeaker.MALAKOR
            }
            else -> {
                DialogueSpeaker.CEDRIC
            }
        }
    }

    val rightSpeakerAsset = remember(rightSpeaker?.id) {
        rightSpeaker?.effectivePortraitAsset(currentNode.id)
    }
    val rightBitmap = remember(rightSpeakerAsset) {
        rightSpeakerAsset?.let { StoryAssetLoader.loadBitmap(context, it) }
    }

    val isRightSpeaking = rightSpeaker != null && (currentNode.speaker == rightSpeaker || currentNode.side == SpeakerSide.RIGHT)

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
                .then(
                    if (storyState.isFastForwarding) {
                        Modifier.clickable { storyViewModel.stopFastForward() }
                    } else if (speechState is com.voicerpg.android.audio.SpeechState.Standby) {
                        Modifier.clickable { storyViewModel.resumeVoiceListening() }
                    } else Modifier
                )
        ) {
            // 1. Room / Exploration Scene Background Image (Widescreen 16:9)
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

            // Atmospheric dark vignette
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xCC08080C),
                                Color(0x2208080C),
                                Color(0x2208080C),
                                Color(0xE608080C)
                            )
                        )
                    )
            )

            // 2. Top Header: Chapter & Scene Location Banner on Left + Stacked Controls on Right
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 12.dp, vertical = if (isLandscape) 4.dp else 8.dp)
            ) {
                if (storyState.isPureStoryMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF311B92).copy(alpha = 0.95f))
                            .border(1.dp, Color(0xFFBA68C8), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (storyState.isStoryAutoPlayPaused) "⏸ STORY MODE (PAUSED)" else "▶ STORY MODE (AUTO-PLAY)",
                                color = Color(0xFFF3E5F5),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (storyState.isStoryAutoPlayPaused) Color(0xFF2E7D32) else Color(0xFFEF6C00))
                                        .clickable { storyViewModel.toggleStoryAutoPlayPause() }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (storyState.isStoryAutoPlayPaused) "RESUME" else "PAUSE",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFC62828))
                                        .clickable { storyViewModel.returnToTitle() }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "EXIT",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                // Title Banner Card (with set flex width and clean multi-line wrapping)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = if (isLandscape) 44.dp else 60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RetroPanel.copy(alpha = 0.92f))
                        .border(1.dp, RetroBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = if (isLandscape) 4.dp else 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = storyState.currentScene.chapterTitle.uppercase(),
                            color = LogosGold,
                            fontSize = if (isLandscape) 10.sp else 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            softWrap = true,
                            maxLines = if (isLandscape) 1 else 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 14.sp
                        )
                        Text(
                            text = "📍 ${storyState.currentScene.name}",
                            color = Color.LightGray,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Stacked Action Buttons (Outside the banner: 2x2 grid for Log, Options, Skip, Battle)
                Column(
                    modifier = Modifier.width(if (isLandscape) 144.dp else 134.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    val btnHeight = if (isLandscape) 24.dp else 28.dp

                    // Row 1: Backlog + Options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Log / History Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(btnHeight)
                                .clip(RoundedCornerShape(6.dp))
                                .background(RetroPanel.copy(alpha = 0.92f))
                                .border(1.dp, RetroBorderGold, RoundedCornerShape(6.dp))
                                .clickable { storyViewModel.openBacklog() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📜 LOG",
                                color = LogosGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }

                        // Options / Pocket Mode Toggle Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(btnHeight)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isEyesFreeMode) Color(0xFF1565C0) else RetroPanel.copy(alpha = 0.92f))
                                .border(1.dp, if (isEyesFreeMode) Color(0xFF64B5F6) else RetroBorder, RoundedCornerShape(6.dp))
                                .clickable { onOpenOptions() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isEyesFreeMode) "🎧 POCKET" else "⚙️ OPT",
                                color = if (isEyesFreeMode) Color(0xFFE3F2FD) else LogosGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }

                    // Row 2: Skip/Fast-Forward + Battle Sandbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Skip / Fast-Forward Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(btnHeight)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (storyState.isFastForwarding) Color(0xFFD32F2F) else RetroPanel.copy(alpha = 0.92f))
                                .border(1.dp, if (storyState.isFastForwarding) Color(0xFFFF8A80) else RetroBorder, RoundedCornerShape(6.dp))
                                .clickable { storyViewModel.toggleFastForward() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (storyState.isFastForwarding) "⏹️ STOP" else "⏩ SKIP",
                                color = if (storyState.isFastForwarding) Color.White else LogosGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }

                        // Switch to Combat Sandbox Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(btnHeight)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF37474F).copy(alpha = 0.92f))
                                .border(1.dp, Color(0xFF78909C), RoundedCornerShape(6.dp))
                                .clickable { storyViewModel.switchToCombat() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚔️ BTL",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

            // 3. Characters & Dialogue Presentation (Responsive Portrait vs Landscape)
            if (!isLandscape) {
                // ==================== PORTRAIT ORIENTATION ====================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp, top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Character Portraits Row (Resting cleanly right on top of the speech bubble)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Left Character (Aethel or Narrator)
                        CharacterPortraitBust(
                            bitmap = leftBitmap,
                            speaker = leftSpeaker,
                            isSpeaking = isLeftSpeaking,
                            sizeDp = 84.dp,
                            modifier = Modifier.offset(y = (if (isLeftSpeaking) floatY else 0f).dp)
                        )

                        // Right Character (Companion / NPC)
                        if (rightSpeaker != null) {
                            if (rightSpeaker == DialogueSpeaker.SHADOW_WISP) {
                                ShadowWispBust(
                                    bitmap = rightBitmap,
                                    isSpeaking = isRightSpeaking,
                                    sizeDp = 76.dp,
                                    modifier = Modifier.offset(y = (if (isRightSpeaking) floatY else 0f).dp)
                                )
                            } else {
                                CharacterPortraitBust(
                                    bitmap = rightBitmap,
                                    speaker = rightSpeaker,
                                    isSpeaking = isRightSpeaking,
                                    sizeDp = 84.dp,
                                    modifier = Modifier.offset(y = (if (isRightSpeaking) floatY else 0f).dp)
                                )
                            }
                        } else {
                            // Empty spacer to keep left character nicely aligned
                            Spacer(modifier = Modifier.width(84.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Retro Speech Bubble
                    RetroSpeechBubble(
                        node = currentNode,
                        onTapToAdvance = {
                            if (currentNode.choices.isEmpty()) {
                                storyViewModel.advanceDialogue()
                            }
                        },
                        isEyesFreeMode = isEyesFreeMode,
                        speakerBitmapOverride = if (isNarratorSpeaking) narratorBitmap else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.Top)
                    )

                    // Choice Buttons (if node has branching choices)
                    if (currentNode.choices.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
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
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { storyViewModel.advanceDialogue() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD32F2F),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
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
                    VoiceInputPromptBar(
                        hasChoices = currentNode.choices.isNotEmpty(),
                        speechState = speechState,
                        onStartListening = { storyViewModel.resumeVoiceListening() }
                    )
                }
            } else {
                // ==================== LANDSCAPE ORIENTATION (Full Widescreen JRPG Layout) ====================
                // Left Flank: Protagonist or Narrator Bust standing proudly on the left
                CharacterPortraitBust(
                    bitmap = leftBitmap,
                    speaker = leftSpeaker,
                    isSpeaking = isLeftSpeaking,
                    sizeDp = 110.dp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 14.dp, bottom = 8.dp)
                        .offset(y = (if (isLeftSpeaking) floatY else 0f).dp)
                )

                // Right Flank: Companion / NPC Bust standing on the right
                if (rightSpeaker != null) {
                    if (rightSpeaker == DialogueSpeaker.SHADOW_WISP) {
                        ShadowWispBust(
                            bitmap = rightBitmap,
                            isSpeaking = isRightSpeaking,
                            sizeDp = 100.dp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 14.dp, bottom = 8.dp)
                                .offset(y = (if (isRightSpeaking) floatY else 0f).dp)
                        )
                    } else {
                        CharacterPortraitBust(
                            bitmap = rightBitmap,
                            speaker = rightSpeaker,
                            isSpeaking = isRightSpeaking,
                            sizeDp = 110.dp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 14.dp, bottom = 8.dp)
                                .offset(y = (if (isRightSpeaking) floatY else 0f).dp)
                        )
                    }
                }

                // Center Stage: Dialogue Bubble & Choices positioned neatly between characters
                Column(
                    modifier = Modifier
                        .padding(start = 130.dp, end = if (rightSpeaker != null) 130.dp else 16.dp, bottom = 6.dp)
                        .widthIn(max = 600.dp)
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Retro Speech Bubble
                    RetroSpeechBubble(
                        node = currentNode,
                        onTapToAdvance = {
                            if (currentNode.choices.isEmpty()) {
                                storyViewModel.advanceDialogue()
                            }
                        },
                        isEyesFreeMode = isEyesFreeMode,
                        speakerBitmapOverride = if (isNarratorSpeaking) narratorBitmap else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.Top)
                    )

                    // Choice Buttons in Landscape: Side-by-side or compact 2-row grid to preserve artwork space
                    if (currentNode.choices.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        if (currentNode.choices.size == 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currentNode.choices.forEachIndexed { index, choice ->
                                    val isCompleted = choice.completionFlag != null && storyState.narrativeFlags[choice.completionFlag] == true
                                    DialogueChoiceItem(
                                        index = index + 1,
                                        choice = choice,
                                        isCompleted = isCompleted,
                                        onSelect = { storyViewModel.selectChoice(choice) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        } else if (currentNode.choices.size == 3) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (i in 0..1) {
                                        val choice = currentNode.choices[i]
                                        val isCompleted = choice.completionFlag != null && storyState.narrativeFlags[choice.completionFlag] == true
                                        DialogueChoiceItem(
                                            index = i + 1,
                                            choice = choice,
                                            isCompleted = isCompleted,
                                            onSelect = { storyViewModel.selectChoice(choice) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                val choice3 = currentNode.choices[2]
                                val isCompleted = choice3.completionFlag != null && storyState.narrativeFlags[choice3.completionFlag] == true
                                DialogueChoiceItem(
                                    index = 3,
                                    choice = choice3,
                                    isCompleted = isCompleted,
                                    onSelect = { storyViewModel.selectChoice(choice3) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                currentNode.choices.forEachIndexed { index, choice ->
                                    val isCompleted = choice.completionFlag != null && storyState.narrativeFlags[choice.completionFlag] == true
                                    DialogueChoiceItem(
                                        index = index + 1,
                                        choice = choice,
                                        isCompleted = isCompleted,
                                        onSelect = { storyViewModel.selectChoice(choice) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // Battle Trigger Prompt if node triggers combat
                    if (currentNode.triggerBattleEncounterId != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { storyViewModel.advanceDialogue() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD32F2F),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚔️ COMMENCE BATTLE (TAP OR SAY 'FIREBALL')",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Ambient Audio & Voice Mic Prompt Indicator Bar
                    Spacer(modifier = Modifier.height(2.dp))
                    VoiceInputPromptBar(
                        hasChoices = currentNode.choices.isNotEmpty(),
                        speechState = speechState,
                        onStartListening = { storyViewModel.resumeVoiceListening() }
                    )
                }
            }

            // 4. Dialogue Backlog & Quest Recap Modal
            val questRecap = remember(
                storyState.currentScene.id,
                storyState.currentNode.id,
                storyState.narrativeFlags,
                storyState.partyStats
            ) {
                storyViewModel.getQuestRecap()
            }

            DialogueBacklogDialog(
                isOpen = storyState.isBacklogOpen,
                history = storyState.dialogueHistory,
                questRecap = questRecap,
                isRecapActive = storyState.isBacklogRecapActive,
                onTabSelect = { isRecap -> storyViewModel.setBacklogRecapTab(isRecap) },
                onClose = { storyViewModel.closeBacklog() },
                onReplay = { entry -> storyViewModel.replayDialogueEntry(entry) },
                onListenRecap = { spoken ->
                    combatViewModel.combatNarrator.stop()
                    combatViewModel.combatNarrator.speak(spoken, force = true)
                }
            )
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
    sizeDp: Dp = 100.dp,
    modifier: Modifier = Modifier
) {
    val scale = if (isSpeaking) 1.04f else 0.96f
    val alpha = if (isSpeaking) 1.0f else 0.70f
    val borderColor = if (isSpeaking) speaker.themeColor else RetroBorder

    Column(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp)
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

        Spacer(modifier = Modifier.height(3.dp))

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
    bitmap: ImageBitmap?,
    isSpeaking: Boolean,
    sizeDp: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val scale = if (isSpeaking) 1.04f else 0.96f
    val alpha = if (isSpeaking) 1.0f else 0.70f

    Column(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp)
                .shadow(8.dp, CircleShape, spotColor = Color(0xFF7E57C2))
                .clip(CircleShape)
                .background(Color(0xCC1A0033))
                .border(if (isSpeaking) 2.dp else 1.dp, Color(0xFFB39DDB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Corrupted Wisp",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = "👁️",
                    fontSize = if (sizeDp > 90.dp) 38.sp else 32.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

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
    isEyesFreeMode: Boolean = false,
    speakerBitmapOverride: ImageBitmap? = null,
    modifier: Modifier = Modifier
) {
    val speakerColor = node.speaker.themeColor
    val context = LocalContext.current
    val speakerAsset = remember(node.id) { node.speaker.effectivePortraitAsset() }
    val speakerBitmap = remember(speakerAsset, speakerBitmapOverride) {
        speakerBitmapOverride ?: speakerAsset?.let { StoryAssetLoader.loadBitmap(context, it) }
    }

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
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.Top),
            verticalArrangement = Arrangement.Top
        ) {
            // Speaker Name Badge Header with optional mini portrait circle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (speakerBitmap != null) {
                        Image(
                            bitmap = speakerBitmap,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(1.dp, speakerColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = "💬 ${node.speaker.name.uppercase()}",
                        color = speakerColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (node.choices.isEmpty()) {
                    val promptText = when {
                        node.id == "epilogue_credits" -> "👑 PLAY AGAIN"
                        node.triggerBattleEncounterId != null -> "⚔️ TO BATTLE"
                        isEyesFreeMode && node.nextNodeId != null -> "⏩ AUTO NEXT (1.5s)"
                        node.nextNodeId != null -> "▼ NEXT"
                        isEyesFreeMode -> "⏩ AUTO CAMP (1.5s)"
                        else -> "⭐ NEXT (CAMP)"
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = promptText,
                        color = LogosGold.copy(alpha = arrowAlpha),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }
            }

            // Subtitle for title if present
            if (node.speaker.title.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "• ${node.speaker.title}",
                    color = Color.Gray,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Spoken Dialogue Content
            Text(
                text = node.text.trim(),
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

/**
 * Ambient Audio & Voice Mic Prompt Indicator Bar
 */
@Composable
private fun VoiceInputPromptBar(
    hasChoices: Boolean,
    speechState: com.voicerpg.android.audio.SpeechState,
    onStartListening: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isListening = speechState is com.voicerpg.android.audio.SpeechState.Listening
    val isStandby = speechState is com.voicerpg.android.audio.SpeechState.Standby

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when {
                isStandby -> "⏸️ Standing by — tap screen to resume"
                !hasChoices -> "Tap or say 'Next' to continue"
                else -> "Tap choice or speak your decision"
            },
            color = if (isStandby) Color(0xFFFFD54F) else Color.Gray,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )

        // Hands-free Mic Quick Tap
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    when {
                        isListening -> Color(0xFFEF5350)
                        isStandby -> Color(0xFF423810)
                        else -> RetroPanel
                    }
                )
                .border(1.dp, if (isStandby) Color(0xFFFFD54F) else LogosGold, CircleShape)
                .clickable { onStartListening() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = when {
                    isListening -> "🎙️ LISTENING..."
                    isStandby -> "⏸️ STANDBY"
                    else -> "🎤 SPEAK"
                },
                color = if (isStandby) Color(0xFFFFD54F) else LogosGold,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
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
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isCompleted) Color(0xCC1A1C23) else RetroPanel.copy(alpha = 0.95f)
    val borderColor = if (isCompleted) Color(0xFF37474F) else RetroBorderGold
    val textColor = if (isCompleted) Color(0xFF78909C) else Color.White
    val indexColor = if (isCompleted) Color(0xFF546E7A) else LogosGold

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .then(if (!isCompleted) Modifier.clickable { onSelect() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 7.dp)
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
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = choice.text,
                    color = textColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x334CAF50))
                        .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "✓ DONE",
                        color = Color(0xFF81C784),
                        fontSize = 8.sp,
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
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
