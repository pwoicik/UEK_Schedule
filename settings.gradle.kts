pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "UEK Schedule"

include(
    ":app",
    ":resources",
    ":common:jvm",
    ":common:android",
    ":model",
    ":db",
    ":api",
    ":repository",
    ":features:schedule",
    ":features:groups",
    ":features:subjects",
    ":features:activities",
    ":features:preferences",
    ":features:search",
    ":navigation"
)
