package com.voicerpg.android.engine

import com.voicerpg.android.model.DialogueNode
import com.voicerpg.android.model.SavedCharacterStats
import com.voicerpg.android.model.StoryScene

data class QuestMilestone(
    val chapterIndex: Int,
    val actNumber: Int,
    val title: String,
    val location: String,
    val summary: String,
    val icon: String = "✨",
    val isCurrent: Boolean = false
)

data class QuestRecap(
    val currentActTitle: String,
    val currentChapterTitle: String,
    val currentSceneName: String,
    val activeObjective: String,
    val fellowshipRoster: List<String>,
    val milestones: List<QuestMilestone>,
    val spokenRecap: String
)

/**
 * Generates an episodic "Previously on your quest..." narrative recap (inspired by
 * Pokémon FireRed / LeafGreen) chronicling the major milestones achieved up to the
 * player's active position in the story.
 */
object StoryRecapEngine {

    private data class StaticMilestone(
        val chapterIndex: Int,
        val actNumber: Int,
        val title: String,
        val location: String,
        val summary: String,
        val icon: String
    )

    private val CANON_MILESTONES = listOf(
        StaticMilestone(
            chapterIndex = 0,
            actNumber = 1,
            title = "Prologue: The Morning Without Echo",
            location = "Aethel's Cottage & Whispering Pines",
            summary = "The world fell silent under the Great Mute. You awoke in your cottage, discovered the dormant resonant power of the Logos, and drove back the creeping shadow corruption.",
            icon = "🌅"
        ),
        StaticMilestone(
            chapterIndex = 1,
            actNumber = 1,
            title = "Chapter 1: The Oathkeeper of Dawn",
            location = "The Sun Shrine Crossroads",
            summary = "At the misty crossroads, you joined forces with Sir Cedric of the Oathkeepers, repelling an ambush of blighted beasts and forming a fellowship dedicated to ringing the Great Bells.",
            icon = "🛡️"
        ),
        StaticMilestone(
            chapterIndex = 2,
            actNumber = 1,
            title = "Chapter 2: Embers in the Gloom",
            location = "Fellowship Camp & Whispering Caverns",
            summary = "Gathering around the campfire, you and Cedric bonded under a midnight vigil and tracked the source of corruption into the damp, crystal-lit caverns.",
            icon = "⛺"
        ),
        StaticMilestone(
            chapterIndex = 3,
            actNumber = 1,
            title = "Chapter 3: The Ascent of Solaria",
            location = "The Aqueducts of Solaria",
            summary = "Ascending the colossal petrified aqueducts, you struck down the corruption sentinels and destroyed the colossal Cave Broodmother.",
            icon = "🏛️"
        ),
        StaticMilestone(
            chapterIndex = 4,
            actNumber = 1,
            title = "Chapter 4: The Great Bell of Solaria",
            location = "The Bell Chamber of Solaria",
            summary = "Breaching the foundation crypts and ascending high above the clouds, you defeated the castle horde and struck the Sun Bell of Solaria, dispelling the First Mute.",
            icon = "🔔"
        ),
        StaticMilestone(
            chapterIndex = 5,
            actNumber = 2,
            title = "Chapter 5: The Severed Resonance",
            location = "The Drowned Fane",
            summary = "You marched through the poisonous miasma of the Sunken Fen on a perilous rescue mission, uncovering the dark reach of the Mute Sovereign.",
            icon = "🌿"
        ),
        StaticMilestone(
            chapterIndex = 6,
            actNumber = 2,
            title = "Chapter 6: The Warden's Oath",
            location = "The Weeping Willow Sanctuary",
            summary = "Purifying the ancient sacred willow from the Swamp Behemoth, you saved Lyra the Grove Warden, who joined the fellowship with her life-giving verdant incantations.",
            icon = "🌱"
        ),
        StaticMilestone(
            chapterIndex = 7,
            actNumber = 2,
            title = "Chapter 7: Tuning the Veridian Chime",
            location = "The Sunken Catacombs",
            summary = "Navigating submerged jade ruins, you tuned the sacred resonant steles and vanquished the venomous Mire Wyrm.",
            icon = "🐍"
        ),
        StaticMilestone(
            chapterIndex = 8,
            actNumber = 2,
            title = "Chapter 8: The Shadowed Crags & The Defector",
            location = "The Shadowed Crags",
            summary = "Ambushed by Zephyr, an assassin of the Black Guild, you proved your cause was just. Zephyr defected from the Mute and joined your ranks as a swift shadowblade.",
            icon = "🗡️"
        ),
        StaticMilestone(
            chapterIndex = 9,
            actNumber = 3,
            title = "Chapter 9: The Broken Vow of Dawn",
            location = "The Mausoleum of the Sun",
            summary = "Sir Cedric faced his fallen mentor, Sir Galahault, atoning for the past, releasing his sworn brothers from corruption, and awakening his master spell, Aegis of Dawn.",
            icon = "⚔️"
        ),
        StaticMilestone(
            chapterIndex = 10,
            actNumber = 3,
            title = "Chapter 10: The Song of the Mute Grove",
            location = "The Emerald Choir Grove",
            summary = "Lyra returned to her ancestral grove, purging the blighted broodmother to resurrect the petrified dryads and claiming the primordial Verdant Cataclysm.",
            icon = "🌸"
        ),
        StaticMilestone(
            chapterIndex = 11,
            actNumber = 3,
            title = "Chapter 11: The Silent Blade's Reckoning",
            location = "The Blind Gorge & Obsidian Vaults",
            summary = "Zephyr infiltrated his former syndicate, defeating Spymaster Nocturne in the Obsidian Vaults and mastering Umbral Siphon.",
            icon = "🌑"
        ),
        StaticMilestone(
            chapterIndex = 12,
            actNumber = 3,
            title = "Chapter 12: Awakening the Third Bell",
            location = "The Clockwork Bastion of Ouros",
            summary = "Storming the towering iron clockwork bastion, you overcame Warmaster Ouros and rang the Third Great Bell of Creation.",
            icon = "⚙️"
        ),
        StaticMilestone(
            chapterIndex = 13,
            actNumber = 4,
            title = "Chapter 13: Breach of the Silent Citadel",
            location = "The Silent Citadel Gates",
            summary = "Piercing the monolithic obsidian ramparts of the Citadel, you clashed with Commander Vaelor and broke the vanguard of the Mute Sovereign.",
            icon = "🏰"
        ),
        StaticMilestone(
            chapterIndex = 14,
            actNumber = 4,
            title = "Chapter 14: The Void Reservoir",
            location = "The Cosmic Reservoir",
            summary = "Plunging into a cosmic sea of liquid silence, you hunted and destroyed the Abyssal Leviathan, liberating the imprisoned echoes of all creation.",
            icon = "🌊"
        ),
        StaticMilestone(
            chapterIndex = 15,
            actNumber = 4,
            title = "Chapter 15: Ascent of the Celestial Spire",
            location = "The Celestial Ribbon Stair",
            summary = "Ascending the crystalline stairway of harmonic light, you conquered the trials of Gold, Grove, and Shadow, striking down the Archon Custodians.",
            icon = "✨"
        ),
        StaticMilestone(
            chapterIndex = 16,
            actNumber = 4,
            title = "Chapter 16: The Primordial Syllable",
            location = "The Bell of Eternity — Spire Summit",
            summary = "At the peak of all existence, you shattered the mirror gauntlet and nullifier gates, confronting Grand Inquisitor Malakor to restore the Voice of Creation forever.",
            icon = "👑"
        )
    )

    fun getChapterIndex(scene: StoryScene, node: DialogueNode): Int {
        val title = scene.chapterTitle.lowercase()
        val id = node.id.lowercase()
        return when {
            title.contains("chapter 16") || id.startsWith("ch16_") -> 16
            title.contains("chapter 15") || id.startsWith("ch15_") -> 15
            title.contains("chapter 14") || id.startsWith("ch14_") -> 14
            title.contains("chapter 13") || id.startsWith("ch13_") -> 13
            title.contains("chapter 12") || id.startsWith("ch12_") -> 12
            title.contains("chapter 11") || id.startsWith("ch11_") -> 11
            title.contains("chapter 10") || id.startsWith("ch10_") -> 10
            title.contains("chapter 9") || id.startsWith("ch9_") -> 9
            title.contains("chapter 8") || id.startsWith("ch8_") -> 8
            title.contains("chapter 7") || id.startsWith("ch7_") -> 7
            title.contains("chapter 6") || id.startsWith("ch6_") -> 6
            title.contains("chapter 5") || id.startsWith("ch5_") -> 5
            title.contains("chapter 4") || id.startsWith("ch4_") || id.startsWith("chapter4_") -> 4
            title.contains("chapter 3") || id.startsWith("ch3_") || id.startsWith("chapter3_") -> 3
            title.contains("chapter 2") || id.startsWith("camp_") || id.startsWith("cavern_") || id.startsWith("marsh_") -> 2
            title.contains("chapter 1") || id.startsWith("crossroads_") -> 1
            else -> 0
        }
    }

    fun getActNumber(chapterIndex: Int): Int = when (chapterIndex) {
        in 0..4 -> 1
        in 5..8 -> 2
        in 9..12 -> 3
        else -> 4
    }

    fun getActTitle(actNumber: Int): String = when (actNumber) {
        1 -> "Act I: The Silenced Bells"
        2 -> "Act II: The Weeping Fen"
        3 -> "Act III: The Trials of Resonance"
        else -> "Act IV: The Primordial Syllable"
    }

    fun getActiveObjective(chapterIndex: Int, node: DialogueNode, flags: Map<String, Boolean>): String {
        if (node.triggerBattleEncounterId != null) {
            return "Defeat the enemies threatening the fellowship!"
        }
        return when (chapterIndex) {
            0 -> if (node.id.startsWith("village_")) "Investigate the petrified ruins of Whispering Pines and head toward the crossroads." else "Awaken the dormant power of Logos and leave your cottage."
            1 -> "Join Sir Cedric at the Sun Shrine Crossroads and repel the Blight ambush."
            2 -> if (flags["substory_rest_complete"] != true) "Investigate the camp surroundings and keep vigil by the fire." else "Ascend toward the Solaria Aqueducts."
            3 -> if (flags["ch3_all_completed"] != true) "Clear the corruption sentinels in the Aqueducts and defeat the Cave Broodmother." else "Enter the Crypt of the Foundation."
            4 -> if (node.id.contains("crypt")) "Navigate the Foundation Crypts toward the Solaria Bell Chamber." else "Ascend the Bell Chamber and ring the Sun Bell of Solaria."
            5 -> "Scout the Drowned Fane and locate the source of the fen miasma."
            6 -> if (flags["lyra_recruited"] == true) "Confer with Lyra and prepare to enter the Sunken Catacombs." else "Purify the sacred Weeping Willow and liberate Lyra the Grove Warden."
            7 -> if (flags["ch7_wyrm_defeated"] == true) "Proceed toward the Shadowed Crags." else "Tune the four sacred steles and slay the Mire Wyrm."
            8 -> if (flags["zephyr_recruited"] == true) "Coordinate with Zephyr and plan your assault on the Black Guild." else "Defeat the Black Guild executioner in the Shadowed Crags."
            9 -> if (flags["cedric_trial_complete"] == true) "Proceed to the Emerald Choir Grove." else "Help Sir Cedric face Sir Galahault in the Mausoleum of the Sun."
            10 -> if (flags["lyra_trial_complete"] == true) "Venture into the Blind Gorge." else "Aid Lyra in purging the corrupted seed at the Emerald Choir."
            11 -> if (flags["zephyr_trial_complete"] == true) "Advance to the Clockwork Bastion of Ouros." else "Assist Zephyr in infiltrating the Obsidian Vaults and defeating Nocturne."
            12 -> "Infiltrate the iron belfry, defeat Warmaster Ouros, and ring the Third Great Bell."
            13 -> "Breach the gates of the Silent Citadel and overcome Commander Vaelor."
            14 -> "Dive into the Void Reservoir and vanquish the sound-devouring Abyssal Leviathan."
            15 -> "Climb the Celestial Ribbon Stair, pass the tri-chime trials, and defeat the Archon Custodians."
            16 -> if (flags["game_completed"] == true) "The Logos is restored! Bask in the harmony of creation." else "Confront Grand Inquisitor Malakor at the Spire Summit and ring the Bell of Eternity!"
            else -> "Proceed along the path of resonance."
        }
    }

    fun getFellowshipRoster(partyStats: List<SavedCharacterStats>): List<String> {
        val names = mutableListOf("Aethel (Invocator)")
        if (partyStats.any { it.id == "cedric" }) names.add("Sir Cedric (Templar)")
        if (partyStats.any { it.id == "lyra" }) names.add("Lyra (Grove Warden)")
        if (partyStats.any { it.id == "zephyr" }) names.add("Zephyr (Shadowblade)")
        return names
    }

    fun buildRecap(
        scene: StoryScene,
        node: DialogueNode,
        flags: Map<String, Boolean>,
        partyStats: List<SavedCharacterStats>
    ): QuestRecap {
        val currentIndex = getChapterIndex(scene, node)
        val actNumber = getActNumber(currentIndex)
        val actTitle = getActTitle(actNumber)
        val objective = getActiveObjective(currentIndex, node, flags)
        val roster = getFellowshipRoster(partyStats)

        val milestones = mutableListOf<QuestMilestone>()
        for (i in 0..currentIndex) {
            val static = CANON_MILESTONES.getOrNull(i) ?: continue
            val isCurrent = (i == currentIndex)
            milestones.add(
                QuestMilestone(
                    chapterIndex = static.chapterIndex,
                    actNumber = static.actNumber,
                    title = static.title,
                    location = if (isCurrent) scene.name else static.location,
                    summary = static.summary,
                    icon = static.icon,
                    isCurrent = isCurrent
                )
            )
        }

        val spoken = generateSpokenRecap(
            chapterTitle = scene.chapterTitle,
            sceneName = scene.name,
            objective = objective,
            roster = roster,
            milestones = milestones
        )

        return QuestRecap(
            currentActTitle = actTitle,
            currentChapterTitle = scene.chapterTitle,
            currentSceneName = scene.name,
            activeObjective = objective,
            fellowshipRoster = roster,
            milestones = milestones,
            spokenRecap = spoken
        )
    }

    private fun generateSpokenRecap(
        chapterTitle: String,
        sceneName: String,
        objective: String,
        roster: List<String>,
        milestones: List<QuestMilestone>
    ): String {
        val previousMilestone = milestones.filter { !it.isCurrent }.lastOrNull()
        val milestoneSentence = if (previousMilestone != null) {
            "Previously on your quest: ${previousMilestone.summary}"
        } else {
            "Your quest has just begun."
        }

        val companionText = if (roster.size > 1) {
            "Standing with you: " + roster.drop(1).joinToString(", ") + "."
        } else {
            "You journey alone as the Awakened Invocator."
        }

        return "Chronicle recap. You are in $chapterTitle, at $sceneName. " +
            "$milestoneSentence " +
            "$companionText " +
            "Current objective: $objective. " +
            "What is your command?"
    }
}
