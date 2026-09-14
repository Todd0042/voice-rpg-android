package com.voicerpg.android.ui.environment

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.voicerpg.android.model.BattleEnvironment
import kotlin.math.sin

/**
 * 4-Frame Living Background Canvas Renderer
 *
 * Combines authentic, high-production 16-bit SNES JRPG pixel-art backdrops
 * with the strict Four-Frame Living Background Rule:
 * All environments feature 4 distinct cycling animation frames showcasing wind & foliage motion,
 * dynamic light/shadow shifts (torches flaring & dimming, moving light radius), and secondary motes
 * (water ripples, embers, fireflies, will-o'-the-wisps).
 */
@Composable
fun BattleEnvironmentCanvas(
    environment: BattleEnvironment,
    partyCount: Int = 4,
    enemyCount: Int = 3,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val backgroundBitmap = remember(environment) {
        EnvironmentAssetLoader.getOrLoad(context, environment)
    }

    // 4-frame continuous loop: 400ms per frame (1600ms full cycle)
    val infiniteTransition = rememberInfiniteTransition(label = "EnvAnimation")
    val cycleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CycleProgress"
    )
    val frameIndex = cycleProgress.toInt().coerceIn(0, 3)

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val horizonY = h * 0.54f // Perspective horizon where back scenery meets floor

        if (backgroundBitmap != null) {
            // 1. Draw Authentic 16-Bit Pixel Art Artwork Backdrop
            drawImage(
                image = backgroundBitmap,
                dstSize = IntSize(w.toInt(), h.toInt())
            )

            // 2. Layer Dynamic 4-Frame Living Environmental Animation
            when (environment) {
                BattleEnvironment.FOREST -> drawForestAnimationOverlay(w, h, horizonY, frameIndex)
                BattleEnvironment.CASTLE -> drawCastleAnimationOverlay(w, h, horizonY, frameIndex)
                BattleEnvironment.DUNGEON -> drawDungeonAnimationOverlay(w, h, horizonY, frameIndex)
                BattleEnvironment.CAVE -> drawCaveAnimationOverlay(w, h, horizonY, frameIndex)
                BattleEnvironment.SWAMP -> drawSwampAnimationOverlay(w, h, horizonY, frameIndex)
            }
        } else {
            // Procedural fallback if bitmap fails to load
            when (environment) {
                BattleEnvironment.FOREST -> drawForestScene(w, h, horizonY, frameIndex)
                BattleEnvironment.CASTLE -> drawCastleScene(w, h, horizonY, frameIndex)
                BattleEnvironment.DUNGEON -> drawDungeonScene(w, h, horizonY, frameIndex)
                BattleEnvironment.CAVE -> drawCaveScene(w, h, horizonY, frameIndex)
                BattleEnvironment.SWAMP -> drawSwampScene(w, h, horizonY, frameIndex)
            }
        }

        // Contact shadows beneath heroes (left) and enemies (right)
        drawGroundContactShadows(w, h, horizonY, environment, frameIndex, partyCount, enemyCount)
    }
}

// =========================================================================
// LIVING ANIMATION OVERLAYS ON TOP OF PIXEL ART BACKDROPS
// =========================================================================

/**
 * Dungeon Living Animation Overlay:
 * - Dynamic 4-stage torch flare & dimmer lighting (torches dim down in frame 1, flare in frame 2)
 * - Shifting pillar shadows cast across the cracked flagstones
 * - Pulsing cyan arcane runes on the left pillar
 * - Cold drifting ground mist
 */
private fun DrawScope.drawDungeonAnimationOverlay(w: Float, h: Float, horizonY: Float, frame: Int) {
    // 1. Pulsing Cyan Arcane Runes (Left Pillar at x ≈ 0.07w)
    val runePulseAlpha = when (frame) {
        0 -> 0.35f
        1 -> 0.70f // Illuminates as torches dim!
        2 -> 1.0f  // Peak celestial surge
        else -> 0.50f
    }
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF00E5FF).copy(alpha = runePulseAlpha * 0.45f), Color.Transparent),
            center = Offset(w * 0.07f, h * 0.55f),
            radius = 90f
        ),
        radius = 90f,
        center = Offset(w * 0.07f, h * 0.55f)
    )

    // 2. Dynamic Wall Torchlight Flares & Dimmer Shifts (Four Sconces across arches)
    val torches = listOf(
        Offset(w * 0.41f, h * 0.45f),
        Offset(w * 0.54f, h * 0.47f),
        Offset(w * 0.79f, h * 0.46f),
        Offset(w * 0.92f, h * 0.40f)
    )

    val (lightRadius, lightAlpha) = when (frame) {
        0 -> Pair(90f, 0.28f)  // Steady warm amber
        1 -> Pair(50f, 0.12f)  // LIGHTS GO DIMMER (Shadows stretch)
        2 -> Pair(140f, 0.50f) // ROARING FLARE (Radiant amber spill)
        else -> Pair(80f, 0.22f) // Settling ember
    }

    torches.forEachIndexed { tIdx, tPos ->
        val flameShift = if ((frame + tIdx) % 2 == 0) -2f else 3f
        // Volumetric radial glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFB300).copy(alpha = lightAlpha),
                    Color(0xFFFF6F00).copy(alpha = lightAlpha * 0.4f),
                    Color.Transparent
                ),
                center = tPos,
                radius = lightRadius
            ),
            radius = lightRadius,
            center = tPos
        )

        // Dynamic flame tongue peak
        val flameHeight = when (frame) {
            0 -> 16f
            1 -> 9f   // Dim low flame
            2 -> 24f  // Tall roaring tongue
            else -> 14f
        }
        val fPath = Path().apply {
            moveTo(tPos.x - 5f, tPos.y)
            lineTo(tPos.x + flameShift, tPos.y - flameHeight)
            lineTo(tPos.x + 5f, tPos.y)
            close()
        }
        drawPath(fPath, Color(0xFFFF9100).copy(alpha = 0.75f))
        drawCircle(Color(0xFFFFEE58).copy(alpha = 0.85f), radius = 3f, center = Offset(tPos.x, tPos.y - 3f))
    }

    // 3. Shifting Floor Pillar Shadows (React to flame flare)
    val shadowSkew = when (frame) {
        0 -> 0f
        1 -> 24f  // Extended long shadow when light dims!
        2 -> -18f // Angle shifts when right flame surges!
        else -> 6f
    }
    val shadowAlpha = if (frame == 1) 0.35f else 0.20f
    listOf(w * 0.22f, w * 0.60f).forEach { sx ->
        val sPath = Path().apply {
            moveTo(sx, horizonY)
            lineTo(sx + 35f, horizonY)
            lineTo(sx + 35f + shadowSkew, h)
            lineTo(sx + shadowSkew, h)
            close()
        }
        drawPath(sPath, Color(0xFF000000).copy(alpha = shadowAlpha))
    }

    // 4. Low-Creeping Cold Dungeon Mist (4 Drift Steps)
    val mistOffset = frame * 18f
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x1880DEEA),
                Color(0x3000E5FF),
                Color.Transparent
            ),
            startY = horizonY + (h - horizonY) * 0.40f,
            endY = h
        ),
        topLeft = Offset(mistOffset - 40f, horizonY + (h - horizonY) * 0.40f),
        size = Size(w + 80f, (h - horizonY) * 0.60f)
    )
}

/**
 * Castle Living Animation Overlay:
 * - Roaring lion-head iron brazier flame and rising embers (4 trajectory steps)
 * - Waving royal crimson war banner gale ripples
 * - Distant mountain lightning storm flash on frame 2
 * - Courtyard torchlight flicker
 */
private fun DrawScope.drawCastleAnimationOverlay(w: Float, h: Float, horizonY: Float, frame: Int) {
    // 1. Distant Storm Lightning Flash (Illuminates sky on frame 2)
    if (frame == 2) {
        drawRect(
            color = Color(0x28E0B0FF),
            topLeft = Offset(0f, 0f),
            size = Size(w, horizonY)
        )
    }

    // 2. Roaring Lion-Head Brazier (at x ≈ 0.32w, y ≈ 0.56h)
    val bPos = Offset(w * 0.32f, h * 0.56f)
    val (bHeight, bAlpha) = when (frame) {
        0 -> Pair(35f, 0.35f)
        1 -> Pair(46f, 0.50f) // Roaring flare
        2 -> Pair(58f, 0.65f) // Peak roaring inferno!
        else -> Pair(28f, 0.28f)
    }

    // Radial firelight wash across stone courtyard
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFA000).copy(alpha = bAlpha), Color(0x33FF5722), Color.Transparent),
            center = bPos,
            radius = bHeight * 3.5f
        ),
        radius = bHeight * 3.5f,
        center = bPos
    )

    // Rising Golden Sparks & Embers (Drifting up & right toward smoke plume in 4 steps)
    for (i in 0..4) {
        val emberStep = (frame + i) % 4
        val ex = bPos.x + 8f + emberStep * 14f + (i * 6f)
        val ey = bPos.y - bHeight - (emberStep * 18f) - (i * 10f)
        val eAlpha = (1f - (emberStep * 0.22f)).coerceIn(0.2f, 1f)
        drawCircle(Color(0xFFFFD54F).copy(alpha = eAlpha), radius = 2.5f, center = Offset(ex, ey))
    }

    // 3. Royal Crimson Banner Wind Wave Highlights (at x ≈ 0.18w, y ≈ 0.20h)
    val bannerWaveX = when (frame) {
        0 -> 0f
        1 -> 8f
        2 -> 16f
        else -> 5f
    }
    // Shimmering ripple line on banner
    drawLine(
        Color(0xFFFFD54F).copy(alpha = 0.6f),
        start = Offset(w * 0.14f + bannerWaveX * 0.4f, h * 0.18f),
        end = Offset(w * 0.26f + bannerWaveX, h * 0.26f),
        strokeWidth = 2f
    )

    // 4. Right Gatehouse Archway Torches (at x ≈ 0.88w and 0.98w)
    listOf(Offset(w * 0.88f, h * 0.52f), Offset(w * 0.98f, h * 0.52f)).forEach { tPos ->
        val tPulse = if (frame % 2 == 0) 1.25f else 0.85f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x55FFA000), Color.Transparent),
                center = tPos,
                radius = 35f * tPulse
            ),
            radius = 35f * tPulse,
            center = tPos
        )
    }
}

/**
 * Forest Living Animation Overlay:
 * - Volumetric silver moonbeam shimmering rays
 * - Bioluminescent golden fireflies & spores drifting in 4 orbital steps
 * - Wind-blown foliage highlights
 */
private fun DrawScope.drawForestAnimationOverlay(w: Float, h: Float, horizonY: Float, frame: Int) {
    // 1. Volumetric Moonbeams Shimmering (Center top moon at x ≈ 0.50w, y ≈ 0.15h)
    val rayAlpha = when (frame) {
        0 -> 0.12f
        1 -> 0.22f
        2 -> 0.32f // Peak celestial luminescence
        else -> 0.18f
    }
    val moonCenter = Offset(w * 0.50f, h * 0.15f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFE0F7FA).copy(alpha = rayAlpha * 0.7f), Color(0x2200E5FF), Color.Transparent),
            center = moonCenter,
            radius = 160f
        ),
        radius = 160f,
        center = moonCenter
    )

    // 2. Drifting Bioluminescent Fireflies & Spores (5 motes in 4 orbital steps)
    val sporePositions = listOf(
        Offset(w * 0.20f + frame * 8f, h * 0.65f - frame * 4f),
        Offset(w * 0.38f - frame * 6f, h * 0.72f - frame * 5f),
        Offset(w * 0.55f + frame * 10f, h * 0.60f + frame * 3f),
        Offset(w * 0.74f - frame * 7f, h * 0.68f - frame * 4f),
        Offset(w * 0.88f + frame * 5f, h * 0.58f - frame * 6f)
    )
    sporePositions.forEachIndexed { i, pos ->
        val pulse = if ((frame + i) % 2 == 0) 1.3f else 0.7f
        drawCircle(Color(0xFFFFEE58).copy(alpha = (0.85f * pulse).coerceIn(0.2f, 1f)), radius = 3.5f * pulse, center = pos)
        drawCircle(Color(0x33FFD54F), radius = 9f * pulse, center = pos)
    }

    // 3. Rustling Wind Grass Highlights in clearing
    val grassLean = when (frame) {
        0 -> 0f
        1 -> 6f
        2 -> 12f // Peak gust
        else -> 4f
    }
    listOf(0.28f, 0.45f, 0.65f).forEach { gxFrac ->
        val gx = w * gxFrac
        val gy = horizonY + 30f
        drawLine(Color(0x6674C69D), start = Offset(gx, gy), end = Offset(gx + grassLean, gy - 12f), strokeWidth = 2f)
    }
}

/**
 * Cave Living Animation Overlay:
 * - 4-stage stalactite water drip & ripple cycle
 * - Prismatic amethyst & cyan crystal specular star flares
 */
private fun DrawScope.drawCaveAnimationOverlay(w: Float, h: Float, horizonY: Float, frame: Int) {
    // 1. 4-Stage Stalactite Water Drip Cycle into Dark Pool (x ≈ 0.33w, tip at y ≈ 0.52h, pool at y ≈ 0.72h)
    val dripX = w * 0.33f
    val tipY = h * 0.52f
    val poolY = h * 0.72f

    when (frame) {
        0 -> {
            // Frame 0: Water bead swelling & glistening at stalactite tip
            drawCircle(Color(0xFF80DEEA), radius = 4f, center = Offset(dripX, tipY + 2f))
            drawCircle(Color.White, radius = 2f, center = Offset(dripX - 1f, tipY + 1f))
        }
        1 -> {
            // Frame 1: Droplet falling through mid-air
            val dropY = tipY + (poolY - tipY) * 0.55f
            drawLine(Color(0xFF80DEEA), start = Offset(dripX, dropY - 10f), end = Offset(dripX, dropY), strokeWidth = 3f)
            drawCircle(Color.White, radius = 1.5f, center = Offset(dripX, dropY))
        }
        2 -> {
            // Frame 2: Splash impact! Bright starburst impact on pool surface
            drawCircle(Color(0xFFE0F7FA), radius = 5f, center = Offset(dripX, poolY))
            drawLine(Color(0xFF80DEEA), start = Offset(dripX - 12f, poolY - 5f), end = Offset(dripX + 12f, poolY - 5f), strokeWidth = 2f)
            drawLine(Color(0xFF80DEEA), start = Offset(dripX - 7f, poolY - 10f), end = Offset(dripX + 7f, poolY - 10f), strokeWidth = 2f)
        }
        3 -> {
            // Frame 3: Expanding concentric ripple rings spreading across dark pool
            drawOval(
                color = Color(0x6680DEEA),
                topLeft = Offset(dripX - 28f, poolY - 8f),
                size = Size(56f, 16f),
                style = Stroke(width = 2f)
            )
            drawOval(
                color = Color(0x3380DEEA),
                topLeft = Offset(dripX - 52f, poolY - 15f),
                size = Size(104f, 30f),
                style = Stroke(width = 1.5f)
            )
        }
    }

    // 2. Crystal Specular Star Glints (Amethyst on left at 0.10w, Cyan on right at 0.90w)
    val crystals = listOf(
        Pair(Offset(w * 0.10f, h * 0.40f), Color(0xFFE040FB)),
        Pair(Offset(w * 0.90f, h * 0.45f), Color(0xFF00E5FF))
    )
    crystals.forEachIndexed { cIdx, (cPos, cColor) ->
        val cPulse = when ((frame + cIdx) % 4) {
            0 -> 0.4f
            1 -> 0.8f
            2 -> 1.0f // Specular star flare!
            else -> 0.6f
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(cColor.copy(alpha = cPulse * 0.5f), Color.Transparent),
                center = cPos,
                radius = 65f * cPulse
            ),
            radius = 65f * cPulse,
            center = cPos
        )
        if ((frame + cIdx) % 4 == 2) {
            drawCircle(Color.White, radius = 3f, center = cPos)
            drawLine(Color.White, start = Offset(cPos.x - 8f, cPos.y), end = Offset(cPos.x + 8f, cPos.y), strokeWidth = 2f)
            drawLine(Color.White, start = Offset(cPos.x, cPos.y - 8f), end = Offset(cPos.x, cPos.y + 8f), strokeWidth = 2f)
        }
    }
}

/**
 * Swamp Living Animation Overlay:
 * - 4-stage toxic marsh gas bubble inflating, popping, and spreading green ripples
 * - Bioluminescent will-o'-the-wisps bobbing in undulating paths
 * - Eerie creeping emerald swamp haze
 */
private fun DrawScope.drawSwampAnimationOverlay(w: Float, h: Float, horizonY: Float, frame: Int) {
    // 1. 4-Stage Marsh Gas Bubble (in center bog pool at x ≈ 0.50w, y ≈ 0.74h)
    val bubbleX = w * 0.50f
    val bubbleY = h * 0.74f

    when (frame) {
        0 -> {
            // Frame 0: Small toxic bubble rising
            drawCircle(Color(0xFF69F0AE), radius = 4f, center = Offset(bubbleX, bubbleY))
        }
        1 -> {
            // Frame 1: Bubble fully inflated
            drawCircle(Color(0xFFB9F6CA), radius = 8f, center = Offset(bubbleX, bubbleY))
            drawCircle(Color.White, radius = 2.5f, center = Offset(bubbleX - 3f, bubbleY - 3f)) // Specular
        }
        2 -> {
            // Frame 2: Bubble POP! Starburst burst
            drawCircle(Color(0xFF00E676), radius = 3f, center = Offset(bubbleX, bubbleY))
            drawLine(Color(0xFF69F0AE), start = Offset(bubbleX - 10f, bubbleY), end = Offset(bubbleX + 10f, bubbleY), strokeWidth = 2f)
            drawLine(Color(0xFF69F0AE), start = Offset(bubbleX, bubbleY - 10f), end = Offset(bubbleX, bubbleY + 10f), strokeWidth = 2f)
        }
        3 -> {
            // Frame 3: Expanding faint green water ripple ring
            drawOval(
                color = Color(0x6669F0AE),
                topLeft = Offset(bubbleX - 26f, bubbleY - 8f),
                size = Size(52f, 16f),
                style = Stroke(width = 2f)
            )
        }
    }

    // 2. Bioluminescent Will-o'-the-Wisps (Hovering & Bobbing across 4 frames)
    val wisps = listOf(
        Pair(w * 0.32f, h * 0.40f),
        Pair(w * 0.68f, h * 0.35f)
    )
    wisps.forEachIndexed { wIdx, (wx, wy) ->
        val bobY = when ((frame + wIdx * 2) % 4) {
            0 -> 0f
            1 -> -10f
            2 -> -18f // Peak float
            else -> -8f
        }
        val wispCenter = Offset(wx + sin(frame.toFloat()) * 5f, wy + bobY)
        val wispPulse = if (frame % 2 == 0) 1.3f else 0.8f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.85f * wispPulse), Color(0x3300E676), Color.Transparent),
                center = wispCenter,
                radius = 35f * wispPulse
            ),
            radius = 35f * wispPulse,
            center = wispCenter
        )
        drawCircle(Color(0xFFE0F7FA), radius = 4f * wispPulse, center = wispCenter)
    }

    // 3. Eerie Emerald Ground Fog Wafer
    val fogAlpha = when (frame) {
        0 -> 0.15f
        1 -> 0.22f
        2 -> 0.30f
        else -> 0.18f
    }
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color(0xFF00E676).copy(alpha = fogAlpha), Color.Transparent),
            startY = h * 0.60f,
            endY = h * 0.90f
        ),
        topLeft = Offset(0f, h * 0.60f),
        size = Size(w, h * 0.30f)
    )
}

// =========================================================================
// PROCEDURAL FALLBACKS (If Assets Unavailable)
// =========================================================================

private fun DrawScope.drawForestScene(w: Float, h: Float, horizonY: Float, frame: Int) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF070B19), Color(0xFF0E1A33), Color(0xFF14243B)),
            startY = 0f,
            endY = horizonY
        ),
        size = Size(w, horizonY)
    )
    drawCircle(Color(0xFFE0F7FA), radius = 18f, center = Offset(w * 0.78f, h * 0.14f))
    drawCircle(Color(0x3300E5FF), radius = 34f, center = Offset(w * 0.78f, h * 0.14f))

    val swayX = when (frame) { 0 -> 0f; 1 -> 4f; 2 -> 9f; else -> 3f }
    val swayY = when (frame) { 0 -> 0f; 1 -> -1f; 2 -> -3f; else -> -1f }
    drawRect(Color(0xFF2E1C14), topLeft = Offset(w * 0.08f, horizonY - 110f), size = Size(18f, 110f))
    drawCircle(Color(0xFF1B4332), radius = 52f, center = Offset(w * 0.10f + swayX, horizonY - 130f + swayY))
    drawCircle(Color(0xFF2D6A4F), radius = 42f, center = Offset(w * 0.08f + swayX * 1.1f, horizonY - 150f + swayY))

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF1B3828), Color(0xFF142B1F), Color(0xFF0F1E16)),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )
}

private fun DrawScope.drawCastleScene(w: Float, h: Float, horizonY: Float, frame: Int) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF1E0A1E), Color(0xFF37102A), Color(0xFF5A1D2B)),
            startY = 0f,
            endY = horizonY
        ),
        size = Size(w, horizonY)
    )
    drawRect(Color(0xFF37474F), topLeft = Offset(0f, horizonY - 80f), size = Size(w, 80f))
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF263238), Color(0xFF1E272C), Color(0xFF141A1E)),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )
}

private fun DrawScope.drawDungeonScene(w: Float, h: Float, horizonY: Float, frame: Int) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF12151D), Color(0xFF1A1F2B), Color(0xFF141822)),
            startY = 0f,
            endY = horizonY
        ),
        size = Size(w, horizonY)
    )
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF141720), Color(0xFF0D1017), Color(0xFF080A0E)),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )
}

private fun DrawScope.drawCaveScene(w: Float, h: Float, horizonY: Float, frame: Int) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF08050C), Color(0xFF110B1A), Color(0xFF1A0F26)),
            startY = 0f,
            endY = horizonY
        ),
        size = Size(w, horizonY)
    )
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF140D1F), Color(0xFF0F0917), Color(0xFF09050E)),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )
}

private fun DrawScope.drawSwampScene(w: Float, h: Float, horizonY: Float, frame: Int) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0A140F), Color(0xFF12241C), Color(0xFF1A3326)),
            startY = 0f,
            endY = horizonY
        ),
        size = Size(w, horizonY)
    )
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF14291E), Color(0xFF0F1E16), Color(0xFF0A140E)),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )
}

// =========================================================================
// Ground Contact Shadows Beneath Characters
// =========================================================================
private fun DrawScope.drawGroundContactShadows(
    w: Float,
    h: Float,
    horizonY: Float,
    environment: BattleEnvironment,
    frame: Int,
    partyCount: Int = 4,
    enemyCount: Int = 3
) {
    val shadowOffsetX = when (environment) {
        BattleEnvironment.DUNGEON -> when (frame) {
            0 -> 0f
            1 -> 6f
            2 -> -4f
            else -> 2f
        }
        BattleEnvironment.CASTLE -> when (frame) {
            0 -> 0f
            1 -> 4f
            2 -> 8f
            else -> 2f
        }
        else -> 0f
    }

    val shadowColor = Color(0x55000000)

    // Party Members Shadow Positions (Left Flank)
    val safePartyCount = partyCount.coerceIn(1, 4)
    for (i in 0 until safePartyCount) {
        val heroShadowX = w * 0.20f + shadowOffsetX
        val spacing = (h - horizonY) / (safePartyCount + 1)
        val heroShadowY = horizonY + spacing * (i + 1)
        drawOval(
            color = shadowColor,
            topLeft = Offset(heroShadowX - 24f, heroShadowY - 6f),
            size = Size(48f, 12f)
        )
    }

    // Enemy Shadows Positions (Right Flank)
    val safeEnemyCount = enemyCount.coerceIn(1, 6)
    if (safeEnemyCount <= 3) {
        for (i in 0 until safeEnemyCount) {
            val enemyShadowX = w * 0.80f + shadowOffsetX
            val spacing = (h - horizonY) / (safeEnemyCount + 1)
            val enemyShadowY = horizonY + spacing * (i + 1)
            drawOval(
                color = shadowColor,
                topLeft = Offset(enemyShadowX - 28f, enemyShadowY - 7f),
                size = Size(56f, 14f)
            )
        }
    } else {
        // 2-Column Staggered layout for 4-6 enemies
        val frontCount = (safeEnemyCount + 1) / 2
        val backCount = safeEnemyCount / 2

        // Front row (closer to center)
        for (i in 0 until frontCount) {
            val enemyShadowX = w * 0.70f + shadowOffsetX
            val spacing = (h - horizonY) / (frontCount + 1)
            val enemyShadowY = horizonY + spacing * (i + 1)
            drawOval(
                color = shadowColor,
                topLeft = Offset(enemyShadowX - 22f, enemyShadowY - 5f),
                size = Size(44f, 10f)
            )
        }

        // Back row (rearguard)
        for (i in 0 until backCount) {
            val enemyShadowX = w * 0.86f + shadowOffsetX
            val spacing = (h - horizonY) / (backCount + 1)
            val enemyShadowY = horizonY + spacing * (i + 1)
            drawOval(
                color = shadowColor,
                topLeft = Offset(enemyShadowX - 22f, enemyShadowY - 5f),
                size = Size(44f, 10f)
            )
        }
    }
}
