package com.voicerpg.android.ui.sprites

import androidx.compose.ui.graphics.Color

enum class SpriteFrame {
    IDLE_UPRIGHT,
    IDLE_CROUCH,
    ACTION_CAST,
    DAMAGED
}

/**
 * 32-bit Enhanced Pixel Sprites (20x26 grid) with rich multi-tone shading,
 * detailed silhouettes, gear articulation, and breathing/knee-buckle animation frames.
 */
object PixelSpriteData {

    // Palettes
    val Transparent = Color.Transparent
    val DarkOutline = Color(0xFF0C0E14)
    val WhiteSpec = Color(0xFFFFFFFF)

    // 1. Aethel (Elementalist): Archmage cowl, sapphire robes, gold embroidered mantlet, glowing arcane crystal staff
    private val AETHEL_PALETTE = mapOf(
        '.' to Transparent,
        'K' to DarkOutline,
        'C' to Color(0xFF1565C0), // Sapphire Blue
        'L' to Color(0xFF42A5F5), // Light Blue fold
        'B' to Color(0xFF0D47A1), // Deep Navy Shadow
        'S' to Color(0xFFFFCC80), // Skin
        's' to Color(0xFFFFA726), // Skin shadow
        'G' to Color(0xFFFFD54F), // Gold embroidery
        'g' to Color(0xFFFF8F00), // Gold shadow
        'W' to Color(0xFF8D6E63), // Staff wood
        'E' to Color(0xFF00E5FF), // Glowing Arcane Cyan
        'P' to WhiteSpec          // Crystal spark
    )

    // 2. Sir Cedric (Templar): Winged knight helm, gleaming silver plate mail, crimson cross tabard, longsword
    private val CEDRIC_PALETTE = mapOf(
        '.' to Transparent,
        'K' to DarkOutline,
        'M' to Color(0xFFECEFF1), // Silver plate gleam
        'm' to Color(0xFFB0BEC5), // Steel plate
        'd' to Color(0xFF546E7A), // Plate shadow
        'G' to Color(0xFFFFD54F), // Radiant gold trim / helm wings
        'g' to Color(0xFFFFA000), // Deep gold
        'R' to Color(0xFFE53935), // Crimson tabard
        'r' to Color(0xFFB71C1C), // Deep crimson shadow
        'W' to WhiteSpec,         // Holy white cross
        'S' to Color(0xFFFFCC80), // Skin in visor
        'P' to WhiteSpec,         // Specular shine
        'I' to Color(0xFF80DEEA)  // Keen sword steel
    )

    // 3. Lyra (Grove Warden): Deep forest green hooded cloak, blonde braided hair, vine-wrapped composite longbow
    private val LYRA_PALETTE = mapOf(
        '.' to Transparent,
        'K' to DarkOutline,
        'G' to Color(0xFF2E7D32), // Forest Green
        'g' to Color(0xFF4CAF50), // Leaf Green highlight
        'd' to Color(0xFF1B5E20), // Dark Ivy shadow
        'Y' to Color(0xFFFFEE58), // Blonde braids
        'y' to Color(0xFFFDD835), // Blonde shadow
        'S' to Color(0xFFFFF3E0), // Fair skin
        's' to Color(0xFFFFCC80), // Skin shadow
        'B' to Color(0xFF795548), // Leather cuirass
        'b' to Color(0xFF4E342E), // Dark leather
        'W' to Color(0xFF8D6E63), // Carved bow
        'L' to Color(0xFF76FF03), // Living vine wrap
        'P' to WhiteSpec          // Eye glint
    )

    // 4. Zephyr (Shadowblade): Midnight dark violet cowl, glowing amethyst eyes, twin obsidian venom daggers
    private val ZEPHYR_PALETTE = mapOf(
        '.' to Transparent,
        'K' to DarkOutline,
        'V' to Color(0xFF211A2E), // Midnight Violet
        'v' to Color(0xFF311B92), // Deep purple
        'U' to Color(0xFF4A148C), // Shadow vest
        'P' to Color(0xFFBA68C8), // Flowing shadow scarf
        'E' to Color(0xFFE040FB), // Glowing amethyst visor/eyes
        'S' to Color(0xFFFFCC80), // Pale skin
        'O' to Color(0xFF37474F), // Obsidian blade
        'T' to Color(0xFF76FF03), // Toxic venom drop
        'W' to WhiteSpec,         // Dagger gleam
        's' to Color(0xFF120024)  // Deep shadow fold
    )

    // 5. Blighted Orc: Horned iron war-helm, scarlet demon flesh, spiked iron pauldrons, jutting lower tusks
    private val ORC_PALETTE = mapOf(
        '.' to Transparent,
        'K' to DarkOutline,
        'R' to Color(0xFFD32F2F), // Crimson skin
        'r' to Color(0xFFB71C1C), // Deep muscle shadow
        'l' to Color(0xFFEF5350), // Scarlet muscle highlight
        'H' to Color(0xFFECEFF1), // War horn
        'h' to Color(0xFF424242), // Iron helm
        'I' to Color(0xFF757575), // Spiked iron pauldrons
        'B' to Color(0xFF4E342E), // Spiked leather belt
        'Y' to Color(0xFFFFEB3B), // Burning yellow eyes
        'T' to WhiteSpec          // White tusks
    )

    // 6. Corrupted Archer: Skeletal sniper, tattered amethyst hood, glowing red eye sockets, shadow recurve bow
    private val ARCHER_PALETTE = mapOf(
        '.' to Transparent,
        'K' to DarkOutline,
        'C' to Color(0xFF4A148C), // Amethyst hood
        'c' to Color(0xFF260E47), // Dark violet shadow
        'B' to Color(0xFFECEFF1), // Skeletal bone
        'b' to Color(0xFF90A4AE), // Bone shadow
        'E' to Color(0xFFFF1744), // Malevolent red eye glow
        'W' to Color(0xFF37474F), // Shadow bow
        'A' to Color(0xFFD500F9), // Nocked void arrow
        'P' to WhiteSpec          // Arrow tip spark
    )

    // 7. Void Shaman: Floating horned bone mask, cyan void gaze, swirling abyssal robes, orbiting void orbs
    private val SHAMAN_PALETTE = mapOf(
        '.' to Transparent,
        'K' to DarkOutline,
        'V' to Color(0xFF1A1238), // Deep abyssal robe
        'v' to Color(0xFF2A1B54), // Void cloth fold
        'M' to Color(0xFFEEEEEE), // Horned bone mask
        'm' to Color(0xFF757575), // Mask horn shadow
        'E' to Color(0xFF00E5FF), // Void cyan eyes
        'O' to Color(0xFF7C4DFF), // Orbiting void orb
        'o' to Color(0xFFE040FB), // Glowing orb core
        'P' to WhiteSpec          // Arcane spark
    )

    // 8. Practice Dummy: Carved wooden post, bound straw torso with painted red bullseye, crossbeam arms
    private val DUMMY_PALETTE = mapOf(
        '.' to Transparent,
        'K' to DarkOutline,
        'W' to Color(0xFF6D4C41), // Wood pole/crossbeam
        'w' to Color(0xFF4E342E), // Dark wood shadow
        'S' to Color(0xFFD7CCC8), // Bound straw / canvas
        's' to Color(0xFFA1887F), // Straw shadow
        'Y' to Color(0xFFFFD54F), // Rope ties
        'R' to Color(0xFFE53935), // Bullseye red
        'r' to Color(0xFFB71C1C)  // Dark bullseye
    )

    private val DUMMY_UPRIGHT = listOf(
        ".......KKKKK........",
        "......KSSSSSsK......",
        ".....KSSSSSSSK......",
        ".....KSSYYYYSSK.....",
        ".....KSSSSSSSK......",
        ".....KSSwWWwSK......",
        "..KKKKWwwWWwwKKKK...",
        ".KSSSSWWWWWWWWSSSSK.",
        ".KSYYSWWRRRWWSSYYsK.",
        ".KSSSSWWRRRWWSSSSsK.",
        "..KKKKWWRRRWWKKKK...",
        ".....KSSRRRSSK......",
        ".....KSSwWWwSK......",
        ".....KSSSSSSSK......",
        ".....KSSYYYYSSK.....",
        ".....KSSSSSSSK......",
        "......KSSSSSsK......",
        ".......KwwwwK.......",
        ".......KWWWWK.......",
        ".......KwwwwK.......",
        ".......KWWWWK.......",
        ".......KwwwwK.......",
        ".......KWWWWK.......",
        ".......KwwwwK.......",
        "......KwwwwwwK......",
        ".....KKKKKKKKKK....."
    )

    private val DUMMY_CROUCH = listOf(
        "........KKKKK.......",
        ".......KSSSSSsK.....",
        "......KSSSSSSSK.....",
        "......KSSYYYYSSK....",
        "......KSSSSSSSK.....",
        "......KSSwWWwSK.....",
        "...KKKKWwwWWwwKKKK..",
        "..KSSSSWWWWWWWWSSSSK",
        "..KSYYSWWRRRWWSSYYsK",
        "..KSSSSWWRRRWWSSSSsK",
        "...KKKKWWRRRWWKKKK..",
        "......KSSRRRSSK.....",
        "......KSSwWWwSK.....",
        "......KSSSSSSSK.....",
        "......KSSYYYYSSK....",
        "......KSSSSSSSK.....",
        ".......KSSSSSsK.....",
        ".......KwwwwK.......",
        ".......KWWWWK.......",
        ".......KwwwwK.......",
        ".......KWWWWK.......",
        ".......KwwwwK.......",
        ".......KWWWWK.......",
        ".......KwwwwK.......",
        "......KwwwwwwK......",
        ".....KKKKKKKKKK....."
    )

    private val AETHEL_UPRIGHT = listOf(
        ".......KKKKK........",
        "......KCCCCCK.......",
        ".....KCCLLCCCK......",
        ".....KCCLLCCCK......",
        ".....KCCSSCCCK.KPK..",
        ".....KCSESESCKKEEEK.",
        ".....KCsssssCKKEEEK.",
        "......KCGGGCK..KEK..",
        ".....KCCCCCCCK..KWK.",
        "....KCLGGGGGLCK.KWK.",
        "...KCLLCGGGGCLLKKWK.",
        "...KCLLCCCCCCLLCKWK.",
        "...KCCCCGLLGCCCCKWK.",
        "....KCCCGLLGCCCKKWK.",
        "....KCCCCCCCCCCK.KWK",
        "....KCCCCCGCCCCK.KWK",
        "....KCLLCCCGCCCK.KWK",
        "...KCLLLCCCCLLCK.KWK",
        "...KCCCCCCCCCCCK.KWK",
        "..KCCCCCCCCCCCCCKKWK",
        "..KCCCCCCCCCCCCCKKWK",
        "..KBBBBBBBBBBBBBKKWK",
        "...KKKKKKKKKKKKK.KWK",
        "....KBBK...KBBK..KWK",
        "....KBBK...KBBK...K.",
        "...KKKKK...KKKKK...."
    )
    private val AETHEL_CROUCH = listOf(
        "....................",
        ".......KKKKK........",
        "......KCCCCCK.......",
        ".....KCCLLCCCK......",
        ".....KCCLLCCCK.KPK..",
        ".....KCCSSCCCKKEEEK.",
        ".....KCSESESCKKEEEK.",
        ".....KCsssssCK..KEK.",
        "......KCGGGCK...KWK.",
        ".....KCLGGGLCK..KWK.",
        "...KCLLCGGGGCLLKKWK.",
        "...KCLLCCCCCCLLCKWK.",
        "...KCCCCGLLGCCCCKWK.",
        "....KCCCGLLGCCCKKWK.",
        "....KCCCCCCCCCCK.KWK",
        "....KCLLCCCGCCCK.KWK",
        "...KCLLLCCCCLLCK.KWK",
        "...KCCCCCCCCCCCK.KWK",
        "..KCCCCCCCCCCCCCKKWK",
        "..KCCCCCCCCCCCCCKKWK",
        ".KCCCCCCCCCCCCCCCKKW",
        ".KBBBBBBBBBBBBBBBKKW",
        "..KKKKKKKKKKKKKKK.KW",
        "...KBBBK...KBBBK..KW",
        "...KBBBK...KBBBK....",
        "..KKKKKK...KKKKKK..."
    )
    private val AETHEL_CAST = listOf(
        "...............KPKK.",
        "..............KEEEEK",
        ".......KKKKK..KEEEEK",
        "......KCCCCCK..KEEK.",
        ".....KCCLLCCCK..KWK.",
        ".....KCCLLCCCK..KWK.",
        ".....KCCSSCCCK..KWK.",
        ".....KCSESESCK..KWK.",
        ".....KCsssssCK..KWK.",
        "......KCGGGCK...KWK.",
        "....KCLGGGGGLCK.KWK.",
        "...KCLLCGGGGCLLKKWK.",
        "...KEEEECCCCCEEEKKW.",
        "...KEEECGLLGCEEEK.K.",
        "....KCCCCCCCCCCK....",
        "....KCCCCCGCCCCK....",
        "....KCLLCCCGCCCK....",
        "...KCLLLCCCCLLCK....",
        "...KCCCCCCCCCCCK....",
        "..KCCCCCCCCCCCCCK...",
        "..KCCCCCCCCCCCCCK...",
        "..KBBBBBBBBBBBBBKK..",
        "...KKKKKKKKKKKKKKK..",
        "....KBBK...KBBK.....",
        "....KBBK...KBBK.....",
        "...KKKKK...KKKKK...."
    )
    private val CEDRIC_UPRIGHT = listOf(
        ".......KKKKKK.......",
        "......KGMMMMGK......",
        ".....KGGMMMMGGK.....",
        ".....KMMMPPMMMK.....",
        ".....KMMKSSKMMK.....",
        ".....KMMMddMMMK.....",
        "....KGGMMddMMGGK....",
        "...KGMMMRRRRMMMGK...",
        "..KPMmdMRWWMRdmMPK..",
        ".KIPMmdMRWWMRdmMPK..",
        ".KImMmdRWWWWWRdmMIK.",
        ".KImMmdRRWWWRRdmMIK.",
        ".KImMmdRRRRRRdmMIK..",
        ".KImMmdRRRRRRdmMKK..",
        ".KImMmdKGMMGKdmMK...",
        ".KImMK.KRRRRK.KMK...",
        ".KImMK.KRRRRK.KMK...",
        "..KKK..KmmmmK..KK...",
        ".......KmmmmK.......",
        ".......KddddK.......",
        "......KMmmmmMK......",
        "......KMmmmmMK......",
        "......KddddddK......",
        "......KMdKKdMK......",
        "......KMMKKMMK......",
        ".....KKKKKKKKKK....."
    )
    private val CEDRIC_CROUCH = listOf(
        "....................",
        ".......KKKKKK.......",
        "......KGMMMMGK......",
        ".....KGGMMMMGGK.....",
        ".....KMMMPPMMMK.....",
        ".....KMMKSSKMMK.....",
        ".....KMMMddMMMK.....",
        "....KGGMMddMMGGK....",
        "..KPMmdMRWWMRdmMPK..",
        ".KIPMmdMRWWMRdmMPK..",
        ".KImMmdRWWWWWRdmMIK.",
        ".KImMmdRRWWWRRdmMIK.",
        ".KImMmdRRRRRRdmMIK..",
        ".KImMmdKGMMGKdmMK...",
        ".KImMK.KRRRRK.KMK...",
        ".KImMK.KRRRRK.KMK...",
        "..KKK..KmmmmK..KK...",
        ".......KmmmmK.......",
        "......KMmmmmMK......",
        "......KMmmmmMK......",
        ".....KMmmmmmmMK.....",
        ".....KMmmmmmmMK.....",
        ".....KddddddddK.....",
        ".....KMdKKKKdMK.....",
        ".....KMMKKKKMMK.....",
        "....KKKKK..KKKKK...."
    )
    private val CEDRIC_CAST = listOf(
        ".KIIK...............",
        ".KIIK..KKKKKK.......",
        ".KIIK.KGMMMMGK......",
        ".KIIKKGGMMMMGGK.....",
        ".KIIKMMMPPMMMK......",
        ".KIIKMMKSSKMMK......",
        ".KGGKMMMddMMMK......",
        ".KWWKGGMMddMMGGK....",
        "..KKGMMMRRRRMMMGK...",
        "..KPMmdMRWWMRdmMPK..",
        "..KmMMmdRWWWWWRdmMPK",
        "..KmMMmdRRWWWRRdmMPK",
        "..KmMMmdRRRRRRdmMIK.",
        "..KmMMmdKGMMGKdmMKK.",
        "...KMK.KRRRRK.KMK...",
        "...KMK.KRRRRK.KMK...",
        "...KK..KmmmmK..KK...",
        ".......KmmmmK.......",
        ".......KddddK.......",
        "......KMmmmmMK......",
        "......KMmmmmMK......",
        "......KMmmmmMK......",
        "......KddddddK......",
        "......KMdKKdMK......",
        "......KMMKKMMK......",
        ".....KKKKKKKKKK....."
    )
    private val LYRA_UPRIGHT = listOf(
        ".......KKKKKK.......",
        "......KGGGGGGK......",
        ".....KGGggggGGK.....",
        "....KGGyyyyyyGGK....",
        "....KGYYSSSSYYGK....",
        "....KGYSSPSsYYGK.KWK",
        "....KGYSssssYYGKKWLK",
        ".....KGddddddGK.KWLK",
        "....KGGGGGGGGGK.KWLK",
        "...KGGGgBBBBgGGKKWLK",
        "..KGGGGGBBBBBGGGKWWK",
        "..KYYGgGBBBBBgGYYKWK",
        "..KYYGgGBbBbBgGYYKWK",
        "..KYYKK.KBbBK.KKYYWK",
        "..KK....KBbBK...KWLK",
        ".......KGGGGGgK.KWLK",
        ".......KGGGGGgK.KWLK",
        "......KGdddddGGKKWLK",
        "......KGGGGGGGK.KWLK",
        "......KbbKKKbbK.KWLK",
        "......KbbKKKbbK.KWLK",
        "......KbbKKKbbK.KWLK",
        "......KBBKKKBBK..KWK",
        "......KBBKKKBBK...K.",
        ".....KKKKK.KKKKK....",
        "...................."
    )
    private val LYRA_CROUCH = listOf(
        "....................",
        ".......KKKKKK.......",
        "......KGGGGGGK......",
        ".....KGGggggGGK.....",
        "....KGGyyyyyyGGK....",
        "....KGYYSSSSYYGK.KWK",
        "....KGYSSPSsYYGKKWLK",
        "....KGYSssssYYGK.KWK",
        ".....KGddddddGK.KWLK",
        "....KGGGGGGGGGK.KWLK",
        "...KGGGgBBBBgGGKKWLK",
        "..KGGGGGBBBBBGGGKWWK",
        "..KYYGgGBbBbBgGYYKWK",
        "..KYYGgGBbBbBgGYYKWK",
        "..KYYKK.KBbBK.KKYYWK",
        "..KK....KBbBK...KWLK",
        ".......KGGGGGgK.KWLK",
        "......KGdddddGGKKWLK",
        "......KGGGGGGGK.KWLK",
        ".....KGdddddGGKKKWLK",
        ".....KbbKKKKbbK.KWLK",
        ".....KbbKKKKbbK.KWLK",
        ".....KBBKKKKBBK..KWK",
        ".....KBBKKKKBBK...K.",
        "....KKKKK..KKKKK....",
        "...................."
    )
    private val LYRA_CAST = listOf(
        "................KWLK",
        ".......KKKKKK..KWWLK",
        "......KGGGGGGK.KWWLK",
        ".....KGGggggGGKKWWLK",
        "....KGGyyyyyyGGKLLLK",
        "....KGYYSSSSYYGK.KWK",
        "....KGYSSPSsYYGK.KWK",
        "....KGYSssssYYGK.KWK",
        ".....KGddddddGK.KWLK",
        "....KGGGGGGGGGK.KWLK",
        "...KGGGgBBBBgGGKKWLK",
        "..KGGGGGBBBBBGGGKWWK",
        "..KYYGgGBbBbBgGYYKWK",
        "..KYYGgGBbBbBgGYYKWK",
        "..KYYKK.KBbBK.KKYYWK",
        "..KK....KBbBK...KWLK",
        ".......KGGGGGgK.KWLK",
        ".......KGGGGGgK.KWLK",
        "......KGdddddGGKKWLK",
        "......KGGGGGGGK.KWLK",
        "......KbbKKKbbK.KWLK",
        "......KbbKKKbbK.KWLK",
        "......KbbKKKbbK.KWLK",
        "......KBBKKKBBK..KWK",
        "......KBBKKKBBK...K.",
        ".....KKKKK.KKKKK...."
    )
    private val ZEPHYR_UPRIGHT = listOf(
        ".......KKKKKK.......",
        "......KVVVVVVK......",
        ".....KVVvvvvVVK.....",
        ".....KVVssssVVK.....",
        "....KVVVEPPEVVVK....",
        "....KVVVssssVVVK....",
        "....KPPKVVVVKPPK....",
        "...KPPKVVVVVVKPPK...",
        "..KOWKVVUUUUVVKWOK..",
        ".KTOWKVVUssUVVKWOTK.",
        ".KTOWKVVUUUUVVKWOTK.",
        ".KTOWKVVVVVVVVKWOTK.",
        "..KK.KVvUUUUvVK.KK..",
        ".....KVvUUUUvVK.....",
        ".....KVVVVVVVVK.....",
        ".....KVvVssVvVK.....",
        ".....KVvVssVvVK.....",
        ".....KVVVVVVVVK.....",
        ".....KVvvKKvvVK.....",
        ".....KVvvKKvvVK.....",
        ".....KVvvKKvvVK.....",
        ".....KVVVKKVVVK.....",
        ".....KVVVKKVVVK.....",
        ".....KVVsKKsVVK.....",
        "....KKKKK..KKKKK....",
        "...................."
    )
    private val ZEPHYR_CROUCH = listOf(
        "....................",
        ".......KKKKKK.......",
        "......KVVVVVVK......",
        ".....KVVvvvvVVK.....",
        ".....KVVssssVVK.....",
        "....KVVVEPPEVVVK....",
        "....KVVVssssVVVK....",
        "....KPPKVVVVKPPK....",
        "...KPPKVVVVVVKPPK...",
        "..KOWKVVUUUUVVKWOK..",
        ".KTOWKVVUssUVVKWOTK.",
        ".KTOWKVVUUUUVVKWOTK.",
        ".KTOWKVVVVVVVVKWOTK.",
        "..KK.KVvUUUUvVK.KK..",
        ".....KVvUUUUvVK.....",
        ".....KVVVVVVVVK.....",
        ".....KVvVssVvVK.....",
        ".....KVVVVVVVVK.....",
        "....KVvvKKKKvvVK....",
        "....KVvvKKKKvvVK....",
        "....KVvvKKKKvvVK....",
        "....KVVVKKKKVVVK....",
        "....KVVVKKKKVVVK....",
        "....KVVsKKKKsVVK....",
        "...KKKKK....KKKKK...",
        "...................."
    )
    private val ZEPHYR_CAST = listOf(
        "KTWOK..........KTWOK",
        ".KTWOK.KKKKKK.KOWTK.",
        "..KTWOKVVVVVVKOWTK..",
        "...KTWVVvvvvVVWTK...",
        "....KVVssssVVK......",
        "....KVVVEPPEVVVK....",
        "....KVVVssssVVVK....",
        "....KPPKVVVVKPPK....",
        "...KPPKVVVVVVKPPK...",
        "....KKVVUUUUVVKK....",
        ".....KVVUssUVVK.....",
        ".....KVVUUUUVVK.....",
        ".....KVVVVVVVVK.....",
        ".....KVvUUUUvVK.....",
        ".....KVvUUUUvVK.....",
        ".....KVVVVVVVVK.....",
        ".....KVvVssVvVK.....",
        ".....KVvVssVvVK.....",
        ".....KVVVVVVVVK.....",
        ".....KVvvKKvvVK.....",
        ".....KVvvKKvvVK.....",
        ".....KVvvKKvvVK.....",
        ".....KVVVKKVVVK.....",
        ".....KVVsKKsVVK.....",
        "....KKKKK..KKKKK....",
        "...................."
    )
    private val ORC_UPRIGHT = listOf(
        "......KK....KK......",
        ".....KHHhKKhHHK.....",
        "....KHHHHhhhhHHK....",
        "....KhhhhhhhhHHK....",
        "...KRRRRRRRRRRRRK...",
        "...KRRRYRKKRYRRRK...",
        "...KRRRRRRRRRRRRK...",
        "...KRRTTTRRRTTTRK...",
        "..KIIKRRRRRRRRKIIK..",
        ".KIIIIKRRRRRRKIIIIK.",
        ".KIIIIKRRRRRRKIIIIK.",
        "..KIIKRllRllRRKIIK..",
        "...KKRRRRRRRRRRKK...",
        "....KRllRRRRllRK....",
        "....KBBBBBBBBBBK....",
        "....KBBIBBBBIBBK....",
        "....KBBBBBBBBBBK....",
        "....KrrrKKKKrrrK....",
        "...KrrrrKKKKrrrrK...",
        "...KrrrrKKKKrrrrK...",
        "...KrrrrKKKKrrrrK...",
        "...KRRRRKKKKRRRRK...",
        "...KRRRRKKKKRRRRK...",
        "...KRRRRKKKKRRRRK...",
        "..KKKKKK....KKKKKK..",
        "...................."
    )
    private val ORC_CROUCH = listOf(
        "....................",
        "......KK....KK......",
        ".....KHHhKKhHHK.....",
        "....KHHHHhhhhHHK....",
        "....KhhhhhhhhHHK....",
        "...KRRRRRRRRRRRRK...",
        "...KRRRYRKKRYRRRK...",
        "...KRRRRRRRRRRRRK...",
        "...KRRTTTRRRTTTRK...",
        "..KIIKRRRRRRRRKIIK..",
        ".KIIIIKRRRRRRKIIIIK.",
        ".KIIIIKRllRllRIIIIK.",
        "...KKRRRRRRRRRRKK...",
        "....KRllRRRRllRK....",
        "....KBBBBBBBBBBK....",
        "....KBBIBBBBIBBK....",
        "....KBBBBBBBBBBK....",
        "...KrrrKKKKKKrrrK...",
        "..KrrrrKKKKKKrrrrK..",
        "..KrrrrKKKKKKrrrrK..",
        "..KrrrrKKKKKKrrrrK..",
        "..KRRRRKKKKKKRRRRK..",
        "..KRRRRKKKKKKRRRRK..",
        "..KRRRRKKKKKKRRRRK..",
        ".KKKKKK......KKKKKK.",
        "...................."
    )
    private val ARCHER_UPRIGHT = listOf(
        ".......KKKKKK.......",
        "......KCCCCCCK......",
        ".....KCCCCCCCCK.....",
        "....KCCCCccCCCCK....",
        "....KCCBBBBBBCCK....",
        "....KCBEKKKKEBCK....",
        "....KCCBBBBBBCCK.KWK",
        ".....KCBBbBBCK..KWWK",
        "....KCCCCCCCCK..KAAK",
        "...KCCCCccCCCCKKPAAK",
        "..KCCccCBBCCCccKWAAK",
        "..KCCccCBBCCCccKKWWK",
        "...KCCCCBBCCCCK.KWWK",
        "...KCCCCccCCCCK..KWK",
        "...KCCCCCCCCCCK..KWK",
        "...KcCCcCCcCCcK..KWK",
        "....KcCK..KcCK...KWK",
        "....KbbK..KbbK...KWK",
        "....KbbK..KbbK...KWK",
        "....KbbK..KbbK...KWK",
        "....KbbK..KbbK...KWK",
        "....KbbK..KbbK...KWK",
        "....KBBK..KBBK...KWK",
        "....KBBK..KBBK....K.",
        "...KKKKK..KKKKK.....",
        "...................."
    )
    private val ARCHER_CROUCH = listOf(
        "....................",
        ".......KKKKKK.......",
        "......KCCCCCCK......",
        ".....KCCCCCCCCK.....",
        "....KCCCCccCCCCK....",
        "....KCCBBBBBBCCK....",
        "....KCBEKKKKEBCK....",
        "....KCCBBBBBBCCK.KWK",
        ".....KCBBbBBCK..KWWK",
        "....KCCCCCCCCK..KAAK",
        "...KCCCCccCCCCKKPAAK",
        "..KCCccCBBCCCccKWAAK",
        "..KCCccCBBCCCccKKWWK",
        "...KCCCCBBCCCCK.KWWK",
        "...KCCCCccCCCCK..KWK",
        "...KCCCCCCCCCCK..KWK",
        "...KcCCcCCcCCcK..KWK",
        "...KcCK....KcCK..KWK",
        "...KbbK....KbbK..KWK",
        "...KbbK....KbbK..KWK",
        "...KbbK....KbbK..KWK",
        "...KbbK....KbbK..KWK",
        "...KBBK....KBBK..KWK",
        "...KBBK....KBBK...K.",
        "..KKKKK....KKKKK....",
        "...................."
    )
    private val SHAMAN_UPRIGHT = listOf(
        "....KK........KK....",
        "...KmMMK....KmMMK...",
        "....KMMKKKKKKMMK....",
        "....KMMMMMMMMMMK....",
        "....KMMEMMKKMEMK....",
        "....KMMMMMMMMMMK....",
        ".....KMMmMMmMMK.....",
        "....KVVVVVVVVVVK....",
        "...KVVVVvvvvVVVVK...",
        "..KVVvvVVVVVVvvVVK..",
        ".KOKVVvVVvvVVvVVKOK.",
        "KoPOKVVVVVVVVVVKOPOK",
        ".KOKVVvVVvvVVvVVKOK.",
        "..KKVVvvvvvvvvVVK...",
        "...KVVVVVVVVVVVVK...",
        "...KVVvvVVVVvvVVK...",
        "...KVVVVVVVVVVVVK...",
        "...KVVvvVVVVvvVVK...",
        "...KVVVVVVVVVVVVK...",
        "..KVVVvVVVVVVvVVVK..",
        "..KVVVVVVVVVVVVVVK..",
        ".KVVVVVVVVVVVVVVVVK.",
        ".KvvVVVvvvvvvVVVvvK.",
        ".KvvvKKKKKKKKKKvvvK.",
        "..KKK..........KKK..",
        "...................."
    )
    private val SHAMAN_CROUCH = listOf(
        "....................",
        "....KK........KK....",
        "...KmMMK....KmMMK...",
        "....KMMKKKKKKMMK....",
        "....KMMMMMMMMMMK....",
        "....KMMEMMKKMEMK....",
        "....KMMMMMMMMMMK....",
        ".....KMMmMMmMMK.....",
        "....KVVVVVVVVVVK....",
        "...KVVVVvvvvVVVVK...",
        "..KVVvvVVVVVVvvVVK..",
        ".KOKVVvVVvvVVvVVKOK.",
        "KoPOKVVVVVVVVVVKOPOK",
        ".KOKVVvVVvvVVvVVKOK.",
        "..KKVVvvvvvvvvVVK...",
        "...KVVVVVVVVVVVVK...",
        "...KVVvvVVVVvvVVK...",
        "...KVVVVVVVVVVVVK...",
        "..KVVVvVVVVVVvVVVK..",
        "..KVVVVVVVVVVVVVVK..",
        ".KVVVVVVVVVVVVVVVVK.",
        ".KvvVVVvvvvvvVVVvvK.",
        ".KvvvKKKKKKKKKKvvvK.",
        "..KKK..........KKK..",
        "....................",
        "...................."
    )

    fun getPixelMatrix(
        characterId: String,
        frame: SpriteFrame
    ): Pair<List<String>, Map<Char, Color>> {
        return when (characterId.lowercase()) {
            "hero", "aethel" -> {
                val grid = when (frame) {
                    SpriteFrame.IDLE_UPRIGHT -> AETHEL_UPRIGHT
                    SpriteFrame.IDLE_CROUCH -> AETHEL_CROUCH
                    SpriteFrame.ACTION_CAST -> AETHEL_CAST
                    SpriteFrame.DAMAGED -> AETHEL_CROUCH
                }
                grid to AETHEL_PALETTE
            }
            "cedric" -> {
                val grid = when (frame) {
                    SpriteFrame.IDLE_UPRIGHT -> CEDRIC_UPRIGHT
                    SpriteFrame.IDLE_CROUCH -> CEDRIC_CROUCH
                    SpriteFrame.ACTION_CAST -> CEDRIC_CAST
                    SpriteFrame.DAMAGED -> CEDRIC_CROUCH
                }
                grid to CEDRIC_PALETTE
            }
            "lyra" -> {
                val grid = when (frame) {
                    SpriteFrame.IDLE_UPRIGHT -> LYRA_UPRIGHT
                    SpriteFrame.IDLE_CROUCH -> LYRA_CROUCH
                    SpriteFrame.ACTION_CAST -> LYRA_CAST
                    SpriteFrame.DAMAGED -> LYRA_CROUCH
                }
                grid to LYRA_PALETTE
            }
            "zephyr" -> {
                val grid = when (frame) {
                    SpriteFrame.IDLE_UPRIGHT -> ZEPHYR_UPRIGHT
                    SpriteFrame.IDLE_CROUCH -> ZEPHYR_CROUCH
                    SpriteFrame.ACTION_CAST -> ZEPHYR_CAST
                    SpriteFrame.DAMAGED -> ZEPHYR_CROUCH
                }
                grid to ZEPHYR_PALETTE
            }
            else -> {
                val lowerId = characterId.lowercase()
                when {
                    lowerId == "orc" || lowerId.contains("vanguard") || lowerId.contains("ironclad") ||
                            lowerId.contains("guard") || lowerId.contains("captain") || lowerId.contains("brute") ||
                            lowerId.contains("minion") || lowerId.contains("knight") -> {
                        val grid = when (frame) {
                            SpriteFrame.IDLE_UPRIGHT -> ORC_UPRIGHT
                            else -> ORC_CROUCH
                        }
                        grid to ORC_PALETTE
                    }
                    lowerId == "archer" || lowerId.contains("sniper") || lowerId.contains("arbalest") ||
                            lowerId.contains("marksman") || lowerId.contains("scout") || lowerId.contains("bone_archer") -> {
                        val grid = when (frame) {
                            SpriteFrame.IDLE_UPRIGHT -> ARCHER_UPRIGHT
                            else -> ARCHER_CROUCH
                        }
                        grid to ARCHER_PALETTE
                    }
                    lowerId == "shaman" || lowerId.contains("occultist") || lowerId.contains("warlock") ||
                            lowerId.contains("acolyte") || lowerId.contains("wisp") || lowerId.contains("broodmother") ||
                            lowerId.contains("summoner") || lowerId.contains("witch") || lowerId.contains("phantom") ||
                            lowerId.contains("ghoul") || lowerId.contains("bone") -> {
                        val grid = when (frame) {
                            SpriteFrame.IDLE_UPRIGHT -> SHAMAN_UPRIGHT
                            else -> SHAMAN_CROUCH
                        }
                        grid to SHAMAN_PALETTE
                    }
                    lowerId.contains("dummy") -> {
                        val grid = when (frame) {
                            SpriteFrame.DAMAGED -> DUMMY_CROUCH
                            else -> DUMMY_UPRIGHT
                        }
                        grid to DUMMY_PALETTE
                    }
                    lowerId == "hero" || lowerId.contains("aethel") -> {
                        val grid = when (frame) {
                            SpriteFrame.IDLE_UPRIGHT -> AETHEL_UPRIGHT
                            SpriteFrame.IDLE_CROUCH -> AETHEL_CROUCH
                            SpriteFrame.ACTION_CAST -> AETHEL_CAST
                            SpriteFrame.DAMAGED -> AETHEL_CROUCH
                        }
                        grid to AETHEL_PALETTE
                    }
                    else -> {
                        val grid = when (frame) {
                            SpriteFrame.IDLE_UPRIGHT -> ORC_UPRIGHT
                            else -> ORC_CROUCH
                        }
                        grid to ORC_PALETTE
                    }
                }
            }
        }
    }
}
