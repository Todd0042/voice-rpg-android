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
            text = "Rest your vocal cords, friend. Tomorrow we cross into the Sunken Hollows. Before we sleep, what weighs upon your mind?",
            choices = listOf(
                DialogueChoice("c_lore", "Ask about the Silent Blight and Mute Sovereign", listOf("blight", "silence", "sovereign", "lore"), "camp_lore_blight"),
                DialogueChoice("c_towers", "Inquire about the Four Bell Towers", listOf("tower", "towers", "bells", "chime"), "camp_lore_towers"),
                DialogueChoice("c_rest", "Rest by the fire to restore health and mana", listOf("rest", "sleep", "fire", "restore"), "camp_rest")
            )
        ),
        DialogueNode(
            id = "camp_lore_blight",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "Ten cycles ago, the Mute Sovereign descended upon the High Sanctum with an absolute Void that devoured all sound. Those caught in its wake turned to hollow obsidian glass. Only those with the Logos harmonic in their blood can speak.",
            nextNodeId = "camp_next_morning"
        ),
        DialogueNode(
            id = "camp_lore_towers",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The Four Great Bell Towers were forged by the Primordial Chanters. Each bell carries a sacred acoustic frequency that purges the silence. The first tower crowns the Sunken Hollows, past the Whispering Caverns.",
            nextNodeId = "camp_next_morning"
        ),
        DialogueNode(
            id = "camp_rest",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "You sit in quiet meditation by the dancing embers. The resonance in your chest warms and stabilizes. Your spirit and vocal power are renewed for the trials ahead.",
            nextNodeId = "camp_next_morning"
        ),
        DialogueNode(
            id = "camp_next_morning",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Dawn breaks over the jagged forest canopy. A fork in the trail lies ahead: the damp mouth of the Whispering Caverns descends underground, while an overgrown path leads toward the mist-covered marsh.",
            choices = listOf(
                DialogueChoice("c_cavern", "Descend into the Whispering Caverns", listOf("cavern", "cave", "descend", "underground"), "cavern_entry"),
                DialogueChoice("c_marsh", "Traverse the Rotting Marsh", listOf("marsh", "swamp", "bog", "mire"), "marsh_entry")
            )
        ),
        // Cavern Path
        DialogueNode(
            id = "cavern_entry",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The air in these caves carries a faint crystalline hum. Watch your step... blighted crawlers lurk in the shadows.",
            changeSceneId = SCENE_CAVE.id,
            nextNodeId = "cavern_exploration"
        ),
        DialogueNode(
            id = "cavern_exploration",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Bioluminescent azure crystals cling to the stalactites above, pulsing in rhythm with your breathing. Ahead lies the subterranean aqueduct to the Bell Tower, but a chittering horror blocks the passage!",
            triggerBattleEncounterId = "cave_broodmother"
        ),
        DialogueNode(
            id = "cavern_post_battle",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The broodmother has fallen! Look ahead — the subterranean aqueducts rise before us, carving a path directly into the Bell Tower's foundations!",
            nextNodeId = "chapter2_conclusion"
        ),
        // Marsh Path
        DialogueNode(
            id = "marsh_entry",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "The Rotting Marsh... keep to the dry moss mounds. The water here is foul with the Blight's mute venom.",
            changeSceneId = SCENE_SWAMP.id,
            nextNodeId = "marsh_exploration"
        ),
        DialogueNode(
            id = "marsh_exploration",
            speaker = DialogueSpeaker.NARRATOR,
            side = SpeakerSide.CENTER_NARRATOR,
            text = "Thick emerald fog drifts across weeping willow branches. Suddenly, the bog churns as a towering Bog Behemoth surges from the mire with razor leeches!",
            triggerBattleEncounterId = "swamp_behemoth"
        ),
        DialogueNode(
            id = "marsh_post_battle",
            speaker = DialogueSpeaker.CEDRIC,
            side = SpeakerSide.RIGHT,
            text = "A valiant victory! The corrupted willow weeping has ceased. The causeway to the First Bell Tower is open!",
            nextNodeId = "chapter2_conclusion"
        ),
        DialogueNode(
            id = "chapter2_conclusion",
            speaker = DialogueSpeaker.AETHEL,
            side = SpeakerSide.LEFT,
            text = "We stand at the threshold of the First Bell Tower. Let our voices awaken the bell and break the silence of Aethelgard!",
            choices = listOf(
                DialogueChoice("c_replay_camp", "Return to camp and reflect", listOf("camp", "reflect", "rest", "return"), "camp_intro"),
                DialogueChoice("c_replay_branch", "Explore the other fork in the trail", listOf("fork", "explore", "other", "trail"), "camp_next_morning")
            )
        )
    ).associateBy { it.id }
}
