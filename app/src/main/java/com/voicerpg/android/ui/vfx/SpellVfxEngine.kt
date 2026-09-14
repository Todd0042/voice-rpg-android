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
import com.voicerpg.android.model.SpellSchool
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

    if (p.isHeal) {
        // Celestial Pillar of Healing Light descending from top
        val pillarWidth = 70f
        val pillarHeight = size.height * t
        drawRect(
            color = Color(0x66FFD700),
            topLeft = Offset(p.targetX - pillarWidth / 2f, 0f),
            size = Size(pillarWidth, pillarHeight)
        )
        drawRect(
            color = Color(0xAA69F0AE),
            topLeft = Offset(p.targetX - pillarWidth / 4f, 0f),
            size = Size(pillarWidth / 2f, pillarHeight)
        )
        return
    }

    // Trajectory from caster to target
    val curX = p.startX + (p.targetX - p.startX) * t
    // Parabolic arc for fireball/arrows
    val arcHeight = if (p.school == SpellSchool.PYROMANCY) -120f * sin(t * Math.PI.toFloat()) else 0f
    val curY = p.startY + (p.targetY - p.startY) * t + arcHeight

    when (p.school) {
        SpellSchool.PYROMANCY -> {
            // Soaring Fireball with smoke trail
            drawCircle(color = Color(0xFFFFD54F), radius = 14f, center = Offset(curX, curY))
            drawCircle(color = Color(0xFFFF3D00), radius = 9f, center = Offset(curX, curY))
            drawCircle(color = Color.White, radius = 5f, center = Offset(curX, curY))

            // Trailing embers
            for (i in 1..4) {
                val trailT = (t - i * 0.04f).coerceAtLeast(0f)
                val trailX = p.startX + (p.targetX - p.startX) * trailT
                val trailY = p.startY + (p.targetY - p.startY) * trailT - 100f * sin(trailT * Math.PI.toFloat())
                drawCircle(color = Color(0xFFFF6E40).copy(alpha = 1f - i * 0.2f), radius = 7f - i, center = Offset(trailX, trailY))
            }
        }
        SpellSchool.CRYOMANCY -> {
            // Crystalline Frost Lance
            drawRect(color = Color(0xFF00E5FF), topLeft = Offset(curX - 8f, curY - 4f), size = Size(20f, 8f))
            drawRect(color = Color.White, topLeft = Offset(curX - 4f, curY - 2f), size = Size(10f, 4f))
        }
        SpellSchool.ELECTROMANCY -> {
            // Jagged Lightning Arc
            val path = Path().apply {
                moveTo(p.startX, p.startY)
                var lx = p.startX
                var ly = p.startY
                for (step in 1..5) {
                    val st = step / 5f
                    val nx = p.startX + (p.targetX - p.startX) * st + (Random.nextFloat() * 40f - 20f)
                    val ny = p.startY + (p.targetY - p.startY) * st + (Random.nextFloat() * 40f - 20f)
                    lineTo(nx, ny)
                    lx = nx
                    ly = ny
                }
                lineTo(p.targetX, p.targetY)
            }
            drawPath(path, color = Color(0xFFE040FB))
            drawPath(path, color = Color.White)
        }
        SpellSchool.SHADOW -> {
            // Shadow Dash & Twin Slash
            drawCircle(color = Color(0xFF7C4DFF).copy(alpha = 0.8f), radius = 12f, center = Offset(curX, curY))
            drawLine(
                color = Color(0xFFE040FB),
                start = Offset(curX - 25f, curY - 25f),
                end = Offset(curX + 25f, curY + 25f),
                strokeWidth = 4f
            )
        }
        else -> {
            drawCircle(color = Color.White, radius = 8f, center = Offset(curX, curY))
        }
    }
}
