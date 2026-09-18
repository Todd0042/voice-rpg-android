package com.voicerpg.android.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicerpg.android.combat.AffinityTable
import com.voicerpg.android.combat.DamageInputs
import com.voicerpg.android.combat.DamageResolver
import com.voicerpg.android.combat.EnemyBrain
import com.voicerpg.android.combat.EnemyCodex
import com.voicerpg.android.combat.EnemyFamily
import com.voicerpg.android.combat.EnemyMove
import com.voicerpg.android.combat.Moveset
import com.voicerpg.android.combat.MovesetTable
import com.voicerpg.android.combat.Progression
import com.voicerpg.android.combat.ProgressMember
import com.voicerpg.android.combat.School
import com.voicerpg.android.combat.StatusId
import com.voicerpg.android.combat.StatusInstance
import com.voicerpg.android.combat.StatusSystem
import com.voicerpg.android.audio.CombatNarrator
import com.voicerpg.android.audio.SfxManager
import com.voicerpg.android.audio.SpeechManager
import com.voicerpg.android.engine.ClassSpellLibrary
import com.voicerpg.android.engine.IntentParser
import com.voicerpg.android.engine.ResonanceEngine
import com.voicerpg.android.engine.StoryEncounters
import com.voicerpg.android.model.BattleEnvironment
import com.voicerpg.android.model.CharacterStance
import com.voicerpg.android.model.CombatantFaction
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
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

    // Party spell sets - single source of truth in StoryEncounters (mirrors docs/combat-design/data/spells.json)
    private val aethelSpells = StoryEncounters.aethelSpells
    private val cedricSpells = StoryEncounters.cedricSpells
    private val lyraSpells = StoryEncounters.lyraSpells
    private val zephyrSpells = StoryEncounters.zephyrSpells

    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<CombatState> = _state.asStateFlow()

    // Debug-only developer tools visibility. Kept OUT of CombatState so that
    // encounter/restart state reconstructions can never reset it.
    private val _isDeveloperToolsEnabled = MutableStateFlow(false)
    val isDeveloperToolsEnabled: StateFlow<Boolean> = _isDeveloperToolsEnabled.asStateFlow()

    fun toggleDeveloperTools() {
        _isDeveloperToolsEnabled.value = !_isDeveloperToolsEnabled.value
    }

    var currentEncounterId: String? = null
        internal set
    var isZephyrRecruitedMidBattle: Boolean = false
        internal set
    var isPhase3Triggered: Boolean = false
        internal set

    var onContinueStory: (() -> Unit)? = null

    // Persisted progression (level/xp + damaged HP/MP carry) applied when encounters start.
    private var importedStats: List<SavedCharacterStats> = emptyList()
    fun applySavedStats(stats: List<SavedCharacterStats>) {
        importedStats = stats
    }

    private var lastVictoryOutcomes: List<com.voicerpg.android.combat.ProgressOutcome> = emptyList()
    val recentLevelUps: List<com.voicerpg.android.combat.ProgressOutcome> get() = lastVictoryOutcomes.filter { it.levelsGained > 0 }

    /** Lore-faithful combat doctrine from EnemyCodex: family, self-element, defense, xp, moveset. */
    private fun decorateEnemy(enemy: Enemy): Enemy {
        val codex = EnemyCodex.entryOf(enemy.name)
        return enemy.copy(
            family = codex?.family ?: enemy.family,
            selfElement = codex?.selfElement?.ifEmpty { null } ?: enemy.selfElement.ifEmpty { null } ?: "",
            defense = if (codex != null) maxOf(codex.defense, enemy.defense) else enemy.defense,
            xpReward = codex?.baseXp ?: enemy.xpReward,
            movesetId = codex?.moveset?.ifEmpty { null } ?: enemy.movesetId.ifEmpty { null } ?: ""
        )
    }

    private fun schoolOf(school: SpellSchool): School = School.fromNameOrNull(school.name) ?: School.PHYSICAL
    private fun familyOf(enemy: Enemy): EnemyFamily = EnemyFamily.fromNameOrNull(enemy.family) ?: EnemyFamily.FLESH

    var arenaWidth: Float = 1080f
        private set
    var arenaHeight: Float = 1400f
        private set

    private val _combatantPositions = java.util.concurrent.ConcurrentHashMap<String, Pair<Float, Float>>()

    fun updateArenaDimensions(width: Float, height: Float) {
        if (width > 0f && height > 0f) {
            arenaWidth = width
            arenaHeight = height
        }
    }

    fun updateCombatantPosition(id: String, x: Float, y: Float) {
        if (x > 0f && y > 0f) {
            _combatantPositions[id] = x to y
        }
    }

    /** Live sprite center or proportional centered fallback. */
    fun enemyFloatSlot(enemyId: String): Pair<Float, Float> {
        _combatantPositions[enemyId]?.let { return it }
        val enemies = _state.value.enemies
        val idx = enemies.indexOfFirst { it.id == enemyId }.coerceAtLeast(0)
        return if (enemies.size <= 3) {
            val total = enemies.size.coerceAtLeast(1)
            val spacing = (arenaHeight * 0.40f) / total
            val startY = (arenaHeight * 0.50f) - (total - 1) * spacing * 0.5f
            (arenaWidth * 0.78f) to (startY + idx * spacing)
        } else {
            val x = if (idx % 2 == 0) arenaWidth * 0.68f else arenaWidth * 0.84f
            val rows = (enemies.size + 1) / 2
            val rowIdx = idx / 2
            val spacing = (arenaHeight * 0.45f) / rows.coerceAtLeast(1)
            val startY = (arenaHeight * 0.50f) - (rows - 1) * spacing * 0.5f
            x to (startY + rowIdx * spacing)
        }
    }

    fun partyFloatSlot(memberId: String): Pair<Float, Float> {
        _combatantPositions[memberId]?.let { return it }
        val party = _state.value.party
        val idx = party.indexOfFirst { it.id == memberId }.coerceAtLeast(0)
        val total = party.size.coerceAtLeast(1)
        val spacing = (arenaHeight * 0.40f) / total
        val startY = (arenaHeight * 0.50f) - (total - 1) * spacing * 0.5f
        return (arenaWidth * 0.22f) to (startY + idx * spacing)
    }

    fun setPlayerInputPhaseForTesting(heroId: String = "hero") {
        val hero = _state.value.party.firstOrNull { it.id == heroId } ?: _state.value.party.firstOrNull()
        _state.value = _state.value.copy(
            phase = CombatPhase.PLAYER_INPUT,
            activePartyMemberId = hero?.id
        )
    }

    private var atbJob: Job? = null
    private var lastActedHeroId: String? = null
    var lastActedFaction: CombatantFaction = CombatantFaction.NONE
        internal set
    private var turnsTakenInRound: Int = 0

    internal fun registerTurnCompleted() {
        turnsTakenInRound++
        val aliveCount = _state.value.party.count { it.isAlive } + _state.value.enemies.count { it.isAlive }
        if (aliveCount > 0 && turnsTakenInRound >= aliveCount) {
            turnsTakenInRound = 0
            _state.value = _state.value.copy(roundNumber = _state.value.roundNumber + 1)
        }
    }

    init {
        startAtbLoop()
    }

    private fun createInitialState(): CombatState {
        // Party members with Speed ratings and starting battle wear (Cedric pre-damaged)
        val initialParty = listOf(
            PartyMember("hero", "Aethel", "Elementalist", currentHp = 240, maxHp = 240, currentMp = 140, maxMp = 140, spells = aethelSpells, avatarTint = Color(0xFF90CAF9), speed = 70, atbGauge = 0.45f),
            PartyMember("cedric", "Sir Cedric", "Templar", currentHp = 310, maxHp = 420, currentMp = 80, maxMp = 80, spells = StoryEncounters.cedricStarterSpells, avatarTint = Color(0xFFFFD54F), speed = 55, atbGauge = 0.30f)
        )

        val initialEnemies = listOf(
            Enemy("orc", "Blighted Orc", "Vanguard", currentHp = 340, maxHp = 340, baseAttack = 22, isTargeted = false, spriteTint = Color(0xFFEF5350), speed = 50, atbGauge = 0.35f),
            Enemy("archer", "Corrupted Archer", "Sniper", currentHp = 240, maxHp = 240, baseAttack = 25, isTargeted = true, spriteTint = Color(0xFFAB47BC), speed = 65, atbGauge = 0.45f),
            Enemy("shaman", "Void Shaman", "Occultist", currentHp = 280, maxHp = 280, baseAttack = 20, isTargeted = false, spriteTint = Color(0xFF5C6BC0), speed = 55, atbGauge = 0.30f)
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

    internal fun tickAtb() {
        // 1. Advance gauges strictly for ALIVE combatants (atomic recompute-from-current:
        // mid-tick summons/attacks can never be lost to a stale snapshot write).
        // Statuses modulate ATB speed (chill slows, root nearly halts, overload crawls, bless quickens).
        val livingHeroes = _state.value.party.count { it.isAlive }
        val enemyPartyScale = when (livingHeroes) {
            4 -> 1.30f
            3 -> 1.15f
            else -> 1.0f
        }

        _state.update { prev ->
            prev.copy(
                party = prev.party.map { member ->
                    if (member.isAlive) {
                        val advance = (member.speed / 100f) * 0.035f * StatusSystem.speedMult(member.statuses)
                        member.copy(atbGauge = (member.atbGauge + advance).coerceAtMost(1.0f))
                    } else {
                        member.copy(atbGauge = 0f, currentMp = 0, stance = CharacterStance.DEAD)
                    }
                },
                enemies = prev.enemies.map { enemy ->
                    if (enemy.isAlive) {
                        val advance = (enemy.speed / 100f) * 0.035f * enemyPartyScale * StatusSystem.speedMult(enemy.statuses)
                        enemy.copy(atbGauge = (enemy.atbGauge + advance).coerceAtMost(1.0f))
                    } else {
                        enemy.copy(atbGauge = 0f)
                    }
                }
            )
        }

        val freshParty = _state.value.party
        val freshEnemies = _state.value.enemies

        // 2. Check if ANY combatant reached 100% turn readiness
        val readyHeroes = freshParty.filter { it.isAlive && it.isTurnReady }
        val readyEnemies = freshEnemies.filter { it.isAlive && it.isTurnReady }

        if (readyHeroes.isEmpty() && readyEnemies.isEmpty()) {
            return
        }

        // Compare highest readiness between heroes and enemies
        val topHero = readyHeroes.maxByOrNull { it.atbGauge }
        val topEnemy = readyEnemies.maxByOrNull { it.atbGauge }

        val isHeroTurn = when {
            topHero != null && topEnemy == null -> true
            topHero == null && topEnemy != null -> false
            topHero != null && topEnemy != null -> {
                // Interleave factions when both are ready to prevent starvation
                if (lastActedFaction == CombatantFaction.HERO) false
                else if (lastActedFaction == CombatantFaction.ENEMY) true
                else topHero.speed >= topEnemy.speed
            }
            else -> false
        }

        if (isHeroTurn && topHero != null) {
            lastActedFaction = CombatantFaction.HERO
            // HERO TURN: PAUSE EVERYTHING until player completes incantation
            val lastIdx = readyHeroes.indexOfFirst { it.id == lastActedHeroId }
            val readyHero = if (lastIdx != -1 && readyHeroes.size > 1) {
                readyHeroes[(lastIdx + 1) % readyHeroes.size]
            } else {
                topHero
            }

            // Begin-of-turn status processing (DoT ticks, stun skip). Returns true if the turn was consumed.
            if (beginPartyMemberTurn(readyHero.id)) return

            _state.value = _state.value.copy(
                phase = CombatPhase.PLAYER_INPUT,
                activePartyMemberId = readyHero.id,
                party = freshParty.map {
                    if (it.id == readyHero.id) it.copy(stance = CharacterStance.READY) else it
                }
            )
            combatNarrator.narratePlayerTurn(readyHero, freshEnemies) {
                if (speechManager.isAutoListen.value) {
                    activeScope.launch {
                        delay(100)
                        startVoiceListening()
                    }
                }
            }
        } else if (topEnemy != null) {
            lastActedFaction = CombatantFaction.ENEMY
            // ENEMY TURN: PAUSE EVERYTHING until enemy executes attack
            executeSingleEnemyAttack(topEnemy)
        }
    }

    /**
     * Begin-of-turn effects for a party member: DoT ticks, then stun skip.
     * @return true if this member's turn was consumed (skipped or they fell), false if they may act.
     */
    private fun beginPartyMemberTurn(heroId: String): Boolean {
        val member = _state.value.party.firstOrNull { it.id == heroId } ?: return false
        if (member.statuses.isEmpty()) return false

        val wasStunned = StatusSystem.isStunned(member.statuses)
        val (dot, updated) = StatusSystem.advanceTurn(member.statuses)
        var fell = false

        if (dot > 0) {
            val newHp = (member.currentHp - dot).coerceAtLeast(0)
            fell = newHp <= 0
            val (sx, sy) = partyFloatSlot(heroId)
            val fct = FloatingCombatText(text = "-$dot", color = Color(0xFF9CCC65), startX = sx, startY = sy)
            _state.value = _state.value.copy(
                party = _state.value.party.map {
                    if (it.id == heroId) it.copy(
                        currentHp = newHp,
                        statuses = updated,
                        stance = if (fell) CharacterStance.DEAD else it.stance,
                        atbGauge = if (fell) 0f else it.atbGauge
                    ) else it
                },
                floatingTexts = _state.value.floatingTexts + fct
            )
            activeScope.launch {
                delay(1200)
                _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filter { it.id != fct.id })
            }
            combatNarrator.speak("${member.name} suffers $dot damage from ongoing effects.", force = false)
        } else {
            _state.value = _state.value.copy(
                party = _state.value.party.map { if (it.id == heroId) it.copy(statuses = updated) else it }
            )
        }

        if (fell) {
            checkAndTransitionNextTurn()
            return true
        }
        if (wasStunned) {
            _state.value = _state.value.copy(
                party = _state.value.party.map { if (it.id == heroId) it.copy(atbGauge = 0f) else it },
                activePartyMemberId = null
            )
            combatNarrator.speak("${member.name} is bound and cannot act!", force = true)
            lastActedHeroId = heroId
            checkAndTransitionNextTurn()
            return true
        }
        return false
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

            // 2. Begin-of-enemy-turn status effects: DoT ticks and stun/bind skips.
            val livingSelf = _state.value.enemies.firstOrNull { it.id == enemy.id } ?: return@launch
            if (livingSelf.statuses.isNotEmpty()) {
                val wasStunned = StatusSystem.isStunned(livingSelf.statuses)
                val (dot, advanced) = StatusSystem.advanceTurn(livingSelf.statuses)
                val selfNewHp = (livingSelf.currentHp - dot).coerceAtLeast(0)
                val died = dot > 0 && selfNewHp <= 0
                if (dot > 0) {
                    val (sx, sy) = enemyFloatSlot(enemy.id)
                    val dotFct = FloatingCombatText(text = "-$dot", color = Color(0xFFFFAB40), startX = sx, startY = sy)
                    _state.value = _state.value.copy(
                        enemies = _state.value.enemies.map {
                            if (it.id == enemy.id) it.copy(currentHp = selfNewHp, atbGauge = if (died) 0f else it.atbGauge, statuses = advanced) else it
                        },
                        floatingTexts = _state.value.floatingTexts + dotFct
                    )
                    activeScope.launch {
                        delay(1200)
                        _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filterNot { f -> f.id == dotFct.id })
                    }
                    combatNarrator.speak("${enemy.name} suffers $dot damage from ongoing effects.", force = false)
                } else {
                    _state.value = _state.value.copy(
                        enemies = _state.value.enemies.map { if (it.id == enemy.id) it.copy(statuses = advanced) else it }
                    )
                }
                if (died) {
                    if (_state.value.enemies.none { it.isAlive }) {
                        concludeVictory()
                        return@launch
                    }
                    checkAndTransitionNextTurn()
                    return@launch
                }
                if (wasStunned) {
                    _state.value = _state.value.copy(
                        enemies = _state.value.enemies.map { if (it.id == enemy.id) it.copy(atbGauge = 0f) else it }
                    )
                    combatNarrator.speak("${enemy.name} is bound and cannot act!", force = true)
                    lastActedFaction = CombatantFaction.ENEMY
                    registerTurnCompleted()
                    checkAndTransitionNextTurn()
                    return@launch
                }
            }

            // 3. Choose a move from the enemy's moveset (weighted, cooldown + boss-phase gated).
            val refreshedSelf = _state.value.enemies.firstOrNull { it.id == enemy.id } ?: return@launch
            val moveset = MovesetTable.movesetFor(refreshedSelf.movesetId) ?: defaultMovesetFor(refreshedSelf)
            val phase = EnemyBrain.phaseAnnouncement(moveset, enemy.hpRatio, refreshedSelf.hpRatio)
            if (phase != null && phase.note.isNotBlank()) {
                val (px, py) = enemyFloatSlot(enemy.id)
                val phaseFct = FloatingCombatText(text = phase.note, color = Color(0xFFFFD700), startX = px, startY = py, isCrit = true)
                _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts + phaseFct)
                activeScope.launch {
                    delay(1500)
                    _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filterNot { f -> f.id == phaseFct.id })
                }
                combatNarrator.speak("${enemy.name} changes tactics! ${phase.note}", force = true)
                delay(400)
            }
            val move = EnemyBrain.pickMove(moveset, refreshedSelf.hpRatio, refreshedSelf.moveCooldowns)

            // SUMMON moves spawn codex-true minions instead of attacking.
            if (move.kind == "SUMMON") {
                val updatedEnemies = _state.value.enemies.map {
                    if (it.id == enemy.id) it.copy(atbGauge = 0f, moveCooldowns = EnemyBrain.advanceCooldowns(it.moveCooldowns, move)) else it
                }
                _state.value = _state.value.copy(enemies = updatedEnemies)
                val minions = (0 until move.summonCount.coerceAtLeast(1)).map { i ->
                    StoryEncounters.createMinion(
                        idSuffix = "${System.currentTimeMillis() % 1000}_$i",
                        name = move.summon ?: "Blighted Minion",
                        subtitle = "Minion",
                        hp = if (enemy.isBoss) 160 else 130
                    )
                }
                summonReinforcements(minions)
                if (combatNarrator.isEyesFreeMode.value) {
                    val waitStart = System.currentTimeMillis()
                    while (combatNarrator.isSpeaking.value && (System.currentTimeMillis() - waitStart < 7000L)) {
                        delay(50)
                    }
                    delay(300)
                } else {
                    delay(700)
                }
                lastActedFaction = CombatantFaction.ENEMY
                registerTurnCompleted()
                checkAndTransitionNextTurn()
                return@launch
            }

            // 4. Self-buffs deal no damage.
            if (move.kind == "BUFF") {
                _state.value = _state.value.copy(
                    enemies = _state.value.enemies.map {
                        if (it.id == enemy.id) it.copy(
                            atbGauge = 0f,
                            moveCooldowns = EnemyBrain.advanceCooldowns(it.moveCooldowns, move),
                            statuses = StatusSystem.merge(it.statuses, StatusSystem.apply(StatusId.fromNameOrNull(move.applyStatus) ?: StatusId.BLESS, "ADEPT", familyOf(it), 0f))
                        ) else it
                    }
                )
                combatNarrator.speak("${enemy.name} uses ${move.name}!", force = false)
                delay(combatDelay())
                lastActedFaction = CombatantFaction.ENEMY
                registerTurnCompleted()
                checkAndTransitionNextTurn()
                return@launch
            }

            // 5. Resolve targets (taunt from guarding members overrides the move's rule).
            val guardTaunter = aliveHeroes.firstOrNull { it.isGuarding }
            val targetList: List<PartyMember> = when {
                guardTaunter != null -> listOf(guardTaunter)
                move.kind == "AOE" || move.targetRule == "PARTY_AOE" -> aliveHeroes
                move.targetRule == "SINGLE_LOWEST_HP" -> listOfNotNull(aliveHeroes.minByOrNull { it.hpRatio })
                else -> listOf(aliveHeroes.random())
            }

            val weakenedMult = StatusSystem.attackDebuffMult(refreshedSelf.statuses)
            val moveSchool = runCatching { SpellSchool.valueOf(move.school) }.getOrDefault(SpellSchool.PHYSICAL)
            val (enemyCasterX, enemyCasterY) = enemyFloatSlot(enemy.id)

            if (targetList.size > 1) {
                // Multi-projectile AoE from enemy caster to each targeted hero
                val perHeroParticles = (15 / targetList.size).coerceAtLeast(8)
                for (targetHero in targetList) {
                    val (heroX, heroY) = partyFloatSlot(targetHero.id)
                    spellVfxEngine.launch(
                        startX = enemyCasterX,
                        startY = enemyCasterY,
                        targetX = heroX,
                        targetY = heroY,
                        school = moveSchool,
                        isHeal = false,
                        onImpact = {
                            particleEmitter.emit(
                                school = moveSchool,
                                originX = heroX,
                                originY = heroY,
                                count = perHeroParticles,
                                isLogos = false
                            )
                        }
                    )
                }
            } else {
                val targetHero = targetList.firstOrNull()
                val (heroX, heroY) = if (targetHero != null) partyFloatSlot(targetHero.id) else (250f to 350f)
                spellVfxEngine.launch(
                    startX = enemyCasterX,
                    startY = enemyCasterY,
                    targetX = heroX,
                    targetY = heroY,
                    school = moveSchool,
                    isHeal = false,
                    onImpact = {
                        particleEmitter.emit(
                            school = moveSchool,
                            originX = heroX,
                            originY = heroY,
                            count = 15,
                            isLogos = false
                        )
                    }
                )
            }

            delay(350)
            sfxManager.playHitImpact()

            val moveStatusId = StatusId.fromNameOrNull(move.applyStatus)
            val newFloats = mutableListOf<FloatingCombatText>()
            var totalDamage = 0
            var anyFallen = false
            var struckHeroName: String? = null

            val updatedParty = _state.value.party.map { hero ->
                if (targetList.any { it.id == hero.id } && hero.isAlive) {
                    val blessMitigation = 1f / StatusSystem.defenseMult(hero.statuses).coerceAtLeast(1f)
                    val damage = DamageResolver.resolveEnemyStrike(
                        enemyAttack = enemy.baseAttack,
                        movePowerMult = move.powerMult * weakenedMult,
                        defenderGuarding = hero.isGuarding,
                        defenderStatusMitigation = blessMitigation.coerceIn(0.5f, 1.25f)
                    )
                    totalDamage += damage
                    anyFallen = anyFallen || (hero.currentHp - damage) <= 0
                    struckHeroName = hero.name
                    val landedStatus = if (moveStatusId != null) StatusSystem.apply(moveStatusId, "ADEPT", EnemyFamily.FLESH, damage.toFloat()) else null
                    val (hx, hy) = partyFloatSlot(hero.id)
                    newFloats += FloatingCombatText(text = "-$damage", color = Color(0xFFFF1744), startX = hx, startY = hy)
                    val newHp = (hero.currentHp - damage).coerceAtLeast(0)
                    val isFallen = newHp <= 0
                    hero.copy(
                        currentHp = newHp,
                        currentMp = if (isFallen) 0 else hero.currentMp,
                        atbGauge = if (isFallen) 0f else hero.atbGauge,
                        stance = if (isFallen) CharacterStance.DEAD else CharacterStance.DAMAGED,
                        isGuarding = false,
                        statuses = StatusSystem.merge(hero.statuses, landedStatus)
                    )
                } else hero
            }

            // DRAIN moves convert half the damage dealt into enemy healing.
            val healedHp = if (move.kind == "DRAIN") (totalDamage / 2).coerceAtMost(refreshedSelf.maxHp - refreshedSelf.currentHp) else 0

            val updatedEnemies = _state.value.enemies.map {
                if (it.id == enemy.id) it.copy(
                    atbGauge = 0f,
                    currentHp = (it.currentHp + healedHp).coerceAtMost(it.maxHp),
                    moveCooldowns = EnemyBrain.advanceCooldowns(it.moveCooldowns, move)
                ) else it
            }

            _state.value = _state.value.copy(
                enemies = updatedEnemies,
                party = updatedParty,
                floatingTexts = _state.value.floatingTexts + newFloats
            )

            activeScope.launch {
                delay(1200)
                val idsToRemove = newFloats.map { f -> f.id }.toSet()
                _state.value = _state.value.copy(
                    floatingTexts = _state.value.floatingTexts.filterNot { f -> f.id in idsToRemove }
                )
            }

            delay(300)

            // Restore damaged hero to READY if still alive
            _state.value = _state.value.copy(
                party = _state.value.party.map {
                    if (it.isAlive && it.stance == CharacterStance.DAMAGED) it.copy(stance = CharacterStance.READY) else it
                }
            )

            // In Eyes-Free Pocket Mode, narrate enemy action and PAUSE until voice narration completes
            combatNarrator.narrateEnemyActionSuspend(
                enemyName = enemy.name,
                targetHeroName = struckHeroName ?: targetList.firstOrNull()?.name ?: "the fellowship",
                damage = totalDamage,
                isFallen = anyFallen,
                moveName = move.name,
                statusNote = moveStatusId?.let { StatusSystem.shortName(it) }
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
            lastActedFaction = CombatantFaction.ENEMY
            registerTurnCompleted()
            checkAndTransitionNextTurn()
        }
    }

    private fun combatDelay(): Long = if (combatNarrator.isEyesFreeMode.value) 300L else 500L

    /** Family/role-derived fallback when an enemy has no codex moveset (never one-note). */
    private fun defaultMovesetFor(enemy: Enemy): Moveset {
        val roleWord = when {
            enemy.subtitle.contains("Sniper", true) || enemy.subtitle.contains("Assassin", true) -> "sniper"
            enemy.subtitle.contains("Occultist", true) || enemy.subtitle.contains("Caster", true) ||
                    enemy.subtitle.contains("Summoner", true) || enemy.subtitle.contains("Mystic", true) -> "caster"
            else -> "bruiser"
        }
        val key = "${enemy.family.lowercase()}_$roleWord"
        MovesetTable.movesetFor(key)?.let { return it }
        return Moveset(
            moves = listOf(
                MovesetTable.FALLBACK_BASIC.copy(name = "Attack"),
                MovesetTable.FALLBACK_BASIC.copy(name = "Heavy Blow", kind = "HEAVY", powerMult = 1.5f, targetRule = "SINGLE_LOWEST_HP", cooldownTurns = 3, weight = 2)
            )
        )
    }

    /** Shared victory conclusion: award XP, narrate, arm the continue/listen loop. */
    private fun concludeVictory() {
        _state.value = _state.value.copy(phase = CombatPhase.BATTLE_WON)
        awardVictoryXp()
        combatNarrator.narrateConclusion(isVictory = true) {
            if (speechManager.isAutoListen.value) {
                activeScope.launch {
                    delay(150)
                    startVoiceListening()
                }
            }
        }
    }

    private fun awardVictoryXp() {
        val party = _state.value.party
        if (party.isEmpty()) return
        val totalXp = _state.value.enemies.filterNot { it.isAlive }.sumOf { it.xpReward } +
                if (_state.value.enemies.any { it.isBoss }) 300 else 0
        val roster = party.map {
            ProgressMember(
                id = it.id,
                classKey = Progression.classKey(it.id, it.loreClass),
                level = it.level,
                xp = it.xp,
                alive = it.isAlive
            )
        }
        val outcomes = Progression.awardXp(roster, totalXp)
        lastVictoryOutcomes = outcomes
        if (outcomes.isEmpty()) return
        _state.value = _state.value.copy(
            party = _state.value.party.map { hero ->
                val o = outcomes.firstOrNull { it.memberId == hero.id } ?: return@map hero
                if (o.levelsGained == 0) hero
                else hero.copy(
                    level = o.newLevel,
                    xp = o.newXp,
                    maxHp = hero.maxHp + o.dMaxHp,
                    currentHp = hero.currentHp + o.dMaxHp,
                    maxMp = hero.maxMp + o.dMaxMp,
                    currentMp = hero.currentMp + o.dMaxMp,
                    speed = hero.speed + o.dSpeed,
                    defense = hero.defense + o.dDefense
                )
            }
        )
        val levelUps = outcomes.filter { it.levelsGained > 0 }
        if (levelUps.isNotEmpty()) {
            val lines = levelUps.joinToString(" ") { "${it.memberId} ascends to level ${it.newLevel}!" }
            levelUps.forEach { o ->
                val hero = _state.value.party.firstOrNull { it.id == o.memberId }
                val (lx, ly) = partyFloatSlot(o.memberId)
                val fct = FloatingCombatText(text = "LEVEL ${hero?.level ?: o.newLevel}!", color = Color(0xFFFFD700), startX = lx, startY = ly, isCrit = true)
                _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts + fct)
                activeScope.launch {
                    delay(1600)
                    _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filterNot { f -> f.id == fct.id })
                }
            }
            combatNarrator.speak("Victory! ${lines}", force = false)
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

        val shouldEnemyAct = when {
            readyEnemies.isEmpty() -> false
            readyHeroes.isEmpty() -> true
            lastActedFaction == CombatantFaction.HERO -> true // Hero acted last; interleave to ready enemy!
            lastActedFaction == CombatantFaction.ENEMY -> false // Enemy acted last; interleave to ready hero!
            else -> {
                val maxHeroSpeed = readyHeroes.maxOfOrNull { it.speed } ?: 0
                val maxEnemySpeed = readyEnemies.maxOfOrNull { it.speed } ?: 0
                maxEnemySpeed > maxHeroSpeed
            }
        }

        if (shouldEnemyAct) {
            val nextEnemy = readyEnemies.maxByOrNull { it.atbGauge } ?: readyEnemies.first()
            lastActedFaction = CombatantFaction.ENEMY
            executeSingleEnemyAttack(nextEnemy)
        } else if (readyHeroes.isNotEmpty()) {
            val lastIdx = readyHeroes.indexOfFirst { it.id == lastActedHeroId }
            val nextHero = if (lastIdx != -1 && readyHeroes.size > 1) {
                readyHeroes[(lastIdx + 1) % readyHeroes.size]
            } else {
                readyHeroes.first()
            }
            lastActedFaction = CombatantFaction.HERO
            if (beginPartyMemberTurn(nextHero.id)) return
            val refreshedParty = _state.value.party
            _state.value = _state.value.copy(
                phase = CombatPhase.PLAYER_INPUT,
                activePartyMemberId = nextHero.id,
                party = refreshedParty.map {
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
            val (dx, dy) = partyFloatSlot(activeHero.id)
            val fct = FloatingCombatText(
                text = "${activeHero.name} Guards! (-50% incoming)",
                color = Color(0xFF64B5F6),
                startX = dx,
                startY = dy
            )
            lastActedHeroId = activeHero.id
            _state.value = _state.value.copy(
                party = _state.value.party.map {
                    if (it.id == activeHero.id) it.copy(atbGauge = 0.35f, stance = CharacterStance.READY, isGuarding = true) else it
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
            lastActedFaction = CombatantFaction.HERO
            registerTurnCompleted()
            checkAndTransitionNextTurn()
        }
    }

    fun startVoiceListening() {
        speechManager.startListening(
            onStandby = {
                if (_state.value.phase == CombatPhase.PLAYER_INPUT) {
                    if (combatNarrator.isEyesFreeMode.value) {
                        combatNarrator.speak(
                            "Standing by. Tap the screen when you are ready to command.",
                            force = true
                        )
                    }
                }
            },
            onResult = { utterance ->
                processIncantation(utterance)
            }
        )
    }

    fun resumeVoiceListening() {
        if (_state.value.phase == CombatPhase.PLAYER_INPUT) {
            if (combatNarrator.isEyesFreeMode.value) {
                val heroName = _state.value.activePartyMember?.name ?: "Hero"
                combatNarrator.speak("$heroName is ready. Speak your command.", force = true) {
                    activeScope.launch {
                        delay(100)
                        startVoiceListening()
                    }
                }
            } else {
                startVoiceListening()
            }
        } else {
            startVoiceListening()
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
        combatNarrator.speak("Options open. Say Pocket Mode, Auto Listen, Speaker Names, Help, or Close Options.", force = _state.value.isEyesFreeMode) {
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
            MetaCommand.TOGGLE_SPEAKER_ATTRIBUTION -> {
                val enabled = combatNarrator.toggleSpeakerAttribution()
                val status = if (enabled) "Speaker names will be announced." else "Speaker names hidden. Voices remain distinct."
                combatNarrator.speak(status, force = true) {
                    if (_state.value.phase == CombatPhase.PLAYER_INPUT && speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(100)
                            startVoiceListening()
                        }
                    }
                }
                val fct = FloatingCombatText(
                    text = if (enabled) "Speaker Names: ON 🗣️" else "Speaker Names: OFF 🗣️",
                    color = Color(0xFFFFB74D),
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

        // Breath/recovery utterances are SELF-actions: they belong to whoever's turn it is.
        // Never let heroBySpell or heroBySchoolKeyword hijack the turn to another member.
        val BREATH_KEYWORDS = setOf(
            "attune", "breathe", "breath", "steady", "concentrate", "center", "centre",
            "recover mana", "restore mana", "focus", "still", "hush", "deep root", "quiet lungs"
        )
        val isBreathUtterance = BREATH_KEYWORDS.any { lower.contains(it) }

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

        val heroBySpell = if (isBreathUtterance) null else aliveParty.firstOrNull { member ->
            // Breath/restoration actions never steer: they belong to whoever's turn it is.
            member.spells.any { spell ->
                spell.manaRestorePct == 0f && (
                    lower.contains(spell.name.lowercase()) ||
                    spell.name.lowercase().split(" ").any { word -> word.length >= 5 && lower.contains(word) }
                )
            }
        }

        val heroBySchoolKeyword = if (isBreathUtterance) null else aliveParty.firstOrNull { member ->
            when (member.id) {
                "hero" -> lower.contains("fire") || lower.contains("frost") || lower.contains("ice") || lower.contains("lightning") || lower.contains("tempest") || lower.contains("blaze") || lower.contains("primordial")
                "cedric" -> lower.contains("smite") || lower.contains("aegis") || lower.contains("shield wall") || lower.contains("lay on hands") || lower.contains("dawn") || lower.contains("morning star")
                "lyra" -> lower.contains("soothing") || lower.contains("rain") || lower.contains("briar") || lower.contains("entangle") || lower.contains("grove") || lower.contains("cataclysm") || lower.contains("verdant") || lower.contains("thorn") || lower.contains("vine") || lower.contains("nature") || (lower.contains("heal") && !lower.contains("cedric"))
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

            // 0-pre. Parse FIRST (so we know the spell), then enforce MP: an unaffordable chant
            // fails softly WITHOUT burning the hero's turn.
            val parsed = IntentParser.parse(utterance, activeHero.spells, _state.value.enemies, _state.value.party)
            if (parsed.spell.mpCost > activeHero.currentMp) {
                val (mx, my) = partyFloatSlot(activeHero.id)
                val fct = FloatingCombatText(
                    text = if (activeHero.spells.any { it.manaRestorePct > 0f }) "Not enough mana! (say attune)" else "Not enough mana!",
                    color = Color(0xFF80D8FF),
                    startX = mx,
                    startY = my
                )
                _state.value = _state.value.copy(
                    phase = CombatPhase.PLAYER_INPUT,
                    activePartyMemberId = activeHero.id,
                    floatingTexts = _state.value.floatingTexts + fct
                )
                activeScope.launch {
                    delay(1400)
                    _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filterNot { it.id == fct.id })
                }
                combatNarrator.speak(
                    "Not enough mana. ${activeHero.name} needs ${parsed.spell.mpCost} but holds only ${activeHero.currentMp}." +
                            if (activeHero.spells.any { it.manaRestorePct > 0f }) " Say attune to steady your breath and recover." else "",
                    force = true
                ) {
                    if (speechManager.isAutoListen.value) {
                        activeScope.launch {
                            delay(150)
                            startVoiceListening()
                        }
                    }
                }
                return@launch
            }

            // 1. Enter resolving phase & update active hero
            _state.value = _state.value.copy(
                phase = CombatPhase.INCANTATION_RESOLVING,
                activePartyMemberId = activeHero.id
            )

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

            // Spatially place particles & projectile: launch from caster slot to target slots
            val isAttune = parsed.spell.manaRestorePct > 0f
            val isHeal = parsed.spell.isHeal
            val (casterX, casterY) = partyFloatSlot(activeHero.id)

            if (isAttune) {
                // Attune restorative radiance directly over the active hero
                particleEmitter.emit(
                    school = parsed.spell.school,
                    originX = casterX,
                    originY = casterY,
                    count = 16,
                    isLogos = isSuperLogos
                )
            } else if (isHeal) {
                val healTargets = resolveHealTargets(activeHero, parsed.spell, parsed.target, parsed.targetHeroId)
                val effectiveTargets = if (healTargets.isNotEmpty()) healTargets else listOf(activeHero)
                val perHeroParticles = (resonance.particleCount / effectiveTargets.size).coerceAtLeast(8)
                for (targetHero in effectiveTargets) {
                    val (targetX, targetY) = partyFloatSlot(targetHero.id)
                    spellVfxEngine.launch(
                        startX = casterX,
                        startY = casterY,
                        targetX = targetX,
                        targetY = targetY,
                        school = parsed.spell.school,
                        isHeal = true,
                        bonusPercent = resonance.bonusPercent,
                        tier = resonance.tier,
                        onImpact = {
                            particleEmitter.emit(
                                school = parsed.spell.school,
                                originX = targetX,
                                originY = targetY,
                                count = perHeroParticles,
                                isLogos = isSuperLogos
                            )
                        }
                    )
                }
            } else {
                val damageTargets = resolveDamageTargets(parsed.spell, parsed.target, parsed.targetEnemyId)
                if (damageTargets.isEmpty()) {
                    // Fallback if no enemies alive
                    spellVfxEngine.launch(
                        startX = casterX,
                        startY = casterY,
                        targetX = 800f,
                        targetY = 350f,
                        school = parsed.spell.school,
                        isHeal = false,
                        bonusPercent = resonance.bonusPercent,
                        tier = resonance.tier,
                        onImpact = {
                            particleEmitter.emit(
                                school = parsed.spell.school,
                                originX = 800f,
                                originY = 350f,
                                count = resonance.particleCount,
                                isLogos = isSuperLogos
                            )
                        }
                    )
                } else if (damageTargets.size == 1) {
                    val targetEnemy = damageTargets.first()
                    val (targetX, targetY) = enemyFloatSlot(targetEnemy.id)
                    spellVfxEngine.launch(
                        startX = casterX,
                        startY = casterY,
                        targetX = targetX,
                        targetY = targetY,
                        school = parsed.spell.school,
                        isHeal = false,
                        bonusPercent = resonance.bonusPercent,
                        tier = resonance.tier,
                        onImpact = {
                            particleEmitter.emit(
                                school = parsed.spell.school,
                                originX = targetX,
                                originY = targetY,
                                count = resonance.particleCount,
                                isLogos = isSuperLogos
                            )
                        }
                    )
                } else {
                    // Multi-projectile AoE: one projectile aimed at each enemy in formation!
                    val perTargetParticles = (resonance.particleCount / damageTargets.size).coerceAtLeast(8)
                    for (targetEnemy in damageTargets) {
                        val (targetX, targetY) = enemyFloatSlot(targetEnemy.id)
                        spellVfxEngine.launch(
                            startX = casterX,
                            startY = casterY,
                            targetX = targetX,
                            targetY = targetY,
                            school = parsed.spell.school,
                            isHeal = false,
                            bonusPercent = resonance.bonusPercent,
                            tier = resonance.tier,
                            onImpact = {
                                particleEmitter.emit(
                                    school = parsed.spell.school,
                                    originX = targetX,
                                    originY = targetY,
                                    count = perTargetParticles,
                                    isLogos = isSuperLogos
                                )
                            }
                        )
                    }
                }
            }

            // Allow projectile(s) to travel to target
            delay(420)

            // 4. Spend mana, then resolve through the combat doctrine:
            //    affinity scales the BASE; the resonance multiplier stays untouched by any matchup.
            _state.value = _state.value.copy(
                party = _state.value.party.map {
                    if (it.id == activeHero.id) it.copy(
                        currentMp = (it.currentMp - parsed.spell.mpCost).coerceAtLeast(0),
                        isGuarding = parsed.spell.isGuard
                    ) else it
                }
            )

            if (isAttune) {
                applyAttuneAction(activeHero, parsed.spell)
            } else if (isHeal) {
                applyHealAction(activeHero, parsed.spell, parsed.target, parsed.targetHeroId, resonance)
            } else {
                applyDamageToEnemies(activeHero, parsed.spell, parsed.target, parsed.targetEnemyId, resonance)
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
                concludeVictory()
                return@launch
            }

            delay(200)

            // Strictly Turn-Based Progression: check if next combatant is ready or resume charging
            lastActedFaction = CombatantFaction.HERO
            registerTurnCompleted()
            checkAndTransitionNextTurn()
        }
    }

    /**
     * Attune: the hero's free breath discipline. Costs the turn, restores a flat
     * percentage of max MP. Deliberately resonance-INDEPENDENT: sustain can never
     * inflate the voice multiplier.
     */
    private fun applyAttuneAction(member: PartyMember, spell: Spell) {
        val restore = (member.maxMp * spell.manaRestorePct).roundToInt().coerceAtLeast(1)
        val (ax, ay) = partyFloatSlot(member.id)
        sfxManager.playSpellCast()
        val fct = FloatingCombatText(
            text = "+$restore MP",
            color = Color(0xFF4FC3F7),
            startX = ax,
            startY = ay,
            isHeal = true
        )
        _state.value = _state.value.copy(
            party = _state.value.party.map {
                if (it.id == member.id) it.copy(currentMp = (it.currentMp + restore).coerceAtMost(it.maxMp)) else it
            },
            floatingTexts = _state.value.floatingTexts + fct
        )
        activeScope.launch {
            delay(1400)
            _state.value = _state.value.copy(floatingTexts = _state.value.floatingTexts.filterNot { it.id == fct.id })
        }
        combatNarrator.speak("${member.name} breathes slow and steady; $restore mana returns.", force = false)
    }

    private fun resolveHealTargets(
        caster: PartyMember,
        spell: Spell,
        target: TargetSelection,
        targetHeroId: String?
    ): List<PartyMember> {
        val currentParty = _state.value.party
        return when {
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
    }

    private fun resolveDamageTargets(
        spell: Spell,
        target: TargetSelection,
        targetEnemyId: String?
    ): List<Enemy> {
        val currentEnemies = _state.value.enemies
        val isAoe = spell.hitsAll || target == TargetSelection.ALL_ENEMIES
        return when {
            isAoe -> currentEnemies.filter { it.isAlive }
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
    }

    private suspend fun applyHealAction(
        caster: PartyMember,
        spell: Spell,
        target: TargetSelection,
        targetHeroId: String?,
        resonance: ResonanceResult
    ) {
        sfxManager.playLogosFanfare()

        val healStatus = StatusId.fromNameOrNull(spell.status)
        val currentParty = _state.value.party
        val targetsToHeal = resolveHealTargets(caster, spell, target, targetHeroId)

        // Heals are affinity-immune: voice quality alone scales them (plus shallow level potency).
        // Party-wide heals share the potency: a big spell spread over everyone is tuned down.
        val healAmount = DamageResolver.resolveHeal(
            spellBasePower = spell.basePower.toFloat(),
            attackerLevel = caster.level,
            resonanceMultiplier = resonance.damageMultiplier,
            partyWide = targetsToHeal.size > 1
        )

        val updatedParty = currentParty.map { hero ->
            if (targetsToHeal.any { it.id == hero.id }) {
                val newHp = (hero.currentHp + healAmount).coerceAtMost(hero.maxHp)
                val blessed = if (healStatus == StatusId.BLESS || healStatus == StatusId.GUARD)
                    StatusSystem.merge(hero.statuses, StatusSystem.apply(healStatus!!, "ADEPT", EnemyFamily.FLESH, 0f))
                else hero.statuses
                hero.copy(
                    currentHp = newHp,
                    statuses = blessed,
                    isGuarding = hero.isGuarding || healStatus == StatusId.GUARD,
                    stance = if (hero.stance == CharacterStance.DEAD && newHp > 0) CharacterStance.READY else hero.stance
                )
            } else hero
        }

        val newFloatingTexts = targetsToHeal.map {
            val (hx, hy) = partyFloatSlot(it.id)
            FloatingCombatText(
                text = "+$healAmount HP",
                color = Color(0xFF00E676),
                startX = hx,
                startY = hy,
                isHeal = true,
                isCrit = resonance.tier == ResonanceTier.TRANSCENDENTAL || resonance.tier == ResonanceTier.MYTHIC
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
            tierTitle = resonance.tier.title,
            defeatedNames = emptyList()
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
        resonance: ResonanceResult
    ) {
        val currentEnemies = _state.value.enemies
        val isAoe = spell.hitsAll || target == TargetSelection.ALL_ENEMIES
        val targetList = resolveDamageTargets(spell, target, targetEnemyId)

        sfxManager.playHitImpact()

        // Per-enemy resolution: affinity lands on the base, voice multiplier untouched.
        val school = schoolOf(spell.school)
        val statusId = StatusId.fromNameOrNull(spell.status)
        val weakenedCaster = StatusSystem.isWeakened(caster.statuses)
        data class HitOutcome(val enemy: Enemy, val damage: Int, val affinity: Float, val landed: StatusInstance?)
        val hits = targetList.map { enemy ->
            val eff = AffinityTable.effectiveness(school, familyOf(enemy), School.fromNameOrNull(enemy.selfElement))
            val strike = DamageResolver.resolve(
                DamageInputs(
                    spellBasePower = spell.basePower.toFloat(),
                    attackerLevel = caster.level,
                    affinity = eff,
                    resonanceMultiplier = resonance.damageMultiplier,
                    targetDefense = enemy.defense,
                    defenseMultiplier = StatusSystem.defenseMult(enemy.statuses),
                    attackerWeakened = weakenedCaster
                )
            )
            HitOutcome(enemy, strike.damage, strike.affinity, statusId?.let { StatusSystem.apply(it, resonance.tier.name, familyOf(enemy), strike.damage.toFloat()) })
        }
        val totalDamage = hits.sumOf { it.damage }

        val updatedEnemies = currentEnemies.map { enemy ->
            hits.firstOrNull { it.enemy.id == enemy.id }?.let { hit ->
                val newHp = (enemy.currentHp - hit.damage).coerceAtLeast(0)
                enemy.copy(
                    currentHp = newHp,
                    atbGauge = if (newHp <= 0) 0f else enemy.atbGauge,
                    isDamagedFlash = true,
                    statuses = StatusSystem.merge(enemy.statuses, hit.landed)
                )
            } ?: enemy
        }

        // Lifesteal spells convert half the damage dealt into healing for the caster.
        var lifestealHealed = 0
        if (spell.lifesteal && totalDamage > 0) {
            lifestealHealed = totalDamage / 2
            _state.value = _state.value.copy(
                party = _state.value.party.map {
                    if (it.id == caster.id) it.copy(currentHp = (it.currentHp + lifestealHealed).coerceAtMost(it.maxHp)) else it
                }
            )
        }

        val defeatedNames = updatedEnemies
            .filter { e -> targetList.any { it.id == e.id } && !e.isAlive }
            .map { it.name }
        val enemyTargetDesc = if (isAoe) "all foes" else targetList.joinToString(", ") { it.name }

        // Pocket-mode narration: pick the single most informative effect line among targets.
        val effectNote: String? = hits.firstNotNullOfOrNull { hit ->
            AffinityTable.narrationLine(school, hit.enemy.name, hit.affinity)
        } ?: hits.firstNotNullOfOrNull { hit ->
            hit.landed?.let { s -> "${hit.enemy.name} is ${StatusSystem.shortName(s.status)}!" }
        }
        val fullNote = listOfNotNull(
            effectNote,
            if (lifestealHealed > 0) "${caster.name} drains $lifestealHealed health!" else null
        ).joinToString(" ").ifBlank { null }

        combatNarrator.narrateSpellCastSuspend(
            heroName = caster.name,
            spellName = spell.name,
            targetName = enemyTargetDesc,
            amount = totalDamage,
            isHeal = false,
            tierTitle = resonance.tier.title,
            defeatedNames = defeatedNames,
            effectNote = fullNote
        )

        // If targeted enemy was defeated, auto-retarget the next living enemy
        val hasDeadTarget = updatedEnemies.any { it.isTargeted && !it.isAlive }
        val finalEnemies = if (hasDeadTarget) {
            val nextAlive = updatedEnemies.firstOrNull { it.isAlive }
            updatedEnemies.map { it.copy(isTargeted = it.id == nextAlive?.id) }
        } else updatedEnemies

        val isSuper = resonance.tier == ResonanceTier.TRANSCENDENTAL || resonance.tier == ResonanceTier.MYTHIC
        val newFloatingTexts = hits.map { hit ->
            val (ex, ey) = enemyFloatSlot(hit.enemy.id)
            FloatingCombatText(
                text = "-${hit.damage}",
                color = when {
                    hit.affinity >= 1.5f -> Color(0xFFFFD700)
                    isSuper -> Color(0xFFFFC400)
                    hit.affinity < 0.95f -> Color(0xFF90A4AE)
                    else -> Color(0xFFFF5252)
                },
                startX = ex,
                startY = ey,
                isCrit = isSuper || hit.affinity >= 1.5f
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
            decorateEnemy(it).copy(atbGauge = 0f, isDamagedFlash = false)
        }
        if (toAdd.isEmpty()) return 0

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

        _state.update { prev ->
            prev.copy(
                enemies = prev.enemies + toAdd,
                floatingTexts = prev.floatingTexts + fct
            )
        }

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
            speed = heroStats?.speed ?: customization.heroClass.startingSpeed,
            atbGauge = 0.45f,
            level = heroStats?.level ?: 1,
            xp = heroStats?.xp ?: 0
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
        _combatantPositions.clear()
        sfxManager.mute(false)

        val baseParty = encounter.initialParty ?: if (_state.value.party.isNotEmpty()) {
            _state.value.party.map {
                it.copy(
                    currentHp = it.maxHp,
                    currentMp = it.maxMp,
                    stance = CharacterStance.READY,
                    atbGauge = (Random.nextFloat() * 0.3f + 0.25f)
                )
            }
        } else {
            StoryEncounters.createDuoParty()
        }

        // Apply active player customization (custom name, class, starter spells, aura color) to the hero
        val partyToUse = applyImportedProgression(baseParty.map { member ->
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
        })

        startEncounter(partyToUse, encounter.enemies.map { decorateEnemy(it) }, encounter.environment)
    }

    /** Persisted level/xp + damaged HP/MP carry into the next fight; fresh battle with clean statuses. */
    internal fun applyImportedProgression(members: List<PartyMember>): List<PartyMember> {
        return members.map { member ->
            val saved = importedStats.firstOrNull { it.id == member.id } ?: return@map member
            val revived = member.currentHp <= 0 || saved.currentHp <= 0
            val currentHp = if (revived) (saved.maxHp / 2).coerceAtLeast(1) else saved.currentHp.coerceAtMost(saved.maxHp)

            // Resolve companion spells from saved stats if available
            val updatedSpells = if (member.id != "hero" && saved.spellIds.isNotEmpty()) {
                val companionSpells = saved.spellIds.mapNotNull { StoryEncounters.ALL_COMPANION_SPELLS[it] }
                val breath = when (member.id) {
                    "cedric" -> StoryEncounters.steadyBreathSpell
                    "lyra" -> StoryEncounters.deepRootSpell
                    "zephyr" -> StoryEncounters.quietLungsSpell
                    else -> null
                }
                if (companionSpells.isNotEmpty()) {
                    (companionSpells.filter { it.manaRestorePct == 0f } + listOfNotNull(breath)).distinctBy { it.id }
                } else member.spells
            } else {
                member.spells
            }

            member.copy(
                level = saved.level,
                xp = saved.xp,
                maxHp = saved.maxHp,
                maxMp = saved.maxMp,
                currentHp = currentHp,
                currentMp = saved.currentMp.coerceAtMost(saved.maxMp),
                speed = saved.speed,
                spells = updatedSpells,
                statuses = emptyList(),
                isGuarding = false
            )
        }
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
        _combatantPositions.clear()
        turnsTakenInRound = 0
        lastActedHeroId = null
        lastActedFaction = CombatantFaction.NONE

        _state.value = CombatState(
            phase = CombatPhase.ATB_WAITING,
            roundNumber = 1,
            party = party,
            enemies = enemies.map { decorateEnemy(it).let { e -> e.copy(atbGauge = e.atbGauge.coerceAtLeast(0.25f)) } }.take(6),
            currentEnvironment = environment
        )
        startAtbLoop()
    }

    fun restartBattle() {
        val currentEnv = _state.value.currentEnvironment
        turnsTakenInRound = 0
        lastActedHeroId = null
        lastActedFaction = CombatantFaction.NONE
        _state.value = createInitialState().copy(currentEnvironment = currentEnv)
        _combatantPositions.clear()
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
