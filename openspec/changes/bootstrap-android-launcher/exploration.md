# Exploration: bootstrap-android-launcher

**Outcome**: COLS needs one bootstrap change that installs a runnable Android toolchain, scaffolds a Kotlin + Compose project, wires TDD-compatible testing and CI, and leaves every product/platform uncertainty as an explicit research question — no launcher or AI-agent features in this change.

## Quick path

1. Review the decision table below — 6 gaps are open, 3 are already settled product facts.
2. Run `sdd-propose` for `bootstrap-android-launcher` using the recommended scope.
3. Resolve research questions (RQ1–RQ7) before or during the design phase.

## Current State (verified facts)

- Repository is intentionally at zero: `README.md` ("Calls-only: launcher for seniors - Android"), `LICENSE`, `.gitignore` (`.atl/`), `.atl/skill-registry.md`, and SDD scaffolding (`openspec/config.yaml`, empty `openspec/specs/` and `openspec/changes/archive/`).
- No Android project, no JDK/Gradle/Kotlin/Android SDK on the machine (`ANDROID_HOME` unset). Even the intended toolchain is not installed.
- Strict TDD is **disabled** in `openspec/config.yaml` (no-runner fallback); the config documents that it must be re-enabled by re-running `sdd-init` after scaffolding.
- Native status reports no active change and `nextRecommended: sdd-new` for this change name.

**Settled product decisions** (from prior session memory, `product/elderly-call-launcher`):

| Product fact | Consequence for the bootstrap |
|---|---|
| Calls only (incoming + outgoing voice) | Call UX, not general home-screen replacement, drives requirements |
| Speakerphone auto-on, simple answer, no accidental mute/end controls | Accessibility and call-control APIs are first-class testable requirements |
| Caregiver/configurator manages contacts with clear photos | A settings/config surface (probably caregiver-only) will exist later |
| AI assistant must be **separate** from the launcher, call-only via Android APIs, RAG, response limits, confirmation before acting, supports API key OR local models | Architecture must isolate the agent behind an interface from day one |
| Future web capabilities must be separate agents | No web-facing code in the launcher core |

## Affected Areas

- `settings.gradle.kts`, `build.gradle.kts` (root + `app`), `gradle/libs.versions.toml`, `gradle/wrapper/*`, `gradlew(.bat)` — new Gradle/Android scaffold.
- `app/src/**` — minimal Kotlin + Compose module scaffold (smoke-test placeholder only, not launcher features).
- `.github/workflows/ci.yml` (new) — build + lint + unit test gate.
- `.gitignore` — extend the current 1-entry file with the standard Android baseline.
- `README.md` — dev environment setup section (JDK, Android SDK).
- `openspec/config.yaml` — update `context`/`testing` after scaffold; re-run `sdd-init` to enable strict TDD.
- No existing code is modified — there is none.

## Decision Table

| # | Decision gap | Status | Options | Recommendation for proposal | Evidence needed |
|---|---|---|---|---|---|
| 1 | Language & toolchain | Open | Kotlin vs Java; JDK LTS version | Kotlin (K2) + latest JDK LTS, version catalog | **RQ1**: current stable JDK/AGP/Kotlin/Gradle versions (research) |
| 2 | SDK levels & devices | Open | minSdk 23 / 26 / 29+; targetSdk | minSdk 26 (unlocks telecom answer APIs), targetSdk = latest stable | **RQ2**: senior-user device demographics; Play targetSdk policy (research) |
| 3 | UI toolkit | Open | Jetpack Compose vs classic Views | **Compose + Material 3** — dynamic type/contrast support, single Google-recommended path, fewer XML layers for a button-centric UI | **RQ3**: current accessibility guidance (research) |
| 4 | Accessibility baseline | Open (product) | Which minimums to codify | Large touch targets (≥64dp, beyond the 48dp minimum), large type scale, high contrast, TalkBack labels, no swipe/gesture-only actions | **RQ3** + caregiver validation (product input) |
| 5 | Module boundaries | Open | Single `:app` with layered packages vs multi-module from day one | Single `:app` now; AI agent behind a Kotlin interface (port) with in-memory/stub adapter; extract `:agent` module when AI work starts | None — product fact already requires the seam |
| 6 | Testing & TDD | Open | JUnit4 + Robolectric, Compose UI tests, Espresso/instrumented, Kover, ktlint/spotless | JVM-first TDD: JUnit4 + Robolectric + Compose test runtime (fast feedback); Kover coverage; ktlint via spotless; instrumented tests deferred (optional smoke) | Re-run `sdd-init` post-scaffold to re-enable strict TDD; **RQ4**: emulator CI cost (research) |
| 7 | Call UX & roles | Open (research-heavy) | Which roles to request now | Bootstrap requests **ROLE_HOME only**; dialer-role/telecom integration deferred to its own change | **RQ5**: `ROLE_DIALER`/`InCallService`/`ANSWER_PHONE_CALLS` exact API-level behavior; speakerphone auto-on mechanism (research) |
| 8 | AI integration boundary | Open (seam only in bootstrap) | Local model vs API key | Provider-agnostic port; confirmation gate ("confirm before acting") as a domain rule; no network/AI dependency in `:app` core | **RQ6**: on-device model options + secure API-key storage (research) |
| 9 | CI & quality gates | Open | GitHub Actions; what gates | PR gate: ktlint + unit tests + assemble; instrumented/nightly later | **RQ4** |
| 10 | `.gitignore` baseline | Open | Custom vs standard template | Standard Android template appended to current file (keeps `.atl/`) | None |

## Approaches (bootstrap scaffold strategy)

1. **Minimal single-module scaffold** — one `:app` module (Kotlin, Compose, Material 3), layered packages (`ui/`, `domain/`, `data/`), agent seam as interface + stub, Gradle wrapper + version catalog, GitHub Actions, full test stack.
   - Pros: smallest diff to review; boundaries already product-mandated via the interface; `:agent` extraction stays mechanical; fast TDD loop.
   - Cons: boundary enforcement is convention-based until extraction.
   - Effort: **Low-Medium**.

2. **Multi-module scaffold from day one** — `:app` + `:core:designsystem` + `:agent` (empty placeholder).
   - Pros: hard compile-time boundaries; parallel builds.
   - Cons: significant Gradle convention-plugin complexity for a zero-code repo; placeholder modules risk rotting; inflates the bootstrap diff past the 400-line review budget.
   - Effort: **Medium-High**.

3. **Toolchain-first standalone step** — install JDK/Android SDK before any repo change, then scaffold.
   - Pros: honest sequencing — the environment is empty, so scaffold verification requires it anyway.
   - Cons: not repo code; must be documented as environment setup tasks in the proposal, not committed artifacts.
   - Effort: **Medium** (environment-dependent).

## Recommendation

**Approach 1 + 3 combined**: document toolchain setup as environment tasks, scaffold a single `:app` module (Kotlin + Compose + Material 3, version catalog, Gradle wrapper), wire JVM-first TDD (JUnit4 + Robolectric + Compose tests, Kover, ktlint/spotless), add a GitHub Actions PR gate, extend `.gitignore` with the standard Android baseline, and place the AI agent strictly behind an interface with a stub adapter. **Recommended scope for `sdd-propose`**: items 1–7 below; everything else deferred.

1. Environment setup tasks: JDK LTS, Android SDK, (re)generation of Gradle wrapper.
2. Gradle scaffold: settings, root/app build files, version catalog.
3. `:app` scaffold with one smoke-test placeholder screen + one passing example test (RED→GREEN demonstrated).
4. Test stack: JUnit4, Robolectric, Compose test runtime, Kover, ktlint/spotless.
5. CI workflow: lint + unit tests + assemble on PR.
6. `.gitignore` baseline + README dev-setup section.
7. Re-run `sdd-init` to enable strict TDD and refresh config context.

**Explicitly out of scope**: launcher features, contacts, dialer role, telephony integration, any AI/RAG code.

## Research Questions (external — not authorized in this phase)

- **RQ1**: Current stable JDK LTS, Android Gradle Plugin, Kotlin, Gradle, Compose BOM versions.
- **RQ2**: Play targetSdk policy; realistic Android versions among senior users.
- **RQ3**: Current Compose/Material accessibility guidance (touch-target sizes, contrast ratios, TalkBest practices).
- **RQ5**: Telecom/role behavior per API level: `ROLE_HOME`, `ROLE_DIALER`, `InCallService`, `ANSWER_PHONE_CALLS`; speakerphone auto-on mechanism; emergency-call coexistence.
- **RQ6**: On-device LLM options and minimum device specs; recommended secure API-key storage on Android.
- **RQ7**: Emulator-based instrumented test cost in CI (feasibility/cost tradeoff).

## Risks

- **No toolchain installed**: wrapper generation and the first build require environment setup first; the proposal MUST state this dependency explicitly (per `openspec/config.yaml` proposal rule).
- **Android platform churn**: SDK/Compose/API-level facts must be verified in research or design, not asserted here.
- **Accessibility assumptions unvalidated**: senior-UX minimums (64dp targets, etc.) are recommendations pending caregiver/user validation.
- **Review budget**: scaffolding + Gradle files + CI can exceed 400 authored lines; `sdd-tasks` must forecast this (`400-line budget risk: Medium`) and plan slices if needed.
- **Role/telecom complexity**: full dialer integration is a distinct, risk-bearing change; keeping it out of bootstrap contains this risk.

## Ready for Proposal

**Yes** — the orchestrator should proceed to `sdd-propose` for `bootstrap-android-launcher` using the recommended scope above, with RQ1–RQ7 attached as research input for the design phase. No user product decisions are pending for the bootstrap itself; SDK-level and accessibility numbers should be confirmed via RQ2/RQ3 during design.
