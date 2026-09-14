package com.voicerpg.android.ui.environment

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.voicerpg.android.model.BattleEnvironment

/**
 * Loads and caches 16-bit SNES JRPG battle background artworks from assets.
 */
object EnvironmentAssetLoader {
    private val cache = mutableMapOf<BattleEnvironment, ImageBitmap?>()

    fun getOrLoad(context: Context, environment: BattleEnvironment): ImageBitmap? {
        if (cache.containsKey(environment)) {
            return cache[environment]
        }
        val filename = when (environment) {
            BattleEnvironment.FOREST -> "environments/forest.jpg"
            BattleEnvironment.CASTLE -> "environments/castle.jpg"
            BattleEnvironment.DUNGEON -> "environments/dungeon.jpg"
            BattleEnvironment.CAVE -> "environments/cave.jpg"
            BattleEnvironment.SWAMP -> "environments/swamp.jpg"
        }
        val bitmap = try {
            context.assets.open(filename).use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
        cache[environment] = bitmap
        return bitmap
    }
}
