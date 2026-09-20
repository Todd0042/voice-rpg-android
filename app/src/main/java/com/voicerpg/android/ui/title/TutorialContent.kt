package com.voicerpg.android.ui.title

import androidx.compose.ui.graphics.Color
import com.voicerpg.android.model.SpellSchool
import com.voicerpg.android.model.DialogueSpeaker

data class TutorialSpellInfo(
    val name: String,
    val school: SpellSchool,
    val basePower: Int,
    val mpCost: Int,
    val isHeal: Boolean = false,
    val isGuard: Boolean = false,
    val hitsAll: Boolean = false,
    val manaRestorePct: Float = 0f,
    val description: String,
    val aliases: List<String> = emptyList()
)

data class TutorialSegment(
    val characterId: String,
    val speaker: DialogueSpeaker,
    val characterName: String,
    val characterTitle: String,
    val characterColor: Color,
    val introNarration: String,
    val spells: List<TutorialSpellInfo>,
    val isResonanceDemo: Boolean = false
)

data class TutorialStep(
    val segmentIndex: Int,
    val spellIndex: Int = -1,
    val narratorText: String,
    val isPractice: Boolean = false,
    val isResonanceDemo: Boolean = false,
    val isCompletion: Boolean = false
)

object TutorialContent {

    val SEGMENTS: List<TutorialSegment> = listOf(
        TutorialSegment(
            characterId = "aethel",
            speaker = DialogueSpeaker.AETHEL,
            characterName = "Aethel",
            characterTitle = "Elemental Invocator",
            characterColor = DialogueSpeaker.AETHEL.themeColor,
            introNarration = "Aethel is your Elemental Invocator. She channels fire, ice, and lightning through spoken incantation. These are her spells:",
            spells = listOf(
                TutorialSpellInfo(
                    name = "Fireball",
                    school = SpellSchool.PYROMANCY,
                    basePower = 65,
                    mpCost = 15,
                    description = "A searing bolt of flame that can inflict Burn",
                    aliases = listOf("fire", "flame", "blaze", "burn", "inferno")
                ),
                TutorialSpellInfo(
                    name = "Frost Spike",
                    school = SpellSchool.CRYOMANCY,
                    basePower = 58,
                    mpCost = 12,
                    description = "A lance of ice that chills enemies, slowing them",
                    aliases = listOf("frost", "ice", "freeze", "cold", "chill")
                ),
                TutorialSpellInfo(
                    name = "Chain Lightning",
                    school = SpellSchool.ELECTROMANCY,
                    basePower = 55,
                    mpCost = 20,
                    hitsAll = true,
                    description = "Arcing bolts of electricity that strike all foes",
                    aliases = listOf("lightning", "thunder", "storm", "shock", "spark")
                ),
                TutorialSpellInfo(
                    name = "Attune",
                    school = SpellSchool.PYROMANCY,
                    basePower = 0,
                    mpCost = 0,
                    manaRestorePct = 0.35f,
                    description = "A breath discipline. Focuses the mind and restores mana",
                    aliases = listOf("attune", "meditate", "focus", "breathe")
                )
            )
        ),
        TutorialSegment(
            characterId = "cedric",
            speaker = DialogueSpeaker.CEDRIC,
            characterName = "Sir Cedric",
            characterTitle = "Oathkeeper Templar",
            characterColor = DialogueSpeaker.CEDRIC.themeColor,
            introNarration = "Sir Cedric is your stalwart Oathkeeper Templar. He shields the fellowship and smites the unholy with radiant divine power. He joins you early in your journey.",
            spells = listOf(
                TutorialSpellInfo(
                    name = "Holy Smite",
                    school = SpellSchool.HOLY,
                    basePower = 70,
                    mpCost = 14,
                    description = "A radiant strike devastating against undead foes",
                    aliases = listOf("holy", "smite", "radiant", "divine")
                ),
                TutorialSpellInfo(
                    name = "Lay on Hands",
                    school = SpellSchool.HOLY,
                    basePower = 110,
                    mpCost = 16,
                    isHeal = true,
                    hitsAll = true,
                    description = "A powerful holy heal that restores the entire party",
                    aliases = listOf("heal", "hands", "lay", "restore", "mend")
                ),
                TutorialSpellInfo(
                    name = "Shield Wall",
                    school = SpellSchool.PHYSICAL,
                    basePower = 50,
                    mpCost = 10,
                    isGuard = true,
                    hitsAll = true,
                    description = "Raises a protective barrier that guards all allies from harm",
                    aliases = listOf("shield", "wall", "guard", "protect", "defend")
                ),
                TutorialSpellInfo(
                    name = "Steady Breath",
                    school = SpellSchool.HOLY,
                    basePower = 0,
                    mpCost = 0,
                    manaRestorePct = 0.35f,
                    description = "A breath discipline. Calms the spirit and restores mana",
                    aliases = listOf("breath", "steady", "calm")
                )
            )
        ),
        TutorialSegment(
            characterId = "lyra",
            speaker = DialogueSpeaker.LYRA,
            characterName = "Lyra",
            characterTitle = "Grove Warden",
            characterColor = DialogueSpeaker.LYRA.themeColor,
            introNarration = "Lyra is a Grove Warden who commands the forces of nature. She heals allies and entangles foes with living vines. She joins your fellowship in Act II.",
            spells = listOf(
                TutorialSpellInfo(
                    name = "Soothing Rain",
                    school = SpellSchool.NATURE,
                    basePower = 65,
                    mpCost = 18,
                    isHeal = true,
                    hitsAll = true,
                    description = "A gentle downpour that heals all allies",
                    aliases = listOf("rain", "soothe", "soothing", "heal", "downpour")
                ),
                TutorialSpellInfo(
                    name = "Briar Entangle",
                    school = SpellSchool.NATURE,
                    basePower = 60,
                    mpCost = 12,
                    description = "Thorned vines that root enemies in place",
                    aliases = listOf("briar", "entangle", "vines", "thorns", "root")
                ),
                TutorialSpellInfo(
                    name = "Deep Root",
                    school = SpellSchool.NATURE,
                    basePower = 0,
                    mpCost = 0,
                    manaRestorePct = 0.35f,
                    description = "A breath discipline. Draws mana from the living earth",
                    aliases = listOf("root", "deep", "earth", "draw")
                )
            )
        ),
        TutorialSegment(
            characterId = "zephyr",
            speaker = DialogueSpeaker.ZEPHYR,
            characterName = "Zephyr",
            characterTitle = "Shadowblade",
            characterColor = DialogueSpeaker.ZEPHYR.themeColor,
            introNarration = "Zephyr is a swift Shadowblade assassin. He strikes from the darkness with lethal precision and speed. He joins under unexpected circumstances later in the chronicle.",
            spells = listOf(
                TutorialSpellInfo(
                    name = "Shadow Strike",
                    school = SpellSchool.SHADOW,
                    basePower = 75,
                    mpCost = 12,
                    description = "A swift blade from the void. High single-target damage",
                    aliases = listOf("shadow", "strike", "dark", "blade", "void")
                ),
                TutorialSpellInfo(
                    name = "Venom Flurry",
                    school = SpellSchool.SHADOW,
                    basePower = 52,
                    mpCost = 15,
                    hitsAll = true,
                    description = "A flurry of poisoned strikes that hit all enemies",
                    aliases = listOf("venom", "flurry", "poison", "toxic")
                ),
                TutorialSpellInfo(
                    name = "Quiet Lungs",
                    school = SpellSchool.SHADOW,
                    basePower = 0,
                    mpCost = 0,
                    manaRestorePct = 0.35f,
                    description = "A breath discipline. Restores mana in silence",
                    aliases = listOf("quiet", "lungs", "silent", "breath")
                )
            )
        ),
        TutorialSegment(
            characterId = "aethel",
            speaker = DialogueSpeaker.AETHEL,
            characterName = "Aethel",
            characterTitle = "Resonance Demonstration",
            characterColor = DialogueSpeaker.AETHEL.themeColor,
            introNarration = "",
            isResonanceDemo = true,
            spells = listOf(
                TutorialSpellInfo(
                    name = "Fireball",
                    school = SpellSchool.PYROMANCY,
                    basePower = 65,
                    mpCost = 0,
                    description = "Basic incantation",
                    aliases = listOf("fire", "fireball", "flame", "blaze")
                ),
                TutorialSpellInfo(
                    name = "Fireball",
                    school = SpellSchool.PYROMANCY,
                    basePower = 65,
                    mpCost = 0,
                    description = "Elaborate incantation",
                    aliases = listOf("fire", "fireball", "flame", "blaze")
                )
            )
        )
    )

    val STEPS: List<TutorialStep> = listOf(
        TutorialStep(
            segmentIndex = -1,
            narratorText = "Welcome to the Chronicle Guide. This short tutorial will teach you the basics of commanding your fellowship in VoiceRPG: Echoes of the Logos."
        ),
        TutorialStep(
            segmentIndex = -1,
            narratorText = "You are entering the Training Arena. Here, practice dummies stand ready for you to test your spells. Don't worry -- you cannot be harmed here. Speak your commands freely."
        ),
        TutorialStep(
            segmentIndex = 0,
            narratorText = "Aethel is your Elemental Invocator. She channels fire, ice, and lightning through spoken incantation. These are her spells:"
        ),
        TutorialStep(
            segmentIndex = 0, spellIndex = 0, isPractice = true,
            narratorText = "Try casting Fireball. Say the spell name to hurl a bolt of flame at the training dummy."
        ),
        TutorialStep(
            segmentIndex = 0, spellIndex = 1, isPractice = true,
            narratorText = "Now try Frost Spike. Say the spell name to lance the dummy with ice."
        ),
        TutorialStep(
            segmentIndex = 0, spellIndex = 3,
            narratorText = "Aethel's final ability is Attune -- a free breath discipline. Say 'Attune' any time to restore mana. It costs nothing and recovers mana so you can keep casting. Every hero has their own breath ability."
        ),
        TutorialStep(
            segmentIndex = 1,
            narratorText = "Sir Cedric is your stalwart Oathkeeper Templar. He shields the fellowship and smites the unholy with radiant divine power. He joins you early in your journey."
        ),
        TutorialStep(
            segmentIndex = 1, spellIndex = 0, isPractice = true,
            narratorText = "Try casting Holy Smite through Sir Cedric. Say the spell name to strike the dummy with radiant force."
        ),
        TutorialStep(
            segmentIndex = 1, spellIndex = 3,
            narratorText = "Cedric's breath ability is Steady Breath. It calms his spirit and restores mana. He also carries Lay on Hands to heal the entire party, and Shield Wall to guard all allies."
        ),
        TutorialStep(
            segmentIndex = 2,
            narratorText = "Lyra is a Grove Warden who commands the forces of nature. She heals allies and entangles foes with living vines. She joins your fellowship in Act II."
        ),
        TutorialStep(
            segmentIndex = 2, spellIndex = 1, isPractice = true,
            narratorText = "Try casting Briar Entangle through Lyra. Say the spell name to bind the dummy in thorned vines."
        ),
        TutorialStep(
            segmentIndex = 2, spellIndex = 2,
            narratorText = "Lyra's breath ability is Deep Root, which draws mana from the living earth. She also carries Soothing Rain, a gentle downpour that heals the entire party."
        ),
        TutorialStep(
            segmentIndex = 3,
            narratorText = "Zephyr is a swift Shadowblade assassin. He strikes from the darkness with lethal precision and speed. He joins under unexpected circumstances later in the chronicle."
        ),
        TutorialStep(
            segmentIndex = 3, spellIndex = 0, isPractice = true,
            narratorText = "Try casting Shadow Strike through Zephyr. Say the spell name to slash the dummy from the void."
        ),
        TutorialStep(
            segmentIndex = 3, spellIndex = 2,
            narratorText = "Zephyr's breath ability is Quiet Lungs, restoring mana in silence. He also carries Venom Flurry, a flurry of poisoned strikes that hit all enemies."
        ),
        TutorialStep(
            segmentIndex = 4, spellIndex = 0, isPractice = true, isResonanceDemo = true,
            narratorText = "Now witness the Resonance System. Your spells grow stronger with more vivid incantation. First, say just 'Fireball' -- nothing more."
        ),
        TutorialStep(
            segmentIndex = 4, spellIndex = 1, isPractice = true, isResonanceDemo = true,
            narratorText = "Now cast Fireball again, but this time use elaborate language. Describe flames, destruction, the searing heat. Be creative and dramatic. The more vivid your words, the more powerful the spell."
        ),
        TutorialStep(
            segmentIndex = -1, isCompletion = true,
            narratorText = "Well done, Invocator. You now know the basics. Remember: every spell grows stronger with vivid, dramatic incantation. The Logos rewards those who speak with conviction. Your journey awaits."
        )
    )

    fun getStep(index: Int): TutorialStep? = STEPS.getOrNull(index)
    fun getSegment(index: Int): TutorialSegment? = SEGMENTS.getOrNull(index)

    fun getSpellForStep(step: TutorialStep): TutorialSpellInfo? {
        if (step.spellIndex < 0) return null
        val segment = getSegment(step.segmentIndex) ?: return null
        return segment.spells.getOrNull(step.spellIndex)
    }

    fun getSpeakerForStep(step: TutorialStep): DialogueSpeaker {
        if (step.segmentIndex >= 0) {
            return getSegment(step.segmentIndex)?.speaker ?: DialogueSpeaker.NARRATOR
        }
        return DialogueSpeaker.NARRATOR
    }

    val totalSteps: Int get() = STEPS.size
}
