package com.voicerpg.android.engine

import android.content.Context
import androidx.core.util.AtomicFile
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.voicerpg.android.model.GameSaveData
import com.voicerpg.android.model.PlayerCustomization
import com.voicerpg.android.model.SaveSlotInfo
import com.voicerpg.android.model.SaveSummary
import com.voicerpg.android.model.SavedCharacterStats
import java.io.File
import java.io.FileOutputStream

/**
 * Robust Persistence Manager powered by AtomicFile and Multi-Slot Support.
 * Uses Android's AtomicFile to guarantee zero save file corruption even across unexpected
 * process termination or battery depletion.
 * Supports up to 3 independent campaign slots with seamless migration from single-save legacy versions.
 */
class SaveManager(private val context: Context? = null) {

    companion object {
        const val MAX_SLOTS = 3
        const val DEFAULT_SLOT = 1
        private const val LEGACY_SAVE_FILE = "save_game_v1.json"
        fun getSlotFileName(slot: Int): String = "save_slot_$slot.json"
    }

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    var currentSlot: Int = DEFAULT_SLOT
        set(value) {
            field = value.coerceIn(1, MAX_SLOTS)
        }

    // In-memory slot storage for unit tests and headless JVM environments
    private val inMemorySlots = mutableMapOf<Int, String>()

    init {
        migrateLegacySaveIfNeeded()
    }

    private fun getSaveFile(slot: Int): File? {
        val validSlot = slot.coerceIn(1, MAX_SLOTS)
        return context?.let { File(it.filesDir, getSlotFileName(validSlot)) }
    }

    private fun getLegacySaveFile(): File? {
        return context?.let { File(it.filesDir, LEGACY_SAVE_FILE) }
    }

    private fun migrateLegacySaveIfNeeded() {
        val legacy = getLegacySaveFile()
        val slot1 = getSaveFile(1)
        if (legacy != null && legacy.exists() && slot1 != null && !slot1.exists()) {
            try {
                val legacyJson = legacy.readText()
                if (legacyJson.isNotBlank()) {
                    writeAtomic(slot1, legacyJson)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun writeAtomic(file: File, content: String) {
        val atomicFile = AtomicFile(file)
        var stream: FileOutputStream? = null
        try {
            stream = atomicFile.startWrite()
            stream.write(content.toByteArray(Charsets.UTF_8))
            atomicFile.finishWrite(stream)
        } catch (e: Exception) {
            if (stream != null) {
                atomicFile.failWrite(stream)
            }
            throw e
        }
    }

    private fun readAtomic(file: File): String? {
        if (!file.exists()) return null
        return try {
            val atomicFile = AtomicFile(file)
            val bytes = atomicFile.readFully()
            String(bytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun hasSave(slot: Int = currentSlot): Boolean {
        val validSlot = slot.coerceIn(1, MAX_SLOTS)
        val file = getSaveFile(validSlot)
        return if (file != null) {
            file.exists() && file.length() > 0
        } else {
            !inMemorySlots[validSlot].isNullOrBlank()
        }
    }

    fun save(data: GameSaveData, slot: Int = currentSlot): Boolean {
        val validSlot = slot.coerceIn(1, MAX_SLOTS)
        return try {
            val updated = data.copy(saveTimestamp = System.currentTimeMillis())
            val json = gson.toJson(updated)
            val file = getSaveFile(validSlot)
            if (file != null) {
                writeAtomic(file, json)
            } else {
                inMemorySlots[validSlot] = json
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun load(slot: Int = currentSlot): GameSaveData? {
        val validSlot = slot.coerceIn(1, MAX_SLOTS)
        return try {
            val file = getSaveFile(validSlot)
            val json = if (file != null) {
                readAtomic(file)
            } else {
                inMemorySlots[validSlot]
            }

            if (json.isNullOrBlank()) return null
            gson.fromJson(json, GameSaveData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteSave(slot: Int = currentSlot): Boolean {
        val validSlot = slot.coerceIn(1, MAX_SLOTS)
        inMemorySlots.remove(validSlot)
        val file = getSaveFile(validSlot)
        return if (file != null) {
            val atomic = AtomicFile(file)
            atomic.delete()
            true
        } else {
            true
        }
    }

    fun getSlotSummary(slot: Int): SaveSummary? {
        val data = load(slot) ?: return null
        val scene = StoryScript.ALL_SCENES[data.currentSceneId] ?: StoryScript.SCENE_COTTAGE
        return SaveSummary(
            slotIndex = slot,
            heroName = data.player.name,
            heroClassTitle = data.player.heroClass.title,
            chapterTitle = scene.chapterTitle,
            sceneName = scene.name,
            partySize = data.partyStats.size.coerceAtLeast(1),
            timestamp = data.saveTimestamp
        )
    }

    fun getAllSlotInfos(): List<SaveSlotInfo> {
        return (1..MAX_SLOTS).map { slot ->
            SaveSlotInfo(
                slotIndex = slot,
                summary = getSlotSummary(slot)
            )
        }
    }

    fun createInitialSave(customization: PlayerCustomization, slot: Int = currentSlot): GameSaveData {
        val validSlot = slot.coerceIn(1, MAX_SLOTS)
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

        save(newSave, validSlot)
        return newSave
    }
}
