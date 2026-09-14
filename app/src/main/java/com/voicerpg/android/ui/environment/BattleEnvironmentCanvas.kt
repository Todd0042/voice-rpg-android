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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.voicerpg.android.model.BattleEnvironment
import kotlin.math.sin

/**
 * 4-Frame Living Background Canvas Renderer
 * Adheres strictly to the Four-Frame Living Background Rule:
 * All environments feature 4 distinct cycling animation frames showcasing wind & foliage motion,
 * dynamic light/shadow shifts (torches flaring & dimming, moving light radius), and secondary motes
 * (water ripples, embers, fireflies, will-o'-the-wisps).
 */
@Composable
fun BattleEnvironmentCanvas(
    environment: BattleEnvironment,
    modifier: Modifier = Modifier
) {
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

        when (environment) {
            BattleEnvironment.FOREST -> drawForestScene(w, h, horizonY, frameIndex)
            BattleEnvironment.CASTLE -> drawCastleScene(w, h, horizonY, frameIndex)
            BattleEnvironment.DUNGEON -> drawDungeonScene(w, h, horizonY, frameIndex)
            BattleEnvironment.CAVE -> drawCaveScene(w, h, horizonY, frameIndex)
            BattleEnvironment.SWAMP -> drawSwampScene(w, h, horizonY, frameIndex)
        }

        // Contact shadows beneath heroes (left) and enemies (right)
        drawGroundContactShadows(w, h, horizonY, environment, frameIndex)
    }
}

// =========================================================================
// 1. FOREST SCENE (Ashwood Wilds)
// Swaying tree canopies, wind-blown grass tufts, shifting moonlit shadows, drifting fireflies
// =========================================================================
private fun DrawScope.drawForestScene(w: Float, h: Float, horizonY: Float, frame: Int) {
    // 1. Twilight Starry Sky
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF070B19),
                Color(0xFF0E1A33),
                Color(0xFF14243B)
            ),
            startY = 0f,
            endY = horizonY
        ),
        size = Size(w, horizonY)
    )

    // Distant Stars & Moon
    drawCircle(Color(0xFFE0F7FA), radius = 18f, center = Offset(w * 0.78f, h * 0.14f))
    drawCircle(Color(0x3300E5FF), radius = 34f, center = Offset(w * 0.78f, h * 0.14f)) // Moon halo

    val stars = listOf(
        Offset(w * 0.12f, h * 0.08f), Offset(w * 0.28f, h * 0.12f),
        Offset(w * 0.45f, h * 0.06f), Offset(w * 0.62f, h * 0.15f),
        Offset(w * 0.88f, h * 0.07f)
    )
    stars.forEachIndexed { i, pos ->
        val starAlpha = if ((i + frame) % 2 == 0) 0.9f else 0.4f
        drawCircle(Color.White.copy(alpha = starAlpha), radius = 2f, center = pos)
    }

    // Distant Silhouetted Mountain Ridge
    val mountainPath = Path().apply {
        moveTo(0f, horizonY)
        lineTo(w * 0.2f, horizonY - 45f)
        lineTo(w * 0.42f, horizonY - 25f)
        lineTo(w * 0.65f, horizonY - 60f)
        lineTo(w * 0.85f, horizonY - 30f)
        lineTo(w, horizonY - 40f)
        lineTo(w, horizonY)
        close()
    }
    drawPath(mountainPath, Color(0xFF0B1626))

    // 2. Swaying Trees in the Wind (4 Frames of Canopy & Branch Offset)
    // Frame 0: rest (+0px), Frame 1: breeze (+4px), Frame 2: gust (+9px), Frame 3: relaxing (+3px)
    val swayX = when (frame) {
        0 -> 0f
        1 -> 4f
        2 -> 9f
        else -> 3f
    }
    val swayY = when (frame) {
        0 -> 0f
        1 -> -1f
        2 -> -3f
        else -> -1f
    }

    // Left Ancient Pine / Oak Trunk & Foliage
    drawRect(Color(0xFF2E1C14), topLeft = Offset(w * 0.08f, horizonY - 110f), size = Size(18f, 110f))
    // Multi-tiered foliage clusters shifting with wind
    drawCircle(Color(0xFF1B4332), radius = 52f, center = Offset(w * 0.10f + swayX, horizonY - 130f + swayY))
    drawCircle(Color(0xFF2D6A4F), radius = 42f, center = Offset(w * 0.08f + swayX * 1.1f, horizonY - 150f + swayY))
    drawCircle(Color(0xFF40916C), radius = 28f, center = Offset(w * 0.12f + swayX * 1.2f, horizonY - 170f + swayY))

    // Right Distant Birch / Pine
    drawRect(Color(0xFF3E2723), topLeft = Offset(w * 0.86f, horizonY - 95f), size = Size(14f, 95f))
    drawCircle(Color(0xFF1B4332), radius = 44f, center = Offset(w * 0.88f + swayX * 0.8f, horizonY - 115f + swayY))
    drawCircle(Color(0xFF2D6A4F), radius = 34f, center = Offset(w * 0.86f + swayX * 0.9f, horizonY - 135f + swayY))

    // Wind-blown drifting leaves in frame 2
    if (frame == 2) {
        drawCircle(Color(0xFF52B788), radius = 3f, center = Offset(w * 0.28f, horizonY - 110f))
        drawCircle(Color(0xFF52B788), radius = 2.5f, center = Offset(w * 0.48f, horizonY - 90f))
        drawCircle(Color(0xFF52B788), radius = 3f, center = Offset(w * 0.68f, horizonY - 120f))
    }

    // 3. Perspective Earthen & Mossy Ground Floor
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1B3828), // Deep forest green near horizon
                Color(0xFF142B1F),
                Color(0xFF0F1E16)  // Foreground deep forest turf
            ),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )

    // Ground Perspective Texture Lines
    drawLine(Color(0x2240916C), start = Offset(0f, horizonY + (h - horizonY) * 0.35f), end = Offset(w, horizonY + (h - horizonY) * 0.35f), strokeWidth = 1.5f)
    drawLine(Color(0x2240916C), start = Offset(0f, horizonY + (h - horizonY) * 0.70f), end = Offset(w, horizonY + (h - horizonY) * 0.70f), strokeWidth = 2f)

    // 4. Grass Tufts Rustling in the Wind (Angle & Lean changes per frame)
    val grassLean = when (frame) {
        0 -> 0f
        1 -> 5f
        2 -> 11f
        else -> 3f
    }
    val grassTuftXs = listOf(0.15f, 0.28f, 0.42f, 0.58f, 0.72f, 0.85f)
    grassTuftXs.forEachIndexed { i, frac ->
        val gx = w * frac
        val gy = horizonY + 22f + (i % 3) * 28f
        // 3 blades per tuft
        drawLine(Color(0xFF52B788), start = Offset(gx, gy), end = Offset(gx + grassLean - 3f, gy - 12f), strokeWidth = 2f)
        drawLine(Color(0xFF74C69D), start = Offset(gx + 4f, gy), end = Offset(gx + 4f + grassLean, gy - 16f), strokeWidth = 2.5f)
        drawLine(Color(0xFF52B788), start = Offset(gx + 8f, gy), end = Offset(gx + 8f + grassLean + 3f, gy - 11f), strokeWidth = 2f)
    }

    // 5. Drifting Bioluminescent Fireflies / Golden Spores (4 Trajectory Steps)
    val fireflies = listOf(
        Triple(w * 0.22f + frame * 6f, horizonY - 40f - frame * 4f, 0.85f),
        Triple(w * 0.54f + frame * 8f, horizonY + 30f - frame * 3f, 1.0f),
        Triple(w * 0.76f - frame * 5f, horizonY - 20f + frame * 4f, 0.75f)
    )
    fireflies.forEach { (fx, fy, baseAlpha) ->
        val pulse = if (frame % 2 == 0) 1.2f else 0.8f
        drawCircle(Color(0xFFFFD54F).copy(alpha = (baseAlpha * pulse).coerceIn(0.2f, 1f)), radius = 3.5f * pulse, center = Offset(fx, fy))
        drawCircle(Color(0x33FFD54F), radius = 9f * pulse, center = Offset(fx, fy))
    }
}

// =========================================================================
// 2. CASTLE SCENE (The Broken Garrison)
// Stone battlements, roaring braziers, fluttering war banners, dynamic firelight & shadows
// =========================================================================
private fun DrawScope.drawCastleScene(w: Float, h: Float, horizonY: Float, frame: Int) {
    // 1. Stormy Twilight Mountain Sky
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1E0A1E), // Dark twilight purple
                Color(0xFF37102A), // Fiery dusk horizon
                Color(0xFF5A1D2B)
            ),
            startY = 0f,
            endY = horizonY
        ),
        size = Size(w, horizonY)
    )

    // Distant Silhouette Jagged Peaks
    val peaksPath = Path().apply {
        moveTo(0f, horizonY)
        lineTo(w * 0.18f, horizonY - 35f)
        lineTo(w * 0.35f, horizonY - 18f)
        lineTo(w * 0.55f, horizonY - 50f)
        lineTo(w * 0.75f, horizonY - 25f)
        lineTo(w, horizonY - 45f)
        lineTo(w, horizonY)
        close()
    }
    drawPath(peaksPath, Color(0xFF1A0A14))

    // 2. Stone Castle Ramparts with Crenellations (Merlons & Embrasures)
    val wallTopY = horizonY - 80f
    drawRect(Color(0xFF37474F), topLeft = Offset(0f, wallTopY), size = Size(w, horizonY - wallTopY))

    // Crenellation battlements along top
    val merlonWidth = w / 9f
    for (i in 0..8 step 2) {
        drawRect(Color(0xFF455A64), topLeft = Offset(i * merlonWidth, wallTopY - 24f), size = Size(merlonWidth, 24f))
        drawRect(Color(0xFF263238), topLeft = Offset(i * merlonWidth, wallTopY - 24f), size = Size(3f, 24f)) // Merlon shadow
    }

    // 3. Royal War Banner Fluttering in Mountain Gale (4 wave frames)
    val bannerX = w * 0.50f
    val waveOffset = when (frame) {
        0 -> 0f
        1 -> 5f
        2 -> 10f
        else -> 3f
    }
    // Flagpole
    drawRect(Color(0xFF212121), topLeft = Offset(bannerX - 2f, wallTopY - 60f), size = Size(4f, 60f))
    drawCircle(Color(0xFFFFD54F), radius = 4f, center = Offset(bannerX, wallTopY - 60f))
    // Crimson Banner with waving edge
    val bannerPath = Path().apply {
        moveTo(bannerX + 2f, wallTopY - 56f)
        lineTo(bannerX + 44f + waveOffset, wallTopY - 52f)
        lineTo(bannerX + 38f + waveOffset * 0.7f, wallTopY - 32f)
        lineTo(bannerX + 46f + waveOffset, wallTopY - 14f)
        lineTo(bannerX + 2f, wallTopY - 18f)
        close()
    }
    drawPath(bannerPath, Color(0xFFD32F2F))
    // Golden crest stripe on banner
    drawLine(Color(0xFFFFD54F), start = Offset(bannerX + 2f, wallTopY - 37f), end = Offset(bannerX + 40f + waveOffset * 0.8f, wallTopY - 33f), strokeWidth = 3f)

    // 4. Roaring Iron Braziers on Left and Right Battlement Sconces
    val brazierPositions = listOf(Offset(w * 0.16f, wallTopY - 10f), Offset(w * 0.84f, wallTopY - 10f))
    brazierPositions.forEachIndexed { bIdx, bPos ->
        // Iron brazier bowl & stand
        drawRect(Color(0xFF1E272C), topLeft = Offset(bPos.x - 12f, bPos.y), size = Size(24f, 10f))
        drawRect(Color(0xFF10171A), topLeft = Offset(bPos.x - 3f, bPos.y + 10f), size = Size(6f, 14f))

        // 4-Stage Leaping Flame Tongue
        val flameHeight = when (frame) {
            0 -> 24f
            1 -> 32f // Flare up
            2 -> 40f // Peak roar
            else -> 18f // Dips low (dimmer contrast)
        }
        val flameLean = when (frame) {
            0 -> 0f
            1 -> -3f
            2 -> -6f
            else -> -1f
        }

        // Dynamic light cast radius
        val lightRadius = flameHeight * 3.6f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x55FFA000), Color(0x11FF5722), Color.Transparent),
                center = bPos,
                radius = lightRadius
            ),
            radius = lightRadius,
            center = bPos
        )

        // Outer Crimson Flame
        val flamePath = Path().apply {
            moveTo(bPos.x - 10f, bPos.y)
            lineTo(bPos.x + flameLean, bPos.y - flameHeight)
            lineTo(bPos.x + 10f, bPos.y)
            close()
        }
        drawPath(flamePath, Color(0xFFE65100))

        // Inner Golden Core
        val corePath = Path().apply {
            moveTo(bPos.x - 6f, bPos.y)
            lineTo(bPos.x + flameLean * 0.8f, bPos.y - flameHeight * 0.7f)
            lineTo(bPos.x + 6f, bPos.y)
            close()
        }
        drawPath(corePath, Color(0xFFFFD54F))

        // White-hot flame spark tip
        drawCircle(Color.White, radius = 2.5f, center = Offset(bPos.x + flameLean, bPos.y - flameHeight + 2f))

        // Rising Embers in 4 steps
        val emberStep = (frame + bIdx * 2) % 4
        val emberY = bPos.y - flameHeight - (emberStep * 14f)
        val emberX = bPos.x + flameLean * 1.5f + (emberStep * -4f)
        drawCircle(Color(0xFFFFCA28), radius = 2f, center = Offset(emberX, emberY))
    }

    // 5. Stone Rampart Floor with Perspective Masonry Joint Lines
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF263238),
                Color(0xFF1E272C),
                Color(0xFF141A1E)
            ),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )

    // Perspective Flagstone joints
    val floorLines = listOf(0.25f, 0.55f, 0.85f)
    floorLines.forEach { frac ->
        val y = horizonY + (h - horizonY) * frac
        drawLine(Color(0xFF37474F), start = Offset(0f, y), end = Offset(w, y), strokeWidth = 2f)
    }
}

// =========================================================================
// 3. DUNGEON SCENE (Ashwood Sanctum)
// Vaulted stone arches, flickering wall sconces, shifting shadows, pulsing cyan runes
// =========================================================================
private fun DrawScope.drawDungeonScene(w: Float, h: Float, horizonY: Float, frame: Int) {
    // 1. Back Stone Masonry Wall (Ancient dark granite blocks)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF12151D),
                Color(0xFF1A1F2B),
                Color(0xFF141822)
            ),
            startY = 0f,
            endY = horizonY
        ),
        size = Size(w, horizonY)
    )

    // Brick mortar lines on back wall
    val rowH = 22f
    var currY = 0f
    var rowIndex = 0
    while (currY < horizonY) {
        drawLine(Color(0x2237474F), start = Offset(0f, currY), end = Offset(w, currY), strokeWidth = 1.5f)
        val colOffset = if (rowIndex % 2 == 0) 0f else 28f
        var cx = colOffset
        while (cx < w) {
            drawLine(Color(0x2237474F), start = Offset(cx, currY), end = Offset(cx, currY + rowH), strokeWidth = 1.5f)
            cx += 56f
        }
        currY += rowH
        rowIndex++
    }

    // 2. Vaulted Gothic Arch Columns (Left, Center, Right)
    val colWidth = 24f
    listOf(w * 0.05f, w * 0.48f, w * 0.90f).forEach { colX ->
        drawRect(Color(0xFF263238), topLeft = Offset(colX, 0f), size = Size(colWidth, horizonY))
        drawRect(Color(0xFF192226), topLeft = Offset(colX + colWidth - 4f, 0f), size = Size(4f, horizonY)) // Column shadow
    }

    // 3. Ancient Cyan Logos Runes (Pulsing luminosity across the 4 frames)
    val runeAlpha = when (frame) {
        0 -> 0.45f
        1 -> 0.75f // Pulsing brighter as torch dips
        2 -> 1.0f  // Peak celestial surge
        else -> 0.60f
    }
    val runeColor = Color(0xFF00E5FF).copy(alpha = runeAlpha)
    // Runes carved vertically along center pillar
    val runeYs = listOf(35f, 65f, 95f, 125f)
    runeYs.forEach { ry ->
        drawCircle(runeColor, radius = 4f, center = Offset(w * 0.48f + colWidth / 2f, ry))
        drawLine(runeColor, start = Offset(w * 0.48f + 4f, ry), end = Offset(w * 0.48f + colWidth - 4f, ry), strokeWidth = 2f)
    }

    // 4. Wall Sconce Torches with 4-Stage Flame & Moving Light Radius
    // Frame 0: steady, Frame 1: dims low, Frame 2: flares bright amber, Frame 3: calm ember
    val torchPositions = listOf(Offset(w * 0.22f, horizonY - 75f), Offset(w * 0.76f, horizonY - 75f))
    torchPositions.forEachIndexed { tIdx, tPos ->
        // Iron bracket
        drawRect(Color(0xFF212121), topLeft = Offset(tPos.x - 3f, tPos.y), size = Size(6f, 18f))
        drawLine(Color(0xFF424242), start = Offset(tPos.x, tPos.y + 12f), end = Offset(tPos.x + 8f, tPos.y - 2f), strokeWidth = 2f)

        // Dynamic Light Radius & Brightness (Lights go dimmer in frame 1, flare bright in frame 2!)
        val lightRadius = when (frame) {
            0 -> 140f
            1 -> 95f  // Significantly dimmer!
            2 -> 210f // Bright flare!
            else -> 155f
        }
        val lightAlpha = when (frame) {
            0 -> 0.35f
            1 -> 0.18f // Dim amber
            2 -> 0.55f // High-contrast radiant wash
            else -> 0.38f
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFB300).copy(alpha = lightAlpha), Color(0xFFFF6F00).copy(alpha = lightAlpha * 0.4f), Color.Transparent),
                center = tPos,
                radius = lightRadius
            ),
            radius = lightRadius,
            center = tPos
        )

        // Flame shape
        val fHeight = when (frame) {
            0 -> 20f
            1 -> 13f // Dim low flame
            2 -> 28f // Tall roaring flame
            else -> 18f
        }
        val fShift = if ((frame + tIdx) % 2 == 0) -2f else 3f

        val flamePath = Path().apply {
            moveTo(tPos.x - 6f, tPos.y)
            lineTo(tPos.x + fShift, tPos.y - fHeight)
            lineTo(tPos.x + 6f, tPos.y)
            close()
        }
        drawPath(flamePath, Color(0xFFFF5722))
        drawCircle(Color(0xFFFFD54F), radius = 4f, center = Offset(tPos.x, tPos.y - 4f))
    }

    // 5. Cold Flagstone Floor with Perspective Grid & Moving Pillar Shadows
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF141720),
                Color(0xFF0D1017),
                Color(0xFF080A0E)
            ),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )

    // Dynamic Floor Shadows adjusting according to torch flare
    // When light is bright (frame 2), shadows are sharp and skewed; when dim (frame 1), shadows are long
    val shadowSkew = when (frame) {
        0 -> 0f
        1 -> 15f  // Skewed long
        2 -> -12f // Shifted angle
        else -> 4f
    }
    // Pillar shadows cast onto the floor
    listOf(w * 0.05f, w * 0.48f, w * 0.90f).forEach { colX ->
        val sPath = Path().apply {
            moveTo(colX, horizonY)
            lineTo(colX + colWidth, horizonY)
            lineTo(colX + colWidth + shadowSkew, h)
            lineTo(colX + shadowSkew, h)
            close()
        }
        drawPath(sPath, Color(0x33000000))
    }

    // Flagstone horizontal perspective lines
    val stoneLines = listOf(0.30f, 0.62f, 0.90f)
    stoneLines.forEach { frac ->
        val y = horizonY + (h - horizonY) * frac
        drawLine(Color(0xFF263238), start = Offset(0f, y), end = Offset(w, y), strokeWidth = 1.5f)
    }
}

// =========================================================================
// 4. CAVE SCENE (Void Hollows)
// Stalactites, 4-stage water droplet & expanding puddle ripples, pulsing amethyst crystals
// =========================================================================
private fun DrawScope.drawCaveScene(w: Float, h: Float, horizonY: Float, frame: Int) {
    // 1. Deep Cavern Darkness
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF08050C),
                Color(0xFF110B1A),
                Color(0xFF1A0F26)
            ),
            startY = 0f,
            endY = horizonY
        ),
        size = Size(w, horizonY)
    )

    // Craggy jagged cavern ceiling with stalactites
    val stalactitePath = Path().apply {
        moveTo(0f, 0f)
        lineTo(0f, 40f)
        lineTo(w * 0.15f, 75f)
        lineTo(w * 0.25f, 25f)
        lineTo(w * 0.40f, 95f) // Major stalactite that drips!
        lineTo(w * 0.50f, 30f)
        lineTo(w * 0.68f, 80f)
        lineTo(w * 0.82f, 20f)
        lineTo(w * 0.92f, 70f)
        lineTo(w, 35f)
        lineTo(w, 0f)
        close()
    }
    drawPath(stalactitePath, Color(0xFF1C1327))
    drawPath(stalactitePath, Color(0xFF2D1E40), style = Stroke(width = 2f))

    // 2. 4-Stage Water Droplet & Ripple Cycle from Major Stalactite (w * 0.40f)
    val dripX = w * 0.40f
    val stalactiteTipY = 95f
    val puddleY = horizonY + (h - horizonY) * 0.45f

    when (frame) {
        0 -> {
            // Frame 0: Water bead swelling at stalactite tip
            drawCircle(Color(0xFF80DEEA), radius = 3.5f, center = Offset(dripX, stalactiteTipY + 2f))
        }
        1 -> {
            // Frame 1: Droplet falling through mid-air
            val dropY = stalactiteTipY + (puddleY - stalactiteTipY) * 0.55f
            drawLine(Color(0xFF80DEEA), start = Offset(dripX, dropY - 8f), end = Offset(dripX, dropY), strokeWidth = 2.5f)
        }
        2 -> {
            // Frame 2: Splash impact! Sharp splash star on puddle surface
            drawCircle(Color(0xFFE0F7FA), radius = 4f, center = Offset(dripX, puddleY))
            drawLine(Color(0xFF80DEEA), start = Offset(dripX - 8f, puddleY - 4f), end = Offset(dripX + 8f, puddleY - 4f), strokeWidth = 1.5f)
            drawLine(Color(0xFF80DEEA), start = Offset(dripX - 5f, puddleY - 8f), end = Offset(dripX + 5f, puddleY - 8f), strokeWidth = 1.5f)
        }
        3 -> {
            // Frame 3: Concentric expanding ripple rings across the dark mineral pool
            drawOval(
                color = Color(0x6680DEEA),
                topLeft = Offset(dripX - 22f, puddleY - 7f),
                size = Size(44f, 14f),
                style = Stroke(width = 1.5f)
            )
            drawOval(
                color = Color(0x3380DEEA),
                topLeft = Offset(dripX - 38f, puddleY - 12f),
                size = Size(76f, 24f),
                style = Stroke(width = 1.5f)
            )
        }
    }

    // 3. Glowing Amethyst & Cyan Crystal Clusters with Specular Glints
    val crystalClusters = listOf(
        Pair(Offset(w * 0.18f, horizonY - 45f), Color(0xFFBA68C8)), // Amethyst
        Pair(Offset(w * 0.82f, horizonY - 55f), Color(0xFF00E5FF))  // Cyan
    )
    crystalClusters.forEachIndexed { cIdx, (cPos, cColor) ->
        // Ambient aura pulse
        val auraPulse = when ((frame + cIdx) % 4) {
            0 -> 0.35f
            1 -> 0.70f // Specular flash
            2 -> 0.95f // Peak radiance
            else -> 0.50f
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(cColor.copy(alpha = auraPulse), Color.Transparent),
                center = cPos,
                radius = 70f
            ),
            radius = 70f,
            center = cPos
        )

        // Geometric Crystal Spikes
        val crystalPath = Path().apply {
            moveTo(cPos.x - 8f, cPos.y + 14f)
            lineTo(cPos.x - 2f, cPos.y - 22f)
            lineTo(cPos.x + 6f, cPos.y + 14f)
            close()
        }
        drawPath(crystalPath, cColor)

        val crystalPath2 = Path().apply {
            moveTo(cPos.x + 2f, cPos.y + 14f)
            lineTo(cPos.x + 12f, cPos.y - 14f)
            lineTo(cPos.x + 18f, cPos.y + 14f)
            close()
        }
        drawPath(crystalPath2, cColor.copy(alpha = 0.8f))

        // 4-point Specular Star Flare in frame 1 & 2
        if ((frame + cIdx) % 4 == 1 || (frame + cIdx) % 4 == 2) {
            val starCenter = Offset(cPos.x - 2f, cPos.y - 22f)
            drawCircle(Color.White, radius = 2.5f, center = starCenter)
            drawLine(Color.White, start = Offset(starCenter.x - 6f, starCenter.y), end = Offset(starCenter.x + 6f, starCenter.y), strokeWidth = 1.5f)
            drawLine(Color.White, start = Offset(starCenter.x, starCenter.y - 6f), end = Offset(starCenter.x, starCenter.y + 6f), strokeWidth = 1.5f)
        }
    }

    // 4. Craggy Cave Floor with Dark Mineral Puddle
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF140D1F),
                Color(0xFF0F0917),
                Color(0xFF09050E)
            ),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )

    // Dark Mineral Pool on Floor
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF1A1F38), Color(0xFF0A0F20)),
            center = Offset(dripX, puddleY),
            radius = 90f
        ),
        topLeft = Offset(dripX - 85f, puddleY - 26f),
        size = Size(170f, 52f)
    )
}

// =========================================================================
// 5. SWAMP SCENE (The Sunken Mire)
// Murky bog pools, bobbing will-o'-the-wisps, swaying marsh cattails, shifting green mist
// =========================================================================
private fun DrawScope.drawSwampScene(w: Float, h: Float, horizonY: Float, frame: Int) {
    // 1. Murky Eerie Swamp Sky
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0A140F),
                Color(0xFF12241C),
                Color(0xFF1A3326)
            ),
            startY = 0f,
            endY = horizonY
        ),
        size = Size(w, horizonY)
    )

    // Gnarled Dead Mangrove Tree Trunks with Draped Spanish Moss
    val treeX = w * 0.12f
    drawRect(Color(0xFF1E281F), topLeft = Offset(treeX, horizonY - 110f), size = Size(16f, 110f))
    // Twisted branches
    drawLine(Color(0xFF1E281F), start = Offset(treeX + 8f, horizonY - 90f), end = Offset(treeX - 25f, horizonY - 120f), strokeWidth = 5f)
    drawLine(Color(0xFF1E281F), start = Offset(treeX + 8f, horizonY - 70f), end = Offset(treeX + 35f, horizonY - 105f), strokeWidth = 5f)

    // Draped Moss Swaying in Breeze (4 Frames)
    val mossSway = when (frame) {
        0 -> 0f
        1 -> 4f
        2 -> 8f
        else -> 3f
    }
    drawLine(Color(0xFF335C45), start = Offset(treeX - 25f, horizonY - 120f), end = Offset(treeX - 25f + mossSway, horizonY - 70f), strokeWidth = 3f)
    drawLine(Color(0xFF284837), start = Offset(treeX + 35f, horizonY - 105f), end = Offset(treeX + 35f + mossSway, horizonY - 60f), strokeWidth = 3.5f)

    // 2. Murky Bog Water Ground Plane
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF14291E),
                Color(0xFF0F1E16),
                Color(0xFF0A140E)
            ),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )

    // Water Pool Reflective Sheen
    drawOval(
        Color(0x3300E676),
        topLeft = Offset(w * 0.35f, horizonY + 15f),
        size = Size(w * 0.55f, 48f)
    )

    // 3. Swamp Gas Bubble Popping & Ripple Cycle (Center Pool)
    val bubbleX = w * 0.60f
    val bubbleY = horizonY + 35f
    when (frame) {
        0 -> {
            // Frame 0: Small bubble rising
            drawCircle(Color(0xFF69F0AE), radius = 3.5f, center = Offset(bubbleX, bubbleY))
        }
        1 -> {
            // Frame 1: Bubble fully inflated
            drawCircle(Color(0xFFB9F6CA), radius = 6.5f, center = Offset(bubbleX, bubbleY))
            drawCircle(Color.White, radius = 2f, center = Offset(bubbleX - 2f, bubbleY - 2f)) // Specular
        }
        2 -> {
            // Frame 2: Bubble pops! Starburst splash
            drawCircle(Color(0xFF00E676), radius = 2f, center = Offset(bubbleX, bubbleY))
            drawLine(Color(0xFF69F0AE), start = Offset(bubbleX - 8f, bubbleY), end = Offset(bubbleX + 8f, bubbleY), strokeWidth = 1.5f)
            drawLine(Color(0xFF69F0AE), start = Offset(bubbleX, bubbleY - 8f), end = Offset(bubbleX, bubbleY + 8f), strokeWidth = 1.5f)
        }
        3 -> {
            // Frame 3: Expanding faint green water ripple ring
            drawOval(
                color = Color(0x5569F0AE),
                topLeft = Offset(bubbleX - 22f, bubbleY - 6f),
                size = Size(44f, 12f),
                style = Stroke(width = 1.5f)
            )
        }
    }

    // 4. Marsh Cattails & Reeds Swaying (Tilt angle cycles across 4 frames)
    val reedTilt = when (frame) {
        0 -> 0f
        1 -> 4f
        2 -> 9f
        else -> 3f
    }
    listOf(0.24f, 0.30f, 0.78f, 0.84f).forEach { rFrac ->
        val rx = w * rFrac
        val ry = horizonY + 28f
        // Stem
        drawLine(Color(0xFF2E4F39), start = Offset(rx, ry), end = Offset(rx + reedTilt, ry - 32f), strokeWidth = 2.5f)
        // Brown cattail sausage tip
        drawLine(Color(0xFF5D4037), start = Offset(rx + reedTilt, ry - 32f), end = Offset(rx + reedTilt * 1.2f, ry - 18f), strokeWidth = 5f)
    }

    // 5. Bioluminescent Will-o'-the-Wisps (Hovering & Bobbing across 4 frames)
    val wisps = listOf(
        Pair(w * 0.32f, horizonY - 45f),
        Pair(w * 0.72f, horizonY - 25f)
    )
    wisps.forEachIndexed { wIdx, (wx, wy) ->
        val bobY = when ((frame + wIdx * 2) % 4) {
            0 -> 0f
            1 -> -8f
            2 -> -14f // Peak float
            else -> -6f
        }
        val wispCenter = Offset(wx + sin(frame.toFloat()) * 4f, wy + bobY)
        val wispPulse = if (frame % 2 == 0) 1.25f else 0.85f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.8f * wispPulse), Color(0x3300E676), Color.Transparent),
                center = wispCenter,
                radius = 28f * wispPulse
            ),
            radius = 28f * wispPulse,
            center = wispCenter
        )
        drawCircle(Color(0xFFE0F7FA), radius = 3.5f * wispPulse, center = wispCenter)
    }
}

// =========================================================================
// Ground Contact Shadows Beneath Characters
// Anchors the heroes and enemies to the perspective floor plane
// =========================================================================
private fun DrawScope.drawGroundContactShadows(
    w: Float,
    h: Float,
    horizonY: Float,
    environment: BattleEnvironment,
    frame: Int
) {
    // Dynamic shadow shift based on environmental lighting
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

    val shadowColor = Color(0x44000000)

    // 4 Party Members Shadow Positions (Left Flank)
    for (i in 0..3) {
        val heroShadowX = w * 0.22f + shadowOffsetX
        val heroShadowY = horizonY + (h - horizonY) * (0.22f + i * 0.22f)
        drawOval(
            color = shadowColor,
            topLeft = Offset(heroShadowX - 24f, heroShadowY - 6f),
            size = Size(48f, 12f)
        )
    }

    // 3 Enemy Shadows Positions (Right Flank)
    for (i in 0..2) {
        val enemyShadowX = w * 0.78f + shadowOffsetX
        val enemyShadowY = horizonY + (h - horizonY) * (0.26f + i * 0.28f)
        drawOval(
            color = shadowColor,
            topLeft = Offset(enemyShadowX - 28f, enemyShadowY - 7f),
            size = Size(56f, 14f)
        )
    }
}
