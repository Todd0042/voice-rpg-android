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
        backgroundAsset = "story/forest_crossroads.jpg",
        initialNodeId = "camp_intro",
        ambientDescription = "Warm embers dance in the night air beside the ancient sun shrine."
    )

    val SCENE_CAVE = StoryScene(
        id = "scene_cave",
        name = "The Whispering Caverns",
        chapterTitle = "Chapter 2: The Sunken Grotto",
        backgroundAsset = "environments/cave.jpg",
        initialNodeId = "cavern_entry",
        ambientDescription = "Bioluminescent azure crystals hum faintly along damp limestone walls."
    )

    val SCENE_SWAMP = StoryScene(
        id = "scene_swamp",
        name = "The Rotting Marsh",
        chapterTitle = "Chapter 2: The Sunken Bog",
        backgroundAsset = "environments/swamp.jpg",
        initialNodeId = "marsh_entry",
        ambientDescription = "Thick emerald mist drifts over black mire and gnarled roots."
    )

    val SCENE_AQUEDUCT = StoryScene(
        id = "scene_aqueduct",
        name = "The Aqueducts of Solaria",
        chapterTitle = "Chapter 3: The Ascent of Solaria",
        backgroundAsset = "environments/cave.jpg",
        initialNodeId = "chapter3_intro",
        ambientDescription = "Colossal limestone arches rise above the mist as ancient waterfalls hang petrified in obsidian glass."
    )

    val SCENE_DUNGEON = StoryScene(
        id = "scene_dungeon",
        name = "Crypt of the Foundation",
        chapterTitle = "Chapter 4: The Silent Catacombs",
        backgroundAsset = "environments/dungeon.jpg",
        initialNodeId = "chapter4_intro",
        ambientDescription = "Ancient mosaic pillars of the Primordial Chanters lie buried beneath the Bell Tower foundations."
    )

    val SCENE_TOWER = StoryScene(
        id = "scene_tower",
        name = "The Solaria Bell Chamber",
        chapterTitle = "Chapter 4: The Great Bell of Solaria",
        backgroundAsset = "environments/castle.jpg",
        initialNodeId = "ch4_tower_ascent",
        ambientDescription = "High above the cloudline, the massive bronze Bell of Solaria hangs beneath open gothic parapets."
    )

    val SCENE_MARSH_FANE = StoryScene(
        id = "scene_marsh_fane",
        name = "The Drowned Fane",
        chapterTitle = "Chapter 5: The Severed Resonance",
        backgroundAsset = "story/marsh_fane.jpg",
        initialNodeId = "ch5_intro",
        ambientDescription = "Murky emerald waters lap against sunken gothic pillars and twisted weeping willow roots."
    )

    val SCENE_WILLOW_SANCTUARY = StoryScene(
        id = "scene_willow_sanctuary",
        name = "The Weeping Willow Sanctuary",
        chapterTitle = "Chapter 6: The Warden's Oath",
        backgroundAsset = "environments/swamp.jpg",
        initialNodeId = "ch6_intro",
        ambientDescription = "Bioluminescent emerald motes float among the hanging moss of the ancient sacred willow."
    )

    val SCENE_SUNKEN_CATACOMBS = StoryScene(
        id = "scene_sunken_catacombs",
        name = "The Sunken Catacombs",
        chapterTitle = "Chapter 7: Tuning the Veridian Chime",
        backgroundAsset = "environments/swamp.jpg",
        initialNodeId = "ch7_intro",
        ambientDescription = "Jade-infused bronze rings over crystal clear waters as ancient stone pathways emerge from the bog."
    )

    val SCENE_SHADOWED_CRAGS = StoryScene(
        id = "scene_shadowed_crags",
        name = "The Shadowed Crags",
        chapterTitle = "Chapter 8: The Shadowed Crags & Zephyr's Defection",
        backgroundAsset = "environments/cave.jpg",
        initialNodeId = "ch8_intro",
        ambientDescription = "Razor obsidian crags tower over a cold canyon shrouded in purple mountain mist."
    )

    val SCENE_MAUSOLEUM = StoryScene(
        id = "scene_mausoleum",
        name = "The Mausoleum of the Sun",
        chapterTitle = "Chapter 9: The Broken Vow of Dawn",
        backgroundAsset = "environments/dungeon.jpg",
        initialNodeId = "ch9_intro",
        ambientDescription = "Shattered marble statues of the Golden Chime knights lie beneath weeping golden sunburst banners."
    )

    val SCENE_EMERALD_CHOIR = StoryScene(
        id = "scene_emerald_choir",
        name = "The Emerald Choir Grove",
        chapterTitle = "Chapter 10: The Song of the Mute Grove",
        backgroundAsset = "environments/swamp.jpg",
        initialNodeId = "ch10_intro",
        ambientDescription = "Petrified dryads stand frozen around a dark spring choked in obsidian silt."
    )

    val SCENE_BLIND_GORGE = StoryScene(
        id = "scene_blind_gorge",
        name = "The Blind Gorge",
        chapterTitle = "Chapter 11: The Silent Blade's Reckoning",
        backgroundAsset = "environments/cave.jpg",
        initialNodeId = "ch11_intro",
        ambientDescription = "Thick silence and shadow mist cling to the jagged canyon hideout of the Black Guild."
    )

    val SCENE_CLOCKWORK_BASTION = StoryScene(
        id = "scene_clockwork_bastion",
        name = "The Clockwork Bastion of Ouros",
        chapterTitle = "Chapter 12: Awakening the Third Bell",
        backgroundAsset = "environments/castle.jpg",
        initialNodeId = "ch12_intro",
        ambientDescription = "Colossal brass cogs and steam pipes hum within the towering iron belfry of Ouros."
    )

    val SCENE_SILENT_CITADEL = StoryScene(
        id = "scene_silent_citadel",
        name = "The Silent Citadel Gates",
        chapterTitle = "Chapter 13: Breach of the Silent Citadel",
        backgroundAsset = "environments/castle.jpg",
        initialNodeId = "ch13_intro",
        ambientDescription = "Banners of the Mute Sovereign hang from monolithic black glass battlements before Sol-Aethel."
    )

    val SCENE_VOID_RESERVOIR = StoryScene(
        id = "scene_void_reservoir",
        name = "The Void Reservoir",
        chapterTitle = "Chapter 14: The Void Reservoir",
        backgroundAsset = "environments/swamp.jpg",
        initialNodeId = "ch14_intro",
        ambientDescription = "A cosmic lake of pure liquid silence that drinks all echoes high above the clouds."
    )

    val SCENE_CELESTIAL_SPIRE = StoryScene(
        id = "scene_celestial_spire",
        name = "The Celestial Ribbon Stair",
        chapterTitle = "Chapter 15: Ascent of the Celestial Spire",
        backgroundAsset = "environments/castle.jpg",
        initialNodeId = "ch15_intro",
        ambientDescription = "A ribbon staircase of crystallized harmonic light rises toward the aurora of the stars."
    )

    val SCENE_FINAL_SUMMIT = StoryScene(
        id = "scene_final_summit",
        name = "The Spire Summit — Bell of Eternity",
        chapterTitle = "Chapter 16: The Primordial Syllable",
        backgroundAsset = "environments/castle.jpg",
        initialNodeId = "ch16_intro",
        ambientDescription = "The colossal Fourth Great Bell hangs beneath cosmic auroras where Grand Inquisitor Malakor waits."
    )

    val SCENE_EPILOGUE = StoryScene(
        id = "scene_epilogue",
        name = "Whispering Pines Awakened",
        chapterTitle = "Epilogue: The Great Awakening",
        backgroundAsset = "story/village_square.jpg",
        initialNodeId = "epilogue_awakening",
        ambientDescription = "Golden sunlight bathes the awakened village square as songbirds fill the living pines."
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
            text = "The Four Towers were built by the Primordial Chanters: Solaria in the east, the Drowned Spire in the marsh, the Iron Belfry in the north, and the Celestial Spire at the capital. When rung in harmony, their chimes generate an acoustic ward that shields every living soul from the Blight.",
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
            text = "The Broodmother shatters into harmless iridescent mist! The corrupted brood dissolves into the stone. Look through the breach — the aqueduct tunnels lead directly into the catacombs beneath the First Bell Tower!",
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
            text = "The skeletal legion crumbles to dust! Look inside the altar vault — the Sun-Iron Bell Clapper! It radiates with the warmth of an ancient star. With this clapper, the Great Bell of Solaria will ring once more!",
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
            text = "The perimeter is secured, their ambush traps disarmed, and the ward frequencies mapped! Lyra is suspended above the fane pool in a cage of writhing black briars. It is time. Draw your breath, Invocator—we charge to her rescue!",
            choices = listOf(
                DialogueChoice("ch5_assault_ready", "Sound the battle cry and breach the Briar Cage!", listOf("sound", "battle", "cry", "breach", "charge", "assault"), "ch5_rescue_assault")
            )
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
            text = "CHAPTER 5 COMPLETED: THE DROWNED FANE RESCUE. You have penetrated the Rotting Marsh, defeated the Void Binder garrison, and saved Lyra the Grove Warden from the Briar Cage! Chapter 6: The Warden's Oath awaits.",
            choices = listOf(
                DialogueChoice("ch6_start", "Greet the dawn and receive the Warden's Oath (Begin Chapter 6)", listOf("dawn", "oath", "warden", "chapter6", "begin", "start"), "ch6_intro")
            )
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
            text = "Lyra touches the mossy bark reverently. 'The Veridian Chime was cast in the First Age of Song,' she whispers. 'Its bronze is fused with jade mined from the earth-veins deep beneath the marsh. When chimed, its resonance does not merely travel through air—it surges through plant roots and groundwater, restoring life to every blighted blossom for fifty leagues.' Knowing its sacred purpose steels your fellowship's resolve!",
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
            text = "The protective spores coat our armor, and the sacred destiny of the Veridian Chime is etched in our hearts! The three of us fight as one fellowship. Invocator, give the command and let us cleanse the Weeping Willow!",
            choices = listOf(
                DialogueChoice("ch6_boss_ready", "March into the willow roots and destroy the Bog Behemoth!", listOf("march", "destroy", "behemoth", "roots", "battle"), "ch6_willow_assault")
            )
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
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "The preparatory runes glow with incandescent jade light! The Veridian Chime is primed to awaken. But beneath the pool, the ancient silt core thrashes—a gargantuan Mire Wyrm rises to crush the chime before it can ring!",
            choices = listOf(
                DialogueChoice("ch7_boss_ready", "Unleash the Logos and destroy the Mire Wyrm!", listOf("unleash", "destroy", "wyrm", "battle", "strike"), "ch7_wyrm_assault")
            )
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
            text = "The Veridian Chime rings true! Two bells restored, two remain! As dusk falls across the drained marsh, the northern crags loom cold and jagged before us. Let us make camp at the foothills and prepare for the assassin's pass.",
            choices = listOf(
                DialogueChoice("ch7_to_camp", "Make camp at the foot of the Shadowed Crags", listOf("camp", "make", "rest", "foothills", "crags"), "ch7_camp_intro")
            )
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
            text = "Lyra stares into the embers. 'My twin sister, Sylvan, was the chief harmonist of our grove. When the Inquisition marched upon us, she sang the final verse that sealed the Veridian Chime beneath the roots, even as the obsidian draught silenced her. I know her spirit heard that chime toll today. Thank you, Aethel.'",
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
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Dawn breaks in cold violet and gold across the crags. The fellowship gathers their packs, invigorated by rest and prepared for whatever awaits in the canyon.",
            choices = listOf(
                DialogueChoice("ch7_camp_to_ch8", "March into the Shadowed Crags (Begin Chapter 8)", listOf("march", "crags", "shadowed", "begin", "chapter 8", "advance"), "ch8_intro")
            )
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
            text = "Lyra kneels and gently points with an arrow. 'An acoustic percussion mine. If anyone steps on the floor below, the soundwaves detonate the whole gorge.' Using the tip of her knife, she snips the lead counter-weight, safely neutralizing the trap!",
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
            text = "Cedric steps forward with shield raised, blade gleaming! 'Hold, assassin! You have stalked our tracks from the Weeping Willow. Drop your steel or be struck down!'",
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
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch8_proof_given",
            text = "Aethel draws a deep breath and chants the sacred syllable of fire! A swirling corona of radiant golden warmth erupts between your palms, illuminating the frozen obsidian canyon with brilliant, crackling sunlight! The frozen mist dissolves in an instant!",
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
            text = "A harsh horn blasts from the cliff above! Boulders tumble down as Grand Executioner Kaelen drops onto the path with twin heavy execution axes, flanked by lethal shadowblade elites! 'Zephyr! Traitorous cur! You were sent to bring the Sovereign their tongues in lead, and you babble with our prey! You will die beside them!'",
            choices = listOf(
                DialogueChoice("ch8_fight_ambush", "Defend the pass alongside Zephyr against Executioner Kaelen!", listOf("defend", "fight", "ambush", "kaelen", "battle", "zephyr"), "ch8_assassin_assault")
            )
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
            text = "Executioner Kaelen collapses against the rocky cliff, his axes shattering upon the stones. Zephyr wipes his daggers and sheathes them. 'I was sent to sever your tongues,' Zephyr breathes in a calm, lethal whisper. 'Instead, I chose to keep my own. You speak with the First Voice. I will fight at your side.'",
            nextNodeId = "ch8_hub"
        ),
        DialogueNode(
            id = "ch8_hub",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Zephyr leans against the canyon wall, silver hair catching the cold mountain light. 'The four harmonic disciplines are gathered: Flame, Dawn, Grove, and Shadow. But before we assault Ouros, we have preparations to make.'",
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
            text = "Zephyr touches his collar. 'In the Black Guild, initiates undergo the Severing—drinking molten obsidian to burn out our vocal cords so we carry no secrets. I feigned silence, concealing a hidden razor under my tongue. Malakor does not desire peace; he desires a graveyard of mute puppets. I would rather die screaming than live in his silence.'",
            nextNodeId = "ch8_hub"
        ),
        DialogueNode(
            id = "ch8_map_dialogue",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch8_map_complete",
            text = "Zephyr unrolls a sheepskin diagram marked with clockwork gears. 'Ouros is powered by geothermal steam chambers. The Third Bell—the Resonant Bastion—is locked in the apex belfry. If we disable the steam valves, the automated defense grid will collapse, exposing Warmaster Ouros!'",
            nextNodeId = "ch8_hub"
        ),
        DialogueNode(
            id = "ch8_herbs_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch8_herbs_complete",
            text = "Lyra brews dried winterberry leaves over Cedric's consecrated flint. The invigorating steam soothes the cold chill of the crags, restoring full vitality and morale to all four champions!",
            nextNodeId = "ch8_hub"
        ),
        DialogueNode(
            id = "ch8_all_completed",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The quad fellowship stands united! Yet Sir Cedric looks down with heavy eyes. 'Invocator... before we assault the bastion, my spirit carries a debt. In the catacombs beneath this ridge lies the Mausoleum of the Sun. My former mentor, Sir Galahault, haunts that desecrated hall. If my blade is to remain unbroken, I must face my past.'",
            choices = listOf(
                DialogueChoice("ch8_to_cedric_trial", "Enter the Mausoleum of the Sun for Sir Cedric's Trial", listOf("enter", "mausoleum", "cedric", "trial", "dawn"), "ch9_intro")
            )
        ),

        // === CHAPTER 9: THE BROKEN VOW OF DAWN (Sir Cedric's Required Trial) ===
        DialogueNode(
            id = "ch9_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_mausoleum",
            text = "Cold marble echoes beneath your boots. The Mausoleum of the Sun lies buried beneath the forgotten foundations of Sol-Aethel. Shattered statues of knights kneel before weeping golden sunburst banners.",
            nextNodeId = "ch9_hub"
        ),
        DialogueNode(
            id = "ch9_hub",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Cedric touches his golden cross. 'Here lies the tomb of Sir Galahault, Grandmaster of the Golden Chime. When the Blight came, he ordered the gates sealed from within, damning thirty thousand innocent citizens to become obsidian statues so our order might survive. I broke my vow and opened the sally port to save the children. His ghost cursed me with eternal shame.'",
            choices = listOf(
                DialogueChoice("ch9_knights_choice", "Examine the petrified statues of the Golden Chime knights", listOf("knights", "statues", "examine", "petrified"), "ch9_knights_dialogue", "ch9_knights_complete"),
                DialogueChoice("ch9_altar_choice", "Offer a prayer of renewal at the Solar Sunburst Altar", listOf("altar", "prayer", "solar", "sunburst", "offer"), "ch9_altar_dialogue", "ch9_altar_complete")
            )
        ),
        DialogueNode(
            id = "ch9_knights_dialogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch9_knights_complete",
            text = "Cedric brushes dust from the fallen knight statues. 'They obeyed orders without question... and in their obedience, they turned to obsidian all the same. Blind vows do not protect righteousness; only living conscience does.'",
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
            id = "ch9_all_completed",
            speaker = DialogueSpeaker.GALAHAULT,
            side = SpeakerSide.RIGHT,
            text = "The central obsidian sarcophagus bursts open! A petrified spectral knight encased in black obsidian armor steps forth, eyes blazing with pale golden fury: 'Traitor! You chose thirty crying orphans over five centuries of sacred chivalric lineage!' Cedric draws his greatsword: 'My oath was to the people, master!'",
            choices = listOf(
                DialogueChoice("ch9_boss_ready", "Stand beside Cedric and shatter Grandmaster Galahault's curse!", listOf("stand", "shatter", "galahault", "curse", "battle"), "ch9_galahault_assault")
            )
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
            text = "Sir Galahault drops to one knee as the obsidian plate fractures and dissolves into golden sparks. The Grandmaster smiles gently: 'You did not break the vow, Cedric... you fulfilled its truest meaning. The dawn belongs to you.' He dissolves peacefully into celestial light. Sir Cedric unlocks the Master Chant: Aegis of the Dawn!",
            nextNodeId = "ch9_post_victory"
        ),
        DialogueNode(
            id = "ch9_post_victory",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "Sir Cedric stands unburdened, his holy aura blazing brighter than the sun. Lyra clutches her Heart-Tree amulet with trembling hands. 'Invocator... my sisters call to me. The Emerald Choir spring is dying under obsidian silt. We must go there next!'",
            choices = listOf(
                DialogueChoice("ch9_to_lyra_trial", "Journey to the Emerald Choir Grove for Lyra's Trial", listOf("journey", "emerald", "choir", "lyra", "trial", "grove"), "ch10_intro")
            )
        ),

        // === CHAPTER 10: THE SONG OF THE MUTE GROVE (Lyra's Required Trial) ===
        DialogueNode(
            id = "ch10_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_emerald_choir",
            text = "The fellowship arrives in the secluded sunken glade of the Emerald Choir. Massive ancient redwoods loom overhead, completely motionless. Dozens of singing dryads stand petrified in obsidian around a bubbling black spring.",
            nextNodeId = "ch10_hub"
        ),
        DialogueNode(
            id = "ch10_hub",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "Lyra kneels beside a statue of an elder dryad with tears of crystallized sap on her cheeks. 'When Malakor's priests came, they dumped obsidian venom into the spring. The dryads sang until their voices turned to glass. I was too afraid to sing loud enough to save them... But now, with your fellowship, I will not be quiet!'",
            choices = listOf(
                DialogueChoice("ch10_dryads_choice", "Touch the weeping obsidian statues of Lyra's sisters", listOf("dryads", "statues", "sisters", "touch", "weeping"), "ch10_dryads_dialogue", "ch10_dryads_complete"),
                DialogueChoice("ch10_seed_choice", "Prepare the Living Seed of the Sacred Willow", listOf("seed", "willow", "sacred", "prepare", "spring"), "ch10_seed_dialogue", "ch10_seed_complete")
            )
        ),
        DialogueNode(
            id = "ch10_dryads_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch10_dryads_complete",
            text = "Lyra presses her palms to the dryads' chests. A faint emerald heartbeat resonates through the stone. 'They are still alive inside! If we cleanse the venom source, their voices will bloom once more!'",
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
            text = "The spring roils violently! The Blighted Broodmother—a colossal arachnid behemoth dripping with obsidian venom—climbs from the pit, hissing with discordant screeching!",
            choices = listOf(
                DialogueChoice("ch10_boss_ready", "Cleanse the sacred spring and crush the Blighted Broodmother!", listOf("cleanse", "crush", "broodmother", "battle", "spring"), "ch10_broodmother_assault")
            )
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
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "lyra_trial_complete",
            text = "The Broodmother shatters into harmless green loam! Lyra drops the living seed into the bubbling waters. Crystal pure turquoise water erupts in geysers! Across the grove, the obsidian crusts peel away, and thirty dryads awaken, singing a glorious four-part hymn of thanksgiving! Lyra unlocks the Master Chant: Verdant Cataclysm!",
            nextNodeId = "ch10_post_victory"
        ),
        DialogueNode(
            id = "ch10_post_victory",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Lyra is radiant, surrounded by dancing woodland spirits. Zephyr tightens his gloves. 'Two debts cleared. Now comes mine. Master Nocturne has tracked us to the Blind Gorge. If I do not extinguish him today, his blades will seek us at our backs during the assault on Ouros.'",
            choices = listOf(
                DialogueChoice("ch10_to_zephyr_trial", "Enter the Blind Gorge for Zephyr's Trial", listOf("enter", "blind", "gorge", "zephyr", "trial"), "ch11_intro")
            )
        ),

        // === CHAPTER 11: THE SILENT BLADE'S RECKONING (Zephyr's Required Trial) ===
        DialogueNode(
            id = "ch11_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_blind_gorge",
            text = "The Blind Gorge is a labyrinth of razor obsidian slabs and subterranean thermal vents. An oppressive silence hangs in the air, muffled by dark alchemical smog.",
            nextNodeId = "ch11_hub"
        ),
        DialogueNode(
            id = "ch11_hub",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Zephyr tests the wind. 'Nocturne is the deadliest assassin in Aethelgard. He moves without sound or shadow. Watch your footing—the gorge is rigged with sonic mines and poisoned garrotes.'",
            choices = listOf(
                DialogueChoice("ch11_traps_choice", "Disarm the acoustic tripwires strung across the canyon", listOf("traps", "tripwires", "disarm", "acoustic", "mines"), "ch11_traps_dialogue", "ch11_traps_complete"),
                DialogueChoice("ch11_vials_choice", "Identify the obsidian venom vials left along the trail", listOf("vials", "venom", "poison", "identify", "trail"), "ch11_vials_dialogue", "ch11_vials_complete")
            )
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
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch11_vials_complete",
            text = "Lyra treats the obsidian vials with eucalyptus spores, neutralizing the airborne paralysis toxins. The air in the gorge clears!",
            nextNodeId = "ch11_hub"
        ),
        DialogueNode(
            id = "ch11_all_completed",
            speaker = DialogueSpeaker.NOCTURNE,
            side = SpeakerSide.RIGHT,
            text = "A whirlpool of black mist congeals on the canyon floor. Master Nocturne steps forward, masked in obsidian bone. 'You were my prize pupil, Zephyr. Yet you trade the perfection of silence for the babbling of fools.' Zephyr uncrosses his daggers: 'Silence is death. Words are how we choose each other!'",
            choices = listOf(
                DialogueChoice("ch11_boss_ready", "Strike down Master Nocturne and shatter the Black Guild!", listOf("strike", "nocturne", "guild", "battle", "master"), "ch11_nocturne_assault")
            )
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
            nextNodeId = "ch11_post_victory"
        ),
        DialogueNode(
            id = "ch11_post_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Sir Cedric claps Zephyr firmly on the shoulder. 'All four companions have proven their souls in trial! Our blades are keen, our spirits unyielding. The Clockwork Bastion of Ouros stands before us. Let us awaken the Third Great Bell!'",
            choices = listOf(
                DialogueChoice("ch11_to_ouros", "Assault the Clockwork Bastion of Ouros!", listOf("assault", "ouros", "clockwork", "bastion", "advance"), "ch12_intro")
            )
        ),

        // === CHAPTER 12: AWAKENING THE THIRD BELL (The Iron Belfry of Ouros) ===
        DialogueNode(
            id = "ch12_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_clockwork_bastion",
            text = "The Clockwork Bastion rises like an iron mountain into the clouds. Colossal brass gears groan with mechanical rhythm as pressurized steam vents roar along the parapets. At the pinnacle hangs the Third Great Bell: The Resonant Bastion!",
            nextNodeId = "ch12_hub"
        ),
        DialogueNode(
            id = "ch12_hub",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Zephyr points to the central clockwork core. 'The bell is clamped by magnetic hydraulic locks powered by the main steam manifold. If we shut those bypass valves, the locks will drop!'",
            choices = listOf(
                DialogueChoice("ch12_valves_choice", "Override the steam pressure bypass valves", listOf("valves", "steam", "pressure", "override", "bypass"), "ch12_valves_dialogue", "ch12_valves_complete"),
                DialogueChoice("ch12_cogs_choice", "Disengage the magnetic clamps locking the Great Bell", listOf("cogs", "clamps", "magnetic", "disengage", "gear"), "ch12_cogs_dialogue", "ch12_cogs_complete")
            )
        ),
        DialogueNode(
            id = "ch12_valves_dialogue",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch12_valves_complete",
            text = "Aethel invokes a burst of intense glacial ice, freezing the superheated pressure valves shut. The steam sirens howl and sputter out!",
            nextNodeId = "ch12_hub"
        ),
        DialogueNode(
            id = "ch12_cogs_dialogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch12_cogs_complete",
            text = "Cedric wedges his greatsword into the magnetic interlock lever, throwing his entire weight into it. With a thunderous clank, the massive iron clamps retract from the bell!",
            nextNodeId = "ch12_hub"
        ),
        DialogueNode(
            id = "ch12_all_completed",
            speaker = DialogueSpeaker.OUROS,
            side = SpeakerSide.RIGHT,
            text = "The foundry floor trembles as Clockwork Warmaster Ouros deploys from the furnace elevator—a ten-foot armored automaton wielding molten steam cannons and backed by iron phalanx guards! 'Intruders detected! Protocol: Silence the living!'",
            choices = listOf(
                DialogueChoice("ch12_boss_ready", "Destroy Warmaster Ouros and ring the Iron Belfry!", listOf("destroy", "warmaster", "ouros", "belfry", "battle"), "ch12_warmaster_assault")
            )
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
            nextNodeId = "ch12_post_victory"
        ),
        DialogueNode(
            id = "ch12_post_victory",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "Three Great Bells restored! The sky bridge has opened! The capital gates of Sol-Aethel lie just ahead. Forward, to the heart of the Blight!",
            choices = listOf(
                DialogueChoice("ch12_to_citadel", "Cross the sky bridge to the Silent Citadel Gates", listOf("cross", "bridge", "citadel", "gates", "advance"), "ch13_intro")
            )
        ),

        // === CHAPTER 13: BREACH OF THE SILENT CITADEL ===
        DialogueNode(
            id = "ch13_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_silent_citadel",
            text = "High above the cloudline on the floating plateau of Sol-Aethel, the Silent Citadel looms in terrifying majesty. Monolithic battlements of black glass reflect the eerie silence of the capital. The Great Gates are bolted shut with obsidian sorcery.",
            nextNodeId = "ch13_hub"
        ),
        DialogueNode(
            id = "ch13_hub",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Cedric looks upon the city of his youth. 'The streets of Sol-Aethel were once filled with songs, market calls, and the laughter of pilgrims. Now it is a mausoleum in the sky. We must break the seal on these gates!'",
            choices = listOf(
                DialogueChoice("ch13_gate_choice", "Inspect the fortified black glass portcullis", listOf("gate", "portcullis", "inspect", "glass", "black"), "ch13_gate_dialogue", "ch13_gate_complete"),
                DialogueChoice("ch13_seal_choice", "Purge the obsidian seal binding the entrance", listOf("seal", "purge", "dispel", "binding", "obsidian"), "ch13_seal_dialogue", "ch13_seal_complete")
            )
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
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch13_seal_complete",
            text = "Aethel unleashes a focused harmonic incantation. Cracks of golden fire race across the black glass seal, weakening its grip!",
            nextNodeId = "ch13_hub"
        ),
        DialogueNode(
            id = "ch13_all_completed",
            speaker = DialogueSpeaker.VAELOR,
            side = SpeakerSide.RIGHT,
            text = "The black gates open to reveal Commander Vaelor, Hand of Malakor, flanked by obsidian sentinels! At his belt hangs the terrifying Void Horn. 'You have climbed high, little songbirds. But here, the sky belongs to the void!' Vaelor raises the horn to his lips!",
            choices = listOf(
                DialogueChoice("ch13_boss_ready", "Slay Commander Vaelor and breach the Citadel!", listOf("slay", "vaelor", "citadel", "battle", "horn"), "ch13_vaelor_assault")
            )
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
            nextNodeId = "ch13_post_victory"
        ),
        DialogueNode(
            id = "ch13_post_victory",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            text = "Vaelor is dead. The path to the Void Reservoir is clear. Let us reclaim what was stolen!",
            choices = listOf(
                DialogueChoice("ch13_to_reservoir", "Enter the Chamber of the Void Reservoir", listOf("enter", "reservoir", "void", "chamber", "advance"), "ch14_intro")
            )
        ),

        // === CHAPTER 14: THE VOID RESERVOIR ===
        DialogueNode(
            id = "ch14_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_void_reservoir",
            text = "A vast sunken lake of pitch-black liquid silence stretches beneath a weeping starlight dome. Ripples move across the dark pool in complete, eerie noiselessness. Swirling voice motes struggle beneath the surface, trapped like captive stars.",
            nextNodeId = "ch14_hub"
        ),
        DialogueNode(
            id = "ch14_hub",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            text = "Lyra clutches her ears. 'The water... it is drinking our echoes before they can even leave our lips. This is where Malakor pools the stolen voices of everyone petrified across Aethelgard.'",
            choices = listOf(
                DialogueChoice("ch14_archons_choice", "Commune with the petrified High Archons kneeling by the lake", listOf("archons", "commune", "high", "statues", "kneeling"), "ch14_archons_dialogue", "ch14_archons_complete"),
                DialogueChoice("ch14_eddies_choice", "Dispel the swirling silt eddies of liquid silence", listOf("eddies", "dispel", "silt", "liquid", "silence"), "ch14_eddies_dialogue", "ch14_eddies_complete")
            )
        ),
        DialogueNode(
            id = "ch14_archons_dialogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch14_archons_complete",
            text = "Cedric kneels beside the frozen Archons. 'Their minds are still trapped in prayer. They warn us: the lake has a guardian—the Abyssal Leviathan. We must chant with overwhelming volume and cadence to break its muffle aura!'",
            nextNodeId = "ch14_hub"
        ),
        DialogueNode(
            id = "ch14_eddies_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch14_eddies_complete",
            text = "Lyra chants an invigorating cleansing verse. The black liquid eddies churn and clear, restoring acoustic clarity to the shoreline!",
            nextNodeId = "ch14_hub"
        ),
        DialogueNode(
            id = "ch14_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The liquid silence rises into a towering tidal wave as the colossal Abyssal Leviathan breaches the surface! Tendrils of pure void lash out, suffocating all sound in their wake!",
            choices = listOf(
                DialogueChoice("ch14_boss_ready", "Strike the Abyssal Leviathan and free the stolen voices!", listOf("strike", "leviathan", "free", "voices", "battle"), "ch14_leviathan_assault")
            )
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
            nextNodeId = "ch14_post_victory"
        ),
        DialogueNode(
            id = "ch14_post_victory",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The voices are free! They swirl ahead of us, lighting the ribbon stair into the heavens. The summit is within our grasp!",
            choices = listOf(
                DialogueChoice("ch14_to_spire", "Ascend the Celestial Ribbon Stair to the Summit", listOf("ascend", "ribbon", "stair", "summit", "spire", "advance"), "ch15_intro")
            )
        ),

        // === CHAPTER 15: ASCENT OF THE CELESTIAL SPIRE ===
        DialogueNode(
            id = "ch15_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_celestial_spire",
            text = "You ascend a stairway made of solidified harmonic light suspended between the clouds and stars. The four Great Bell Towers of Aethelgard form a colossal cross of gold, jade, and iron below you. Ahead stands the Celestial Belfry.",
            nextNodeId = "ch15_hub"
        ),
        DialogueNode(
            id = "ch15_hub",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "The four companions pause on the final landing before the great double doors. The fellowship stands at the edge of destiny.",
            choices = listOf(
                DialogueChoice("ch15_cedric_choice", "Speak with Sir Cedric upon the threshold of dawn", listOf("cedric", "dawn", "vow", "speak", "templar"), "ch15_cedric_dialogue", "ch15_cedric_complete"),
                DialogueChoice("ch15_lyra_choice", "Speak with Lyra beneath the starlight canopy", listOf("lyra", "starlight", "grove", "warden", "speak"), "ch15_lyra_dialogue", "ch15_lyra_complete"),
                DialogueChoice("ch15_zephyr_choice", "Speak with Zephyr overlooking the waking world", listOf("zephyr", "shadow", "world", "overlook", "speak"), "ch15_zephyr_dialogue", "ch15_zephyr_complete")
            )
        ),
        DialogueNode(
            id = "ch15_cedric_dialogue",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "ch15_cedric_complete",
            text = "Cedric adjusts his gauntlet and looks at you with profound respect. 'I was an exile, broken by guilt. You taught me that honor is not a wall—it is the courage to speak for those who cannot. Whatever waits behind those doors, Invocator, my shield is yours until my last breath.'",
            nextNodeId = "ch15_hub"
        ),
        DialogueNode(
            id = "ch15_lyra_dialogue",
            speaker = DialogueSpeaker.LYRA,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch15_lyra_complete",
            text = "Lyra catches a drifting voice mote in her palm and smiles softly. 'I lived in terror of the quiet for so many years. But together, our voices made the blossoms bloom and the dryads sing. We are going to bring the morning back to everyone.'",
            nextNodeId = "ch15_hub"
        ),
        DialogueNode(
            id = "ch15_zephyr_dialogue",
            speaker = DialogueSpeaker.ZEPHYR,
            side = SpeakerSide.LEFT,
            setFlagOnEnter = "ch15_zephyr_complete",
            text = "Zephyr rests a hand on his daggers. 'In the shadows, they told me words were weakness. But I see now that words are how we bind our hearts together in the dark. Malakor thinks silence is peace. Let us show him the storm.'",
            nextNodeId = "ch15_hub"
        ),
        DialogueNode(
            id = "ch15_all_completed",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Golden Archon Custodians and Celestial Spires materialize to guard the final sanctum. They raise their halberds: 'Only those of true harmonic resonance may enter the Belfry of Eternity!'",
            choices = listOf(
                DialogueChoice("ch15_boss_ready", "Prove your fellowship's resonance to the Archons!", listOf("prove", "resonance", "archons", "battle", "enter"), "ch15_custodians_assault")
            )
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
            nextNodeId = "ch15_post_victory"
        ),
        DialogueNode(
            id = "ch15_post_victory",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "The final belfry is open. The fourth Great Bell—The Bell of Eternity—awaits. It is time to end the silence forever.",
            choices = listOf(
                DialogueChoice("ch15_to_finale", "Enter the Celestial Belfry to confront Grand Inquisitor Malakor", listOf("enter", "belfry", "malakor", "sovereign", "finale", "advance"), "ch16_intro")
            )
        ),

        // === CHAPTER 16: THE PRIMORDIAL SYLLABLE (Grand Finale) ===
        DialogueNode(
            id = "ch16_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            changeSceneId = "scene_final_summit",
            text = "The pinnacle of the Spire of Echoes touches the outer rim of the cosmos. High above hangs the Fourth Great Bell—The Bell of Eternity—cast from star-metal and meteoric glass. Grand Inquisitor Malakor stands beneath it, his filigree muzzle mask gleaming beneath cold crimson eyes.",
            nextNodeId = "ch16_confrontation"
        ),
        DialogueNode(
            id = "ch16_confrontation",
            speaker = DialogueSpeaker.MALAKOR,
            side = SpeakerSide.RIGHT,
            text = "Malakor's telepathic voice echoes directly into your minds, cold and tragic: 'Why do you fight for voice? Every war began with a proclamation. Every heartbreak began with a whisper. Every cruelty was justified with a spoken lie. Silence is the only mercy that lasts forever.'",
            nextNodeId = "ch16_fellowship_reply"
        ),
        DialogueNode(
            id = "ch16_fellowship_reply",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "Aethel steps forward, voice ringing like pure bronze: 'Words can wound, Malakor, but words are also how we say I love you. Words are how we promise to protect each other. Without voice, peace is just an empty grave! We will not let your sorrow mute the universe!'",
            choices = listOf(
                DialogueChoice("ch16_boss_ready", "Confront Grand Inquisitor Malakor, The Mute Sovereign!", listOf("confront", "malakor", "sovereign", "final", "battle"), "ch16_malakor_assault")
            )
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
            speaker = DialogueSpeaker.MALAKOR,
            side = SpeakerSide.RIGHT,
            setFlagOnEnter = "malakor_defeated",
            text = "Malakor falls to his knees upon the celestial dais. The filigree mask cracks and clatters to the stone floor. For the first time in five hundred years, color returns to his pale skin and a tear tracks down his cheek. He touches his throat and whispers with a restored, frail human voice: 'I remember... the song my mother sang.' He closes his eyes with a serene smile and dissolves into peaceful motes of golden light.",
            nextNodeId = "ch16_toll_bell"
        ),
        DialogueNode(
            id = "ch16_toll_bell",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The fellowship approaches the Bell of Eternity. Together, all four companions grasp the cord of braided starlight. The player invokes the First Word with full lung power: 'LET THERE BE ECHO!'",
            choices = listOf(
                DialogueChoice("ch16_ring_bell", "Toll the Bell of Eternity and awaken Aethelgard!", listOf("toll", "ring", "bell", "eternity", "awaken"), "epilogue_awakening")
            )
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
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The fellowship stands upon the high overlook above Whispering Pines. Sir Cedric pledges: 'I shall rebuild the Order of the Harmonious Dawn—knights dedicated to defending the right of every living soul to speak freely.' Lyra smiles as green sprouts burst from her staff: 'The Emerald Choir will sing again, louder and more joyous than ever.' Zephyr flips his daggers into his belt with a grin: 'And I shall travel from province to province, carrying the stories of what we did here.'",
            nextNodeId = "epilogue_invocator"
        ),
        DialogueNode(
            id = "epilogue_invocator",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
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
