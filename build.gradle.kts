// COLS root build. Plugin versions resolve only from the version catalog;
// AGP and Kover are declared but not applied here (the `:app` module applies
// them). Spotless is applied at the root because the root already owns
// Kotlin-DSL build scripts to format; the `:app` module applies it as well
// when it lands, so `./gradlew spotlessCheck` covers all modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    // Compose Compiler plugin declared at the root per the Kotlin
    // compose-compiler migration guide; every Compose module applies it.
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.spotless)
}

spotless {
    kotlinGradle {
        target("*.gradle.kts", "gradle/**/*.gradle.kts")
        ktlint()
    }
}
