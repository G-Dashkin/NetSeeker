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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NetSeeker"

include(":app")
include(":core:di")
include(":core:ui")
include(":core:utils")
include(":core:network")
include(":core:wifi")
include(":core:speedtest")
include(":database")
include(":feature:map")
include(":feature:nearby")
include(":feature:speedtest")
include(":feature:wifidetail")
include(":feature:settings")
