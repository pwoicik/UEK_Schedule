plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.versions)
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
        }
    }
}

fun isStable(candidate: ModuleComponentIdentifier): Boolean {
    val group = candidate.group
    val version = candidate.version

    if (group == "com.tickaroo.tikxml" && version == "0.8.15") return false
    if (group.startsWith("androidx.compose")) return true
    if (group == "com.google.accompanist") return true

    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return stableKeyword || regex.matches(version)
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    rejectVersionIf {
        !isStable(candidate)
    }
}
