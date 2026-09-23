# App Testing Specification

## Purpose

Establish a JVM-first, fast-feedback test loop for the COLS Android project: a workspace-level Gradle test command, a JUnit4 + Robolectric + Compose test runtime stack, coverage tooling, lint/format gating, and one demonstrated RED→GREEN example test proving the TDD loop works from day one. Instrumented/emulator tests are explicitly deferred; the test pyramid and coverage gates are decided by TS-01 in design, never invented here.

## Requirements

### Requirement: Workspace-level test command

The project SHALL provide a single workspace-level test command via the committed Gradle wrapper that runs all unit tests for the `:app` module and reports pass/fail with zero exit code semantics suitable for CI. This command is the canonical test entry point referenced by `openspec/config.yaml` after the `sdd-init` re-run.

The system SHALL ensure `./gradlew :app:testDebugUnitTest` executes the JVM test suite and exits 0 only when all tests pass.

#### Scenario: Running the full unit suite

- GIVEN the scaffolded project with the documented environment
- WHEN `./gradlew :app:testDebugUnitTest` is executed at the workspace root
- THEN all configured unit tests run and the command exits 0 on a green suite

#### Scenario: Failing test fails the command

- GIVEN at least one failing unit test in the suite
- WHEN `./gradlew :app:testDebugUnitTest` is executed
- THEN the command exits non-zero and reports the failing test

### Requirement: JVM-first test stack

Unit tests SHALL run on the JVM (JUnit4 as the test framework, Robolectric where Android framework behavior is needed, Compose test runtime for UI-layer component tests) without requiring a device or emulator. The stack MUST be declared through the version catalog.

#### Scenario: Android-dependent unit test without a device

- GIVEN a unit test that exercises Android framework behavior
- WHEN the suite runs via the workspace test command
- THEN the test passes on the JVM using Robolectric, with no emulator attached

#### Scenario: Compose component test without a device

- GIVEN a test that renders Compose UI content
- WHEN the suite runs via the workspace test command
- THEN the test runs on the JVM using the Compose test runtime

### Requirement: RED→GREEN example test

The scaffold SHALL include at least one passing example test for which the TDD loop (RED→GREEN) is demonstrated during implementation. The test lives in the `:app` module's test source set and serves as the template for all future TDD work.

#### Scenario: Example test proves the loop

- GIVEN the scaffolded test stack
- WHEN the example test is intentionally broken (RED state)
- THEN the workspace test command fails and identifies the failure
- WHEN the fix is restored (GREEN state)
- THEN the workspace test command passes again

### Requirement: Coverage tooling wired

A coverage tool (Kover) SHALL be wired into the `:app` module so coverage reports can be produced by a Gradle task. Coverage thresholds and gates SHALL NOT be invented in this bootstrap; they are defined by TS-01 in the design phase. Until then, the coverage task MAY exist with no enforced minimum.

#### Scenario: Coverage report generation

- GIVEN the scaffolded project
- WHEN the Kover coverage Gradle task is executed after the unit suite
- THEN a coverage report is produced covering `:app` unit tests

#### Scenario: Coverage thresholds deferred

- GIVEN TS-01 has not yet been resolved at scaffold time
- WHEN the build is run
- THEN no coverage minimum blocks the build (thresholds arrive with design, per TS-01)

### Requirement: Lint and format gate via Spotless/ktlint

Code style SHALL be enforced by ktlint through the Spotless plugin, providing both a format capability and a check capability. The check MUST be runnable as a Gradle task and MUST be part of the local quality loop before CI (CI enforcement is specified by `ci-pipeline`).

#### Scenario: Format check on compliant code

- GIVEN all Kotlin sources conform to the configured ktlint rules
- WHEN the Spotless check Gradle task is executed
- THEN the check passes

#### Scenario: Format check on non-compliant code

- GIVEN a Kotlin source violating the configured ktlint rules
- WHEN the Spotless check Gradle task is executed
- THEN the check fails and identifies the violating file
- AND running the Spotless apply/format task reformats the file to compliance

### Requirement: Instrumented tests excluded from bootstrap CI

The bootstrap SHALL NOT add instrumented or emulator-based tests to the local or CI loop. Whether and when instrumented tests enter CI is decided by TS-01; this bootstrap defines only the JVM-first layer.

#### Scenario: No emulator required anywhere in the loop

- GIVEN the complete bootstrap test and CI setup
- WHEN local tests and the CI gate run
- THEN no Android device, emulator, or emulator image is required at any point
