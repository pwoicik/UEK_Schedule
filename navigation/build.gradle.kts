plugins {
    id("com.android.library")
    kotlin("android")
    id("com.google.devtools.ksp")
}

kotlin {
    sourceSets.forEach {
        it.kotlin.srcDir("build/generated/ksp/${it.name}/kotlin")
    }
}

ksp {
    arg("compose-destinations.mode", "destinations")
    arg("compose-destinations.moduleName", "search")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "com.github.pwoicik.uekschedule.navigation"
    compileSdk = libs.versions.sdk.target.get().toInt()

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(project(":common:android"))
    implementation(project(":features:activities"))
    implementation(project(":features:groups"))
    implementation(project(":features:preferences"))
    implementation(project(":features:schedule"))
    implementation(project(":features:search"))
    implementation(project(":features:subjects"))

    implementation(libs.destinations)
    ksp(libs.destinations.ksp)
}
