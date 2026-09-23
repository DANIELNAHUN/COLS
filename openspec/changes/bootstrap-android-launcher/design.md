# Design: Bootstrap Android Launcher Scaffold (COLS)

## Technical Approach

COLS is at repository zero, so this design covers three layers in strict order (per `openspec/config.yaml` design rule "cover toolchain bootstrap before app architecture"):

1. **Environment bootstrap (Phase 0, machine state, not committed)** — JDK 17 LTS, Android SDK (cmdline-tools, platform-tools, build-tools 36.0.0, platform android-37), `ANDROID_HOME`, then generate the Gradle wrapper with Gradle 9.4.1 and commit it.
2. **Build scaffold (Phases 1–2)** — a single `:app` Gradle module whose every version resolves from one version catalog (`gradle/libs.versions.toml`), Kotlin K2 delivered by AGP's built-in Kotlin support, Jetpack Compose + Material 3, layered packages (`ui/`, `domain/`, `data/`), a smoke placeholder screen, and the agent seam (port + confirmation-before-action gate + deterministic in-memory stub).
3. **Quality loop (Phases 3–5)** — JVM-first test stack (JUnit4 + Robolectric + Compose test runtime, Kover, ktlint via Spotless) with one demonstrated RED→GREEN test, a GitHub Actions PR gate (Spotless check + Android Lint + unit tests + assembleDebug, no emulator), `.gitignore` baseline, README environment docs, and the `sdd-init` re-run that re-enables strict TDD.

This implements the proposal's combined Approach 1 (minimal single-module scaffold) + Approach 3 (toolchain-first) and satisfies the four delta specs: `project-scaffold`, `app-testing`, `ci-pipeline`, `agent-seam`.

**Research-status honesty (supersedes the proposal note):** the compileSdk-37-vs-AGP-9.0 tension recorded in the proposal is now RESOLVED by evidence gathered 2026-09-23 — AGP 9.2.0 (April 2026) and later support API level 37 and require Gradle ≥ 9.4.1, which clears Compose 1.12's requirement of compileSdk 37 with minimum AGP 9.1.x. The pin set below is verified as a mutually compatible set from primary sources on 2026-09-23 and MUST be re-verified together at implementation before the first build (one catalog file, one re-check).

## Research Question Resolutions (RQ1–RQ3, TS-01)

### RQ1 — Toolchain versions: RESOLVED with verified facts (2026-09-23), re-verify at implementation

Every row below is a fact retrieved from primary documentation today, not a remembered number:

| Component | Verified value | Verified compatibility constraint | Source (checked 2026-09-23) |
|---|---|---|---|
| Android Gradle Plugin | **9.2.1** (9.2.0 Apr 2026; 9.2.1 exists per AGP release-history) | Supports max API **37.0**; requires **Gradle 9.4.1** (min/default); **JDK 17**; Build Tools **36.0.0** | AGP 9.2.0 release notes, developer.android.com/build/releases/agp-9-2-0-release-notes |
| Gradle (wrapper) | **9.4.1** | AGP 9.2 min/default version | same |
| JDK | **17 (LTS)** | AGP 9.x min and default JDK across 9.0–9.4 | AGP 9.0/9.2/9.4 release notes |
| SDK Build Tools | **36.0.0** | AGP 9.2 default | AGP 9.2 release notes |
| Kotlin | **2.3.x** via AGP **built-in Kotlin** | AGP 9.0 introduced built-in Kotlin with a runtime dependency on KGP 2.2.10; AGP 9.2 updated it to **KGP 2.3.10**; `org.jetbrains.kotlin.android` is NOT compatible with the new DSL | AGP 9.0 + 9.2 release notes |
| Compose BOM | **2026.08.00** (stable, Aug 2026) | Core Compose **1.12.0**; requires **compileSdk 37** and **minimum AGP 9.1.x**; Compose compiler version = Kotlin version (Kotlin 2.0+) | Compose Aug '26 release blog; compose-bom docs |
| compileSdk | **37** | Required by Compose 1.12; supported by AGP 9.2 (max API 37.0); ≥ targetSdk 36 | Compose Aug '26 blog; AGP 9.2 notes; AGP consumer rule |
| targetSdk | **36** (Android 16) | Since **Aug 31, 2026**, new apps and updates on Google Play MUST target API 36+ | developer.android.com/google/play/requirements/target-sdk |
| minSdk | **26** (working decision, see RQ2) | Chosen so the future telephony change is not blocked; raising minSdk later is trivial, lowering it is not | exploration decision table (product input) |

Rejected alternatives for the pin set, with reasons:

- **AGP 9.0.0 + Gradle 9.1.0** (the proposal's only previously retrieved row): caps at API 36.1 — incompatible with Compose 1.12's compileSdk 37. Rejected.
- **AGP 9.3.1** (listed "Current Release" on the AGP API reference page): its Gradle requirement row was not retrieved (page fetch timed out); pinning it would assert an unverified Gradle number. Rejected in favor of the fully verified 9.2 row; re-checking 9.3.x is optional at implementation.
- **AGP 9.4.0 + Gradle 9.6.0** (September 2026): verified row exists, but it is a same-week release — avoid newest-release risk for a bootstrap. Rejected for now; the catalog makes the bump one line.

Non-critical tool versions (Kover, Spotless, ktlint, JUnit, androidx core/lifecycle/activity) are NOT part of the verified AGP/Gradle/JDK/Compose/compileSdk set; their catalog entries are resolved from the plugin portals at implementation with AGP-9 compatibility confirmed at first build. No version number is asserted here without verification.

### RQ2 — SDK levels and Play policy: PARTIALLY RESOLVED (unresolved part does not block)

- **Verified:** Play's target-API step took effect August 31, 2026 — new apps and updates must target **API 36+**. COLS therefore pins `targetSdk = 36`. (Extension window to Nov 1, 2026 applies to existing apps only; irrelevant to a new app.)
- **Unresolved (non-blocking):** real-device distribution of Android versions among the target senior demographic. No authorized demographic data source was available in this phase. The bootstrap pins `minSdk = 26` as the working decision (exploration recommendation: unlocks the telephony/answer APIs the product requires, while still covering devices back to Android 8.0). Consequence: minSdk is a one-line catalog change if caregiver/device research later says otherwise; nothing in the scaffold hard-codes it. **Does not block task planning.**

### RQ3 — Accessibility guidance: RESOLVED to the validated floor; senior-UX extras stay product decisions

Verified, currently published guidance (checked 2026-09-23):

- **Touch targets ≥ 48×48dp**, separated by **≥ 8dp** (Material Design accessibility guidelines; Android Accessibility Help; Compose "API defaults" page).
- Compose Material components (Checkbox, Switch, Slider, Surface…) already pad interactive components to the 48dp minimum internally.
- **Contrast ≥ 4.5:1 for small text; ≥ 3:1 for large text** (Material accessibility guidance, matching WCAG AA).
- TalkBack practice: every actionable element exposes semantics (labels/roles) via Compose semantics — standard Compose accessibility practice.

Codified in this bootstrap as testable requirements: the placeholder screen's interactive element meets the 48dp minimum and carries a semantics label. **NOT codified** (explicitly product-pending, never invented): the ≥ 64dp senior touch-target floor, specific type-scale minimums, and final TalkBack wording standards — these await caregiver validation (RQ3 product input) and land as testable requirements in a later change. **Does not block task planning.**

### TS-01 — Test pyramid, coverage gates, and acceptance-criterion verification mapping: RESOLVED (design decision, defined here)

**Test pyramid (COLS bootstrap and default for later changes):**

| Layer | Runner / stack | Where it runs | Share of suite | Status in bootstrap |
|---|---|---|---|---|
| 1. JVM unit | JUnit4; pure JVM for domain | Gradle JVM | broad base (~70%) | Active |
| 1b. JVM unit + Robolectric | JUnit4 + Robolectric for Android-framework behavior | JVM, no emulator | framework-touching tests | Active |
| 1c. JVM Compose component tests | Compose test runtime (`createComposeRule`) on Robolectric | JVM, no emulator | UI component checks | Active (smoke screen) |
| 2. Instrumented / device | Espresso/UiAutomator on emulator or device | emulator/device | thin apex, only for behavior JVM cannot verify (TelecomManager, real roles) | **Deferred** to the telephony change (RQ5); no instrumented source set is created |

There is no separate "integration" module in this bootstrap: Robolectric IS the JVM integration layer. Instrumented tests enter CI only when a change needs device-only behavior; that decision is attached to the telephony change, not re-opened per change.

**Coverage gates (Kover):**

- Kover is wired to `:app`; report tasks (`koverHtmlReport`, `koverXmlReport`) produce reports after the unit suite.
- **Initial enforced gate:** `:app:koverVerify` requires **≥ 80% line coverage for the `domain` package only** (agent seam + confirmation rule), with `ui` and generated code excluded. Rationale: the domain layer in this bootstrap is a small, pure-JVM, fully unit-testable seam; the seam tests this change writes are exactly what covers it, so an 80% floor is meaningful without inventing risk. This floor is an engineering-policy decision delegated to design by TS-01 (the "no invented numbers" honesty rule in the proposal applies to platform/version facts, not to this policy), and it is adjustable in one place (`libs.versions.toml`-adjacent Kover config) as real code lands.
- No global/total-project threshold is enforced until a later change re-baselines it with product code present.

**Acceptance-criterion → verification mapping (proposal §Success Criteria):**

| # | Acceptance criterion | Concrete verification check | Automated where? |
|---|---|---|---|
| AC1 | `./gradlew :app:assembleDebug` succeeds on clean checkout | Fresh-clone run on a machine with the documented env; automated proxy: CI `assembleDebug` step | CI + local |
| AC2 | `:app:testDebugUnitTest` workspace command passes ≥1 example test, RED→GREEN demonstrated | CI test step + recorded RED demonstration during apply (RED run output captured in tasks/verify notes) | CI + apply log |
| AC3 | `:app:lint` + Spotless check pass; Kover wired | CI `spotlessCheck` + `:app:lintDebug` steps; `koverVerify` gate in check; coverage report file exists (verify phase) | CI + verify |
| AC4 | GitHub Actions runs gate on every PR | Workflow `on: pull_request`; confirmed by an executed run on the bootstrap PR | CI (live run) |
| AC5 | `.gitignore` has Android baseline, still ignores `.atl/` | `git check-ignore .atl build local.properties` returns paths; `git status` shows no build noise | verify script |
| AC6 | README documents JDK/SDK setup + first-build commands | Doc review against AC1's fresh-machine path | review |
| AC7 | Catalog pins a mutually verified AGP/Gradle/JDK/Compose/compileSdk set | Implementation re-checks catalog against the RQ1 matrix before first build; evidence recorded in verify-report | verify |
| AC8 | Agent seam = interface + stub + confirmation rule; zero feature/AI code | Diff audit (agent-seam spec scenarios) + `./gradlew :app:dependencies` filtered for network/AI/artifact groups + grep audit for OkHttp/Retrofit/API-key terms | verify |
| AC9 | `openspec/config.yaml` reports `strict_tdd: true` after sdd-init re-run | Read config post-re-run | sdd-init re-run output |

## Architecture Decisions

### Decision D1: Toolchain bootstrap precedes and gates every scaffold task

**Choice**: Phase 0 (JDK 17, Android SDK, `ANDROID_HOME`, wrapper generation) is a hard prerequisite executed on the machine before any committed scaffold work; it produces no repo files except the generated wrapper itself.
**Alternatives considered**: (a) scaffold first and hope the environment materializes — rejected, first build would fail and fake the TDD loop; (b) CI-only verification — rejected, the workspace-level test command must work locally per `app-testing` spec.
**Rationale**: the machine provably lacks the toolchain (verified in config context and exploration); every downstream verification (RED→GREEN, lint, assemble) is meaningless without it. README becomes the single documented environment contract (AC6).

### Decision D2: Pin the verified AGP 9.2.x row, not the newest AGP

**Choice**: `AGP 9.2.1`, `Gradle wrapper 9.4.1`, `JDK 17`, `Build Tools 36.0.0`, `compileSdk 37`, `targetSdk 36`, `minSdk 26` — re-verified as one set at implementation.
**Alternatives considered**: AGP 9.0.0 row (incompatible with Compose 1.12's compileSdk 37); AGP 9.3.1 (Gradle row unverified at design time); AGP 9.4.0 (same-month release).
**Rationale**: the version-catalog spec requires a mutually compatible, verified set; AGP 9.2.x is the newest row whose every member is documented together in one release-notes page. Compose 1.12 (BOM 2026.08.00) needs compileSdk 37 + AGP ≥ 9.1.x, and AGP 9.2 supports API 37.0 — the whole set closes.

### Decision D3: Kotlin ships from AGP built-in Kotlin — no separate Kotlin Android plugin

**Choice**: the `:app` build applies only `com.android.application`; Kotlin compilation comes from AGP 9's built-in Kotlin (KGP 2.3.x). The Compose compiler is versioned with Kotlin (Kotlin 2.0+ rule).
**Alternatives considered**: applying `org.jetbrains.kotlin.android` — explicitly NOT compatible with AGP 9's new DSL (AGP 9.0 release notes); the pre-AGP-9 pattern is dead for this toolchain.
**Rationale**: removes an entire plugin/version-drift axis from the catalog; Kotlin is then versioned by AGP itself, matching the "single source of pins" requirement. Open implementation detail (non-blocking): the exact DSL for enabling Compose under built-in Kotlin must be read from the AGP built-in-Kotlin migration docs during Phase 2; it is a DSL lookup, not a compatibility risk.

### Decision D4: Single `:app` module with layered packages; `:agent` extraction stays mechanical

**Choice**: exactly one Gradle module (`:app`) with packages `com.cols.launcher.ui`, `com.cols.launcher.domain`, `com.cols.launcher.data`.
**Alternatives considered**: multi-module scaffold (`:agent`, `:core:designsystem`) from day one — rejected (exploration Approach 2): Gradle convention-plugin complexity, placeholder-module rot, and a diff that blows the 400-line review budget for zero product value yet.
**Rationale**: boundaries are enforced by package layering + the port interface now; compile-time enforcement is added later by moving already-isolated files — mechanical by construction of the seam (D6).

### Decision D5: Agent seam = port + confirmation-gated action gateway + deterministic stub

**Choice**: the domain layer owns two interfaces — `AgentPort` (provider-agnostic capability port) and `AgentActionGateway` (the consequential-action boundary) — plus a `ConfirmationPolicy`. The `data` layer provides in-memory implementations with deterministic behavior. Consumers (domain use cases, the smoke screen) depend only on interfaces; a manual composition root wires the stub. `suspend` signatures on the port (kotlinx-coroutines declared).
**Alternatives considered**: putting the confirmation rule in UI code — rejected by the agent-seam spec (rule must live in domain so every future agent inherits it); adding DI (Hilt) in bootstrap — rejected as unnecessary machinery for one consumer; embedding the stub in `domain/` — rejected, adapters belong on the data side of the port.
**Rationale**: makes the confirmation-before-action rule mechanically un-bypassable (a call without a valid confirmation token structurally returns `ConfirmationRequired`), keeps the future provider/model/API-key work outside this change's diff, and makes `:agent` extraction a file move, not a redesign.

### Decision D6: CI runs on hosted runners with first-party actions + explicit SDK provisioning

**Choice**: `ci.yml` triggers on `pull_request`, runs on `ubuntu-latest`, uses first-party GitHub actions (`actions/checkout`, `actions/setup-java`) pinned to reviewed majors, provisions the Android SDK itself via command-line `sdkmanager` (platform-tools, platforms;android-37, build-tools;36.0.0, licenses accepted), then runs `./gradlew spotlessCheck :app:lintDebug :app:testDebugUnitTest :app:assembleDebug`. No emulator, no pre-installed runner Gradle, JDK 17 matching the catalog pin.
**Alternatives considered**: third-party SDK-setup actions (broader supply-chain surface) — rejected for the bootstrap; a third-party Gradle-wrapper-validation action is included as a recommended extra (pinned by commit SHA) because the wrapper JAR is the repo's one committed binary.
**Rationale**: satisfies `ci-pipeline` spec ("obtains it via documented setup steps inside the workflow", "Gradle comes solely from the committed wrapper") while minimizing third-party code executed per PR. Step order (format → lint → test → assemble) fails fast on the cheapest check first.

### Decision D7: Compose smoke placeholder screen; no role, no launcher manifest features

**Choice**: one `MainActivity` rendering a Material 3 placeholder screen (title text + one interactive element meeting the 48dp/semantics floor). Manifest carries only the standard `LAUNCHER` intent filter — no `ROLE_HOME`/`ROLE_DIALER` queries, no telecom permissions.
**Alternatives considered**: requesting `ROLE_HOME` in bootstrap (exploration decision-table row 7) — SUPERSEDED by the proposal's explicit non-goal ("No telephony integration: `ROLE_HOME`/`ROLE_DIALER`…"); roles belong to the telephony change.
**Rationale**: the proposal is the later, authoritative artifact; roles bring manifest/permission surface with zero bootstrap payoff.

### Decision D8: Committed wrapper is the only sanctioned entry point; executable-bit integrity is a design requirement

**Choice**: `gradlew`, `gradlew.bat`, and `gradle/wrapper/*` are generated with Gradle 9.4.1 and committed; wrapper distributionUrl pins 9.4.1; on the Windows dev host the `gradlew` POSIX script is committed with mode 100755 (via `git update-index --chmod=+x gradlew`) so CI's `./gradlew` works.
**Alternatives considered**: invoking `bash gradlew` in CI to dodge a lost exec bit — rejected as masking a real defect; installing Gradle on CI — violates the scaffold spec.
**Rationale**: wrapper-integrity failures are the classic Windows-host-first Android repo failure; making the mode a checked requirement (see Threat Matrix) keeps the CI green without hacks.

## Data Flow

**Build/CI flow (Phase 0 environment is the root dependency of everything):**

    Developer machine (Phase 0)                    GitHub Actions runner (PR gate)
    ─────────────────────────────                 ────────────────────────────────
    README env section ─→ JDK 17 + Android SDK        actions/checkout
    (JDK, SDK 37, build-tools 36.0.0,                  actions/setup-java (temurin 17)
     ANDROID_HOME)                                     sdkmanager: platform-tools,
                                                       platforms;android-37,
                                                       build-tools;36.0.0 + licenses
                                                            │
                                                            ▼
              gradle/libs.versions.toml ◄──single source of pins──► gradlew (9.4.1)
                                                            │
                       ┌────────────────────────────────────┼──────────────────────────┐
                       ▼                                    ▼                          ▼
                spotlessCheck (ktlint)              :app:testDebugUnitTest        :app:assembleDebug
                                                    (JUnit4 + Robolectric +            │
                                                     Compose test runtime)             ▼
                                                            │                    debug APK artifact
                                                            ▼
                                                     :app:koverVerify (domain ≥80%)
                                             any failure → red run → merge blocked

**Runtime seam flow (bootstrap):**

    MainActivity ─→ ui/ PlaceholderScreen ─→ domain/ use case ──depends on──► AgentPort
                                                  │                              │
                          ConfirmationPolicy ◄────┤                     data/ InMemoryAgentAdapter
                                  │               │                              ▲
                                  │ issue/validate│                             │ (wired by
                                  ▼               ▼                             │ composition root)
                          AgentActionGateway.perform(action, confirmation?)
                              ├─ confirmation missing/invalid → ConfirmationRequired (action NOT executed)
                              └─ confirmation valid           → Executed (deterministic stub receipt)

    Composition root (Application/MainActivity): binds AgentPort → InMemoryAgentAdapter,
    AgentActionGateway → InMemoryAgentActionGateway + ConfirmationPolicy. No DI framework.

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `settings.gradle.kts` | Create | Root settings: plugin/dependency repositories (google, mavenCentral, gradlePluginPortal), `include(":app")` only |
| `build.gradle.kts` | Create | Root build: plugin declarations (`apply false`) for AGP, Spotless; hosts the Spotless/ktlint configuration applied to all modules |
| `gradle/libs.versions.toml` | Create | Version catalog: the verified toolchain set (D2) + library/plugin pins; carries verification-date comments |
| `gradle.properties` | Create | `org.gradle.jvmargs`, `android.useAndroidX=true`, `kotlin.code.style=official` |
| `gradle/wrapper/gradle-wrapper.properties` (+ jar) | Create | Wrapper properties pinning Gradle 9.4.1 distribution (generated in Phase 0) |
| `gradlew` | Create | POSIX wrapper script, committed with mode 100755 (D8) |
| `gradlew.bat` | Create | Windows wrapper script (generated) |
| `app/build.gradle.kts` | Create | `:app` module: android application plugin (built-in Kotlin), Compose enabled, compileSdk 37 / minSdk 26 / targetSdk 36, namespace `com.cols.launcher`, unit-test deps, Kover plugin + domain-scoped verify gate |
| `app/src/main/AndroidManifest.xml` | Create | Minimal manifest: application, `MainActivity` with standard LAUNCHER intent; no roles/permissions |
| `app/src/main/java/com/cols/launcher/ui/MainActivity.kt` | Create | Composition root + `setContent` host for the placeholder screen |
| `app/src/main/java/com/cols/launcher/ui/PlaceholderScreen.kt` | Create | Material 3 smoke screen (title + interactive element ≥48dp with semantics) |
| `app/src/main/java/com/cols/launcher/domain/agent/AgentPort.kt` | Create | Provider-agnostic port interface + `AgentRequest`/`AgentOutcome` model (D5) |
| `app/src/main/java/com/cols/launcher/domain/agent/AgentActionGateway.kt` | Create | Consequential-action boundary interface + `ActionExecutionResult` model |
| `app/src/main/java/com/cols/launcher/domain/agent/ConfirmationPolicy.kt` | Create | Issues/validates confirmation tokens for proposed actions (domain rule) |
| `app/src/main/java/com/cols/launcher/data/agent/InMemoryAgentAdapter.kt` | Create | Deterministic in-memory `AgentPort` implementation; no network/files/credentials |
| `app/src/main/java/com/cols/launcher/data/agent/InMemoryAgentActionGateway.kt` | Create | In-memory gateway refusing action execution without a valid confirmation |
| `app/src/main/res/**` | Create | Minimal resources: `strings.xml` (app_name), Material 3 theme placeholder, simple adaptive launcher icon |
| `app/src/test/java/com/cols/launcher/…` | Create | Unit tests: confirmation-rule RED→GREEN example, stub determinism, Robolectric framework test, Compose smoke test |
| `.github/workflows/ci.yml` | Create | PR gate per D6: checkout → JDK 17 → sdkmanager SDK → spotlessCheck → lintDebug → testDebugUnitTest → assembleDebug |
| `.gitignore` | Modify | Append standard Android baseline (`.gradle/`, `build/`, `local.properties`, `.idea/`, `*.iml`, `.externalNativeBuild/`, `.DS_Store`…); preserve `.atl/` |
| `README.md` | Modify | Add "Development environment" section: JDK 17 install, SDK component list + `sdkmanager` commands, `ANDROID_HOME` (Windows/CI), first-build + test commands |
| `openspec/config.yaml` | Modify | Via `sdd-init` re-run (Phase 5): `context`/`testing` refreshed with the real stack; `strict_tdd: true` with workspace command `./gradlew :app:testDebugUnitTest` |

No files are deleted. No existing source is modified (there is none).

## Interfaces / Contracts

### Agent seam (Kotlin, domain-owned)

```kotlin
package com.cols.launcher.domain.agent

// Provider-agnostic: no vendor, model, key, or transport concept may appear here.
interface AgentPort {
    suspend fun respondTo(request: AgentRequest): AgentOutcome
}

data class AgentRequest(val utterance: String)

sealed interface AgentOutcome {
    data class Answer(val text: String) : AgentOutcome
    // An agent-proposed consequential action: execution requires confirmation.
    data class ActionProposal(
        val action: AgentAction,
        val confirmation: Confirmation,
    ) : AgentOutcome
}

data class Confirmation(val id: String)   // opaque, single-use token

// The consequential-action boundary. Domain rule: no valid Confirmation, no execution.
interface AgentActionGateway {
    suspend fun perform(action: AgentAction, confirmation: Confirmation?): ActionExecutionResult
}

sealed interface ActionExecutionResult {
    data object ConfirmationRequired : ActionExecutionResult  // flow must surface this
    data class Executed(val receipt: String) : ActionExecutionResult
}

interface ConfirmationPolicy {
    fun issueFor(action: AgentAction): Confirmation
    fun isValid(confirmation: Confirmation?, action: AgentAction): Boolean
}
```

Substitutability contract (agent-seam spec): consumers compile and behave against `AgentPort`/`AgentActionGateway` only; any future real adapter (on-device model, API-keyed service) implements these without touching domain code, and the confirmation rule is inherited automatically.

### Version catalog shape (`gradle/libs.versions.toml`)

```toml
[versions]
agp = "9.2.1"              # verified 2026-09-23 vs AGP 9.2 release notes — re-verify at implementation
gradle-wrapper = "9.4.1"   # distributionUrl pin; AGP 9.2 min/default
jvm = "17"                 # AGP 9.x required JDK
compose-bom = "2026.08.00" # Compose 1.12.0; requires compileSdk 37
compile-sdk = "37"
target-sdk = "36"          # Play policy effective 2026-08-31
min-sdk = "26"             # working decision (telephony headroom)
robolectric = "4.16.1"     # latest stable 2026-09-23; SDK shadow max verified at implementation
# junit, kover, spotless, ktlint, androidx core/lifecycle/activity:
# resolved from portals at implementation; asserted only after first-build verification
```

### CI workflow contract (`.github/workflows/ci.yml`)

```yaml
name: pr-gate
on: { pull_request: {} }
jobs:
  gate:
    runs-on: ubuntu-latest
    steps:
      # 1. checkout (first-party action, reviewed major)
      # 2. actions/setup-java: temurin, 17  ← must equal [versions].jvm (ci-pipeline spec)
      # 3. SDK: command-line tools → sdkmanager install
      #    "platform-tools" "platforms;android-37" "build-tools;36.0.0"; licenses accepted in-job
      # 4. ./gradlew spotlessCheck            (cheapest, fails first)
      # 5. ./gradlew :app:lintDebug           (Android Lint; API-level misuse gate)
      # 6. ./gradlew :app:testDebugUnitTest   (workspace unit command; exits 0 only on green)
      # 7. ./gradlew :app:assembleDebug
```

No emulator, no device, no pre-installed Gradle; Gradle comes only from the committed wrapper.

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit (pure JVM) | Confirmation-before-action rule: proposal issues a confirmation; `perform` without confirmation → `ConfirmationRequired`; with valid confirmation → `Executed`; expired/foreign confirmation refused | JUnit4, no Robolectric |
| Unit (stub contract) | `InMemoryAgentAdapter` returns deterministic outcomes via `AgentPort`; no network/file/credential access (contract test) | JUnit4 |
| Unit (Robolectric) | Android-framework-dependent code path exercises real framework behavior on JVM with no emulator | JUnit4 + Robolectric (`@Config` pinned to the highest verified SDK) |
| Component (Compose) | Placeholder screen renders title; its interactive element reports ≥48dp touch target and a semantics label | Compose test runtime (`createComposeRule`) on Robolectric |
| RED→GREEN demonstration | The confirmation-rule test is written first, observed failing (RED), then the gate implementation turns it GREEN | Executed during apply; failure/pass output recorded as AC2 evidence |
| Format/lint | ktlint compliance via `spotlessCheck`; Android Lint via `:app:lintDebug` | Gradle tasks, local + CI |
| Coverage | `koverVerify` domain floor (≥80%); report generation smoke-checked | Kover wired, no global threshold |
| CI gate | Workflow executes all steps on a PR; any failing step fails the run with the failing step visible | Live run on the bootstrap PR |
| Scope-guard audit | No AI/network/credential/model code anywhere in the diff; no AI SDK in `:app` dependencies | Diff audit + `:app:dependencies` filter + term grep (verify phase) |

## Threat Matrix

Applicability-driven assessment for this change (changes CI process integration, shell-documented commands, and committed executable files; `references/threat-matrix.md` loaded):

| Boundary | Minimum adversarial cases | Applicability | Design response | Planned RED tests |
|---|---|---|---|---|
| Documentation-like paths / executable files | Committed executable scripts (`gradlew`, `gradlew.bat`, wrapper JAR); executable-bit loss from a Windows-first repo; wrapper JAR substitution | **Applicable** — the wrapper is the repo's only build entry point | `gradlew` committed with mode 100755 (D8); wrapper distributionUrl pinned to 9.4.1; wrapper-validation step (SHA-pinned) recommended in CI; README documents that the wrapper is the sanctioned entry point | CI/verify check: `git ls-files -s gradlew` reports `100755`; wrapper-validation action passes on the committed jar |
| Git repository selection | `git -C`, relative/absolute path authority | N/A — the change authors no git-selection logic; CI checkout is action-managed | — | — |
| Commit state | staged/`commit -a`/empty-index semantics | N/A — no commit automation is authored; contributors follow normal git practice | — | — |
| Push state | tracking branch, first push, refspec | N/A — no push automation authored | — | — |
| PR commands | explicit `--head`, composed `gh` commands | N/A — no PR CLI automation authored; the gate is GitHub-native `on: pull_request` | — | — |

Applicable rows carry into `tasks.md` unchanged; implementation writes the mapped checks before or with the production change.

## Migration / Rollout

No data migration exists. Rollout follows the proposal's phase order (0→5); all committed work is additive except three modified files (`.gitignore`, `README.md`, `openspec/config.yaml`), restorable from `HEAD`. Rollback paths (proposal §Rollback Plan) remain valid: delete generated scaffold, restore three files, uninstall machine-state tools independently of the repo. Review-budget note carried forward for `sdd-tasks`: scaffold + Gradle + CI is forecast to exceed 400 authored lines (`400-line budget risk: Medium`, per exploration/proposal); delivery strategy `ask-on-risk` stands until tasks resolve slicing.

## Open Questions

1. **Robolectric SDK-36/37 shadow availability** — Robolectric stable (4.16.1) targets recent SDKs, but exact API-36/37 shadow coverage was not verifiable in this phase. Fallback designed-in: `@Config(sdk = [<highest verified>])`. **Does not block task planning.**
2. **Kover / Spotless / ktlint / androidx library versions** — resolved from portals at implementation; excluded from the critical verified set (D2). **Does not block task planning.**
3. **Compose-enablement DSL under AGP built-in Kotlin** — read from AGP docs during Phase 2. **Does not block task planning.**
4. **Final `applicationId`** — working value `com.cols.launcher` must be settled before any Play distribution (identity is effectively permanent post-publication). **Blocks only Play publishing (far future), not this change's tasks.**
5. **Senior-device demographics (minSdk validation)** and **64dp touch-target floor** — product/caregiver inputs deferred with the telephony and accessibility-baseline changes (RQ2/RQ3 product halves). **Do not block this bootstrap.**
6. **AGP 9.3.x Gradle row** — unverified at design time (fetch timeout); the pinned 9.2.1 row is unaffected. **Does not block task planning.**
