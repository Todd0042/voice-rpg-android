package com.voicerpg.android.ui.creation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicerpg.android.engine.ClassSpellLibrary
import com.voicerpg.android.model.AuraColor
import com.voicerpg.android.model.HeroClass
import com.voicerpg.android.model.PlayerCustomization
import com.voicerpg.android.model.SpellSchool
import com.voicerpg.android.ui.story.StoryAssetLoader
import com.voicerpg.android.ui.theme.LogosGold
import com.voicerpg.android.ui.theme.LogosGlow
import com.voicerpg.android.ui.theme.RetroBlack
import com.voicerpg.android.ui.theme.RetroBorder
import com.voicerpg.android.ui.theme.RetroBorderGold
import com.voicerpg.android.ui.theme.RetroPanel

@Composable
fun CharacterCreationScreen(
    initialCustomization: PlayerCustomization = PlayerCustomization(),
    onConfirmCharacter: (PlayerCustomization) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(initialCustomization.name) }
    var selectedClass by remember { mutableStateOf(initialCustomization.heroClass) }
    var selectedTitle by remember { mutableStateOf(initialCustomization.title) }
    var selectedAura by remember { mutableStateOf(initialCustomization.auraColor) }
    var selectedAffinity by remember { mutableStateOf(initialCustomization.voiceAffinityBonusSchool) }

    val context = LocalContext.current
    val aethelBitmap = remember {
        StoryAssetLoader.loadBitmap(context, "portraits/aethel.jpg")
    }

    val titles = listOf(
        "The Awakened Invocator",
        "The Flame-Woven",
        "The Silent Breaker",
        "The Star-Caller",
        "The Oath-Bound"
    )

    val quickNames = listOf("Aethel", "Rowan", "Kaelen", "Elira", "Vaelen")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RetroBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "⚔️ FORGE YOUR INVOCATOR ⚔️",
                color = LogosGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Character Creation & Initial Loadout",
                color = Color.LightGray,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Character Preview Card with chosen Aura & Name
            val auraColorVal = Color(android.graphics.Color.parseColor(selectedAura.hexColor))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RetroPanel.copy(alpha = 0.9f))
                    .border(2.dp, auraColorVal, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Portrait Bust
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(8.dp, RoundedCornerShape(10.dp), spotColor = auraColorVal)
                            .clip(RoundedCornerShape(10.dp))
                            .border(2.dp, auraColorVal, RoundedCornerShape(10.dp))
                    ) {
                        if (aethelBitmap != null) {
                            Image(
                                bitmap = aethelBitmap,
                                contentDescription = name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(auraColorVal.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.firstOrNull()?.toString() ?: "A",
                                    color = auraColorVal,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (name.isBlank()) "Nameless" else name,
                            color = LogosGlow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = selectedTitle,
                            color = auraColorVal,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Class: ${selectedClass.title} • Affinity: ${selectedAffinity.name}",
                            color = Color.LightGray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Name Input
            Text(
                text = "1. INVOCATOR'S TRUE NAME",
                color = LogosGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Character Name", fontFamily = FontFamily.Monospace) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LogosGold,
                    unfocusedBorderColor = RetroBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = RetroPanel,
                    unfocusedContainerColor = RetroPanel
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Name Suggestions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickNames.forEach { qn ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (name == qn) LogosGold.copy(alpha = 0.2f) else RetroPanel)
                            .border(1.dp, if (name == qn) LogosGold else RetroBorder, RoundedCornerShape(6.dp))
                            .clickable { name = qn }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = qn,
                            color = if (name == qn) LogosGold else Color.LightGray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Class Selection
            Text(
                text = "2. CHOOSE YOUR DISCIPLINE (CLASS)",
                color = LogosGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroClass.entries.forEach { hc ->
                    ClassOptionCard(
                        heroClass = hc,
                        isSelected = hc == selectedClass,
                        onSelect = { selectedClass = hc }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Aesthetics (Aura Color & Title)
            Text(
                text = "3. AURA LUMINESCENCE (AESTHETICS)",
                color = LogosGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AuraColor.entries.forEach { aura ->
                    val isSelected = aura == selectedAura
                    val col = Color(android.graphics.Color.parseColor(aura.hexColor))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedAura = aura }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(if (isSelected) 10.dp else 2.dp, CircleShape, spotColor = col)
                                .clip(CircleShape)
                                .background(col)
                                .border(if (isSelected) 3.dp else 1.dp, if (isSelected) Color.White else RetroBorder, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = aura.displayName.split(" ").first(),
                            color = if (isSelected) col else Color.Gray,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Epithet Title Selector
            Text(
                text = "4. HONORIFIC TITLE",
                color = LogosGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                titles.forEach { t ->
                    val isSelected = t == selectedTitle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) LogosGold.copy(alpha = 0.2f) else RetroPanel)
                            .border(1.dp, if (isSelected) LogosGold else RetroBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedTitle = t }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = t,
                            color = if (isSelected) LogosGold else Color.LightGray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm & Enter Adventure Button
            Button(
                onClick = {
                    val finalCustomization = PlayerCustomization(
                        name = if (name.isBlank()) "Aethel" else name.trim(),
                        heroClass = selectedClass,
                        title = selectedTitle,
                        auraColor = selectedAura,
                        voiceAffinityBonusSchool = selectedClass.preferredSchool
                    )
                    onConfirmCharacter(finalCustomization)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogosGold,
                    contentColor = RetroBlack
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "AWAKEN IN AETHELGARD ➔",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ClassOptionCard(
    heroClass: HeroClass,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val spells = remember(heroClass) { ClassSpellLibrary.getSpellsForClass(heroClass) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) RetroPanel else Color(0xFF141420))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) LogosGold else RetroBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = heroClass.title.uppercase(),
                        color = if (isSelected) LogosGold else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = heroClass.subtitle,
                        color = if (isSelected) LogosGlow else Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Stats Pills
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatPill("HP", heroClass.startingHp.toString(), Color(0xFF4CAF50))
                    StatPill("MP", heroClass.startingMp.toString(), Color(0xFF00B0FF))
                    StatPill("SPD", heroClass.startingSpeed.toString(), Color(0xFFFFB74D))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = heroClass.description,
                color = Color.LightGray,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Starter Spells
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                spells.forEach { sp ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF222234))
                            .border(1.dp, Color(0xFF42425E), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⚡ ${sp.name}",
                            color = Color(0xFF80D8FF),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.18f))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$label:$value",
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
