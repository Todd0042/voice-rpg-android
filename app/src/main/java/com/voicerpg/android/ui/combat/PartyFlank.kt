package com.voicerpg.android.ui.combat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voicerpg.android.model.PartyMember
import com.voicerpg.android.ui.sprites.PixelCharacterView

@Composable
fun PartyFlank(
    party: List<PartyMember>,
    activePartyMemberId: String?,
    onSelectHero: (PartyMember) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(start = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        party.forEach { member ->
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
                onClick = { onSelectHero(member) }
            )
        }
    }
}
