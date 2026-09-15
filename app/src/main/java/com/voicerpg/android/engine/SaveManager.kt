package com.voicerpg.android.engine

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.voicerpg.android.model.GameSaveData
import com.voicerpg.android.model.PartyMember
import com.voicerpg.android.model.PlayerCustomization
import com.voicerpg.android.model.SavedCharacterStats
import java.io.File

/**
 * Robust JSON-based Persistence Manager.
 * Preserves the player's initial loadout, character stats, entire decision history,
 * story progression, and world accomplishments across sessions.
 */
class SaveManager(private val context: Context? = null) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val saveFileName = "save_game_v1.json"

    // In-memory slot for unit testing / headless JVM environments
    private var inMemorySaveJson: String? = null

    private fun getSaveFile(): File? {
        return context?.let { File(it.filesDir, saveFileName) }
    }

    fun hasSave(): Boolean {
        val file = getSaveFile()
        return if (file != null) file.exists() && file.length() > 0 else inMemorySaveJson != null
    }

    fun save(data: GameSaveData): Boolean {
        return try {
            val updated = data.copy(saveTimestamp = System.currentTimeMillis())
            val json = gson.toJson(updated)
            val file = getSaveFile()
            if (file != null) {
                file.writeText(json)
            } else {
                inMemorySaveJson = json
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun load(): GameSaveData? {
        return try {
            val file = getSaveFile()
            val json = if (file != null && file.exists()) {
                file.readText()
            } else {
                inMemorySaveJson
            }

            if (json.isNullOrBlank()) return null
            gson.fromJson(json, GameSaveData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteSave(): Boolean {
        inMemorySaveJson = null
        val file = getSaveFile()
        return file?.delete() ?: true
    }

    /**
     * Initializes a brand-new game save from a custom character creation loadout.
     */
    fun createInitialSave(customization: PlayerCustomization): GameSaveData {
        val heroClass = customization.heroClass
        val starterSpells = ClassSpellLibrary.getSpellsForClass(heroClass)

        val initialHeroStats = SavedCharacterStats(
            id = "hero",
            name = customization.name,
            loreClass = heroClass.title,
            currentHp = heroClass.startingHp,
            maxHp = heroClass.startingHp,
            currentMp = heroClass.startingMp,
            maxMp = heroClass.startingMp,
            speed = heroClass.startingSpeed,
            level = 1,
            xp = 0,
            spellIds = starterSpells.map { it.id }
        )

        val newSave = GameSaveData(
            player = customization,
            currentSceneId = "scene_cottage",
            currentNodeId = "cottage_intro",
            decisionsMade = emptyList(),
            partyStats = listOf(initialHeroStats),
            defeatedEncounters = emptyList(),
            unlockedCompanions = listOf("hero"),
            achievements = listOf("AWAKENED_THE_LOGOS")
        )

        save(newSave)
        return newSave
    }
}
