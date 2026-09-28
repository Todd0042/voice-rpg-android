package com.voicerpg.engine

import com.voicerpg.engine.content.GameContent
import com.voicerpg.engine.engine.StoryRecapEngine
import com.voicerpg.engine.model.SavedCharacterStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoryRecapEngineTest {

    private fun scene(id: String) = GameContent.scenes.getValue(id)
    private fun node(id: String) = GameContent.nodes.getValue(id)

    private val heroParty = listOf(
        SavedCharacterStats(
            id = "hero",
            name = "Aethel",
            loreClass = "Elementalist",
            currentHp = 240,
            maxHp = 240,
            currentMp = 140,
            maxMp = 140,
            speed = 70
        )
    )

    @Test
    fun testCurrentSceneRecapSpansAllScenesInChapterOrder() {
        val recap = StoryRecapEngine.buildRecap(scene("scene_crossroads"), node("crossroads_intro"), emptyMap(), heroParty)

        assertEquals(2, recap.milestones.size)
        assertEquals(0, recap.milestones[0].chapterIndex)
        assertEquals(1, recap.milestones[1].chapterIndex)
        assertFalse(recap.milestones[0].isCurrent)
        assertTrue(recap.milestones[1].isCurrent)
        assertEquals("Aethel's Cottage", recap.milestones[0].location)
        assertEquals("The Sun Shrine Crossroads", recap.milestones[1].location)
    }

    @Test
    fun testFirstSceneRecapMarksItsOnlyMilestoneCurrent() {
        val recap = StoryRecapEngine.buildRecap(scene("scene_cottage"), node("cottage_intro"), emptyMap(), heroParty)

        assertEquals("Act I: The Ashen Awakening", recap.currentActTitle)
        assertEquals("Prologue: The Morning Without Echo", recap.currentChapterTitle)
        assertEquals("Aethel's Cottage", recap.currentSceneName)
        assertEquals(1, recap.milestones.size)
        assertTrue(recap.milestones[0].isCurrent)
        assertEquals("Prologue: The Morning Without Echo", recap.activeObjective)
    }

    @Test
    fun testFellowshipRosterAndSpeakersContainHeroAndNarrator() {
        assertEquals(listOf("Aethel (Elementalist)"), StoryRecapEngine.getPartyRoster(heroParty))
        assertEquals(listOf("Aethel (Elementalist)"), StoryRecapEngine.getPartyRoster(emptyList()))

        assertTrue(GameContent.allSpeakers.any { it.id == "narrator" })
        assertTrue(GameContent.allSpeakers.any { it.id == "hero" })

        val recap = StoryRecapEngine.buildRecap(scene("scene_crossroads"), node("crossroads_intro"), emptyMap(), heroParty)
        assertEquals(listOf("Aethel (Elementalist)"), recap.partyRoster)
        assertEquals(recap.partyRoster, recap.fellowshipRoster)
        assertFalse(recap.partyRoster.any { it.contains("Narrator") })
    }

    @Test
    fun testSpokenRecapForCurrentSceneContainsChapterTitleAndObjective() {
        val recap = StoryRecapEngine.buildRecap(scene("scene_crossroads"), node("crossroads_intro"), emptyMap(), heroParty)

        assertTrue(recap.spokenRecap.contains("Chronicle recap."))
        assertTrue(recap.spokenRecap.contains("Chapter 1: The Oathkeeper of Dawn"))
        assertTrue(recap.spokenRecap.contains("at The Sun Shrine Crossroads"))
        assertTrue(
            recap.spokenRecap.contains(
                "Current objective: Chapter 1: The Oathkeeper of Dawn."
            )
        )
        assertTrue(recap.spokenRecap.contains("What is your command?"))
        assertTrue(recap.activeObjective.contains("The Oathkeeper of Dawn"))
    }

    @Test
    fun testSpokenRecapCountsPreviousMilestoneAsPreviouslyOnYourQuest() {
        val crossroadsRecap = StoryRecapEngine.buildRecap(scene("scene_crossroads"), node("crossroads_intro"), emptyMap(), heroParty)
        assertTrue(crossroadsRecap.spokenRecap.contains("Previously on your quest:"))
        assertTrue(crossroadsRecap.spokenRecap.contains("Sunlight streams through the window into a silent room."))

        val cottageRecap = StoryRecapEngine.buildRecap(scene("scene_cottage"), node("cottage_intro"), emptyMap(), heroParty)
        assertTrue(cottageRecap.spokenRecap.contains("Your quest has just begun."))
        assertFalse(cottageRecap.spokenRecap.contains("Previously on your quest:"))
    }
}