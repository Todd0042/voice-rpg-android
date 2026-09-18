package com.voicerpg.android.ui.combat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.voicerpg.android.model.CharacterStance
import com.voicerpg.android.model.Enemy
import com.voicerpg.android.ui.sprites.PixelCharacterView

@Composable
fun EnemyFlank(
    enemies: List<Enemy>,
    onSelectEnemy: (Enemy) -> Unit,
    onPositionEnemy: ((String, Offset) -> Unit)? = null,
    arenaCoordinates: LayoutCoordinates? = null,
    modifier: Modifier = Modifier
) {
    val isLargeHorde = enemies.size > 3

    if (!isLargeHorde) {
        // Standard Classic Layout (1 to 3 enemies)
        Column(
            modifier = modifier.padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.End
        ) {
            enemies.forEach { enemy ->
                EnemyCard(
                    enemy = enemy,
                    isCompact = false,
                    onSelectEnemy = onSelectEnemy,
                    onPositionEnemy = onPositionEnemy,
                    arenaCoordinates = arenaCoordinates
                )
            }
        }
    } else {
        // 2-Column Staggered JRPG Formation (4 to 6 enemies)
        // Front row (closer to heroes) and Back row (rearguard)
        val frontRow = enemies.filterIndexed { index, _ -> index % 2 == 0 }
        val backRow = enemies.filterIndexed { index, _ -> index % 2 != 0 }

        Row(
            modifier = modifier.padding(end = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Front Row (closer to heroes)
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                frontRow.forEach { enemy ->
                    EnemyCard(
                        enemy = enemy,
                        isCompact = true,
                        onSelectEnemy = onSelectEnemy,
                        onPositionEnemy = onPositionEnemy,
                        arenaCoordinates = arenaCoordinates
                    )
                }
            }

            // Back Row (rearguard)
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                backRow.forEach { enemy ->
                    EnemyCard(
                        enemy = enemy,
                        isCompact = true,
                        onSelectEnemy = onSelectEnemy,
                        onPositionEnemy = onPositionEnemy,
                        arenaCoordinates = arenaCoordinates
                    )
                }
            }
        }
    }
}

@Composable
private fun EnemyCard(
    enemy: Enemy,
    isCompact: Boolean,
    onSelectEnemy: (Enemy) -> Unit,
    onPositionEnemy: ((String, Offset) -> Unit)? = null,
    arenaCoordinates: LayoutCoordinates? = null
) {
    val stance = when {
        !enemy.isAlive -> CharacterStance.DEAD
        enemy.isDamagedFlash -> CharacterStance.DAMAGED
        else -> CharacterStance.READY
    }

    var cardCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    LaunchedEffect(cardCoords, arenaCoordinates) {
        val card = cardCoords
        val arena = arenaCoordinates
        if (card != null && arena != null && card.isAttached && arena.isAttached) {
            val pos = arena.localPositionOf(card, Offset.Zero)
            val center = Offset(
                pos.x + card.size.width / 2f,
                pos.y + card.size.height / 2f
            )
            onPositionEnemy?.invoke(enemy.id, center)
        }
    }

    PixelCharacterView(
        characterId = enemy.id,
        name = enemy.name,
        subtitle = enemy.subtitle,
        hpRatio = enemy.hpRatio,
        currentHp = enemy.currentHp,
        maxHp = enemy.maxHp,
        atbRatio = enemy.atbRatio,
        isTurnReady = enemy.isTurnReady,
        isActiveTurn = false,
        isTargeted = enemy.isAlive && enemy.isTargeted,
        stance = stance,
        isFlippedHorizontally = true,
        pixelSize = if (isCompact) 1.65.dp else 2.2.dp,
        onClick = if (enemy.isAlive) { { onSelectEnemy(enemy) } } else null,
        modifier = Modifier.onGloballyPositioned { cardCoords = it }
    )
}
