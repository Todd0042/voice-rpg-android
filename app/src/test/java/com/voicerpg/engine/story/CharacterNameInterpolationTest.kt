package com.voicerpg.engine.story

import androidx.compose.ui.graphics.Color
import com.voicerpg.engine.audio.CombatNarrator
import com.voicerpg.engine.audio.SpeechManager
import com.voicerpg.engine.content.GameContent
import com.voicerpg.engine.model.DialogueChoice
import com.voicerpg.engine.model.DialogueNode
import com.voicerpg.engine.model.DialogueSpeaker
import com.voicerpg.engine.model.PlayerCustomization
import com.voicerpg.engine.model.SpeakerSide
import com.voicerpg.engine.viewmodel.StoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterNameInterpolationTest {

    private fun createViewModel(): StoryViewModel {
        val speechManager = SpeechManager()
        val combatNarrator = CombatNarrator()
        val testScope = CoroutineScope(Dispatchers.Default)
        return StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            scopeOverride = testScope
        )
    }

    @Test
    fun testDefaultHeroNameInterpolation() {
        val vm = createViewModel()
        val formatted = vm.formatStoryText("Listen closely, {hero}.", "Hero", "The Initiate")
        assertEquals("Listen closely, Hero.", formatted)
    }

    @Test
    fun testCustomHeroNameInterpolation() {
        val vm = createViewModel()
        val formatted = vm.formatStoryText("Listen closely, {hero}. {hero}'s resolve is unbroken.", "Rowan", "Flame-Woven")
        assertEquals("Listen closely, Rowan. Rowan's resolve is unbroken.", formatted)
    }

    @Test
    fun testLegacyNameReplacementWhenCustomized() {
        val vm = createViewModel()
        // Legacy story text hardcodes the content's default hero name; when the player
        // customizes their name, those literal references are rewritten. Derive the
        // default from the manifest so the check is content-agnostic.
        val defaultName = GameContent.manifest.defaultHeroName.ifBlank { "Hero" }
        val formatted = vm.formatStoryText(
            "$defaultName, stand firm. $defaultName's courage guides us.",
            "Rowan",
            "Flame-Woven"
        )
        assertEquals("Rowan, stand firm. Rowan's courage guides us.", formatted)
    }

    @Test
    fun testFormatNodeForPlayerHeroSpeaker() {
        val vm = createViewModel()
        val heroSpeaker = DialogueSpeaker(
            id = "hero",
            name = "Hero",
            title = "The Initiate",
            portraitAsset = null,
            themeColor = Color.White
        )
        val node = DialogueNode(
            id = "test_node",
            speaker = heroSpeaker,
            side = SpeakerSide.LEFT,
            text = "The voice resonates through me, {hero}.",
            choices = listOf(
                DialogueChoice(
                    id = "c1",
                    text = "Follow {hero}",
                    voiceKeywords = listOf("follow"),
                    nextNodeId = "next_node"
                )
            )
        )
        val player = PlayerCustomization(name = "Kaelen", title = "Star-Caller")
        val formatted = vm.formatNodeForPlayer(node, player)

        assertEquals("Kaelen", formatted.speaker.name)
        assertEquals("Star-Caller", formatted.speaker.title)
        assertEquals("The voice resonates through me, Kaelen.", formatted.text)
        assertEquals("Follow Kaelen", formatted.choices.first().text)
    }
}
