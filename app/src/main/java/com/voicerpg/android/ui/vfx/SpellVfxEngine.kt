package com.voicerpg.android.ui.vfx

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.voicerpg.android.model.ResonanceTier
import com.voicerpg.android.model.SpellSchool
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class ActiveSpellProjectile(
    val id: Long = System.nanoTime(),
    val startX: Float,
    val startY: Float,
    val targetX: Float,
    val targetY: Float,
    val school: SpellSchool,
    val isHeal: Boolean,
    val bonusPercent: Int = 0,
    val tier: ResonanceTier = ResonanceTier.BASIC,
    var progress: Float = 0f,
    val onImpact: () -> Unit
)

class SpellVfxEngine {
    val projectiles = mutableStateListOf<ActiveSpellProjectile>()

    fun launch(
        startX: Float,
        startY: Float,
        targetX: Float,
        targetY: Float,
        school: SpellSchool,
        isHeal: Boolean,
        bonusPercent: Int = 0,
        tier: ResonanceTier = ResonanceTier.BASIC,
        onImpact: () -> Unit
    ) {
        projectiles.add(
            ActiveSpellProjectile(
                startX = startX,
                startY = startY,
                targetX = targetX,
                targetY = targetY,
                school = school,
                isHeal = isHeal,
                bonusPercent = bonusPercent,
                tier = tier,
                onImpact = onImpact
            )
        )
    }

    fun update(deltaProgress: Float = 0.045f) {
        val iterator = projectiles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.progress += deltaProgress
            if (p.progress >= 1.0f) {
                p.onImpact()
                iterator.remove()
            }
        }
    }
}

@Composable
fun SpellVfxCanvas(
    engine: SpellVfxEngine,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(engine) {
        while (true) {
            withFrameNanos {
                engine.update()
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        for (p in engine.projectiles) {
            renderProjectile(p)
        }
    }
}

private fun DrawScope.renderProjectile(p: ActiveSpellProjectile) {
    val t = p.progress.coerceIn(0f, 1f)
    val isSuper = p.bonusPercent >= 100
    val isTranscendental = p.bonusPercent >= 155
    val sizeScale = 1.0f + 1.5f * (p.bonusPercent / 200.0f)

    if (p.isHeal) {
        // Celestial Cathedral Pillar of Light
        val pillarWidth = if (isTranscendental) 150f else if (isSuper) 110f else 70f
        val pillarHeight = size.height * t

        // Outer holy radiance
        drawRect(
            color = Color(0x44FFD700),
            topLeft = Offset(p.targetX - pillarWidth / 2f, 0f),
            size = Size(pillarWidth, pillarHeight)
        )
        // Inner divine green/white shaft
        drawRect(
            color = Color(0xAA69F0AE),
            topLeft = Offset(p.targetX - pillarWidth / 4f, 0f),
            size = Size(pillarWidth / 2f, pillarHeight)
        )

        // Descending Golden Halos for high resonance heals
        if (isSuper) {
            val haloY = pillarHeight * 0.85f
            drawOval(
                color = Color(0xFFFFD54F),
                topLeft = Offset(p.targetX - pillarWidth * 0.45f, haloY - 12f),
                size = Size(pillarWidth * 0.9f, 24f),
                style = Stroke(width = 4f)
            )
        }
        return
    }

    // Projectile coordinates along trajectory
    val curX = p.startX + (p.targetX - p.startX) * t
    val arcHeight = if (p.school == SpellSchool.PYROMANCY) -120f * sin(t * Math.PI.toFloat()) else 0f
    val curY = p.startY + (p.targetY - p.startY) * t + arcHeight

    when (p.school) {
        SpellSchool.PYROMANCY -> {
            val baseRadius = 14f * sizeScale

            // 1. Rotating Arcane Rune Rings for Mythic/Transcendental
            if (isSuper) {
                val orbitAngle = (t * 12f).toDouble()
                val orbitRadius = baseRadius * 1.6f
                drawCircle(
                    color = Color(0x88FFD54F),
                    radius = orbitRadius,
                    center = Offset(curX, curY),
                    style = Stroke(width = 2.5f)
                )
                // Orbiting celestial plasma motes
                for (k in 0..2) {
                    val moteAngle = orbitAngle + (k * 2.0 * Math.PI / 3.0)
                    val mx = curX + (cos(moteAngle) * orbitRadius).toFloat()
                    val my = curY + (sin(moteAngle) * orbitRadius).toFloat()
                    drawCircle(Color(0xFFFFD54F), radius = 5f, center = Offset(mx, my))
                }
            }

            // 2. Multi-Stage Solar Core
            if (isTranscendental) {
                // Outer heat distortion aura
                drawCircle(Color(0x33E040FB), radius = baseRadius * 2.1f, center = Offset(curX, curY))
            }
            drawCircle(Color(0xFFFF3D00).copy(alpha = 0.85f), radius = baseRadius * 1.3f, center = Offset(curX, curY))
            drawCircle(Color(0xFFFFD54F), radius = baseRadius * 0.9f, center = Offset(curX, curY))
            drawCircle(Color.White, radius = baseRadius * 0.5f, center = Offset(curX, curY))

            // 3. Dense Comet Tail
            val trailCount = if (isTranscendental) 8 else if (isSuper) 6 else 4
            for (i in 1..trailCount) {
                val trailT = (t - i * 0.035f).coerceAtLeast(0f)
                val trailX = p.startX + (p.targetX - p.startX) * trailT
                val trailY = p.startY + (p.targetY - p.startY) * trailT - 100f * sin(trailT * Math.PI.toFloat())
                val alpha = (1f - i.toFloat() / trailCount).coerceIn(0.1f, 1f)
                val rad = (baseRadius * 0.7f) - (i * 1.5f).coerceAtLeast(2f)
                drawCircle(Color(0xFFFF6E40).copy(alpha = alpha), radius = rad, center = Offset(trailX, trailY))
            }
        }

        SpellSchool.CRYOMANCY -> {
            val length = 24f * sizeScale
            val height = 10f * sizeScale

            // Glacial Lance
            drawRect(color = Color(0xFF00E5FF), topLeft = Offset(curX - length / 2f, curY - height / 2f), size = Size(length, height))
            drawRect(color = Color.White, topLeft = Offset(curX - length / 4f, curY - height / 4f), size = Size(length / 2f, height / 2f))

            if (isSuper) {
                // Frost nova rings expanding along path
                drawOval(
                    color = Color(0x6680DEEA),
                    topLeft = Offset(curX - length, curY - height * 1.5f),
                    size = Size(length * 2f, height * 3f),
                    style = Stroke(width = 2f)
                )
            }
        }

        SpellSchool.ELECTROMANCY -> {
            // Multi-branching Lightning Tempest
            val branchCount = if (isSuper) 4 else 2
            for (b in 0 until branchCount) {
                val path = Path().apply {
                    moveTo(p.startX, p.startY)
                    for (step in 1..5) {
                        val st = step / 5f
                        val jitter = if (isSuper) 45f else 25f
                        val nx = p.startX + (p.targetX - p.startX) * st + (Random.nextFloat() * jitter * 2 - jitter)
                        val ny = p.startY + (p.targetY - p.startY) * st + (Random.nextFloat() * jitter * 2 - jitter)
                        lineTo(nx, ny)
                    }
                    lineTo(p.targetX, p.targetY)
                }
                drawPath(path, color = if (b == 0) Color(0xFFE040FB) else Color(0xFF00E5FF), style = Stroke(width = if (isSuper) 4f else 2.5f))
                drawPath(path, color = Color.White, style = Stroke(width = 1.5f))
            }
        }

        SpellSchool.SHADOW -> {
            val radius = 14f * sizeScale
            drawCircle(color = Color(0xFF7C4DFF).copy(alpha = 0.85f), radius = radius, center = Offset(curX, curY))
            // Cross slash lines
            val slashLen = 25f * sizeScale
            drawLine(
                color = Color(0xFF00E676),
                start = Offset(curX - slashLen, curY - slashLen),
                end = Offset(curX + slashLen, curY + slashLen),
                strokeWidth = 3f * sizeScale
            )
            drawLine(
                color = Color(0xFFE040FB),
                start = Offset(curX - slashLen, curY + slashLen),
                end = Offset(curX + slashLen, curY - slashLen),
                strokeWidth = 3f * sizeScale
            )
        }

        else -> {
            drawCircle(color = Color.White, radius = 10f * sizeScale, center = Offset(curX, curY))
        }
    }
}
