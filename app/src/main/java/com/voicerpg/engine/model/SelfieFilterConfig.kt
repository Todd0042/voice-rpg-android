package com.voicerpg.engine.model

/**
 * Data-driven configuration for custom player selfie/portrait generation and post-processing filters.
 * All fields have defaults so games can selectively customize or omit features via JSON assets.
 */
data class SelfieFilterConfig(
    val isEnabled: Boolean = false,
    val disclaimerTitle: String? = null,
    val disclaimerText: String? = null,
    val disclaimerConfirmButton: String? = null,
    val disclaimerCancelButton: String? = null,
    val sourceDialogTitle: String? = null,
    val sourceDialogSubtitle: String? = null,
    val filterDialogTitle: String? = null,
    val filterDialogSubtitle: String? = null,
    val filterConfirmButton: String? = null,
    val backgroundToggleLabel: String? = null,
    val backgroundToggleDescription: String? = null,
    val availableBackgrounds: List<SelfieBackgroundOption> = emptyList(),
    val eyeEffect: EyeEffectConfig? = null,
    val colorGrade: ColorGradeConfig? = null,
    val scanlines: ScanlineConfig? = null,
    val vignette: VignetteConfig? = null
)

data class SelfieBackgroundOption(
    val id: String = "",
    val label: String = "",
    val assetPath: String = ""
)

data class EyeEffectConfig(
    val enabledByDefault: Boolean = true,
    val label: String = "Eye Shading",
    val description: String = "Sub-orbital dark exhaustion shading",
    val darkCenterColorHex: String = "#872D192A",
    val radiusFactor: Float = 0.28f,
    val yOffsetFactor: Float = 0.55f
)

data class ColorGradeConfig(
    val enabledByDefault: Boolean = true,
    val label: String = "Color Grade",
    val description: String = "Atmospheric color filtering",
    val colorMatrix: List<Float> = emptyList() // 20 floats (4x5 ColorMatrix)
)

data class ScanlineConfig(
    val enabledByDefault: Boolean = true,
    val label: String = "Scanlines",
    val description: String = "CRT monitor scanlines",
    val lineAlpha: Int = 32,
    val lineSpacing: Float = 3.8f,
    val strokeWidth: Float = 1.8f
)

data class VignetteConfig(
    val enabled: Boolean = true,
    val radiusFactor: Float = 0.7f,
    val colorHex: String = "#550A0C12"
)
