# VoiceRPG Android: Agent Guidelines & Engineering Rules

This repository contains the native Android implementation of *VoiceRPG: Echoes of the Logos*. When pair programming on this codebase, adhere strictly to the following architectural guidelines and core project rules.

---

## 1. Combat Roster & Story Synchronization
* **Strict Narrative Roster Fidelity:** Never include companions in combat who have not yet officially joined the fellowship in the active story.
  * **Act I (Chapters 1–4):** Strictly the **Duo Fellowship** (**Aethel** + **Sir Cedric**). Lyra must NOT appear in combat.
  * **Act II Chapter 5:** Strictly the **Duo Fellowship** on a rescue operation.
  * **Act II Chapter 6:** **Trio Fellowship** (**Aethel**, **Sir Cedric**, and **Lyra the Grove Warden**).
  * Subsequent companions (Vane, Zephyr) join only when recruited in their respective chapters.
* Default fallback parties in `CombatViewModel` and test encounters must default to the Duo Fellowship unless testing multi-character mechanics explicitly.

---

## 2. Dialogue Hubs & Completed Option Elimination
* **No Endless Loops:** When the player visits a narrative hub offering multiple choices/inquiries (e.g. Camp, Aqueducts, Solaria Belfry, Drowned Fane):
  1. Each choice branch must set a distinct completion flag (e.g., `setFlagOnEnter = "ch5_creek_scouted"`).
  2. Choices with matching `completionFlag` must be filtered out / eliminated once completed so the player cannot loop endlessly.
  3. When all sub-story options in a hub are completed, the hub node must automatically advance to the next narrative milestone.
* **Rich Narrative Prose:** Maintain evocative, atmospheric JRPG storytelling with meaningful dialogue and distinct character voices, avoiding 1-line dismissive dialogue.

---

## 3. Screenless & Audio-First Playability (Pocket Mode)
* The game must remain **100% playable without looking at the screen**:
  * All dialogue lines and combat actions must support Text-To-Speech (TTS) narration.
  * Available choices must be readable aloud via `isReadChoicesEnabled` (*"Option 1: ... Option 2: ... What is your command?"*).
  * System toggles and status queries must be operable hands-free via speech meta-commands (*"Status"*, *"Enemies"*, *"Toggle narration"*, *"Read choices"*, *"Pocket mode"*, *"Auto listen"*, *"Options"*).
  * Auto-listen microphone re-arms seamlessly after TTS completion.

---

## 4. Multi-Voice Character Assignment
* Use Android's `android.speech.tts.Voice` API to programmatically discover installed voice models on the device:
  * Bind distinct physical voice models to each character:
    * **Sir Cedric:** Installed deep male baritone voice.
    * **Aethel (Hero):** Heroic female / invocator voice.
    * **Lyra:** Gentle nature warden voice.
    * **Narrator:** Impartial chronicle storyteller voice.
  * Fall back smoothly if fewer voice models are installed, using pitch and rate modulation as secondary character expressions.

---

## 5. Mandatory Verification, GitHub Release & Device Deployment
* **Automated Testing:** Always verify all changes with automated tests: `./gradlew test`.
* **Compilation:** Always build a fresh debug APK: `./gradlew assembleDebug`.
* **Mandatory GitHub Release For Every Change:** For every change or feature completed, always tag, create, and publish a new GitHub release with the compiled `app-debug.apk` attached as an asset.
* **Mandatory Phone Installation For Every Change:** For every change or feature completed, always automatically install the updated APK directly onto the user's connected phone via ADB (`adb install -r app/build/outputs/apk/debug/app-debug.apk`) without waiting to be asked.


