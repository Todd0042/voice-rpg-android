package com.voicerpg.android.engine

import androidx.compose.ui.graphics.Color
import com.voicerpg.android.model.BattleEnvironment
import com.voicerpg.android.model.Enemy
import com.voicerpg.android.model.EncounterDefinition
import com.voicerpg.android.model.PartyMember
import com.voicerpg.android.model.Spell
import com.voicerpg.android.model.SpellSchool

object StoryEncounters {

    // Party spell sets
    val aethelSpells = listOf(
        Spell("fireball", "Fireball", SpellSchool.PYROMANCY, basePower = 65, mpCost = 15, description = "Roaring sphere of flame", exampleChant = "Fireball archer"),
        Spell("frost_spike", "Frost Spike", SpellSchool.CRYOMANCY, basePower = 58, mpCost = 12, description = "Piercing icicle", exampleChant = "Glacial frost spike the orc!"),
        Spell("chain_lightning", "Chain Lightning", SpellSchool.ELECTROMANCY, basePower = 48, mpCost = 20, hitsAll = true, description = "Arcing lightning storm", exampleChant = "Tempest lightning strike all enemies!")
    )

    val cedricSpells = listOf(
        Spell("holy_smite", "Holy Smite", SpellSchool.HOLY, basePower = 70, mpCost = 14, description = "Righteous celestial blow", exampleChant = "By celestial dawn, smite the heretic!"),
        Spell("lay_on_hands", "Lay on Hands", SpellSchool.HOLY, basePower = 110, mpCost = 16, isHeal = true, description = "Restorative blessing", exampleChant = "Sacred radiance mend Cedric's wounds!"),
        Spell("shield_wall", "Shield Wall", SpellSchool.PHYSICAL, basePower = 40, mpCost = 10, hitsAll = true, description = "Vanguard protection", exampleChant = "Raise the golden aegis against the horde!")
    )

    val lyraSpells = listOf(
        Spell("soothing_rain", "Soothing Rain", SpellSchool.HOLY, basePower = 65, mpCost = 18, isHeal = true, hitsAll = true, description = "Grove restorative mist", exampleChant = "Spirits of the grove, grant soothing rain upon our party!"),
        Spell("briar_entangle", "Briar Entangle", SpellSchool.HOLY, basePower = 60, mpCost = 12, description = "Thorny vines snare the foe", exampleChant = "Thorny vines and briars ensnare that archer!")
    )

    val zephyrSpells = listOf(
        Spell("shadow_strike", "Shadow Strike", SpellSchool.SHADOW, basePower = 75, mpCost = 12, description = "Lethal strike from behind", exampleChant = "From the silent umbra, strike the shaman's throat!"),
        Spell("venom_flurry", "Venom Flurry", SpellSchool.SHADOW, basePower = 50, mpCost = 15, hitsAll = true, description = "Poisoned twin daggers", exampleChant = "Abyssal venom coat my blades!")
    )

    fun createDuoParty(): List<PartyMember> = listOf(
        PartyMember("hero", "Aethel", "Elementalist", currentHp = 240, maxHp = 240, currentMp = 140, maxMp = 140, spells = aethelSpells, avatarTint = Color(0xFF90CAF9), speed = 70, atbGauge = 0.85f),
        PartyMember("cedric", "Sir Cedric", "Templar", currentHp = 310, maxHp = 420, currentMp = 80, maxMp = 80, spells = cedricSpells, avatarTint = Color(0xFFFFD54F), speed = 55, atbGauge = 0.50f)
    )

    fun createStandardParty(): List<PartyMember> = listOf(
        PartyMember("hero", "Aethel", "Elementalist", currentHp = 240, maxHp = 240, currentMp = 140, maxMp = 140, spells = aethelSpells, avatarTint = Color(0xFF90CAF9), speed = 70, atbGauge = 0.85f),
        PartyMember("cedric", "Sir Cedric", "Templar", currentHp = 310, maxHp = 420, currentMp = 80, maxMp = 80, spells = cedricSpells, avatarTint = Color(0xFFFFD54F), speed = 55, atbGauge = 0.50f),
        PartyMember("lyra", "Lyra", "Grove Warden", currentHp = 240, maxHp = 280, currentMp = 120, maxMp = 120, spells = lyraSpells, avatarTint = Color(0xFFA5D6A7), speed = 65, atbGauge = 0.70f),
        PartyMember("zephyr", "Zephyr", "Shadowblade", currentHp = 250, maxHp = 250, currentMp = 90, maxMp = 90, spells = zephyrSpells, avatarTint = Color(0xFFCE93D8), speed = 85, atbGauge = 0.95f)
    )

    // 1. Prologue Solo (1 Hero vs 2 Enemies)
    val PROLOGUE_SOLO = EncounterDefinition(
        id = "prologue_solo",
        name = "Prologue: Whispers in the Fog",
        description = "Aethel awakens alone in the mist-veiled forest as shadowy wisps converge.",
        environment = BattleEnvironment.FOREST,
        enemies = listOf(
            Enemy("wisp_1", "Shadow Wisp Alpha", "Phantom", currentHp = 160, maxHp = 160, baseAttack = 14, isTargeted = true, spriteTint = Color(0xFF81D4FA), speed = 60, atbGauge = 0.35f),
            Enemy("wisp_2", "Shadow Wisp Beta", "Phantom", currentHp = 140, maxHp = 140, baseAttack = 12, isTargeted = false, spriteTint = Color(0xFFB39DDB), speed = 50, atbGauge = 0.15f)
        ),
        initialParty = listOf(
            PartyMember("hero", "Aethel", "Elementalist", currentHp = 240, maxHp = 240, currentMp = 140, maxMp = 140, spells = aethelSpells, avatarTint = Color(0xFF90CAF9), speed = 75, atbGauge = 0.85f)
        )
    )

    // 2. Forest Ambush (2 Heroes vs 3 Enemies)
    val FOREST_AMBUSH = EncounterDefinition(
        id = "forest_ambush",
        name = "Chapter 1: The Old Way Shrine",
        description = "Sir Cedric joins Aethel to repel a blighted hunting party at the ancient shrine.",
        environment = BattleEnvironment.FOREST,
        enemies = listOf(
            Enemy("orc", "Blighted Orc", "Vanguard", currentHp = 340, maxHp = 340, baseAttack = 22, isTargeted = false, spriteTint = Color(0xFFEF5350), speed = 50, atbGauge = 0.30f),
            Enemy("archer", "Corrupted Archer", "Sniper", currentHp = 240, maxHp = 240, baseAttack = 25, isTargeted = true, spriteTint = Color(0xFFAB47BC), speed = 65, atbGauge = 0.45f),
            Enemy("shaman", "Void Shaman", "Occultist", currentHp = 280, maxHp = 280, baseAttack = 20, isTargeted = false, spriteTint = Color(0xFF5C6BC0), speed = 55, atbGauge = 0.20f)
        ),
        initialParty = listOf(
            PartyMember("hero", "Aethel", "Elementalist", currentHp = 240, maxHp = 240, currentMp = 140, maxMp = 140, spells = aethelSpells, avatarTint = Color(0xFF90CAF9), speed = 70, atbGauge = 0.85f),
            PartyMember("cedric", "Sir Cedric", "Templar", currentHp = 310, maxHp = 420, currentMp = 80, maxMp = 80, spells = cedricSpells, avatarTint = Color(0xFFFFD54F), speed = 55, atbGauge = 0.50f)
        )
    )

    // 3. Dungeon Descent (2 Heroes vs 3 Enemies with Mid-Battle Reinforcements!)
    val DUNGEON_DESCENT = EncounterDefinition(
        id = "dungeon_descent",
        name = "Chapter 4: Crypt of the Restless",
        description = "Deep in the forgotten catacombs, a Bone Acolyte commands skeletal legionnaires.",
        environment = BattleEnvironment.DUNGEON,
        enemies = listOf(
            Enemy("bone_knight", "Crypt Guard", "Vanguard", currentHp = 360, maxHp = 360, baseAttack = 24, isTargeted = false, spriteTint = Color(0xFFCFD8DC), speed = 48, atbGauge = 0.30f),
            Enemy("bone_archer", "Skeletal Sniper", "Sniper", currentHp = 220, maxHp = 220, baseAttack = 26, isTargeted = true, spriteTint = Color(0xFFB0BEC5), speed = 68, atbGauge = 0.40f),
            Enemy("bone_acolyte", "Bone Acolyte", "Summoner", currentHp = 320, maxHp = 320, baseAttack = 22, isTargeted = false, spriteTint = Color(0xFF7E57C2), speed = 52, atbGauge = 0.20f)
        ),
        initialParty = createDuoParty()
    )

    // 4. Full 6-Enemy Horde Siege in Castle (2 Heroes vs 6 Enemies)
    val CASTLE_HORDE = EncounterDefinition(
        id = "castle_horde",
        name = "Chapter 4: The Solaria Bell Chamber",
        description = "The fellowship clashes against the royal vanguard garrison on the belfry parapets.",
        environment = BattleEnvironment.CASTLE,
        enemies = listOf(
            // Front Row (Indices 0, 2, 4)
            Enemy("ironclad_1", "Iron Vanguard Alpha", "Vanguard", currentHp = 320, maxHp = 320, baseAttack = 20, isTargeted = false, spriteTint = Color(0xFF90A4AE), speed = 48, atbGauge = 0.20f),
            Enemy("ironclad_2", "Iron Vanguard Beta", "Vanguard", currentHp = 320, maxHp = 320, baseAttack = 20, isTargeted = false, spriteTint = Color(0xFF78909C), speed = 52, atbGauge = 0.25f),
            Enemy("gate_captain", "Gate Captain", "Vanguard", currentHp = 380, maxHp = 380, baseAttack = 25, isTargeted = true, spriteTint = Color(0xFFE57373), speed = 55, atbGauge = 0.35f),
            // Back Row (Indices 1, 3, 5)
            Enemy("sniper_1", "Castle Arbalest A", "Sniper", currentHp = 210, maxHp = 210, baseAttack = 24, isTargeted = false, spriteTint = Color(0xFFBA68C8), speed = 66, atbGauge = 0.45f),
            Enemy("sniper_2", "Castle Arbalest B", "Sniper", currentHp = 210, maxHp = 210, baseAttack = 24, isTargeted = false, spriteTint = Color(0xFFAB47BC), speed = 64, atbGauge = 0.40f),
            Enemy("court_mage", "Court Warlock", "Occultist", currentHp = 260, maxHp = 260, baseAttack = 28, isTargeted = false, spriteTint = Color(0xFF3F51B5), speed = 58, atbGauge = 0.30f)
        ),
        initialParty = createDuoParty()
    )

    // 5. Cave Broodmother Boss Encounter (2 Heroes vs Boss + Mid-Fight Minion Summons)
    val CAVE_BROODMOTHER = EncounterDefinition(
        id = "cave_broodmother",
        name = "Boss Trial: The Chittering Queen",
        description = "In the subterranean abyss, the Broodmother summons toxic hatchlings whenever threatened.",
        environment = BattleEnvironment.CAVE,
        enemies = listOf(
            Enemy("broodmother", "Grave Broodmother", "Boss Summoner", currentHp = 780, maxHp = 780, baseAttack = 32, isBoss = true, isTargeted = true, spriteTint = Color(0xFFFF5722), speed = 52, atbGauge = 0.40f)
        ),
        initialParty = createDuoParty()
    )

    // 6. Marsh Rescue: The Drowned Fane (Duo Party vs Void Briar Binder & Wardens)
    val MARSH_RESCUE = EncounterDefinition(
        id = "marsh_rescue",
        name = "Chapter 5: The Drowned Fane Rescue",
        description = "Aethel and Sir Cedric assault the corrupted wardens guarding Lyra's void-briar containment cage.",
        environment = BattleEnvironment.SWAMP,
        enemies = listOf(
            Enemy("mire_ironclad", "Bog Ironclad", "Vanguard", currentHp = 380, maxHp = 380, baseAttack = 24, isTargeted = false, spriteTint = Color(0xFF558B2F), speed = 46, atbGauge = 0.25f),
            Enemy("mire_stalker", "Mire Stalker", "Sniper", currentHp = 220, maxHp = 220, baseAttack = 26, isTargeted = true, spriteTint = Color(0xFF7CB342), speed = 66, atbGauge = 0.40f),
            Enemy("void_binder", "Void Briar Binder", "Occultist", currentHp = 340, maxHp = 340, baseAttack = 22, isTargeted = false, spriteTint = Color(0xFF6A1B9A), speed = 52, atbGauge = 0.20f)
        ),
        initialParty = createDuoParty()
    )

    // 7. Swamp Encounter: The Weeping Willow Sanctuary (Trio Party vs Bog Behemoth Boss)
    val SWAMP_BEHEMOTH = EncounterDefinition(
        id = "swamp_behemoth",
        name = "Chapter 6: The Weeping Willow Sanctuary",
        description = "Lyra joins the fellowship to cleanse the colossal Weeping Willow of the corrupting Bog Behemoth.",
        environment = BattleEnvironment.SWAMP,
        enemies = listOf(
            Enemy("behemoth", "Bog Behemoth", "Vanguard", currentHp = 540, maxHp = 540, baseAttack = 28, isBoss = true, isTargeted = true, spriteTint = Color(0xFF66BB6A), speed = 40, atbGauge = 0.25f),
            Enemy("leech_1", "Marsh Leech Alpha", "Sniper", currentHp = 170, maxHp = 170, baseAttack = 18, isTargeted = false, spriteTint = Color(0xFF81C784), speed = 68, atbGauge = 0.40f),
            Enemy("leech_2", "Marsh Leech Beta", "Sniper", currentHp = 170, maxHp = 170, baseAttack = 18, isTargeted = false, spriteTint = Color(0xFFAED581), speed = 64, atbGauge = 0.35f)
        ),
        initialParty = listOf(
            PartyMember("hero", "Aethel", "Elementalist", currentHp = 240, maxHp = 240, currentMp = 140, maxMp = 140, spells = aethelSpells, avatarTint = Color(0xFF90CAF9), speed = 70, atbGauge = 0.85f),
            PartyMember("cedric", "Sir Cedric", "Templar", currentHp = 310, maxHp = 420, currentMp = 80, maxMp = 80, spells = cedricSpells, avatarTint = Color(0xFFFFD54F), speed = 55, atbGauge = 0.50f),
            PartyMember("lyra", "Lyra", "Grove Warden", currentHp = 240, maxHp = 280, currentMp = 120, maxMp = 120, spells = lyraSpells, avatarTint = Color(0xFFA5D6A7), speed = 65, atbGauge = 0.70f)
        )
    )

    // 7. Camp Reconnaissance: Blighted Trackers (Cedric + Aethel vs 2 Prowlers)
    val BLIGHT_TRACKERS = EncounterDefinition(
        id = "blight_trackers",
        name = "Camp Reconnaissance: The Blighted Prowlers",
        description = "Cedric and Aethel ambush two corrupted trackers stalking the camp perimeter.",
        environment = BattleEnvironment.FOREST,
        enemies = listOf(
            Enemy("tracker_1", "Obsidian Tracker", "Sniper", currentHp = 220, maxHp = 220, baseAttack = 18, isTargeted = true, spriteTint = Color(0xFFCE93D8), speed = 62, atbGauge = 0.35f),
            Enemy("tracker_2", "Blighted Stalker", "Vanguard", currentHp = 280, maxHp = 280, baseAttack = 20, isTargeted = false, spriteTint = Color(0xFFEF5350), speed = 54, atbGauge = 0.20f)
        ),
        initialParty = listOf(
            PartyMember("hero", "Aethel", "Elementalist", currentHp = 240, maxHp = 240, currentMp = 140, maxMp = 140, spells = aethelSpells, avatarTint = Color(0xFF90CAF9), speed = 70, atbGauge = 0.85f),
            PartyMember("cedric", "Sir Cedric", "Templar", currentHp = 310, maxHp = 420, currentMp = 80, maxMp = 80, spells = cedricSpells, avatarTint = Color(0xFFFFD54F), speed = 55, atbGauge = 0.50f)
        )
    )

    // 8. Aqueduct Reconnaissance: Corrupted Sentinels (Cedric + Aethel vs 2 Sentinels)
    val CH3_SENTINELS = EncounterDefinition(
        id = "ch3_sentinels",
        name = "Chapter 3: The Corrupted Sentinels",
        description = "Cedric and Aethel engage the razor-clawed sentinels guarding the aqueduct perimeter.",
        environment = BattleEnvironment.CAVE,
        enemies = listOf(
            Enemy("sentinel_1", "Aqueduct Sentinel", "Vanguard", currentHp = 260, maxHp = 260, baseAttack = 22, isTargeted = true, spriteTint = Color(0xFFEF5350), speed = 58, atbGauge = 0.35f),
            Enemy("sentinel_2", "Brood Stalker", "Sniper", currentHp = 210, maxHp = 210, baseAttack = 24, isTargeted = false, spriteTint = Color(0xFFAB47BC), speed = 66, atbGauge = 0.25f)
        ),
        initialParty = listOf(
            PartyMember("hero", "Aethel", "Elementalist", currentHp = 240, maxHp = 240, currentMp = 140, maxMp = 140, spells = aethelSpells, avatarTint = Color(0xFF90CAF9), speed = 70, atbGauge = 0.85f),
            PartyMember("cedric", "Sir Cedric", "Templar", currentHp = 310, maxHp = 420, currentMp = 80, maxMp = 80, spells = cedricSpells, avatarTint = Color(0xFFFFD54F), speed = 55, atbGauge = 0.50f)
        )
    )

    // Helper to generate reinforcement minions dynamically
    fun createMinion(idSuffix: String, name: String = "Blighted Minion", subtitle: String = "Minion", hp: Int = 180): Enemy {
        return Enemy(
            id = "minion_$idSuffix",
            name = name,
            subtitle = subtitle,
            currentHp = hp,
            maxHp = hp,
            baseAttack = 16,
            isTargeted = false,
            spriteTint = Color(0xFF80CBC4),
            speed = 50,
            atbGauge = 0f
        )
    }

    val ALL_ENCOUNTERS = listOf(
        PROLOGUE_SOLO,
        FOREST_AMBUSH,
        DUNGEON_DESCENT,
        CASTLE_HORDE,
        CAVE_BROODMOTHER,
        MARSH_RESCUE,
        SWAMP_BEHEMOTH,
        BLIGHT_TRACKERS,
        CH3_SENTINELS
    )
}
