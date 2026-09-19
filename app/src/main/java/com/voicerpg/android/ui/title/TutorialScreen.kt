package com.voicerpg.android.ui.title

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicerpg.android.ui.theme.FrostCyan
import com.voicerpg.android.ui.theme.HolyYellow
import com.voicerpg.android.ui.theme.LogosGold
import com.voicerpg.android.ui.theme.RetroBlack
import com.voicerpg.android.ui.theme.RetroBorder
import com.voicerpg.android.ui.theme.RetroBorderGold
import com.voicerpg.android.ui.theme.RetroDeepSlate
import com.voicerpg.android.ui.theme.RetroPanel

@Composable
fun TutorialScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RetroBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = RetroDeepSlate),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, RetroBorder, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = LogosGold,
                        modifier = Modifier.padding(2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "CHRONICLE GUIDE",
                    color = LogosGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, LogosGold, Color.Transparent)
                        )
                    )
            )

            Spacer(modifier = Modifier.height(20.dp))

            TutorialSection(
                icon = Icons.AutoMirrored.Default.MenuBook,
                iconTint = LogosGold,
                title = "THE WORLD OF ECHOES",
                borderColor = RetroBorderGold
            ) {
                TutorialParagraph(
                    "VoiceRPG: Echoes of the Logos is a voice-first high fantasy chronicle. " +
                        "You command your fellowship of heroes not by tapping buttons, but by speaking " +
                        "incantations aloud. The more vivid, elaborate, and dramatic your spoken words, " +
                        "the more powerful your spells become."
                )
                TutorialParagraph(
                    "The game is designed to be fully playable without ever looking at the screen. " +
                        "All dialogue is narrated aloud, choices can be read to you, and every action " +
                        "can be triggered by voice alone. Say 'Pocket Mode' at any time to enable " +
                        "eyes-free play."
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TutorialSection(
                icon = Icons.Default.Mic,
                iconTint = FrostCyan,
                title = "HOW COMBAT WORKS",
                borderColor = FrostCyan
            ) {
                TutorialParagraph(
                    "Combat uses an Active Time Battle system. Each combatant has a speed gauge " +
                        "that fills over time. When your hero's turn arrives, you speak a voice " +
                        "command to cast a spell."
                )
                TutorialParagraph(
                    "A typical command has two parts: the spell name and the target. " +
                        "For example, saying 'Fireball at the Shadow Wisp' will cast Fireball on " +
                        "that enemy. You can also say just the spell name and the system will pick " +
                        "a target for you."
                )
                TutorialParagraph(
                    "Each hero also has a free Breath ability that restores mana. " +
                        "Say 'Attune' for Aethel, 'Steady Breath' for Sir Cedric, 'Deep Root' for " +
                        "Lyra, or 'Quiet Lungs' for Zephyr when running low on mana."
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TutorialSection(
                icon = Icons.Default.AutoAwesome,
                iconTint = Color(0xFFE040FB),
                title = "THE RESONANCE SYSTEM",
                borderColor = Color(0xFFE040FB)
            ) {
                TutorialParagraph(
                    "This is the heart of VoiceRPG. Your spells are not fixed in power. " +
                        "The Resonance Engine scores your spoken incantation across five dimensions, " +
                        "and the better you score, the more damage your spell deals -- up to triple " +
                        "normal power."
                )
                TutorialSubheading("Thematic Vocabulary")
                TutorialParagraph(
                    "Use words that match your spell's elemental school. A fire spell benefits from " +
                        "words like 'blaze', 'inferno', 'ember', 'scorch'. A holy spell resonates with " +
                        "'radiant', 'divine', 'consecrate'. The Spell Thesaurus rewards creative, " +
                        "thematic language."
                )
                TutorialSubheading("Lexical Complexity")
                TutorialParagraph(
                    "Longer, more elaborate chants score higher. A simple 'Fireball' will work, but " +
                        "'By the searing wrath of the ancient flame, reduce this heretic to cinder' " +
                        "will unleash devastating power. Syllable count and sentence variety matter."
                )
                TutorialSubheading("Vocal Projection and Inflection")
                TutorialParagraph(
                    "Speak loudly and with conviction. A bold, projected voice scores higher than a " +
                        "mumble. Vary your pitch and tone -- a dramatic crescendo or rising intensity " +
                        "earns bonus resonance."
                )
                TutorialSubheading("Novelty")
                TutorialParagraph(
                    "Repeating the exact same incantation over and over will diminish your resonance. " +
                        "The system rewards fresh, creative phrasing each turn. Mix up your words and " +
                        "style."
                )
                TutorialHighlight(
                    "The key takeaway: if you go all out with dramatic, thematic, elaborate " +
                        "incantations, your spells will deal significantly more damage. A whispered " +
                        "'fireball' is a candle flame. A thundered invocation of ancient fire is a " +
                        "cataclysm."
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TutorialSection(
                icon = Icons.Default.People,
                iconTint = HolyYellow,
                title = "YOUR FELLOWSHIP",
                borderColor = HolyYellow
            ) {
                TutorialCharacterCard(
                    name = "Aethel",
                    title = "Elemental Invocator (Your Hero)",
                    color = FrostCyan,
                    description = "Your player character and primary spellcaster. Aethel wields " +
                        "the elemental forces of fire, ice, and lightning.",
                    spells = listOf(
                        "Fireball -- Pyromancy. A searing bolt of flame that can inflict Burn.",
                        "Frost Spike -- Cryomancy. A lance of ice that chills enemies, slowing them.",
                        "Chain Lightning -- Electromancy. Arcing bolts that strike all foes.",
                        "Attune -- A free breath discipline. Restores mana each turn."
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                TutorialCharacterCard(
                    name = "Sir Cedric",
                    title = "Oathkeeper Templar",
                    color = HolyYellow,
                    description = "A stalwart knight who joins your fellowship early on. Cedric is " +
                        "your shield and healer, protecting the party while smiting the unholy.",
                    spells = listOf(
                        "Holy Smite -- A radiant strike devastating against undead foes.",
                        "Lay on Hands -- A powerful holy heal that restores the entire party.",
                        "Shield Wall -- Raises a barrier that guards all allies from harm.",
                        "Steady Breath -- A free breath discipline. Restores mana each turn."
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                TutorialCharacterCard(
                    name = "Lyra",
                    title = "Grove Warden",
                    color = Color(0xFFA5D6A7),
                    description = "A nature mystic who joins your fellowship in Act II. Lyra " +
                        "commands the forces of the living grove, healing and entangling foes.",
                    spells = listOf(
                        "Soothing Rain -- A gentle downpour that heals all allies.",
                        "Briar Entangle -- Thorned vines that root enemies in place.",
                        "Deep Root -- A free breath discipline. Draws mana from the earth."
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                TutorialCharacterCard(
                    name = "Zephyr",
                    title = "Shadowblade",
                    color = Color(0xFFCE93D8),
                    description = "A swift and deadly assassin who may join under unexpected " +
                        "circumstances. Zephyr strikes from the shadows with lethal precision.",
                    spells = listOf(
                        "Shadow Strike -- A swift blade from the void. High single-target damage.",
                        "Venom Flurry -- A flurry of poisoned strikes hitting all enemies.",
                        "Quiet Lungs -- A free breath discipline. Restores mana in silence."
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TutorialSection(
                icon = Icons.Default.Shield,
                iconTint = Color(0xFFFF5722),
                title = "SPELL AFFINITIES",
                borderColor = Color(0xFFFF5722)
            ) {
                TutorialParagraph(
                    "Every spell belongs to an elemental school, and every enemy belongs to a " +
                        "creature family. Matching the right school against the right enemy type deals " +
                        "bonus damage -- up to double. Mismatched spells still work, but deal reduced " +
                        "damage."
                )
                TutorialParagraph(
                    "Some key affinities to remember: Fire is devastating against plant creatures. " +
                        "Holy magic obliterates the undead. Lightning overloads mechanical constructs. " +
                        "Shadow arts corrode the living. Experiment and discover what works."
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TutorialSection(
                icon = Icons.Default.GraphicEq,
                iconTint = Color(0xFF80DEEA),
                title = "VOICE COMMANDS",
                borderColor = Color(0xFF80DEEA)
            ) {
                TutorialParagraph(
                    "Beyond casting spells, you can use these voice commands at any time:"
                )
                TutorialCommandRow("Status", "Hear a full situation report of party and enemies")
                TutorialCommandRow("Enemies", "Scan and hear details about current foes")
                TutorialCommandRow("Help", "Hear a quick summary of available commands")
                TutorialCommandRow("Pocket Mode", "Toggle eyes-free screenless play")
                TutorialCommandRow("Auto Listen", "Toggle hands-free microphone re-arming")
                TutorialCommandRow("Options", "Open settings and accessibility menu")
                TutorialCommandRow("Read Choices", "Toggle whether choices are read aloud")
                TutorialCommandRow("Narration", "Toggle story dialogue narration on or off")
                TutorialCommandRow("Recap", "Hear a summary of your quest progress")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TutorialSection(
                icon = Icons.AutoMirrored.Default.MenuBook,
                iconTint = LogosGold,
                title = "STORY AND EXPLORATION",
                borderColor = RetroBorderGold
            ) {
                TutorialParagraph(
                    "Between battles, you explore a rich narrative world. The Narrator reads the " +
                        "story aloud, and when choices appear, you can either tap them on screen or " +
                        "speak a keyword from the choice. Each choice has a voice keyword hint shown " +
                        "beneath it."
                )
                TutorialParagraph(
                    "Some story hubs offer multiple objectives. Once you complete a sub-story, it " +
                        "is marked as done and removed from the hub. When all objectives are finished, " +
                        "the story automatically advances to the next chapter."
                )
                TutorialParagraph(
                    "Say 'Next', 'Continue', or 'Proceed' to advance dialogue. Say 'Skip' to " +
                        "fast-forward through text. Say 'Recap' at any time to hear where you are " +
                        "in the story."
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .border(2.dp, RetroBorderGold, RoundedCornerShape(10.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RetroPanel.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "RETURN TO TITLE",
                    color = LogosGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TutorialSection(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    borderColor: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .background(RetroDeepSlate.copy(alpha = 0.92f))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = title,
                color = iconTint,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(borderColor.copy(alpha = 0.4f))
        )

        Spacer(modifier = Modifier.height(10.dp))

        content()
    }
}

@Composable
private fun TutorialParagraph(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.9f),
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        lineHeight = 18.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun TutorialSubheading(text: String) {
    Text(
        text = text,
        color = Color(0xFFE040FB).copy(alpha = 0.9f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
    )
}

@Composable
private fun TutorialHighlight(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE040FB).copy(alpha = 0.12f))
            .border(1.dp, Color(0xFFE040FB).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFFFFF59D),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun TutorialCharacterCard(
    name: String,
    title: String,
    color: Color,
    description: String,
    spells: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .background(RetroPanel.copy(alpha = 0.6f))
            .padding(12.dp)
    ) {
        Text(
            text = name,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = title,
            color = color.copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        spells.forEach { spell ->
            Text(
                text = spell,
                color = Color.LightGray.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }
    }
}

@Composable
private fun TutorialCommandRow(command: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = command,
            color = FrostCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = description,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp
        )
    }
}
