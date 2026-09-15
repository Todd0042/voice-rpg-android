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

    val ALL_SCENES = mapOf(
        SCENE_COTTAGE.id to SCENE_COTTAGE,
        SCENE_VILLAGE.id to SCENE_VILLAGE,
        SCENE_CROSSROADS.id to SCENE_CROSSROADS,
        SCENE_CAMP.id to SCENE_CAMP,
        SCENE_CAVE.id to SCENE_CAVE,
        SCENE_SWAMP.id to SCENE_SWAMP
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
            text = "Glorious! Your voice pierces the darkness like the morning sun! With the power of the Logos and the Aegis of the Sun, we can save Aethelgard.",
            nextNodeId = "crossroads_conclusion"
        ),
        DialogueNode(
            id = "crossroads_conclusion",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "The Fellowship of Echoes begins today. To the Bell Tower!",
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

        // === CHAPTER 3 COMMENCEMENT ===
        DialogueNode(
            id = "chapter3_intro",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "CHAPTER 3: THE ASCENT OF SOLARIA. Leaving the forest camp behind, you and Sir Cedric ascend the rocky slopes toward the colossal stone aqueducts of the First Bell Tower. Looming in the morning mist, the ancient gateway stands sealed by the Mute Sovereign's obsidian wards.",
            changeSceneId = SCENE_CAVE.id,
            choices = listOf(
                DialogueChoice("ch3_chime", "Raise the Echo Chime to unseal the Aqueduct Gate", listOf("chime", "unseal", "raise", "echo", "gate"), "ch3_gate_unsealed"),
                DialogueChoice("ch3_scout", "Inspect the corrupted sentinels guarding the portal", listOf("inspect", "sentinels", "guardians", "scout"), "ch3_sentinels_scout")
            )
        ),
        DialogueNode(
            id = "ch3_gate_unsealed",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "By the resonance of the First Dawn... Echo Chime, awaken!",
            nextNodeId = "ch3_aqueduct_boss_trigger"
        ),
        DialogueNode(
            id = "ch3_sentinels_scout",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The gate is guarded by the Chittering Queen and her corrupted brood! We must purge them to breach the tower!",
            nextNodeId = "ch3_aqueduct_boss_trigger"
        ),
        DialogueNode(
            id = "ch3_aqueduct_boss_trigger",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "The Grave Broodmother descends from the aqueduct archway, screeching in deafening discord! Strike with your voice and cleanse the gate!",
            triggerBattleEncounterId = "cave_broodmother"
        ),
        DialogueNode(
            id = "ch3_victory_ascent",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The gate collapses open! Ahead lies the winding spiral staircase to the Solaria Bell chamber. The First Bell Tower is within our grasp!",
            nextNodeId = null
        )
    ).associateBy { it.id }
}
