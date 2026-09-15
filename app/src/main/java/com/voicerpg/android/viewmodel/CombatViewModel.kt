package com.voicerpg.android.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SfxManager
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.ClassSpellLibrary
import com.voicerpg.android.engine.IntentParser
import com.voicerpg.android.engine.ResonanceEngine
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.model.BattleEnvironment
import com.voicerpg.android.model.CharacterStance
import com.voicerpg.android.model.CombatPhase
import com.voicerpg.android.model.EncounterDefinition
import com.voicerpg.android.model.PlayerCustomization
import com.voicerpg.android.model.SavedCharacterStats
import com.voicerpg.android.model.Enemy
import com.voicerpg.android.model.FloatingCombatText
import com.voicerpg.android.model.MetaCommand
import com.voicerpg.android.model.PartyMember
import com.voicerpg.android.model.ResonanceResult
import com.voicerpg.android.model.ResonanceTier
import com.voicerpg.android.model.Spell
import com.voicerpg.android.model.SpellSchool
import com.voicerpg.android.model.TargetSelection
import com.voicerpg.android.ui.vfx.ParticleEmitter
import com.voicerpg.android.ui.vfx.SpellVfxEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class CombatState(
    val phase: CombatPhase = CombatPhase.ATB_WAITING,
    val roundNumber: Int = 1,
    val party: List<PartyMember> = emptyList(),
    val enemies: List<Enemy> = emptyList(),
    val activePartyMemberId: String? = null,
    val lastResonance: ResonanceResult? = null,
    val isLogosBannerVisible: Boolean = false,
    val floatingTexts: List<FloatingCombatText> = emptyList(),
    val screenShakeOffsetX: Float = 0f,
    val screenShakeOffsetY: Float = 0f,
    val currentEnvironment: BattleEnvironment = BattleEnvironment.DUNGEON,
    val isEyesFreeMode: Boolean = false,
    val isOptionsOpen: Boolean = false
) {
    val activePartyMember: PartyMember?
        get() = party.firstOrNull { it.id == activePartyMemberId }
            ?: party.firstOrNull { it.isAlive && it.isTurnReady }
            ?: party.firstOrNull { it.isAlive }

    val activeSpells: List<Spell>
        get() = activePartyMember?.spells ?: emptyList()
}

class CombatViewModel(
    val speechManager: SpeechManager,
    val sfxManager: SfxManager = SfxManager(),
    val resonanceEngine: ResonanceEngine = ResonanceEngine(),
    val particleEmitter: ParticleEmitter = ParticleEmitter(),
    val spellVfxEngine: SpellVfxEngine = SpellVfxEngine(),
    val combatNarrator: CombatNarrator = CombatNarrator(),
    private val scopeOverride: CoroutineScope? = null
) : ViewModel() {

    private val activeScope: CoroutineScope
        get() = scopeOverride ?: viewModelScope

    // Unique Authentic Spell Sets for each Party Member (from DESIGN.md)
    private val aethelSpells = listOf(
        Spell("fireball", "Fireball", SpellSchool.PYROMANCY, basePower = 65, mpCost = 15, description = "Roaring sphere of flame", exampleChant = "Fireball archer"),
        Spell("frost_spike", "Frost Spike", SpellSchool.CRYOMANCY, basePower = 58, mpCost = 12, description = "Piercing icicle", exampleChant = "Glacial frost spike the orc!"),
        Spell("chain_lightning", "Chain Lightning", SpellSchool.ELECTROMANCY, basePower = 48, mpCost = 20, hitsAll = true, description = "Arcing lightning storm", exampleChant = "Tempest lightning strike all enemies!")
    )

    private val cedricSpells = listOf(
        Spell("holy_smite", "Holy Smite", SpellSchool.HOLY, basePower = 70, mpCost = 14, description = "Righteous celestial blow", exampleChant = "By celestial dawn, smite the heretic!"),
        Spell("lay_on_hands", "Lay on Hands", SpellSchool.HOLY, basePower = 110, mpCost = 16, isHeal = true, description = "Restorative blessing", exampleChant = "Sacred radiance mend Cedric's wounds!"),
        Spell("shield_wall", "Shield Wall", SpellSchool.PHYSICAL, basePower = 40, mpCost = 10, hitsAll = true, description = "Vanguard protection", exampleChant = "Raise the golden aegis against the horde!"),
        Spell("aegis_dawn", "Aegis of the Dawn", SpellSchool.HOLY, basePower = 150, mpCost = 25, isHeal = true, hitsAll = true, description = "Radiant invulnerability barrier", exampleChant = "By celestial dawn, raise the morning star!")
    )

    private val lyraSpells = listOf(
        Spell("soothing_rain", "Soothing Rain", SpellSchool.HOLY, basePower = 65, mpCost = 18, isHeal = true, hitsAll = true, description = "Grove restorative mist", exampleChant = "Spirits of the grove, grant soothing rain upon our party!"),
        Spell("briar_entangle", "Briar Entangle", SpellSchool.HOLY, basePower = 60, mpCost = 12, description = "Thorny vines snare the foe", exampleChant = "Thorny vines and briars ensnare that archer!"),
        Spell("verdant_cataclysm", "Verdant Cataclysm", SpellSchool.HOLY, basePower = 140, mpCost = 25, hitsAll = true, description = "Roots surge and reclaim the earth", exampleChant = "Ancient roots of the deep earth awaken!")
    )

    private val zephyrSpells = listOf(
        Spell("shadow_strike", "Shadow Strike", SpellSchool.SHADOW, basePower = 75, mpCost = 12, description = "Lethal strike from behind", exampleChant = "From the silent umbra, strike the shaman's throat!"),
        Spell("venom_flurry", "Venom Flurry", SpellSchool.SHADOW, basePower = 50, mpCost = 15, hitsAll = true, description = "Poisoned twin daggers", exampleChant = "Abyssal venom coat my blades!"),
        Spell("umbral_oblivion", "Umbral Oblivion", SpellSchool.SHADOW, basePower = 160, mpCost = 25, description = "Fourfold shadow execution", exampleChant = "From the shadow between heartbeats, cut the tether of the void!")
    )

    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<CombatState> = _state.asStateFlow()

    var currentEncounterId: String? = null
        internal set
    var isZephyrRecruitedMidBattle: Boolean = false
        internal set
    var isPhase3Triggered: Boolean = false
        internal set

    var onContinueStory: (() -> Unit)? = null

    fun setPlayerInputPhaseForTesting(heroId: String = "hero") {
        val hero = _state.value.party.firstOrNull { it.id == heroId } ?: _state.value.party.firstOrNull()
        _state.value = _state.value.copy(
            phase = CombatPhase.PLAYER_INPUT,
            activePartyMemberId = hero?.id
        )
    }

    private var atbJob: Job? = null
    private var lastActedHeroId: String? = null

    init {
        startAtbLoop()
    }

    private fun createInitialState(): CombatState {
        // Party members with Speed ratings and starting battle wear (Cedric pre-damaged)
        val initialParty = listOf(
            PartyMember("hero", "Aethel", "Elementalist", currentHp = 240, maxHp = 240, currentMp = 140, maxMp = 140, spells = aethelSpells, avatarTint = Color(0xFF90CAF9), speed = 70, atbGauge = 0.85f),
            PartyMember("cedric", "Sir Cedric", "Templar", currentHp = 310, maxHp = 420, currentMp = 80, maxMp = 80, spells = cedricSpells, avatarTint = Color(0xFFFFD54F), speed = 55, atbGauge = 0.50f)
        )

        val initialEnemies = listOf(
            Enemy("orc", "Blighted Orc", "Vanguard", currentHp = 340, maxHp = 340, baseAttack = 22, isTargeted = false, spriteTint = Color(0xFFEF5350), speed = 50, atbGauge = 0.30f),
            Enemy("archer", "Corrupted Archer", "Sniper", currentHp = 240, maxHp = 240, baseAttack = 25, isTargeted = true, spriteTint = Color(0xFFAB47BC), speed = 65, atbGauge = 0.45f),
            Enemy("shaman", "Void Shaman", "Occultist", currentHp = 280, maxHp = 280, baseAttack = 20, isTargeted = false, spriteTint = Color(0xFF5C6BC0), speed = 55, atbGauge = 0.20f)
        )

        return CombatState(
            phase = CombatPhase.ATB_WAITING,
            party = initialParty,
            enemies = initialEnemies
        )
    }

    private fun startAtbLoop() {
        atbJob?.cancel()
        atbJob = activeScope.launch {
            while (true) {
                delay(40)
                if (_state.value.phase == CombatPhase.ATB_WAITING) {
                    if (combatNarrator.isEyesFreeMode.value && combatNarrator.isSpeaking.value) {
                        continue
                    }
                    tickAtb()
                }
            }
        }
    }

    private fun tickAtb() {
        val currentParty = _state.value.party
        val currentEnemies = _state.value.enemies

        // 1. Advance gauges strictly for ALIVE combatants; dead combatants stay at 0
        val updatedParty = currentParty.map { member ->
            if (member.isAlive) {
                val advance = (member.speed / 100f) * 0.035f
                member.copy(atbGauge = (member.atbGauge + advance).coerceAtMost(1.0f))
            } else {
                member.copy(atbGauge = 0f, currentMp = 0, stance = CharacterStance.DEAD)
            }
        }

        val updatedEnemies = currentEnemies.map { enemy ->
            if (enemy.isAlive) {
                val advance = (enemy.speed / 100f) * 0.022f
                enemy.copy(atbGauge = (enemy.atbGauge + advance).coerceAtMost(1.0f))
            } else {
                enemy.copy(atbGauge = 0f)
            }
        }

        _state.value = _state.value.copy(party = updatedParty, enemies = updatedEnemies)

        // 2. Check if ANY combatant reached 100% turn readiness
        val readyHeroes = updatedParty.filter { it.isAlive && it.isTurnReady }
        val readyEnemies = updatedEnemies.filter { it.isAlive && it.isTurnReady }

        if (readyHeroes.isEmpty() && readyEnemies.isEmpty()) {
            return
        }

        // Compare highest readiness between heroes and enemies
        val topHero = readyHeroes.maxByOrNull { it.atbGauge }
        val topEnemy = readyEnemies.maxByOrNull { it.atbGauge }

        val isHeroTurn = when {
            topHero != null && topEnemy == null -> true
            topHero == null && topEnemy != null -> false
            topHero != null && topEnemy != null -> topHero.speed >= topEnemy.speed
            else -> false
        }

        if (isHeroTurn && topHero != null) {
            // HERO TURN: PAUSE EVERYTHING until player completes incantation
            val lastIdx = readyHeroes.indexOfFirst { it.id == lastActedHeroId }
            val readyHero = if (lastIdx != -1 && readyHeroes.size > 1) {
                readyHeroes[(lastIdx + 1) % readyHeroes.size]
            } else {
                topHero
            }

            _state.value = _state.value.copy(
                phase = CombatPhase.PLAYER_INPUT,
                activePartyMemberId = readyHero.id,
                party = updatedParty.map {
                    if (it.id == readyHero.id) it.copy(stance = CharacterStance.READY) else it
                }
            )
            combatNarrator.narratePlayerTurn(readyHero, updatedEnemies) {
                if (speechManager.isAutoListen.value) {
                    activeScope.launch {
                        delay(100)
                        startVoiceListening()
                    }
                }
            }
        } else if (topEnemy != null) {
            // ENEMY TURN: PAUSE EVERYTHING until enemy executes attack
            executeSingleEnemyAttack(topEnemy)
        }
    }

    private fun executeSingleEnemyAttack(enemy: Enemy) {
        activeScope.launch {
            // 1. Immediately pause everything in ENEMY_ACTIONS phase
            _state.value = _state.value.copy(
                phase = CombatPhase.ENEMY_ACTIONS,
                activePartyMemberId = null
            )
            speechManager.cancel()

            val aliveHeroes = _state.value.party.filter { it.isAlive }
            if (aliveHeroes.isEmpty()) {
                _state.value = _state.value.copy(phase = CombatPhase.BATTLE_LOST)
                combatNarrator.narrateConclusion(isVictory = false) {
                    if (speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(150)
                            startVoiceListening()
                        }
                    }
                }
                return@launch
            }

            val aliveEnemiesCount = _state.value.enemies.count { it.isAlive }
            val isSummoner = enemy.isBoss || enemy.subtitle.contains("Summoner", ignoreCase = true) ||
                    enemy.subtitle.contains("Occultist", ignoreCase = true)

            // Boss or summoner summons minions if under 70% HP or field has fewer than 3 enemies (capped at 6)
            val shouldSummon = isSummoner && aliveEnemiesCount < 4 &&
                    (enemy.hpRatio <= 0.70f || (enemy.isBoss && aliveEnemiesCount <= 1)) &&
                    Random.nextFloat() < 0.60f

            if (shouldSummon) {
                // Reset this enemy's ATB gauge to 0
                val updatedEnemies = _state.value.enemies.map {
                    if (it.id == enemy.id) it.copy(atbGauge = 0f) else it
                }
                _state.value = _state.value.copy(enemies = updatedEnemies)

                val minionName = when {
                    enemy.id.contains("broodmother") -> "Spiderling"
                    enemy.id.contains("acolyte") || enemy.id.contains("bone") -> "Restless Skeleton"
                    else -> "Blighted Minion"
                }
                val minion = StoryEncounters.createMinion(
                    idSuffix = "${System.currentTimeMillis() % 1000}",
                    name = minionName,
                    subtitle = "Minion",
                    hp = if (enemy.isBoss) 160 else 130
                )
                summonReinforcements(listOf(minion))

                if (combatNarrator.isEyesFreeMode.value) {
                    val waitStart = System.currentTimeMillis()
                    while (combatNarrator.isSpeaking.value && (System.currentTimeMillis() - waitStart < 7000L)) {
                        delay(50)
                    }
                    delay(300)
                } else {
                    delay(700)
                }
                checkAndTransitionNextTurn()
                return@launch
            }

            val targetHero = aliveHeroes.random()
            val damage = (enemy.baseAttack * (Random.nextFloat() * 0.25f + 0.85f)).toInt()
            val isHeroFallen = (targetHero.currentHp - damage) <= 0

            val school = if (enemy.id == "shaman") SpellSchool.SHADOW else SpellSchool.PHYSICAL
            spellVfxEngine.launch(
                startX = 780f,
                startY = 480f,
                targetX = 260f,
                targetY = 480f,
                school = school,
                isHeal = false,
                onImpact = {
                    particleEmitter.emit(
                        school = school,
                        originX = 260f,
                        originY = 480f,
                        count = 15,
                        isLogos = false
                    )
                }
            )

            delay(350)
            sfxManager.playHitImpact()

            val fct = FloatingCombatText(
                text = "-$damage",
                color = Color(0xFFFF1744),
                startX = 260f,
                startY = 440f
            )

            // Flash enemy and target hero damaged; if hero dies, zero resources and set DEAD stance
            val updatedParty = _state.value.party.map { hero ->
                if (hero.id == targetHero.id) {
                    val newHp = (hero.currentHp - damage).coerceAtLeast(0)
                    val isFallen = newHp <= 0
                    hero.copy(
                        currentHp = newHp,
                        currentMp = if (isFallen) 0 else hero.currentMp,
                        atbGauge = if (isFallen) 0f else hero.atbGauge,
                        stance = if (isFallen) CharacterStance.DEAD else CharacterStance.DAMAGED
                    )
                } else hero
            }

            // Reset this enemy's ATB gauge to 0
            val updatedEnemies = _state.value.enemies.map {
                if (it.id == enemy.id) it.copy(atbGauge = 0f) else it
            }

            _state.value = _state.value.copy(
                enemies = updatedEnemies,
                party = updatedParty,
                floatingTexts = _state.value.floatingTexts + fct
            )

            activeScope.launch {
                delay(1200)
                _state.value = _state.value.copy(
                    floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id }
                )
            }

            delay(300)

            // Restore damaged hero to READY if still alive
            _state.value = _state.value.copy(
                party = _state.value.party.map {
                    if (it.id == targetHero.id && it.isAlive) it.copy(stance = CharacterStance.READY) else it
                }
            )

            // In Eyes-Free Pocket Mode, narrate enemy attack and PAUSE until voice narration completes
            combatNarrator.narrateEnemyActionSuspend(
                enemyName = enemy.name,
                targetHeroName = targetHero.name,
                damage = damage,
                isFallen = isHeroFallen
            )

            // Check defeat
            if (_state.value.party.none { it.isAlive }) {
                _state.value = _state.value.copy(phase = CombatPhase.BATTLE_LOST)
                combatNarrator.narrateConclusion(isVictory = false) {
                    if (speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(150)
                            startVoiceListening()
                        }
                    }
                }
                return@launch
            }

            if (combatNarrator.isEyesFreeMode.value) {
                delay(300)
            } else {
                delay(250)
            }

            // Option A: Zephyr mid-battle recruitment in Chapter 8
            if (currentEncounterId == "ch8_executioner_ambush" && !isZephyrRecruitedMidBattle && _state.value.party.none { it.id == "zephyr" }) {
                recruitZephyrMidBattle()
            }

            // Turn transition: check if another combatant is already ready
            checkAndTransitionNextTurn()
        }
    }

    private fun checkAndTransitionNextTurn() {
        activeScope.launch {
            if (combatNarrator.isEyesFreeMode.value && combatNarrator.isSpeaking.value) {
                val waitStart = System.currentTimeMillis()
                while (combatNarrator.isSpeaking.value && (System.currentTimeMillis() - waitStart < 7000L)) {
                    delay(50)
                }
                delay(200)
            }
            performTurnTransition()
        }
    }

    private fun performTurnTransition() {
        val currentParty = _state.value.party
        val currentEnemies = _state.value.enemies

        // Phase 3 Death of Voice in Chapter 16 against Grand Inquisitor Malakor
        val malakor = currentEnemies.firstOrNull { it.id == "malakor" && it.isAlive }
        if (currentEncounterId == "ch16_malakor_finale" && malakor != null && malakor.currentHp <= 450 && !isPhase3Triggered) {
            triggerPhase3DeathOfVoice()
            if (combatNarrator.isEyesFreeMode.value) {
                activeScope.launch {
                    val waitStart = System.currentTimeMillis()
                    while (combatNarrator.isSpeaking.value && (System.currentTimeMillis() - waitStart < 7000L)) {
                        delay(50)
                    }
                    delay(200)
                    proceedAfterTurnCheck(currentParty, currentEnemies)
                }
                return
            }
        }

        proceedAfterTurnCheck(currentParty, currentEnemies)
    }

    private fun proceedAfterTurnCheck(currentParty: List<PartyMember>, currentEnemies: List<Enemy>) {
        val readyHeroes = currentParty.filter { it.isAlive && it.isTurnReady }
        val readyEnemies = currentEnemies.filter { it.isAlive && it.isTurnReady }

        if (readyHeroes.isNotEmpty()) {
            val lastIdx = readyHeroes.indexOfFirst { it.id == lastActedHeroId }
            val nextHero = if (lastIdx != -1 && readyHeroes.size > 1) {
                readyHeroes[(lastIdx + 1) % readyHeroes.size]
            } else {
                readyHeroes.first()
            }
            _state.value = _state.value.copy(
                phase = CombatPhase.PLAYER_INPUT,
                activePartyMemberId = nextHero.id,
                party = currentParty.map {
                    if (it.id == nextHero.id) it.copy(stance = CharacterStance.READY) else it
                }
            )
            combatNarrator.narratePlayerTurn(nextHero, currentEnemies) {
                if (speechManager.isAutoListen.value) {
                    activeScope.launch {
                        delay(100)
                        startVoiceListening()
                    }
                }
            }
        } else if (readyEnemies.isNotEmpty()) {
            val nextEnemy = readyEnemies.maxByOrNull { it.atbGauge } ?: readyEnemies.first()
            executeSingleEnemyAttack(nextEnemy)
        } else {
            // Nobody ready yet — resume advancing turn gauges
            _state.value = _state.value.copy(phase = CombatPhase.ATB_WAITING)
        }
    }

    fun recruitZephyrMidBattle() {
        if (isZephyrRecruitedMidBattle || _state.value.party.any { it.id == "zephyr" }) return
        isZephyrRecruitedMidBattle = true
        val zephyr = StoryEncounters.createZephyrMember()
        val updatedParty = _state.value.party + zephyr
        val fct = FloatingCombatText(
            text = "Zephyr Defects to the Fellowship!",
            color = Color(0xFFCE93D8),
            startX = 400f,
            startY = 350f,
            isCrit = true
        )
        sfxManager.playSpellCast()
        triggerScreenShake(16f)
        combatNarrator.speak(
            "Zephyr descends from the rocky canyon rim with twin daggers flashing! 'I will not cut my tongue for your silence!' Zephyr defects to the fellowship!",
            force = true
        )
        _state.value = _state.value.copy(
            party = updatedParty,
            floatingTexts = _state.value.floatingTexts + fct
        )
        activeScope.launch {
            delay(1500)
            _state.value = _state.value.copy(
                floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id }
            )
        }
    }

    fun triggerPhase3DeathOfVoice() {
        if (isPhase3Triggered) return
        isPhase3Triggered = true
        sfxManager.mute(true)
        triggerScreenShake(20f)
        val fct = FloatingCombatText(
            text = "THE DEATH OF VOICE — Total Silence",
            color = Color(0xFFEF5350),
            startX = 400f,
            startY = 350f,
            isCrit = true
        )
        combatNarrator.speak(
            "The Death of Voice! Malakor severs the cords of creation. The world plunges into total, deafening silence. All music and echoes cease. Speak the Primordial Incantation in unison to shatter the void!",
            force = true
        )
        _state.value = _state.value.copy(
            floatingTexts = _state.value.floatingTexts + fct
        )
        activeScope.launch {
            delay(2000)
            _state.value = _state.value.copy(
                floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id }
            )
        }
    }

    fun selectEnemy(enemy: Enemy) {
        _state.value = _state.value.copy(
            enemies = _state.value.enemies.map { it.copy(isTargeted = it.id == enemy.id) }
        )
    }

    fun selectPartyMember(member: PartyMember) {
        if (!member.isAlive) return
        if (_state.value.phase != CombatPhase.PLAYER_INPUT) return

        if (member.isTurnReady) {
            _state.value = _state.value.copy(
                activePartyMemberId = member.id,
                party = _state.value.party.map {
                    if (it.id == member.id) it.copy(stance = CharacterStance.READY) else it
                }
            )
        } else {
            // Visual feedback showing current ATB charge level
            val pct = (member.atbRatio * 100).toInt()
            val fct = FloatingCombatText(
                text = "${member.name} charging: $pct%",
                color = Color(0xFF80D8FF),
                startX = 260f,
                startY = 420f
            )
            _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts + fct)
            activeScope.launch {
                delay(1000)
                _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id })
            }
        }
    }

    fun cycleNextPartyMember() {
        if (_state.value.phase != CombatPhase.PLAYER_INPUT) return
        val readyHeroes = _state.value.party.filter { it.isAlive && it.isTurnReady }
        if (readyHeroes.size <= 1) {
            val fct = FloatingCombatText(
                text = "No other heroes ready!",
                color = Color(0xFFFFD54F),
                startX = 260f,
                startY = 420f
            )
            _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts + fct)
            activeScope.launch {
                delay(900)
                _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id })
            }
            return
        }
        val currentId = _state.value.activePartyMemberId
        val currentIndex = readyHeroes.indexOfFirst { it.id == currentId }
        val nextHero = if (currentIndex != -1) {
            readyHeroes[(currentIndex + 1) % readyHeroes.size]
        } else {
            readyHeroes.first()
        }
        _state.value = _state.value.copy(
            activePartyMemberId = nextHero.id,
            party = _state.value.party.map {
                if (it.id == nextHero.id) it.copy(stance = CharacterStance.READY) else it
            }
        )
    }

    fun defendActivePartyMember() {
        val activeHero = _state.value.activePartyMember ?: return
        if (_state.value.phase != CombatPhase.PLAYER_INPUT) return

        activeScope.launch {
            speechManager.cancel()
            val fct = FloatingCombatText(
                text = "${activeHero.name} Guards! (+Def)",
                color = Color(0xFF64B5F6),
                startX = 260f,
                startY = 420f
            )
            lastActedHeroId = activeHero.id
            _state.value = _state.value.copy(
                party = _state.value.party.map {
                    if (it.id == activeHero.id) it.copy(atbGauge = 0.35f, stance = CharacterStance.READY) else it
                },
                activePartyMemberId = null,
                floatingTexts = _state.value.floatingTexts + fct
            )
            activeScope.launch {
                delay(1000)
                _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id })
            }
            if (combatNarrator.isEyesFreeMode.value) {
                combatNarrator.speakSuspend("${activeHero.name} braces and defends!", force = true)
                delay(200)
            } else {
                delay(300)
            }
            checkAndTransitionNextTurn()
        }
    }

    fun startVoiceListening() {
        speechManager.startListening { utterance ->
            processIncantation(utterance)
        }
    }

    fun stopVoiceListening() {
        speechManager.stopListening()
    }

    fun submitTypedChant(text: String, forcedAcoustic: com.voicerpg.android.model.AcousticProfile? = null) {
        speechManager.cancel()
        processIncantation(text, forcedAcoustic)
    }

    fun toggleEyesFreeMode(): Boolean {
        val enabled = combatNarrator.toggleEyesFreeMode()
        _state.value = _state.value.copy(isEyesFreeMode = enabled)
        val text = if (enabled) "Eyes-free pocket mode enabled." else "Eyes-free pocket mode disabled."
        combatNarrator.speak(text, force = true) {
            if (_state.value.phase == CombatPhase.PLAYER_INPUT && speechManager.isAutoListen.value) {
                activeScope.launch {
                    delay(100)
                    startVoiceListening()
                }
            }
        }
        return enabled
    }

    fun setEyesFreeMode(enabled: Boolean) {
        combatNarrator.setEyesFreeMode(enabled)
        _state.value = _state.value.copy(isEyesFreeMode = enabled)
    }

    fun openOptions() {
        _state.value = _state.value.copy(isOptionsOpen = true)
        combatNarrator.speak("Options open. Say Pocket Mode, Auto Listen, Help, or Close Options.", force = _state.value.isEyesFreeMode) {
            if (speechManager.isAutoListen.value) {
                activeScope.launch {
                    delay(100)
                    startVoiceListening()
                }
            }
        }
    }

    fun closeOptions() {
        _state.value = _state.value.copy(isOptionsOpen = false)
        combatNarrator.speak("Resuming battle.", force = _state.value.isEyesFreeMode) {
            if (_state.value.phase == CombatPhase.PLAYER_INPUT && speechManager.isAutoListen.value) {
                activeScope.launch {
                    delay(100)
                    startVoiceListening()
                }
            }
        }
    }

    fun handleMetaCommand(command: MetaCommand) {
        when (command) {
            MetaCommand.STATUS_REPORT -> {
                combatNarrator.narrateStatus(_state.value.party, _state.value.enemies) {
                    if (_state.value.phase == CombatPhase.PLAYER_INPUT && speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(100)
                            startVoiceListening()
                        }
                    }
                }
            }
            MetaCommand.CHECK_ENEMIES -> {
                combatNarrator.narrateEnemies(_state.value.enemies) {
                    if (_state.value.phase == CombatPhase.PLAYER_INPUT && speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(100)
                            startVoiceListening()
                        }
                    }
                }
            }
            MetaCommand.CHECK_PARTY -> {
                combatNarrator.narrateParty(_state.value.party) {
                    if (_state.value.phase == CombatPhase.PLAYER_INPUT && speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(100)
                            startVoiceListening()
                        }
                    }
                }
            }
            MetaCommand.TOGGLE_EYES_FREE -> {
                val enabled = toggleEyesFreeMode()
                val fct = FloatingCombatText(
                    text = if (enabled) "Eyes-Free Mode: ON 🎧" else "Eyes-Free Mode: OFF 📱",
                    color = Color(0xFF64B5F6),
                    startX = 500f,
                    startY = 400f
                )
                _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts + fct)
                activeScope.launch {
                    delay(1500)
                    _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id })
                }
            }
            MetaCommand.TOGGLE_AUTO_LISTEN -> {
                speechManager.toggleAutoListen()
                val enabled = speechManager.isAutoListen.value
                val status = if (enabled) "Auto listen enabled." else "Auto listen disabled."
                combatNarrator.speak(status, force = true) {
                    if (enabled && _state.value.phase == CombatPhase.PLAYER_INPUT) {
                        activeScope.launch {
                            delay(100)
                            startVoiceListening()
                        }
                    }
                }
                val fct = FloatingCombatText(
                    text = if (enabled) "Auto-Listen: ON 👂" else "Auto-Listen: OFF 🔇",
                    color = Color(0xFF81C784),
                    startX = 500f,
                    startY = 400f
                )
                _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts + fct)
                activeScope.launch {
                    delay(1500)
                    _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id })
                }
            }
            MetaCommand.TOGGLE_NARRATION -> {
                val enabled = combatNarrator.toggleNarration()
                val status = if (enabled) "Story narration enabled." else "Story narration muted."
                combatNarrator.speak(status, force = true) {
                    if (_state.value.phase == CombatPhase.PLAYER_INPUT && speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(100)
                            startVoiceListening()
                        }
                    }
                }
                val fct = FloatingCombatText(
                    text = if (enabled) "Narration: ON 📖" else "Narration: OFF 🔇",
                    color = Color(0xFFFFD54F),
                    startX = 500f,
                    startY = 400f
                )
                _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts + fct)
                activeScope.launch {
                    delay(1500)
                    _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id })
                }
            }
            MetaCommand.TOGGLE_READ_CHOICES -> {
                val enabled = combatNarrator.toggleReadChoices()
                val status = if (enabled) "Choice reading enabled." else "Choice reading disabled."
                combatNarrator.speak(status, force = true) {
                    if (_state.value.phase == CombatPhase.PLAYER_INPUT && speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(100)
                            startVoiceListening()
                        }
                    }
                }
                val fct = FloatingCombatText(
                    text = if (enabled) "Read Choices: ON 🔢" else "Read Choices: OFF 🔇",
                    color = Color(0xFF80D8FF),
                    startX = 500f,
                    startY = 400f
                )
                _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts + fct)
                activeScope.launch {
                    delay(1500)
                    _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id })
                }
            }
            MetaCommand.OPEN_OPTIONS -> {
                openOptions()
            }
            MetaCommand.CLOSE_OPTIONS -> {
                closeOptions()
            }
            MetaCommand.HELP -> {
                combatNarrator.narrateHelp {
                    if (_state.value.phase == CombatPhase.PLAYER_INPUT && speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(100)
                            startVoiceListening()
                        }
                    }
                }
            }
            MetaCommand.NONE -> Unit
        }
    }

    fun processIncantation(utterance: String, forcedAcoustic: com.voicerpg.android.model.AcousticProfile? = null) {
        val lower = utterance.lowercase().trim()
        if (lower.isBlank()) return

        // 0. Intercept Meta Voice Commands (Accessibility, Screenless / Pocket Mode, Status, Options)
        val peek = IntentParser.parse(utterance, emptyList(), _state.value.enemies, _state.value.party)
        if (peek.metaCommand != MetaCommand.NONE) {
            handleMetaCommand(peek.metaCommand)
            return
        }

        // 0b. Intercept Battle Victory / Defeat Voice Commands
        if (_state.value.phase == CombatPhase.BATTLE_WON || _state.value.phase == CombatPhase.BATTLE_LOST) {
            val conclusionAction = com.voicerpg.android.engine.StoryChoiceMatcher.parseBattleConclusionIntent(utterance)
            when (conclusionAction) {
                com.voicerpg.android.engine.StoryChoiceMatcher.ConclusionAction.CONTINUE_STORY -> {
                    speechManager.cancel()
                    onContinueStory?.invoke()
                }
                com.voicerpg.android.engine.StoryChoiceMatcher.ConclusionAction.RESTART_BATTLE -> {
                    speechManager.cancel()
                    restartBattle()
                }
                com.voicerpg.android.engine.StoryChoiceMatcher.ConclusionAction.NONE -> {
                    if (speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(200)
                            if (_state.value.phase == CombatPhase.BATTLE_WON || _state.value.phase == CombatPhase.BATTLE_LOST) {
                                startVoiceListening()
                            }
                        }
                    }
                }
            }
            return
        }

        if (_state.value.phase != CombatPhase.PLAYER_INPUT) return

        if (lower == "defend" || lower == "guard" || lower == "pass" || lower.contains("defend")) {
            defendActivePartyMember()
            return
        }

        // Smart Hero Resolution: check if utterance invokes a specific hero or spell
        val aliveParty = _state.value.party.filter { it.isAlive }

        val heroByName = aliveParty.firstOrNull { member ->
            val matchesCustomName = member.name.isNotBlank() && lower.contains(member.name.lowercase())
            val matchesClassTitle = member.loreClass.isNotBlank() && lower.contains(member.loreClass.lowercase())
            matchesCustomName || matchesClassTitle || when (member.id) {
                "hero" -> lower.contains("aethel") || lower.contains("elementalist") || (lower.contains("mage") && !lower.contains("shaman"))
                "cedric" -> lower.contains("cedric") || lower.contains("templar") || lower.contains("paladin") || lower.contains("knight")
                "lyra" -> lower.contains("lyra") || lower.contains("warden") || lower.contains("druid")
                "zephyr" -> lower.contains("zephyr") || lower.contains("shadowblade") || lower.contains("assassin") || lower.contains("rogue")
                else -> false
            }
        }

        val heroBySpell = aliveParty.firstOrNull { member ->
            member.spells.any { spell ->
                lower.contains(spell.name.lowercase()) ||
                spell.name.lowercase().split(" ").any { word -> word.length >= 5 && lower.contains(word) }
            }
        }

        val heroBySchoolKeyword = aliveParty.firstOrNull { member ->
            when (member.id) {
                "hero" -> lower.contains("fire") || lower.contains("frost") || lower.contains("ice") || lower.contains("lightning") || lower.contains("tempest") || lower.contains("blaze") || lower.contains("primordial")
                "cedric" -> lower.contains("smite") || lower.contains("aegis") || lower.contains("shield wall") || lower.contains("lay on hands") || lower.contains("dawn") || lower.contains("morning star")
                "lyra" -> lower.contains("soothing") || lower.contains("rain") || lower.contains("briar") || lower.contains("entangle") || lower.contains("grove") || lower.contains("cataclysm") || (lower.contains("heal") && !lower.contains("cedric"))
                "zephyr" -> lower.contains("shadow strike") || lower.contains("venom") || lower.contains("flurry") || lower.contains("dagger") || lower.contains("poison") || lower.contains("oblivion")
                else -> false
            }
        }

        val activeHero = heroByName ?: heroBySpell ?: heroBySchoolKeyword ?: _state.value.activePartyMember
        if (activeHero == null) {
            if (speechManager.isAutoListen.value && _state.value.phase == CombatPhase.PLAYER_INPUT) {
                activeScope.launch {
                    delay(200)
                    startVoiceListening()
                }
            }
            return
        }

        activeScope.launch {
            speechManager.cancel()

            // 1. Enter resolving phase & update active hero
            _state.value = _state.value.copy(
                phase = CombatPhase.INCANTATION_RESOLVING,
                activePartyMemberId = activeHero.id
            )

            // 2. Parse intent using the selected hero's specific available spells
            val parsed = IntentParser.parse(utterance, activeHero.spells, _state.value.enemies, _state.value.party)
            val acoustic = forcedAcoustic ?: speechManager.getLatestAcousticProfile()
            val rawResonance = resonanceEngine.evaluate(
                utterance = utterance,
                school = parsed.spell.school,
                acousticProfile = acoustic,
                ignoreNoveltyDecay = forcedAcoustic != null
            )
            val resonance = if (isPhase3Triggered) {
                sfxManager.mute(false)
                rawResonance.copy(
                    bonusPercent = 200,
                    damageMultiplier = 3.0f,
                    tier = ResonanceTier.TRANSCENDENTAL,
                    particleCount = 120
                )
            } else {
                rawResonance
            }

            val isSuperLogos = resonance.bonusPercent >= 100
            val isTranscendental = resonance.tier == ResonanceTier.TRANSCENDENTAL

            val showBanner = resonance.bonusPercent >= 50

            _state.value = _state.value.copy(
                lastResonance = resonance,
                isLogosBannerVisible = showBanner,
                party = _state.value.party.map {
                    if (it.id == activeHero.id) it.copy(stance = CharacterStance.CASTING) else it
                }
            )

            // Audio & Feedback
            sfxManager.playSpellCast()
            if (isSuperLogos) {
                sfxManager.playLogosFanfare()
                triggerScreenShake(if (isTranscendental) 30f else 18f)
            } else if (resonance.bonusPercent >= 50) {
                triggerScreenShake(10f)
            }

            // Let the banner shine in its full glory, then dismiss it BEFORE the spell fires
            if (showBanner) {
                delay(if (isTranscendental) 1200L else 1000L)
                _state.value = _state.value.copy(isLogosBannerVisible = false)
                delay(220L) // Wait for smooth scaleOut + fadeOut exit animation
            } else {
                delay(250L)
            }

            // 3. Play VFX on Canvas - Projectile Launch & Impact (Screen is now completely clear!)
            _state.value = _state.value.copy(phase = CombatPhase.SPELL_VFX_PLAYING)

            // Spatially place particles & projectile: heal over party (left), attack over monsters (right)
            val isHeal = parsed.spell.isHeal
            val startX = 260f
            val startY = 480f
            val targetOriginX = if (isHeal) 260f else 780f
            val targetOriginY = if (isHeal) 440f else 480f

            spellVfxEngine.launch(
                startX = startX,
                startY = startY,
                targetX = targetOriginX,
                targetY = targetOriginY,
                school = parsed.spell.school,
                isHeal = isHeal,
                bonusPercent = resonance.bonusPercent,
                tier = resonance.tier,
                onImpact = {
                    particleEmitter.emit(
                        school = parsed.spell.school,
                        originX = targetOriginX,
                        originY = targetOriginY,
                        count = resonance.particleCount,
                        isLogos = isSuperLogos
                    )
                }
            )

            // Allow projectile to travel to target
            delay(420)

            // 4. Calculate amount
            val finalAmount = (parsed.spell.basePower * resonance.damageMultiplier).toInt()

            if (isHeal) {
                applyHealAction(activeHero, parsed.spell, parsed.target, parsed.targetHeroId, finalAmount, resonance.tier)
            } else {
                applyDamageToEnemies(activeHero, parsed.spell, parsed.target, parsed.targetEnemyId, finalAmount, resonance.tier)
            }

            if (combatNarrator.isEyesFreeMode.value) {
                delay(300)
            } else {
                delay(700)
            }

            // 5. Reset active hero's ATB gauge to 0 and record lastActedHeroId for round-robin rotation
            lastActedHeroId = activeHero.id
            val updatedParty = _state.value.party.map {
                if (it.id == activeHero.id) {
                    it.copy(stance = CharacterStance.READY, atbGauge = 0f)
                } else if (!it.isAlive) {
                    it.copy(stance = CharacterStance.DEAD, atbGauge = 0f, currentMp = 0)
                } else it
            }

            _state.value = _state.value.copy(
                isLogosBannerVisible = false,
                party = updatedParty,
                activePartyMemberId = null
            )

            // Check victory
            if (_state.value.enemies.none { it.isAlive }) {
                _state.value = _state.value.copy(phase = CombatPhase.BATTLE_WON)
                combatNarrator.narrateConclusion(isVictory = true) {
                    if (speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(150)
                            startVoiceListening()
                        }
                    }
                }
                return@launch
            }

            delay(200)

            // Strictly Turn-Based Progression: check if next combatant is ready or resume charging
            checkAndTransitionNextTurn()
        }
    }

    private suspend fun applyHealAction(
        caster: PartyMember,
        spell: Spell,
        target: TargetSelection,
        targetHeroId: String?,
        healAmount: Int,
        tier: ResonanceTier
    ) {
        sfxManager.playLogosFanfare()

        val currentParty = _state.value.party

        val targetsToHeal = when {
            spell.hitsAll || target == TargetSelection.PARTY_LOWEST -> currentParty.filter { it.isAlive }
            targetHeroId != null -> {
                val matched = currentParty.filter { it.id == targetHeroId && it.isAlive }
                if (matched.isNotEmpty()) matched else listOfNotNull(currentParty.filter { it.isAlive }.minByOrNull { it.hpRatio })
            }
            target == TargetSelection.CEDRIC -> currentParty.filter { it.id == "cedric" && it.isAlive }
            target == TargetSelection.LYRA -> currentParty.filter { it.id == "lyra" && it.isAlive }
            target == TargetSelection.ZEPHYR -> currentParty.filter { it.id == "zephyr" && it.isAlive }
            target == TargetSelection.HERO -> currentParty.filter { it.id == "hero" && it.isAlive }
            target == TargetSelection.SELF -> listOf(caster)
            else -> {
                // Heal lowest HP percentage hero
                listOfNotNull(currentParty.filter { it.isAlive }.minByOrNull { it.hpRatio })
            }
        }

        val updatedParty = currentParty.map { hero ->
            if (targetsToHeal.any { it.id == hero.id }) {
                val newHp = (hero.currentHp + healAmount).coerceAtMost(hero.maxHp)
                hero.copy(currentHp = newHp)
            } else hero
        }

        val newFloatingTexts = targetsToHeal.map {
            FloatingCombatText(
                text = "+$healAmount HP",
                color = Color(0xFF00E676),
                startX = 260f,
                startY = 400f,
                isHeal = true,
                isCrit = tier == ResonanceTier.TRANSCENDENTAL || tier == ResonanceTier.MYTHIC
            )
        }

        _state.value = _state.value.copy(
            party = updatedParty,
            floatingTexts = _state.value.floatingTexts + newFloatingTexts
        )

        val targetDesc = if (spell.hitsAll) "the fellowship" else targetsToHeal.joinToString(", ") { it.name }
        combatNarrator.narrateSpellCastSuspend(
            heroName = caster.name,
            spellName = spell.name,
            targetName = targetDesc,
            amount = healAmount,
            isHeal = true,
            tierTitle = tier.title,
            isDefeated = false
        )

        activeScope.launch {
            delay(1200)
            val idsToRemove = newFloatingTexts.map { it.id }.toSet()
            _state.value = _state.value.copy(
                floatingTexts = _state.value.floatingTexts.filterNot { it.id in idsToRemove }
            )
        }
    }

    private suspend fun applyDamageToEnemies(
        caster: PartyMember,
        spell: Spell,
        target: TargetSelection,
        targetEnemyId: String?,
        damage: Int,
        tier: ResonanceTier
    ) {
        val currentEnemies = _state.value.enemies

        val targetList = when {
            target == TargetSelection.ALL_ENEMIES -> currentEnemies.filter { it.isAlive }
            targetEnemyId != null -> {
                val matched = currentEnemies.filter { it.id == targetEnemyId && it.isAlive }
                if (matched.isNotEmpty()) matched
                else {
                    val marked = currentEnemies.firstOrNull { it.isTargeted && it.isAlive }
                    if (marked != null) listOf(marked)
                    else listOfNotNull(currentEnemies.firstOrNull { it.isAlive })
                }
            }
            target == TargetSelection.ORC -> currentEnemies.filter { it.id == "orc" && it.isAlive }
            target == TargetSelection.ARCHER -> currentEnemies.filter { it.id == "archer" && it.isAlive }
            target == TargetSelection.SHAMAN -> currentEnemies.filter { it.id == "shaman" && it.isAlive }
            else -> {
                val marked = currentEnemies.firstOrNull { it.isTargeted && it.isAlive }
                if (marked != null) listOf(marked)
                else listOfNotNull(currentEnemies.firstOrNull { it.isAlive })
            }
        }

        sfxManager.playHitImpact()

        val updatedEnemies = currentEnemies.map { enemy ->
            if (targetList.any { it.id == enemy.id }) {
                val newHp = (enemy.currentHp - damage).coerceAtLeast(0)
                val isDefeated = newHp <= 0
                enemy.copy(
                    currentHp = newHp,
                    atbGauge = if (isDefeated) 0f else enemy.atbGauge,
                    isDamagedFlash = true
                )
            } else enemy
        }

        val anyDefeated = updatedEnemies.any { targetList.any { t -> t.id == it.id } && !it.isAlive }
        val enemyTargetDesc = if (target == TargetSelection.ALL_ENEMIES) "all enemies" else targetList.joinToString(", ") { it.name }
        combatNarrator.narrateSpellCastSuspend(
            heroName = caster.name,
            spellName = spell.name,
            targetName = enemyTargetDesc,
            amount = damage,
            isHeal = false,
            tierTitle = tier.title,
            isDefeated = anyDefeated
        )

        // If targeted enemy was defeated, auto-retarget the next living enemy
        val hasDeadTarget = updatedEnemies.any { it.isTargeted && !it.isAlive }
        val finalEnemies = if (hasDeadTarget) {
            val nextAlive = updatedEnemies.firstOrNull { it.isAlive }
            updatedEnemies.map { it.copy(isTargeted = it.id == nextAlive?.id) }
        } else updatedEnemies

        val isSuper = tier == ResonanceTier.TRANSCENDENTAL || tier == ResonanceTier.MYTHIC
        val newFloatingTexts = targetList.map {
            FloatingCombatText(
                text = "-$damage",
                color = if (isSuper) Color(0xFFFFD700) else Color(0xFFFF5252),
                startX = 780f,
                startY = 400f,
                isCrit = isSuper
            )
        }

        _state.value = _state.value.copy(
            enemies = finalEnemies,
            floatingTexts = _state.value.floatingTexts + newFloatingTexts
        )

        activeScope.launch {
            delay(1200)
            val idsToRemove = newFloatingTexts.map { it.id }.toSet()
            _state.value = _state.value.copy(
                floatingTexts = _state.value.floatingTexts.filterNot { it.id in idsToRemove }
            )
        }

        // Clear flash
        activeScope.launch {
            delay(200)
            _state.value = _state.value.copy(
                enemies = _state.value.enemies.map { it.copy(isDamagedFlash = false) }
            )
        }
    }

    private fun triggerScreenShake(magnitude: Float = 20f) {
        activeScope.launch {
            for (i in 0 until 8) {
                val dx = (Random.nextFloat() * magnitude * 2f - magnitude)
                val dy = (Random.nextFloat() * magnitude * 2f - magnitude)
                _state.value = _state.value.copy(screenShakeOffsetX = dx, screenShakeOffsetY = dy)
                delay(30)
            }
            _state.value = _state.value.copy(screenShakeOffsetX = 0f, screenShakeOffsetY = 0f)
        }
    }

    fun setEnvironment(env: BattleEnvironment) {
        _state.value = _state.value.copy(currentEnvironment = env)
    }

    /**
     * Spawns new enemies onto the battlefield mid-fight (e.g. boss summon or reinforcements wave),
     * strictly enforcing the hard maximum cap of 6 enemies simultaneously on field.
     */
    fun summonReinforcements(newEnemies: List<Enemy>): Int {
        val currentAlive = _state.value.enemies.count { it.isAlive }
        val maxAllowed = (6 - currentAlive).coerceAtLeast(0)
        val toAdd = newEnemies.take(maxAllowed).map {
            it.copy(atbGauge = 0f, isDamagedFlash = false)
        }
        if (toAdd.isEmpty()) return 0

        val updatedEnemies = _state.value.enemies + toAdd
        val announcementText = if (toAdd.size == 1) {
            "${toAdd.first().name} Joined!"
        } else {
            "Reinforcements Arrived! (+${toAdd.size})"
        }

        val fct = FloatingCombatText(
            text = announcementText,
            color = Color(0xFFFFB74D),
            startX = 780f,
            startY = 380f,
            isCrit = true
        )

        sfxManager.playSpellCast()
        triggerScreenShake(12f)
        combatNarrator.narrateReinforcements(toAdd.size, toAdd.map { it.name })

        _state.value = _state.value.copy(
            enemies = updatedEnemies,
            floatingTexts = _state.value.floatingTexts + fct
        )

        activeScope.launch {
            delay(1500)
            _state.value = _state.value.copy(
                floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id }
            )
        }

        return toAdd.size
    }

    fun spawnEnemy(enemy: Enemy): Boolean = summonReinforcements(listOf(enemy)) > 0

    var activeCustomization: PlayerCustomization = PlayerCustomization()
        private set

    fun applyPlayerCustomization(
        customization: PlayerCustomization,
        savedStats: List<SavedCharacterStats> = emptyList()
    ) {
        activeCustomization = customization
        val spells = ClassSpellLibrary.getSpellsForClass(customization.heroClass)
        val heroStats = savedStats.firstOrNull { it.id == "hero" }
        val hp = heroStats?.currentHp ?: customization.heroClass.startingHp
        val maxHp = heroStats?.maxHp ?: customization.heroClass.startingHp
        val mp = heroStats?.currentMp ?: customization.heroClass.startingMp
        val maxMp = heroStats?.maxMp ?: customization.heroClass.startingMp
        val tint = try {
            Color(android.graphics.Color.parseColor(customization.auraColor.hexColor))
        } catch (_: Exception) {
            Color(0xFF90CAF9)
        }

        val updatedHero = PartyMember(
            id = "hero",
            name = customization.name,
            loreClass = customization.heroClass.title,
            currentHp = hp,
            maxHp = maxHp,
            currentMp = mp,
            maxMp = maxMp,
            spells = spells,
            avatarTint = tint,
            speed = customization.heroClass.startingSpeed,
            atbGauge = 0.85f
        )

        val updatedParty = _state.value.party.map {
            if (it.id == "hero") updatedHero else it
        }

        _state.value = _state.value.copy(party = updatedParty)
    }

    fun startEncounter(encounter: EncounterDefinition) {
        currentEncounterId = encounter.id
        isZephyrRecruitedMidBattle = false
        isPhase3Triggered = false
        sfxManager.mute(false)

        val baseParty = encounter.initialParty ?: if (_state.value.party.isNotEmpty()) {
            _state.value.party.map {
                it.copy(
                    currentHp = it.maxHp,
                    currentMp = it.maxMp,
                    stance = CharacterStance.READY,
                    atbGauge = (Random.nextFloat() * 0.4f + 0.3f)
                )
            }
        } else {
            StoryEncounters.createDuoParty()
        }

        // Apply active player customization (custom name, class, starter spells, aura color) to the hero
        val partyToUse = baseParty.map { member ->
            if (member.id == "hero") {
                val spells = ClassSpellLibrary.getSpellsForClass(activeCustomization.heroClass)
                val tint = try {
                    Color(android.graphics.Color.parseColor(activeCustomization.auraColor.hexColor))
                } catch (_: Exception) {
                    member.avatarTint
                }
                member.copy(
                    name = activeCustomization.name,
                    loreClass = activeCustomization.heroClass.title,
                    spells = spells,
                    avatarTint = tint,
                    speed = activeCustomization.heroClass.startingSpeed
                )
            } else member
        }

        startEncounter(partyToUse, encounter.enemies, encounter.environment)
    }

    fun startEncounter(
        party: List<PartyMember>,
        enemies: List<Enemy>,
        environment: BattleEnvironment
    ) {
        atbJob?.cancel()
        speechManager.cancel()
        particleEmitter.clear()
        spellVfxEngine.projectiles.clear()
        resonanceEngine.noveltyCache.clear()

        _state.value = CombatState(
            phase = CombatPhase.ATB_WAITING,
            roundNumber = 1,
            party = party,
            enemies = enemies.take(6),
            currentEnvironment = environment
        )
        startAtbLoop()
    }

    fun restartBattle() {
        val currentEnv = _state.value.currentEnvironment
        _state.value = createInitialState().copy(currentEnvironment = currentEnv)
        particleEmitter.clear()
        spellVfxEngine.projectiles.clear()
        resonanceEngine.noveltyCache.clear()
        startAtbLoop()
    }

    fun cleanup() {
        atbJob?.cancel()
        speechManager.destroy()
        combatNarrator.destroy()
    }

    override fun onCleared() {
        super.onCleared()
        cleanup()
    }
}
