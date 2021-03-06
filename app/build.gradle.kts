plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.github.pwoicik.uekschedule"

    compileSdk = libs.versions.sdk.target.get().toInt()

    defaultConfig {
        applicationId = "com.github.pwoicik.uekschedule"
        minSdk = libs.versions.sdk.min.get().toInt()
        targetSdk = libs.versions.sdk.target.get().toInt()
        versionCode = 28
        versionName = "1.2.3.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("debug_key.jks")
            storePassword = "debug_key"
            keyAlias = "key0"
            keyPassword = "debug_key"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")

            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.asProvider().get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":common:android"))
    implementation(project(":common:jvm"))
    implementation(project(":model"))
    implementation(project(":navigation"))
    implementation(project(":repository"))
    implementation(project(":resources"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.lifecycle.runtime)

    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    implementation(libs.accompanist.uicontroller)

    implementation(libs.hilt)
    kapt(libs.hilt.kapt)

    implementation(libs.timber)
}
