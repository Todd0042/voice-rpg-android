package com.voicerpg.android

import com.voicerpg.android.engine.StoryRecapEngine
import com.voicerpg.android.engine.StoryScript
import com.voicerpg.android.model.SavedCharacterStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StoryRecapEngineTest {

    @Test
    fun testPrologueRecap() {
        val scene = StoryScript.SCENE_COTTAGE
        val node = StoryScript.ALL_NODES["cottage_intro"]!!
        val flags = emptyMap<String, Boolean>()
        val party = listOf(
            SavedCharacterStats(id = "hero", name = "Aethel", loreClass = "Invocator", currentHp = 240, maxHp = 240, currentMp = 160, maxMp = 160, speed = 70)
        )

        val recap = StoryRecapEngine.buildRecap(scene, node, flags, party)

        assertEquals("Act I: The Silenced Bells", recap.currentActTitle)
        assertEquals("Prologue: The Morning Without Echo", recap.currentChapterTitle)
        assertEquals("Aethel's Cottage", recap.currentSceneName)
        assertEquals(1, recap.milestones.size)
        assertTrue(recap.milestones.first().isCurrent)
        assertTrue(recap.activeObjective.contains("cottage"))
        assertTrue(recap.spokenRecap.contains("Aethel's Cottage"))
    }

    @Test
    fun testChapter4RecapHasPriorMilestonesCompleted() {
        val scene = StoryScript.SCENE_TOWER
        val node = StoryScript.ALL_NODES["ch4_tower_ascent"]!!
        val flags = mapOf("cedric_recruited" to true)
        val party = listOf(
            SavedCharacterStats(id = "hero", name = "Aethel", loreClass = "Invocator", currentHp = 240, maxHp = 240, currentMp = 160, maxMp = 160, speed = 70),
            SavedCharacterStats(id = "cedric", name = "Sir Cedric", loreClass = "Templar", currentHp = 300, maxHp = 300, currentMp = 80, maxMp = 80, speed = 55)
        )

        val recap = StoryRecapEngine.buildRecap(scene, node, flags, party)

        assertEquals("Act I: The Silenced Bells", recap.currentActTitle)
        // Milestones: Prologue (0), Ch 1 (1), Ch 2 (2), Ch 3 (3), Ch 4 (4) = 5 milestones
        assertEquals(5, recap.milestones.size)

        // Prior 4 milestones must be marked completed
        for (i in 0..3) {
            assertFalse(recap.milestones[i].isCurrent)
        }
        // Chapter 4 milestone must be marked current
        assertTrue(recap.milestones[4].isCurrent)
        assertEquals(4, recap.milestones[4].chapterIndex)

        // Fellowship should list Cedric
        assertTrue(recap.fellowshipRoster.any { it.contains("Cedric") })

        // Spoken recap should reference Solaria and current goal
        assertTrue(recap.spokenRecap.contains("Solaria"))
        assertTrue(recap.spokenRecap.contains("Current objective:"))
    }

    @Test
    fun testChapter8RecapIncludesZephyrAndLyra() {
        val scene = StoryScript.SCENE_SHADOWED_CRAGS
        val node = StoryScript.ALL_NODES["ch8_intro"]!!
        val flags = mapOf("lyra_recruited" to true, "ch7_wyrm_defeated" to true)
        val party = listOf(
            SavedCharacterStats(id = "hero", name = "Aethel", loreClass = "Invocator", currentHp = 240, maxHp = 240, currentMp = 160, maxMp = 160, speed = 70),
            SavedCharacterStats(id = "cedric", name = "Sir Cedric", loreClass = "Templar", currentHp = 300, maxHp = 300, currentMp = 80, maxMp = 80, speed = 55),
            SavedCharacterStats(id = "lyra", name = "Lyra", loreClass = "Grove Warden", currentHp = 240, maxHp = 240, currentMp = 120, maxMp = 120, speed = 65)
        )

        val recap = StoryRecapEngine.buildRecap(scene, node, flags, party)

        assertEquals("Act II: The Weeping Fen", recap.currentActTitle)
        // Chapter 8 has 9 milestones (0..8)
        assertEquals(9, recap.milestones.size)
        assertTrue(recap.milestones.last().isCurrent)
        assertEquals(8, recap.milestones.last().chapterIndex)

        // Milestone 7 (Ch 7) summary should mention Mire Wyrm or Veridian Chime
        val ch7Milestone = recap.milestones[7]
        assertTrue(ch7Milestone.summary.contains("Mire Wyrm") || ch7Milestone.summary.contains("Veridian"))

        // Spoken recap should recount the previous milestone
        assertTrue(recap.spokenRecap.contains("Previously on your quest:"))
        assertTrue(recap.spokenRecap.contains("Sir Cedric"))
        assertTrue(recap.spokenRecap.contains("Lyra"))
    }
}
