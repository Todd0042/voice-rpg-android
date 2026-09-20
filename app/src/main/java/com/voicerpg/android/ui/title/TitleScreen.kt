package com.voicerpg.android.ui.title

import android.text.format.DateUtils
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.voicerpg.android.audio.SpeechState
import com.voicerpg.android.model.SaveSlotInfo
import com.voicerpg.android.model.SaveSummary
import com.voicerpg.android.ui.story.StoryAssetLoader
import com.voicerpg.android.ui.theme.FrostCyan
import com.voicerpg.android.ui.theme.HolyYellow
import com.voicerpg.android.ui.theme.LogosGold
import com.voicerpg.android.ui.theme.LogosGlow
import com.voicerpg.android.ui.theme.RetroBlack
import com.voicerpg.android.ui.theme.RetroBorder
import com.voicerpg.android.ui.theme.RetroBorderGold
import com.voicerpg.android.ui.theme.RetroDeepSlate
import com.voicerpg.android.ui.theme.RetroPanel

/**
 * Atmospheric Title & Main Menu Screen for VoiceRPG: Echoes of the Logos.
 * Provides hands-free voice command navigation, save summary preview, overwrite confirmation,
 * and immediate access to Audio Setup, Options, and Campaign Continue.
 */
@Composable
fun TitleScreen(
    hasSave: Boolean,
    saveSummary: SaveSummary?,
    saveSlots: List<SaveSlotInfo> = emptyList(),
    currentSlot: Int = 1,
    speechState: SpeechState,
    isAutoListen: Boolean,
    onContinue: () -> Unit = {},
    onNewGame: () -> Unit = {},
    onSelectSlot: (Int) -> Unit = {},
    onDeleteSlot: (Int) -> Unit = {},
    onContinueSlot: (Int) -> Unit = { onContinue() },
    onNewGameInSlot: (Int) -> Unit = { onNewGame() },
    onAudioSetup: () -> Unit,
    onOptions: () -> Unit,
    onTutorial: () -> Unit,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showOverwriteDialog by remember { mutableStateOf(false) }
    var showSlotArchivesDialog by remember { mutableStateOf(false) }
    var slotDialogIsNewGameMode by remember { mutableStateOf(false) }

    val backgroundBitmap = remember {
        StoryAssetLoader.loadBitmap(context, "backgrounds/bg_summit.jpg")
    }

    val infiniteTransition = rememberInfiniteTransition(label = "title_glow")
    val titleGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "title_alpha"
    )

    val isListening = speechState is SpeechState.Listening

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RetroBlack)
    ) {
        // Background Artwork with Dark Mythic Vignette Scrim
        if (backgroundBitmap != null) {
            Image(
                bitmap = backgroundBitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RetroBlack.copy(alpha = 0.70f),
                            RetroBlack.copy(alpha = 0.88f),
                            RetroBlack.copy(alpha = 0.98f)
                        )
                    )
                )
        )

        // Main Title Screen Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header / Title Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "VOICE RPG",
                    color = LogosGold.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ECHOES OF THE LOGOS",
                    color = LogosGold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    modifier = Modifier.scale(1.0f + (titleGlowAlpha - 0.65f) * 0.05f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, LogosGold, Color.Transparent)
                            )
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A Voice-First High Fantasy Chronicle",
                    color = Color.LightGray.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Center Actions: Continue / New Game / Setup / Options
            Column(
                modifier = Modifier.fillMaxWidth(0.92f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Continue Button (with Save Preview)
                if (hasSave && saveSummary != null) {
                    Button(
                        onClick = { onContinueSlot(currentSlot) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, RetroBorderGold, RoundedCornerShape(10.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RetroPanel.copy(alpha = 0.95f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = LogosGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CONTINUE [SLOT $currentSlot]",
                                        color = LogosGold,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                if (saveSummary.timestamp > 0) {
                                    val relativeTime = DateUtils.getRelativeTimeSpanString(
                                        saveSummary.timestamp,
                                        System.currentTimeMillis(),
                                        DateUtils.MINUTE_IN_MILLIS
                                    ).toString()
                                    Text(
                                        text = relativeTime,
                                        color = Color.LightGray.copy(alpha = 0.7f),
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "⚔️ ${saveSummary.heroName} • ${saveSummary.heroClassTitle}  |  👥 ${saveSummary.partySize} In Fellowship",
                                color = FrostCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "📜 ${saveSummary.chapterTitle} • ${saveSummary.sceneName}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // 2. Chronicle Archives / Multi-Slot Management Button
                Button(
                    onClick = {
                        slotDialogIsNewGameMode = false
                        showSlotArchivesDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RetroBorderGold.copy(alpha = 0.7f), RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RetroDeepSlate.copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = LogosGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CHRONICLE ARCHIVES (3 SLOTS)",
                            color = LogosGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // 3. New Game Button
                Button(
                    onClick = {
                        val hasAnySave = saveSlots.any { !it.isEmpty } || hasSave
                        if (hasAnySave) {
                            slotDialogIsNewGameMode = true
                            showSlotArchivesDialog = true
                        } else {
                            onNewGameInSlot(currentSlot)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (!hasSave) RetroBorderGold else RetroBorder,
                            RoundedCornerShape(8.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!hasSave) Color(0xFF1B3B2B) else RetroDeepSlate.copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = if (!hasSave) HolyYellow else Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasSave) "START NEW JOURNEY" else "BEGIN JOURNEY",
                            color = if (!hasSave) HolyYellow else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // 3. Audio & Voice Calibration Button
                Button(
                    onClick = onAudioSetup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RetroBorder, RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RetroDeepSlate.copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = FrostCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AUDIO SETUP & VOICES",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // 4. Options & Settings Button
                Button(
                    onClick = onOptions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RetroBorder, RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RetroDeepSlate.copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = LogosGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GAME OPTIONS & ACCESSIBILITY",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // 5. Chronicle Guide / Tutorial Button
                Button(
                    onClick = onTutorial,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RetroBorder, RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RetroDeepSlate.copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.MenuBook,
                            contentDescription = null,
                            tint = FrostCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CHRONICLE GUIDE",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer / Speech Voice Status & Tap-to-Speak Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isListening) Color(0xFF1B5E20) else RetroPanel.copy(alpha = 0.92f))
                    .border(
                        1.dp,
                        if (isListening) Color(0xFF81C784) else RetroBorderGold,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        if (isListening) onStopListening() else onStartListening()
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (isListening) Color.White else LogosGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isListening) {
                            "🎤 Listening... Speak 'Continue', 'New Game', 'Tutorial', or 'Options'"
                        } else {
                            "🎤 Speak 'Continue', 'New Game', 'Tutorial', or Tap to Talk"
                        },
                        color = if (isListening) Color.White else Color.LightGray,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isListening) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Overwrite Existing Save Confirmation Dialog
        if (showOverwriteDialog) {
            Dialog(onDismissRequest = { showOverwriteDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(RetroBlack.copy(alpha = 0.98f))
                        .border(2.dp, Color(0xFFD32F2F), RoundedCornerShape(12.dp))
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "⚠️ OVERWRITE EXISTING CHRONICLE?",
                            color = Color(0xFFEF5350),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "An active journey for ${saveSummary?.heroName ?: "your Hero"} in ${saveSummary?.chapterTitle ?: "Aethelgard"} is currently saved.\n\nBeginning a new campaign will replace this save file. Are you certain you wish to proceed?",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showOverwriteDialog = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, RetroBorder, RoundedCornerShape(6.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = RetroDeepSlate),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "CANCEL",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Button(
                                onClick = {
                                    showOverwriteDialog = false
                                    onNewGame()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, Color(0xFFEF5350), RoundedCornerShape(6.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "OVERWRITE",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Multi-Slot Campaign Archives Modal Dialog
        if (showSlotArchivesDialog) {
            SaveSlotDialog(
                slots = saveSlots,
                currentSlot = currentSlot,
                isNewGameMode = slotDialogIsNewGameMode,
                onSelectAndLoad = { slot ->
                    showSlotArchivesDialog = false
                    onContinueSlot(slot)
                },
                onSelectAndNewGame = { slot ->
                    showSlotArchivesDialog = false
                    onNewGameInSlot(slot)
                },
                onDeleteSlot = { slot ->
                    onDeleteSlot(slot)
                },
                onDismiss = {
                    showSlotArchivesDialog = false
                }
            )
        }
    }
}

/**
 * Multi-Slot Chronicle Manager Dialog supporting up to 3 independent campaign slots.
 * Allows instant slot switching, new campaign creation in an empty slot, and safe slot deletion.
 */
@Composable
fun SaveSlotDialog(
    slots: List<SaveSlotInfo>,
    currentSlot: Int,
    isNewGameMode: Boolean,
    onSelectAndLoad: (Int) -> Unit,
    onSelectAndNewGame: (Int) -> Unit,
    onDeleteSlot: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var slotPendingDelete by remember { mutableStateOf<Int?>(null) }
    var slotPendingOverwrite by remember { mutableStateOf<Int?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, RetroBorderGold, RoundedCornerShape(16.dp)),
            color = RetroBlack.copy(alpha = 0.98f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isNewGameMode) "SELECT CHRONICLE SLOT" else "CHRONICLE ARCHIVES",
                            color = LogosGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isNewGameMode) "Choose where your new journey shall be written" else "Manage your active fellowship campaigns",
                            color = Color.LightGray.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Slots (1, 2, 3)
                val allSlots = (1..3).map { index ->
                    slots.firstOrNull { it.slotIndex == index } ?: SaveSlotInfo(index, null)
                }

                allSlots.forEach { slotInfo ->
                    val isActive = slotInfo.slotIndex == currentSlot
                    val summary = slotInfo.summary

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(RetroPanel.copy(alpha = 0.9f))
                            .border(
                                width = if (isActive) 2.dp else 1.dp,
                                color = if (isActive) RetroBorderGold else RetroBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "SLOT ${slotInfo.slotIndex}",
                                        color = if (isActive) HolyYellow else LogosGold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (isActive) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(LogosGold.copy(alpha = 0.2f))
                                                .border(1.dp, LogosGold, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                color = LogosGold,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }

                                if (summary != null && summary.timestamp > 0) {
                                    val relative = DateUtils.getRelativeTimeSpanString(
                                        summary.timestamp,
                                        System.currentTimeMillis(),
                                        DateUtils.MINUTE_IN_MILLIS
                                    ).toString()
                                    Text(
                                        text = relative,
                                        color = Color.LightGray.copy(alpha = 0.65f),
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (summary != null) {
                                Text(
                                    text = "⚔️ ${summary.heroName} • ${summary.heroClassTitle}  |  👥 ${summary.partySize}",
                                    color = FrostCyan,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "📜 ${summary.chapterTitle} • ${summary.sceneName}",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (isNewGameMode) {
                                        Button(
                                            onClick = { slotPendingOverwrite = slotInfo.slotIndex },
                                            modifier = Modifier
                                                .weight(1f)
                                                .border(1.dp, Color(0xFFEF5350), RoundedCornerShape(6.dp)),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "OVERWRITE",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Button(
                                            onClick = { onSelectAndLoad(slotInfo.slotIndex) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .border(1.dp, RetroBorderGold, RoundedCornerShape(6.dp)),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3B2B)),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = HolyYellow,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "RESUME",
                                                    color = HolyYellow,
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = { slotPendingDelete = slotInfo.slotIndex },
                                            modifier = Modifier
                                                .border(1.dp, Color(0xFF7F1D1D), RoundedCornerShape(6.dp)),
                                            colors = ButtonDefaults.buttonColors(containerColor = RetroDeepSlate),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color(0xFFEF5350),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "✨ Empty Chronicle Archive",
                                    color = Color.Gray,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { onSelectAndNewGame(slotInfo.slotIndex) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, RetroBorderGold, RoundedCornerShape(6.dp)),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3B2B)),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = HolyYellow,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "START NEW JOURNEY HERE",
                                            color = HolyYellow,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RetroBorder, RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = RetroDeepSlate),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "RETURN",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }

    // Sub-dialog: Confirm Delete
    slotPendingDelete?.let { slotNum ->
        Dialog(onDismissRequest = { slotPendingDelete = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RetroBlack.copy(alpha = 0.98f))
                    .border(2.dp, Color(0xFFD32F2F), RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "⚠️ DELETE SAVE SLOT $slotNum?",
                        color = Color(0xFFEF5350),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "This will permanently erase all campaign progress, party stats, and story decisions recorded in Slot $slotNum.\n\nThis action cannot be undone.",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { slotPendingDelete = null },
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, RetroBorder, RoundedCornerShape(6.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = RetroDeepSlate),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "CANCEL",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = {
                                onDeleteSlot(slotNum)
                                slotPendingDelete = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFFEF5350), RoundedCornerShape(6.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "DELETE FOREVER",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }

    // Sub-dialog: Confirm Overwrite
    slotPendingOverwrite?.let { slotNum ->
        Dialog(onDismissRequest = { slotPendingOverwrite = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RetroBlack.copy(alpha = 0.98f))
                    .border(2.dp, Color(0xFFD32F2F), RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "⚠️ OVERWRITE SAVE SLOT $slotNum?",
                        color = Color(0xFFEF5350),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Beginning a new chronicle in Slot $slotNum will erase its previous save data.\n\nAre you sure you wish to proceed?",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { slotPendingOverwrite = null },
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, RetroBorder, RoundedCornerShape(6.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = RetroDeepSlate),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "CANCEL",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = {
                                val s = slotNum
                                slotPendingOverwrite = null
                                onDismiss()
                                onSelectAndNewGame(s)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFFEF5350), RoundedCornerShape(6.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "OVERWRITE",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
