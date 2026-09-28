package com.voicerpg.engine.ui.setup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.voicerpg.engine.localization.TranslationManager
import com.voicerpg.engine.model.LanguageCatalog
import com.voicerpg.engine.ui.theme.FrostCyan
import com.voicerpg.engine.ui.theme.HolyYellow
import com.voicerpg.engine.ui.theme.LogosGold
import com.voicerpg.engine.ui.theme.LogosGlow
import com.voicerpg.engine.ui.theme.RetroBlack
import com.voicerpg.engine.ui.theme.RetroBorder
import com.voicerpg.engine.ui.theme.RetroBorderGold
import com.voicerpg.engine.ui.theme.RetroPanel

/**
 * Dedicated first-boot Language and Regional Accent Selection Screen.
 * Renders BEFORE the Title Screen is ever shown, ensuring the player never sees English
 * menus if they prefer Spanish, German, French, Portuguese, or Italian.
 */
@Composable
fun LanguageSelectionScreen(
    initialLanguage: String = "en",
    initialRegion: String = "",
    onConfirmSelection: (language: String, region: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLang by remember { mutableStateOf(initialLanguage.lowercase().take(2).ifBlank { "en" }) }
    var selectedReg by remember { mutableStateOf(initialRegion.uppercase()) }

    val currentRegions = LanguageCatalog.getRegionsForLanguage(selectedLang)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RetroBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RetroBlack,
                            Color(0xFF0F1420),
                            RetroBlack
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Emblem / Icon
                Text(
                    text = "🌐",
                    fontSize = 36.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Title - dynamically rendered in the selected language
                Text(
                    text = TranslationManager.translate("SELECT YOUR LANGUAGE", selectedLang),
                    color = LogosGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Choose your spoken language and regional accent",
                    color = Color.LightGray.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // 1. Language Cards Grid
                val languages = LanguageCatalog.SUPPORTED_LANGUAGES
                languages.chunked(2).forEach { rowLanguages ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowLanguages.forEach { langOpt ->
                            val isSelected = selectedLang.equals(langOpt.code, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) LogosGold else Color(0xFF1B2230))
                                    .border(
                                        2.dp,
                                        if (isSelected) LogosGlow else RetroBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        selectedLang = langOpt.code
                                        val newRegions = LanguageCatalog.getRegionsForLanguage(langOpt.code)
                                        if (newRegions.none { it.code == selectedReg }) {
                                            selectedReg = ""
                                        }
                                        TranslationManager.setLanguageAndRegion(langOpt.code, selectedReg)
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = langOpt.nativeName,
                                        color = if (isSelected) RetroBlack else Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "[${langOpt.code.uppercase()}]",
                                        color = if (isSelected) RetroBlack.copy(alpha = 0.8f) else Color.Gray,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2. Regional Accent Selection
                Text(
                    text = TranslationManager.translate("SELECT YOUR REGIONAL ACCENT", selectedLang),
                    color = HolyYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                val chunkedRegions = currentRegions.chunked(2)
                chunkedRegions.forEach { rowRegions ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowRegions.forEach { regOpt ->
                            val isSelected = selectedReg.equals(regOpt.code, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) FrostCyan else RetroPanel)
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.White else RetroBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedReg = regOpt.code
                                        TranslationManager.setLanguageAndRegion(selectedLang, regOpt.code)
                                    }
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = regOpt.displayName,
                                    color = if (isSelected) RetroBlack else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        if (rowRegions.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Confirm & Enter Realm Button
                Button(
                    onClick = {
                        TranslationManager.setLanguageAndRegion(selectedLang, selectedReg)
                        onConfirmSelection(selectedLang, selectedReg)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(2.dp, RetroBorderGold, RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = LogosGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = TranslationManager.translate("ENTER THE REALM ▶", selectedLang),
                        color = RetroBlack,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
