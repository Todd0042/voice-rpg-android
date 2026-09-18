package com.voicerpg.android.ui.combat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.voicerpg.android.model.PartyMember
import com.voicerpg.android.ui.sprites.PixelCharacterView

@Composable
fun PartyFlank(
    party: List<PartyMember>,
    activePartyMemberId: String?,
    onSelectHero: (PartyMember) -> Unit,
    onPositionHero: ((String, Offset) -> Unit)? = null,
    arenaCoordinates: LayoutCoordinates? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(start = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        party.forEach { member ->
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
                    onPositionHero?.invoke(member.id, center)
                }
            }

            PixelCharacterView(
                characterId = member.id,
                name = member.name,
                subtitle = member.loreClass,
                hpRatio = member.hpRatio,
                currentHp = member.currentHp,
                maxHp = member.maxHp,
                mpRatio = member.mpRatio,
                atbRatio = member.atbRatio,
                isTurnReady = member.isTurnReady,
                isActiveTurn = member.id == activePartyMemberId,
                stance = member.stance,
                isFlippedHorizontally = false,
                onClick = { onSelectHero(member) },
                modifier = Modifier.onGloballyPositioned { cardCoords = it }
            )
        }
    }
}
