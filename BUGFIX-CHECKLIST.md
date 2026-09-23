# VoiceRPG Engine — Bug Fix Checklist

Portable fixes made while building Commander America that apply to the original
VoiceRPG engine game (Echoes of the Logos). Fixes live in the shared engine package
`com.voicerpg.engine.*` (CombatViewModel, the pure `combat/` package, SaveManager,
StoryViewModel, StoryChoiceMatcher), so they port directly.

Legend: [x] = already applied/fixed in this repo (Echoes of the Logos).
        [ ] = not yet applied here — see "Remaining work" at the bottom.

## A. Combat bugs

- [x] **1. AOE spells only hit one target.** AOE spells must resolve against every
      target. Per-enemy numbers, and if the chosen target dies mid-application the
      attack auto-retargets the next living enemy.
      → Fixed: `CombatViewModel.resolveDamageTargets()` resolves AOE against every
      living enemy; `applyDamageToEnemies()` computes per-target hits and auto-
      retargets (`commit 235bc75`).

- [x] **2. False "all foes defeated" victory.** Victory must be derived from the
      authoritative living-enemy list at resolution time, never a stale/cached snapshot.
      → Fixed: victory is re-checked against live `_state.value.enemies` immediately
      after each per-enemy application (`commit 422711f`, "victory clobbering").

- [x] **3. MP is never spent / no affordability gate.** Spells carry `mpCost`; casting
      checks affordability and spends MP. On insufficient MP, soft-fail with a hint to
      use a free recovery action — never silently proceed.
      → Fixed: `processIncantation()` parses first, soft-fails with "say attune" hint,
      and deducts MP only after the affordability check (`commit 2dcb16c`).

- [x] **4. Mana-recovery spells leaking into auto-pick fallback.** Recovery spells are
      excluded from ALL auto-pick spell fallbacks and from turn steering, and sorted
      LAST in every kit — garbled utterances fall back to the first real spell, never
      the recovery/breath action.
      → Fixed: `triggerAutoHeroAction()` excludes `manaRestorePct > 0`; kits are
      breath-last; `IntentParser` final fallback prefers `manaRestorePct == 0f` spells
      before any recovery spell (`commits 5c1f68d, 69adec0, 2dcb16c`).

- [x] **5. Defend/Guard is a no-op.** Guard must apply real mitigation (-50% incoming),
      set an `isGuarding` flag that expires next turn, support taunt/target override,
      and must NOT speed the guard's ATB up.
      → Fixed: -50% mitigation + taunt override + `isGuarding` flag implemented
      (`commit edfb1e0`). REMAINING NUANCE: `isGuarding` never expires at the start of
      the guard's next turn (only clears when hit) — see Remaining work.

- [x] **6. Status spells (stun/poison/haste/slow) do nothing.** Implement a real status
      system: apply with tier-gated potency/duration, advance every turn (DoT ticks,
      stun skips turn), never block — resistance reduces but keeps a minimum 1-turn
      landing.
      → Fixed: pure `combat/StatusSystem` + `StatusCatalogTable` with tier gate, per-
      turn advance, landing floor (`commits 601c095, 974caef`).

- [x] **7. Drain spells heal but never damage the enemy.** A drain must deal damage to
      the target AND heal the caster from that damage, not be coded as a plain heal.
      → Fixed: `applyDamageToEnemies()` lifesteals (`totalDamage / 2`) after damaging;
      enemy DRAIN moves heal the enemy from damage dealt.

- [x] **8. Flat deterministic damage.** Replace `basePower x multiplier` with a resolver:
      (base + resonance/level power) x affinity x variance (0.9–1.1) [x crit 1.5]
      minus target defense, floored at 1.
      → Fixed: `combat/DamageResolver.resolve()` (SPEC §2 model, `commit 601c095`).

- [x] **9. AOE damage numbers all stack at one coordinate.** Give each target its own
      floating-text slot so per-target numbers display separately.
      → Fixed: per-enemy `FloatingCombatText` at `enemyFloatSlot(enemy.id)` + live-
      measured Compose positions (`commits 4f93441, 4a4163b`).

- [x] **10. No elemental weakness/resistance.** Add an affinity/effectiveness table.
      Nothing is ever immune/blocked; worst case is a hard floor (0.8x) applied to base
      damage only — the resonance/chant multiplier is never discounted.
      → Fixed: `combat/AffinityTable` (floor 0.8, same-element 0.8, `commit 601c095`).

- [x] **11. Blanket boss phase multiplier never cleared.** Replace global multipliers
      (e.g., 3x on low HP) with scripted one-time boss phase moves unlocked at HP
      thresholds.
      → Fixed: `PhaseRule` HP-threshold phases in `data/movesets.json` + scripted one-
      time Malakor "Cords of Severance" (`commits 2920e23, 601c095`).

- [x] **12. State read-modify-write races (lost updates).** All combat-state mutations
      must use atomic `MutableStateFlow.update {}` (or equivalent CAS) — especially ATB
      ticks, summon refresh, and target updates fired from delayed coroutines.
      → Fixed: `tickAtb()` and `summonReinforcements()` use `_state.update {}`
      (`commit 2dcb16c`). REMAINING HARDENING: a few delayed-coroutine writes (floating-
      text removal, round increment, status DoT paths) still use plain
      `_state.value = ...` — main-thread confined, so cosmetic.

- [x] **13. `roundNumber` never increments.** The round counter must actually advance
      (UI was stuck on "ROUND 1").
      → Fixed: `registerTurnCompleted()` advances the round after all living combatants
      act (`commit 422711f`).

- [ ] **14. Three divergent duplicate spell tables.** Consolidate to one source-of-truth
      spell catalog loaded from data; delete the other tables (powers had drifted apart).
      → PARTIAL: combat tables were consolidated, but the **hero** kit is still defined
      twice in code — `StoryEncounters.aethelSpells` (used as default) and
      `ClassSpellLibrary.*_SPELLS` (used at runtime for the customized hero) are
      duplicate Spell literals, with `docs/combat-design/data/spells.json` as a third,
      unloaded copy. Values currently match but can drift — see Remaining work.

- [x] **15. Enemy identity via brittle string match.** Replace `subtitle.contains(...)`
      / `id == "shaman"` sniffing with explicit enemy data (family, self-element, role,
      stats, defense, xp, moveset).
      → Fixed: `combat/EnemyCodex` (generated from `data/enemies.json`) keyed by
      normalized name; subtitle sniffing remains only as a last-resort fallback moveset
      for unseen enemies (`commit 601c095`).

- [x] **16. Enemy AI targets a random living hero.** Give enemies a move-selection brain:
      weighted moves with cooldowns and target rules (single / lowest-HP / marked /
      party-AOE / self), taunt override.
      → Fixed: `combat/EnemyBrain` + `MovesetTable` target rules + guard taunt override
      (`commit 601c095`).

- [x] **17. Every enemy has exactly one attack.** Enemies need a real moveset; bosses get
      phase scripts (new moves below HP thresholds) instead of a blanket multiplier.
      → Fixed: per-archetype movesets + boss `PhaseRule` scripts (`commit 601c095`).

- [x] **18. No XP / leveling / stat growth (dead `level`/`xp` fields).** Wire up real
      progression: XP from defeated enemies, level-ups written into saved character
      stats, per-class stat growth; `level`/`xp` must actually feed combat.
      → Fixed: `combat/Progression` wired through `awardVictoryXp()`, persisted via
      `updatePartyStatsFromCombat()`, levels feed `DamageResolver` potency
      (`commit 601c095`).

## B. Engine / save & state fixes

- [x] **19. Multi-slot save slot sanitization + atomic writes.** Slot indices must be
      clamped consistently in every path (save/load/delete/summary), including a
      dedicated story-mode slot; use atomic file writes to avoid corrupt saves.
      → Fixed: `SaveManager.sanitizeSlot()` on every path, `AtomicFile` writes, and the
      dedicated `STORY_MODE_SLOT` (`commit 2323111`).

- [x] **20. Story choice/completion state not persisted.** Narrative flags, decisions,
      and completion markers must be written through on every choice and survive
      save -> reload, not just live in memory.
      → Fixed: `persistCurrentState()` runs inside `applyNodeTransition()` (every choice/
      node transition) writing `narrativeFlags`/`decisionsMade`/`defeatedEncounters`;
      `continueGame()` restores them (`commit 2323111` lineage).

- [ ] **21. Choice matcher drops completed choices.** Completed choices were filtered
      out entirely, so re-speaking them silently did nothing. Keep matching them so the
      caller can give "already completed" feedback.
      → NOT FULLY APPLIED: `StoryChoiceMatcher.matchChoice()` still drops completed
      choices from the candidate set (when other options remain), per AGENTS Rule 2
      (elimination). Result: re-speaking a completed option silently no-ops or can
      mis-match a *different* choice. The "already completed" narration in
      `StoryViewModel` is only reachable on the all-completed fallback path. Needs the
      reconcile-fix in Remaining work (eliminated from being *selectable*, still
      *matchable* for feedback).

- [x] **22. ATB tick / summon lost-update race.** Delayed-coroutine ATB tick + summon
      refresh path must not lose writes (same fix as #12, called out separately because
      it was a pre-existing bug).
      → Fixed: `tickAtb()` and `summonReinforcements()` use `MutableStateFlow.update {}`
      (`commit 2dcb16c`).

- [x] **23. Recovery action ordering.** Free recovery/breath actions belong to whoever's
      turn it is, are never spoken by other characters, and are sorted last in every kit.
      → Fixed: breath aliases are turn-local (`isBreathUtterance`), every kit is
      breath-last, and spell-granting importers re-sort breath last
      (`commits 5c1f68d, 69adec0`).

## Not included (Commander America-specific, do not port)

- Character roster, kits, and Commander America spell names (TREMENDOUS_STRIKE, etc.)
- Story/dialogue/boss content and hub-dialogue choice graph
- Political/present-day content, assets, background image sets
- "Say attune" pocket-mode voice prompts (if custom to this build)

---

## Remaining work (not yet applied — prioritized critical → cosmetic)

No outright critical bugs remain; all combat-correctness and save-persistence fixes are
in. What remains is functional-priority:

- **[P1] #21 — Completed-choice voice feedback** (story hub UX). Make
  `StoryChoiceMatcher` score against the FULL choice list (never drop completed ones),
  and have the caller (`StoryViewModel.handleStoryVoiceInput` / `selectChoice` /
  `StoryScreen`) give audible "that objective has already been completed" feedback when
  a *completed* branch is spoken — while still excluding completed branches from the
  on-screen list and from being re-entered (preserves AGENTS Rule 2: no endless loops).
  Also prevents an utterance for a completed option from mis-firing a different choice.
  Tests: extend `StoryChoiceMatcherTest` + add a hub-voice feedback test.

- **[P2] #5 — Guard expiry** (balance). Clear `isGuarding` when the guarding hero's next
  turn begins (in `beginPartyMemberTurn`), so the taunt + -50% mitigation lasts at most
  ~one round and cannot persist indefinitely while un-hit. Verify guard can never speed
  an ATB gauge (guard turns always start at full readiness, so the 0.35 reset is a
  slowdown; keep it) and that `DamageResolver.resolveEnemyStrike` still applies
  `GUARD_MULTIPLIER`. Tests: extend `DoctrineIntegrationTest`.

- **[P3] #14 — Single source-of-truth spell catalog** (drift risk). Consolidate the two
  code-level hero kit definitions (`ClassSpellLibrary` inline literals + duplicated
  `StoryEncounters.aethelSpells`/companion lists) into one shared `Spell` catalog object
  referenced by both, and re-align `docs/combat-design/data/spells.json` with it (either
  load the JSON at runtime with a code fallback, or treat JSON as regenerated docs).
  Per AGENTS Rule 6, mirror the shared catalog change in
  `/home/todd/Documents/GitHub/voice-rpg-engine` if that repo carries the duplicate
  tables.

- **[P3] #12 — Residual state-write hardening** (cosmetic). Convert the remaining
  delayed-coroutine / status-path writes in `CombatViewModel` (floating-text removals,
  `registerTurnCompleted` round increment, DoT updates, mid-battle recruit) to
  `MutableStateFlow.update {}` for uniform CAS. No behavior change expected; guarded by
  the existing unit tests.

Every fix must be verified per AGENTS Rule 5 (`./gradlew test`, `assembleDebug`, tagged
GitHub release with `app-debug.apk`, `adb install -r`), and engine-level changes synced
to the standalone engine repo per AGENTS Rule 6.