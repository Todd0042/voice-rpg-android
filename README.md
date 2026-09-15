# VoiceRPG Android: Echoes of the Logos

An immersive, voice-commanded retro JRPG designed natively for Android. All gameplay—incantations, tactical party combat, world exploration, dialogue choices, and game settings—can be commanded via natural voice or touch.

> [!NOTE]
> For the complete architectural specification, system design, and mathematical formulas, see [DESIGN.md](DESIGN.md).  
> For development guidelines and autonomous engineering principles, see [AGENTS.md](AGENTS.md).

---

## Core Pillars & Key Features

### 1. 100% Screenless & Pocket Mode (Audio-First Accessibility)
* **Play Anywhere, Eyes-Free:** Play while walking with the phone in your pocket, or fully accessible for blind and visually impaired gamers.
* **Continuous Hands-Free Voice Control:** Optional auto-listen microphone mode automatically re-arms after every action and dialogue utterance.
* **On-the-Fly Voice Commands:**
  * Tactical reports: *"Status"*, *"Enemies"*, *"Fellowship"*, *"Help"*.
  * System toggles: *"Toggle narration"*, *"Read choices"*, *"Pocket mode"*, *"Auto listen"*, *"Open options"*, *"Close options"*.
* **Multi-Voice TTS Character Engine:**
  * Leverages Android's `android.speech.tts.Voice` API to dynamically bind distinct physical voice models to each character:
    * **Sir Cedric:** Installed deep male baritone voice.
    * **Aethel (Hero):** Heroic female / invocator voice.
    * **Lyra:** Gentle druidic grove voice.
    * **Narrator:** Impartial chronicle storyteller voice.
  * Preserves subtle pitch and cadence shaping as secondary expressive accents.

### 2. Narrative Hubs & Completed Option Elimination
* **No Endless Loops:** Dialogue hubs (e.g., Whispering Pines Camp, Solaria Aqueducts, the Belfry, and the Drowned Fane) track story progress with completion flags.
* **Option Elimination:** When a sub-story or objective is completed, that option is eliminated from the choice list.
* **Milestone Progression:** Once all sub-stories in a hub are explored, the story automatically advances to the next chapter.
* **16-Bit JRPG Presentation:** Character portraits on left and right flanks with speaking animations and vintage dialogue bubbles over atmospheric environmental backgrounds.

### 3. Party Roster Fidelity & Dynamic Scalability
* **Story-Grounded Combat:** Combat encounters strictly reflect active story party members:
  * **Act I (Chapters 1–4):** Strictly the **Duo Fellowship** (Aethel + Sir Cedric).
  * **Act II Chapter 5:** Duo rescue mission into the Rotting Marsh.
  * **Act II Chapter 6:** **Trio Fellowship** (Aethel, Sir Cedric, and recruited Grove Warden Lyra).
* **Flexible Engine Architecture:** Supports battles up to 4 heroes vs. 6 enemies with front/back rows and dynamic mid-battle reinforcements/summons (up to a strict 6-enemy cap).

### 4. Character Creation & Persistent Save System
* **Initial Loadout:** Custom hero name, class selection (*Elementalist, Templar, Grove Warden, Shadowblade*), and starter aesthetic aura colors.
* **Robust JSON Persistence (`SaveManager`):** Automatically saves every decision made, completed choice IDs, party stats, defeated encounters, and audio settings across app restarts.

### 5. On-Device Incantation Resonance Engine
* 100% offline, zero-cloud speech recognition via Android's `SpeechRecognizer`.
* Evaluates spoken chants across 5 pillars: Thematic Vocabulary Density, Lexical Richness & Cadence, Acoustic Volume, Pitch Modulation, and Anti-Repetition Novelty.
* Rewards creative, expressive chants with up to a **+200% bonus to damage/healing** and visual particle scaling up to **450+ particles** with screen-splitting shockwaves.

---

## Story Overview

* **Act I: The Falling Silence Shattered (Chapters 1–4):**
  * *Prologue:* Awakening in Whispering Pines; harnessing the spoken Logos.
  * *Chapter 1:* Repelling the Forest Ambush at the Old Way Shrine with Sir Cedric.
  * *Chapter 2:* Campfire fellowship and investigating the petrified village.
  * *Chapter 3:* The Solaria Aqueduct infiltration, corrupted sentinels, and tuning fork discovery.
  * *Chapter 4:* Breaching the Sun-Tower Belfry, garrison battle, and ringing the First Great Bell of Solaria!
* **Act II: The Severed Resonance (Chapters 5–8):**
  * *Chapter 5: The Drowned Fane & The Briar Cage:* Descent into the Rotting Marsh; assaulting the Blight Binder's lair to rescue Lyra.
  * *Chapter 6: The Warden's Oath & The Weeping Willow:* Lyra joins the fellowship; trio battle against the Bog Behemoth to purify the sacred pool and uncover the Second Great Bell.
  * *Chapters 7–8:* Tuning the Veridian Chime and delving toward the Clockwork Bastion.

---

## Technical Architecture

```
app/src/main/java/com/voicerpg/android/
├── MainActivity.kt               # Entrypoint & Compose window root
├── audio/
│   ├── CombatNarrator.kt        # Multi-voice TTS & screenless accessibility engine
│   └── SpeechManager.kt         # On-device SpeechRecognizer wrapper
├── engine/
│   ├── ClassSpellLibrary.kt     # Class abilities, spells, and starting stats
│   ├── IntentParser.kt          # Dual-stream voice command & spell intent parser
│   ├── ResonanceEngine.kt       # 5-pillar acoustic & lexical resonance grader
│   ├── SaveManager.kt           # JSON save file serializer / deserializer
│   ├── StoryEncounters.kt       # Canonical encounters & dynamic reinforcement definitions
│   └── StoryScript.kt           # Dialogue nodes, choice hubs, and narrative scenes
├── model/
│   ├── CombatModels.kt          # ATB, party members, enemies, and spell data models
│   ├── SaveModels.kt            # Save state data classes
│   └── StoryModels.kt           # Dialogue speaker, scenes, and choice models
├── ui/
│   ├── combat/                  # Side-view battle arena, living 4-frame backgrounds, ATB HUD
│   ├── creation/                # Character creation loadout screen
│   ├── dialogue/                # 16-bit JRPG dialogue bubble & portrait overlay
│   └── story/                   # Story exploration screen & options modal
└── viewmodel/
    ├── CombatViewModel.kt       # 60 FPS ATB battle state machine & voice combat dispatcher
    └── StoryViewModel.kt        # Exploration, persistence, and dialogue state manager
```

---

## Building, Testing & Deployment

### Build & Run Tests
```bash
# Run all unit and regression tests
./gradlew test

# Assemble debug APK
./gradlew assembleDebug
```
The compiled APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`.

### Wi-Fi ADB Direct Deployment
```bash
# Verify connected wireless device
adb devices

# Install APK directly over Wi-Fi
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch the game
adb shell am start -n com.voicerpg.android/.MainActivity
```
