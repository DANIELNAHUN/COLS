// COLS root settings. The version catalog is the single source of pins;
// no build file may hard-code a version (project-scaffold spec).
//
// Module includes arrive with the `:app` module (include(":app")); the
// bootstrap keeps this file module-free until then.

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "COLS"
