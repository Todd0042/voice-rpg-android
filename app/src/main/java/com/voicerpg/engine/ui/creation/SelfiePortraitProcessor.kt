package com.voicerpg.engine.ui.creation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import com.voicerpg.engine.model.ColorGradeConfig
import com.voicerpg.engine.model.EyeEffectConfig
import com.voicerpg.engine.model.ScanlineConfig
import com.voicerpg.engine.model.SelfieFilterConfig
import com.voicerpg.engine.model.VignetteConfig
import com.voicerpg.engine.engine.SaveManager
import com.voicerpg.engine.ui.story.StoryAssetLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.math.hypot
import kotlin.math.min

/**
 * ActivityResultContract that requests the system camera to launch using the front-facing
 * (selfie) lens by default across device vendors.
 */
class TakeSelfiePreviewContract : ActivityResultContracts.TakePicturePreview() {
    override fun createIntent(context: Context, input: Void?): Intent {
        return super.createIntent(context, input).apply {
            putExtra("android.intent.extras.CAMERA_FACING", 1) // 1 = CameraInfo.CAMERA_FACING_FRONT
            putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            putExtra("android.intent.extra.CAMERA_FACING", 1)
            putExtra("default_camera", "1")
            putExtra("camerafacing", "front")
            putExtra("previous_mode", "front")
        }
    }
}

/**
 * Generic image processor for custom hero portraits.
 * Operates on [SelfieFilterConfig] to apply game-defined backgrounds, face landmark shading,
 * color matrices, scanlines, and vignette effects.
 */
object SelfiePortraitProcessor {

    const val PORTRAIT_SIZE = 512
    const val CUSTOM_PORTRAIT_FILENAME = "custom_hero_portrait.jpg"

    fun loadFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val raw = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            } ?: return null

            val orientation = try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val exif = android.media.ExifInterface(stream)
                    exif.getAttributeInt(
                        android.media.ExifInterface.TAG_ORIENTATION,
                        android.media.ExifInterface.ORIENTATION_NORMAL
                    )
                } ?: android.media.ExifInterface.ORIENTATION_NORMAL
            } catch (_: Exception) {
                android.media.ExifInterface.ORIENTATION_NORMAL
            }

            val matrix = android.graphics.Matrix()
            when (orientation) {
                android.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                android.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                android.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                android.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1.0f, 1.0f)
                android.media.ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1.0f, -1.0f)
                else -> return raw
            }
            Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, matrix, true)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun processSelfie(
        context: Context,
        inputBitmap: Bitmap,
        filterConfig: SelfieFilterConfig,
        selectedBgAsset: String? = null,
        applySegmentation: Boolean = true,
        applyEyeEffect: Boolean = true,
        applyScanlines: Boolean = true,
        applyColorGrade: Boolean = true
    ): Bitmap = withContext(Dispatchers.Default) {
        // 1. Center crop and scale to square 512x512
        val squareInput = cropAndScale(inputBitmap, PORTRAIT_SIZE)

        // 2. AI Background Segmentation & Background Replacement
        val composited = if (applySegmentation && !selectedBgAsset.isNullOrBlank()) {
            val maskBuffer = runSegmentation(squareInput)
            val bgBitmap = loadAssetBitmap(context, selectedBgAsset, PORTRAIT_SIZE)
            compositeWithBackground(squareInput, bgBitmap, maskBuffer)
        } else {
            squareInput
        }

        // 3. Eye Orbit Shading (Arcane fatigue / exhaustion shading)
        val withEyeEffect = if (applyEyeEffect && filterConfig.eyeEffect != null) {
            applyEyeShading(composited, filterConfig.eyeEffect)
        } else {
            composited
        }

        // 4. Color Grading, Scanlines, and Vignette
        val finalResult = applyPostProcessing(
            withEyeEffect,
            applyScanlines = applyScanlines,
            scanlineConfig = filterConfig.scanlines,
            applyColorGrade = applyColorGrade,
            colorGradeConfig = filterConfig.colorGrade,
            vignetteConfig = filterConfig.vignette
        )

        finalResult
    }

    fun getCustomPortraitFilename(slot: Int): String {
        return if (slot == SaveManager.STORY_MODE_SLOT) {
            "custom_hero_portrait_story.jpg"
        } else {
            "custom_hero_portrait_slot_$slot.jpg"
        }
    }

    fun saveCustomPortrait(context: Context, bitmap: Bitmap, slot: Int = SaveManager.DEFAULT_SLOT): File {
        val filename = getCustomPortraitFilename(slot)
        val destFile = File(context.filesDir, filename)
        FileOutputStream(destFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        StoryAssetLoader.clearSlot(slot)
        return destFile
    }

    fun getCustomPortraitFile(context: Context, slot: Int = SaveManager.DEFAULT_SLOT): File? {
        val filename = getCustomPortraitFilename(slot)
        val f = File(context.filesDir, filename)
        if (f.exists()) return f

        // Backward-compatibility: if slot is DEFAULT_SLOT (1), check legacy single-file name
        if (slot == SaveManager.DEFAULT_SLOT) {
            val legacy = File(context.filesDir, CUSTOM_PORTRAIT_FILENAME)
            if (legacy.exists()) {
                try {
                    legacy.copyTo(f, overwrite = true)
                    legacy.delete()
                    if (f.exists()) return f
                } catch (_: Exception) {
                    return legacy
                }
            }
        }
        return null
    }

    fun deleteCustomPortrait(context: Context, slot: Int = SaveManager.DEFAULT_SLOT): Boolean {
        val f = getCustomPortraitFile(context, slot)
        StoryAssetLoader.clearSlot(slot)
        return f?.delete() ?: false
    }

    private fun cropAndScale(src: Bitmap, targetSize: Int): Bitmap {
        val minDim = min(src.width, src.height)
        val xOffset = (src.width - minDim) / 2
        val yOffset = (src.height - minDim) / 2
        val cropped = Bitmap.createBitmap(src, xOffset, yOffset, minDim, minDim)
        return if (cropped.width != targetSize || cropped.height != targetSize) {
            Bitmap.createScaledBitmap(cropped, targetSize, targetSize, true)
        } else {
            cropped
        }
    }

    private fun loadAssetBitmap(context: Context, assetPath: String, targetSize: Int): Bitmap? = try {
        context.assets.open(assetPath).use { stream ->
            val raw = BitmapFactory.decodeStream(stream)
            if (raw != null) cropAndScale(raw, targetSize) else null
        }
    } catch (_: Exception) {
        null
    }

    private suspend fun runSegmentation(bitmap: Bitmap): ByteBuffer? =
        suspendCancellableCoroutine { cont ->
            try {
                val options = SelfieSegmenterOptions.Builder()
                    .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
                    .build()
                val segmenter = Segmentation.getClient(options)
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                segmenter.process(inputImage)
                    .addOnSuccessListener { mask ->
                        cont.resume(mask.buffer)
                    }
                    .addOnFailureListener {
                        cont.resume(null)
                    }
            } catch (_: Exception) {
                cont.resume(null)
            }
        }

    private fun compositeWithBackground(
        subject: Bitmap,
        background: Bitmap?,
        maskBuffer: ByteBuffer?
    ): Bitmap {
        if (background == null || maskBuffer == null) return subject

        val w = subject.width
        val h = subject.height
        val totalPixels = w * h

        val subjectPixels = IntArray(totalPixels)
        val bgPixels = IntArray(totalPixels)
        val outPixels = IntArray(totalPixels)

        subject.getPixels(subjectPixels, 0, w, 0, 0, w, h)
        background.getPixels(bgPixels, 0, w, 0, 0, w, h)

        maskBuffer.rewind()

        for (i in 0 until totalPixels) {
            val confidence = if (maskBuffer.hasRemaining()) maskBuffer.float else 1f
            val alpha = confidence.coerceIn(0f, 1f)

            val sCol = subjectPixels[i]
            val bCol = bgPixels[i]

            val r = (Color.red(sCol) * alpha + Color.red(bCol) * (1f - alpha)).toInt()
            val g = (Color.green(sCol) * alpha + Color.green(bCol) * (1f - alpha)).toInt()
            val b = (Color.blue(sCol) * alpha + Color.blue(bCol) * (1f - alpha)).toInt()

            outPixels[i] = Color.rgb(r, g, b)
        }

        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        result.setPixels(outPixels, 0, w, 0, 0, w, h)
        return result
    }

    private suspend fun applyEyeShading(source: Bitmap, eyeConfig: EyeEffectConfig): Bitmap =
        suspendCancellableCoroutine { cont ->
            try {
                val options = FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .build()
                val detector = FaceDetection.getClient(options)
                val inputImage = InputImage.fromBitmap(source, 0)

                detector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        if (faces.isEmpty()) {
                            cont.resume(source)
                            return@addOnSuccessListener
                        }

                        val result = source.copy(Bitmap.Config.ARGB_8888, true)
                        val canvas = Canvas(result)

                        val darkCenterColor = parseColor(eyeConfig.darkCenterColorHex, Color.argb(135, 45, 25, 42))
                        val transparentColor = Color.argb(0, Color.red(darkCenterColor), Color.green(darkCenterColor), Color.blue(darkCenterColor))
                        val radiusFactor = eyeConfig.radiusFactor.coerceIn(0.1f, 1.0f)
                        val yOffsetFactor = eyeConfig.yOffsetFactor.coerceIn(0.1f, 1.5f)

                        for (face in faces) {
                            val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
                            val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position

                            if (leftEye != null && rightEye != null) {
                                val dx = rightEye.x - leftEye.x
                                val dy = rightEye.y - leftEye.y
                                val eyeDist = hypot(dx.toDouble(), dy.toDouble()).toFloat()

                                val radius = eyeDist * radiusFactor

                                // Left eye shading
                                drawEyeShade(canvas, leftEye.x, leftEye.y + radius * yOffsetFactor, radius, darkCenterColor, transparentColor)

                                // Right eye shading
                                drawEyeShade(canvas, rightEye.x, rightEye.y + radius * yOffsetFactor, radius, darkCenterColor, transparentColor)
                            }
                        }
                        cont.resume(result)
                    }
                    .addOnFailureListener {
                        cont.resume(source)
                    }
            } catch (_: Exception) {
                cont.resume(source)
            }
        }

    private fun drawEyeShade(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        centerColor: Int,
        edgeColor: Int
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, radius,
                centerColor, edgeColor,
                Shader.TileMode.CLAMP
            )
        }
        val oval = RectF(
            cx - radius * 1.15f,
            cy - radius * 0.55f,
            cx + radius * 1.15f,
            cy + radius * 0.75f
        )
        canvas.drawOval(oval, paint)
    }

    private fun applyPostProcessing(
        source: Bitmap,
        applyScanlines: Boolean,
        scanlineConfig: ScanlineConfig?,
        applyColorGrade: Boolean,
        colorGradeConfig: ColorGradeConfig?,
        vignetteConfig: VignetteConfig?
    ): Bitmap {
        val w = source.width
        val h = source.height
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        // 1. Color Grading Matrix
        if (applyColorGrade && colorGradeConfig != null && colorGradeConfig.colorMatrix.size >= 20) {
            val filterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                val matrix = ColorMatrix(colorGradeConfig.colorMatrix.toFloatArray())
                colorFilter = ColorMatrixColorFilter(matrix)
            }
            canvas.drawBitmap(source, 0f, 0f, filterPaint)
        }

        // 2. Scanlines
        if (applyScanlines && scanlineConfig != null) {
            val alpha = scanlineConfig.lineAlpha.coerceIn(0, 255)
            val scanlinePaint = Paint().apply {
                color = Color.argb(alpha, 0, 0, 0)
                strokeWidth = scanlineConfig.strokeWidth
            }
            var y = 0f
            val spacing = if (scanlineConfig.lineSpacing > 0.5f) scanlineConfig.lineSpacing else 3.8f
            while (y < h) {
                canvas.drawLine(0f, y, w.toFloat(), y, scanlinePaint)
                y += spacing
            }
        }

        // 3. Vignette
        if (vignetteConfig?.enabled == true) {
            val vigColor = parseColor(vignetteConfig.colorHex, Color.argb(85, 10, 12, 18))
            val radius = w * (if (vignetteConfig.radiusFactor > 0.1f) vignetteConfig.radiusFactor else 0.7f)
            val vignettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    w / 2f, h / 2f, radius,
                    Color.TRANSPARENT,
                    vigColor,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), vignettePaint)
        }

        return result
    }

    private fun parseColor(hex: String?, fallback: Int): Int {
        if (hex.isNullOrBlank()) return fallback
        return runCatching {
            val clean = hex.removePrefix("#").trim()
            val value = clean.toLong(16)
            when (clean.length) {
                6 -> (0xFF000000L or value).toInt()
                8 -> value.toInt()
                else -> fallback
            }
        }.getOrDefault(fallback)
    }
}
