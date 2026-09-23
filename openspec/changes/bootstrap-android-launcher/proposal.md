# Proposal: Bootstrap Android Launcher Scaffold (COLS)

## Intent

COLS is a calls-only Android launcher for senior users, currently at repository zero: no Android project, no JDK/Gradle/Kotlin/Android SDK installed (`ANDROID_HOME` unset), and only SDD scaffolding present (`openspec/config.yaml`, empty `openspec/specs/`). No feature work can start until a runnable Android project exists.

This bootstrap change installs the runnable toolchain and scaffolds a minimal, verified Kotlin + Compose project with JVM-first TDD wiring, a CI gate, and the architectural seam for the future provider-agnostic AI agent — without implementing any product feature.

**This change depends on project bootstrap**: the JDK, Android SDK, Gradle wrapper generation, and a runnable build environment are prerequisites, not optional conveniences (per `openspec/config.yaml` proposal rule).

## Goals

- Runnable Android project from a clean checkout, with a documented environment.
- Minimal verifiable scaffold: Kotlin (K2), Jetpack Compose + Material 3, single `:app` module, version catalog, committed Gradle wrapper.
- JVM-first TDD loop working from day one (workspace-level test command).
- CI PR gate: lint + unit tests + assemble.
- AI agent isolated behind an interface (port) with a confirmation-before-action domain rule — no agent feature code in the bootstrap.
- SDD strict TDD re-enabled after scaffolding.

## Non-Goals

- No product features: calls, contacts, launcher UX, caregiver surfaces.
- No telephony integration: `ROLE_HOME`/`ROLE_DIALER`, `InCallService`, `ANSWER_PHONE_CALLS`, speakerphone auto-on.
- No AI/RAG code: model, API-key handling, network calls, web agents.
- No unverified platform facts: concrete versions, SDK levels, and accessibility values are asserted only after research (RQ1–RQ3) confirms them.
- No instrumented/emulator test suite in the bootstrap CI.
- No multi-module split (`:agent`, `:core:designsystem`) — deferred; extraction stays mechanical behind the seam.

## Scope

### In Scope

- **Environment setup tasks** (documented, not committed code): JDK LTS install, Android SDK (cmdline-tools, platform-tools, build-tools, platform), `ANDROID_HOME` configuration, Gradle wrapper generation, first-build sanity check.
- **Gradle scaffold**: `settings.gradle.kts`, root and `:app` build files, version catalog (`gradle/libs.versions.toml`), committed Gradle wrapper.
- **Single `:app` module**: Kotlin (K2), Jetpack Compose + Material 3, layered packages (`ui/`, `domain/`, `data/`), one smoke-test placeholder screen (no launcher features).
- **JVM-first TDD stack**: JUnit4 + Robolectric + Compose test runtime, Kover coverage, ktlint via Spotless — with one passing RED→GREEN example test.
- **CI workflow**: GitHub Actions PR gate running lint + unit tests + assemble.
- **Android `.gitignore` baseline** appended to the current file (`.atl/` preserved).
- **README dev-environment section**: JDK/SDK setup and first-build commands.
- **Re-run `sdd-init`** after scaffolding to enable strict TDD and refresh config context.
- **AI agent architectural seam**: provider-agnostic interface (port) + in-memory/stub adapter with a confirmation-before-action domain rule.

### Out of Scope

- Launcher features (home screen, app grid, call UX).
- Telephony integration and call-control APIs (deferred to a dedicated change; RQ5).
- Contacts and caregiver/configurator surfaces.
- Any AI/RAG, model, API-key, or web-agent code (RQ6 informs the deferred change).
- Multi-module split.
- Instrumented tests in CI (deferred; TS-01 defines the test pyramid, coverage gates, and acceptance-criterion verification mapping for design).
- Final accessibility baseline values — codified as testable requirements only where validated (RQ3 + caregiver input), never invented.

## Capabilities

> Contract between proposal and specs phases. `openspec/specs/` is empty (zero-state), so all capabilities below are new.

### New Capabilities

- `project-scaffold`: Gradle/Android build scaffold — committed wrapper, version catalog, single `:app` module, Kotlin (K2), Jetpack Compose + Material 3 baseline, toolchain versions pinned only after the full AGP/Gradle/JDK/Compose/compileSdk set is re-verified together (RQ1).
- `app-testing`: JVM-first test stack — JUnit4 + Robolectric + Compose test runtime, Kover coverage, ktlint/Spotless, workspace-level test command; test pyramid and coverage gates per TS-01.
- `ci-pipeline`: GitHub Actions PR gate — lint + unit tests + assemble on every PR.
- `agent-seam`: provider-agnostic AI-agent port (interface) with a confirmation-before-action domain rule and in-memory stub adapter; explicitly no model, API-key, network, or agent features.

### Modified Capabilities

None — no existing specs in `openspec/specs/`.

## Approach

Combine exploration **Approach 1 (minimal single-module scaffold)** with **Approach 3 (toolchain-first)**:

1. Phase 0 establishes the environment (JDK, SDK, wrapper) as prerequisite tasks — documented, not committed artifacts.
2. Scaffold a single `:app` module with layered packages (`ui/`, `domain/`, `data/`); the AI agent lives behind a Kotlin interface (port) with an in-memory stub adapter, so `:agent` extraction later stays mechanical.
3. Wire the JVM-first test stack and one RED→GREEN example test to prove the TDD loop.
4. Add a GitHub Actions PR gate (lint + unit tests + assemble).
5. Extend `.gitignore` with the standard Android baseline, add README setup guidance, then re-run `sdd-init` to enable strict TDD.

Version pins, minSdk/targetSdk, and accessibility requirements are filled in during research/design from RQ1–RQ3 — nothing asserted unverified in this proposal. All AGP/Gradle/JDK/Compose/compileSdk pins are re-verified as one compatible set at implementation (see research-status note below).

## Research Questions (RQ1, RQ2, RQ3, RQ5, RQ6 + TS-01)

External evidence, deferred to research/design. RQ5/RQ6 inform deferred changes, not this bootstrap's scope. TS-01 is the canonical test-strategy item — it consolidates the former duplicate RQ4/RQ7 question; downstream phases must not re-open it as a separate research item.

| ID | Question | Decides |
|----|----------|---------|
| RQ1 | Current stable JDK LTS, Android Gradle Plugin, Kotlin, Gradle, Compose BOM versions | Version-catalog pins; wrapper version |
| RQ2 | Play targetSdk policy; realistic Android versions among senior users | minSdk/targetSdk |
| RQ3 | Current Compose/Material accessibility guidance (touch-target sizes, contrast ratios, TalkBack practices) | Testable accessibility requirements |
| RQ5 | Telecom/role behavior per API level: `ROLE_HOME`, `ROLE_DIALER`, `InCallService`, `ANSWER_PHONE_CALLS`; speakerphone auto-on; emergency-call coexistence | Future telephony change scope |
| RQ6 | On-device LLM options and minimum device specs; recommended secure API-key storage on Android | Future agent implementation |
| TS-01 | Define the test pyramid and coverage gates for unit, integration, and instrumented/UI tests, and map every bootstrap acceptance criterion to a concrete verification check | Whether/when instrumented tests enter CI; coverage gates; acceptance-criterion verification mapping |

> **Research-status honesty**: the only fully retrieved toolchain compatibility row to date is AGP 9.0.0 + Gradle 9.1.0 + JDK 17 + Build Tools 36.0.0, and it is time-sensitive. A real tension exists between AGP 9.0.0's max supported API 36.1 and a Compose 1.12 guide requiring compileSdk 37. No AGP/Gradle/JDK/Compose/compileSdk pin is final — the full set MUST be re-verified together at implementation. Current Play target-API deadlines and the detailed TelecomManager/InCallService contracts remain unverified; this proposal asserts no deadlines, API levels, or role contracts.

## Phase / Delivery Outline

| Phase | Deliverable | Committed? |
|-------|-------------|------------|
| 0 — Environment | JDK LTS + Android SDK installed, `ANDROID_HOME` set, Gradle wrapper generated, first-build sanity check | No (machine state) |
| 1 — Scaffold | `settings.gradle.kts`, root/`:app` build files, version catalog, wrapper, `.gitignore` baseline, README setup | Yes |
| 2 — App + seam | `:app` Kotlin K2 + Compose + Material 3 scaffold, smoke placeholder screen, agent seam (interface + stub), layered packages | Yes |
| 3 — Testing | JUnit4 + Robolectric + Compose test runtime, Kover, Spotless/ktlint, RED→GREEN example test | Yes |
| 4 — CI | GitHub Actions PR gate (lint + unit tests + assemble) | Yes |
| 5 — SDD re-init | Re-run `sdd-init`: strict TDD enabled, config context/testing refreshed | Yes |

**Review-budget forecast**: scaffold + Gradle files + CI workflow will likely exceed the 400-line PR review budget (`400-line budget risk: Medium`, exploration). Exact slicing into reviewable work units is deferred to `sdd-tasks`; delivery strategy is `ask-on-risk`.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `settings.gradle.kts` | New | Root Gradle settings, module includes, repository config |
| `build.gradle.kts` (root) | New | Root build config (plugins DSL, Spotless) |
| `app/build.gradle.kts` | New | `:app` module: Kotlin K2, Compose, Material 3, test deps, Kover |
| `gradle/libs.versions.toml` | New | Version catalog (pins verified via RQ1) |
| `gradle/wrapper/*`, `gradlew`, `gradlew.bat` | New | Gradle wrapper (generated) |
| `app/src/**` | New | Kotlin + Compose scaffold: layered packages, smoke placeholder, agent seam (interface + stub) |
| `.github/workflows/ci.yml` | New | PR gate: lint + unit tests + assemble |
| `.gitignore` | Modified | Append standard Android baseline; keep `.atl/` |
| `README.md` | Modified | Dev-environment setup section (JDK, SDK, first build) |
| `openspec/config.yaml` | Modified | `context`/`testing` refresh via `sdd-init` re-run; strict TDD enabled |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| No toolchain installed — wrapper generation and first build blocked until JDK/SDK setup | High (verified fact) | Phase 0 environment tasks are explicit prerequisites; README documents setup; this proposal states the bootstrap dependency |
| Android platform churn — wrong/unverified AGP/Gradle/Kotlin/Compose versions | Med | RQ1/RQ2 research before design; the only fully retrieved compatibility row (AGP 9.0.0 + Gradle 9.1.0 + JDK 17 + Build Tools 36.0.0) is time-sensitive and sits in tension with a Compose 1.12 guide requiring compileSdk 37 (AGP 9.0.0 caps at API 36.1) — the full pin set is re-verified together at implementation |
| Accessibility assumptions unvalidated (e.g. 64dp touch targets) | Med | Codify only validated values; caregiver validation via RQ3 |
| Bootstrap diff exceeds the 400-line review budget | High (forecast) | `sdd-tasks` forecasts and slices into work units; `ask-on-risk` delivery strategy |
| AGP↔Gradle↔JDK compatibility mismatch | Med | RQ1 verification plus re-verification of the whole pin set (AGP/Gradle/JDK/Compose/compileSdk) at implementation; wrapper pinned to a verified Gradle version |
| Agent seam drifts into feature code | Low | Scope guard: seam = interface + stub only; `agent-seam` spec enforces no model/API-key/network code |

## Rollback Plan

- **Repo state**: the change only adds new files and modifies `.gitignore`, `README.md`, and `openspec/config.yaml`. Rollback = remove the generated scaffold/CI files and restore the three modified files from `HEAD` (`git clean` + `git checkout`). No data migration exists.
- **Generated artifacts**: wrapper and build files are reproducible from the version catalog + wrapper template — regeneration is the recovery path, not manual surgery.
- **Environment (machine state)**: JDK/SDK installs live outside the repo; rollback = uninstall or repin versions. They cannot corrupt repo state.
- **Toolchain regression**: if a pinned version proves broken, the version catalog centralizes the fix — change one file and regenerate the wrapper.
- **Config drift**: if `sdd-init` re-run left `openspec/config.yaml` inconsistent, restore it from git and re-run `sdd-init`.

## Dependencies

- JDK LTS installed (Phase 0).
- Android SDK installed (cmdline-tools, platform-tools, build-tools, platform); `ANDROID_HOME` configured (Phase 0).
- Gradle distribution or verified wrapper template for wrapper generation (RQ1).
- Research inputs RQ1–RQ3 before design finalizes versions, SDK levels, and accessibility values; TS-01 before CI gates and coverage thresholds are finalized.
- Toolchain pins (AGP, Gradle, JDK, Compose BOM, compileSdk) re-verified as one compatible set at implementation — the only fully retrieved compatibility row is time-sensitive and no pin is final.
- Network access for first dependency resolution; GitHub Actions runner availability for CI.
- Re-run of `sdd-init` after scaffolding to re-enable strict TDD.

## Success Criteria (Acceptance Criteria)

- [ ] `./gradlew :app:assembleDebug` succeeds on a clean checkout with the documented JDK + SDK environment.
- [ ] `./gradlew :app:testDebugUnitTest` runs at workspace level and passes at least one example test (RED→GREEN demonstrated).
- [ ] `./gradlew :app:lint` and Spotless check pass; Kover coverage task is wired.
- [ ] GitHub Actions runs lint + unit tests + assemble on every PR.
- [ ] `.gitignore` contains the Android baseline and still ignores `.atl/`.
- [ ] README documents JDK/SDK setup and first-build commands.
- [ ] Version catalog pins a mutually compatible AGP/Gradle/JDK/Compose/compileSdk set re-verified together — no unverified or stale numbers asserted.
- [ ] Agent seam exists as an interface + stub adapter with confirmation-before-action; no launcher, telephony, contacts, RAG, model, API-key, or web-agent code anywhere in the change.
- [ ] `openspec/config.yaml` reports `strict_tdd: true` after the `sdd-init` re-run, with `context`/`testing` refreshed.