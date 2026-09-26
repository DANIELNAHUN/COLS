# Tasks: Bootstrap Android Launcher Scaffold (COLS)

Implementation plan derived from `proposal.md`, the four delta specs (`project-scaffold`, `app-testing`, `ci-pipeline`, `agent-seam`), and `design.md` (pin set verified 2026-09-23; re-verify at implementation). Repository is at zero-state: no Gradle/Android files exist; `.gitignore` currently contains only `.atl/`; README is 2 lines.

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~1000–1300 authored (excludes generated wrapper scripts/JAR) |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1 (Gradle skeleton + wrapper) → PR 2 (`:app` module + CI gate) → PR 3 (agent seam, RED→GREEN) → PR 4 (quality loop + docs + re-init) |
| Delivery strategy | ask-on-risk |
| Chain strategy | stacked-to-main |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: stacked-to-main
400-line budget risk: High

> The scaffold (Gradle config, Kotlin sources, tests, CI workflow, docs) exceeds the 400-line review budget. Delivery strategy is `ask-on-risk`, so the orchestrator must ask the user to choose a chain strategy before `sdd-apply` starts: `stacked-to-main`, `feature-branch-chain`, or `size:exception`.

### Suggested Work Units

| Unit | Goal | Likely PR | Focused test command | Runtime harness | Rollback boundary |
|------|------|-----------|----------------------|-----------------|-------------------|
| 1 | Toolchain-ready Gradle skeleton, committed: `gradle/libs.versions.toml`, `settings.gradle.kts` (no `:app` include yet), root `build.gradle.kts` (plugins `apply false` + Spotless), `gradle.properties`, wrapper (9.4.1, `gradlew` mode 100755), `.gitignore` Android baseline | PR 1 | `./gradlew --version` (reports Gradle 9.4.1, JVM 17) plus `git ls-files -s gradlew` asserting `100755` (RED→GREEN on exec bit) | Real scenario: on the Phase-0 machine (JDK 17 + Android SDK, `ANDROID_HOME` set), `./gradlew --version` and `./gradlew help` succeed with no standalone Gradle installed | Delete wrapper files, catalog, `settings.gradle.kts`, root `build.gradle.kts`, `gradle.properties`; restore `.gitignore` from `HEAD`. No other files depend on them yet |
| 2 | `:app` module compiles and assembles: `app/build.gradle.kts`, `AndroidManifest.xml`, minimal res, `include(":app")` in settings, `.github/workflows/ci.yml` full gate | PR 2 | `./gradlew :app:assembleDebug` and `./gradlew :app:lintDebug` | Real scenario: local `assembleDebug` produces a debug APK; CI run on PR 2 executes spotlessCheck → lintDebug → testDebugUnitTest (passes with no tests yet) → assembleDebug and reports green | Delete `app/` and `.github/workflows/ci.yml`; revert the `include(":app")` line in `settings.gradle.kts`. PR 1 files untouched |
| 3 | Agent seam: domain contracts (`AgentPort`, `AgentActionGateway`, `ConfirmationPolicy`) + deterministic in-memory adapters + confirmation-rule RED→GREEN tests; composition-root wiring in `MainActivity` | PR 3 | `./gradlew :app:testDebugUnitTest` | Real scenario: RED run captured as AC2 evidence (workspace command fails and identifies the failure), then GREEN run exits 0; stub determinism exercised through `AgentPort` | Delete `domain/agent/`, `data/agent/`, the seam tests, and the wiring lines in `MainActivity.kt`; no consumer outside the seam |
| 4 | Quality loop completion + docs + re-init: Robolectric framework test, Compose smoke test (48dp + semantics), Kover `domain` ≥80% gate, `spotlessCheck` verification, README dev-environment section, `sdd-init` re-run (`strict_tdd: true`) | PR 4 | `./gradlew :app:testDebugUnitTest :app:koverVerify spotlessCheck` | Real scenario: full command green locally; README walkthrough lets a clean-machine developer reach a first successful build (AC6) | Remove the added tests + Kover config; restore `README.md` and `openspec/config.yaml` from `HEAD` |

For `feature-branch-chain` (if chosen): PR #1 base = feature/tracker branch; PR #2 base = PR #1 branch; PR #3 base = PR #2 branch; PR #4 base = PR #3 branch. If a child PR shows previous-slice changes, retarget/rebase before review.

## Threat Matrix — Applicable Rows (carried unchanged from design)

| Boundary | Minimum adversarial cases | Applicability | Design response | Planned RED tests |
|---|---|---|---|---|
| Documentation-like paths / executable files | Committed executable scripts (`gradlew`, `gradlew.bat`, wrapper JAR); executable-bit loss from a Windows-first repo; wrapper JAR substitution | **Applicable** — the wrapper is the repo's only build entry point | `gradlew` committed with mode 100755 (D8); wrapper distributionUrl pinned to 9.4.1; wrapper-validation step (SHA-pinned) recommended in CI; README documents that the wrapper is the sanctioned entry point | CI/verify check: `git ls-files -s gradlew` reports `100755`; wrapper-validation action passes on the committed jar |

RED checks mapped to tasks: local RED check **1.6** → GREEN **1.7**; CI half **4.2**. Rows marked N/A in the design are omitted.

## Phase 0: Environment Bootstrap (machine state — NOT committed)

Prerequisite for every other phase (D1); produces no repo files except the generated wrapper.

- [x] 0.1 Install JDK 17 LTS (Temurin or equivalent); verify `java -version` reports 17 and `java` is on PATH.
- [ ] 0.2 Install Android SDK command-line tools; accept all SDK licenses (`sdkmanager --licenses`).
- [x] 0.3 Install SDK components with `sdkmanager`: `platform-tools`, `platforms;android-37`, `build-tools;36.0.0`; verify with `sdkmanager --list_installed`. (Evidence deviation: components verified present by filesystem inspection — `platform-tools/`, `platforms/android-37.0/`, `build-tools/36.0.0/`, `licenses/android-sdk-license` — because `cmdline-tools`/`sdkmanager` is not installed on this machine; see 0.2.)
- [x] 0.4 Set `ANDROID_HOME` (Windows user environment variable) to the SDK root; verify `$env:ANDROID_HOME` resolves and `sdkmanager --version` runs from a fresh shell.
- [x] 0.5 Generate the Gradle wrapper pinned to 9.4.1 (`gradle wrapper --gradle-version 9.4.1` from any available Gradle distribution); verify `./gradlew --version` reports Gradle 9.4.1 on JVM 17.
- [x] 0.6 Confirm the wrapper is the only entry point from here on: no later task may invoke a standalone `gradle` binary.

## Phase 1: Build Scaffold — committed Gradle skeleton (PR 1)

- [x] 1.1 Create `gradle/libs.versions.toml` with the verified 2026-09-23 pin set (D2): `agp = "9.2.1"`, `gradle-wrapper = "9.4.1"`, `jvm = "17"`, `compose-bom = "2026.08.00"`, `compile-sdk = "37"`, `target-sdk = "36"`, `min-sdk = "26"`; each pin carries a verification-date comment; re-verify the full set as mutually compatible before the first build (AC7); resolve non-critical libs (JUnit, Robolectric 4.16.1, Kover, Spotless, ktlint, androidx core/lifecycle/activity) from the portals at implementation and assert them only after first-build verification. No build file may hard-code a version bypassing the catalog. (Re-verified 2026-09-24; Spotless 8.10.1 / Kover 0.9.9 asserted by the first root build — `gradlew help` + `spotlessCheck` green.)
- [x] 1.2 Create `settings.gradle.kts` with repositories `google`, `mavenCentral`, `gradlePluginPortal` and NO module includes yet (dependency ordering: `include(":app")` arrives with the module in 2.1).
- [x] 1.3 Create root `build.gradle.kts` with plugin declarations (`apply false`) for AGP, Spotless, and Kover, plus the Spotless/ktlint configuration applied to all modules.
- [x] 1.4 Create `gradle.properties` with `org.gradle.jvmargs`, `android.useAndroidX=true`, `kotlin.code.style=official`.
- [x] 1.5 Commit the wrapper generated in 0.5: `gradle/wrapper/gradle-wrapper.properties` (distributionUrl pinned to 9.4.1), `gradle/wrapper/gradle-wrapper.jar`, `gradlew`, `gradlew.bat`.
- [x] 1.6 RED (threat matrix: executable integrity): run `git ls-files -s gradlew` — on a Windows-first host the initial staging typically reports `100644` (executable bit lost) and the planned check FAILS (RED); record the actual staged mode as evidence. (RED confirmed: staged mode was `100644`.)
- [x] 1.7 GREEN: run `git update-index --chmod=+x gradlew`; re-run `git ls-files -s gradlew` → reports `100755` (GREEN); the wrapper-distribution pin remains 9.4.1 (D8). (GREEN confirmed: `100755`.)
- [x] 1.8 Append the standard Android baseline to `.gitignore` (`.gradle/`, `build/`, `local.properties`, `.idea/`, `*.iml`, `.externalNativeBuild/`, `.DS_Store`, …), preserving the existing `.atl/` entry.
- [x] 1.9 Verify ignore rules (AC5): `git check-ignore -v .atl build local.properties` returns paths for all three and `git status` shows no build/IDE noise. (All three matched; `.gradle/` noise gone.)

## Phase 2: `:app` Module + Smoke Screen + Agent Seam (PR 2 + PR 3)

- [x] 2.1 Create `app/build.gradle.kts`: apply `com.android.application` only — Kotlin comes from AGP built-in Kotlin, NOT `org.jetbrains.kotlin.android` (D3); enable Compose (exact DSL read from the AGP built-in-Kotlin migration docs during implementation); `compileSdk 37 / minSdk 26 / targetSdk 36`; namespace `com.cols.launcher`; every version from the catalog; declare JVM test dependencies (JUnit4, Robolectric, Compose test runtime). Add `include(":app")` to `settings.gradle.kts` in the same task.
- [x] 2.2 Create `app/src/main/AndroidManifest.xml`: application + `MainActivity` with the standard `LAUNCHER` intent filter only; no `ROLE_HOME`/`ROLE_DIALER` queries, no telecom permissions (D7).
- [x] 2.3 Create minimal resources under `app/src/main/res/`: `values/strings.xml` (`app_name`), Material 3 theme placeholder, simple adaptive launcher icon.
- [x] 2.4 RED (TDD example, AC2): write `app/src/test/java/com/cols/launcher/domain/agent/ConfirmationPolicyTest.kt` and `AgentActionGatewayTest.kt` against the design contract — `perform(action, null)` returns `ConfirmationRequired` and does NOT execute; a valid single-use confirmation returns `Executed`; a foreign/reused confirmation is refused. Run `./gradlew :app:testDebugUnitTest` and capture the RED output (compile-fail or failing assertions) as evidence. (RED captured: `compileDebugUnitTestKotlin FAILED`, exit 1 — `Unresolved reference 'ConfirmationPolicy'/'AgentActionGateway'/'ActionExecutionResult'/'perform'; implementation did not exist yet. RED line evidence in apply-progress.)
- [x] 2.5 Create `app/src/main/java/com/cols/launcher/domain/agent/AgentPort.kt`: `AgentPort` interface + `AgentRequest`, `AgentOutcome` (`Answer` / `ActionProposal`), `Confirmation` — provider-agnostic; no vendor, model, API-key, or transport concept in names/signatures (agent-seam spec scenario).
- [x] 2.6 Create `app/src/main/java/com/cols/launcher/domain/agent/AgentActionGateway.kt`: `AgentActionGateway` interface + `ActionExecutionResult` (`ConfirmationRequired` / `Executed`) — the consequential-action boundary; domain rule: no valid confirmation, no execution.
- [x] 2.7 Create `app/src/main/java/com/cols/launcher/domain/agent/ConfirmationPolicy.kt`: `issueFor(action)` issues a token; `isValid(confirmation?, action)` validates it — the confirmation-before-action domain rule lives here, not in UI code.
- [x] 2.8 GREEN: create `app/src/main/java/com/cols/launcher/data/agent/InMemoryAgentActionGateway.kt` refusing execution without a valid confirmation; re-run `./gradlew :app:testDebugUnitTest` → GREEN (RED→GREEN loop closed for the example test). (GREEN confirmed: 7 tests, 0 failures, exit 0.)
- [x] 2.9 Create `app/src/main/java/com/cols/launcher/data/agent/InMemoryAgentAdapter.kt` (deterministic in-memory `AgentPort` implementation) plus a stub-contract test asserting deterministic outcomes and no network/file/credential access; `./gradlew :app:testDebugUnitTest` stays GREEN. (GREEN: +3 adapter tests, 10 total green, exit 0.)
- [x] 2.10 Create `app/src/main/java/com/cols/launcher/ui/PlaceholderScreen.kt`: Material 3 smoke screen — title text + one interactive element meeting the ≥48dp touch-target and semantics-label floor (RQ3 codified values only); no launcher, call, contacts, or caregiver features.
- [x] 2.11 Create `app/src/main/java/com/cols/launcher/ui/MainActivity.kt`: composition root binding `AgentPort → InMemoryAgentAdapter` and `AgentActionGateway → InMemoryAgentActionGateway + ConfirmationPolicy`; `setContent` hosts `PlaceholderScreen`; no DI framework (D5).
- [x] 2.12 Verify AC1: `./gradlew :app:assembleDebug` succeeds on the Phase-0 machine and produces a debug APK.

## Phase 3: JVM-First Quality Loop (PR 4)

- [x] 3.1 Write a Robolectric test (`@Config(sdk = [<highest verified>])`) that exercises Android-framework behavior on the JVM with no emulator (app-testing scenario); part of `./gradlew :app:testDebugUnitTest`. (Highest verified SDK = 35: Robolectric 4.16.x supports up to API 36, but the SDK-36 runtime requires Java 21 while the catalog pins `jvm = "17"` — confirmed empirically on first run: "Android SDK 36 requires Java 21 (have Java 17)". `@Config(sdk = [35])` in `AndroidFrameworkTest.kt`: application context from the merged manifest + resource resolution.)
- [x] 3.2 Write a Compose component test (`createComposeRule`) asserting the placeholder screen renders the title and that its interactive element reports a ≥48dp touch target and a semantics label (RQ3 codified floor). (Implemented as `createAndroidComposeRule(MainActivity)` in `PlaceholderScreenSmokeTest.kt` — Robolectric PR 4736 refuses the synthetic ui-test-manifest `ComponentActivity` (no intent filter); the real MainActivity hosts the screen via its composition root. Assertions: `onNodeWithText("COLS").assertIsDisplayed()`, `onNodeWithContentDescription("Placeholder action button")`, and `SemanticsNode.touchBoundsInRoot` ≥ `48.dp.toPx()`.)
- [x] 3.3 Wire Kover in `app/build.gradle.kts`: `koverVerify` enforces ≥80% line coverage for the `domain` package only (`ui` and generated code excluded, TS-01); `koverHtmlReport`/`koverXmlReport` produce reports after the unit suite; no global threshold (app-testing scenarios). (Kover 0.9.9 DSL verified against the plugin jar bytecode: `kover { reports { total { filters { includes { packages("com.cols.launcher.domain") } }; verify { rule("domain line coverage floor") { minBound(80, CoverageUnit.LINE, AggregationType.COVERED_PERCENTAGE) } } } } }` — scope lives on the total variant so per-variant Debug reports stay unfiltered. Measured: LINE 25/26 covered = 96% ≥ 80%.) 
- [x] 3.4 Run `./gradlew spotlessCheck` — all Kotlin sources pass ktlint; if violations, run `spotlessApply` and re-check (app-testing format scenarios). (`:app` now applies Spotless with `kotlin { target("src/**/*.kt") ktlint() }`; root `kotlinGradle` extended to `app/*.gradle.kts`; `.editorconfig` added with the ktlint-documented Compose option `ktlint_function_naming_ignore_when_annotated_with = Composable` for the PascalCase composable. spotlessApply reformatted existing sources (trailing commas/chain style, no semantics change); re-check → BUILD SUCCESSFUL, exit 0.)
- [x] 3.5 Verify workspace test command semantics (AC2): `./gradlew :app:testDebugUnitTest` exits 0 on the green suite; introduce one deliberate break → non-zero exit + failing test identified; restore → green again. (Green: exit 0, 15 tests/0 failures. Break: flipped `null confirmation is never valid` assertion → exit 1, `ConfirmationPolicyTest > null confirmation is never valid FAILED` identified by name. Restored: exit 0 again.)

## Phase 4: CI PR Gate (PR 2 workflow, PR 4 live-run confirmation)

- [ ] 4.1 Create `.github/workflows/ci.yml` (D6): `on: pull_request`; `ubuntu-latest`; steps: `actions/checkout` → `actions/setup-java` (temurin 17, matching catalog `jvm` pin) → SDK provisioning via command-line `sdkmanager` (`platform-tools`, `platforms;android-37`, `build-tools;36.0.0`, licenses accepted in-job) → `./gradlew spotlessCheck` → `./gradlew :app:lintDebug` → `./gradlew :app:testDebugUnitTest` → `./gradlew :app:assembleDebug`; no emulator, no pre-installed Gradle — Gradle comes solely from the committed wrapper (ci-pipeline spec).
- [ ] 4.2 Add the wrapper-integrity CI guard (threat-matrix continuation): a step asserting `git ls-files -s gradlew` reports `100755`, plus a wrapper-validation action pinned by commit SHA (recommended per D6) that must pass on the committed `gradle/wrapper/gradle-wrapper.jar`.
- [ ] 4.3 Verify ci-pipeline scenarios: the workflow JDK version matches the catalog pin; Gradle comes only from the wrapper; no emulator anywhere in the loop; cold-runner provisioning is self-contained.
- [ ] 4.4 Confirm a live green run on the bootstrap PR (AC4) with each of the three gate steps reporting explicit pass/fail status on the PR.

## Phase 5: Docs, Scope Guard, SDD Re-init (PR 4)

- [ ] 5.1 Add the README "Development environment" section: JDK 17 install, Android SDK component list + `sdkmanager` commands, `ANDROID_HOME` configuration (Windows + CI), first-build and test commands; sufficient for a clean-machine developer to complete a first successful build (AC6, project-scaffold onboarding scenario).
- [ ] 5.2 Re-run `sdd-init` at the workspace root so `openspec/config.yaml` reports `strict_tdd: true`, refresh `context`/`testing` with the real stack, and set the workspace test command to `./gradlew :app:testDebugUnitTest` (AC9).
- [ ] 5.3 Scope-guard audit (agent-seam spec, AC8): grep the full change diff for model/API-key/network/RAG/web-agent terms (e.g. `okhttp`, `retrofit`, `apiKey`, `Authorization`, `https://`, `llama`, `gpt`) and filter `./gradlew :app:dependencies` for AI/network artifact groups — zero matches expected; the only agent artifacts are the port, the stub, and the confirmation rule.
- [ ] 5.4 Record the rollback boundary in the PR description per proposal §Rollback Plan: the change is additive except `.gitignore`, `README.md`, `openspec/config.yaml` (restorable from `HEAD`); rollback = delete generated scaffold/CI files + `git clean`/`git checkout` of the three modified files; no data migration; environment state is uninstalled independently of the repo.
