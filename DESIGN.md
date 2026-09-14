# VoiceRPG Android: Game Design & Technical Architecture

**Working Title:** *Echoes of the Logos*  
**Platform:** Android Native (Kotlin, Jetpack Compose Canvas, Android Speech API)  
**Target Architecture:** Offline-first, 100% on-device speech processing, high-performance 60 FPS retro graphics.

---

## 1. Executive Summary & Vision

*Echoes of the Logos* is a retro-inspired, high-production JRPG designed natively for Android. It marries **first-person dungeon and world exploration** (evoking classic *Wizardry*, *Etrian Odyssey*, and *Shining in the Darkness*) with **side-view tactical party combat** (in the vein of 16-bit *Final Fantasy VI* and *Chrono Trigger*). 

Every aspect of gameplay—party commands, spell incantations, tactical positioning, dungeon navigation, and dialogue choices—is commanded via natural voice.

A centerpiece innovation of the game is the **Incantation Resonance Engine**: an on-device speech grading system that evaluates not only *what* spell the player is casting, but *how uniquely and evocatively* they chant it. Chants with high thematic resonance, lexical richness, and novelty receive up to a **+20% bonus to damage/healing** and trigger dramatically enhanced **visual particle density (up to 3.8x)**, screen shake, and auditory fanfare.

---

## 2. Core Visual Presentation & Aesthetic

### High-Production Retro (16-Bit "HD-Pixel" Style)
* **Visual Identity:** Rich, handcrafted pixel art sprites combined with modern visual polish: dynamic 2D lighting, chromatic aberration flashes, screen-shake impacts, floating damage numbers, and hardware-accelerated particle spell effects (embers, frost crystals, lightning arcs).
* **The Dual-Perspective Loop:**
  1. **World & Dungeon Exploration (First-Person):**
     * Players navigate stone corridors, ancient catacombs, bustling taverns, and enchanted forests through a first-person viewport framed by an ornate retro stone/gilded bezel.
     * Minimap and compass at the top corner track step-by-step progress.
     * Hands-free voice movement:
       * *"Step forward"*, *"Turn left"*, *"Turn around"*, *"Check the iron door"*, *"Open the gilded chest"*, *"Speak with the tavern keeper"*.
  2. **Combat Encounters (Side-View Tactical Battle):**
     * Shattering screen transition into battle.
     * **Layout:**
       * **Left Flank (Enemies):** Animated monster pixel sprites with breathing cycles, hit reactions, status ailment icons, and segmented HP/barrier gauges.
       * **Right Flank (Party):** Up to 4 active party members in classic 16-bit combat stances (ready, casting, damaged, victory).
       * **Top HUD:** Turn-order initiative track (ATB / round-based timeline).
       * **Bottom HUD Console:** Live voice transcription terminal displaying the player's recognized words in glowing pixel typography with real-time Resonance Meter feedback.

---

## 3. The Incantation Resonance Engine

### Conceptual Architecture
Rather than forcing players into rigid, robotic commands ("Cast Fireball"), the game rewards creativity, roleplaying immersion, and lyrical expression while running entirely **offline** on the device.

```mermaid
flowchart TD
    A["Player Spoken Chants / Commands"] --> B["Android SpeechRecognizer (On-Device Audio Stream)"]
    
    subgraph Analysis ["Dual-Stream Intent & Resonance Analyzer"]
        B --> C["Intent Extractor (Base Spell / Skill & Target Selection)"]
        B --> D["Resonance Grading Engine (Lexical Analysis & Novelty Cache)"]
    end

    subgraph Scoring ["Multi-Factor Uniqueness Scoring (0.0 to 1.0)"]
        D --> E1["Thematic Thesaurus Match (Pyromancy, Cryomancy, Holy, Shadow, etc.)"]
        D --> E2["Lexical Richness & Cadence (Word diversity, syllables, evocative phrasing)"]
        D --> E3["Anti-Repetition Novelty Cache (Circular buffer penalizing repeated phrases)"]
        E1 & E2 & E3 --> F["Final Resonance Score: 0.0 - 1.0"]
    end

    subgraph Resolution ["Combat & Visual Impact"]
        C & F --> G["Damage / Healing Scaling (100% to 120%)"]
        C & F --> H["Particle Engine Density Scaling (1.0x to 3.8x Particles)"]
        C & F --> I["Audio FX Pitch, Bass & Reverb Amplification"]
    end
```

### Uniqueness & Resonance Scoring Algorithm (100% On-Device)
The engine processes recognized text through three discrete algorithms:

1. **Thematic Vocabulary Density (Spell Thesaurus):**
   * Each school of magic contains a curated, multi-tiered dictionary of evocative roots:
     * **Pyromancy (Fire):** *cinder, inferno, ash, blaze, ignite, scorching, incandescent, solar, phoenix, wrath, embers, consume*.
     * **Cryomancy (Ice):** *glacial, permafrost, frostbite, blizzard, crystalline, absolute zero, tundra, shards, bitter, freeze*.
     * **Electromancy (Lightning):** *tempest, thunderclap, galvanic, arc, storm, fulgur, lightning, volt, flash, strike*.
     * **Holy / Restoration:** *radiance, divine, seraph, celestial, dawn, sanctify, blessing, mend, aegis, purity*.
     * **Shadow / Rogue:** *umbra, abyss, venom, phantom, whisper, shroud, eclipse, silent, strike, hollow*.
2. **Lexical Richness & Cadence:**
   * Analyzes syllable count, adjective density, and linguistic complexity.
   * Simple blunt shouts ("Fireball goblin") score low; rhythmic, multi-clause incantations score high.
3. **Anti-Repetition Novelty Cache:**
   * A rolling circular buffer of the player's last 15 incantations is preserved in memory.
   * Repeating the exact same incantation in successive turns decays the resonance score by 50% per repetition.
   * Uttering a fresh, unrepeated formulation rewards a **Novel Chant Surge**.

### Resonance Tiers, Damage Bonus & Graphic Scaling

$$\text{Final Damage} = \text{Base Damage} \times (1.0 + [0.20 \times \text{Resonance Score}])$$

| Tier | Resonance Score | Bonus | Graphic Particle Density | Visual & Audio Effects | Example Chant |
| :--- | :---: | :---: | :---: | :--- | :--- |
| **Basic** | `0.0 - 0.2` | $+0\%$ | **1.0x** (Standard, ~40 particles) | Single retro fireball sprite, basic impact sound. | *"Fireball archer"* |
| **Adept** | `0.3 - 0.5` | $+5\% - 10\%$ | **1.8x** (~80 particles) | Trailing smoke particles, moderate fireball size, slight screen bump. | *"Burn the archer with blazing flames!"* |
| **Master** | `0.6 - 0.8` | $+11\% - 16\%$ | **2.6x** (~180 particles) | Swirling flame vortex, flying spark clusters, heavy screen shake, bass boom. | *"Spirits of the cinder, engulf the archer in an inferno!"* |
| **Legendary (Logos)** | `0.9 - 1.0` | **$+20\%$ (Max)** | **3.8x** (~320+ particles) | Full-screen chromatic aberration flash, massive firestorm vortex, erupting embers, reverberant choir SFX, and glowing golden HUD banner: **"LOGOS RESONANCE (+20%)"**. | *"O primordial flame of the solar core, descend from the heavens and reduce that wretched archer to ash!"* |

---

## 4. Party & Class Architecture

```mermaid
flowchart LR
    subgraph PlayerHero ["Main Character (Player Choice)"]
        MC["Player Hero"] --> C_Warrior["Warrior / Knight"]
        MC --> C_Mage["Black Mage / Elementalist"]
        MC --> C_Cleric["White Mage / Priest"]
        MC --> C_Rogue["Rogue / Duelist"]
    end

    subgraph Companions ["Story Companions (Distinct Lore Classes)"]
        Comp1["Sir Cedric: Exiled Templar (Paladin - Holy Aegis & Taunts)"]
        Comp2["Lyra: Grove Warden (Druid - Beast Forms & Nature Magic)"]
        Comp3["Vane: Clockwork Gunner (Artificer - Traps & Piercing Fire)"]
        Comp4["Zephyr: Shadowblade (Assassin - Stealth & Criticals)"]
    end

    PlayerHero & Companions --> BattleParty["Active 4-Hero Combat Formation"]
```

### Main Character Starting Classes
* **Knight (Warrior):** Heavy armor, tanking stances, cleaving physical strikes (*"Shield Wall"*, *"Power Slash"*, *"Challenge the front line"*).
* **Elementalist (Mage):** High-burst elemental spells (*"Ignite"*, *"Frost Spike"*, *"Chain Lightning"*).
* **Cleric (White Mage):** Healing, blessings, purification (*"Mend wounds"*, *"Bless party"*, *"Sanctuary"*).
* **Rogue (Thief):** Fast initiative, dual-wielding, poison daggers (*"Shadow Strike"*, *"Trip the beast"*, *"Steal"*).

### Companions & Lore Classes
Companions are recruited across the story and possess fixed, hand-crafted classes tied directly to their personal backstory:
* **Sir Cedric (Exiled Templar):** Holy Tank. Commands: *"Raise the Aegis"*, *"Smite the heretic"*, *"Stand between them and the mage"*.
* **Lyra (Grove Warden):** Nature Druid / Shifter. Commands: *"Call the pack"*, *"Entangle their feet in briars"*, *"Soothing rain upon the party"*.
* **Vane (Clockwork Gunner):** Steampunk Artificer. Commands: *"Deploy flash grenade"*, *"Target weak point with piercing shot"*, *"Overclock repeater"*.
* **Zephyr (Shadowblade):** Assassin. Commands: *"Vanish into the mist"*, *"Throat slice from behind"*, *"Venomous flurry"*.

---

## 5. Narrative Architecture: Chapters & Recruitment Vignettes

### The Overarching Lore: *The Silent Blight*
Magic in the realm of *Aethelgard* is drawn from the **Logos**—the primordial spoken word that brought existence into being. A creeping void known as the *Silent Blight* is spreading across the kingdom, corrupting ancient beasts into rabid husks and stripping mortals of their ability to speak. 

The player's party represents the last bastion of warriors and mages whose voices can still resonate with the Logos.

### Chapter Structure & Playable Character Vignettes
Each chapter features an overarching main story quest, environmental exploration, and a dedicated **Playable Companion Vignette** (a 10-15 minute interactive playable memory where the player controls the recruit during a pivotal moment in their past before they join the main party):

* **Prologue: The Ashwood Sanctum:**
  * Awaken in the ancient sanctum; select your Hero class; learn the basics of voice exploration and the Incantation Resonance Engine.
* **Chapter 1: The Broken Garrison:**
  * Journey to the frontier border to repel blighted war-beasts.
  * **Recruitment Vignette — *Sir Cedric's Last Stand*:** Play as Sir Cedric defending the cathedral gates against overwhelming numbers after being betrayed by his corrupt order. Once rescued, he swears his oath to your cause.
* **Chapter 2: The Sunken Mire:**
  * Traverse poisoned wetlands and sunken ruins to reach the weeping heart of the forest.
  * **Recruitment Vignette — *Lyra & the Corrupted Ancient*:** Play as Lyra desperately trying to cleanse her sacred grove and bond with her wolf companion amidst the first spread of the Blight.
* **Chapter 3: The Clockwork Citadel:**
  * Infiltrate the automated brass city of Ouros, where rogue automata protect ancient energy cores.
  * **Recruitment Vignette — *Vane's Great Clockwork Heist*:** Play as Vane sabotaging the inner vaults and deploying ingenious clockwork gadgets.

---

## 6. Technical Stack & Implementation Details

| Component | Implementation Technology | Key Details |
| :--- | :--- | :--- |
| **Platform** | Android Native (Kotlin) | Min SDK 26, Target SDK 35, Java 17. |
| **UI & Rendering** | Jetpack Compose + Compose Canvas | Declarative UI for menus/HUD; custom `Canvas` drawing loop for 60 FPS sprites & particle emitters. |
| **Speech Engine** | `android.speech.SpeechRecognizer` | 100% on-device offline recognition via `EXTRA_PREFER_OFFLINE`. Zero cloud latency (<150ms). |
| **Resonance Grader** | Kotlin Algorithmic Engine | Fast dictionary hash lookups, syllabic counter, and FIFO ring buffer for novelty checking. |
| **VFX Particle System** | Compose Canvas Particle Emitter | Emits 40 to 350+ particles per spell based on resonance multiplier with velocity, gravity, and alpha decay. |
| **Audio SFX** | Android `SoundPool` | Low-latency audio samples with runtime pitch and volume modulation. |
| **Persistence** | Android Room DB / DataStore | Game saves, party state, unlocked chapters, and custom incantation records. |

---

## 7. Development Roadmap

### Phase 1: Core Combat & Resonance Prototype (Immediate Next Step)
* Set up Gradle project with Android SDK 35 and Compose dependencies.
* Build `SpeechManager.kt` wrapping `android.speech.SpeechRecognizer` with `RECORD_AUDIO` runtime permissions.
* Implement `ResonanceGrader.kt` with Pyromancy, Cryomancy, and Electromancy thesauri, syllable counter, and novelty cache.
* Build `RetroBattleView.kt` with 16-bit side-view layout (party on right, goblin/orc sprites on left).
* Build Compose `Canvas` particle emitter demonstrating particle density scaling (Basic fireball $\rightarrow$ Logos Firestorm Vortex).

### Phase 2: First-Person Exploration Engine
* Implement grid-based dungeon crawler renderer with retro stone wall ray-casting / pseudo-3D textured planes.
* Voice navigation dispatcher (*"Forward"*, *"Turn left"*, *"Examine chest"*).
* First-person HUD with retro bezel and minimap.

### Phase 3: Party Progression & Vignettes
* Class data models, skills, and equipment inventories.
* Playable companion vignettes for Sir Cedric, Lyra, and Vane.
* Save system via Room DB.
