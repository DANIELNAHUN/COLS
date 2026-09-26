# Apply Progress: Bootstrap Android Launcher Scaffold (COLS)

Cumulative apply-progress artifact for this change (created per OpenSpec file
convention; no prior apply-progress existed — a previous apply dispatch was
cancelled after generating untracked partial files, reconciled in this slice).

**Header reconciliation (this slice)**: the previous version of this file
claimed "completed: 15" while `tasks.md` and native status report 14 checked
tasks before this slice. A direct count of the checked-in `tasks.md` at that
point confirmed 14 (0.1, 0.3–0.6 = 5, plus all nine Phase 1 tasks). The stale
15 was a bookkeeping error in a cancelled draft, not hidden completed work;
this slice corrects the running counts to 18/40 after 2.1–2.3 and 2.12.

**Slice 3 update (work unit 3, PR 3 / `pr3/agent-seam`, commit cfbe556)**:
tasks 2.4–2.11 complete — running counts now 26/40 (completed: 26, pending: 14).

**Slice 4 update (work unit 4, PR 4 / `pr4/quality-loop`, commit a1cc252)**:
Phase 3 (tasks 3.1–3.5) complete — running counts now 31/40 (completed: 31, pending: 9).

**Slice 5 update (work unit 5, PR 5 / `pr5/ci-gate`, commit 6ccd392)**:
Phase 4 tasks 4.1–4.3 complete — running counts now 34/40 (completed: 34, pending: 6).
Task 4.4 (live green run) stays open: pushing `pr5/ci-gate` and opening the PR is the orchestrator's/user's call (interactive pace precedent), and AC4's evidence must come from the real hosted run, not a local simulation.

**Slice 6 update (work unit 6, PR 6 / `pr6/docs-reinit`, on this slice)**:
Phase 5 tasks 5.1–5.3 complete — running counts now 37/40 (completed: 37, pending: 3).
Pending: 0.2 (machine-state cmdline-tools install), 4.4 (live PR run), 5.4 (rollback text in the PR description). 4.4 and 5.4 are both blocked on the same remote step: pushing the stacked chain and opening the PRs — not authorized to this executor; the prepared rollback text is below.

**Slice 7 update (post-delivery progress continuation, no source changes; commits 4866b99 → this slice)**:
remote delivery completed by the maintainer: the stacked chain pushed to `origin`, PRs #1–#6 opened (stacked-to-main per `Chain strategy: stacked-to-main`). Task 5.4 is now [x]. Task 4.4 remains open with recorded hosted-gate evidence. Running counts: 38/40 (completed: 38, pending: 2).

## Slice 7 (progress continuation after authorized remote delivery): mark 5.4 done via PR #6 evidence

- [x] 5.4 close-out evidence: all six branches pushed to `origin` and PRs opened (stacked-to-main): #1 `pr1/gradle-skeleton` → `main` (https://github.com/DANIELNAHUN/COLS/pull/1), #2 `pr2/app-module` → `pr1/gradle-skeleton` (https://github.com/DANIELNAHUN/COLS/pull/2), #3 `pr3/agent-seam` → `pr2/app-module` (https://github.com/DANIELNAHUN/COLS/pull/3, maintainer explicitly accepted `size:exception` at 443 authored lines), #4 `pr4/quality-loop` → `pr3/agent-seam` (https://github.com/DANIELNAHUN/COLS/pull/4), #5 `pr5/ci-gate` → `pr4/quality-loop` (https://github.com/DANIELNAHUN/COLS/pull/5), #6 `pr6/docs-reinit` → `pr5/ci-gate` (https://github.com/DANIELNAHUN/COLS/pull/6). PR #6's description carries the prepared rollback boundary text; task 5.4 marked [x] on that evidence.
- ❌ 4.4 evidence state (kept open): a hosted-gate check on `pr6/docs-reinit` returned exactly `no checks reported on the 'pr6/docs-reinit' branch` — no workflow can trigger because the CI workflow is not present on `main` (Actions API reports 0 registered workflows and 0 runs). GitHub Actions permissions are enabled but unused. The three gate steps (`spotlessCheck`, `lintDebug`, `testDebugUnitTest` + `assembleDebug`) therefore have ZERO hosted pass/fail status; AC4 is NOT evidenced. The blocker resolves only once PR #1 (workflow arrives on `main`) merges and the workflow runs, so 4.4's completion must be re-checked then — no simulated or local evidence can substitute.
- [ ] 0.2 remains open (machine-state cmdline-tools install, deferred from Phase 0).

## Slice 6 (work unit 6, PR 6 / `pr6/docs-reinit`): Phase 5 docs + re-init + scope-guard

- [x] 5.1 README "Development environment" section (100-line README): wrapper-only entry point up front; JDK 17 (Temurin/winget, `java -version` check, catalog↔CI co-update contract); SDK cmdline-tools + `sdkmanager --licenses` + the exact pinned component set; persistent Windows `ANDROID_HOME` (`[Environment]::SetEnvironmentVariable`) + POSIX variant + "CI provisions its own SDK" note; first-build/test commands in `.\gradlew.bat` and `./gradlew` forms; the four quality-gate commands matching ci.yml steps; toolchain-pins section (catalog as single source, wrapper auto-downloads 9.4.1).
- [x] 5.2 `sdd-init` re-run reflected in `openspec/config.yaml`: project discovery found exactly one in-scope project root (Gradle at the workspace root) and the explicit workspace-level command covers it → decision gate resolves `strict_tdd: true` (workspace-wide-command rationale recorded in `strict_tdd_reason`); `context` refreshed with the real stack; `testing.projects` carries the `.` entry (stack/command/framework), `workspace_test_command: ./gradlew :app:testDebugUnitTest`, layers/coverage/lint/format commands enumerated, instrumented deferred per TS-01; `rules.apply.tdd: true` + test_command; `rules.verify` commands + `coverage_threshold: 80` (matches the Kover domain floor). `.atl/skill-registry.md` pre-existed from the original init; not rebuilt (out of this task's config-refresh scope).
- [x] 5.3 Scope-guard audit (AC8): term grep over the full `main..HEAD` text diff (32 tracked text files) — every `okhttp`/`retrofit`/`apiKey`/`Authorization`/`llama`/`gpt`/`openai`/`anthropic`/`onnx`/`tensorflow`/`tflite`/`huggingface` hit is self-referential documentation (the audit's own task text; AgentPort.kt's KDoc stating the absence of those concepts); zero hits in Kotlin sources, build files, or executable artifacts. `https://` only as documented provisioning/install URLs (1 in ci.yml, 2 in README), zero in `app/**` code. Dependency filter `:app:dependencies --configuration debugCompileClasspath` → zero AI/network artifact lines, exit 0.
- [x] 5.4 Rollback boundary in the PR description — CLOSED (slice 7): PR #6 (https://github.com/DANIELNAHUN/COLS/pull/6, `pr6/docs-reinit` → `pr5/ci-gate`) carries the prepared rollback text verbatim in its description. Prepared text retained below as the historical record of what was delivered.

### Rollback Boundary (prepared for the PR description — task 5.4)

The bootstrap change is additive except three modified files (`.gitignore`, `README.md`, `openspec/config.yaml`), restorable from `HEAD`:
- Rollback = delete the generated scaffold/CI files (everything under `app/`, `gradle/`, `gradlew*`, `.github/workflows/ci.yml`, `.editorconfig`) and `git checkout` the three modified files; `git clean -fd` clears untracked build output (`app/build/`, `.gradle/` are gitignored).
- No data migration exists. JDK/SDK installs live outside the repo and are uninstalled independently of it; the wrapper/catalog make the scaffold reproducible (regeneration is the recovery path, not manual surgery).

## Cumulative Task State

Total tasks: 40 — completed: 38, pending: 2 (0.2 machine-state cmdline-tools; 4.4 live PR run — the latter is now a hosted-infra blocker: the workflow is not on `main`, so no PR in this chain can show gate checks until the chain's first PRs merge; see Slice 7's evidence note).

### Phase 0: Environment Bootstrap (machine state)

- [x] 0.1 JDK 17 LTS verified: `java -version` → Temurin 17.0.20.1+1 (OpenJDK 17.0.20.1), on PATH.
- [ ] 0.2 Android SDK command-line tools — NOT DONE: no `cmdline-tools/` in the SDK root and no `sdkmanager` binary found on PATH or under `%LOCALAPPDATA%\Android` or `%ANDROID_HOME%`. Licenses evidence exists (`licenses/android-sdk-license`), but the cmdline-tools install itself is not evidenced. Left unchecked.
- [x] 0.3 SDK components verified present by filesystem inspection (deviation: `sdkmanager --list_installed` impossible without 0.2): `platform-tools/`, `platforms/android-37.0/`, `build-tools/36.0.0/`, `licenses/android-sdk-license`.
- [x] 0.4 `ANDROID_HOME` and `ANDROID_SDK_ROOT` both resolve to `C:\Users\danielnahun\AppData\Local\Android\Sdk`.
- [x] 0.5 Wrapper verified: `gradlew.bat --version` → Gradle 9.4.1 (build 2026-03-19), Launcher JVM 17.0.20.1 (Eclipse Adoptium), OS Windows 11 amd64.
- [x] 0.6 Wrapper-only entry point holds: every command in this slice ran via `gradlew.bat`; no standalone `gradle` invocation.

### Phase 1: Build Scaffold — committed Gradle skeleton (PR 1) — COMPLETE

- [x] 1.1 `gradle/libs.versions.toml` with the D2 pin set (AGP 9.2.1, Gradle wrapper 9.4.1, JVM 17, Compose BOM 2026.08.00, compileSdk 37, targetSdk 36, minSdk 26), each pin carrying a verification-date comment; full set re-verified as mutually compatible (re-check dated 2026-09-24 in the catalog header); non-critical plugin pins (Spotless 8.10.1, Kover 0.9.9) asserted by the first root build (plugin resolution succeeded). No build file hard-codes a version.
- [x] 1.2 `settings.gradle.kts`: plugin repos (google with group regex, mavenCentral, gradlePluginPortal), dependency repos (google, mavenCentral), `FAIL_ON_PROJECT_REPOS`, `rootProject.name = "COLS"`, NO module includes (ordering preserved for 2.1).
- [x] 1.3 Root `build.gradle.kts`: AGP + Kover `apply false`, Spotless applied at root with `kotlinGradle` ktlint targets on `*.gradle.kts`.
- [x] 1.4 `gradle.properties`: `org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8`, `android.useAndroidX=true`, `kotlin.code.style=official`.
- [x] 1.5 Wrapper committed (f83a58e): `gradle-wrapper.properties` distributionUrl pinned to `gradle-9.4.1-bin.zip`, `gradle-wrapper.jar` (48,966 bytes), `gradlew`, `gradlew.bat`.
- [x] 1.6 RED (threat matrix: executable integrity): initial staging reported `100644` for `gradlew` (`git ls-files -s gradlew` → `100644 739907df…`) — executable bit lost on Windows-first host, check FAILS as planned.
- [x] 1.7 GREEN: `git update-index --chmod=+x gradlew` → `git ls-files -s gradlew` now reports `100755` (`gradlew.bat` intentionally stays `100644`); distributionUrl pin remains 9.4.1.
- [x] 1.8 `.gitignore` Android baseline appended (`.gradle/`, `build/`, `local.properties`, `.idea/`, `*.iml`, `.externalNativeBuild/`, `.cxx/`, `.kotlin/`, `captures/`, `.DS_Store`); existing `.atl/` entry preserved on line 1.
- [x] 1.9 AC5 verified: `git check-ignore -v .atl build local.properties` returned matches for all three (`.atl/`, `build/`, `local.properties`); `git status` shows no `.gradle/` or build noise.

### Phase 2: `:app` Module + Smoke Screen + Agent Seam — slice for PR 2 (app scaffold buildable)

- [x] 2.1 `app/build.gradle.kts` reconciled from the cancelled dispatch's partial candidate and validated, then verified by build: applies only `libs.plugins.androidApplication` plus `libs.plugins.composeCompiler` — NOT `org.jetbrains.kotlin.android` (D3 confirmed against the Kotlin compose-compiler migration guide: with AGP ≥ 9.0 the `kotlin.android` plugin is dropped, while the `org.jetbrains.kotlin.plugin.compose` plugin is still applied per module and declared `apply false` at the root). Compose enablement: `buildFeatures { compose = true }` (AGP-9 pattern from the JetBrains AGP-9 migration skill material; no legacy `composeOptions`/`kotlinOptions` DSL). `compileSdk 37` / `minSdk 26` / `targetSdk 36`, namespace and applicationId `com.cols.launcher`, `JavaVersion.VERSION_17` compile options, `isIncludeAndroidResources = true` for the Robolectric/Compose-test slice — all versions resolve from the catalog (`compileSdk`/`minSdk`/`targetSdk` via `libs.versions.…get().toInt()`, no hard-coded numbers). JVM test deps declared: JUnit4, Robolectric, Compose test runtime (`ui-test-junit4` + `ui-test-manifest`), all from the catalog/BOM. Catalog gained the non-critical library rows previously deferred to this slice (activityCompose 1.13.0, junit 4.13.2, robolectric 4.16.1, composeCompiler plugin 2.3.10 matching AGP 9.2's built-in KGP); `include(":app")` added to `settings.gradle.kts`. Catalog comment in the cancelled draft cited "androidx versions page 2026-09-25"; not re-verified against a live source in this slice — recorded as an honesty note in Issues Found.
- [x] 2.2 `app/src/main/AndroidManifest.xml` (reconciled partial, validated): application with `android:label="@string/app_name"`, icon/roundIcon adaptive mipmaps, `android:theme="@style/Theme.COLS"`; single `MainActivity` (`com.cols.launcher.ui.MainActivity`, `exported="true"`) with the standard MAIN/LAUNCHER intent filter only. No `ROLE_HOME`, no `ROLE_DIALER`, no telecom permissions, no queries element (D7; manually re-audited against the file bytes).
- [x] 2.3 Minimal resources (reconciled partials, validated): `values/strings.xml` (`app_name`=COLS), `values/themes.xml` placeholder `Theme.COLS` parented on `android:Theme.Material.Light.NoActionBar` (documented placeholder: Compose owns theming; a real M3 XML theme is deferred), `values/colors.xml` (`ic_launcher_background`), `drawable/ic_launcher_foreground.xml` (vector inside the 108dp adaptive viewport, 66dp-safe-zone comment), `mipmap-anydpi-v26/ic_launcher.xml` + `ic_launcher_round.xml` (minSdk 26 → anydpi-v26 covers all supported devices, no legacy rasters). All referenced resource IDs resolve — the APK packaging tasks (`mergeDebugResources`/`processDebugResources`) succeeded.
- [x] 2.12 AC1 verified with a clean, bounded command: `.\gradlew.bat --no-daemon --console=plain :app:assembleDebug` → `BUILD SUCCESSFUL in 1m 20s`, 34 tasks executed, **exit code 0, process returned control**. APK produced: `app/build/outputs/apk/debug/app-debug.apk` (11,483,439 bytes, written 2026-09-25 14:11). The Gradle junk-report trap (silent hang after BUILD SUCCESSFUL) did NOT occur on this run; output alone was never trusted — completion was confirmed by process exit + echo of `$LASTEXITCODE`. Notably `compileDebugKotlin`/`compileDebugJavaWithJavac` ran NO-SOURCE (no Kotlin sources yet — they arrive with 2.4–2.11), and the former `platforms/android-37.0` concern from Phase 1 did not materialize: resource/manifest processing resolved the installed `android-37.0` platform directory without error. (Re-confirmed post-seam: `assembleDebug` green in 16s with all Kotlin sources compiled + dexed, exit 0.)

### Phase 4: CI PR Gate (slice 5, PR 5 / `pr5/ci-gate` — commit 6ccd392)

- [x] 4.1 `.github/workflows/ci.yml` created (77 insertions): `on: pull_request`, `ubuntu-latest`, `permissions: contents: read`; steps = checkout (SHA-pinned, `persist-credentials: false`) → wrapper exec-bit assert → wrapper-JAR validation → temurin JDK 17 → in-job SDK provisioning (bounded curl of Google `commandlinetools-linux-13114788-update.zip` + `unzip` into `${{ runner.temp }}/android-sdk` + `yes | sdkmanager --licenses` + `sdkmanager` install of exactly `platform-tools`, `platforms;android-37`, `build-tools;36.0.0`) → `./gradlew spotlessCheck` → `./gradlew :app:lintDebug` → `./gradlew :app:testDebugUnitTest` → `./gradlew :app:assembleDebug`. No emulator; Gradle only via the committed wrapper. Deviation from the D6 sketch: the design named no concrete SDK provisioning mechanism, so a bounded self-run shell step implements it (a third-party SDK setup action was rejected per D6's supply-chain rationale).
- [x] 4.2 Wrapper-integrity guard in the same commit: `git ls-files -s gradlew` must report `100755` (grep-asserted before any JDK/SDK spend; failure emits a `::error::` annotation + fix hint); wrapper JAR checksums validated by `gradle/actions/wrapper-validation` pinned to SHA `9c971963bec38e04b3d30dcc455b5382be2fdbfb` (= tag `v6`; the annotated tag object `4733eaac…` → commit `9c971963…` chain was re-resolved twice via the GitHub API on 2026-09-26 after one earlier read returned a differently-shaped/stale payload — pinned only after the repeat fetch confirmed it; a decoy lookalike SHA `d990644…` circulating in local dotfiles was discarded because it does not resolve). First-party pins: checkout `fbc6f39…` = v5, setup-java `b6effb0…` = v5 (both SHA→major verified; 40-char length re-checked by regex).
- [x] 4.3 Scenario checks verified locally against the committed file (live cold-runner execution is inherently the CI host's job and is covered by 4.4): (1) `pull_request` trigger + `ubuntu-latest` + JDK `17` in workflow, and catalog `jvm = "17"` confirmed — matched-pair equality; (2) exactly 4 `run: ./gradlew …` invocations and 0 bare `gradle …` invocations; (3) emulator/avd/adb-shell string search over the file: 1 match, the header comment stating "NO emulator"; (4) provisioning is self-contained in-job with a size check (`[ -s … ]`) after download, licenses accepted in-job, and the installed component set matching the tasks-0.3 filesystem-verified list. YAML sanity: `on:`/steps/job keys verified by structural grep; file is UTF-8 clean, no BOM, LF endings in the index (`.git/info/attributes` local patch `*.yml text eol=lf` neutralized the transcode-on-stage hazard; the forward-looking `.gitattributes` fix is recorded below).
- [ ] 4.4 Live green run on the bootstrap PR (AC4) — STILL OPEN with recorded unavailable-evidence: the chain is now pushed and PRs #1–#6 open, but a hosted-gate check on `pr6/docs-reinit` returned exactly `no checks reported on the 'pr6/docs-reinit' branch`; GitHub Actions permissions are enabled while the workflow is NOT present on `main` (0 registered workflows / 0 runs, Actions API). Root cause: no workflow on the default branch → no PR in this chain can trigger `ci.yml`. Expected evidence remains: all gate steps report explicit pass/fail on the PR check list, per step names in ci.yml — only obtainable after PR #1 (which introduces ci.yml to `main`) merges and a run actually executes. Not a local-runtime substitute, not derivable from local verification alone.
- Note: the `.gitattributes`-visible fix for YAML files (`*.yml text eol=lf`) was NOT committed in this slice to keep the work-unit boundary exact (attributes changes affect every `yml` blob, not just this one); it belongs with Phase 5's docs/repo-hygiene slice alongside the existing `.gitattributes`.

### Slice 3 (work unit 3, PR 3 / `pr3/agent-seam`, commit cfbe556): agent seam

- [x] 2.4 RED captured: `.\gradlew.bat --no-daemon --console=plain :app:testDebugUnitTest` with ONLY the tests + `AgentAction.kt` present (no domain/data implementation) → `Task :app:compileDebugUnitTestKotlin FAILED`, **exit 1**, `Unresolved reference 'ConfirmationPolicy' / 'AgentActionGateway' / 'InMemoryAgentActionGateway' / 'perform' / 'ActionExecutionResult'` (compile-fail form of RED, the expected failure mode when the implementation does not exist). Written-first evidence: `ConfirmationPolicyTest.kt` (3 tests) and `AgentActionGatewayTest.kt` (4 tests) authored before any main-source implementation of policy/gateway existed.
- [x] 2.5 `domain/agent/AgentPort.kt`: `AgentPort` (`suspend fun respondTo(request): AgentOutcome`), `AgentRequest(utterance)`, `AgentOutcome` sealed (`Answer(text)` / `ActionProposal(action, confirmation)`), `Confirmation(id)`. Provider-agnostic: no vendor/model/key/transport concept in any declaration (achieved, matches the D5 contract verbatim).
- [x] 2.6 `domain/agent/AgentActionGateway.kt`: `AgentActionGateway` (`suspend fun perform(action, confirmation?): ActionExecutionResult`) + `ActionExecutionResult` sealed (`ConfirmationRequired` object / `Executed(receipt)`). Domain rule stated on the interface: no valid confirmation, no execution.
- [x] 2.7 `domain/agent/ConfirmationPolicy.kt`: `ConfirmationPolicyContract` (`issueFor` / `isValid` / `consume`) + `ConfirmationPolicy` implementation. Tokens are single-use (`consume` burns them, called by the gateway at execution time) and bound to the issuing action (`isValid` compares the bound action). Lives in domain, not UI.
- [x] 2.8 GREEN: `data/agent/InMemoryAgentActionGateway.kt` refuses execution when `isValid` fails (returns `ConfirmationRequired`, action NOT executed — inspectable via `executed` list), burns the token on success (single-use), returns a deterministic receipt. Re-run: `testDebugUnitTest` → `BUILD SUCCESSFUL`, **exit 0**, suites: AgentActionGatewayTest 4/0, ConfirmationPolicyTest 3/0. **AC2 RED→GREEN loop closed.**
- [x] 2.9 `data/agent/InMemoryAgentAdapter.kt`: deterministic `AgentPort` stub — canonical action proposal when the utterance contains a call keyword, fixed answer otherwise; constructor takes ONLY the `ConfirmationPolicyContract` (no network/file/credential type anywhere). Plus `data/agent` test `InMemoryAgentAdapterTest` (3 tests): deterministic answer, canonical proposal + confirmation id, repeated-call determinism. Suite state: 10 tests total, 0 failures, exit 0.
- [x] 2.10 `ui/PlaceholderScreen.kt`: Material 3 `Scaffold` + `Text("COLS")` title + one `Button` at `48.dp` height (codified RQ3 floor; 64dp senior floor documented as future product decision) with `semantics { contentDescription }` label. No launcher/call/contacts/caregiver feature.
- [x] 2.11 `ui/MainActivity.kt`: composition root binding `AgentPort → InMemoryAgentAdapter(ConfirmationPolicy())` and `AgentActionGateway → InMemoryAgentActionGateway(ConfirmationPolicy())`; `setContent { MaterialTheme { PlaceholderScreen } }`; no DI framework. The tap smoke path calls the port but deliberately does NOT confirm/execute any gateway action (scope guard preserved).

### Slice 4 (work unit 4, PR 4 / `pr4/quality-loop`, commit a1cc252): JVM-first quality loop

- [x] 3.1 `AndroidFrameworkTest.kt` (2 tests): Robolectric exercises REAL framework behavior on the JVM — application context built from the merged manifest (`packageName = com.cols.launcher`) and resource resolution (`R.string.app_name` = "COLS"). **Highest-verified-sdk finding**: Robolectric 4.16.x supports up to API 36 per its docs, but the SDK-36 sandbox requires Java 21 while the catalog pins `jvm = "17"` (D2) — first run failed with `Failed to create a Robolectric sandbox: Android SDK 36 requires Java 21 (have Java 17)`. SDK 35 verified empirically: `@Config(sdk = [35])` is the honest highest-verified value on this toolchain (deviation from the design's `[<highest verified>]` intent resolved by evidence, not by assumption).
- [x] 3.2 `PlaceholderScreenSmokeTest.kt` (3 tests): Compose test runtime on Robolectric asserting the codified RQ3 floor — title `COLS` displayed, interactive element carries `contentDescription = "Placeholder action button"`, and `SemanticsNode.touchBoundsInRoot` is ≥ `48.dp.toPx()` in BOTH dimensions (density-consistent comparison). **Implementation finding**: `createComposeRule()` fails on Robolectric PR 4736 semantics ("Unable to resolve activity ... androidx.activity.ComponentActivity" — the synthetic ui-test-manifest activity has no intent filter); the test launches the REAL `MainActivity` via `createAndroidComposeRule(MainActivity)` instead, which resolves (MAIN/LAUNCHER declared) and hosts the placeholder through the actual composition root.
- [x] 3.3 Kover 0.9.9 wired in `app/build.gradle.kts`: `kover { reports { total { filters { includes { packages("com.cols.launcher.domain") } }; verify { rule("domain line coverage floor") { minBound(80, CoverageUnit.LINE, AggregationType.COVERED_PERCENTAGE) } } } } }` — the gate measures the `domain` package ONLY; `ui`, `data`, and generated code are NOT measured; no other bound exists (TS-01 / no global threshold). DSL verified against the actual plugin-jar bytecode before use (`CoverageUnit`, NOT the pre-0.9 `MetricType`). Measured domain LINE coverage: 25 covered / 1 missed = **96% ≥ 80%** (`koverVerify` green). `:app:koverXmlReport`/`:app:koverHtmlReport` ran post-suite; `app/build/reports/kover/report.xml` + `html/index.html` produced.
- [x] 3.4 `spotlessCheck` green over ALL Kotlin sources: `:app` now applies Spotless (`kotlin { target("src/**/*.kt") ktlint() }`); root `kotlinGradle` extended to cover `app/*.gradle.kts`; `.editorconfig` added with the ktlint-documented Compose integration (`ktlint_function_naming_ignore_when_annotated_with = Composable` — ktlint otherwise flags the PascalCase composable). First `spotlessApply` reformatted existing sources (trailing commas, chain-call style, comment reflow — formatting only, no semantic change); `spotlessCheck` → BUILD SUCCESSFUL, exit 0.
- [x] 3.5 AC2 workspace-command semantics verified with an honest deliberate-break cycle: GREEN `:app:testDebugUnitTest` → exit 0 (15 tests / 0 failures); deliberate break (flipped the `null confirmation is never valid` assertion) → **exit 1** with the failing test identified by name: `ConfirmationPolicyTest > null confirmation is never valid FAILED`; restored → **exit 0** again. Full combined gate `:app:testDebugUnitTest :app:koverVerify spotlessCheck` → BUILD SUCCESSFUL, exit 0.

## Work Unit Evidence (Hard Gate)

Work unit 1 — "Toolchain-ready Gradle skeleton" (PR 1 / `pr1/gradle-skeleton`):

| Evidence | Required value |
|---|---|
| Focused test command and exact result | `.\gradlew.bat --version` → Gradle 9.4.1, Launcher JVM 17.0.20.1, exit 0; `git ls-files -s gradlew` RED→GREEN: `100644` (staged initial) → `100755` (after `git update-index --chmod=+x gradlew`); `git check-ignore -v .atl build local.properties` → all three matched, exit 0 |
| Runtime harness command/scenario and exact result | `.\gradlew.bat help` → BUILD SUCCESSFUL in 1s, exit 0 — settings + root build + version catalog evaluate and plugin aliases (AGP 9.2.1, Kover 0.9.9, Spotless 8.10.1) resolve from the plugin portals; `.\gradlew.bat spotlessCheck` → initially FAILED (CRLF line-ending violations in `build.gradle.kts`/`settings.gradle.kts` under `core.autocrlf=true`), then `spotlessApply` + re-check → BUILD SUCCESSFUL, exit 0, after adding `.gitattributes` LF policy. N/A remainder: no runtime app boundary exists yet (`:app` module lands with work unit 2), and `./gradlew help`/`spotlessCheck` via the wrapper IS the real integration path for this unit |
| Rollback boundary | Commit f83a58e is self-contained: delete `settings.gradle.kts`, root `build.gradle.kts`, `gradle.properties`, `gradle/` (catalog + wrapper), `gradlew`, `gradlew.bat`, `.gitattributes`; restore `.gitignore` from `HEAD` (c16a93d). No other file depends on these yet; `include(":app")` not yet added |

Work unit 2 — "`:app` module scaffolding compiles and assembles" (PR 2 / `pr2/app-module` — this slice; commit 7b59caf, single work unit):

| Evidence | Required value |
|---|---|
| Focused test command and exact result | `.\gradlew.bat --no-daemon --console=plain :app:assembleDebug` → `BUILD SUCCESSFUL in 1m 20s`, 34 actionable tasks executed, exit code 0, process returned control promptly. No dedicated unit exists for a smaller focus command here — this IS the smallest command proving the unit (module compiles, resources/manifest merge, APK packages) |
| Runtime harness command/scenario and exact result | Same command is the real integration/runtime path: produced `app/build/outputs/apk/debug/app-debug.apk` (11,483,439 bytes, 2026-09-25 14:11) — a packaged debug APK is the runtime artifact this work unit owes; install/launch on a device is out of scope for the scaffold (project-scaffold spec keeps the render check to `assembleDebug` + the later Compose smoke test) |
| Rollback boundary | Delete `app/` (build file, manifest, res — `app/build/` is gitignored output), remove `include(":app")` from `settings.gradle.kts`, drop the newly added [libraries] rows + `composeCompiler` plugin alias from `gradle/libs.versions.toml`, drop the `composeCompiler apply false` line from root `build.gradle.kts`. PR 1 files (wrapper, gradle.properties, `.gitignore`, `.gitattributes`) untouched |

Work unit 3 — "Agent seam: domain contracts + in-memory adapters + confirmation-rule RED→GREEN tests + composition root" (PR 3 / `pr3/agent-seam`; commit cfbe556, single work unit):

| Evidence | Required value |
|---|---|
| Focused test command and exact result | `.\gradlew.bat --no-daemon --console=plain :app:testDebugUnitTest` — RED run (2.4, pre-implementation): `:app:compileDebugUnitTestKotlin FAILED`, exit 1, unresolved references to the seam names. GREEN run (2.8): `BUILD SUCCESSFUL`, exit 0, 7 tests / 0 failures (AgentActionGatewayTest 4, ConfirmationPolicyTest 3). Final state (2.9): `BUILD SUCCESSFUL`, exit 0, 10 tests / 0 failures across 3 suites (+ InMemoryAgentAdapterTest 3) — verified from `app/build/test-results/testDebugUnitTest/*.xml` suite attributes, not console output alone |
| Runtime harness command/scenario and exact result | `.\gradlew.bat --no-daemon --console=plain :app:assembleDebug` after the seam landed → `BUILD SUCCESSFUL in 16s`, exit 0 — `compileDebugKotlin` + `dexBuilderDebug` executed over the new Kotlin sources, APK still produced. Device install/launch remains out of scaffold scope (unchanged from work unit 2); the JVM unit suite IS this unit's runtime path for the rule, per the app-testing spec JVM-first pyramid |
| Rollback boundary | Revert commit cfbe556 on `pr3/agent-seam`: removes exactly the 8 main files under `app/src/main/java/com/cols/launcher/{domain,data,ui}/` + the 3 test files — nothing else references them; no shared file touched (no catalog/build-file/manifest change in this slice). PR 2 files (7b59caf) and PR 1 files (f83a58e) untouched |

Work unit 4 — "Quality loop: Robolectric framework test + Compose smoke test + Kover domain gate + Spotless/ktlint over all Kotlin sources + AC2 deliberate-break verification" (PR 4 / `pr4/quality-loop` — this slice; commit a1cc252, single work unit):

| Evidence | Required value |
|---|---|
| Focused test command and exact result | `.\gradlew.bat --no-daemon --console=plain :app:testDebugUnitTest :app:koverVerify spotlessCheck` → `BUILD SUCCESSFUL in 47s`, **exit 0**; test XML: 15 tests / 0 failures across 5 suites (AndroidFrameworkTest 2, InMemoryAgentAdapterTest 3, AgentActionGatewayTest 4, ConfirmationPolicyTest 3, PlaceholderScreenSmokeTest 3); domain LINE coverage 25/26 = 96% ≥ 80% gate. AC2 semantics: deliberate break → exit 1 with `ConfirmationPolicyTest > null confirmation is never valid FAILED` identified; restore → exit 0 again |
| Runtime harness command/scenario and exact result | `PlaceholderScreenSmokeTest` launches the REAL `MainActivity` through `createAndroidComposeRule` on Robolectric SDK 35 — a genuine activity-launch + Compose-render path on the JVM with no emulator (title rendered, 48dp touch bounds and semantics label asserted on the live semantics tree). Device/emulator install remains out of bootstrap scope per the app-testing spec (Robolectric IS the JVM integration layer); `:app:koverVerify` additionally compiled and exercised the release variant's test path |
| Rollback boundary | Revert commit a1cc252 on `pr4/quality-loop`: removes the 2 new test files, the `.editorconfig`, the Kover + `:app`-Spotless blocks from `app/build.gradle.kts`, and the root `kotlinGradle` target tweak; restores the pre-slice formatting of the 11 reformatted source files (formatting-only). No main-source behavior changed; no catalog, manifest, or wrapper file touched. PR 3 (cfbe556), PR 2 (7b59caf), PR 1 (f83a58e) untouched |

Work unit 6 — "Phase 5: README dev-environment docs + sdd-init re-run (strict TDD) + scope-guard audit + YAML line-ending hygiene" (PR 6 / `pr6/docs-reinit` — this slice):

| Evidence | Required value |
|---|---|
| Focused test command and exact result | `.\gradlew.bat --no-daemon --console=plain :app:testDebugUnitTest :app:koverVerify spotlessCheck` → `BUILD SUCCESSFUL in 28s`, exit 0 (15 tests / 0 failures; domain koverVerify ≥80%; ktlint clean) — repo proven still green after the README/config/.gitattributes edits. Scope audit: term grep over the full `main..HEAD` text diff → zero code hits (all term matches are self-referential documentation in tasks.md/AgentPort.kt KDoc); `:app:dependencies --configuration debugCompileClasspath` filtered for AI/network artifact groups → zero matching lines, exit 0 |
| Runtime harness command/scenario and exact result | N/A — no runtime boundary exists for a documentation/config slice: `README.md`, `openspec/config.yaml`, and `.gitattributes` are never executed at runtime; the change's runtime path (unit suite + Kover + Spotless) is unchanged and re-proven green by the focused command above |
| Rollback boundary | Revert the slice's commits on `pr6/docs-reinit`: `README.md` returns to the 2-line stub, `.gitattributes` loses the one `*.yml` line, `openspec/config.yaml` returns to the pre-re-init state — no other file touched; nothing generated depends on these three files |

## Mode Resolution

Standard Mode — `openspec/config.yaml` reports `strict_tdd: false`. No threat-matrix RED tests were skipped: the one applicable row (executable integrity) was executed as RED→GREEN per 1.6/1.7. Work unit 2's own hard-gate evidence table is above; `assembleDebug` with no Kotlin sources is the honest upper bound of what that slice could prove. Work unit 3 closed the first real test loop: the task-2.4 RED requirement (explicitly dispatched by the orchestrator) was honored as written-first tests → captured failing run (compile-fail RED) → implementation → green run (AC2 evidence recorded in Phase 2 Slice 3 above). Work unit 4 is verification-tooling work (tests/config only, no production behavior), so its hard-gate table records the focused command, the runtime path (real activity launch on Robolectric), and the rollback boundary above; the AC2 deliberate-break cycle (3.5) is the slice's own RED→GREEN evidence.

## Files Changed in Slice 4 (work unit 4, PR 4 — commit a1cc252, 16 tracked files, 279 insertions / 82 deletions)

| File | Action | What Was Done |
|------|--------|---------------|
| `app/src/test/java/com/cols/launcher/AndroidFrameworkTest.kt` | Created | Robolectric framework test (3.1): application context from merged manifest + resource resolution, `@Config(sdk = [35])` (highest verified on JDK 17) |
| `app/src/test/java/com/cols/launcher/ui/PlaceholderScreenSmokeTest.kt` | Created | Compose smoke test (3.2): title displayed, semantics label, `touchBoundsInRoot` ≥ 48dp both axes |
| `app/build.gradle.kts` | Modified | Applied Kover + Spotless plugins; domain-only `koverVerify` rule (≥80% LINE, total variant, domain includes-filter); `spotless { kotlin { target("src/**/*.kt") ktlint() } }` (3.3/3.4) |
| `build.gradle.kts` | Modified | Root `kotlinGradle` Spotless target extended to `app/*.gradle.kts` (3.4) |
| `.editorconfig` | Created (deviation addition) | ktlint Compose integration: `ktlint_function_naming_ignore_when_annotated_with = Composable` (3.4) |
| `app/src/main/java/com/cols/launcher/{domain,data,ui}/*.kt` (8 files) | Modified | `spotlessApply` reformatting ONLY — trailing commas, chain-call style, comment reflow; zero semantic change (3.4) |
| `app/src/test/java/com/cols/launcher/{domain,data}/*Test.kt` (3 files) | Modified | `spotlessApply` reformatting ONLY (3.4) |

Authored tracked diff: 279 insertions / 82 deletions (361 changed lines) — within the 400-line review budget. Of the 82 deletions, ~80 are the formatting-only reflow of existing lines; new authored content ≈ 210 lines (2 test files 112 + build config ~90 + editorconfig 8). No code was compressed, deleted, or restyled to fit the budget.

## Commit Identity (this slice, work unit 4)

- Work-unit commit: **a1cc252** — `test(app): close the JVM-first quality loop with Robolectric, Compose smoke, Kover gate, and ktlint` on branch `pr4/quality-loop` (stacked-to-main chain; base = `pr3/agent-seam`). Not pushed; PR creation is the orchestrator's/user's call (interactive pace). Authored diff: 279 insertions / 82 deletions, 16 files, single work unit.
- SDD artifact updates (`tasks.md` checkboxes 3.1–3.5; this apply-progress merge, which also commits the slice-3 artifact edits left uncommitted by the previous dispatch) are committed as a separate `docs(openspec)` commit per the 858a0e4 precedent.

## Files Changed in Slice 3 (work unit 3, PR 3 — commit cfbe556, 11 files, 443 insertions / 0 deletions)

| File | Action | What Was Done |
|------|--------|---------------|
| `app/src/main/java/com/cols/launcher/domain/agent/AgentPort.kt` | Created | Provider-agnostic port + `AgentRequest`/`AgentOutcome`/`Confirmation` (2.5) |
| `app/src/main/java/com/cols/launcher/domain/agent/AgentActionGateway.kt` | Created | Consequential-action boundary + `ActionExecutionResult` model (2.6) |
| `app/src/main/java/com/cols/launcher/domain/agent/ConfirmationPolicy.kt` | Created | Single-use, action-bound confirmation tokens; `consume` for single-use enforcement (2.7) |
| `app/src/main/java/com/cols/launcher/domain/agent/AgentAction.kt` | Created | `AgentAction(kind, target)` generic action model supporting the contract |
| `app/src/main/java/com/cols/launcher/data/agent/InMemoryAgentActionGateway.kt` | Created | Gateway adapter refusing execution without a valid confirmation; burns tokens; deterministic receipt (2.8) |
| `app/src/main/java/com/cols/launcher/data/agent/InMemoryAgentAdapter.kt` | Created | Deterministic in-memory `AgentPort` stub; no network/file/credential dependency (2.9) |
| `app/src/main/java/com/cols/launcher/ui/PlaceholderScreen.kt` | Created | M3 smoke screen, 48dp interactive element + semantics label (2.10) |
| `app/src/main/java/com/cols/launcher/ui/MainActivity.kt` | Created | Composition root: port/gateway bindings, `setContent` host, no DI (2.11) |
| `app/src/test/java/com/cols/launcher/domain/agent/ConfirmationPolicyTest.kt` | Created | RED-first policy tests: issue, validate, null refused (2.4) |
| `app/src/test/java/com/cols/launcher/domain/agent/AgentActionGatewayTest.kt` | Created | RED-first gateway tests: no-confirmation refused, valid executes, reuse refused, foreign refused (2.4) |
| `app/src/test/java/com/cols/launcher/data/agent/InMemoryAgentAdapterTest.kt` | Created | Stub-contract tests: deterministic answer, canonical proposal + token, repeated-call determinism (2.9) |

Authored tracked diff: 443 insertions / 0 deletions, all in `app/src/` — exceeds the single-PR 400-line budget by ~11%. One honest slicing pass was performed (chained-pr bounded-slicing rule): the slice IS the smallest cohesive agent-seam work unit — contracts, adapters, their tests, and the composition root form one deliverable; tests cannot be split from the behavior they verify, and dropping `MainActivity`/`PlaceholderScreen` would leave the seam unwired per tasks 2.10/2.11. Recommendation for the orchestrator: accept PR 3 as `size:exception` (443 authored lines) or split `ui/` wiring off into a follow-up PR. No code compression was performed and none will be.

## Commit Identity (this slice, work unit 3)

- Work-unit commit: **cfbe556** — `feat(agent): add provider-agnostic agent seam with confirmation-gated actions` on branch `pr3/agent-seam` (stacked-to-main chain; base = `pr2/app-module`). Not pushed; PR creation is the orchestrator's/user's call (interactive pace). Authored diff: 443 insertions / 0 deletions, 11 files, single work unit.
- SDD artifact updates (`tasks.md` checkboxes 2.4–2.11; this apply-progress merge) are intentionally uncommitted working-tree edits — prior slices in this repo committed them as separate `docs(openspec)` commits (858a0e4 precedent), and the dispatch reserves commit rights to the single work-unit commit for this batch.

## Files Changed in Slice 2 (work unit 2, PR 2 — commit 7b59caf, 11 tracked files, 158 insertions / 5 deletions)

<details><summary>Phase 1 slice file table (commit f83a58e — preserved from previous apply-progress)</summary>

| File | Action | What Was Done |
|------|--------|---------------|
| `gradle/libs.versions.toml` | Created (reconciled) | Version catalog, D2 pin set + verification comments |
| `settings.gradle.kts` | Created (reconciled) | Repositories, FAIL_ON_PROJECT_REPOS, no includes yet |
| `build.gradle.kts` | Created (reconciled) | Root plugins (AGP/Kover apply false, Spotless applied) + ktlint config |
| `gradle.properties` | Created (reconciled) | JVM args, AndroidX, Kotlin code style |
| `gradle/wrapper/gradle-wrapper.jar` | Created (reconciled) | Generated wrapper JAR, committed |
| `gradle/wrapper/gradle-wrapper.properties` | Created (reconciled) | distributionUrl pinned to Gradle 9.4.1 |
| `gradlew` | Created (reconciled) | Generated POSIX wrapper, staged mode 100755 (D8) |
| `gradlew.bat` | Created (reconciled) | Generated Windows wrapper |
| `.gitignore` | Modified | Android build-ignore baseline appended; `.atl/` preserved |
| `.gitattributes` | Created (deviation addition) | LF policy for `.kts`/`.kt`/`.toml`/`.properties`/`gradlew` so the Spotless gate survives fresh Windows clones (`core.autocrlf=true`) |

Authored line count ≈ 108 (catalog 35, settings 29, root build 17, gradle.properties 8, .gitignore +10, .gitattributes 9); generated wrapper scripts/JAR excluded per the tasks forecast rule. Within the 400-line review budget.
</details>

## Files Changed in Slice 2 (work unit 2, PR 2 — commit 7b59caf, 11 tracked files, 158 insertions / 5 deletions)

| File | Action | What Was Done |
|------|--------|---------------|
| `app/build.gradle.kts` | Created (reconciled) | `:app` module build: AGP application plugin + Compose Compiler plugin (no `kotlin.android`), Compose enabled via `buildFeatures`, catalog-resolved SDK levels/namespace/JVM-17 compile options, `isIncludeAndroidResources`, JVM test stack (JUnit4 + Robolectric + Compose test runtime via BOM) |
| `app/src/main/AndroidManifest.xml` | Created (reconciled) | Launcher-only manifest: application label/icon/theme + `MainActivity` MAIN/LAUNCHER filter; no roles, no telecom permissions |
| `app/src/main/res/values/strings.xml` | Created (reconciled) | `app_name` = COLS |
| `app/src/main/res/values/themes.xml` | Created (reconciled) | Placeholder `Theme.COLS` (Material/NoActionBar parent; documented as placeholder, Compose owns theming) |
| `app/src/main/res/values/colors.xml` | Created (reconciled) | Adaptive-icon background color |
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | Created (reconciled) | Vector ring inside the adaptive-icon safe zone |
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` | Created (reconciled) | Adaptive launcher icon (anydpi-v26, minSdk 26) |
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` | Created (reconciled) | Round adaptive launcher icon |
| `gradle/libs.versions.toml` | Modified | Added deferred non-critical library rows (activityCompose, junit, robolectric) + BOM-backed Compose library aliases + `composeCompiler` plugin alias (2.3.10 = AGP 9.2 built-in KGP) with provenance comments |
| `settings.gradle.kts` | Modified | Updated module comment + `include(":app")` (task 2.1 requirement) |
| `build.gradle.kts` | Modified | Added `alias(libs.plugins.composeCompiler) apply false` at the root per the compose-compiler migration guide |

`app/build/**` is gitignored generated output (assembled APK + intermediates) and is excluded from the commit. Authored tracked diff: 158 insertions / 5 deletions; well within the 400-line review budget. No file was deleted; no document content was compressed or trimmed.

## Commit Identity (slice 2, work unit 2)

- Work-unit commit: **7b59caf** — `feat(app): scaffold buildable :app module with Compose and launcher-only manifest` on branch `pr2/app-module` (stacked-to-main chain; PR 2 slice). Not pushed; PR creation is the orchestrator's/user's call (interactive pace).
- SDD artifact updates (`tasks.md` checkboxes 2.1–2.3, 2.12; the slice-2 apply-progress merge) were committed as `docs(openspec)` working-tree edits by that slice per the 858a0e4 precedent.

## Commit Identity (slice 6, work unit 6)

- Work-unit commits on branch `pr6/docs-reinit` (stacked-to-main chain; base = `pr5/ci-gate`), one deliverable scope in three conventional commits:
  - **e5d60fe** — `docs(readme): add clean-machine development environment guide` (task 5.1; 99 insertions / 1 deletion)
  - **dbb9e60** — `chore(gitattributes): pin YAML working-tree endings to LF` (slice-5 deferred repo hygiene; +1 line)
  - **802e4cb** — `chore(sdd): enable strict TDD and refresh workspace testing context` (task 5.2, the sdd-init re-run artifact; 34 insertions / 25 deletions)
- Authored diff for the slice: 134 insertions / 26 deletions (3 files) — well within the 400-line review budget. No code, comments, tests, or docs were compressed or deleted to fit the budget.
- Not pushed; PR creation is the orchestrator's/user's call (interactive pace + remote not authorized to this executor). The SDD artifact updates (tasks.md checkboxes 5.1–5.3 + 5.4 blocked note; this apply-progress merge) follow as the `docs(openspec)` commit per the 858a0e4 precedent.

## Files Changed in Slice 6 (work unit 6, PR 6 — commits e5d60fe + dbb9e60 + 802e4cb)

| File | Action | What Was Done |
|------|--------|---------------|
| `README.md` | Modified | "Development environment" section (task 5.1): wrapper-only entry, JDK 17, SDK components + licenses, `ANDROID_HOME` (Windows/CI), first-build/test + quality-gate commands, toolchain-pins contract (AC6) |
| `.gitattributes` | Modified | Added `*.yml text eol=lf` — the slice-5 deferred fix so committed YAML (ci.yml) survives `core.autocrlf=true` clones |
| `openspec/config.yaml` | Modified | sdd-init re-run (task 5.2): `strict_tdd: true`, refreshed `context`/`testing`, workspace test command set, `rules.apply.tdd: true`, verify commands + `coverage_threshold: 80` (AC9) |

## Deviations from Design

1. **`.gitattributes` added (not in the design file list)** — required to make `spotlessCheck` durable on this Windows host (`core.autocrlf=true` regenerates CRLF on every clone; ktlint requires LF). Same threat-matrix family as D8 (Windows-first-host repo integrity). Design files otherwise unchanged.
2. **0.3 verified by filesystem inspection instead of `sdkmanager --list_installed`** — `cmdline-tools` is not installed (0.2 left unchecked).
3. **`platforms/android-37.0` naming** — the installed platform directory uses the new minor-version style (`android-37.0`) rather than `android-37`. Flagged in Phase 1 as a work-unit-2 risk; now RESOLVED by evidence: `:app:assembleDebug` passed with the installed `android-37.0` directory (resource/manifest processing completed cleanly).
4. **Theme placeholder uses `android:Theme.Material.Light.NoActionBar` (platform theme), not a Material 3 XML theme** — the design says "Material 3 theme placeholder"; with a Compose-only screen a real M3 XML theme would require the `com.google.android.material` dependency, which the bootstrap does not declare. The XML theme is a window/NoActionBar host and Compose's `MaterialTheme` (added with the placeholder screen in 2.10) provides the M3 layer. Documented in the file's comment; flagged as a judgment call for review.
5. **Root `build.gradle.kts` gained `composeCompiler apply false`** — not in the design's file-change table text, but it is the documented setup from the Kotlin compose-compiler migration guide (root declares, module applies) and keeps plugin resolution unambiguous with `FAIL_ON_PROJECT_REPOS`/portal-driven resolution.
6. **Slice 4: Robolectric pinned to SDK 35, not 36/37** — the design's open question #1 anticipated SDK-36/37 shadow uncertainty; the empirical result is sharper: Robolectric 4.16.x supports up to API 36, but the SDK-36 sandbox requires Java 21 while the catalog pins `jvm = "17"` (D2). `@Config(sdk = [35])` is the highest verified value on this toolchain, recorded in the test's KDoc. Resolving this differently (Java 21 test JVM) would break the catalog/CI JDK-pin contract, so it was not attempted.
7. **Slice 4: Compose smoke test launches the real `MainActivity` (`createAndroidComposeRule`) instead of `createComposeRule()` + `setContent`** — Robolectric PR 4736 makes `ActivityScenario` strict: the synthetic `androidx.activity.ComponentActivity` supplied by `ui-test-manifest` carries no intent filter and is refused ("Unable to resolve activity"). The real `MainActivity` is declared with MAIN/LAUNCHER and hosts the placeholder through its composition root, so the assertions run against the actual app screen — arguably stronger than the design's `createComposeRule` phrasing; behavior identical.
8. **Slice 4: `.editorconfig` added (not in the design file list)** — ktlint's `standard:function-naming` rule rejects the PascalCase Composable (`PlaceholderScreen`); the ktlint-documented Compose integration is this editorconfig option. Same Windows/repo-integrity family as the `.gitattributes` deviation.
9. **Slice 4: `deprecation warnings` on `createComposeRule`/`createAndroidComposeRule`** — Compose 1.12 deprecates the v1 test-rule factories in favor of `androidx.compose.ui.test.junit4.v2.*` (StandardTestDispatcher semantics). The v1 APIs are used deliberately in the bootstrap (documented, stable, and the migration guide's v2 dispatcher semantics are a behavioral change not in this change's scope); noted for a later change.

## Issues Found

1. `cmdline-tools`/`sdkmanager` absent — task 0.2 remains open; future SDK management has no CLI path until it is installed.
2. Spotless initially failed on CRLF — resolved durably via `.gitattributes`; evidence recorded above.
3. Cancelled-dispatch partial files were reconciled in place and retained (all valid; no partial file was deleted).
4. **Catalog provenance honesty note**: the cancelled draft's `libs.versions.toml` comment cites "androidx versions page (2026-09-25)" and pins `activityCompose = 1.13.0`, `junit = 4.13.2`, `robolectric = 4.16.1` — `robolectric`/`junit` match the design notes, but the `activityCompose` pin was NOT re-verified against a live source in this bounded slice, and no group/accessor is currently exercised by any source file (no Kotlin sources exist yet). The pins are argued by the successful first build only at the dependency-resolution level. Task 2.1 asserts catalog correctness "with approved catalog pins" as dispatched; the re-verification debt is noted here and belongs with tasks 2.4+ (first real consumers) or verify phase.
5. The dispatched "Gradle may hang after BUILD SUCCESSFUL" trap did not occur: `--no-daemon` + bounded timeout returned exit 0 in ~80s. `org.gradle.jvmargs` caused a single-use daemon fork notice (expected with `--no-daemon`; not a hang).
6. **Slice 4 encoding gotcha**: PowerShell 5.1 `Set-Content` round-trips re-encoded two UTF-8 test files to ANSI (em-dashes became U+FFFD) and switched them to CRLF, which `spotlessCheck` then flagged. Files were rewritten ASCII-clean via the file tool; `spotlessApply` + `spotlessCheck` confirmed. Lesson recorded: never round-trip artifact files through PS 5.1 default encoding on this host.
7. **Slice 4 Compose-test runtime cost**: the 3-test smoke suite takes ~17s on Robolectric (first Compose render + GC on the JVM); total suite still exits in <50s. Acceptable for the JVM-first loop; instrumented-layer cost is out of bootstrap scope.
8. `androidx.test:core` / `androidx.test.ext:junit` are used by the new tests via `ui-test-junit4`'s transitive dependencies rather than explicit catalog rows — noted deliberately: declaring them explicitly would require choosing unverified version pins, which the honesty rule forbids asserting without a live source; the build resolves them deterministically through the pinned BOM/POM graph.

## Remaining Tasks (next slice: Phase 4 CI gate, then Phase 5 docs/re-init)

- [x] 2.4–2.11 **DONE (slice 3, commit cfbe556)** — RED→GREEN closed (AC2), seam + stub + screen + wiring in place, 10 tests green
- [x] 2.12 DONE in slice 2 (AC1 green, exit 0) — see Phase 2 evidence
- [x] Phase 3 **DONE (slice 4, commit a1cc252)** — Robolectric (SDK 35) + Compose smoke (48dp/semantics) + Kover domain gate (96%) + spotlessCheck green + AC2 deliberate-break cycle captured
- [x] 4.1–4.3 CI workflow + wrapper-integrity guard created and scenario-verified (slice 5, commit 6ccd392)
- [x] Phase 5 **DONE (slice 6, PR 6 commits e5d60fe + dbb9e60 + 802e4cb; 5.4 closed in slice 7 via PR #6)** — README env docs, `sdd-init` re-run, scope-guard audit, rollback boundary now in PR #6's description
- [ ] 4.4 live green run on the bootstrap PR (AC4) — blocked on hosted CI: workflow not on `main` yet; `no checks reported on the 'pr6/docs-reinit' branch`; re-check after PR #1 merges and the workflow actually runs
- [ ] 0.2 cmdline-tools install (machine state, deferred)
