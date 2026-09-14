package com.voicerpg.android.ui.combat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voicerpg.android.model.CharacterStance
import com.voicerpg.android.model.Enemy
import com.voicerpg.android.ui.sprites.PixelCharacterView

@Composable
fun EnemyFlank(
    enemies: List<Enemy>,
    onSelectEnemy: (Enemy) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.End
    ) {
        enemies.forEach { enemy ->
            val stance = when {
                !enemy.isAlive -> CharacterStance.DEAD
                enemy.isDamagedFlash -> CharacterStance.DAMAGED
                else -> CharacterStance.READY
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
                onClick = if (enemy.isAlive) { { onSelectEnemy(enemy) } } else null
            )
        }
    }
}
