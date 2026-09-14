# VoiceRPG Android: Echoes of the Logos

An immersive, voice-controlled retro JRPG designed natively for Android. All gameplay—incantations, tactical party combat, first-person dungeon exploration, and companion dialogue—is driven by natural voice commands.

> [!NOTE]
> For the complete, detailed game design and technical architecture specification, see [DESIGN.md](DESIGN.md).

---

## Key Features

1. **Dual-Perspective Retro Aesthetic (16-Bit HD-Pixel):**
   * **Exploration:** First-person retro dungeon crawler (in the vein of *Wizardry* and *Shining in the Darkness*) with step-by-step navigation, secret walls, and puzzle interactions.
   * **Combat:** Side-view tactical battle view (in the style of classic *Final Fantasy VI* / *Chrono Trigger*) with animated monster sprites on the left flank and your 4-hero party on the right.

2. **On-Device Incantation Resonance Engine:**
   * 100% offline, low-latency speech recognition via `android.speech.SpeechRecognizer`.
   * **Uniqueness & Novelty Grading:** The engine evaluates not only *which* spell the player casts, but *how uniquely and evocatively* it was chanted.
   * **Damage & Healing Bonus:** Ranging from normal ($+0\%$) up to **$+20\%$ maximum bonus** for high-resonance chants.
   * **Dynamic Particle Density Scaling:** Particle counts scale from **$1.0\times$ (basic ~40 particles)** up to **$3.8\times$ (320+ particle firestorm vortex)** with screen shake, bass-boosted sound effects, and golden **"LOGOS RESONANCE (+20%)"** banner.

3. **Party & Vignette Storytelling:**
   * Choose your Main Character's starting class (*Knight, Elementalist, Cleric, Rogue*).
   * Recruit story companions with unique lore classes (*Sir Cedric the Paladin, Lyra the Druid, Vane the Artificer, Zephyr the Assassin*).
   * Each companion features a 10–15 minute **playable recruitment vignette** exploring their backstory before they join your team.

---

## Technical Stack

* **Platform:** Android Native (Kotlin, Jetpack Compose, Compose Canvas)
* **Audio & Speech:** Android `SpeechRecognizer` (on-device offline), `SoundPool`
* **Target SDK:** Android 15 (API 35), Min SDK 26
* **Persistence:** Room Database (SQLite)

---

## Project Structure

```
voice-rpg-android/
├── DESIGN.md                 # Complete game design and architecture spec
├── README.md                 # Project overview
├── gradlew / gradlew.bat     # Gradle wrapper
└── gradle/                   # Gradle wrapper distribution
```
