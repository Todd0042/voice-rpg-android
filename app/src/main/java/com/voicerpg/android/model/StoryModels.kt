package com.voicerpg.android.model

import androidx.compose.ui.graphics.Color
import com.voicerpg.android.ui.theme.FrostCyan
import com.voicerpg.android.ui.theme.HolyYellow
import com.voicerpg.android.ui.theme.LogosGold
import com.voicerpg.android.ui.theme.ShadowPurple

enum class SpeakerSide {
    LEFT,
    RIGHT,
    CENTER_NARRATOR
}

data class DialogueSpeaker(
    val id: String,
    val name: String,
    val title: String,
    val portraitAsset: String?,
    val themeColor: Color,
    val ttsPitch: Float = 1.0f
) {
    companion object {
        val AETHEL = DialogueSpeaker(
            id = "aethel",
            name = "Aethel",
            title = "Elemental Invocator",
            portraitAsset = "portraits/aethel.jpg",
            themeColor = FrostCyan,
            ttsPitch = 1.15f
        )

        val CEDRIC = DialogueSpeaker(
            id = "cedric",
            name = "Sir Cedric",
            title = "Oathkeeper Templar",
            portraitAsset = "portraits/cedric.jpg",
            themeColor = HolyYellow,
            ttsPitch = 0.85f
        )

        val NARRATOR = DialogueSpeaker(
            id = "narrator",
            name = "Narrator",
            title = "Chronicle",
            portraitAsset = null,
            themeColor = LogosGold,
            ttsPitch = 1.0f
        )

        val SHADOW_WISP = DialogueSpeaker(
            id = "shadow_wisp",
            name = "Corrupted Wisp",
            title = "Blighted Husk",
            portraitAsset = null,
            themeColor = ShadowPurple,
            ttsPitch = 0.70f
        )

        val LYRA = DialogueSpeaker(
            id = "lyra",
            name = "Lyra",
            title = "Grove Warden",
            portraitAsset = "portraits/lyra.jpg",
            themeColor = Color(0xFFA5D6A7),
            ttsPitch = 1.20f
        )

        val ZEPHYR = DialogueSpeaker(
            id = "zephyr",
            name = "Zephyr",
            title = "Shadowblade",
            portraitAsset = "portraits/zephyr.jpg",
            themeColor = Color(0xFFCE93D8),
            ttsPitch = 0.95f
        )

        val MALAKOR = DialogueSpeaker(
            id = "malakor",
            name = "Grand Inquisitor Malakor",
            title = "The Mute Sovereign",
            portraitAsset = "portraits/malakor.jpg",
            themeColor = Color(0xFFEF5350),
            ttsPitch = 0.60f
        )

        val VAELOR = DialogueSpeaker(
            id = "vaelor",
            name = "Commander Vaelor",
            title = "The Voice-Thief",
            portraitAsset = null,
            themeColor = Color(0xFFB0BEC5),
            ttsPitch = 0.75f
        )

        val GALAHAULT = DialogueSpeaker(
            id = "galahault",
            name = "Sir Galahault",
            title = "The Obsidian Grandmaster",
            portraitAsset = null,
            themeColor = HolyYellow,
            ttsPitch = 0.78f
        )

        val NOCTURNE = DialogueSpeaker(
            id = "nocturne",
            name = "Master Nocturne",
            title = "Spymaster of the Mute",
            portraitAsset = null,
            themeColor = ShadowPurple,
            ttsPitch = 0.70f
        )

        val OUROS = DialogueSpeaker(
            id = "ouros",
            name = "Clockwork Warmaster Ouros",
            title = "Iron Bastion Sovereign",
            portraitAsset = null,
            themeColor = Color(0xFFFFB74D),
            ttsPitch = 0.65f
        )
    }
}

data class DialogueChoice(
    val id: String,
    val text: String,
    val voiceKeywords: List<String>,
    val nextNodeId: String,
    val completionFlag: String? = null
)

data class DialogueNode(
    val id: String,
    val speaker: DialogueSpeaker,
    val side: SpeakerSide,
    val text: String,
    val choices: List<DialogueChoice> = emptyList(),
    val nextNodeId: String? = null,
    val triggerBattleEncounterId: String? = null,
    val changeSceneId: String? = null,
    val setFlagOnEnter: String? = null
)

data class StoryScene(
    val id: String,
    val name: String,
    val chapterTitle: String,
    val backgroundAsset: String,
    val initialNodeId: String,
    val ambientDescription: String
)

enum class GameScreen {
    AUDIO_SETUP,
    CHARACTER_CREATION,
    STORY_EXPLORATION,
    COMBAT_ARENA
}
