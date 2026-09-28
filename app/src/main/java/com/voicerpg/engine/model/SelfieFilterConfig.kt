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
    val tfliteStylization: TFLiteStylizeConfig? = null,
    val celShading: CelShadingConfig? = null,
    val inkOutlines: InkOutlineConfig? = null,
    val colorGrade: ColorGradeConfig? = null,
    val scanlines: ScanlineConfig? = null,
    val vignette: VignetteConfig? = null
)

data class TFLiteStylizeConfig(
    val enabledByDefault: Boolean = true,
    val modelAssetPath: String = "game/creation/models/anime_style.tflite",
    val label: String = "🎨 JRPG Anime Neural Style",
    val description: String = "On-device deep neural stylization into anime line-and-wash art",
    val inputSize: Int = 256
)

data class CelShadingConfig(
    val enabledByDefault: Boolean = true,
    val label: String = "✨ Cel-Shaded Anime",
    val description: String = "Smooth tonal quantization with warm highlights and cool shadows",
    val bands: Int = 4,
    val smoothRadius: Int = 2,
    val shadowCoolingFactor: Float = 0.12f
)

data class InkOutlineConfig(
    val enabledByDefault: Boolean = true,
    val label: String = "🖋️ Stylized Ink Outlines",
    val description: String = "Hand-drawn line-art contours and silhouette definition",
    val inkColorHex: String = "#1A162B",
    val sensitivity: Float = 1.0f,
    val outlineSilhouette: Boolean = true
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
