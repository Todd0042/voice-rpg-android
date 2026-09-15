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
        SCENE_WILLOW_SANCTUARY.id to SCENE_WILLOW_SANCTUARY
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
                DialogueChoice("ch6_view_willow", "Gaze upon the blossoming Weeping Willow Sanctuary", listOf("gaze", "view", "willow", "blossom", "sanctuary"), "ch6_willow_purified"),
                DialogueChoice("ch6_commune_lyra", "Speak with Lyra beside the sacred pool", listOf("speak", "commune", "lyra", "pool"), "ch6_chime_revealed")
            )
        )
    ).associateBy { it.id }
}
