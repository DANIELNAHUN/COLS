# CI Pipeline Specification

## Purpose

Provide a continuous-integration pull-request gate for COLS on GitHub Actions: every PR must pass lint (ktlint via Spotless), unit tests, and assembly before merge. The gate uses the committed Gradle wrapper, requires no emulator, and is the first automated quality gate the project owns. Coverage thresholds in CI follow TS-01 and are not asserted here.

## Requirements

### Requirement: PR gate executes lint, tests, and assemble

A GitHub Actions workflow SHALL run on every pull request. The gate MUST execute, at minimum:

1. the Spotless/ktlint check,
2. the workspace-level unit test command (`:app:testDebugUnitTest`), and
3. a debug assembly of the `:app` module (`:app:assembleDebug`).

All three MUST run through the committed Gradle wrapper — no pre-installed Gradle on the runner may be relied upon.

#### Scenario: Gate runs on a pull request

- GIVEN an open pull request with any new commit
- WHEN CI is triggered
- THEN the workflow runs lint check, unit tests, and debug assembly
- AND all three report explicit pass/fail status on the PR

#### Scenario: Gate on a green PR

- GIVEN a pull request with compliant code, passing tests, and a buildable app
- WHEN CI completes
- THEN all three gate steps succeed and the PR is marked as green

### Requirement: Gate blocks merge on failure

The workflow SHALL fail when any of the three steps fails, and project practice SHALL treat a red gate as a merge blocker. A failure in any one step MUST be visible as the cause of the failed run.

#### Scenario: Unit test failure blocks the PR

- GIVEN a pull request containing a failing unit test
- WHEN CI runs the gate
- THEN the test step fails
- AND the overall workflow run is reported as failed

#### Scenario: Lint violation blocks the PR

- GIVEN a pull request containing a ktlint violation
- WHEN CI runs the gate
- THEN the lint check step fails and the workflow reports the violation

#### Scenario: Build break blocks the PR

- GIVEN a pull request that does not compile
- WHEN CI runs the gate
- THEN the assemble step fails and the workflow reports the build error

### Requirement: CI runs without emulator or device

The gate SHALL complete entirely on a hosted runner with no Android device or emulator. If the workflow needs the Android SDK, it MUST obtain it via documented setup steps inside the workflow itself (e.g. SDK install action or command-line tools), not by depending on pre-installed runner state.

#### Scenario: Cold runner provisioning

- GIVEN a fresh hosted runner with no COLS-specific state
- WHEN the gate runs for a PR
- THEN the workflow provisions the required JDK and Android SDK from its own configuration
- AND the gate completes without any emulator usage

### Requirement: Workflow version pins trace to the version catalog

CI toolchain versions (JDK distribution/version, Gradle via wrapper) SHALL stay consistent with the version catalog's verified toolchain set. No CI file MAY pin a conflicting version.

#### Scenario: CI respects the catalog pins

- GIVEN the verified toolchain set in the version catalog
- WHEN the CI workflow is inspected
- THEN the JDK version used by the workflow matches the catalog's JDK pin
- AND Gradle comes solely from the committed wrapper
