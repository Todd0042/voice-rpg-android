package com.voicerpg.android.engine

import com.voicerpg.android.model.DialogueChoice
import com.voicerpg.android.model.DialogueNode
import com.voicerpg.android.model.DialogueSpeaker
import com.voicerpg.android.model.SpeakerSide
import com.voicerpg.android.model.StoryScene

object StoryScript {

    // -------------------------------------------------------------------------
    // Story Scenes
    // -------------------------------------------------------------------------
    val SCENE_COTTAGE = StoryScene(
        id = "scene_cottage",
        name = "Aethel's Cottage",
        chapterTitle = "Prologue: The Morning Without Echo",
        backgroundAsset = "story/cottage_bedroom.jpg",
        initialNodeId = "cottage_intro",
        ambientDescription = "Sunlight streams through the window into a silent room."
    )

    val SCENE_VILLAGE = StoryScene(
        id = "scene_village",
        name = "Whispering Pines",
        chapterTitle = "Prologue: The Ashen Statues",
        backgroundAsset = "story/village_square.jpg",
        initialNodeId = "village_intro",
        ambientDescription = "Eerie mist drifts past obsidian crystallized statues."
    )

    val SCENE_CROSSROADS = StoryScene(
        id = "scene_crossroads",
        name = "The Sun Shrine Crossroads",
        chapterTitle = "Chapter 1: The Oathkeeper of Dawn",
        backgroundAsset = "story/forest_crossroads.jpg",
        initialNodeId = "crossroads_intro",
        ambientDescription = "Ancient pines tower over a weathered sun shrine."
    )

    val SCENE_CAMP = StoryScene(
        id = "scene_camp",
        name = "Camp of the Fellowship",
        chapterTitle = "Chapter 2: Embers in the Gloom",
        backgroundAsset = "story/fellowship_camp.jpg",
        initialNodeId = "camp_intro",
        ambientDescription = "Warm embers dance in the night air beside the ancient sun shrine."
    )

    val SCENE_CAVE = StoryScene(
        id = "scene_cave",
        name = "The Whispering Caverns",
        chapterTitle = "Chapter 2: The Sunken Grotto",
        backgroundAsset = "story/caverns.jpg",
        initialNodeId = "cavern_entry",
        ambientDescription = "Bioluminescent azure crystals hum faintly along damp limestone walls."
    )

    val SCENE_SWAMP = StoryScene(
        id = "scene_swamp",
        name = "The Rotting Marsh",
        chapterTitle = "Chapter 2: The Sunken Bog",
        backgroundAsset = "story/rotting_marsh.jpg",
        initialNodeId = "marsh_entry",
        ambientDescription = "Thick emerald mist drifts over black mire and gnarled roots."
    )

    val SCENE_AQUEDUCT = StoryScene(
        id = "scene_aqueduct",
        name = "The Aqueducts of Solaria",
        chapterTitle = "Chapter 3: The Ascent of Solaria",
        backgroundAsset = "story/aqueducts.jpg",
        initialNodeId = "chapter3_intro",
        ambientDescription = "Colossal limestone arches rise above the mist as ancient waterfalls hang petrified in obsidian glass."
    )

    val SCENE_DUNGEON = StoryScene(
        id = "scene_dungeon",
        name = "Crypt of the Foundation",
        chapterTitle = "Chapter 4: The Silent Catacombs",
        backgroundAsset = "story/foundation_crypt.jpg",
        initialNodeId = "chapter4_intro",
        ambientDescription = "Ancient mosaic pillars of the Primordial Chanters lie buried beneath the Bell Tower foundations."
    )

    val SCENE_TOWER = StoryScene(
        id = "scene_tower",
        name = "The Solaria Bell Chamber",
        chapterTitle = "Chapter 4: The Great Bell of Solaria",
        backgroundAsset = "story/bell_chamber.jpg",
        initialNodeId = "ch4_tower_ascent",
        ambientDescription = "High above the cloudline, the massive bronze Bell of Solaria hangs beneath open gothic parapets."
    )

    val SCENE_MARSH_FANE = StoryScene(
        id = "scene_marsh_fane",
        name = "The Drowned Fane",
        chapterTitle = "Chapter 5: The Severed Resonance",
        backgroundAsset = "story/marsh_fane.jpg",
        initialNodeId = "ch5_intro",
        ambientDescription = "Murky emerald waters lap against sunken gothic pillars and twisted weeping willow roots.",
        musicAsset = "audio/music/bgm_act2_marsh.ogg"
    )

    val SCENE_WILLOW_SANCTUARY = StoryScene(
        id = "scene_willow_sanctuary",
        name = "The Weeping Willow Sanctuary",
        chapterTitle = "Chapter 6: The Warden's Oath",
        backgroundAsset = "story/willow_sanctuary.jpg",
        initialNodeId = "ch6_intro",
        ambientDescription = "Bioluminescent emerald motes float among the hanging moss of the ancient sacred willow.",
        musicAsset = "audio/music/bgm_act2_marsh.ogg"
    )

    val SCENE_SUNKEN_CATACOMBS = StoryScene(
        id = "scene_sunken_catacombs",
        name = "The Sunken Catacombs",
        chapterTitle = "Chapter 7: Tuning the Veridian Chime",
        backgroundAsset = "story/sunken_catacombs.jpg",
        initialNodeId = "ch7_intro",
        ambientDescription = "Jade-infused bronze rings over crystal clear waters as ancient stone pathways emerge from the bog.",
        musicAsset = "audio/music/bgm_act2_marsh.ogg"
    )

    val SCENE_SHADOWED_CRAGS = StoryScene(
        id = "scene_shadowed_crags",
        name = "The Shadowed Crags",
        chapterTitle = "Chapter 8: The Shadowed Crags & Zephyr's Defection",
        backgroundAsset = "story/shadowed_crags.jpg",
        initialNodeId = "ch8_intro",
        ambientDescription = "Razor obsidian crags tower over a cold canyon shrouded in purple mountain mist.",
        musicAsset = "audio/music/bgm_act2_marsh.ogg"
    )

    val SCENE_MAUSOLEUM = StoryScene(
        id = "scene_mausoleum",
        name = "The Mausoleum of the Sun",
        chapterTitle = "Chapter 9: The Broken Vow of Dawn",
        backgroundAsset = "story/mausoleum_sun.jpg",
        initialNodeId = "ch9_intro",
        ambientDescription = "Shattered marble statues of the Golden Chime knights lie beneath weeping golden sunburst banners.",
        musicAsset = "audio/music/bgm_act3_bastion.ogg"
    )

    val SCENE_EMERALD_CHOIR = StoryScene(
        id = "scene_emerald_choir",
        name = "The Emerald Choir Grove",
        chapterTitle = "Chapter 10: The Song of the Mute Grove",
        backgroundAsset = "story/emerald_choir.jpg",
        initialNodeId = "ch10_intro",
        ambientDescription = "Petrified dryads stand frozen around a dark spring choked in obsidian silt.",
        musicAsset = "audio/music/bgm_act3_bastion.ogg"
    )

    val SCENE_BLIND_GORGE = StoryScene(
        id = "scene_blind_gorge",
        name = "The Blind Gorge",
        chapterTitle = "Chapter 11: The Silent Blade's Reckoning",
        backgroundAsset = "story/blind_gorge.jpg",
        initialNodeId = "ch11_intro",
        ambientDescription = "Thick silence and shadow mist cling to the jagged canyon hideout of the Black Guild.",
        musicAsset = "audio/music/bgm_act3_bastion.ogg"
    )

    val SCENE_CLOCKWORK_BASTION = StoryScene(
        id = "scene_clockwork_bastion",
        name = "The Clockwork Bastion of Ouros",
        chapterTitle = "Chapter 12: Awakening the Third Bell",
        backgroundAsset = "story/clockwork_bastion.jpg",
        initialNodeId = "ch12_intro",
        ambientDescription = "Colossal brass cogs and steam pipes hum within the towering iron belfry of Ouros.",
        musicAsset = "audio/music/bgm_act3_bastion.ogg"
    )

    val SCENE_SILENT_CITADEL = StoryScene(
        id = "scene_silent_citadel",
        name = "The Silent Citadel Gates",
        chapterTitle = "Chapter 13: Breach of the Silent Citadel",
        backgroundAsset = "story/silent_citadel_gates.jpg",
        initialNodeId = "ch13_intro",
        ambientDescription = "Banners of the Mute Sovereign hang from monolithic black glass battlements before Sol-Aethel.",
        musicAsset = "audio/music/bgm_act4_celestial.ogg"
    )

    val SCENE_VOID_RESERVOIR = StoryScene(
        id = "scene_void_reservoir",
        name = "The Void Reservoir",
        chapterTitle = "Chapter 14: The Void Reservoir",
        backgroundAsset = "story/void_reservoir.jpg",
        initialNodeId = "ch14_intro",
        ambientDescription = "A cosmic lake of pure liquid silence that drinks all echoes high above the clouds.",
        musicAsset = "audio/music/bgm_act4_celestial.ogg"
    )

    val SCENE_CELESTIAL_SPIRE = StoryScene(
        id = "scene_celestial_spire",
        name = "The Celestial Ribbon Stair",
        chapterTitle = "Chapter 15: Ascent of the Celestial Spire",
        backgroundAsset = "story/celestial_stair.jpg",
        initialNodeId = "ch15_intro",
        ambientDescription = "A ribbon staircase of crystallized harmonic light rises toward the aurora of the stars.",
        musicAsset = "audio/music/bgm_act4_celestial.ogg"
    )

    val SCENE_FINAL_SUMMIT = StoryScene(
        id = "scene_final_summit",
        name = "The Spire Summit — Bell of Eternity",
        chapterTitle = "Chapter 16: The Primordial Syllable",
        backgroundAsset = "story/final_summit.jpg",
        initialNodeId = "ch16_intro",
        ambientDescription = "The colossal Fourth Great Bell hangs beneath cosmic auroras where Grand Inquisitor Malakor waits.",
        musicAsset = "audio/music/bgm_act4_celestial.ogg"
    )

    val SCENE_OBSIDIAN_VAULTS = StoryScene(
        id = "scene_obsidian_vaults",
        name = "The Obsidian Vaults of the Black Guild",
        chapterTitle = "Chapter 11: The Silent Blade's Reckoning",
        backgroundAsset = "story/obsidian_vaults.jpg",
        initialNodeId = "ch11_vaults_entry",
        ambientDescription = "Racks of severed tongues sealed in lead, and the ledgers that record them, line the guild's subterranean reliquary.",
        musicAsset = "audio/music/bgm_act3_bastion.ogg"
    )

    val SCENE_UMBRAL_TRENCH = StoryScene(
        id = "scene_umbral_trench",
        name = "The Umbral Trench",
        chapterTitle = "Chapter 14: The Void Reservoir",
        backgroundAsset = "story/umbral_trench.jpg",
        initialNodeId = "ch14_trench_entry",
        ambientDescription = "Starlight is muted here; the stolen voices of the drowned pool thicken into a living, breathing dark.",
        musicAsset = "audio/music/bgm_act4_celestial.ogg"
    )

    val SCENE_CELESTIAL_VESTIBULE = StoryScene(
        id = "scene_celestial_vestibule",
        name = "The Vestibule of Echoes",
        chapterTitle = "Chapter 16: The Primordial Syllable",
        backgroundAsset = "story/vestibule_echoes.jpg",
        initialNodeId = "ch16_vestibule_entry",
        ambientDescription = "Frozen supplicants kneel in rings around four pillars of glass, each a severed fragment of the world's song.",
        musicAsset = "audio/music/bgm_act4_celestial.ogg"
    )

    val SCENE_EPILOGUE = StoryScene(
        id = "scene_epilogue",
        name = "Whispering Pines Awakened",
        chapterTitle = "Epilogue: The Great Awakening",
        backgroundAsset = "story/village_bright.jpg",
        initialNodeId = "epilogue_awakening",
        ambientDescription = "Golden sunlight bathes the awakened village square as songbirds fill the living pines.",
        musicAsset = "audio/music/bgm_act4_celestial.ogg"
    )

    val ALL_SCENES = mapOf(
        SCENE_COTTAGE.id to SCENE_COTTAGE,
        SCENE_VILLAGE.id to SCENE_VILLAGE,
        SCENE_CROSSROADS.id to SCENE_CROSSROADS,
        SCENE_CAMP.id to SCENE_CAMP,
        SCENE_CAVE.id to SCENE_CAVE,
        SCENE_SWAMP.id to SCENE_SWAMP,
        SCENE_AQUEDUCT.id to SCENE_AQUEDUCT,
        SCENE_DUNGEON.id to SCENE_DUNGEON,
        SCENE_TOWER.id to SCENE_TOWER,
        SCENE_MARSH_FANE.id to SCENE_MARSH_FANE,
        SCENE_WILLOW_SANCTUARY.id to SCENE_WILLOW_SANCTUARY,
        SCENE_SUNKEN_CATACOMBS.id to SCENE_SUNKEN_CATACOMBS,
        SCENE_SHADOWED_CRAGS.id to SCENE_SHADOWED_CRAGS,
        SCENE_MAUSOLEUM.id to SCENE_MAUSOLEUM,
        SCENE_EMERALD_CHOIR.id to SCENE_EMERALD_CHOIR,
        SCENE_BLIND_GORGE.id to SCENE_BLIND_GORGE,
        SCENE_CLOCKWORK_BASTION.id to SCENE_CLOCKWORK_BASTION,
        SCENE_SILENT_CITADEL.id to SCENE_SILENT_CITADEL,
        SCENE_VOID_RESERVOIR.id to SCENE_VOID_RESERVOIR,
        SCENE_CELESTIAL_SPIRE.id to SCENE_CELESTIAL_SPIRE,
        SCENE_FINAL_SUMMIT.id to SCENE_FINAL_SUMMIT,
        SCENE_OBSIDIAN_VAULTS.id to SCENE_OBSIDIAN_VAULTS,
        SCENE_UMBRAL_TRENCH.id to SCENE_UMBRAL_TRENCH,
        SCENE_CELESTIAL_VESTIBULE.id to SCENE_CELESTIAL_VESTIBULE,
        SCENE_EPILOGUE.id to SCENE_EPILOGUE
    )

    // -------------------------------------------------------------------------
    // Dialogue Graph
    // -------------------------------------------------------------------------
    val ALL_NODES: Map<String, DialogueNode> = listOf(
        // === SCENE 1: COTTAGE BEDROOM ===
        DialogueNode(
            id = "cottage_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Morning sunlight spills across the wooden planks of your cottage. Yet something is desperately wrong. Dust motes hang frozen in the air. The mantel clock has ceased ticking. An absolute, deafening silence covers the world.",
            choices = listOf(
                DialogueChoice("c1_look", "Examine the cold fireplace", listOf("fireplace", "hearth", "examine", "cold"), "cottage_fireplace"),
                DialogueChoice("c1_window", "Look out the window", listOf("window", "look", "outside", "pines"), "cottage_window"),
                DialogueChoice("c1_speak", "Try to speak", listOf("speak", "voice", "talk", "hello"), "cottage_voice")
            )
        ),
        DialogueNode(
            id = "cottage_fireplace",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "The ash is stone cold... as if heat itself was stolen away. Even when I stir the embers with the iron poker, not a single scrape makes a sound.",
            nextNodeId = "cottage_voice"
        ),
        DialogueNode(
            id = "cottage_window",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "The tall pines outside stand completely unmoving. No wind rustle. No birdsong. The village of Whispering Pines lies under an eerie gray veil of fog.",
            nextNodeId = "cottage_voice"
        ),
        DialogueNode(
            id = "cottage_voice",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Hello...? Is anyone out there?!",
            nextNodeId = "cottage_sparks"
        ),
        DialogueNode(
            id = "cottage_sparks",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The moment you whisper, incandescent azure sparks erupt from your lips! A surge of tingling warmth races through your chest. Your voice... it has weight. It has physical power.",
            choices = listOf(
                DialogueChoice("c2_chant", "Chant 'Fireball!' to test your flame", listOf("fireball", "flame", "fire", "chant", "test"), "cottage_flame_test"),
                DialogueChoice("c2_outside", "Hurry outside into the village", listOf("outside", "village", "hurry", "leave", "step"), "cottage_to_village")
            )
        ),
        DialogueNode(
            id = "cottage_flame_test",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "By the ancient spark... Fireball!",
            nextNodeId = "cottage_flame_reaction"
        ),
        DialogueNode(
            id = "cottage_flame_reaction",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "A roaring sphere of golden pyromancy bursts into life above your palm, illuminating the cabin with radiant warmth! The Logos lives within you. You must see what has happened to the village.",
            nextNodeId = "cottage_to_village"
        ),
        DialogueNode(
            id = "cottage_to_village",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "The whole village is silent. I have to check on the others.",
            changeSceneId = SCENE_VILLAGE.id,
            nextNodeId = "village_intro"
        ),

        // === SCENE 2: VILLAGE SQUARE ===
        DialogueNode(
            id = "village_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You push open the door and step onto the cobblestones of Whispering Pines. In the square stands a horrifying monument: a townsman petrified into pitch-black obsidian, frozen mid-step with hollow crying eyes.",
            choices = listOf(
                DialogueChoice("v1_inspect", "Inspect the obsidian statue", listOf("inspect", "statue", "obsidian", "examine"), "village_statue_inspect"),
                DialogueChoice("v1_call", "Call out for any survivors", listOf("call", "survivors", "shout", "anyone"), "village_call")
            )
        ),
        DialogueNode(
            id = "village_statue_inspect",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Their vocal cords have turned into black glass... The Silent Blight has stolen their voices and their souls.",
            nextNodeId = "village_ambush"
        ),
        DialogueNode(
            id = "village_call",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Is anybody alive?! Answer me!",
            nextNodeId = "village_ambush"
        ),
        DialogueNode(
            id = "village_ambush",
            speaker = DialogueSpeaker.SHADOW_WISP,
            side = SpeakerSide.RIGHT,
            text = "Sssssshhhhh... Silence the spark... Surrender your tongue to the Great Stillness...",
            nextNodeId = "village_battle_trigger"
        ),
        DialogueNode(
            id = "village_battle_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Two Shadow Wisps glide from the mist, their razor talons aimed at your throat! Speak your incantations to defend your life!",
            triggerBattleEncounterId = "prologue_solo"
        ),
        DialogueNode(
            id = "village_post_battle",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "The flame shattered their obsidian bodies! If the entire realm is falling to this Silence, the ancient Bell Towers must be re-chimed. I must follow the old western road.",
            changeSceneId = SCENE_CROSSROADS.id,
            nextNodeId = "crossroads_intro"
        ),

        // === SCENE 3: FOREST CROSSROADS ===
        DialogueNode(
            id = "crossroads_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You follow the trail through the ancient pine woods. Ahead, resting against a weathered stone sun-shrine, sits a heavily armored knight. Battered golden plate shines beneath his crimson mantle.",
            nextNodeId = "crossroads_cedric_first"
        ),
        DialogueNode(
            id = "crossroads_cedric_first",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Halt, traveler! Draw no closer! The woods are overrun with blighted beasts... Wait. You... you can speak?!",
            choices = listOf(
                DialogueChoice("cr_yes", "Yes! My voice commands the flame!", listOf("flame", "fire", "magic", "commands", "yes"), "crossroads_cedric_reply_mage"),
                DialogueChoice("cr_who", "Who are you, noble knight?", listOf("who", "knight", "identity", "name"), "crossroads_cedric_reply_who")
            )
        ),
        DialogueNode(
            id = "crossroads_cedric_reply_mage",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "By the First Dawn... an Awakened Invocator! I thought the sacred fire of the Logos was extinguished when the capital fell. I am Sir Cedric of the Golden Chime!",
            nextNodeId = "crossroads_warning"
        ),
        DialogueNode(
            id = "crossroads_cedric_reply_who",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "I am Sir Cedric, Oathkeeper of the Golden Chime. I swore to guard the holy way-shrine against the Mute Sovereign's hordes.",
            nextNodeId = "crossroads_warning"
        ),
        DialogueNode(
            id = "crossroads_warning",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Listen! Through the brush... heavy footfalls without a sound. An orc vanguard and their archers approach!",
            nextNodeId = "crossroads_unite"
        ),
        DialogueNode(
            id = "crossroads_unite",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Then let us stand together, Sir Cedric! My incantations will burn them, your shield will hold the line!",
            nextNodeId = "crossroads_battle_trigger"
        ),
        DialogueNode(
            id = "crossroads_battle_trigger",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "By celestial dawn, smite the heretic! To battle, Invocator!",
            triggerBattleEncounterId = "forest_ambush"
        ),
        DialogueNode(
            id = "crossroads_post_battle",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Glorious! Your voice pierces the darkness like the morning sun! I have not heard an incantation of such pure resonance since the high cathedral of Sol-Aethel was silenced.",
            choices = listOf(
                DialogueChoice("cr_ask_towers", "Ask Cedric about the Four Great Bell Towers", listOf("towers", "bells", "four", "solaria", "ask"), "crossroads_lore_towers"),
                DialogueChoice("cr_ask_blight", "Ask how the Silent Blight overthrew the kingdom", listOf("blight", "silence", "sovereign", "kingdom", "overthrew"), "crossroads_lore_blight")
            )
        ),
        DialogueNode(
            id = "crossroads_lore_towers",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The Four Towers were built by the Primordial Chanters: the Belfry of Solaria in the east, the Veridian Chime of the Emerald Choir in the marsh, the Resonant Bastion of Ouros in the north, and the Bell of Eternity at the capital. When rung in harmony, their sacred chimes generate an acoustic ward that shields every living soul from the Blight.",
            nextNodeId = "crossroads_conclusion"
        ),
        DialogueNode(
            id = "crossroads_lore_blight",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "It happened during the Grand Concordance. The Mute Sovereign inverted the holy chimes, turning our words into petrifying obsidian glass. Entire armies were frozen mid-battle cry. Only those with the dormant spark of the Logos can speak and awaken the bells.",
            nextNodeId = "crossroads_conclusion"
        ),
        DialogueNode(
            id = "crossroads_conclusion",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Then our purpose is clear. My voice and your shield will reignite the chimes of Aethelgard. The Fellowship of Echoes begins today!",
            nextNodeId = "crossroads_camp_trans"
        ),
        DialogueNode(
            id = "crossroads_camp_trans",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "As twilight settles over the whispering woods, you and Sir Cedric strike camp near the weathered sun shrine. A warm campfire crackles between you, keeping the biting chill of the Blight at bay.",
            changeSceneId = SCENE_CAMP.id,
            nextNodeId = "camp_intro"
        ),
        DialogueNode(
            id = "camp_intro",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Rest your vocal cords, friend. Before we cross into the Sunken Hollows tomorrow, our camp preparations remain. What shall we attend to?",
            choices = listOf(
                DialogueChoice("c_lore", "Scout the woods for Blighted trackers", listOf("scout", "trackers", "woods", "prowlers", "blight"), "camp_scout_entry", completionFlag = "substory_blight_complete"),
                DialogueChoice("c_towers", "Inspect the Sun Shrine ruins for ancient chime lore", listOf("shrine", "ruins", "chime", "altar", "towers"), "camp_shrine_entry", completionFlag = "substory_towers_complete"),
                DialogueChoice("c_rest", "Rest by the campfire and take the midnight vigil", listOf("rest", "vigil", "sleep", "fire", "restore"), "camp_vigil_entry", completionFlag = "substory_rest_complete")
            )
        ),

        // === SUB-STORY X: SCOUT THE WOODS FOR TRACKERS ===
        DialogueNode(
            id = "camp_scout_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You and Sir Cedric venture into the dark tree line bordering the camp. Pitch-black obsidian claw marks scar the pine bark. Fresh tracks lead toward a shadowy outcrop.",
            choices = listOf(
                DialogueChoice("c_scout_tracks", "Cast an azure spark to illuminate the tracks", listOf("spark", "illuminate", "light", "tracks"), "camp_scout_tracks"),
                DialogueChoice("c_scout_rush", "Draw weapons and rush the sound in the bracken", listOf("rush", "draw", "weapons", "sound", "attack"), "camp_scout_rush")
            )
        ),
        DialogueNode(
            id = "camp_scout_tracks",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Wise thinking! The luminescence exposes two obsidian stalkers attempting to circle our eastern flank! Stand your ground!",
            nextNodeId = "camp_scout_ambush"
        ),
        DialogueNode(
            id = "camp_scout_rush",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "I hear their ragged breathing in the brush! Sir Cedric, with me!",
            nextNodeId = "camp_scout_ambush"
        ),
        DialogueNode(
            id = "camp_scout_ambush",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Two Blighted Trackers burst from the darkness, razor obsidian claws poised to strike! Defend the fellowship's camp!",
            triggerBattleEncounterId = "blight_trackers"
        ),
        DialogueNode(
            id = "camp_scout_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The prowlers shatter into inert ash! From their belt satchel, I've recovered an obsidian order cipher from the Mute Sovereign — their vanguard is fortifying the Bell Tower. Our perimeter is now secure!",
            setFlagOnEnter = "substory_blight_complete",
            nextNodeId = "camp_return_hub"
        ),

        // === SUB-STORY Y: SUN SHRINE RUINS & ECHO CHIME RELIC ===
        DialogueNode(
            id = "camp_shrine_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Behind the campfire stands the weathered sun shrine. Its granite altar is inscribed with ancient musical clefs and celestial dials left by the Primordial Chanters.",
            choices = listOf(
                DialogueChoice("c_shrine_chant", "Chant the three sacred notes inscribed on the altar", listOf("chant", "notes", "sing", "sacred"), "camp_shrine_chant"),
                DialogueChoice("c_shrine_dial", "Align the astrological dial with the Dawn constellation", listOf("dial", "astrological", "align", "constellation"), "camp_shrine_dial")
            )
        ),
        DialogueNode(
            id = "camp_shrine_chant",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Sol... Aeterna... Cantus! Let the ancient harmonic awaken!",
            nextNodeId = "camp_shrine_relic"
        ),
        DialogueNode(
            id = "camp_shrine_dial",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The gears click into place! Look — the stone pedestal is sliding open with a golden glow!",
            nextNodeId = "camp_shrine_relic"
        ),
        DialogueNode(
            id = "camp_shrine_relic",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "A radiant golden tuning relic ascends from the altar — the Echo Chime of Solaria! It hums in harmony with your voice.",
            nextNodeId = "camp_shrine_lore"
        ),
        DialogueNode(
            id = "camp_shrine_lore",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "By the First Dawn... that tuning chime was forged to break the Sovereign's acoustic lock on the First Bell Tower. We now possess the key to the gates!",
            setFlagOnEnter = "substory_towers_complete",
            nextNodeId = "camp_return_hub"
        ),

        // === SUB-STORY Z: REST & MIDNIGHT VIGIL ===
        DialogueNode(
            id = "camp_vigil_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Midnight cloaks the forest in silence. Sir Cedric quietly feeds dry pine boughs into the fire as glowing embers rise toward the starry sky.",
            choices = listOf(
                DialogueChoice("c_vigil_bond", "Ask Cedric what binds his oath against the Blight", listOf("oath", "sister", "binds", "story", "ask"), "camp_vigil_story"),
                DialogueChoice("c_vigil_meditate", "Focus on harmonic breathwork and restoration", listOf("meditate", "breathe", "heal", "focus", "restoration"), "camp_vigil_meditate")
            )
        ),
        DialogueNode(
            id = "camp_vigil_story",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "My sister was in the capital when the silence fell. Her voice was stolen... turned to obsidian before my eyes. I swore on my shield that until all four bells chime again, my sword belongs to the Logos.",
            nextNodeId = "camp_vigil_restored"
        ),
        DialogueNode(
            id = "camp_vigil_meditate",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Warmth fills my chest. The primordial embers rekindle the harmonic flow within our souls.",
            nextNodeId = "camp_vigil_restored"
        ),
        DialogueNode(
            id = "camp_vigil_restored",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "A profound calm settles over the camp. The party's health and mana are completely restored, and fellowship morale reaches its peak!",
            setFlagOnEnter = "substory_rest_complete",
            nextNodeId = "camp_return_hub"
        ),

        // === CAMP RETURN HUB ===
        DialogueNode(
            id = "camp_return_hub",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You return to the warm circle of the campfire.",
            nextNodeId = "camp_intro"
        ),
        DialogueNode(
            id = "camp_all_completed",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Look toward the northern ridge — dawn is breaking! The perimeter is secure, the Echo Chime is in hand, and our spirits are whole. Invocator, our camp preparations are complete. The First Bell Tower awaits!",
            nextNodeId = "chapter3_intro"
        ),

        // === CHAPTER 3: THE SUNKEN AQUEDUCTS OF SOLARIA ===
        DialogueNode(
            id = "chapter3_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "CHAPTER 3: THE ASCENT OF SOLARIA. Leaving the warmth of the camp behind, you and Sir Cedric climb the craggy mountain ridges toward the colossal stone aqueducts of the First Bell Tower. Looming in the morning mist, monumental arches span the gorge, yet the grand portal is choked in pulsing obsidian vines.",
            changeSceneId = SCENE_AQUEDUCT.id,
            nextNodeId = "ch3_cedric_assessment"
        ),
        DialogueNode(
            id = "ch3_cedric_assessment",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Look at the sheer scale of the aqueduct... It brought mountain spring water to the bell disciples centuries ago. Now, the Mute Sovereign has turned it into a fortress. The death wards hum with discordant energy, and corrupted sentinels prowl the upper buttresses.",
            nextNodeId = "ch3_hub"
        ),
        DialogueNode(
            id = "ch3_hub",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "We cannot force the main gate while the death wards hum and the sentinels flank our approach. Which objective shall we tackle first, Invocator?",
            choices = listOf(
                DialogueChoice(
                    id = "ch3_scout",
                    text = "Infiltrate the aqueduct cliffs to scout the corrupted sentinels",
                    voiceKeywords = listOf("scout", "sentinels", "cliffs", "infiltrate", "prowl"),
                    nextNodeId = "ch3_scout_approach",
                    completionFlag = "ch3_sentinels_complete"
                ),
                DialogueChoice(
                    id = "ch3_chime",
                    text = "Channel the Echo Chime to unseal the resonant barrier",
                    voiceKeywords = listOf("chime", "barrier", "unseal", "echo", "channel", "raise"),
                    nextNodeId = "ch3_chime_approach",
                    completionFlag = "ch3_chime_complete"
                )
            )
        ),

        // --- CHAPTER 3 / BRANCH 1: SENTINELS RECONNAISSANCE & ACOUSTIC FLAW ---
        DialogueNode(
            id = "ch3_scout_approach",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You and Sir Cedric scale the damp stone buttresses, clinging to ancient handholds above the churning gorge. Ahead, two razor-clawed sentinels crouch over pulsating black cocoons, their jagged carapaces clicking in a discordant rhythm.",
            choices = listOf(
                DialogueChoice("c_scout_listen", "Observe their acoustic resonance patterns from the shadows", listOf("listen", "observe", "shadows", "resonance", "watch"), "ch3_scout_listen"),
                DialogueChoice("c_scout_strike", "Chant a piercing harmonic note to test their reaction", listOf("strike", "chant", "piercing", "test", "sing"), "ch3_scout_strike")
            )
        ),
        DialogueNode(
            id = "ch3_scout_listen",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Listen closely... Their clicking is synchronized to a single subterranean pulse from the chamber above. When they screech, their obsidian shells vibrate out of phase.",
            nextNodeId = "ch3_scout_ambush"
        ),
        DialogueNode(
            id = "ch3_scout_strike",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Sol... Cantus! A high resonant pitch bursts from your lips. The sentinels shriek in agony as hairline fractures spider across their crystal shells!",
            nextNodeId = "ch3_scout_ambush"
        ),
        DialogueNode(
            id = "ch3_scout_ambush",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The sentinels whirl around with venomous mandibles, leaping from the buttress! Cedric locks his golden shield into place: 'They've caught our scent! Purge the vanguard!'",
            triggerBattleEncounterId = "ch3_sentinels"
        ),
        DialogueNode(
            id = "ch3_sentinels_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Magnificent! Notice how their chitin shattered when your chants struck their frequency? Their queen, the Grave Broodmother, shares the exact same vulnerability. High-cadence chants will pierce her carapace and shatter her summonings!",
            setFlagOnEnter = "ch3_sentinels_complete",
            nextNodeId = "ch3_return_hub"
        ),

        // --- CHAPTER 3 / BRANCH 2: RAISING THE ECHO CHIME & WARD SHATTERING ---
        DialogueNode(
            id = "ch3_chime_approach",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You step before the grand ironwood portal. Interlocking obsidian bands seal the doors, oozing a pitch-black liquid silence that smothers all ambient sound. At the base stands an ancient tuning dais engraved with the sacred music staff of Solaria.",
            choices = listOf(
                DialogueChoice("c_chime_align", "Strike the Echo Chime to align with the Dawn harmonic", listOf("align", "dawn", "strike", "harmonic", "chime"), "ch3_chime_align"),
                DialogueChoice("c_chime_channel", "Pour incandescent elemental mana through the chime", listOf("channel", "fire", "elemental", "mana", "pour"), "ch3_chime_channel")
            )
        ),
        DialogueNode(
            id = "ch3_chime_align",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "By the chord of the First Morning... awaken! You strike the Echo Chime against the dais. A pristine, golden chime rings out, vibrating at the frequency of pure sunlight!",
            nextNodeId = "ch3_chime_shatter"
        ),
        DialogueNode(
            id = "ch3_chime_channel",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Flames of the Logos, ignite the ancient steel! The Echo Chime blazes with incandescent solar fire, boiling away the dark venom coating the stone!",
            nextNodeId = "ch3_chime_shatter"
        ),
        DialogueNode(
            id = "ch3_chime_shatter",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "CRACK! A radiant shockwave of golden sound detonates from the Echo Chime! Like brittle crystal under an opera note, the obsidian death wards fracture into millions of harmless motes of light.",
            nextNodeId = "ch3_chime_complete_node"
        ),
        DialogueNode(
            id = "ch3_chime_complete_node",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The death wards have disintegrated! The mechanical counterweights inside the archway are groaning back to life. The acoustic seal of Solaria is broken!",
            setFlagOnEnter = "ch3_chime_complete",
            nextNodeId = "ch3_return_hub"
        ),

        // --- CHAPTER 3 HUB RETURN & GRAND BREACH ---
        DialogueNode(
            id = "ch3_return_hub",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You and Sir Cedric regroup at the base of the massive aqueduct threshold.",
            nextNodeId = "ch3_hub"
        ),
        DialogueNode(
            id = "ch3_all_completed",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The sentinels' acoustic weakness is known and the death wards are shattered! Look at the gates — the ancient sun-gears are turning! Brace yourself, Invocator, the queen descends!",
            nextNodeId = "ch3_aqueduct_boss_trigger"
        ),
        DialogueNode(
            id = "ch3_aqueduct_boss_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The colossal ironwood gates blast open! From the vaulted ceiling crawls the Grave Broodmother, screeching in deafening discord as toxic hatchlings spill from her back! Unleash your voice and cleanse the gate!",
            triggerBattleEncounterId = "cave_broodmother"
        ),
        DialogueNode(
            id = "ch3_boss_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "She falls! Look through the breach, Invocator — the aqueduct tunnels lead directly into the catacombs beneath the First Bell Tower! Solaria is within our reach!",
            nextNodeId = "ch3_to_ch4"
        ),
        DialogueNode(
            id = "ch3_to_ch4",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "The air grows cool with the scent of sacred incense and sun-baked granite. The First Bell Tower is within our reach. Let us enter the catacombs!",
            changeSceneId = SCENE_DUNGEON.id,
            nextNodeId = "chapter4_intro"
        ),

        // === CHAPTER 4: THE BELL TOWER OF SOLARIA ===
        DialogueNode(
            id = "chapter4_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "CHAPTER 4: THE BELL TOWER OF SOLARIA. You cross into the subterranean catacombs beneath the First Bell Tower. Vaulted stone pillars carved with sacred musical clefs hold up the monumental foundations. Yet eerie violet embers flicker among the ancient sarcophagi.",
            choices = listOf(
                DialogueChoice("ch4_investigate", "Examine the desecrated burial vaults", listOf("examine", "vaults", "burial", "sarcophagi", "investigate"), "ch4_crypt_cedric"),
                DialogueChoice("ch4_call_dawn", "Chant a prayer of warding to the First Dawn", listOf("prayer", "warding", "dawn", "chant", "protect"), "ch4_crypt_cedric")
            )
        ),
        DialogueNode(
            id = "ch4_crypt_cedric",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "This is the Mausoleum of the Sun-Chanters... Look at the central pedestal! The sacred Sun-Iron Bell Clapper was cast down here by the Mute Sovereign's disciples. A Bone Acolyte is conducting an unholy ritual over the relic!",
            choices = listOf(
                DialogueChoice("ch4_confront_acolyte", "Demand the desecrator surrender the holy clapper", listOf("demand", "surrender", "confront", "halt"), "ch4_crypt_confront"),
                DialogueChoice("ch4_charge_acolyte", "Draw blade and ready a searing pyromancy blast", listOf("charge", "fireball", "attack", "blade", "strike"), "ch4_crypt_confront")
            )
        ),
        DialogueNode(
            id = "ch4_crypt_confront",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Unholy defiler! In the name of the Logos and the Dawn, release the clapper and return to the dust!",
            nextNodeId = "ch4_crypt_battle_trigger"
        ),
        DialogueNode(
            id = "ch4_crypt_battle_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The Bone Acolyte raises its skeletal staff with a hollow hiss! Crypt legionnaires claw their way from granite tombs to defend the desecrated shrine. Strike with your voice and cleanse the mausoleum!",
            triggerBattleEncounterId = "dungeon_descent"
        ),
        DialogueNode(
            id = "ch4_crypt_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Their dark ward is broken! Look inside the altar vault — the Sun-Iron Bell Clapper! It radiates with the warmth of an ancient star. With this clapper, the Great Bell of Solaria will ring once more!",
            nextNodeId = "ch4_tower_ascent"
        ),
        DialogueNode(
            id = "ch4_tower_ascent",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Bearing the heavy sun-iron clapper, you and Sir Cedric ascend the grand spiral staircase. Thousands of steps coil upward through the core of the tower until you emerge into the open-air summit — the high Bell Chamber.",
            changeSceneId = SCENE_TOWER.id,
            nextNodeId = "ch4_chamber_confrontation"
        ),
        DialogueNode(
            id = "ch4_chamber_confrontation",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "There she hangs... The Great Bell of Solaria! But the Sovereign's royal iron vanguard has seized the belfry! An ironclad captain and his archers block the bell ropes!",
            choices = listOf(
                DialogueChoice("ch4_vanguard_challenge", "Proclaim the return of the Logos and charge their phalanx", listOf("proclaim", "charge", "logos", "challenge", "fight"), "ch4_vanguard_charge"),
                DialogueChoice("ch4_vanguard_smite", "Call upon Sir Cedric's holy aegis to break the line", listOf("aegis", "smite", "shield", "cedric", "break"), "ch4_vanguard_charge")
            )
        ),
        DialogueNode(
            id = "ch4_vanguard_charge",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Knights of the Mute Eye, your reign over this sacred belfry ends today! Invocator, let the thunder of our voices break their iron wills!",
            nextNodeId = "ch4_tower_battle_trigger"
        ),
        DialogueNode(
            id = "ch4_tower_battle_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The Gate Captain and Iron Vanguard lock shields in a formidable fortress phalanx, covered by lethal snipers and a royal court warlock! Speak your ultimate chants and reclaim the First Bell Tower!",
            triggerBattleEncounterId = "castle_horde"
        ),
        DialogueNode(
            id = "ch4_tower_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The garrison has fallen! The belfry is ours! Quick, Invocator — mount the Sun-Iron Clapper into the bronze bell's heart!",
            nextNodeId = "ch4_bell_climax"
        ),
        DialogueNode(
            id = "ch4_bell_climax",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You hoist the glowing Sun-Iron Clapper into the colossal bronze bell. Together, you and Sir Cedric take hold of the velvet-wrapped ropes. Closing your eyes, you speak the sacred Invocation of Dawn: 'From flame unyielding, from darkness spoken—let the First Bell ring and silence be broken!'",
            nextNodeId = "ch4_bell_ringing"
        ),
        DialogueNode(
            id = "ch4_bell_ringing",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "BONGGGGGG! The Great Bell of Solaria peals across the realm with celestial majesty! Concentric rings of golden sound ripple through the mountain air. Below, the black obsidian vines suffocating Whispering Pines shatter into shimmering dust. Hundreds of petrified villagers awaken, drawing their first breath in tears of wonder!",
            nextNodeId = "ch4_epilogue"
        ),
        DialogueNode(
            id = "ch4_epilogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "They are saved... The First Bell has chimed, and the eastern province is free! Yet our quest has only begun. Look southwest across the valley: the murky, toxic fog of the Rotting Marsh. The Second Bell Tower sleeps in the swamp, where the Grove Warden Lyra fights to protect the weeping willow. Whenever you are ready, Invocator, our next chapter awaits!",
            choices = listOf(
                DialogueChoice("ch4_reflect", "Gaze upon the liberated valley and rest in the sunrise", listOf("gaze", "valley", "rest", "sunrise", "peace"), "ch4_reflect_dialogue"),
                DialogueChoice("ch4_march", "Vow to march southwest to the Rotting Marsh and save Lyra", listOf("march", "marsh", "lyra", "southwest", "vow"), "ch4_march_dialogue")
            )
        ),
        DialogueNode(
            id = "ch4_reflect_dialogue",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "The golden morning sun reflects off the restored village roofs. The silence of this land is broken, Sir Cedric. We will not stop until every bell in Aethelgard chimes again.",
            nextNodeId = "ch4_act1_complete"
        ),
        DialogueNode(
            id = "ch4_march_dialogue",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Then we rest for the noon hour and set our course southwest. To the Rotting Marsh, to Lyra the Grove Warden, and to the Second Bell Tower!",
            nextNodeId = "ch5_intro"
        ),
        DialogueNode(
            id = "ch4_act1_complete",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "ACT I CONCLUDED: THE FALLING SILENCE SHATTERED. You have liberated the eastern valleys and restored the First Great Bell of Solaria! The journey southwest toward the Rotting Marsh and the rescue of Grove Warden Lyra begins in Act II.",
            choices = listOf(
                DialogueChoice("ch4_start_act2", "Descend the southwestern cliffs into the Rotting Marsh (Begin Act II)", listOf("descend", "southwest", "marsh", "act2", "act two", "begin", "start"), "ch5_intro"),
                DialogueChoice("ch4_replay_bell", "Re-listen to the glorious chime of Solaria", listOf("relisten", "chime", "bell", "solaria"), "ch4_bell_ringing"),
                DialogueChoice("ch4_view_epilogue", "Reflect with Sir Cedric upon the belfry", listOf("reflect", "cedric", "view", "belfry"), "ch4_epilogue")
            )
        ),

        // =====================================================================
        // ACT II: CHAPTER 5 — THE ROTTING MARSH & THE BRIAR CAGE RESCUE
        // =====================================================================
        DialogueNode(
            id = "ch5_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_marsh_fane",
            text = "ACT II: THE SEVERED RESONANCE. Descending the precipitous switchbacks of Solaria, the golden mountain light chokes out into suffocating viridian fog. The air turns cold and heavy with the sulfurous reek of stagnant bogwater. Before you lies the Rotting Marsh, where ancient sunken arches mark the entrance to the Drowned Fane.",
            nextNodeId = "ch5_tracks"
        ),
        DialogueNode(
            id = "ch5_tracks",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Halt, Invocator! Look upon the mud beside these cypress roots. Shredded green silk bearing the leaf crest of the Grove Wardens. There was a violent skirmish here within the hour. Lyra fought retreating into the sunken fane, but the blight-tracks surround her. We must scout their perimeter before breaching the central altar.",
            nextNodeId = "ch5_hub"
        ),
        DialogueNode(
            id = "ch5_hub",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "We are at the threshold of the Drowned Fane. Muffled, discordant incantations echo from the flooded pavilion ahead. How shall we coordinate the rescue assault, Invocator?",
            choices = listOf(
                DialogueChoice("ch5_creek_choice", "Scout the poisoned creek bed for warden tracks", listOf("scout", "creek", "tracks", "warden"), "ch5_scout_creek", completionFlag = "ch5_creek_scouted"),
                DialogueChoice("ch5_wards_choice", "Inspect the pulsing obsidian warding stones", listOf("inspect", "examine", "stones", "wards", "obsidian"), "ch5_examine_wards", completionFlag = "ch5_wards_examined"),
                DialogueChoice("ch5_assault_choice", "Charge the sunken altar and breach Lyra's Briar Cage!", listOf("charge", "assault", "breach", "cage", "rescue"), "ch5_rescue_assault")
            )
        ),
        DialogueNode(
            id = "ch5_scout_creek",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch5_creek_scouted",
            text = "Wading through the knee-deep black mire, you discover three snap-jaw mire traps hidden beneath floating duckweed. Sir Cedric triggers them harmlessly with his spear shaft. Carved into an ancient cypress trunk, you find a hasty druidic glyph: 'THEY SEEK THE VERIDIAN CHIME. I AM BOUND AT THE ALTAR. —L'. Knowing their ambush positions gives your duo fellowship high tactical advantage!",
            nextNodeId = "ch5_hub"
        ),
        DialogueNode(
            id = "ch5_examine_wards",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch5_wards_examined",
            text = "I place my palm near the pulsing obsidian obelisks. A sickening, discordant hum vibrates through my marrow. 'Void Briar Runes,' I warn Sir Cedric. 'The Blight Binder is siphoning the marsh's life-force to tighten the cage and drain Lyra's communion with the trees.' Discerning their counter-harmonic frequency ensures our incantations will shatter their barrier!",
            nextNodeId = "ch5_hub"
        ),
        DialogueNode(
            id = "ch5_all_completed",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The perimeter is secured, their ambush traps disarmed, and the ward frequencies mapped! Lyra is suspended above the fane pool in a cage of writhing black briars. It is time. Draw your breath, Invocator—we charge to her rescue! Sound the battle cry and breach the Briar Cage!",
            nextNodeId = "ch5_rescue_assault",
        ),
        DialogueNode(
            id = "ch5_rescue_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "marsh_rescue",
            text = "You and Sir Cedric storm the flooded courtyard! Suspended above the sunken fane within a cage of writhing obsidian briars hangs Lyra the Grove Warden! A hulking Bog Ironclad, a venomous Mire Stalker, and the Void Briar Binder spin around, weapons bristling with dark sorcery!"
        ),
        DialogueNode(
            id = "ch5_rescue_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The Void Briar Binder crumbles into dissolving ash! With a ferocious roar, Sir Cedric brings his radiant greatsword down upon the anchoring rune-chains. CLANG-CRACK! The black thorns wither and snap like dry twigs. The cage shatters, and Lyra falls from mid-air!",
            nextNodeId = "ch5_lyra_unbound"
        ),
        DialogueNode(
            id = "ch5_lyra_unbound",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "I've got you! Steady your footing, Warden. Breathe the fresh air—the corruption is broken, you are safe now.",
            nextNodeId = "ch5_lyra_first_words"
        ),
        DialogueNode(
            id = "ch5_lyra_first_words",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.RIGHT,
            text = "You... you wield the living Logos? For weeks I heard only the suffocating whispers of the Silent Blight. I thought Whispering Pines and the Solaria heights had fallen into eternal ruin. Who are you noble champions that dare breach the Sunken Mire?",
            nextNodeId = "ch5_cedric_introduces"
        ),
        DialogueNode(
            id = "ch5_cedric_introduces",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "I am Sir Cedric, formerly of the Templar Guard, and this is Aethel, the Awakened Invocator. Together we rang the Great Bell of Solaria at dawn and shattered the silence over the eastern valleys. We marched through the southwestern pass to rescue you, Grove Warden.",
            nextNodeId = "ch5_lyra_explains_crisis"
        ),
        DialogueNode(
            id = "ch5_lyra_explains_crisis",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "Solaria's Bell rings once more? Then hope still breathes in Aethelgard! But our time is perilously short. The Binder was merely a warden of my cage. One league deeper, the monstrous Bog Behemoth has infested the roots of the primordial Weeping Willow. If the Willow's heart rots, the Second Great Bell—The Veridian Chime—will drown forever beneath the mire!",
            nextNodeId = "ch5_rest_sanctuary"
        ),
        DialogueNode(
            id = "ch5_rest_sanctuary",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You carry Lyra to a secluded limestone alcove framed by ancient willow roots. Together, you dress her wounds and drink clean springwater filtered by the grove. As dusk settles over the marsh, Lyra's strength returns, her verdant staff glowing with emerald life.",
            nextNodeId = "ch5_complete"
        ),
        DialogueNode(
            id = "ch5_complete",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "CHAPTER 5 COMPLETED: THE DROWNED FANE RESCUE. You have penetrated the Rotting Marsh, defeated the Void Binder garrison, and saved Lyra the Grove Warden from the Briar Cage! Chapter 6: The Warden's Oath awaits. Greet the dawn and receive the Warden's Oath (Begin Chapter 6)",
            nextNodeId = "ch6_intro",
        ),

        // =====================================================================
        // ACT II: CHAPTER 6 — THE WARDEN'S OATH & THE WEEPING WILLOW
        // =====================================================================
        DialogueNode(
            id = "ch6_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_willow_sanctuary",
            text = "Dawn pierces the swamp canopy in shafts of shimmering emerald radiance. The sacred waters surrounding the colossal Weeping Willow ripple with quiet power. Lyra stands tall, her verdant tunic buckled and her staff crowned with freshly bloomed white jasmine blossoms.",
            nextNodeId = "ch6_oath_ceremony"
        ),
        DialogueNode(
            id = "ch6_oath_ceremony",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "Aethel... Sir Cedric. You entered the jaws of the mire for me when the rest of the kingdom had surrendered these lands to silence. My grove is my life, but its fate is bound to your quest. Before the ancient roots of the Willow, hear my vow.",
            nextNodeId = "ch6_oath_words"
        ),
        DialogueNode(
            id = "ch6_oath_words",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "'By root and stone, by rain and thorn—where the Logos calls, the Grove Warden answers! My briars shall shield your flank, and my soothing rains shall mend your wounds. From this breath until the last chime, I stand with the Fellowship!'",
            nextNodeId = "ch6_party_joins"
        ),
        DialogueNode(
            id = "ch6_party_joins",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "lyra_recruited",
            text = "LYRA THE GROVE WARDEN HAS JOINED YOUR FELLOWSHIP! Your active combat party now numbers three champions: Aethel the Invocator, Sir Cedric the Templar, and Lyra the Grove Warden. In battle, Lyra commands nature spells: Soothing Rain, Briar Entangle, and Grounded Mend!",
            nextNodeId = "ch6_hub"
        ),
        DialogueNode(
            id = "ch6_hub",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "The colossal Weeping Willow rises just beyond this mossy embankment. We can hear the guttural, subterranean groans of the Bog Behemoth poisoning its taproot. Before we engage the titan, how shall we prepare?",
            choices = listOf(
                DialogueChoice("ch6_lore_choice", "Ask Lyra about the lore of the Veridian Chime (Second Great Bell)", listOf("ask", "lore", "veridian", "chime", "history"), "ch6_lore_dialogue", completionFlag = "ch6_lore_complete"),
                DialogueChoice("ch6_spores_choice", "Harvest cleansing willow spores to resist the toxic bog miasma", listOf("harvest", "spores", "willow", "cleanse", "miasma"), "ch6_spores_dialogue", completionFlag = "ch6_spores_complete"),
                DialogueChoice("ch6_willow_assault_choice", "Advance into the heart of the pool and confront the Bog Behemoth!", listOf("advance", "heart", "pool", "confront", "behemoth", "battle"), "ch6_willow_assault")
            )
        ),
        DialogueNode(
            id = "ch6_lore_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch6_lore_complete",
            text = "The Veridian Chime was cast in the First Age of Song. Its bronze is fused with jade mined from the earth-veins deep beneath the marsh. When chimed, its resonance does not merely travel through air—it surges through plant roots and groundwater, restoring life to every blighted blossom for fifty leagues.",
            nextNodeId = "ch6_hub"
        ),
        DialogueNode(
            id = "ch6_spores_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch6_spores_complete",
            text = "Together with Lyra, you harvest luminescent sapphire spores clinging to the water lilies. Crushing them releases an invigorating eucalyptus vapor that clears your lungs. 'These spores neutralize the Behemoth's corrosive acid,' Lyra smiles. Sir Cedric's armor and your robes are coated in protective azure pollen. The party's vitality surges to maximum!",
            nextNodeId = "ch6_hub"
        ),
        DialogueNode(
            id = "ch6_all_completed",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The protective spores coat our armor, and the sacred destiny of the Veridian Chime is etched in our hearts! The three of us fight as one fellowship. Invocator, give the command and let us cleanse the Weeping Willow! March into the willow roots and destroy the Bog Behemoth!",
            nextNodeId = "ch6_willow_assault",
        ),
        DialogueNode(
            id = "ch6_willow_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "swamp_behemoth",
            text = "The fellowship wades into the churning waters of the sacred pool! The mire boils as a colossal, moss-armored Bog Behemoth bursts from the depths, flanked by razor-fanged marsh leeches! Lyra raises her staff, Sir Cedric readies his shield, and your voice invokes the Logos!"
        ),
        DialogueNode(
            id = "ch6_willow_purified",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "With a deafening bellow that shakes the swamp canopy, the Bog Behemoth collapses into the depths! Its black corruption dissolves into shimmering emerald foam. Across the giant willow, withered branches burst into vibrant verdant leaves, and thousands of luminous blue blossoms open in joyous symphony!",
            nextNodeId = "ch6_chime_revealed"
        ),
        DialogueNode(
            id = "ch6_chime_revealed",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "Look at the center of the pool! The murky waters have turned crystal clear! The roots are parting... Behold, the Veridian Chime!",
            nextNodeId = "ch6_bell_inspection"
        ),
        DialogueNode(
            id = "ch6_bell_inspection",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Rising gracefully from the cleansed pool, cradled by intertwining cypress roots, hangs the colossal Second Great Bell of Aethelgard—The Veridian Chime. Inscribed along its jade-inlaid bronze rim are ancient botanical runes waiting to be awakened by the sacred Logos.",
            nextNodeId = "ch6_act2_climax"
        ),
        DialogueNode(
            id = "ch6_act2_climax",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Two Great Bells found... one ringing with dawn, and one awaiting our rite of tuning! Whispering Pines is safe, the Rotting Marsh breathes with life once more, and Grove Warden Lyra fights at our side. Invocator, our fellowship grows mightier each day. Whenever you are ready, we shall tune the Veridian Chime and march toward the Clockwork Bastion!",
            choices = listOf(
                DialogueChoice("ch6_advance_ch7", "March north to tune the Veridian Chime (Advance to Chapter 7)", listOf("advance", "chapter 7", "tune", "veridian", "chime", "north", "march", "next"), "ch7_intro"),
                DialogueChoice("ch6_commune_lyra", "Speak with Lyra regarding the fellowship's path ahead", listOf("speak", "commune", "lyra", "path"), "ch6_lyra_dialogue", "ch6_lyra_dialogue_complete"),
                DialogueChoice("ch6_view_willow", "Inspect the ancient jade runes along the chime's rim", listOf("inspect", "runes", "rim", "bell", "jade", "chime"), "ch6_runes_dialogue", "ch6_runes_dialogue_complete")
            )
        ),
        DialogueNode(
            id = "ch6_lyra_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch6_lyra_dialogue_complete",
            text = "Lyra clutches her heartwood staff, eyes shining with quiet wonder. 'For five years I believed our grove was dead. Your voice did not merely break the mire, Invocator—it reminded the earth how to sing. Wherever the Logos leads, my bow and spells are pledged to you.'",
            nextNodeId = "ch6_act2_climax"
        ),
        DialogueNode(
            id = "ch6_runes_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch6_runes_dialogue_complete",
            text = "The jade carvings along the Veridian Chime glow softly. Inscribed in the script of the First Age are the words: 'That which was silenced in roots shall awaken in song.' The chime vibrates eagerly, attuned to the botanical frequency awaiting your tuning rite!",
            nextNodeId = "ch6_act2_climax"
        ),

        // === CHAPTER 7: TUNING THE VERIDIAN CHIME ===
        DialogueNode(
            id = "ch7_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_sunken_catacombs",
            text = "Morning light filters through the emerald canopy above the cleansed Weeping Willow pool. The Veridian Chime hangs suspended over the crystal water, its jade surface etched with sleeping botanical runes. Lyra unclasps her botanical tuning fork, its polished heartwood trembling with natural resonance.",
            nextNodeId = "ch7_hub"
        ),
        DialogueNode(
            id = "ch7_hub",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "Lyra holds the tuning fork steady. 'To awaken the Veridian Chime, we must align our voices with the ancient root-frequency of Aethelgard. Invocator, how shall we prepare the rite?'",
            choices = listOf(
                DialogueChoice("ch7_tuning_choice", "Strike the botanical tuning fork against the jade bell", listOf("tuning", "fork", "strike", "bell", "chime"), "ch7_tuning_dialogue", "ch7_tuning_complete"),
                DialogueChoice("ch7_stele_choice", "Inspect the submerged steles of the First Word", listOf("stele", "tablet", "inscriptions", "inspect", "submerged"), "ch7_stele_dialogue", "ch7_stele_complete"),
                DialogueChoice("ch7_dagger_choice", "Examine an obsidian kunai pinned to a cypress trunk", listOf("dagger", "kunai", "stalker", "examine", "pinned", "trunk", "obsidian"), "ch7_dagger_dialogue", "ch7_dagger_complete"),
                DialogueChoice("ch7_cedric_choice", "Confer with Cedric regarding the northern crags", listOf("cedric", "crags", "north", "scout", "confer"), "ch7_cedric_dialogue", "ch7_cedric_complete")
            )
        ),
        DialogueNode(
            id = "ch7_tuning_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch7_tuning_complete",
            text = "Lyra strikes the fork gently against the rim of the Veridian Chime. A clear, bell-like hum ripples across the water, making the willow blossoms glow in sympathetic blue-green light. Calcified silt begins flaking from the bell's underbelly, revealing pure jade filigree! Lyra smiles: 'The resonance is pure. The bell remembers!'",
            nextNodeId = "ch7_hub"
        ),
        DialogueNode(
            id = "ch7_stele_dialogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch7_stele_complete",
            text = "Cedric scrapes silt from the submerged limestone steles. 'These carvings tell of the First Age,' he reads in awe. 'When the ancient kings sought to enforce a single language upon all subjects, they distorted the Logos into an iron law. The Blight was not born of darkness, but of forced uniformity—silencing dissent until the silence swallowed the world.' The sobering truth steels your fellowship's purpose!",
            nextNodeId = "ch7_hub"
        ),
        DialogueNode(
            id = "ch7_dagger_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch7_dagger_complete",
            text = "Aethel steps toward an ancient cypress and pulls free an obsidian kunai wrapped in silver thread. Pinned beneath the blade is a parchment strip: 'Turn back, Invocator. Grand Executioner Kaelen holds the crags with sonic tripwires. He was ordered to take your tongue in a lead jar. If you march north, tread only the high ledges. —A brother in silence.' Cedric inspects the blade: 'The sigil of the Black Guild of Assassins... but why would one of Malakor's killers warn us?'",
            nextNodeId = "ch7_hub"
        ),
        DialogueNode(
            id = "ch7_cedric_dialogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch7_cedric_complete",
            text = "Cedric gazes north toward the jagged mountain peaks. 'Between the assassin's warning and the tremors beneath our feet, we cannot hesitate. When the chime rings, the water will drain, but the guardian beneath will strike first. Steel your incantations, Invocator!'",
            nextNodeId = "ch7_hub"
        ),
        DialogueNode(
            id = "ch7_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The preparatory runes glow with incandescent jade light! The Veridian Chime is primed to awaken. But beneath the pool, the ancient silt core thrashes—a gargantuan Mire Wyrm rises to crush the chime before it can ring! Unleash the Logos and destroy the Mire Wyrm!",
            nextNodeId = "ch7_wyrm_assault",
        ),
        DialogueNode(
            id = "ch7_wyrm_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch7_mire_wyrm",
            text = "The Mire Wyrm erupts from the muddy depths, spraying corrosive silt as swarms of silt ghouls rush the shore! Lyra raises her staff, Sir Cedric locks his golden shield in place, and your voice invokes the Logos!"
        ),
        DialogueNode(
            id = "ch7_wyrm_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch7_wyrm_defeated",
            text = "With a subterranean groan, the Ancient Mire Wyrm collapses and dissolves into radiant jade moss! Lyra strikes the botanical fork with all her strength against the Veridian Chime. A deafening, glorious jade toll reverberates across fifty leagues of marsh! Massive whirlpools form as the stagnant black bogwater drains through subterranean caverns, exposing the dry stone road leading into the northern crags!",
            nextNodeId = "ch7_post_toll"
        ),
        DialogueNode(
            id = "ch7_post_toll",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The Veridian Chime rings true! Two bells restored, two remain! As dusk falls across the drained marsh, the northern crags loom cold and jagged before us. Let us make camp at the foothills and prepare for the assassin's pass. Make camp at the foot of the Shadowed Crags.",
            nextNodeId = "ch7_camp_intro",
        ),

        // === CHAPTER 7 CAMPFIRE INTERLUDE: THE FOOTHILLS OF THE CRAGS ===
        DialogueNode(
            id = "ch7_camp_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_camp",
            text = "A crackling campfire illuminates the cold granite base of the northern mountains. Thin mountain air bites through cloaks, but the fire's warmth provides a safe haven. Lyra brews steeping mint leaves while Cedric checks his shield straps.",
            nextNodeId = "ch7_camp_hub"
        ),
        DialogueNode(
            id = "ch7_camp_hub",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The campfire crackles softly under the starry mountain sky. What will you do before turning in for the night?",
            choices = listOf(
                DialogueChoice("ch7_camp_lyra_choice", "Speak with Lyra about the song of her elven kin", listOf("lyra", "song", "kin", "sister", "elves"), "ch7_camp_lyra_dialogue", "ch7_camp_lyra_complete"),
                DialogueChoice("ch7_camp_cedric_choice", "Ask Cedric about the Brotherhood of the Mute", listOf("cedric", "brotherhood", "mute", "assassins", "guild"), "ch7_camp_cedric_dialogue", "ch7_camp_cedric_complete"),
                DialogueChoice("ch7_camp_watch_choice", "Take the midnight watch and scan the rocky ridge", listOf("watch", "midnight", "scan", "ridge", "guard", "scout"), "ch7_camp_watch_dialogue", "ch7_camp_watch_complete")
            )
        ),
        DialogueNode(
            id = "ch7_camp_lyra_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch7_camp_lyra_complete",
            text = "My twin sister, Sylvan, was the chief harmonist of our grove. When the Inquisition marched upon us, she sang the final verse that sealed the Veridian Chime beneath the roots, even as the obsidian draught silenced her. I know her spirit heard that chime toll today. Thank you, Aethel.",
            nextNodeId = "ch7_camp_hub"
        ),
        DialogueNode(
            id = "ch7_camp_cedric_dialogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch7_camp_cedric_complete",
            text = "Cedric's brow furrows as he oils his blade. 'The Brotherhood of the Mute are not mere bandits. Initiates undergo the Severing—drinking boiling obsidian resin to incinerate their vocal cords so they can never betray the Inquisition under interrogation. If someone inside their ranks warned us with that dagger, that soul has risked worse than death to aid our journey.'",
            nextNodeId = "ch7_camp_hub"
        ),
        DialogueNode(
            id = "ch7_camp_watch_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch7_camp_watch_complete",
            text = "Aethel climbs onto a flat boulder to take the midnight watch. A tiny pebble clicks on the ledge twenty feet above! A slender, silver-haired rogue crouches in the moonlight, hooded in shadow leather. His violet eyes flash with astonishment. 'You truly speak living words,' the silhouette whispers across the wind. 'Do not take the canyon floor tomorrow. The ledges are your only hope.' The figure drops a pouch of dried mountain wolfsbane tea and vanishes into the dark like mist!",
            nextNodeId = "ch7_camp_hub"
        ),
        DialogueNode(
            id = "ch7_camp_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Dawn breaks in cold violet and gold across the crags. The fellowship gathers their packs, invigorated by rest and prepared for whatever awaits in the canyon. March into the Shadowed Crags (Begin Chapter 8)",
            nextNodeId = "ch8_intro",
        ),

        // === CHAPTER 8: THE SHADOWED CRAGS & ZEPHYR'S PARLEY ===
        DialogueNode(
            id = "ch8_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_shadowed_crags",
            text = "Freezing winds whistle through the razor-sharp obsidian pillars of the Shadowed Crags. Purple mountain mist snakes across the trail. Following the midnight stalker's advice, the fellowship ascends along the narrow upper cliff ledge.",
            nextNodeId = "ch8_scout_hub"
        ),
        DialogueNode(
            id = "ch8_scout_hub",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The canyon path splits before a narrow chasm. Strange metallic glints and caustic smoke catch your attention.",
            choices = listOf(
                DialogueChoice("ch8_wire_choice", "Inspect the hair-thin sonic tripwire spanning the gap", listOf("tripwire", "wire", "sonic", "trap", "inspect", "disarm"), "ch8_wire_dialogue", "ch8_wire_complete"),
                DialogueChoice("ch8_herbs_scout_choice", "Burn the stalker's wolfsbane herbs to clear poison mist", listOf("herbs", "wolfsbane", "burn", "mist", "poison", "clear"), "ch8_herbs_scout_dialogue", "ch8_herbs_scout_complete")
            )
        ),
        DialogueNode(
            id = "ch8_wire_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch8_wire_complete",
            text = "Halt! An acoustic percussion mine. If anyone steps on the stones below, the reverberation would detonate the whole gorge. Watch closely—a careful cut to the lead counter-weight neutralizes the trip-wire safely.",
            nextNodeId = "ch8_scout_hub"
        ),
        DialogueNode(
            id = "ch8_herbs_scout_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch8_herbs_scout_complete",
            text = "Aethel kindles a small handful of the dried wolfsbane herbs. A fragrant lavender smoke billows forward, neutralizing the choking purple gloom in the pass. The trail ahead is clear and safe to traverse!",
            nextNodeId = "ch8_scout_hub"
        ),
        DialogueNode(
            id = "ch8_scout_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "As the mist parts, a silken shadow drops from an overhanging boulder, blocking the pass with twin daggers twirling! The silver-haired rogue stands in broad daylight, his violet eyes locked upon Aethel!",
            nextNodeId = "ch8_zephyr_standoff"
        ),
        DialogueNode(
            id = "ch8_zephyr_standoff",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Hold, assassin! You have stalked our tracks from the Weeping Willow. Drop your steel or be struck down!",
            nextNodeId = "ch8_zephyr_reply"
        ),
        DialogueNode(
            id = "ch8_zephyr_reply",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Zephyr lets out a low, amused breath, twirling his twin daggers before resting them casually against his wrists. 'Put away the heavy iron, Templar. If I wanted your throat opened, that sonic mine would have turned you into red mist twenty paces ago.' He turns to Aethel: 'I am Zephyr of the Black Guild. I left the dagger. I gave you the herbs. But before I commit treason against the Mute Sovereign, I must know the truth from your own mouth.'",
            nextNodeId = "ch8_zephyr_demand"
        ),
        DialogueNode(
            id = "ch8_zephyr_demand",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Zephyr takes a slow step forward. 'They told us all living voice was an abomination—that silence was peace. But my heart screamed every time they poured boiling lead down an initiate's throat. Prove to me your voice is real, Invocator. Speak a word of living flame or dawn!'",
            choices = listOf(
                DialogueChoice("ch8_chant_fire", "Chant 'Fireball' to kindle living solar flame in the gorge", listOf("fireball", "fire", "chant", "solar", "flame"), "ch8_proof_fire"),
                DialogueChoice("ch8_chant_fellowship", "Speak words of fellowship: 'We seek to free all voices in Aethelgard'", listOf("free", "voices", "fellowship", "speak", "words"), "ch8_proof_words")
            )
        ),
        DialogueNode(
            id = "ch8_proof_fire",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch8_proof_given",
            text = "Aethel draws a deep breath and chants the sacred syllable of fire! A swirling corona of radiant golden warmth erupts between her palms, illuminating the frozen obsidian canyon with brilliant, crackling sunlight! The frozen mist dissolves in an instant!",
            nextNodeId = "ch8_zephyr_awakened"
        ),
        DialogueNode(
            id = "ch8_proof_words",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch8_proof_given",
            text = "Aethel speaks clearly and without fear: 'We do not wield silence, Zephyr. We bring back the songs, the laughter, and the truth of Aethelgard. No soul was meant to live in a cage of quiet.' The warmth and resonance of your words ring against the cold stone like silver chimes!",
            nextNodeId = "ch8_zephyr_awakened"
        ),
        DialogueNode(
            id = "ch8_zephyr_awakened",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Zephyr's eyes widen with profound reverence. His daggers drop to his sides, trembling slightly. 'It is true... The First Voice lives. Ten years I hid a razor under my tongue so I could die speaking, but now... now I have something to speak for!'",
            nextNodeId = "ch8_ambush_strike"
        ),
        DialogueNode(
            id = "ch8_ambush_strike",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "A harsh horn blasts from the cliff above! Boulders tumble down as Grand Executioner Kaelen drops onto the path with twin heavy execution axes, flanked by lethal shadowblade elites! 'Zephyr! Traitorous cur! You were sent to bring the Sovereign their tongues in lead, and you babble with our prey! You will die beside them!' Defend the pass alongside Zephyr against Executioner Kaelen!",
            nextNodeId = "ch8_assassin_assault",
        ),
        DialogueNode(
            id = "ch8_assassin_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch8_executioner_ambush",
            text = "Zephyr spins his blades with deadly grace and steps into formation beside Sir Cedric and Lyra! 'My daggers are yours, Invocator! Let us teach the Inquisition that words will never die!' Prepare for battle!"
        ),
        DialogueNode(
            id = "ch8_executioner_victory",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "zephyr_recruited",
            text = "I was sent to sever your tongues. Instead, I chose to keep my own. You speak with the First Voice. From this day forward, my daggers fight at your side.",
            nextNodeId = "ch8_hub"
        ),
        DialogueNode(
            id = "ch8_hub",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "The four harmonic disciplines are gathered: Flame, Dawn, Grove, and Shadow. But before we assault Ouros, we have preparations to make.",
            choices = listOf(
                DialogueChoice("ch8_motives_choice", "Ask Zephyr why he turned against the Inquisition", listOf("motives", "why", "turned", "betrayed", "inquisition"), "ch8_motives_dialogue", "ch8_motives_complete"),
                DialogueChoice("ch8_map_choice", "Study Zephyr's stolen blueprints of the Clockwork Bastion", listOf("map", "blueprints", "bastion", "ouros", "study"), "ch8_map_dialogue", "ch8_map_complete"),
                DialogueChoice("ch8_herbs_choice", "Rest around the cragfire and blend mountain herbs", listOf("rest", "cragfire", "herbs", "tend", "wounds"), "ch8_herbs_dialogue", "ch8_herbs_complete")
            )
        ),
        DialogueNode(
            id = "ch8_motives_dialogue",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch8_motives_complete",
            text = "In the Black Guild, initiates undergo the Severing—drinking molten obsidian to burn out our vocal cords so we carry no secrets. I feigned silence, concealing a hidden razor under my tongue. Malakor does not desire peace; he desires a graveyard of mute puppets. I would rather die screaming than live in his silence.",
            nextNodeId = "ch8_hub"
        ),
        DialogueNode(
            id = "ch8_map_dialogue",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch8_map_complete",
            text = "Ouros is powered by geothermal steam chambers. The Third Bell—the Resonant Bastion—is locked in the apex belfry. If we disable the steam valves, the automated defense grid will collapse, exposing Warmaster Ouros!",
            nextNodeId = "ch8_hub"
        ),
        DialogueNode(
            id = "ch8_herbs_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch8_herbs_complete",
            text = "Lyra brews dried winterberry leaves over Cedric's consecrated flint. The invigorating steam soothes the cold chill of the crags, restoring full vitality and morale to all four champions!",
            nextNodeId = "ch8_hub"
        ),
        DialogueNode(
            id = "ch8_all_completed",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Invocator... before we assault the bastion, my spirit carries a debt. In the catacombs beneath this ridge lies the Mausoleum of the Sun. My former mentor, Sir Galahault, haunts that desecrated hall. If my blade is to remain unbroken, I must face my past.",
            nextNodeId = "ch9_intro",
        ),

        // === CHAPTER 9: THE BROKEN VOW OF DAWN (Sir Cedric's Required Trial) ===
        DialogueNode(
            id = "ch9_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_mausoleum",
            text = "Cold marble echoes beneath your boots. The Mausoleum of the Sun lies buried beneath the forgotten foundations of Sol-Aethel. Shattered statues of knights kneel before weeping golden sunburst banners.",
            nextNodeId = "ch9_peristyle_entry"
        ),
        DialogueNode(
            id = "ch9_peristyle_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "A ring of marble columns circles a sunken peristyle where petrified golden knights still stand in formation, swords drawn against a foe that came upon them from within. Dust motes hang in the slanting shafts of pale light, and beyond the far arch a sprawl of tombs and shrines waits.",
            choices = listOf(
                DialogueChoice("ch9_peristyle_cautious", "Advance cautiously between the petrified sentinels", listOf("advance", "cautious", "sentinels", "peristyle"), "ch9_peristyle_cautious_scene"),
                DialogueChoice("ch9_peristyle_ward", "Trace a warding sigil of dawn-light before proceeding", listOf("ward", "sigil", "dawn", "bless"), "ch9_peristyle_ward_scene")
            )
        ),
        DialogueNode(
            id = "ch9_peristyle_cautious_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Cedric moves as silently as a hunting wolf, his mailed palm hovering over the pommel of his greatsword. 'They fell facing the altar, not the door,' he murmurs. 'Whatever they were ordered to guard, it stood behind them.'",
            nextNodeId = "ch9_penitent_trigger"
        ),
        DialogueNode(
            id = "ch9_peristyle_ward_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You trace the old Sunburst sigil in the air; it hangs like a fallen star, and the darkness between the columns recoils. Somewhere in the depths, stone grinds against stone in answer, as if the tomb itself has begun to wake.",
            nextNodeId = "ch9_penitent_trigger"
        ),
        DialogueNode(
            id = "ch9_penitent_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch9_penitent_gate",
            text = "The threshold guardians of the Sun's Penitence rise from the flagstones — knights who broke their vows and were buried alive at the gate itself. Wreathed in cold fire, they advance to bar the way to the Grandmaster's crypt hall!"
        ),
        DialogueNode(
            id = "ch9_penitent_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch9_penitent_complete",
            text = "The penitent knights crumble, at last released from their vigil. Cedric bows his head to them. 'They asked for judgment and were given only silence. May the dawn be kinder to them than their order was.'",
            nextNodeId = "ch9_descent_hall"
        ),
        DialogueNode(
            id = "ch9_descent_hall",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Beyond the gate, the sunken hall descends in tiers lined with the sealed urns of long-dead orders. At its center stands the monument where the Golden Chime kept its truest oaths — and where Grandmaster Galahault now dreams in obsidian.",
            nextNodeId = "ch9_hub"
        ),
        DialogueNode(
            id = "ch9_hub",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Here lies the tomb of Sir Galahault, Grandmaster of the Golden Chime. When the Blight struck, he gave his life holding the acoustic seals of the vault so the acolytes could escape. But his spirit... something has bound it in torment.",
            choices = listOf(
                DialogueChoice("ch9_knights_choice", "Examine the petrified statues of the Golden Chime knights", listOf("knights", "statues", "examine", "petrified"), "ch9_knights_dialogue", "ch9_knights_complete"),
                DialogueChoice("ch9_altar_choice", "Offer a prayer of renewal at the Solar Sunburst Altar", listOf("altar", "prayer", "solar", "sunburst", "offer"), "ch9_altar_dialogue", "ch9_altar_complete"),
                DialogueChoice("ch9_reliquary_choice", "Visit the reliquary of broken oaths and unfulfilled vows", listOf("reliquary", "oaths", "vows", "shrine"), "ch9_reliquary_dialogue", "ch9_reliquary_complete")
            )
        ),
        DialogueNode(
            id = "ch9_knights_dialogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch9_knights_complete",
            text = "They obeyed orders without question... and in their obedience, they turned to obsidian all the same. Blind vows do not protect righteousness; only living conscience does.",
            nextNodeId = "ch9_hub"
        ),
        DialogueNode(
            id = "ch9_altar_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch9_altar_complete",
            text = "You kindle sacred embers upon the solar altar. Golden light washes across the crypt, dispelling the suffocating gloom. Cedric's shield begins to glow with blinding morning luminescence!",
            nextNodeId = "ch9_hub"
        ),
        DialogueNode(
            id = "ch9_reliquary_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch9_reliquary_complete",
            text = "In the reliquary, rows of bronze plaques bear the names of ordained knights whose vows were never fulfilled. A single inscription catches the light: 'Here lies Sir Marrok, who chose mercy over command.' Cedric reads it in silence, then touches the plaque. 'Grandmaster Galahault struck his name from our records,' he says quietly, 'but the dawn remembers it.'",
            nextNodeId = "ch9_hub"
        ),
        DialogueNode(
            id = "ch9_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The central obsidian sarcophagus bursts open! Grandmaster Galahault steps forth in corrupted plate, crying out against Cedric's broken oath! Sir Cedric draws his greatsword: 'My oath was to the people, master!' Stand beside Cedric and shatter Grandmaster Galahault's curse!",
            nextNodeId = "ch9_galahault_assault",
        ),
        DialogueNode(
            id = "ch9_galahault_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch9_galahault_trial",
            text = "Sir Galahault charges with spectral paladins! Cedric steps into the vanguard with radiant shield held high!"
        ),
        DialogueNode(
            id = "ch9_galahault_victory",
            speaker = DialogueSpeaker.GALAHAULT,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "cedric_trial_complete",
            text = "Cedric... my boy. You did not break your vow. You fulfilled its truest meaning. Protect them... protect the voice.",
            nextNodeId = "ch9_dawn_benediction"
        ),
        DialogueNode(
            id = "ch9_dawn_benediction",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Through the shattered roof of the mausoleum, the first true dawn of the old realm pours down in a column of honeyed light. The obsidian encasing the fallen knights begins to fall away like shed scales, and beneath it, still and serene, lie the unblemished bodies of the Golden Chime.",
            nextNodeId = "ch9_cedric_kneel"
        ),
        DialogueNode(
            id = "ch9_cedric_kneel",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "I carried his shame for twenty years. A vow broken is either a wound that never heals... or the hinge a door was always meant to swing on. I ask you to witness which it was.",
            choices = listOf(
                DialogueChoice("ch9_kneel_sister", "Kneel beside him and name his mercy his truest vow", listOf("kneel", "witness", "mercy", "vow", "sister"), "ch9_cedric_sister"),
                DialogueChoice("ch9_kneel_pray", "Offer a prayer over the tomb for the thirty thousand lost", listOf("pray", "tomb", "thirty", "thousand", "lost"), "ch9_cedric_prayer")
            )
        ),
        DialogueNode(
            id = "ch9_cedric_sister",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You kneel at his side, the flagstones cold beneath you both. 'Mercy is not weakness, Sir Cedric,' you say. 'It is the one command the order never taught you to obey — and the only one that ever made you a knight worth naming.'",
            nextNodeId = "ch9_cedric_absolution"
        ),
        DialogueNode(
            id = "ch9_cedric_prayer",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You raise your voice in the old prayer of the Sunburst, and the motes of golden light respond, drifting like a congregation of candles to gather above the tomb of the thirty thousand. For a moment the mausoleum is not a tomb at all, but a cathedral full of answered grief.",
            nextNodeId = "ch9_cedric_absolution"
        ),
        DialogueNode(
            id = "ch9_cedric_absolution",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch9_vigil_complete",
            text = "Sir Cedric rises, and the weight that has stooped his shoulders for two decades lifts like a winter fog burned off by morning. He sets his gauntlet over his heart and bows. 'The dawn will find me in the sally port again, choosing the children every time. Thank you, Invocator. You buried my ghost.'",
            nextNodeId = "ch9_grave_vigil"
        ),
        DialogueNode(
            id = "ch9_grave_vigil",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch9_vigil_rest_complete",
            text = "The fellowship keeps a long vigil in the new light, breaking bread in the Grandmaster's tomb and tending each other's wounds. When at last you rise, rested and whole, the Mausoleum of the Sun is quiet behind you, its ghosts finally at peace.",
            nextNodeId = "ch9_post_victory"
        ),
        DialogueNode(
            id = "ch9_post_victory",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "The corruption over the knights is cleansed! Look—Sir Cedric stands unburdened, and the path to the Emerald Grove is open!",
            nextNodeId = "ch10_intro",
        ),

        // === CHAPTER 10: THE SONG OF THE MUTE GROVE (Lyra's Required Trial) ===
        DialogueNode(
            id = "ch10_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_emerald_choir",
            text = "The fellowship arrives in the secluded sunken glade of the Emerald Choir. Massive ancient redwoods loom overhead, completely motionless. Dozens of singing dryads stand petrified in obsidian around a bubbling black spring.",
            nextNodeId = "ch10_thicket_entry"
        ),
        DialogueNode(
            id = "ch10_thicket_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Between you and the central spring lies a dense tangle of fallen redwoods and black-mossed briars, the last patch of the choir's wildwood. The silence here is wrong — the thorns have begun to whisper, and where the bracken thins, something pale watches from the shadows between the roots.",
            choices = listOf(
                DialogueChoice("ch10_thicket_light", "Raise a soft song of light to thread the tangled thicket", listOf("sing", "light", "thicket", "song"), "ch10_thicket_light_scene"),
                DialogueChoice("ch10_thicket_tread", "Slip through the black briars on a silent hunter's path", listOf("tread", "silent", "path", "briars"), "ch10_thicket_tread_scene")
            )
        ),
        DialogueNode(
            id = "ch10_thicket_light_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Lyra hums the old root-song of the grove, and the briars part like a door held open by shy green hands. Light returns to the leaves inch by inch. 'The wood remembers its own name,' she murmurs, 'if only someone will say it out loud.'",
            nextNodeId = "ch10_ambush_trigger"
        ),
        DialogueNode(
            id = "ch10_thicket_tread_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You move as the hunters of Sol-Aethel once moved, quiet as the shadow of a leaf. Behind you, Cedric follows without a single chime of armor. The choking smog grows thicker, and ahead, the first colossal fern stirs of its own accord.",
            nextNodeId = "ch10_ambush_trigger"
        ),
        DialogueNode(
            id = "ch10_ambush_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch10_thicket_guardians",
            text = "The colossal ferns tear themselves free of the earth! Vines of glass and bark, infected with the same obsidian venom that froze the choir, rise up as the thicket's poisoned guardians to bar every path to the spring!"
        ),
        DialogueNode(
            id = "ch10_thicket_victory",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch10_thicket_cleared",
            text = "The glass vines splinter and fall still. Lyra touches the broken veins of infected bark, and the sickly black sap bleeds out of the earth, soaking into the deep roots. 'Whatever poisoned them,' she says, 'we have cut it loose from the wood.'",
            nextNodeId = "ch10_choir_heart"
        ),
        DialogueNode(
            id = "ch10_choir_heart",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The thicket gives way at last to the marrow of the grove: the silent spring where thirty frozen dryads stand around the blackened waters, waiting for a voice. You step onto the living shore and the wait ends.",
            nextNodeId = "ch10_hub"
        ),
        DialogueNode(
            id = "ch10_hub",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "When Malakor's priests came, they dumped obsidian venom into the spring. The dryads sang until their voices turned to glass. I was too afraid to sing loud enough to save them... But now, with your fellowship, I will not be quiet!",
            choices = listOf(
                DialogueChoice("ch10_dryads_choice", "Touch the weeping obsidian statues of Lyra's sisters", listOf("dryads", "statues", "sisters", "touch", "weeping"), "ch10_dryads_dialogue", "ch10_dryads_complete"),
                DialogueChoice("ch10_seed_choice", "Prepare the Living Seed of the Sacred Willow", listOf("seed", "willow", "sacred", "prepare", "spring"), "ch10_seed_dialogue", "ch10_seed_complete"),
                DialogueChoice("ch10_hymn_choice", "Sing the lost Breviary Hymn to the choir of frozen dryads", listOf("hymn", "breviary", "sing", "choir", "frozen"), "ch10_hymn_dialogue", "ch10_hymn_complete")
            )
        ),
        DialogueNode(
            id = "ch10_hymn_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch10_hymn_complete",
            text = "You lend your voice to a verse of the old Breviary Hymn — the one the choir sang before woven silence took them. Faint emerald shimmer runs along the dryads' glass cheeks, and from one frozen throat, a syllable answers back.",
            nextNodeId = "ch10_hub"
        ),
        DialogueNode(
            id = "ch10_dryads_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch10_dryads_complete",
            text = "They are still alive! A faint emerald heartbeat resonates through the stone. Their spirits are trapped in petrified amber. If we purify the Elder Spring with the Logos, they will sing again!",
            nextNodeId = "ch10_hub"
        ),
        DialogueNode(
            id = "ch10_seed_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch10_seed_complete",
            text = "You channel harmonic energy into the ancient willow seed. It sprouts luminous tendrils of emerald light that snake toward the murky spring, craving pure living water.",
            nextNodeId = "ch10_hub"
        ),
        DialogueNode(
            id = "ch10_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The spring roils violently! The Blighted Broodmother—a colossal arachnid behemoth dripping with obsidian venom—climbs from the pit, hissing with discordant screeching! Cleanse the sacred spring and crush the Blighted Broodmother!",
            nextNodeId = "ch10_broodmother_assault",
        ),
        DialogueNode(
            id = "ch10_broodmother_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch10_broodmother_trial",
            text = "The Broodmother strikes with venomous fangs as toxic spiderlings swarm the grove! Lyra raises her staff to weave the living earth!"
        ),
        DialogueNode(
            id = "ch10_broodmother_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "lyra_trial_complete",
            text = "The Broodmother shatters into harmless green loam! Lyra drops the living seed into the bubbling waters. Crystal pure turquoise water erupts in geysers! Across the grove, the obsidian crusts peel away, and thirty dryads awaken, singing a glorious four-part hymn of thanksgiving! Lyra unlocks the Master Chant: Verdant Cataclysm!",
            nextNodeId = "ch10_spring_of_voices"
        ),
        DialogueNode(
            id = "ch10_spring_of_voices",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The awakened dryads do not scatter like startled deer — they form a great ring around the spring and bow to the four winds, their voices braided into a harmony that has not been sung since before the Blight. At the heart of the ring, the eldest dryad slowly opens her eyes.",
            nextNodeId = "ch10_dryad_matron"
        ),
        DialogueNode(
            id = "ch10_dryad_matron",
            speaker = DialogueSpeaker.DRYAD_MATRON,
            side = SpeakerSide.LEFT,
            text = "The Elder Dryad rises to her full height, bark and blossom woven through her silver hair. 'Little One of the Grove,' she says to Lyra, her voice deep as summer rain, 'you fled the choir when the priests came. We were proud of you for it. To burn beside us would have served nothing; to return with the dawn serves everything. There is one debt the grove still owes.'",
            choices = listOf(
                DialogueChoice("ch10_offer_seed", "Offer the sprouting Living Seed to the Elder Dryad", listOf("offer", "seed", "elder", "grove"), "ch10_seed_bloom"),
                DialogueChoice("ch10_join_hymn", "Ask Lyra to join the dryads in the closing hymn", listOf("join", "hymn", "lyra", "sing"), "ch10_hymn_scene")
            )
        ),
        DialogueNode(
            id = "ch10_seed_bloom",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Lyra lowers the sprouting seed into the Elder Dryad's cupped hands. The seed bursts into a cascade of blossoms, and the Elder Dryad weaves them into a crown. 'Wear this until the woodlands wake,' she says. 'You are no longer the smallest voice of the Grove. You are its promise.'",
            nextNodeId = "ch10_choir_rest"
        ),
        DialogueNode(
            id = "ch10_hymn_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Lyra steps into the ring and the dryads part for her like grass under wind. When she sings, her voice no longer trembles — and for the first time since leaving the Grove, she holds a note so true the frozen pond at the wood's edge begins to thaw.",
            nextNodeId = "ch10_choir_rest"
        ),
        DialogueNode(
            id = "ch10_choir_rest",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch10_choir_rest_complete",
            text = "The choir shelters the fellowship for the night in nests of living boughs, singing softly as the stars come out. By morning you wake healed and restored, the greenwood's blessing settled deep in your bones.",
            nextNodeId = "ch10_post_victory"
        ),
        DialogueNode(
            id = "ch10_post_victory",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Lyra is radiant, surrounded by dancing woodland spirits. Zephyr tightens his gloves. 'Two debts cleared. Now comes mine. Master Nocturne has tracked us to the Blind Gorge. If I do not extinguish him today, his blades will seek us at our backs during the assault on Ouros.' Enter the Blind Gorge for Zephyr's Trial",
            nextNodeId = "ch11_intro",
        ),

        // === CHAPTER 11: THE SILENT BLADE'S RECKONING (Zephyr's Required Trial) ===
        DialogueNode(
            id = "ch11_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_blind_gorge",
            text = "The Blind Gorge is a labyrinth of razor obsidian slabs and subterranean thermal vents. An oppressive silence hangs in the air, muffled by dark alchemical smog.",
            nextNodeId = "ch11_gorge_entry"
        ),
        DialogueNode(
            id = "ch11_gorge_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The gorge narrows ahead into the killing lanes Nocturne staked out decades ago: glass-edged culverts, blackened shrines to the mute, and cauterized scaffolding where the Black Guild once trained its apprentices in silence. Now the smog has begun to move against the wind.",
            choices = listOf(
                DialogueChoice("ch11_lane_traps", "Advance down the trap-laden culverts with blade in hand", listOf("advance", "culverts", "traps", "blade"), "ch11_lane_traps_scene"),
                DialogueChoice("ch11_lane_shadow", "Stalk the smog in silence, a step behind your own shadow", listOf("stalk", "shadow", "silence", "smog"), "ch11_lane_shadow_scene")
            )
        ),
        DialogueNode(
            id = "ch11_lane_traps_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Zephyr graces his old master's traps, snapping wires and cutting garrotes with the flat of his daggers. 'Every one of these,' he says without breaking pace, 'was invented to teach apprentices to feel an enemy before it moves. Nocturne has been teaching me how to beat him since I was nine.'",
            nextNodeId = "ch11_first_strike_trigger"
        ),
        DialogueNode(
            id = "ch11_lane_shadow_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You walk the gorge the way its keepers did — no heartbeat of sound, no chink of mail. Even the smog seems unsure where you are. Somewhere ahead, a footstep falters, and you know the hunt has gone in the wrong direction.",
            nextNodeId = "ch11_first_strike_trigger"
        ),
        DialogueNode(
            id = "ch11_first_strike_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch11_gorge_stalkers",
            text = "The gorge widens into a killing bowl — and it is already occupied. Nocturne's stalkers, masked assassins of the Black Guild, drift out of the culverts in a silent ring, crossbows nocked and poisoned blades dripping!"
        ),
        DialogueNode(
            id = "ch11_first_strike_victory",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch11_gorge_cleared",
            text = "The stalkers fall, and with them the guild's watch on the approach. Zephyr gathers a handful of their broken mask-class tokens and casts them into the vents. 'They were children, once. So was I. Nocturne made us all the same blade — and dulled us with it.'",
            nextNodeId = "ch11_gorge_cleared"
        ),
        DialogueNode(
            id = "ch11_gorge_cleared",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The path to Nocturne's sanctum lies open and silent. In the hush, the gorge feels less like a hunting ground and more like a tomb waiting to be emptied of its ghost.",
            nextNodeId = "ch11_hub"
        ),
        DialogueNode(
            id = "ch11_hub",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Zephyr tests the wind. 'Nocturne is the deadliest assassin in Aethelgard. He moves without sound or shadow. Watch your footing—the gorge is rigged with sonic mines and poisoned garrotes.'",
            choices = listOf(
                DialogueChoice("ch11_traps_choice", "Disarm the acoustic tripwires strung across the canyon", listOf("traps", "tripwires", "disarm", "acoustic", "mines"), "ch11_traps_dialogue", "ch11_traps_complete"),
                DialogueChoice("ch11_vials_choice", "Identify the obsidian venom vials left along the trail", listOf("vials", "venom", "poison", "identify", "trail"), "ch11_vials_dialogue", "ch11_vials_complete"),
                DialogueChoice("ch11_doctrine_choice", "Study the Black Guild's doctrine of stolen breath in the sanctum", listOf("doctrine", "guild", "stolen", "breath", "sanctum"), "ch11_doctrine_dialogue", "ch11_doctrine_complete")
            )
        ),
        DialogueNode(
            id = "ch11_doctrine_dialogue",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch11_doctrine_complete",
            text = "Look at this: the Doctrine of the Mute, written in the guild's breath-code. Every name on this list had their tongue cut before they were sixteen. They called it purification. It was just cowardice made into law.",
            nextNodeId = "ch11_hub"
        ),
        DialogueNode(
            id = "ch11_traps_dialogue",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch11_traps_complete",
            text = "Zephyr nimbly snips the nearly invisible obsidian wires, disabling the sonic concussion mines. 'My former master taught me these traps. Now they will not harm us.'",
            nextNodeId = "ch11_hub"
        ),
        DialogueNode(
            id = "ch11_vials_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch11_vials_complete",
            text = "Lyra treats the obsidian vials with eucalyptus spores, neutralizing the airborne paralysis toxins. The air in the gorge clears!",
            nextNodeId = "ch11_hub"
        ),
        DialogueNode(
            id = "ch11_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "A whirlpool of black mist congeals on the canyon floor as Master Nocturne steps forth in obsidian bone! Zephyr uncrosses his daggers with lethal resolve: 'Silence is death. Words are how we choose each other!' Pursue Nocturne into the Obsidian Vaults beneath the gorge!",
            nextNodeId = "ch11_vaults_entry",
        ),
        DialogueNode(
            id = "ch11_nocturne_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch11_nocturne_trial",
            text = "Master Nocturne dissolves into three shadow duplicates, striking from the dark! Zephyr leaps forward to meet his former master blade to blade!"
        ),
        DialogueNode(
            id = "ch11_nocturne_victory",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "zephyr_trial_complete",
            text = "Zephyr's twin daggers pierce Nocturne's shadow core. The master's obsidian mask splits in half and clatters to the stone floor. Nocturne dissolves into drifting soot. Zephyr breathes deeply, tearing the Black Guild insignias from his cloak: 'I am no longer an assassin of the mute. I am Zephyr of the Fellowship!' Zephyr unlocks the Master Chant: Umbral Oblivion!",
            nextNodeId = "ch11_severing_memory"
        ),
        DialogueNode(
            id = "ch11_severing_memory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "In the dying dark of the sanctum, Zephyr kneels and opens a small locket he has never shown anyone. Inside is a portrait of a woman whose eyes are the same grey as his. 'The guild took her voice, and then her breath, to make me their perfect weapon,' he says. 'I never got to hear her call my name. I have carried the silence of it long enough.'",
            choices = listOf(
                DialogueChoice("ch11_memory_name", "Speak her name aloud for him into the quiet", listOf("her", "name", "speak", "locket"), "ch11_carry_name"),
                DialogueChoice("ch11_memory_renew", "Ask him to renew his oath in his own voice, not the guild's", listOf("oath", "renew", "voice", "guild"), "ch11_renew_oath")
            )
        ),
        DialogueNode(
            id = "ch11_carry_name",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Zephyr listens as you say the name from beneath the locket's hinge — a soft name, a mother's name. He repeats it once, roughly, then again, gently. 'There,' he says, and closes the locket. 'Now it is mine to carry, not theirs to keep.'",
            nextNodeId = "ch11_gorge_dawn"
        ),
        DialogueNode(
            id = "ch11_renew_oath",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "I once swore my life to the mute. Now I swear my blades to the speakers. Let that be the only contract that outlives me.",
            nextNodeId = "ch11_gorge_dawn"
        ),
        DialogueNode(
            id = "ch11_gorge_dawn",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch11_gorge_rest_complete",
            text = "The fellowship takes its rest at the mouth of the gorge as the smoke clears and the first clean wind of morning rushes through the canyon. Wounds are bound, silence is broken with campfire talk, and by light of the risen sun the path to Ouros stands open.",
            nextNodeId = "ch11_post_victory"
        ),
        DialogueNode(
            id = "ch11_vaults_entry",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            changeSceneId = "scene_obsidian_vaults",
            text = "Beneath the gorge floor, the Black Guild's true heart: a reliquary of every voice it ever stole. Racks of sealed lead jars line the vaults, each trembling faintly with a sound that can never be forgotten. 'He buries them here,' Zephyr says, 'to prove silence outlasts speech. Let us prove him wrong.'",
            choices = listOf(
                DialogueChoice("ch11_vault_ledger", "Read the ledger of the stolen to free the names from the archives", listOf("read", "ledger", "names", "archives", "vault"), "ch11_vault_ledger_scene"),
                DialogueChoice("ch11_vault_altar", "Shatter the guild's altar of severed tongues", listOf("shatter", "altar", "severed", "tongues"), "ch11_vault_altar_scene")
            )
        ),
        DialogueNode(
            id = "ch11_vault_ledger_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The ledger runs to thousands of names, each struck through with a guild seal. Zephyr begins at the oldest page and reads every name aloud, unbroken, until his voice has restored each one to the air. The sealed lead jars hum in answer, refusing to be silent.",
            nextNodeId = "ch11_archive_battle_trigger"
        ),
        DialogueNode(
            id = "ch11_vault_altar_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You bring the altar of severed tongues crashing down with a blast of light. Beyond it, the true sanctum door stands revealed, and from the shadows behind the fallen shrine, the guild's archivists rise to defend their silence with blades.",
            nextNodeId = "ch11_archive_battle_trigger"
        ),
        DialogueNode(
            id = "ch11_archive_battle_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch11_archive_enforcers",
            text = "The Obsidian Archive Enforcers — the guild's voiceless wardens, bred to guard its silence — emerge from the walls in black robes, each carrying a blade forged to cut sound itself from the air!"
        ),
        DialogueNode(
            id = "ch11_archive_victory",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch11_archive_purged",
            text = "The archivists fall, and with them the last armed will of the guild. Zephyr stands amid the shattered racks and says, softly: 'Silence has no blade of its own. It only borrows ours. Come — Nocturne is waiting at the end of this passage.'",
            nextNodeId = "ch11_grandmaster_found"
        ),
        DialogueNode(
            id = "ch11_grandmaster_found",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_blind_gorge",
            text = "The vault corridor empties into a vast dark arena: the cavern heart of the Blind Gorge, where Master Nocturne himself waits beneath a single hanging lamp, his obsidian mask the only lit thing in the black. The chord of ending blows cold.",
            nextNodeId = "ch11_nocturne_assault"
        ),
        DialogueNode(
            id = "ch11_post_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Sir Cedric claps Zephyr firmly on the shoulder. 'All four companions have proven their souls in trial! Our blades are keen, our spirits unyielding. The Clockwork Bastion of Ouros stands before us. Let us awaken the Third Great Bell!' Assault the Clockwork Bastion of Ouros!",
            nextNodeId = "ch12_intro",
        ),

        // === CHAPTER 12: AWAKENING THE THIRD BELL (The Iron Belfry of Ouros) ===
        DialogueNode(
            id = "ch12_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_clockwork_bastion",
            text = "The Clockwork Bastion rises like an iron mountain into the clouds. Colossal brass gears groan with mechanical rhythm as pressurized steam vents roar along the parapets. At the pinnacle hangs the Third Great Bell: The Resonant Bastion!",
            nextNodeId = "ch12_steamworks_entry"
        ),
        DialogueNode(
            id = "ch12_steamworks_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The outer ironworks churn in a deafening heart of pistons and drop-forges. Catwalks cross roaring furnaces, and at every junction, armored clockwork sentinels stand locked mid-march, waiting for the intrusion alarm to let them finish their stride.",
            choices = listOf(
                DialogueChoice("ch12_pipe_lanes", "Thread the high pipe-lanes above the furnace floor", listOf("thread", "pipe", "lanes", "furnace", "high"), "ch12_pipe_lanes_scene"),
                DialogueChoice("ch12_furnace_heat", "Move through the furnace heat, where the sentinels scan least", listOf("furnace", "heat", "below", "sentinels"), "ch12_furnace_heat_scene")
            )
        ),
        DialogueNode(
            id = "ch12_pipe_lanes_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Zephyr hurls the grapple into the vaulted girders and the fellowship climbs hand over hand into the steam-choked dark above the works. Below, the sentinels march on, unaware, their clockwork heads turning in the empty lanes they were set to guard.",
            nextNodeId = "ch12_patrol_trigger"
        ),
        DialogueNode(
            id = "ch12_furnace_heat_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You walk straight into the roar of the furnaces, letting the heat wrap the fellowship in its shroud. Sensors tuned for the cold of living breath go blind around you. On the far catwalk, a patrol halts, scans, and marches on whistling steam.",
            nextNodeId = "ch12_patrol_trigger"
        ),
        DialogueNode(
            id = "ch12_patrol_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch12_automaton_patrol",
            text = "The catwalk collapses under concealed counterweights! From the smoke, Ouros's automaton patrol — tripod wardens of riveted brass and molten cores — clanks into formation, their targeting lenses burning with furnace light!"
        ),
        DialogueNode(
            id = "ch12_patrol_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch12_sentry_down",
            text = "The warden-automata grind to a halt, sparks guttering out of their cracked cores. Cedric wipes cinders from his brow. 'It will take more than iron to keep this bell from ringing,' he says, and the forge-light glints off his smile.",
            nextNodeId = "ch12_bastion_core"
        ),
        DialogueNode(
            id = "ch12_bastion_core",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The express lift hauls you up through the bastion's living gears into the belfry command deck. Stands of black machinery surround the great furnace heart, and above, caged and humming, hangs the Resonant Bastion itself.",
            nextNodeId = "ch12_hub"
        ),
        DialogueNode(
            id = "ch12_hub",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "The bell is clamped by magnetic hydraulic locks powered by the main steam generators. If we disable the pressure valves and decouple the interlock gears, the housing will retract, allowing us to challenge High Engineer Ouros!",
            choices = listOf(
                DialogueChoice("ch12_valves_choice", "Override the steam pressure bypass valves", listOf("valves", "steam", "pressure", "override", "bypass"), "ch12_valves_dialogue", "ch12_valves_complete"),
                DialogueChoice("ch12_cogs_choice", "Disengage the magnetic clamps locking the Great Bell", listOf("cogs", "clamps", "magnetic", "disengage", "gear"), "ch12_cogs_dialogue", "ch12_cogs_complete"),
                DialogueChoice("ch12_grimoire_choice", "Read the forge-master's grimoire of lost incantations", listOf("grimoire", "forge", "incantations", "read", "oaths"), "ch12_grimoire_dialogue", "ch12_grimoire_complete")
            )
        ),
        DialogueNode(
            id = "ch12_grimoire_dialogue",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch12_grimoire_complete",
            text = "Zephyr deciphers the forge-master's iron-bound grimoire by the light of the furnace. Its margins carry warnings in half-erased breath-code: the bell's resonance has been wired to feed the void. 'Knowledge sharpens any blade,' he says, and tucks the book into his satchel.",
            nextNodeId = "ch12_hub"
        ),
        DialogueNode(
            id = "ch12_valves_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch12_valves_complete",
            text = "Aethel invokes a burst of intense glacial ice, freezing the superheated pressure valves shut. The steam sirens howl and sputter out!",
            nextNodeId = "ch12_hub"
        ),
        DialogueNode(
            id = "ch12_cogs_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch12_cogs_complete",
            text = "Cedric wedges his greatsword into the magnetic interlock lever, throwing his entire weight into it. With a thunderous clank, the massive iron clamps retract from the bell!",
            nextNodeId = "ch12_hub"
        ),
        DialogueNode(
            id = "ch12_all_completed",
            speaker = DialogueSpeaker.OUROS,
            side = SpeakerSide.RIGHT,
            text = "The foundry floor trembles as Clockwork Warmaster Ouros deploys from the furnace elevator—a ten-foot armored automaton wielding molten steam cannons and backed by iron phalanx guards! 'Intruders detected! Protocol: Silence the living!' Destroy Warmaster Ouros and ring the Iron Belfry!",
            nextNodeId = "ch12_warmaster_assault",
        ),
        DialogueNode(
            id = "ch12_warmaster_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch12_warmaster_ouros",
            text = "The 4-hero fellowship clashes against Warmaster Ouros and his clockwork phalanx battalion high on the belfry platform!"
        ),
        DialogueNode(
            id = "ch12_warmaster_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch12_ouros_defeated",
            text = "With a cataclysmic blast of steam and flying cogs, Warmaster Ouros collapses into glowing molten scrap! The fellowship grips the massive iron chain and pulls together. The Third Great Bell—The Resonant Bastion—tolls with a thunderous, metallic roar that shakes the very foundations of the earth! Across the skies, a blinding ribbon of celestial light solidifies into a sky bridge leading directly to the capital of Sol-Aethel!",
            nextNodeId = "ch12_skybridge_rise"
        ),
        DialogueNode(
            id = "ch12_skybridge_rise",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The sky bridge hangs like a fallen moonbeam, wide enough for a full company, spanning from the belfry platform to the distant marble walls of the capital. Below, the Blight-ravaged kingdom lies folded in shadow — and for the first time, the road home is made of light.",
            nextNodeId = "ch12_bridge_crossing"
        ),
        DialogueNode(
            id = "ch12_bridge_crossing",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The crossing is long enough for thoughts to unspool with each step above the void. The city ahead glitters, and somewhere in Aethelgard, a village square waits to be filled with the sound of its own bells.",
            choices = listOf(
                DialogueChoice("ch12_bridge_step", "Walk with steady stride toward the capital gates", listOf("walk", "steady", "gates", "capital", "bridge"), "ch12_bridge_scene"),
                DialogueChoice("ch12_bridge_gaze", "Gaze down at the freed lands and name what you fight for", listOf("gaze", "freed", "lands", "below", "name"), "ch12_bridge_gaze_scene")
            )
        ),
        DialogueNode(
            id = "ch12_bridge_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You match the rhythm of the party's footfalls as one. The sky bridge holds firm; the wind carries the faint sound of clover-fields and far-off sheep bells, and beneath your cuirass your heart beats to the old march of returning soldiers.",
            nextNodeId = "ch12_threshold_rest"
        ),
        DialogueNode(
            id = "ch12_bridge_gaze_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You stop on the crown of the bridge and name them all aloud — the Marsh Fane, the Willow Sanctuary, the sunken catacombs, the Emerald Choir — each a place that went silent and each, by your hand, learning to sing again. The fellowship listens, and the bridge does not hurry you.",
            nextNodeId = "ch12_threshold_rest"
        ),
        DialogueNode(
            id = "ch12_threshold_rest",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch12_threshold_rest_complete",
            text = "The fellowship camps on the sky bridge's far end, on the roof of the outer curtain wall, watching the capital's lights edge closer with the dawn. Restored and armed in spirit, the company wakes ready to enter the silent city.",
            nextNodeId = "ch12_post_victory"
        ),
        DialogueNode(
            id = "ch12_post_victory",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "Three Great Bells restored! Look—the sky bridge has opened! The capital gates of Sol-Aethel lie just ahead. The final trial draws near!",
            nextNodeId = "ch13_intro",
        ),

        // === CHAPTER 13: BREACH OF THE SILENT CITADEL ===
        DialogueNode(
            id = "ch13_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_silent_citadel",
            text = "High above the cloudline on the floating plateau of Sol-Aethel, the Silent Citadel looms in terrifying majesty. Monolithic battlements of black glass reflect the eerie silence of the capital. The Great Gates are bolted shut with obsidian sorcery.",
            nextNodeId = "ch13_whisperway_entry"
        ),
        DialogueNode(
            id = "ch13_whisperway_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Between the outer rampart and the Great Gates runs the Whisperway: the pilgrim road into Sol-Aethel, once hung with festival banners and the songs of a thousand travelers. Now shadow moves along it in skirls, and the banner poles hold garlands of black glass.",
            choices = listOf(
                DialogueChoice("ch13_way_still", "Walk the Whisperway still and watchful, banners overhead", listOf("walk", "still", "banners", "whisperway", "watch"), "ch13_way_still_scene"),
                DialogueChoice("ch13_way_voice", "Call out a greeting to the empty city as you approach", listOf("call", "greeting", "voice", "empty", "city"), "ch13_way_voice_scene")
            )
        ),
        DialogueNode(
            id = "ch13_way_still_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Cedric walks the road in perfect silence, and it moves his heart in ways silence seldom does. 'I have heard this road since I was a squire,' he says. 'It never sounded like this. It sounded like a festival that refused to end.'",
            nextNodeId = "ch13_plaza_trigger"
        ),
        DialogueNode(
            id = "ch13_way_voice_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You send your voice ringing against the black glass walls, a field-call of the old realm. It returns thinner than it left, but somewhere far up among the battlements, a shutter bangs open — someone is still listening.",
            nextNodeId = "ch13_plaza_trigger"
        ),
        DialogueNode(
            id = "ch13_plaza_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch13_plaza_legion",
            text = "The Whisperway opens onto the Pilgrim's Plaza — and the plaza is not empty. Vaelor's garrison, a legion of obsidian-sheathed sentinels in ordered ranks, stand lamp-lit where the pilgrims once bought their candles. They turn as one at your approach!"
        ),
        DialogueNode(
            id = "ch13_plaza_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch13_plaza_cleared",
            text = "The garrison ranks lie broken across the flagstones, and the lamps they guarded still burn. Cedric stoops to right a toppled pilgrim's shrine at the plaza's center. 'Their candles were lit for the living,' he says. 'The city has gone long without that kind of light.'",
            nextNodeId = "ch13_gate_foot"
        ),
        DialogueNode(
            id = "ch13_gate_foot",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You stand at the foot of the Great Gates, dwarfed by black glass and iron older than the kingdom's name. The seal of the void hums against the stone, waiting for the touch of a living voice to reveal its weakness.",
            nextNodeId = "ch13_hub"
        ),
        DialogueNode(
            id = "ch13_hub",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The streets of Sol-Aethel were once filled with songs, market calls, and the laughter of children. Now it is a graveyard of black glass and petrified sentinels. Malakor has turned our glorious capital into a monument to grief.",
            choices = listOf(
                DialogueChoice("ch13_gate_choice", "Inspect the fortified black glass portcullis", listOf("gate", "portcullis", "inspect", "glass", "black"), "ch13_gate_dialogue", "ch13_gate_complete"),
                DialogueChoice("ch13_seal_choice", "Purge the obsidian seal binding the entrance", listOf("seal", "purge", "dispel", "binding", "obsidian"), "ch13_seal_dialogue", "ch13_seal_complete"),
                DialogueChoice("ch13_banners_choice", "Take down a fallen Whisperway banner and carry it to the gates", listOf("banners", "banner", "whisperway", "carry", "gates"), "ch13_banners_dialogue", "ch13_banners_complete")
            )
        ),
        DialogueNode(
            id = "ch13_banners_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch13_banners_complete",
            text = "Lyra finds one banner that survived the purge — the old lion-and-sun of Sol-Aethel, threadbare but whole. She folds it carefully into her satchel. 'When we ring the bells,' she says, 'I want this going back up on that pole before the echoes fade.'",
            nextNodeId = "ch13_hub"
        ),
        DialogueNode(
            id = "ch13_gate_dialogue",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch13_gate_complete",
            text = "Zephyr inspects the locking mechanism: 'The gate is held by void resonance. If we strike the seal with a unified chant, the glass will shatter!'",
            nextNodeId = "ch13_hub"
        ),
        DialogueNode(
            id = "ch13_seal_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch13_seal_complete",
            text = "Aethel unleashes a focused harmonic incantation. Cracks of golden fire race across the black glass seal, weakening its grip!",
            nextNodeId = "ch13_hub"
        ),
        DialogueNode(
            id = "ch13_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The black gates open to reveal Commander Vaelor, Hand of Malakor, flanked by obsidian sentinels! At his belt hangs the terrifying Void Horn as he sneers: 'You have climbed high, little songbirds. But here, the sky belongs to the void!' Slay Commander Vaelor and breach the Citadel!",
            nextNodeId = "ch13_vaelor_assault",
        ),
        DialogueNode(
            id = "ch13_vaelor_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch13_commander_vaelor",
            text = "Commander Vaelor sounds the Void Horn, summoning spectral reapers from the abyss as the fellowship charges the gatehouse!"
        ),
        DialogueNode(
            id = "ch13_vaelor_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch13_vaelor_defeated",
            text = "Commander Vaelor falls from the ramparts, and the Void Horn shatters into harmless glass shards! Beyond the citadel gates, a grand celestial plaza opens onto the lake of the Void Reservoir, where the stolen voices of the world are pooled.",
            nextNodeId = "ch13_capital_stirs"
        ),
        DialogueNode(
            id = "ch13_capital_stirs",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "With the garrison broken and the seal undone, the high city begins, at last, to stir. Here and there a shutter opens, a lantern wavers, a frozen fountain sighs back into motion. The citizens of Sol-Aethel emerge in twos and threes, blinking in the light of their own reclaimed streets.",
            nextNodeId = "ch13_cedric_memory"
        ),
        DialogueNode(
            id = "ch13_cedric_memory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "My mother sold cured meats in that stall by the awning. She had a voice like iron. When she called us home for dinner, half the district heard her. I would give every medal I ever took to hear her shout my name once more.",
            choices = listOf(
                DialogueChoice("ch13_memory_song", "Promise him that the first toll shall carry her name", listOf("promise", "song", "toll", "her", "name"), "ch13_market_song"),
                DialogueChoice("ch13_memory_promise", "Swear you will return here to ring them together", listOf("swear", "return", "ring", "together", "plaza"), "ch13_market_promise")
            )
        ),
        DialogueNode(
            id = "ch13_market_song",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Sir Cedric repeats his mother's name once, like a man testing a blade for balance, then smiles — the first true smile you have seen since the mausoleum. 'Then she will hear the end of the Blight,' he says, 'and that will be worth every scar between here and the summit.'",
            nextNodeId = "ch13_plaza_rest"
        ),
        DialogueNode(
            id = "ch13_market_promise",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You clasp his gauntlet in the manner of knights sealing vows and make the promise plain: when the bells of Sol-Aethel sing again, Sir Cedric will stand at the great bell's rope and give it his name. He nods once, formal and grave, and the plaza watches them both with its new, waking eyes.",
            nextNodeId = "ch13_plaza_rest"
        ),
        DialogueNode(
            id = "ch13_plaza_rest",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch13_plaza_rest_complete",
            text = "The citizens of the high city bring bread and water to the rampart camp, whispering their first words in months as gifts. By the time the stars wheel, the fellowship is healed, fed, and standing at the threshold of the Void Reservoir, ready to descend.",
            nextNodeId = "ch13_post_victory"
        ),
        DialogueNode(
            id = "ch13_post_victory",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Vaelor is dead. The path to the Void Reservoir is clear. Let us reclaim what was stolen! Enter the Chamber of the Void Reservoir",
            nextNodeId = "ch14_intro",
        ),

        // === CHAPTER 14: THE VOID RESERVOIR ===
        DialogueNode(
            id = "ch14_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_void_reservoir",
            text = "A vast sunken lake of pitch-black liquid silence stretches beneath a weeping starlight dome. Ripples move across the dark pool in complete, eerie noiselessness. Swirling voice motes struggle beneath the surface, trapped like captive stars.",
            nextNodeId = "ch14_shore_entry"
        ),
        DialogueNode(
            id = "ch14_shore_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The reservoir shore is a drowned pilgrim road, its flagstones half-swallowed by the black lake. Broken lanterns line the way, and the water laps without a sound — a wrongness that presses on the ears like held breath.",
            choices = listOf(
                DialogueChoice("ch14_shore_kneel", "Kneel at the drowned pilgrims' shrine before the crossing", listOf("kneel", "shrine", "pilgrims", "crossing", "shore"), "ch14_shore_kneel_scene"),
                DialogueChoice("ch14_shore_ward", "Trace a ward of protection over the silent waters", listOf("ward", "protect", "waters", "silent", "trace"), "ch14_shore_ward_scene")
            )
        ),
        DialogueNode(
            id = "ch14_shore_kneel_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Cedric kneels before the half-drowned shrine and says the old traveler's blessing for the lost. The lake does not mock him. Somewhere under the black glass water, a voice-still-light answers once, faint as a struck bell far below.",
            nextNodeId = "ch14_shallows_trigger"
        ),
        DialogueNode(
            id = "ch14_shore_ward_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You draw the ward across the shallows and the water recoils in a ring of shivering light, as if the silence itself flinches from the touch of a living syllable. Beyond the ring, the lake's surface grows taut and watchful.",
            nextNodeId = "ch14_shallows_trigger"
        ),
        DialogueNode(
            id = "ch14_shallows_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch14_shallows_husks",
            text = "The shallows erupt! Drowned husks — pilgrims who waded in seeking their stolen voices only to be caught by the lake — drag themselves ashore by the hundreds, their throats still open in silent screams, and close on the fellowship!"
        ),
        DialogueNode(
            id = "ch14_shallows_victory",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch14_shallows_cleared",
            text = "The husks crumble into silt, and for a single beat the lake goes clear as glass. Lyra watches the drowned settle gently to the bottom and speaks the old grove-prayer over them. 'They were not taken by the water,' she says. 'They were taken by forgetting.'",
            nextNodeId = "ch14_shore_cleared"
        ),
        DialogueNode(
            id = "ch14_shore_cleared",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The shore of the Reservoir is quiet again, but now it is the quiet of the living, not of the void. The drowned lights of the voice motes shift below the surface, and the true crossing opens ahead.",
            nextNodeId = "ch14_hub"
        ),
        DialogueNode(
            id = "ch14_hub",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "Lyra clutches her ears. 'The water... it is drinking our echoes before they can even leave our lips. This is where Malakor pools the stolen voices of everyone petrified across Aethelgard.'",
            choices = listOf(
                DialogueChoice("ch14_archons_choice", "Commune with the petrified High Archons kneeling by the lake", listOf("archons", "commune", "high", "statues", "kneeling"), "ch14_archons_dialogue", "ch14_archons_complete"),
                DialogueChoice("ch14_eddies_choice", "Dispel the swirling silt eddies of liquid silence", listOf("eddies", "dispel", "silt", "liquid", "silence"), "ch14_eddies_dialogue", "ch14_eddies_complete"),
                DialogueChoice("ch14_rites_choice", "Perform the ancient rite of the drowned whose names are not lost", listOf("rites", "drowned", "ancient", "names", "lost"), "ch14_rites_dialogue", "ch14_rites_complete")
            )
        ),
        DialogueNode(
            id = "ch14_rites_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch14_rites_complete",
            text = "You stand at the shore's crown and name the drowned — every pilgrim name the Archons' records give — and at each name a single mote of light shakes loose from the lake's black bed and rises, until a dim constellation of the lost floats above the water, waiting to be freed.",
            nextNodeId = "ch14_hub"
        ),
        DialogueNode(
            id = "ch14_archons_dialogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch14_archons_complete",
            text = "The frozen Archons... their minds are still trapped in prayer. They warn us: the lake has a guardian—the Leviathan of Whispers. It feeds upon the harvested voices of the drowned!",
            nextNodeId = "ch14_hub"
        ),
        DialogueNode(
            id = "ch14_eddies_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch14_eddies_complete",
            text = "Lyra chants an invigorating cleansing verse. The black liquid eddies churn and clear, restoring acoustic clarity to the shoreline!",
            nextNodeId = "ch14_hub"
        ),
        DialogueNode(
            id = "ch14_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The liquid silence rises into a towering tidal wave as the colossal Abyssal Leviathan breaches the surface! Tendrils of pure void lash out, suffocating all sound in their wake! Descend the umbilical trench into the Leviathan's den.",
            nextNodeId = "ch14_trench_descent",
        ),
        DialogueNode(
            id = "ch14_trench_descent",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_umbral_trench",
            text = "The Leviathan does not wait on the shore — it retreats down the umbilical trench that feeds the reservoir, trailing thick black silk. Following it means entering the drowned dark where the voices of the reservoir are drawn and buried.",
            nextNodeId = "ch14_trench_entry",
        ),
        DialogueNode(
            id = "ch14_trench_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The trench walls are lined with the calcified shells of swallowed songs — pearl-and-glass growths that hold the dim shapes of motes thrashing inside. The way ahead splits, both branches falling into the same swallowing dark.",
            choices = listOf(
                DialogueChoice("ch14_trench_dive", "Dive straight down after the Leviathan's wake", listOf("dive", "straight", "wake", "down", "trench"), "ch14_trench_dive_scene"),
                DialogueChoice("ch14_trench_hook", "Hang back and follow the trench wall to its hidden edge", listOf("hang", "wall", "edge", "hidden", "follow"), "ch14_trench_hook_scene")
            )
        ),
        DialogueNode(
            id = "ch14_trench_dive_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You follow the silk-threaded wake with a torch of pure voice-light, letting its hum hold the dark at bay. Around you, the cocoons of stolen songs sway like a drowned orchard, and the trench floor slants toward a vast buried chamber.",
            nextNodeId = "ch14_trench_trigger"
        ),
        DialogueNode(
            id = "ch14_trench_hook_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Lyra finds the sideroad the Leviathan's keepers used — a shelf of worked stone running beneath the main bed. Its carvings show the drowned rite of the Reservoir being performed by figures who did not want to be seen. The shelf ends in the wall of the buried chamber.",
            nextNodeId = "ch14_trench_trigger"
        ),
        DialogueNode(
            id = "ch14_trench_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch14_trench_leeches",
            text = "The buried chamber's floor comes alive! Trench leeches — great blind coils, each with the stolen voice of a hundred throats knotted inside its hide — wreathe up from the silt to feed the Leviathan's feast, singing with mouths that are not theirs!"
        ),
        DialogueNode(
            id = "ch14_trench_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch14_trench_cleared",
            text = "The leeches burst into cataracts of freed motes, and the voice-stars they release spiral up through the trench like a river of lantern-light. Lyra staggers under the swell of it — a thousand names and lullabies rushing past her ears, all of them, at last, free.",
            nextNodeId = "ch14_broach"
        ),
        DialogueNode(
            id = "ch14_broach",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_void_reservoir",
            text = "The ribbon of freed motes carves a tunnel of light back up to the reservoir floor, and you rise with it into the open air of the lakeshore — just as the black water convulses and the Abyssal Leviathan bursts from the reservoir's heart, howling with all the voices it has swallowed!",
            nextNodeId = "ch14_leviathan_assault"
        ),
        DialogueNode(
            id = "ch14_leviathan_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch14_abyssal_leviathan",
            text = "The Abyssal Leviathan envelops the party in its Muffle Aura! Speak your chants with unyielding conviction and volume to pierce the void!"
        ),
        DialogueNode(
            id = "ch14_leviathan_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch14_leviathan_defeated",
            text = "With a deafening shriek of dissolving shadow, the Abyssal Leviathan bursts into millions of incandescent voice motes! The motes swirl around the fellowship like a spiral galaxy, singing melodies of love, hope, and courage. The liquid silence evaporates, revealing the crystalline Ribbon Stair ascending to the Spire Summit!",
            nextNodeId = "ch14_motes_awakened"
        ),
        DialogueNode(
            id = "ch14_motes_awakened",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The freed voices do not scatter. They coil around the fellowship in a slow, warm galaxy, and one mote — brighter than the rest — drifts to rest above the everyone's upturned palms. It is not a stranger's voice. It is the one you have both been carrying unfound at the bottom of your own grief.",
            nextNodeId = "ch14_mote_of_sister"
        ),
        DialogueNode(
            id = "ch14_mote_of_sister",
            speaker = DialogueSpeaker.VOICE_MOTE,
            side = SpeakerSide.LEFT,
            text = "Lyra... do not weep. The grove remembers every song you ever gave it. We are not lost. Ring the bells... and call us home.",
            choices = listOf(
                DialogueChoice("ch14_mote_listen", "Listen to her voice as long as the mote will hold", listOf("listen", "voice", "mote", "hold", "hear"), "ch14_mote_scene"),
                DialogueChoice("ch14_mote_answer", "Answer her, and give her a memory of your own", listOf("answer", "memory", "give", "speak", "her"), "ch14_mote_answer_scene")
            )
        ),
        DialogueNode(
            id = "ch14_mote_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You stay still while the mote sings the fragment of her life the lake had stolen: a rainy market day, a shared apple, a joke whose punchline neither of you ever fully remembered. When it fades, you are crying without noticing, and the galaxy of voices overhead kindles a little brighter for it.",
            nextNodeId = "ch14_stair_rest"
        ),
        DialogueNode(
            id = "ch14_mote_answer_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You tell the mote the memory it could not have stolen — the morning she left, and the last thing she said, and how you have carried it verbatim since. The mote shudders, brightens to a small sun, and then, so softly it is almost a thought, says: 'Then I was not wasted.'",
            nextNodeId = "ch14_stair_rest"
        ),
        DialogueNode(
            id = "ch14_stair_rest",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch14_stair_rest_complete",
            text = "The fellowship makes rest beneath the drifting constellation of freed voices, wrapped in songs the reservoir will never drink again. Sleep comes easily, woundless and deep, and in the morning the Ribbon Stair stands bright, waiting to be climbed.",
            nextNodeId = "ch14_post_victory"
        ),
        DialogueNode(
            id = "ch14_post_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The voices are free! They swirl ahead of us, lighting the ribbon stair into the heavens. The summit is within our grasp! Ascend the Celestial Ribbon Stair to the Summit",
            nextNodeId = "ch15_intro",
        ),

        // === CHAPTER 15: ASCENT OF THE CELESTIAL SPIRE ===
        DialogueNode(
            id = "ch15_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_celestial_spire",
            text = "You ascend a stairway made of solidified harmonic light suspended between the clouds and stars. The four Great Bell Towers of Aethelgard form a colossal cross of gold, jade, and iron below you. Ahead stands the Celestial Belfry.",
            nextNodeId = "ch15_stair_entry"
        ),
        DialogueNode(
            id = "ch15_stair_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The Ribbon Stair narrows as it climbs, and the air thins to crystal. Higher still, the stair rises through a ring of worn stone archways that have stood since before the first syllable — and beneath each arch, a gate of unfinished business waits for a companion's name.",
            choices = listOf(
                DialogueChoice("ch15_stair_sing", "Climb singing, letting each step carry the fellowship's songs", listOf("sing", "climb", "songs", "steps", "stair"), "ch15_stair_sing_scene"),
                DialogueChoice("ch15_stair_logic", "Climb in steady, measured silence, mapping every arch", listOf("steady", "silence", "arch", "measure", "climb"), "ch15_stair_logic_scene")
            )
        ),
        DialogueNode(
            id = "ch15_stair_sing_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Lyra begins the song of the four bells — Solaria, the Veridian Chime, the Resonant Bastion, and the Bell of Eternity — and one by one the fellowship takes up a part. The celestial stair brightens beneath your feet, and the first archway's shadow draws back like a curtain.",
            nextNodeId = "ch15_trial_gold_gate"
        ),
        DialogueNode(
            id = "ch15_stair_logic_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Cedric counts the arches aloud, noting the worn patterns in each threshold — the marks of countless pilgrims who climbed to judgment and never returned. 'These gates weigh a soul,' he says. 'Let us give them a fellowship to weigh.'",
            nextNodeId = "ch15_trial_gold_gate"
        ),
        DialogueNode(
            id = "ch15_trial_gold_gate",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The first archway bars the stair with a shimmer of molten light. Within it, the companion who has been weighed most by fortune and coffer stands revealed — Sir Cedric, whose vow was once sold to silence for a chance to atone.",
            nextNodeId = "ch15_trial_gold_trigger"
        ),
        DialogueNode(
            id = "ch15_trial_gold_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch15_trial_gold",
            text = "The Gate of Gold summons Cedric's trial: the gilded reflections of every oath bought and bent — armored creditors of light who would have him, even now, trade his name for peace. Sir Cedric raises his shield. 'My vow was never for sale. Even peace may not buy it.'"
        ),
        DialogueNode(
            id = "ch15_trial_gold_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch15_trial_gold_complete",
            text = "It tried to buy me with everything I never wanted enough. My vow was never for sale. The gate of gold is broken.",
            nextNodeId = "ch15_trial_grove_gate"
        ),
        DialogueNode(
            id = "ch15_trial_grove_gate",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The second archway curtains the stair in tender green shadow. Within it, the companion whose heart was torn between duty and homeland steps forward — Lyra, who fled the choir, and has not forgiven herself for it.",
            nextNodeId = "ch15_trial_grove_trigger"
        ),
        DialogueNode(
            id = "ch15_trial_grove_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch15_trial_grove",
            text = "The Gate of the Grove answers with the roots that always grow back: snarled memories of the day she ran, made into thorn-vined guardians that know her every fear. Lyra's staff comes up, her jaw set: 'I ran then. I am walking through it now.'"
        ),
        DialogueNode(
            id = "ch15_trial_grove_victory",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch15_trial_grove_complete",
            text = "I am the promise now. Not the flight. The grove's trial has bloomed.",
            nextNodeId = "ch15_trial_shadow_gate"
        ),
        DialogueNode(
            id = "ch15_trial_shadow_gate",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The third archway bleeds darkness into the ascending light. Within it, the companion who has lived longest in shadow waits to be judged — Zephyr, the guild's blade who has carried the silence of his mother like a wound.",
            nextNodeId = "ch15_trial_shadow_trigger"
        ),
        DialogueNode(
            id = "ch15_trial_shadow_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch15_trial_shadow",
            text = "The Gate of Shadow releases all that Nocturne ever was — every lesson of stealth and breath-stealing, made into assassins of the dark. Zephyr draws his daggers and walks in: 'I learned all their tricks. I was never one of them.'"
        ),
        DialogueNode(
            id = "ch15_trial_shadow_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch15_trial_shadow_complete",
            text = "The shadow assassins dissolve at the first stroke of a blade that no longer hunts. Zephyr stands in the returning starlight, and for the first time his own shadow at his feet looks like a guard, not a chain.",
            nextNodeId = "ch15_threshold"
        ),
        DialogueNode(
            id = "ch15_threshold",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The three archways stand open behind the fellowship as the stair reaches its crown — a wide starlit landing before the great double doors of the Celestial Belfry. Ahead, the doors hum with expectation, waiting on one last, human pause.",
            nextNodeId = "ch15_hub"
        ),
        DialogueNode(
            id = "ch15_hub",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The four companions pause on the final landing before the great double doors. The fellowship stands at the edge of destiny.",
            choices = listOf(
                DialogueChoice("ch15_cedric_choice", "Speak with Sir Cedric upon the threshold of dawn", listOf("cedric", "dawn", "vow", "speak", "templar"), "ch15_cedric_dialogue", "ch15_cedric_complete"),
                DialogueChoice("ch15_lyra_choice", "Speak with Lyra beneath the starlight canopy", listOf("lyra", "starlight", "grove", "warden", "speak"), "ch15_lyra_dialogue", "ch15_lyra_complete"),
                DialogueChoice("ch15_zephyr_choice", "Speak with Zephyr overlooking the waking world", listOf("zephyr", "shadow", "world", "overlook", "speak"), "ch15_zephyr_dialogue", "ch15_zephyr_complete"),
                DialogueChoice("ch15_bell_choice", "Steady the hush with the chant that awaits the toll", listOf("bell", "toll", "chant", "hush", "steady"), "ch15_bell_dialogue", "ch15_bell_complete")
            )
        ),
        DialogueNode(
            id = "ch15_bell_dialogue",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch15_bell_complete",
            text = "You lead the fellowship in the tolling chant — the verse each Bell of Aethelgard is meant to answer, the one the four companions will strike together in the belfry beyond. Their voices braid and hold a single long note, and the doors themselves shiver with the sound of what attends them.",
            nextNodeId = "ch15_hub"
        ),
        DialogueNode(
            id = "ch15_cedric_dialogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch15_cedric_complete",
            text = "I was an exile, broken by guilt. You taught me that honor is not a wall—it is the courage to speak for those who cannot. Whatever waits behind those doors, Invocator, my shield is yours until my last breath.",
            nextNodeId = "ch15_hub"
        ),
        DialogueNode(
            id = "ch15_lyra_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch15_lyra_complete",
            text = "I lived in terror of the quiet for so many years. But together, our voices made the blossoms bloom and the dryads sing. We are going to bring the morning back to everyone.",
            nextNodeId = "ch15_hub"
        ),
        DialogueNode(
            id = "ch15_zephyr_dialogue",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch15_zephyr_complete",
            text = "In the shadows, they told me words were weakness. But I see now that words are how we bind our hearts together in the dark. Malakor thinks silence is peace. Let us show him the storm.",
            nextNodeId = "ch15_hub"
        ),
        DialogueNode(
            id = "ch15_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Golden Archon Custodians and Celestial Spires materialize to guard the final sanctum. They raise their halberds: 'Only those of true harmonic resonance may enter the Belfry of Eternity!' Prove your fellowship's resonance to the Archons!",
            nextNodeId = "ch15_custodians_assault",
        ),
        DialogueNode(
            id = "ch15_custodians_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch15_archon_custodians",
            text = "The Archon Custodians strike with celestial radiance! The four companions unite their master spells to break through the final test!"
        ),
        DialogueNode(
            id = "ch15_custodians_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch15_custodians_defeated",
            text = "The Archon Custodians bow low, their halberds lowering as the golden doors slowly swing open into the celestial belfry. Beyond, against a swirling backdrop of cosmic auroras, stands Grand Inquisitor Malakor—The Mute Sovereign!",
            nextNodeId = "ch15_doors_open"
        ),
        DialogueNode(
            id = "ch15_doors_open",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The golden doors swing wide on their own, silent as everything Malakor has made. Beyond them, at the far end of the belfry floor, the Bell of Eternity hangs in a field of starlight — and the pathway to it is open, waiting, absolute.",
            nextNodeId = "ch15_fellowship_oath"
        ),
        DialogueNode(
            id = "ch15_fellowship_oath",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The four companions stand at the threshold of the belfry, looking across the final floor to the waiting Bell. One long breath passes through them all at once. This is the last step before the last word.",
            choices = listOf(
                DialogueChoice("ch15_oath_together", "Bind the fellowship's promise before crossing the floor", listOf("oath", "promise", "bind", "together", "fellowship"), "ch15_oath_scene"),
                DialogueChoice("ch15_oath_bell", "Promise each other one thing each will do when the bells ring", listOf("promise", "each", "bell", "ring", "one"), "ch15_oath_bell_scene")
            )
        ),
        DialogueNode(
            id = "ch15_oath_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Cedric lays his gauntlet flat, palm up, and the others set their hands upon it, one by one. 'However this ends,' he says, 'it ends with us having spoken. That is more than Malakor has ever given anyone.' The four hands hold, and the belfry floor may finally be crossed.",
            nextNodeId = "ch15_prefinale_rest"
        ),
        DialogueNode(
            id = "ch15_oath_bell_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Each of you names one thing the ringing of the bells will mean — a mother's name for Zephyr, a choir for Lyra, a promise by an old market stall for Cedric, and for you, a world that can say 'I remember' again. The staircase of stars beneath them brightens at the sound of it.",
            nextNodeId = "ch15_prefinale_rest"
        ),
        DialogueNode(
            id = "ch15_prefinale_rest",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch15_prefinale_rest_complete",
            text = "The fellowship rests in the antechamber of the belfry, tenders to each other's armor and robes under the first stars of an infinite hour. When they rise, they are whole, and the Bell of Eternity has grown impatient with silence.",
            nextNodeId = "ch15_post_victory"
        ),
        DialogueNode(
            id = "ch15_post_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The final belfry is open. The fourth Great Bell—The Bell of Eternity—awaits. It is time to end the silence forever. Enter the Celestial Belfry to confront Grand Inquisitor Malakor",
            nextNodeId = "ch16_intro",
        ),

        // === CHAPTER 16: THE PRIMORDIAL SYLLABLE (Grand Finale) ===
        DialogueNode(
            id = "ch16_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_final_summit",
            text = "The pinnacle of the Spire of Echoes touches the outer rim of the cosmos. High above hangs the Fourth Great Bell—The Bell of Eternity—cast from star-metal and meteoric glass. Grand Inquisitor Malakor stands beneath it, his filigree muzzle mask gleaming beneath cold crimson eyes.",
            nextNodeId = "ch16_vestibule_entry"
        ),
        DialogueNode(
            id = "ch16_vestibule_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_celestial_vestibule",
            text = "Between the summit stairs and Malakor's dais lies the Vestibule of Echoes: four pillars of living glass, each holding a fragment of the world's song, each ringed by the frozen supplicants who came to bow to silence and never left. The glass pillars hum as the fellowship passes — and one by one, the supplicants begin to rise.",
            choices = listOf(
                DialogueChoice("ch16_vest_chant", "Raise the Toll Chant to match the hum of the pillars", listOf("chant", "pillars", "hum", "raise", "toll"), "ch16_vest_chant_scene"),
                DialogueChoice("ch16_vest_face", "Face the risen supplicants and hold your ground", listOf("face", "supplicants", "ground", "stand", "rise"), "ch16_vest_face_scene")
            )
        ),
        DialogueNode(
            id = "ch16_vest_chant_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You strike the Toll Chant and the pillars drink it in, their hum climbing toward the Bell of Eternity. The supplicants' heads turn toward the sound like flowers toward dawn, and their frozen hands unclench from their raptor-prayers.",
            nextNodeId = "ch16_vest_trigger"
        ),
        DialogueNode(
            id = "ch16_vest_face_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Cedric plants himself before the rising supplicants, shield high, and the company closes ranks at his back. The risen ones look at the wall of living defiance before them — and for the first time since their petrification, their mouths frame a question rather than a plea.",
            nextNodeId = "ch16_vest_trigger"
        ),
        DialogueNode(
            id = "ch16_vest_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch16_nullifier_gate",
            text = "The four glass pillars shatter inward and the Echo Nullifiers — Malakor's last gatekeepers, each forged to erase a thousand voices — step from the shards, weapons shaped like the negative spaces where songs used to be!"
        ),
        DialogueNode(
            id = "ch16_vest_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch16_vestibule_cleared",
            text = "The Nullifiers fall, and with them the last armed echo of the old silence. The four pillars slowly repair themselves — now glowing softer, hymn-bright instead of void-bright — as the supplicants kneel back down, this time in quiet wonder at the pillars' restored light.",
            nextNodeId = "ch16_mirror_gate"
        ),
        DialogueNode(
            id = "ch16_mirror_gate",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Beyond the pillars, the vestibule narrows into a gallery of polished obsidian — a hall of mirrors that has caught and kept every doubt spoken within it for a thousand years. At the far end stands Malakor, unmoved, watching your approach in every reflection.",
            nextNodeId = "ch16_confrontation"
        ),
        DialogueNode(
            id = "ch16_confrontation",
            speaker = DialogueSpeaker.MALAKOR,
            side = SpeakerSide.RIGHT,
            text = "Why do you fight for voice? Every war began with a proclamation. Every heartbreak began with a whisper. Every cruelty was justified with a spoken lie. Silence is the only mercy that lasts forever.",
            nextNodeId = "ch16_fellowship_reply"
        ),
        DialogueNode(
            id = "ch16_fellowship_reply",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Words can wound, Malakor, but words are also how we say I love you. Words are how we promise to protect each other. Without voice, peace is just an empty grave! We will not let your sorrow mute the universe!",
            nextNodeId = "ch16_mirror_entry",
        ),
        DialogueNode(
            id = "ch16_mirror_entry",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The gallery of mirrors swallows the light. In each polished face, a reflection of the fellowship appears — then begins to move of its own accord, whispering every fear that was ever set down here in the shape of your own voices. Malakor watches from the far end, patient, unmoved.",
            choices = listOf(
                DialogueChoice("ch16_mirror_deny", "Deny the doubts and walk through the gallery unbroken", listOf("deny", "doubts", "walk", "through", "unbroken"), "ch16_mirror_deny_scene"),
                DialogueChoice("ch16_mirror_speak", "Speak every doubt aloud so the mirrors lose their hold", listOf("speak", "aloud", "doubts", "mirrors", "hold"), "ch16_mirror_speak_scene")
            )
        ),
        DialogueNode(
            id = "ch16_mirror_deny_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You refuse the reflections one by one — every whispered could-have, every doubt Malakor has steeped in this hall for generations — until the mirrors, having no purchase, begin to crack with the strain of holding lies against a will that will not bend.",
            nextNodeId = "ch16_mirror_trigger"
        ),
        DialogueNode(
            id = "ch16_mirror_speak_scene",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You walk the gallery saying each doubt aloud as your own voice — not a reflection's, but yours — and the naming of them drains the venom from the hall. 'A fear that is named,' Lyra says softly, 'is a fear that has already lost.'",
            nextNodeId = "ch16_mirror_trigger"
        ),
        DialogueNode(
            id = "ch16_mirror_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch16_mirror_gauntlet",
            text = "The mirrors shatter all at once — and from the shards, the Mirror Gauntlet rises: the doubt-shadows of the entire fellowship, given blade and shape, their reflections turning against their originals in Malakor's last trick before the dais!"
        ),
        DialogueNode(
            id = "ch16_mirror_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "ch16_mirror_cleared",
            changeSceneId = "scene_final_summit",
            text = "The doubt-shadows dissolve into falling light, and the gallery empties into the open air of the summit. Ahead, across the star-lit dais, Malakor stands beneath the Bell of Eternity — mask level, hands still, patient as the void he has become. There is nothing left between you now.",
            nextNodeId = "ch16_malakor_assault"
        ),
        DialogueNode(
            id = "ch16_malakor_assault",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            triggerBattleEncounterId = "ch16_malakor_finale",
            text = "The final battle begins! Malakor shields himself in the Glass Monolith and summons the Echo Nullifiers! In Phase 3, the Death of Voice will silence all sound—speak the four-line Primordial Incantation in unison to shatter the void!"
        ),
        DialogueNode(
            id = "ch16_malakor_victory",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "malakor_defeated",
            text = "Malakor falls to his knees upon the celestial dais. The filigree mask cracks and clatters to the stone floor. For the first time in five hundred years, color returns to his pale skin and a tear tracks down his cheek. He touches his throat and whispers with a restored, frail human voice: 'I remember... the song my mother sang.' He closes his eyes with a serene smile and dissolves into peaceful motes of golden light.",
            nextNodeId = "ch16_incantation_verse"
        ),
        DialogueNode(
            id = "ch16_incantation_verse",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The last mote of Malakor's light rises past your face and is gone into the auroras. In the hush that follows, you realize the summit is not silent at all — it is humming, the four-line Primordial Incantation already forming itself unbidden in each of your four voices, waiting for one joined breath.",
            nextNodeId = "ch16_bell_vigil"
        ),
        DialogueNode(
            id = "ch16_bell_vigil",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The four companions gather under the Bell of Eternity, hands on the cord of braided starlight, and hold the incantation ready in the living hush. The Mute Sovereign's reign is over; only the first toll remains. Whatever comes after the ringing, none of you will face it alone.",
            nextNodeId = "ch16_toll_bell"
        ),
        DialogueNode(
            id = "ch16_toll_bell",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The fellowship approaches the Bell of Eternity. Together, all four companions grasp the cord of braided starlight. The player invokes the First Word with full lung power: 'LET THERE BE ECHO!' Toll the Bell of Eternity and awaken Aethelgard!",
            nextNodeId = "epilogue_awakening",
        ),

        // === EPILOGUE: THE GREAT AWAKENING ===
        DialogueNode(
            id = "epilogue_awakening",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_epilogue",
            text = "The Bell of Eternity tolls! A blinding shockwave of incandescent golden sound cascades down from the summit. Across the four quadrants of Aethelgard, the Bell of Solaria, the Veridian Chime, and the Resonant Bastion answer in symphonic four-part resonance! The sky turns brilliant sapphire, woven with auroras of pure melodic light!",
            nextNodeId = "epilogue_village"
        ),
        DialogueNode(
            id = "epilogue_village",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Down in Whispering Pines, the thick obsidian vines that strangled the village square shatter into iridescent dust! The frozen statues gasp and draw their first deep breaths of mountain air! The village elder shouts with joy. Parents weep as they embrace their children. Down in the valley, the river sings over smooth stones, and thousands of songbirds burst into joyful morning hymn!",
            nextNodeId = "epilogue_destinies"
        ),
        DialogueNode(
            id = "epilogue_destinies",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The fellowship stands upon the high overlook above Whispering Pines. Sir Cedric pledges to rebuild the Order of the Harmonious Dawn, defending the right of every living soul to speak freely. Beside him, Lyra watches green sprouts burst from her staff, promising the Emerald Choir will sing again. And with a grin, Zephyr flips his daggers into his belt, vowing to carry the tale of your victory across every province.",
            nextNodeId = "epilogue_invocator"
        ),
        DialogueNode(
            id = "epilogue_invocator",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "And what of you, Invocator? As the First Invocator of the New Age, you will keep vigil at the Spire of Echoes, teaching the children of Aethelgard to speak with conviction, kindness, and truth—so that the silence of fear may never claim the world again.",
            nextNodeId = "epilogue_credits"
        ),
        DialogueNode(
            id = "epilogue_credits",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            setFlagOnEnter = "game_completed",
            text = "CONGRATULATIONS! You have completed Voice RPG: Chronicles of the Logos! Every chapter, every trial, every companion bond, and the ultimate confrontation with the Mute Sovereign was conquered by your courage, your voice, and your heart. Thank you for playing!"
        )
    ).associateBy { it.id }
}
