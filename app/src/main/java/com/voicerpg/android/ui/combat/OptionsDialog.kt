package com.voicerpg.android.ui.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.voicerpg.android.ui.theme.LogosGold
import com.voicerpg.android.ui.theme.LogosGlow
import com.voicerpg.android.ui.theme.RetroBlack
import com.voicerpg.android.ui.theme.RetroBorder
import com.voicerpg.android.ui.theme.RetroBorderGold
import com.voicerpg.android.ui.theme.RetroPanel

@Composable
fun OptionsDialog(
    isOpen: Boolean,
    isEyesFreeMode: Boolean,
    isNarrationEnabled: Boolean,
    isReadChoicesEnabled: Boolean,
    isCharacterPitchEnabled: Boolean,
    isSpeakerAttributionEnabled: Boolean,
    onToggleSpeakerAttribution: () -> Unit,
    speechRate: Float,
    isAutoListen: Boolean,
    isChimeMuted: Boolean,
    isMusicEnabled: Boolean = true,
    musicVolume: Float = 0.55f,
    onToggleEyesFreeMode: () -> Unit,
    onToggleNarration: () -> Unit,
    onToggleReadChoices: () -> Unit,
    onToggleCharacterPitch: () -> Unit,
    onSpeechRateChange: (Float) -> Unit,
    onToggleAutoListen: () -> Unit,
    onToggleChimeMute: () -> Unit,
    onToggleMusic: () -> Unit = {},
    onMusicVolumeChange: (Float) -> Unit = {},
    onOpenVoiceSettings: (() -> Unit)? = null,
    onOpenVoiceAssignment: (() -> Unit)? = null,
    isDebugWarpEnabled: Boolean = false,
    onOpenDebugWarp: (() -> Unit)? = null,
    onClose: () -> Unit
) {
    if (!isOpen) return

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(RetroBlack.copy(alpha = 0.96f))
                .border(2.dp, RetroBorderGold, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚙️ GAME & AUDIO OPTIONS",
                            color = LogosGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Voice-Controlled • Narration • Pocket Mode",
                            color = Color.LightGray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Close icon button
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(RetroPanel)
                            .border(1.dp, RetroBorder, CircleShape)
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✖",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // =============================================================
                // Section 1: Story & Dialogue Narration
                // =============================================================
                Text(
                    text = "📖 STORY & DIALOGUE NARRATION",
                    color = LogosGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle: Read Dialogue Aloud
                OptionToggleRow(
                    title = "📖 Read Dialogue Aloud",
                    subtitle = "Phone reads story narration and character voices aloud (screen on or off).",
                    voiceHint = "Voice command: \"Toggle narration\" or \"Narration\"",
                    checked = isNarrationEnabled,
                    activeColor = Color(0xFFFFD54F),
                    onCheckedChange = { onToggleNarration() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle: Read Dialogue Choices
                OptionToggleRow(
                    title = "🔢 Read Dialogue Choices",
                    subtitle = "Speaks available response options after the dialogue line finishes.",
                    voiceHint = "Voice command: \"Read choices\" or \"Toggle choices\"",
                    checked = isReadChoicesEnabled,
                    activeColor = Color(0xFF80D8FF),
                    onCheckedChange = { onToggleReadChoices() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle: Character Voice Pitch Modulation
                OptionToggleRow(
                    title = "🎭 Character Voice Pitch",
                    subtitle = "Modulates pitch per character (Cedric deep knight, Aethel spirited, Wisps raspy).",
                    voiceHint = "Dynamic pitch shifting",
                    checked = isCharacterPitchEnabled,
                    activeColor = Color(0xFFCE93D8),
                    onCheckedChange = { onToggleCharacterPitch() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle: Speaker Name Attribution
                OptionToggleRow(
                    title = "🗣️ Speaker Names Aloud",
                    subtitle = "Narrator announces who is talking (\"Sir Cedric says...\"). Off keeps each character's distinct voice, just without the name.",
                    voiceHint = "Voice command: \"Speaker names\" or \"Who is speaking\"",
                    checked = isSpeakerAttributionEnabled,
                    activeColor = Color(0xFFFFB74D),
                    onCheckedChange = { onToggleSpeakerAttribution() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Speech Speed Selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RetroPanel.copy(alpha = 0.8f))
                        .border(1.dp, RetroBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ Speech Speed",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "%.2fx".format(speechRate),
                                color = LogosGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                0.90f to "0.9x Relaxed",
                                1.05f to "1.05x Normal",
                                1.25f to "1.25x Fast"
                            ).forEach { (rate, label) ->
                                val isSelected = kotlin.math.abs(speechRate - rate) < 0.08f
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) LogosGold else RetroBlack)
                                        .border(
                                            1.dp,
                                            if (isSelected) LogosGold else RetroBorder,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onSpeechRateChange(rate) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) RetroBlack else Color.LightGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                if (onOpenVoiceSettings != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(RetroPanel.copy(alpha = 0.8f))
                            .border(1.dp, RetroBorderGold, RoundedCornerShape(8.dp))
                            .clickable { onOpenVoiceSettings() }
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🎙️ Install Companion Voices",
                                    color = LogosGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Open system Text-to-Speech settings to download additional English voices for Sir Cedric, Lyra, Zephyr, and Malakor.",
                                    color = Color.LightGray,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OPEN ➔",
                                color = LogosGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                if (onOpenVoiceAssignment != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(RetroPanel.copy(alpha = 0.8f))
                            .border(1.dp, Color(0xFF64B5F6), RoundedCornerShape(8.dp))
                            .clickable { onOpenVoiceAssignment() }
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🎭 Assign & Customize Companion Voices",
                                    color = Color(0xFF90CAF9),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Audition character quotes, cycle voice models for Sir Cedric, Lyra, Zephyr, and others without restarting your game.",
                                    color = Color.LightGray,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ASSIGN ➔",
                                color = Color(0xFF90CAF9),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                if (isDebugWarpEnabled && onOpenDebugWarp != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(RetroBlack.copy(alpha = 0.6f))
                            .border(1.dp, Color(0xFFAB47BC), RoundedCornerShape(8.dp))
                            .clickable { onOpenDebugWarp() }
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "up up down down left right left right b a start",
                                color = Color(0xFFCE93D8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "➔",
                                color = Color(0xFFCE93D8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // =============================================================
                // Section 2: Ambient Music & Sound
                // =============================================================
                Text(
                    text = "🎵 AMBIENT MUSIC & SOUND",
                    color = LogosGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle: Background Music
                OptionToggleRow(
                    title = "🎵 Background Music (BGM)",
                    subtitle = "Continuous ambient soundtrack per Act & battle arena. Automatically ducks during narration.",
                    voiceHint = "Voice command: \"Toggle music\" or \"Music on/off\"",
                    checked = isMusicEnabled,
                    activeColor = Color(0xFFFFD54F),
                    onCheckedChange = { onToggleMusic() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Music Volume Selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RetroPanel.copy(alpha = 0.8f))
                        .border(1.dp, RetroBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔊 Music Volume",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${(musicVolume * 100).toInt()}%",
                                color = LogosGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                0.25f to "25% Soft",
                                0.55f to "55% Balanced",
                                0.80f to "80% Epic",
                                1.00f to "100% Max"
                            ).forEach { (vol, label) ->
                                val isSelected = kotlin.math.abs(musicVolume - vol) < 0.12f
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) LogosGold else RetroBlack)
                                        .border(
                                            1.dp,
                                            if (isSelected) LogosGold else RetroBorder,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onMusicVolumeChange(vol) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) RetroBlack else Color.LightGray,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // =============================================================
                // Section 3: Pocket & Hands-Free Accessibility
                // =============================================================
                Text(
                    text = "🎧 POCKET & ACCESSIBILITY",
                    color = LogosGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle: Screenless / Pocket Mode
                OptionToggleRow(
                    title = "🎧 Screenless Pocket Mode",
                    subtitle = "Hands-free & eyes-free play: spoken combat narration and autoplays non-branching dialogue after a 1.5s delay.",
                    voiceHint = "Voice command: \"Pocket mode\" or \"Eyes free\"",
                    checked = isEyesFreeMode,
                    activeColor = Color(0xFF64B5F6),
                    onCheckedChange = { onToggleEyesFreeMode() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle: Hands-Free Auto Listen
                OptionToggleRow(
                    title = "👂 Hands-Free Auto-Listen",
                    subtitle = "Automatically opens microphone after narrator finishes speaking. Zero touch needed.",
                    voiceHint = "Voice command: \"Auto listen\" or \"Hands free\"",
                    checked = isAutoListen,
                    activeColor = Color(0xFF81C784),
                    onCheckedChange = { onToggleAutoListen() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle: Chime Mute
                OptionToggleRow(
                    title = "🔔 Microphone Chime Mute",
                    subtitle = "Silences system mic beeps for stealthy, uninterrupted chanting.",
                    voiceHint = "Default: Muted",
                    checked = isChimeMuted,
                    activeColor = LogosGold,
                    onCheckedChange = { onToggleChimeMute() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // =============================================================
                // Section 3: Voice Command Cheat Sheet
                // =============================================================
                Text(
                    text = "VOICE COMMAND CHEAT SHEET",
                    color = LogosGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RetroPanel.copy(alpha = 0.9f))
                        .border(1.dp, RetroBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        VoiceCommandItem(command = "📖 \"Narration\"", desc = "Toggles dialogue reading on/off")
                        VoiceCommandItem(command = "🔢 \"Read Choices\"", desc = "Toggles reading choices on/off")
                        VoiceCommandItem(command = "⏭️ \"Next\" / \"Continue\"", desc = "Advances story dialogue")
                        VoiceCommandItem(command = "📢 \"Status\" / \"Report\"", desc = "Announces HP of party & living enemies")
                        VoiceCommandItem(command = "👁️ \"Enemies\" / \"Targets\"", desc = "Scans foes and states targeted enemy")
                        VoiceCommandItem(command = "🛡️ \"Party\" / \"Allies\"", desc = "Checks health & state of fellowship")
                        VoiceCommandItem(command = "🎧 \"Pocket Mode\"", desc = "Toggles audio-guided combat")
                        VoiceCommandItem(command = "👂 \"Auto Listen\"", desc = "Toggles hands-free turn mic on/off")
                        VoiceCommandItem(command = "🎭 \"Assign Voices\"", desc = "Opens companion voice customization screen")
                        VoiceCommandItem(command = "🗣️ \"Speaker Names\"", desc = "Toggles \"Sir Cedric says...\" announcements")
                        VoiceCommandItem(command = "🎵 \"Music\" / \"Toggle Music\"", desc = "Toggles ambient background music on/off")
                        VoiceCommandItem(command = "⚙️ \"Options\" / \"Close\"", desc = "Opens or closes this settings screen")
                        VoiceCommandItem(command = "❓ \"Help\"", desc = "Spoken audio overview of voice commands")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close / Resume Button
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogosGold,
                        contentColor = RetroBlack
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "RESUME GAME",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionToggleRow(
    title: String,
    subtitle: String,
    voiceHint: String,
    checked: Boolean,
    activeColor: Color,
    onCheckedChange: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(RetroPanel.copy(alpha = 0.8f))
            .border(
                1.dp,
                if (checked) activeColor.copy(alpha = 0.8f) else RetroBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onCheckedChange() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (checked) activeColor else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = voiceHint,
                    color = Color(0xFF80D8FF),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = activeColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF1E1E28)
                )
            )
        }
    }
}

@Composable
private fun VoiceCommandItem(
    command: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = command,
            color = LogosGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = desc,
            color = Color.LightGray,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
