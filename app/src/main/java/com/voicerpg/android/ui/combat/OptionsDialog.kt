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
    isAutoListen: Boolean,
    isChimeMuted: Boolean,
    onToggleEyesFreeMode: () -> Unit,
    onToggleAutoListen: () -> Unit,
    onToggleChimeMute: () -> Unit,
    onClose: () -> Unit
) {
    if (!isOpen) return

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(RetroBlack.copy(alpha = 0.95f))
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
                            text = "⚙️ TACTICAL OPTIONS",
                            color = LogosGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Voice-Controlled • Pocket Accessibility",
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

                // Section 1: Accessibility & Audio Toggles
                Text(
                    text = "ACCESSIBILITY & SPEECH",
                    color = LogosGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle 1: Screenless / Pocket Mode
                OptionToggleRow(
                    title = "🎧 Screenless Pocket Mode",
                    subtitle = "Spoken combat narration for turns, hits, and enemy actions. Ideal for phone in pocket or visually impaired play.",
                    voiceHint = "Voice command: \"Pocket mode\" or \"Eyes free\"",
                    checked = isEyesFreeMode,
                    activeColor = Color(0xFF64B5F6),
                    onCheckedChange = { onToggleEyesFreeMode() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle 2: Hands-Free Auto Listen
                OptionToggleRow(
                    title = "👂 Hands-Free Auto-Listen",
                    subtitle = "Automatically opens microphone on player turn after narrator speaks. No screen touch needed.",
                    voiceHint = "Voice command: \"Auto listen\" or \"Hands free\"",
                    checked = isAutoListen,
                    activeColor = Color(0xFF81C784),
                    onCheckedChange = { onToggleAutoListen() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle 3: Chime Mute
                OptionToggleRow(
                    title = "🔔 Microphone Chime Mute",
                    subtitle = "Silences system mic start/stop beeps for stealthy, uninterrupted chanting.",
                    voiceHint = "Default: Muted",
                    checked = isChimeMuted,
                    activeColor = LogosGold,
                    onCheckedChange = { onToggleChimeMute() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Voice Command Reference Card
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
                        VoiceCommandItem(command = "📢 \"Status\" / \"Report\"", desc = "Announces HP of party & living enemies")
                        VoiceCommandItem(command = "👁️ \"Enemies\" / \"Targets\"", desc = "Scans foes and states current targeted enemy")
                        VoiceCommandItem(command = "🛡️ \"Party\" / \"Allies\"", desc = "Checks health & state of all fellowship heroes")
                        VoiceCommandItem(command = "🎧 \"Pocket Mode\"", desc = "Toggles audio narration on/off")
                        VoiceCommandItem(command = "👂 \"Auto Listen\"", desc = "Toggles hands-free turn mic on/off")
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
                        text = "RESUME BATTLE",
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
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 13.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = voiceHint,
                    color = activeColor.copy(alpha = 0.9f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = RetroBlack,
                    checkedTrackColor = activeColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = RetroPanel
                )
            )
        }
    }
}

@Composable
private fun VoiceCommandItem(command: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
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
