package com.voicerpg.android.ui.story

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Loads and caches 16-bit JRPG story scene backgrounds and character portraits from assets.
 */
object StoryAssetLoader {
    private val bitmapCache = mutableMapOf<String, ImageBitmap?>()

    fun loadBitmap(context: Context, assetPath: String): ImageBitmap? {
        if (bitmapCache.containsKey(assetPath)) {
            return bitmapCache[assetPath]
        }
        val bitmap = try {
            context.assets.open(assetPath).use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
        bitmapCache[assetPath] = bitmap
        return bitmap
    }

    fun clear() {
        bitmapCache.clear()
    }
}
