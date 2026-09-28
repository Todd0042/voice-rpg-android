package com.voicerpg.engine.content

import com.voicerpg.engine.model.SelfieFilterConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class SelfieFilterConfigTest {

    @Before
    fun setUp() {
        GameContent.reset()
    }

    @Test
    fun testSelfieFilterConfigLoadedFromAssets() {
        val config = GameContent.selfieConfig
        assertNotNull("SelfieFilterConfig should not be null", config)
        assertTrue("Template selfie filter should be enabled by default", config.isEnabled)
        assertNotNull("Disclaimer title should be present", config.disclaimerTitle)
        assertNotNull("Disclaimer text should be present", config.disclaimerText)
        assertTrue("Available backgrounds should not be empty", config.availableBackgrounds.isNotEmpty())

        // Verify each background points to a real asset file
        for (bg in config.availableBackgrounds) {
            assertTrue("Background label must not be blank", bg.label.isNotBlank())
            assertTrue("Background asset path must not be blank", bg.assetPath.isNotBlank())
            val file = File("app/src/main/assets/${bg.assetPath}")
            assertTrue("Background file must exist at ${file.path}", file.exists() || File("src/main/assets/${bg.assetPath}").exists())
        }
    }

    @Test
    fun testEyeEffectConfig() {
        val eye = GameContent.selfieConfig.eyeEffect
        assertNotNull("Eye effect config should be present", eye)
        assertTrue("Eye effect label must not be blank", eye!!.label.isNotBlank())
        assertTrue("Eye effect description must not be blank", eye.description.isNotBlank())
        assertTrue("Eye effect dark center color must start with #", eye.darkCenterColorHex.startsWith("#"))
        assertTrue("Radius factor must be positive", eye.radiusFactor > 0f)
    }

    @Test
    fun testColorGradeAndScanlineConfigs() {
        val colorGrade = GameContent.selfieConfig.colorGrade
        assertNotNull("Color grade config should be present", colorGrade)
        assertEquals("Color grade matrix must contain 20 float entries for 4x5 ColorMatrix", 20, colorGrade!!.colorMatrix.size)

        val scanlines = GameContent.selfieConfig.scanlines
        assertNotNull("Scanlines config should be present", scanlines)
        assertTrue("Line alpha must be within 0..255", scanlines!!.lineAlpha in 0..255)
        assertTrue("Line spacing must be positive", scanlines.lineSpacing > 0f)
        assertTrue("Stroke width must be positive", scanlines.strokeWidth > 0f)
    }

    @Test
    fun testCelShadingAndInkOutlineConfigs() {
        val cel = GameContent.selfieConfig.celShading
        assertNotNull("Cel shading config should be present", cel)
        assertTrue("Cel shading label must not be blank", cel!!.label.isNotBlank())
        assertTrue("Bands must be between 2 and 6", cel.bands in 2..6)
        assertTrue("Smooth radius must be positive", cel.smoothRadius > 0)

        val ink = GameContent.selfieConfig.inkOutlines
        assertNotNull("Ink outline config should be present", ink)
        assertTrue("Ink outline label must not be blank", ink!!.label.isNotBlank())
        assertTrue("Ink color must start with #", ink.inkColorHex.startsWith("#"))
        assertTrue("Sensitivity must be positive", ink.sensitivity > 0f)
    }

    @Test
    fun testTFLiteStylizationConfig() {
        val tflite = GameContent.selfieConfig.tfliteStylization
        assertNotNull("TFLite stylization config should be present", tflite)
        assertTrue("TFLite label must not be blank", tflite!!.label.isNotBlank())
        assertTrue("Model asset path must not be blank", tflite.modelAssetPath.isNotBlank())
        val file = File("app/src/main/assets/${tflite.modelAssetPath}")
        assertTrue("Model file must exist at ${file.path}", file.exists() || File("src/main/assets/${tflite.modelAssetPath}").exists())
        assertEquals(256, tflite.inputSize)
    }

    @Test
    fun testDefaultFallbackWhenConfigAbsent() {
        val emptySource = object : ContentSource {
            override fun readText(path: String): String? = null
            override fun listFiles(dir: String): List<String> = emptyList()
        }
        val pack = GameContentLoader(emptySource).load()
        assertFalse("SelfieFilterConfig should be disabled by default if missing", pack.manifest.selfieConfig.isEnabled)
        assertTrue("Available backgrounds should be empty in fallback", pack.manifest.selfieConfig.availableBackgrounds.isEmpty())
        org.junit.Assert.assertNull("TFLite stylization should be null in fallback", pack.manifest.selfieConfig.tfliteStylization)
        org.junit.Assert.assertNull("Cel shading should be null in fallback", pack.manifest.selfieConfig.celShading)
        org.junit.Assert.assertNull("Ink outlines should be null in fallback", pack.manifest.selfieConfig.inkOutlines)
    }
}
