package com.voicerpg.android

import com.voicerpg.android.engine.StoryScript
import com.voicerpg.android.model.DialogueSpeaker
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Guards that every artwork path wired into the story engine (scene backgrounds and
 * character portraits) resolves to a real asset file, and that the narrator easter-egg
 * portrait alternates are valid.
 */
class AssetWiringTest {

    private val assetsDir: File = findAssetsDir()

    private fun findAssetsDir(): File {
        val candidates = listOf(File("src/main/assets"), File("app/src/main/assets"))
        for (candidate in candidates) {
            if (candidate.isDirectory) return candidate
        }
        throw IllegalStateException(
            "Could not locate assets dir from working dir ${System.getProperty("user.dir")}"
        )
    }

    @Test
    fun everySceneBackgroundAssetResolvesToARealFile() {
        val missing = mutableListOf<String>()
        for (scene in StoryScript.ALL_SCENES.values) {
            val file = File(assetsDir, scene.backgroundAsset)
            if (!file.isFile) missing.add("${scene.id} -> ${scene.backgroundAsset}")
        }
        assertTrue("Missing scene background assets: $missing", missing.isEmpty())
    }

    @Test
    fun everySceneMusicAssetResolvesToARealFile() {
        val missing = mutableListOf<String>()
        for (scene in StoryScript.ALL_SCENES.values) {
            val file = File(assetsDir, scene.musicAsset)
            if (!file.isFile) missing.add("${scene.id} -> ${scene.musicAsset}")
        }
        assertTrue("Missing scene music assets: $missing", missing.isEmpty())
    }

    @Test
    fun allBgmAudioFilesExist() {
        val expectedTracks = listOf(
            "audio/music/bgm_act1_forest.ogg",
            "audio/music/bgm_act2_marsh.ogg",
            "audio/music/bgm_act3_bastion.ogg",
            "audio/music/bgm_act4_celestial.ogg",
            "audio/music/bgm_combat.ogg"
        )
        val missing = mutableListOf<String>()
        for (track in expectedTracks) {
            val file = File(assetsDir, track)
            if (!file.isFile) missing.add(track)
        }
        assertTrue("Missing BGM tracks: $missing", missing.isEmpty())
    }

    @Test
    fun everyPortraitAssetResolvesToARealFile() {
        val speakers = listOf(
            DialogueSpeaker.AETHEL,
            DialogueSpeaker.CEDRIC,
            DialogueSpeaker.LYRA,
            DialogueSpeaker.ZEPHYR,
            DialogueSpeaker.MALAKOR,
            DialogueSpeaker.NARRATOR,
            DialogueSpeaker.SHADOW_WISP,
            DialogueSpeaker.VAELOR,
            DialogueSpeaker.GALAHAULT,
            DialogueSpeaker.NOCTURNE,
            DialogueSpeaker.OUROS,
            DialogueSpeaker.DRYAD_MATRON,
            DialogueSpeaker.VOICE_MOTE
        )
        val missing = mutableListOf<String>()
        for (speaker in speakers) {
            val asset = speaker.portraitAsset
            if (asset == null) {
                missing.add("${speaker.id} has no portrait asset")
                continue
            }
            if (!File(assetsDir, asset).isFile) missing.add("${speaker.id} -> $asset")
        }
        assertTrue("Missing portrait assets: $missing", missing.isEmpty())
    }

    @Test
    fun narratorEasterEggAlternatePortraitExists() {
        assertTrue("narrator1.jpg easter-egg art missing", File(assetsDir, "portraits/narrator1.jpg").isFile)
    }

    @Test
    fun effectivePortraitAssetIsStableForNonNarrators() {
        val nonNarrators = listOf(
            DialogueSpeaker.AETHEL,
            DialogueSpeaker.CEDRIC,
            DialogueSpeaker.LYRA,
            DialogueSpeaker.ZEPHYR,
            DialogueSpeaker.MALAKOR,
            DialogueSpeaker.SHADOW_WISP,
            DialogueSpeaker.VAELOR,
            DialogueSpeaker.GALAHAULT,
            DialogueSpeaker.NOCTURNE,
            DialogueSpeaker.OUROS,
            DialogueSpeaker.DRYAD_MATRON,
            DialogueSpeaker.VOICE_MOTE
        )
        for (speaker in nonNarrators) {
            repeat(200) {
                assertTrue(
                    "${speaker.id} must always resolve its own portrait",
                    speaker.effectivePortraitAsset() == speaker.portraitAsset
                )
            }
        }
    }

    @Test
    fun narratorEffectivePortraitChoosesOnlyValidArt() {
        val valid = setOf("portraits/narrator.jpg", "portraits/narrator1.jpg")
        repeat(5000) {
            val picked = DialogueSpeaker.NARRATOR.effectivePortraitAsset()
            assertTrue("Narrator picked invalid asset $picked", picked in valid)
        }
    }
}