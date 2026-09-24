# Apply Progress: Bootstrap Android Launcher Scaffold (COLS)

Cumulative apply-progress artifact for this change (created per OpenSpec file
convention; no prior apply-progress existed — a previous apply dispatch was
cancelled after generating untracked partial files, reconciled in this slice).

## Cumulative Task State

Total tasks: 40 — completed: 15, pending: 25.

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

## Work Unit Evidence (Hard Gate)

Work unit 1 — "Toolchain-ready Gradle skeleton" (PR 1 / `pr1/gradle-skeleton`):

| Evidence | Required value |
|---|---|
| Focused test command and exact result | `.\gradlew.bat --version` → Gradle 9.4.1, Launcher JVM 17.0.20.1, exit 0; `git ls-files -s gradlew` RED→GREEN: `100644` (staged initial) → `100755` (after `git update-index --chmod=+x gradlew`); `git check-ignore -v .atl build local.properties` → all three matched, exit 0 |
| Runtime harness command/scenario and exact result | `.\gradlew.bat help` → BUILD SUCCESSFUL in 1s, exit 0 — settings + root build + version catalog evaluate and plugin aliases (AGP 9.2.1, Kover 0.9.9, Spotless 8.10.1) resolve from the plugin portals; `.\gradlew.bat spotlessCheck` → initially FAILED (CRLF line-ending violations in `build.gradle.kts`/`settings.gradle.kts` under `core.autocrlf=true`), then `spotlessApply` + re-check → BUILD SUCCESSFUL, exit 0, after adding `.gitattributes` LF policy. N/A remainder: no runtime app boundary exists yet (`:app` module lands with work unit 2), and `./gradlew help`/`spotlessCheck` via the wrapper IS the real integration path for this unit |
| Rollback boundary | Commit f83a58e is self-contained: delete `settings.gradle.kts`, root `build.gradle.kts`, `gradle.properties`, `gradle/` (catalog + wrapper), `gradlew`, `gradlew.bat`, `.gitattributes`; restore `.gitignore` from `HEAD` (c16a93d). No other file depends on these yet; `include(":app")` not yet added |

## Mode Resolution

Standard Mode — `openspec/config.yaml` reports `strict_tdd: false` (no test runner at init; workspace test command arrives with the `:app` module in work unit 2). No threat-matrix RED tests were skipped: the one applicable row (executable integrity) was executed as RED→GREEN per 1.6/1.7.

## Files Changed in This Slice (commit f83a58e, 10 files, 461 insertions)

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

## Deviations from Design

1. **`.gitattributes` added (not in the design file list)** — required to make `spotlessCheck` durable on this Windows host (`core.autocrlf=true` regenerates CRLF on every clone; ktlint requires LF). Same threat-matrix family as D8 (Windows-first-host repo integrity). Design files otherwise unchanged.
2. **0.3 verified by filesystem inspection instead of `sdkmanager --list_installed`** — `cmdline-tools` is not installed (0.2 left unchecked).
3. **`platforms/android-37.0` naming** — the installed platform directory uses the new minor-version style (`android-37.0`) rather than `android-37`. Not exercised yet (no `:app` module); flagged as a risk for work unit 2's first `assembleDebug`.

## Issues Found

1. `cmdline-tools`/`sdkmanager` absent — task 0.2 remains open; future SDK management has no CLI path until it is installed.
2. Spotless initially failed on CRLF — resolved durably via `.gitattributes`; evidence recorded above.
3. Cancelled-dispatch partial files were reconciled in place and retained (all valid; no partial file was deleted).

## Remaining Tasks (next slice: work unit 2 — `:app` module + smoke screen + agent seam, PR 2+3)

- [ ] 2.1 `app/build.gradle.kts` (AGP-only plugin, built-in Kotlin, Compose DSL per AGP docs, catalog pins) + `include(":app")`
- [ ] 2.2 `app/src/main/AndroidManifest.xml` (LAUNCHER only, no roles/permissions)
- [ ] 2.3 minimal `res/` (strings, Material 3 theme, adaptive icon)
- [ ] 2.4 RED: confirmation-rule tests (`ConfirmationPolicyTest`, `AgentActionGatewayTest`)
- [ ] 2.5–2.11 domain seam + data adapters + placeholder screen + MainActivity wiring
- [ ] 2.12 AC1 `./gradlew :app:assembleDebug` (watch the `android-37.0` platform lookup)
- Open machine-state item: 0.2 cmdline-tools install (not a blocker for work units 2–4, but worth scheduling)
