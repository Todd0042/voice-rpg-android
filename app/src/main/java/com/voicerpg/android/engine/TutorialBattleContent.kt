package com.voicerpg.android.engine

import androidx.compose.ui.graphics.Color
import com.voicerpg.android.model.DialogueSpeaker
import com.voicerpg.android.model.Enemy
import com.voicerpg.android.model.SpellSchool

data class TutorialSpellDef(
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

data class TutorialCharacterDef(
    val id: String,
    val name: String,
    val title: String,
    val speaker: DialogueSpeaker,
    val loreClass: String,
    val maxHp: Int,
    val maxMp: Int,
    val speed: Int,
    val avatarTint: Color,
    val spells: List<TutorialSpellDef>
)

sealed class TutorialBattleStep {
    data class IntroPopup(
        val text: String,
        val speaker: DialogueSpeaker = DialogueSpeaker.NARRATOR
    ) : TutorialBattleStep()

    data class CharacterJoin(
        val characterDef: TutorialCharacterDef,
        val text: String
    ) : TutorialBattleStep()

    data class SpellOverview(
        val characterDef: TutorialCharacterDef,
        val text: String
    ) : TutorialBattleStep()

    data class PracticeCast(
        val characterId: String,
        val spellName: String,
        val school: SpellSchool,
        val promptText: String,
        val isResonanceDemo: Boolean = false,
        val resonanceDemoIndex: Int = 0
    ) : TutorialBattleStep()

    data class AutoAction(
        val characterId: String,
        val actionText: String,
        val resultText: String
    ) : TutorialBattleStep()

    data class EnemyAttackDemo(
        val introText: String,
        val resultText: String
    ) : TutorialBattleStep()

    data class CompletionPopup(
        val text: String
    ) : TutorialBattleStep()
}

object TutorialBattleContent {

    val CHARACTERS: List<TutorialCharacterDef> = listOf(
        TutorialCharacterDef(
            id = "aethel",
            name = "Aethel",
            title = "Elemental Invocator",
            speaker = DialogueSpeaker.AETHEL,
            loreClass = "Elemental Invocator",
            maxHp = 320,
            maxMp = 100,
            speed = 55,
            avatarTint = Color(0xFF00E5FF),
            spells = listOf(
                TutorialSpellDef(
                    name = "Fireball",
                    school = SpellSchool.PYROMANCY,
                    basePower = 65,
                    mpCost = 15,
                    description = "Hurl a searing bolt of flame at a single target.",
                    aliases = listOf("fire", "flame", "blaze", "burn", "inferno")
                ),
                TutorialSpellDef(
                    name = "Frost Spike",
                    school = SpellSchool.CRYOMANCY,
                    basePower = 58,
                    mpCost = 12,
                    description = "Launch a jagged shard of ice to chill a single foe.",
                    aliases = listOf("frost", "ice", "freeze", "cold", "chill")
                ),
                TutorialSpellDef(
                    name = "Chain Lightning",
                    school = SpellSchool.ELECTROMANCY,
                    basePower = 55,
                    mpCost = 20,
                    hitsAll = true,
                    description = "Arc crackling lightning through every enemy on the field.",
                    aliases = listOf("lightning", "thunder", "storm", "shock", "spark")
                ),
                TutorialSpellDef(
                    name = "Attune",
                    school = SpellSchool.PYROMANCY,
                    basePower = 0,
                    mpCost = 0,
                    manaRestorePct = 0.35f,
                    description = "Focus inward and restore a portion of mana.",
                    aliases = listOf("attune", "meditate", "focus", "breathe")
                )
            )
        ),
        TutorialCharacterDef(
            id = "cedric",
            name = "Sir Cedric",
            title = "Oathkeeper Templar",
            speaker = DialogueSpeaker.CEDRIC,
            loreClass = "Oathkeeper Templar",
            maxHp = 420,
            maxMp = 80,
            speed = 40,
            avatarTint = Color(0xFFFFF176),
            spells = listOf(
                TutorialSpellDef(
                    name = "Holy Smite",
                    school = SpellSchool.HOLY,
                    basePower = 70,
                    mpCost = 14,
                    description = "Bring down radiant divine judgment upon a single foe.",
                    aliases = listOf("holy", "smite", "radiant", "divine")
                ),
                TutorialSpellDef(
                    name = "Lay on Hands",
                    school = SpellSchool.HOLY,
                    basePower = 110,
                    mpCost = 16,
                    isHeal = true,
                    hitsAll = true,
                    description = "Channel sacred light to restore the health of all allies.",
                    aliases = listOf("heal", "hands", "lay", "restore", "mend")
                ),
                TutorialSpellDef(
                    name = "Shield Wall",
                    school = SpellSchool.PHYSICAL,
                    basePower = 50,
                    mpCost = 10,
                    isGuard = true,
                    hitsAll = true,
                    description = "Raise an impenetrable barrier to guard all allies.",
                    aliases = listOf("shield", "wall", "guard", "protect", "defend")
                ),
                TutorialSpellDef(
                    name = "Steady Breath",
                    school = SpellSchool.HOLY,
                    basePower = 0,
                    mpCost = 0,
                    manaRestorePct = 0.35f,
                    description = "Calm the mind and restore a portion of mana.",
                    aliases = listOf("breath", "steady", "calm")
                )
            )
        ),
        TutorialCharacterDef(
            id = "lyra",
            name = "Lyra",
            title = "Grove Warden",
            speaker = DialogueSpeaker.LYRA,
            loreClass = "Grove Warden",
            maxHp = 280,
            maxMp = 110,
            speed = 50,
            avatarTint = Color(0xFFA5D6A7),
            spells = listOf(
                TutorialSpellDef(
                    name = "Soothing Rain",
                    school = SpellSchool.NATURE,
                    basePower = 65,
                    mpCost = 18,
                    isHeal = true,
                    hitsAll = true,
                    description = "Summon a gentle rain that restores the health of all allies.",
                    aliases = listOf("rain", "soothe", "soothing", "heal", "downpour")
                ),
                TutorialSpellDef(
                    name = "Briar Entangle",
                    school = SpellSchool.NATURE,
                    basePower = 60,
                    mpCost = 12,
                    description = "Command thorny vines to ensnare and crush a single foe.",
                    aliases = listOf("briar", "entangle", "vines", "thorns", "root")
                ),
                TutorialSpellDef(
                    name = "Deep Root",
                    school = SpellSchool.NATURE,
                    basePower = 0,
                    mpCost = 0,
                    manaRestorePct = 0.35f,
                    description = "Draw mana from the earth through ancient root networks.",
                    aliases = listOf("root", "deep", "earth", "draw")
                )
            )
        ),
        TutorialCharacterDef(
            id = "zephyr",
            name = "Zephyr",
            title = "Shadowblade",
            speaker = DialogueSpeaker.ZEPHYR,
            loreClass = "Shadowblade",
            maxHp = 260,
            maxMp = 90,
            speed = 75,
            avatarTint = Color(0xFFCE93D8),
            spells = listOf(
                TutorialSpellDef(
                    name = "Shadow Strike",
                    school = SpellSchool.SHADOW,
                    basePower = 75,
                    mpCost = 12,
                    description = "Vanish into darkness and deliver a lethal strike to a single foe.",
                    aliases = listOf("shadow", "strike", "dark", "blade", "void")
                ),
                TutorialSpellDef(
                    name = "Venom Flurry",
                    school = SpellSchool.SHADOW,
                    basePower = 52,
                    mpCost = 15,
                    hitsAll = true,
                    description = "Unleash a rapid barrage of poisoned blades upon all enemies.",
                    aliases = listOf("venom", "flurry", "poison", "toxic")
                ),
                TutorialSpellDef(
                    name = "Quiet Lungs",
                    school = SpellSchool.SHADOW,
                    basePower = 0,
                    mpCost = 0,
                    manaRestorePct = 0.35f,
                    description = "Still the breath and restore a portion of mana.",
                    aliases = listOf("quiet", "lungs", "silent", "breath")
                )
            )
        )
    )

    val ENEMIES: List<Enemy> = listOf(
        Enemy(
            id = "dummy_alpha",
            name = "Practice Dummy Alpha",
            subtitle = "Training Target",
            currentHp = 2000,
            maxHp = 2000,
            baseAttack = 0,
            speed = 30,
            family = "FLESH"
        ),
        Enemy(
            id = "dummy_beta",
            name = "Practice Dummy Beta",
            subtitle = "Training Target",
            currentHp = 2000,
            maxHp = 2000,
            baseAttack = 0,
            speed = 30,
            family = "FLESH"
        ),
        Enemy(
            id = "dummy_gamma",
            name = "Practice Dummy Gamma",
            subtitle = "Training Target",
            currentHp = 2000,
            maxHp = 2000,
            baseAttack = 0,
            speed = 30,
            family = "FLESH"
        )
    )

    val STEPS: List<TutorialBattleStep> = listOf(
        TutorialBattleStep.IntroPopup(
            text = "Welcome to the Training Arena. Here, practice dummies stand ready for you to test your incantations. You cannot be harmed. Speak your commands freely.",
            speaker = DialogueSpeaker.NARRATOR
        ),
        TutorialBattleStep.IntroPopup(
            text = "In real combat, you will speak voice commands to cast spells. The more vivid and dramatic your words, the more powerful your magic becomes. Let us begin.",
            speaker = DialogueSpeaker.NARRATOR
        ),
        TutorialBattleStep.CharacterJoin(
            characterDef = CHARACTERS[0],
            text = "Your first companion is Aethel, an Elemental Invocator. She channels fire, ice, and lightning through spoken incantation."
        ),
        TutorialBattleStep.SpellOverview(
            characterDef = CHARACTERS[0],
            text = "Here are Aethel's spells."
        ),
        TutorialBattleStep.PracticeCast(
            characterId = "aethel",
            spellName = "Fireball",
            school = SpellSchool.PYROMANCY,
            promptText = "Try casting Fireball. Say the spell name to hurl a bolt of flame at the training dummy."
        ),
        TutorialBattleStep.PracticeCast(
            characterId = "aethel",
            spellName = "Frost Spike",
            school = SpellSchool.CRYOMANCY,
            promptText = "Now try Frost Spike. Ice will chill the dummy."
        ),
        TutorialBattleStep.PracticeCast(
            characterId = "aethel",
            spellName = "Chain Lightning",
            school = SpellSchool.ELECTROMANCY,
            promptText = "Chain Lightning strikes all enemies at once. Say the spell name."
        ),
        TutorialBattleStep.AutoAction(
            characterId = "aethel",
            actionText = "Every hero has a free breath ability. Say 'Attune' to restore mana. It costs nothing and lets you keep casting. Every companion has their own version.",
            resultText = "Aethel's mana has been restored. Breath abilities are essential for sustaining long battles."
        ),
        TutorialBattleStep.CharacterJoin(
            characterDef = CHARACTERS[1],
            text = "Sir Cedric is your Oathkeeper Templar. A stalwart knight who shields the fellowship and smites the unholy with radiant divine power."
        ),
        TutorialBattleStep.SpellOverview(
            characterDef = CHARACTERS[1],
            text = "Here are Sir Cedric's spells."
        ),
        TutorialBattleStep.PracticeCast(
            characterId = "cedric",
            spellName = "Holy Smite",
            school = SpellSchool.HOLY,
            promptText = "Try casting Holy Smite through Sir Cedric. Say the spell name."
        ),
        TutorialBattleStep.AutoAction(
            characterId = "cedric",
            actionText = "Cedric also carries Lay on Hands to heal the entire party, and Shield Wall to guard all allies. His breath ability is Steady Breath.",
            resultText = "Sir Cedric's mana restored via Steady Breath."
        ),
        TutorialBattleStep.CharacterJoin(
            characterDef = CHARACTERS[2],
            text = "Lyra is a Grove Warden who commands the forces of nature. She heals allies and entangles foes with living vines."
        ),
        TutorialBattleStep.SpellOverview(
            characterDef = CHARACTERS[2],
            text = "Here are Lyra's spells."
        ),
        TutorialBattleStep.PracticeCast(
            characterId = "lyra",
            spellName = "Briar Entangle",
            school = SpellSchool.NATURE,
            promptText = "Try casting Briar Entangle through Lyra. Say the spell name."
        ),
        TutorialBattleStep.AutoAction(
            characterId = "lyra",
            actionText = "Lyra also carries Soothing Rain, a healing downpour for the entire party. Her breath ability is Deep Root, drawing mana from the earth.",
            resultText = "Lyra's mana restored via Deep Root."
        ),
        TutorialBattleStep.CharacterJoin(
            characterDef = CHARACTERS[3],
            text = "Zephyr is a swift Shadowblade assassin. He strikes from the darkness with lethal precision and speed."
        ),
        TutorialBattleStep.SpellOverview(
            characterDef = CHARACTERS[3],
            text = "Here are Zephyr's spells."
        ),
        TutorialBattleStep.PracticeCast(
            characterId = "zephyr",
            spellName = "Shadow Strike",
            school = SpellSchool.SHADOW,
            promptText = "Try casting Shadow Strike through Zephyr. Say the spell name."
        ),
        TutorialBattleStep.AutoAction(
            characterId = "zephyr",
            actionText = "Zephyr also carries Venom Flurry, a flurry of poisoned strikes hitting all enemies. His breath ability is Quiet Lungs.",
            resultText = "Zephyr's mana restored via Quiet Lungs."
        ),
        TutorialBattleStep.IntroPopup(
            text = "Now witness the Resonance System. Your spells grow stronger with more vivid incantation. First, say just 'Fireball' -- nothing more.",
            speaker = DialogueSpeaker.NARRATOR
        ),
        TutorialBattleStep.PracticeCast(
            characterId = "aethel",
            spellName = "Fireball",
            school = SpellSchool.PYROMANCY,
            promptText = "Say 'Fireball' now. Keep it simple.",
            isResonanceDemo = true,
            resonanceDemoIndex = 0
        ),
        TutorialBattleStep.PracticeCast(
            characterId = "aethel",
            spellName = "Fireball",
            school = SpellSchool.PYROMANCY,
            promptText = "Now cast Fireball again, but this time use elaborate, dramatic language. Describe flames, destruction, the searing heat. Be creative.",
            isResonanceDemo = true,
            resonanceDemoIndex = 1
        ),
        TutorialBattleStep.IntroPopup(
            text = "See the difference? Elaborate, vivid incantations unleash far more power than simple spell names. The Logos rewards those who speak with conviction.",
            speaker = DialogueSpeaker.NARRATOR
        ),
        TutorialBattleStep.EnemyAttackDemo(
            introText = "Now observe -- enemies fight back too! The dummies will take a turn. Don't worry, your fellowship can withstand it.",
            resultText = "In real combat, enemies will strike back on their own turn. Use healing spells, shields, and smart strategy to keep your fellowship alive."
        ),
        TutorialBattleStep.CompletionPopup(
            text = "Well done, Invocator. You now know the basics of commanding your fellowship. Every spell grows stronger with vivid, dramatic incantation. Your journey awaits."
        )
    )

    fun getCharacter(id: String): TutorialCharacterDef? =
        CHARACTERS.firstOrNull { it.id == id }

    val totalSteps: Int get() = STEPS.size
}
