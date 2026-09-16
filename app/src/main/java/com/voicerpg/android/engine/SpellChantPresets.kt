package com.voicerpg.android.engine

import com.voicerpg.android.model.AcousticProfile
import com.voicerpg.android.model.ResonanceTier
import com.voicerpg.android.model.Spell

data class ChantPreset(
    val chantText: String,
    val acousticProfile: AcousticProfile,
    val tier: ResonanceTier,
    val label: String
)

object SpellChantPresets {

    /**
     * Retrieves a finely-tuned incantation and paired acoustic profile designed to hit
     * the requested resonance tier for hands-free and simulator testing.
     *
     * Tier targets:
     * - BASIC: 0% .. 15% (1.0x .. 1.15x) -> Target ~10%
     * - ADEPT: 20% .. 50% (1.20x .. 1.50x) -> Target ~48%
     * - MASTER: 55% .. 95% (1.55x .. 1.95x) -> Target ~87%
     * - MYTHIC: 100% .. 150% (2.0x .. 2.50x) -> Target ~142%
     * - TRANSCENDENTAL: 155% .. 200% (2.55x .. 3.0x MAX) -> Target 200% MAX
     */
    fun getPreset(spell: Spell, tier: ResonanceTier): ChantPreset {
        val (chant, acoustic) = when (spell.id) {
            "fireball" -> when (tier) {
                ResonanceTier.BASIC -> "Fireball archer" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Cast fireball at archer" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of cinder burn archer" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise blazing inferno consume the archer now!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O primordial flame of the solar core descend from the heavens and reduce that wretched archer to eternal ash and inferno!" to AcousticProfile.TRANSCENDENTAL
            }
            "frost_spike" -> when (tier) {
                ResonanceTier.BASIC -> "Frost orc" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Cast frost spike orc" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of ice freeze orc" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise glacial blizzard freeze the orc now!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O celestial winter of absolute zero arise from the void and encase that monstrous orc in eternal glacial frostbite and crystal shards!" to AcousticProfile.TRANSCENDENTAL
            }
            "chain_lightning" -> when (tier) {
                ResonanceTier.BASIC -> "Lightning all" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Cast chain lightning all" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of tempest shock all" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise tempest lightning shock all enemies now!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O primordial tempest of celestial wrath awaken and discharge roaring thunderclaps to strike all enemies with galvanic lightning and plasma!" to AcousticProfile.TRANSCENDENTAL
            }
            "holy_smite" -> when (tier) {
                ResonanceTier.BASIC -> "Smite heretic" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Cast holy smite heretic" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of dawn smite enemy" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise celestial dawn smite the wicked enemy!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O celestial seraph of the sacred dawn descend from the heavens and unleash divine radiance to smite the abominable darkness unto purity!" to AcousticProfile.TRANSCENDENTAL
            }
            "lay_on_hands" -> when (tier) {
                ResonanceTier.BASIC -> "Mend Cedric" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Mend wounds of Cedric" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of grace mend Cedric" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise celestial grace mend Cedric with healing!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O primordial celestial light of pure dawn descend from the heavens to bless mend and cure our valiant guardian Cedric with restorative aegis!" to AcousticProfile.TRANSCENDENTAL
            }
            "shield_wall" -> when (tier) {
                ResonanceTier.BASIC -> "Shield wall" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Raise great shield wall" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of steel shield line" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise steel vanguard shield our line now!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "By the power of celestial steel and unyielding vanguard valor arise and smash the foe to shield all allies with an impenetrable aegis!" to AcousticProfile.TRANSCENDENTAL
            }
            "soothing_rain" -> when (tier) {
                ResonanceTier.BASIC -> "Rain party" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Send gentle rain party" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of grove rain party" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise restorative grove rain upon the party!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O celestial grove of sacred dawn hearken unto our prayer and unleash soothing rain to mend and rejuvenate the entire party with divine grace!" to AcousticProfile.TRANSCENDENTAL
            }
            "briar_entangle" -> when (tier) {
                ResonanceTier.BASIC -> "Briar archer" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Cast great briar archer" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of briar thorns archer" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise thorny briar vines ensnare that archer!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O ancient spirits of the primordial grove awaken from the earth and ensnare that vile archer within crushing briar vines and thorns!" to AcousticProfile.TRANSCENDENTAL
            }
            "shadow_strike" -> when (tier) {
                ResonanceTier.BASIC -> "Shadow shaman" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Cast swift shadow shaman" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of umbra strike shaman" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise abyssal umbra strike the shaman now!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O primordial abyss of the endless umbra awaken from the void and cloak my blade to strike the treacherous shaman into eternal silence!" to AcousticProfile.TRANSCENDENTAL
            }
            "venom_flurry" -> when (tier) {
                ResonanceTier.BASIC -> "Venom all" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Cast swift venom all" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of venom drench all" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise abyssal venom surge drench all foes now!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O dark spirits of the void awaken and unleash a drenching flurry of venomous serpents to engulf all enemies in lethal poison!" to AcousticProfile.TRANSCENDENTAL
            }
            "verdant_cataclysm" -> when (tier) {
                ResonanceTier.BASIC -> "Grove erupt all" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Cast grove-warcry all" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of the grove surge and awaken!" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise ancient grove cataclysm crush all enemies!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O ancient spirits of the deep earth awaken and unleash the full fury of the grove-warcry to crush all foes within an erupting cataclysm of vines and stone!" to AcousticProfile.TRANSCENDENTAL
            }
            "umbral_siphon" -> when (tier) {
                ResonanceTier.BASIC -> "Mend Zephyr" to AcousticProfile.BASIC
                ResonanceTier.ADEPT -> "Siphon essence mend Zephyr" to AcousticProfile.ADEPT
                ResonanceTier.MASTER -> "Spirits of umbral essence mend Zephyr" to AcousticProfile.MASTER
                ResonanceTier.MYTHIC -> "Arise abyssal siphon all essence mend Zephyr with healing!" to AcousticProfile.MYTHIC
                ResonanceTier.TRANSCENDENTAL -> "O primordial abyss of the endless umbra awaken and siphon a torrent of restorative essence to mend and rejuvenate Zephyr with eldritch healing!" to AcousticProfile.TRANSCENDENTAL
            }
            else -> fallbackPreset(spell, tier)
        }

        val label = when (tier) {
            ResonanceTier.BASIC -> "10% Basic"
            ResonanceTier.ADEPT -> "40% Adept"
            ResonanceTier.MASTER -> "80% Master"
            ResonanceTier.MYTHIC -> "125% Mythic"
            ResonanceTier.TRANSCENDENTAL -> "200% MAX"
        }

        return ChantPreset(
            chantText = chant,
            acousticProfile = acoustic,
            tier = tier,
            label = label
        )
    }

    private fun fallbackPreset(spell: Spell, tier: ResonanceTier): Pair<String, AcousticProfile> {
        return when (tier) {
            ResonanceTier.BASIC -> "${spell.name}" to AcousticProfile.BASIC
            ResonanceTier.ADEPT -> "Cast ${spell.name} now!" to AcousticProfile.ADEPT
            ResonanceTier.MASTER -> "Spirits of power, unleash ${spell.name} upon foes!" to AcousticProfile.MASTER
            ResonanceTier.MYTHIC -> "Arise celestial power, awaken and cast ${spell.name} with crushing fury!" to AcousticProfile.MYTHIC
            ResonanceTier.TRANSCENDENTAL -> "O primordial celestial forces descend from the heavens and unleash ${spell.name} to obliterate all enemies in eternal victory!" to AcousticProfile.TRANSCENDENTAL
        }
    }
}
