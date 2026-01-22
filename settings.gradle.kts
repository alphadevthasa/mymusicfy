pluginManagement {
    repositories {
        google()
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

rootProject.name = "MusicWave"
include(":app")
include(":core:common")
include(":core:domain")
include(":core:network")
include(":core:database")
include(":core:ui")
include(":feature:browse")
include(":feature:search")
include(":feature:artist")
include(":feature:release")
include(":feature:player")
include(":feature:favorites")

