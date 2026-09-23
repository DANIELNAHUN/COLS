# Project Scaffold Specification

## Purpose

Establish a runnable, reproducible Android build scaffold for COLS from a clean checkout: a committed Gradle wrapper, a version catalog as the single source of toolchain pins, a single `:app` Kotlin + Jetpack Compose module with layered packages, a smoke placeholder screen, and documented environment prerequisites (JDK, Android SDK). This capability enables every later product change; it implements no launcher feature itself.

This spec deliberately pins NO concrete versions. Toolchain versions (AGP, Gradle, JDK, Kotlin, Compose BOM, compileSdk, minSdk, targetSdk) are supplied by research (RQ1/RQ2) and design, and MUST be re-verified as one mutually compatible set at implementation. Platform facts asserted here without verification would violate the project's honesty rule.

## Requirements

### Requirement: Clean-checkout build succeeds

The project SHALL build successfully from a clean checkout using only the committed repository files plus the environment documented in the README. The Gradle wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/*`) MUST be committed so that no pre-installed Gradle distribution is required; the wrapper MUST be the only sanctioned entry point for building and testing.

The system SHALL ensure that `./gradlew :app:assembleDebug` succeeds when the documented JDK and Android SDK environment is present.

#### Scenario: First build on a prepared machine

- GIVEN a clean checkout of the repository
- AND the JDK and Android SDK environment documented in the README (including `ANDROID_HOME`)
- WHEN the developer runs `./gradlew :app:assembleDebug`
- THEN the build succeeds without manual file edits
- AND a debug APK for the `:app` module is produced

#### Scenario: First build without required environment

- GIVEN a clean checkout of the repository
- AND a machine missing the documented JDK or Android SDK setup
- WHEN the developer runs `./gradlew :app:assembleDebug`
- THEN the build fails with an error that identifies the missing toolchain component
- AND the README contains the setup instructions sufficient to resolve the failure

### Requirement: Committed Gradle wrapper

The repository MUST contain a committed Gradle wrapper (wrapper scripts plus wrapper JAR/properties). The wrapper's Gradle version MUST be the version pinned in the version catalog's verified toolchain set. Build commands MUST NOT depend on a Gradle distribution installed outside the wrapper.

#### Scenario: Wrapper used as the only build entry point

- GIVEN a machine with the documented JDK and Android SDK but no standalone Gradle installation
- WHEN the developer runs the wrapper task for unit tests or assembly
- THEN the wrapper downloads its pinned Gradle distribution if needed and executes the task successfully

#### Scenario: Wrapper integrity after fresh clone

- GIVEN a fresh clone of the repository
- WHEN the developer inspects the repository root
- THEN `gradlew`, `gradlew.bat`, and the `gradle/wrapper/` files are all present and executable via the documented commands

### Requirement: Version catalog as single source of toolchain pins

All dependency and plugin versions SHALL be declared in one version catalog (`gradle/libs.versions.toml`). The catalog MUST pin a mutually compatible AGP / Gradle / JDK / Compose BOM / compileSdk set that has been re-verified together at implementation time (RQ1). No build file MAY hard-code a version that bypasses the catalog.

#### Scenario: All pins resolved from one file

- GIVEN the scaffolded project
- WHEN the version catalog is inspected
- THEN every plugin and dependency version used by the root and `:app` build files resolves from the catalog
- AND the pinned AGP, Gradle, JDK, Compose BOM, and compileSdk values form a verified-compatible set (no stale or unverified numbers)

#### Scenario: Catalog-driven toolchain fix

- GIVEN a pinned version later proven incompatible
- WHEN the version is corrected in the version catalog
- THEN the build, wrapper regeneration, and CI all reflect the correction without edits to individual build files

### Requirement: Single :app module with layered packages

The project SHALL consist of exactly one Android application module (`:app`). Inside `:app`, Kotlin sources MUST be organized into layered packages (`ui/`, `domain/`, `data/`) so that later module extraction (e.g. `:agent`) stays mechanical. No additional Gradle modules SHALL be created in this bootstrap.

#### Scenario: Module structure inspection

- GIVEN the scaffolded project
- WHEN the Gradle settings are inspected
- THEN only the root project and the single `:app` module are included
- AND `:app` sources are grouped under the `ui/`, `domain/`, and `data/` package layers

### Requirement: Smoke placeholder screen

The `:app` module SHALL contain one minimal placeholder screen built with Jetpack Compose and Material 3. The screen exists solely to prove the toolchain compiles and renders; it MUST NOT contain launcher features (app grid, call UX, contacts, caregiver surfaces).

The placeholder screen SHOULD use the project's Compose + Material 3 baseline so it exercises the real rendering stack.

#### Scenario: Placeholder screen compiles and renders

- GIVEN a successful `assembleDebug` build
- WHEN the app is installed and launched on a device or emulator
- THEN the placeholder screen renders using Compose + Material 3
- AND the screen exposes no launcher, call, contacts, or caregiver functionality

### Requirement: Android .gitignore baseline

The repository `.gitignore` SHALL be extended with the standard Android build-ignore baseline (local SDK paths, build outputs, IDE artifacts). The existing `.atl/` entry MUST be preserved.

#### Scenario: Ignore rules after scaffold

- GIVEN the scaffolded repository
- WHEN the `.gitignore` is inspected
- THEN the standard Android baseline entries are present
- AND the `.atl/` entry is still effective

#### Scenario: Generated build artifacts are not committed

- GIVEN a completed build
- WHEN `git status` is inspected
- THEN no `build/` output, `local.properties`, or IDE-local artifact appears as untracked-to-commit noise

### Requirement: Documented dev environment

The README MUST document the development environment setup: JDK installation, Android SDK components (cmdline-tools, platform-tools, build-tools, platform), `ANDROID_HOME` configuration, and first-build commands. The documentation SHALL be sufficient for a developer on a clean machine to reach a successful build.

#### Scenario: Developer onboarding from documentation alone

- GIVEN a developer with no prior COLS setup
- WHEN they follow only the README setup section
- THEN they can install the JDK and Android SDK, configure `ANDROID_HOME`, and complete a first successful build
