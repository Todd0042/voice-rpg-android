package com.voicerpg.android.combat

// GENERATED from docs/combat-design/data/enemies.json - regenerate, do not hand-edit.
data class EnemyCodexEntry(
    val family: String,
    val selfElement: String,
    val defense: Int,
    val baseXp: Int,
    val moveset: String
)

object EnemyCodex {
    private val VARIANTS = setOf("alpha","beta","gamma","delta","epsilon","prime","a","b","c","d","i","ii","iii","iv","v","1","2","3")

    private val TABLE: Map<String, EnemyCodexEntry> = mapOf(
        "shadow wisp" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 2, baseXp = 45, moveset = "void_wisp"),
        "blighted orc" to EnemyCodexEntry(family = "FLESH", selfElement = "PHYSICAL", defense = 6, baseXp = 60, moveset = "flesh_bruiser"),
        "corrupted archer" to EnemyCodexEntry(family = "FLESH", selfElement = "PHYSICAL", defense = 3, baseXp = 55, moveset = "flesh_sniper"),
        "void shaman" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 4, baseXp = 70, moveset = "void_caster"),
        "crypt guard" to EnemyCodexEntry(family = "UNDEAD", selfElement = "PHYSICAL", defense = 8, baseXp = 70, moveset = "undead_bruiser"),
        "skeletal sniper" to EnemyCodexEntry(family = "UNDEAD", selfElement = "PHYSICAL", defense = 3, baseXp = 60, moveset = "undead_sniper"),
        "bone acolyte" to EnemyCodexEntry(family = "UNDEAD", selfElement = "SHADOW", defense = 5, baseXp = 90, moveset = "undead_summoner"),
        "iron vanguard" to EnemyCodexEntry(family = "FLESH", selfElement = "PHYSICAL", defense = 10, baseXp = 60, moveset = "flesh_bruiser"),
        "gate captain" to EnemyCodexEntry(family = "FLESH", selfElement = "PHYSICAL", defense = 12, baseXp = 90, moveset = "flesh_bruiser"),
        "castle arbalest" to EnemyCodexEntry(family = "FLESH", selfElement = "PHYSICAL", defense = 4, baseXp = 60, moveset = "flesh_sniper"),
        "court warlock" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 5, baseXp = 80, moveset = "flesh_caster"),
        "grave broodmother" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 10, baseXp = 600, moveset = "boss_broodmother"),
        "bog ironclad" to EnemyCodexEntry(family = "CONSTRUCT", selfElement = "PHYSICAL", defense = 14, baseXp = 80, moveset = "construct_bruiser"),
        "mire stalker" to EnemyCodexEntry(family = "FLESH", selfElement = "NATURE", defense = 4, baseXp = 70, moveset = "flesh_sniper"),
        "void briar binder" to EnemyCodexEntry(family = "VOID", selfElement = "NATURE", defense = 6, baseXp = 90, moveset = "void_caster"),
        "bog behemoth" to EnemyCodexEntry(family = "VERDANT", selfElement = "NATURE", defense = 12, baseXp = 500, moveset = "boss_behemoth"),
        "marsh leech" to EnemyCodexEntry(family = "FLESH", selfElement = "NATURE", defense = 2, baseXp = 50, moveset = "beast_sniper"),
        "obsidian tracker" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 3, baseXp = 55, moveset = "assassin_sniper"),
        "blighted stalker" to EnemyCodexEntry(family = "FLESH", selfElement = "PHYSICAL", defense = 6, baseXp = 60, moveset = "flesh_bruiser"),
        "aqueduct sentinel" to EnemyCodexEntry(family = "CONSTRUCT", selfElement = "PHYSICAL", defense = 10, baseXp = 65, moveset = "construct_bruiser"),
        "brood stalker" to EnemyCodexEntry(family = "FLESH", selfElement = "PHYSICAL", defense = 3, baseXp = 55, moveset = "beast_sniper"),
        "ancient mire wyrm" to EnemyCodexEntry(family = "FLESH", selfElement = "NATURE", defense = 12, baseXp = 550, moveset = "boss_wyrm"),
        "silt ghoul" to EnemyCodexEntry(family = "UNDEAD", selfElement = "SHADOW", defense = 5, baseXp = 55, moveset = "undead_bruiser"),
        "executioner kaelen" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 10, baseXp = 650, moveset = "boss_kaelen"),
        "shadowblade" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 4, baseXp = 60, moveset = "assassin_sniper"),
        "sir galahault" to EnemyCodexEntry(family = "UNDEAD", selfElement = "HOLY", defense = 14, baseXp = 700, moveset = "boss_galahault"),
        "penitent templar" to EnemyCodexEntry(family = "UNDEAD", selfElement = "HOLY", defense = 9, baseXp = 75, moveset = "undead_bruiser"),
        "chantry cleric" to EnemyCodexEntry(family = "RADIANT", selfElement = "HOLY", defense = 5, baseXp = 70, moveset = "radiant_healer"),
        "blighted broodmother" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 12, baseXp = 700, moveset = "boss_broodmother"),
        "corrupted webweaver" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 3, baseXp = 55, moveset = "beast_sniper"),
        "toxic hatchling" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 2, baseXp = 45, moveset = "beast_sniper"),
        "master nocturne" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 10, baseXp = 750, moveset = "boss_nocturne"),
        "black guild stalker" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 5, baseXp = 70, moveset = "assassin_sniper"),
        "umbral cutthroat" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 4, baseXp = 65, moveset = "void_sniper"),
        "clockwork phalanx" to EnemyCodexEntry(family = "CONSTRUCT", selfElement = "PHYSICAL", defense = 16, baseXp = 80, moveset = "construct_bruiser"),
        "warmaster ouros" to EnemyCodexEntry(family = "CONSTRUCT", selfElement = "PHYSICAL", defense = 20, baseXp = 900, moveset = "boss_ouros"),
        "steam arbalest" to EnemyCodexEntry(family = "CONSTRUCT", selfElement = "PHYSICAL", defense = 8, baseXp = 75, moveset = "construct_sniper"),
        "commander vaelor" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 16, baseXp = 900, moveset = "boss_vaelor"),
        "citadel ironclad" to EnemyCodexEntry(family = "CONSTRUCT", selfElement = "PHYSICAL", defense = 16, baseXp = 85, moveset = "construct_bruiser"),
        "obsidian warden" to EnemyCodexEntry(family = "CONSTRUCT", selfElement = "PHYSICAL", defense = 16, baseXp = 85, moveset = "construct_bruiser"),
        "spectral reaper" to EnemyCodexEntry(family = "UNDEAD", selfElement = "SHADOW", defense = 6, baseXp = 80, moveset = "undead_sniper"),
        "abyssal leviathan" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 18, baseXp = 1100, moveset = "boss_leviathan"),
        "void tendril" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 5, baseXp = 70, moveset = "void_sniper"),
        "archon custodian" to EnemyCodexEntry(family = "RADIANT", selfElement = "HOLY", defense = 16, baseXp = 95, moveset = "radiant_bruiser"),
        "celestial spire" to EnemyCodexEntry(family = "RADIANT", selfElement = "HOLY", defense = 8, baseXp = 85, moveset = "radiant_caster"),
        "grand inquisitor malakor" to EnemyCodexEntry(family = "SILENCE", selfElement = "SHADOW", defense = 22, baseXp = 2000, moveset = "boss_malakor"),
        "echo nullifier" to EnemyCodexEntry(family = "SILENCE", selfElement = "PHYSICAL", defense = 6, baseXp = 65, moveset = "silence_sniper"),
        "penitent vowbreaker" to EnemyCodexEntry(family = "UNDEAD", selfElement = "HOLY", defense = 12, baseXp = 80, moveset = "undead_bruiser"),
        "grave archon" to EnemyCodexEntry(family = "UNDEAD", selfElement = "SHADOW", defense = 8, baseXp = 80, moveset = "undead_caster"),
        "glassvine colossus" to EnemyCodexEntry(family = "VERDANT", selfElement = "NATURE", defense = 12, baseXp = 80, moveset = "verdant_bruiser"),
        "thorn choir singer" to EnemyCodexEntry(family = "VERDANT", selfElement = "NATURE", defense = 6, baseXp = 70, moveset = "verdant_caster"),
        "guild garrote" to EnemyCodexEntry(family = "FLESH", selfElement = "SHADOW", defense = 8, baseXp = 75, moveset = "flesh_bruiser"),
        "archive enforcer" to EnemyCodexEntry(family = "FLESH", selfElement = "PHYSICAL", defense = 14, baseXp = 80, moveset = "flesh_bruiser"),
        "vault watcher" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 6, baseXp = 75, moveset = "void_caster"),
        "iron warden" to EnemyCodexEntry(family = "CONSTRUCT", selfElement = "PHYSICAL", defense = 16, baseXp = 85, moveset = "construct_bruiser"),
        "tripod scorcher" to EnemyCodexEntry(family = "CONSTRUCT", selfElement = "PYROMANCY", defense = 10, baseXp = 80, moveset = "construct_sniper"),
        "plaza legionnaire" to EnemyCodexEntry(family = "FLESH", selfElement = "PHYSICAL", defense = 12, baseXp = 75, moveset = "flesh_bruiser"),
        "walking obelisk" to EnemyCodexEntry(family = "CONSTRUCT", selfElement = "PHYSICAL", defense = 14, baseXp = 85, moveset = "construct_caster"),
        "drowned husk" to EnemyCodexEntry(family = "UNDEAD", selfElement = "SHADOW", defense = 10, baseXp = 75, moveset = "undead_bruiser"),
        "shrine grasper" to EnemyCodexEntry(family = "UNDEAD", selfElement = "SHADOW", defense = 5, baseXp = 65, moveset = "undead_sniper"),
        "umbral leech" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 10, baseXp = 78, moveset = "void_bruiser"),
        "chorus leech" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 6, baseXp = 75, moveset = "void_sniper"),
        "mote drinker" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 6, baseXp = 78, moveset = "void_caster"),
        "gilded creditor" to EnemyCodexEntry(family = "RADIANT", selfElement = "HOLY", defense = 8, baseXp = 85, moveset = "radiant_caster"),
        "glinted vow clade" to EnemyCodexEntry(family = "RADIANT", selfElement = "HOLY", defense = 14, baseXp = 85, moveset = "radiant_bruiser"),
        "thornvined guardian" to EnemyCodexEntry(family = "VERDANT", selfElement = "NATURE", defense = 12, baseXp = 82, moveset = "verdant_bruiser"),
        "root lash" to EnemyCodexEntry(family = "VERDANT", selfElement = "NATURE", defense = 5, baseXp = 70, moveset = "verdant_sniper"),
        "canopy warden" to EnemyCodexEntry(family = "VERDANT", selfElement = "NATURE", defense = 8, baseXp = 82, moveset = "verdant_caster"),
        "nocturne lesson" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 6, baseXp = 78, moveset = "void_sniper"),
        "umbral ghost" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 8, baseXp = 80, moveset = "void_bruiser"),
        "echo of the guild" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 6, baseXp = 80, moveset = "void_caster"),
        "nullifier warden" to EnemyCodexEntry(family = "SILENCE", selfElement = "PHYSICAL", defense = 14, baseXp = 90, moveset = "silence_bruiser"),
        "nullifier sibilant" to EnemyCodexEntry(family = "SILENCE", selfElement = "PHYSICAL", defense = 8, baseXp = 80, moveset = "silence_sniper"),
        "vestibule archivist" to EnemyCodexEntry(family = "SILENCE", selfElement = "SHADOW", defense = 8, baseXp = 85, moveset = "silence_caster"),
        "shadow of the knight" to EnemyCodexEntry(family = "VOID", selfElement = "PHYSICAL", defense = 10, baseXp = 110, moveset = "shadow_knight"),
        "shadow of the warden" to EnemyCodexEntry(family = "VOID", selfElement = "NATURE", defense = 8, baseXp = 110, moveset = "shadow_warden"),
        "shadow of the blade" to EnemyCodexEntry(family = "VOID", selfElement = "SHADOW", defense = 8, baseXp = 110, moveset = "shadow_blade"),
        "shadow of the invocator" to EnemyCodexEntry(family = "VOID", selfElement = "ELECTROMANCY", defense = 8, baseXp = 120, moveset = "shadow_invocator"),
        "gallery of doubts" to EnemyCodexEntry(family = "SILENCE", selfElement = "SHADOW", defense = 16, baseXp = 120, moveset = "silence_bruiser"),
    )

    private fun normalize(name: String): String {
        val parts = name.lowercase().split(" ").toMutableList()
        while (parts.isNotEmpty() && parts.last() in VARIANTS) parts.removeAt(parts.lastIndex)
        return parts.joinToString(" ")
    }

    /** Lore family for display/targeting; unknown enemies default to FLESH (never a wall). */
    fun familyOf(name: String): EnemyFamily =
        EnemyFamily.fromNameOrNull(TABLE[normalize(name)]?.family) ?: EnemyFamily.FLESH

    fun entryOf(name: String): EnemyCodexEntry? = TABLE[normalize(name)]

    fun selfElementOf(name: String): School? = School.fromNameOrNull(TABLE[normalize(name)]?.selfElement?.ifEmpty { null })
    fun defenseOf(name: String): Int = TABLE[normalize(name)]?.defense ?: 0
    fun xpOf(name: String): Int = TABLE[normalize(name)]?.baseXp ?: 60
    fun movesetOf(name: String): String = TABLE[normalize(name)]?.moveset ?: ""
    fun codexForTemplate(name: String): EnemyCodexEntry? = TABLE.entries.firstOrNull { (k, _) ->
        val words = name.lowercase().split(" ")
        k.split(" ").all { it in words } || normalize(name) == k
    }?.value
}
