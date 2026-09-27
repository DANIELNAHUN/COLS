// COLS `:app` module. Kotlin compilation comes from AGP's built-in Kotlin
// (D3): only `com.android.application` is applied — `org.jetbrains.kotlin.android`
// is NOT compatible with the AGP 9 DSL. The Compose Compiler Gradle plugin
// (`org.jetbrains.kotlin.plugin.compose`) is required whenever Compose is
// enabled (Kotlin 2.0+ rule); its version matches the built-in KGP AGP 9.2
// supplies, per the kotlinlang Compose compiler migration guide.
//
// Every version resolves from the version catalog; no version is hard-coded
// here (project-scaffold spec).

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kover)
    alias(libs.plugins.spotless)
}

android {
    namespace = "com.cols.launcher"

    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.cols.launcher"
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "0.1.0"
    }

    // JVM 17 per the catalog's verified toolchain set (AGP 9.x required JDK).
    // Built-in Kotlin derives its jvmTarget from targetCompatibility.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        // Enable Compose (per the Compose Compiler setup guide; the Compose
        // compiler itself is supplied by AGP built-in Kotlin).
        compose = true
    }

    testOptions {
        unitTests {
            // Robolectric (and the Compose test runtime on Robolectric) needs
            // the app's resources and manifests on the JVM.
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    // Compose stack — all Compose versions resolve from the BOM.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)

    // JVM-first unit-test stack (app-testing spec): JUnit4 + Robolectric +
    // Compose test runtime, no device or emulator.
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
}

// ktlint compliance for all Kotlin sources (app-testing format scenarios,
// task 3.4): the check is `./gradlew spotlessCheck`, the fix is spotlessApply.
spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint()
    }
}

// Coverage gate (task 3.3 / TS-01, D-design RQ1 table): `:app:koverVerify`
// enforces >=80% LINE coverage for the `domain` package ONLY — the agent
// seam + confirmation rule. Scope is enforced by a rule-scoped filter (not a
// global threshold): `ui`, `data`, and generated code are NOT measured by
// the gate, and no other bound exists (app-testing spec defers global
// thresholds). `koverHtmlReport`/`koverXmlReport` produce the domain-scoped
// report after the unit suite; per-variant (Debug) reports stay unfiltered
// for future full-module views.
kover {
    reports {
        total {
            filters {
                includes {
                    packages("com.cols.launcher.domain")
                }
            }
            verify {
                rule("domain line coverage floor") {
                    minBound(80, CoverageUnit.LINE, AggregationType.COVERED_PERCENTAGE)
                }
            }
        }
    }
}
