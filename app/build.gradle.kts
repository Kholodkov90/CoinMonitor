import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

android {
    namespace = "com.kholodkov.coinmonitor"

    compileSdk = 36

    val localProperties = Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }

    defaultConfig {
        applicationId = "com.kholodkov.coinmonitor"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            storeFile = file("C:\\Users\\Sergey\\keystores\\coin_key_store")
            storePassword = localProperties["KEYSTORE_PASSWORD"] as String
            keyAlias = localProperties["KEY_ALIAS"] as String
            keyPassword = localProperties["KEY_PASSWORD"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Modules
    implementation(project(":core:ui"))
    implementation(project(":core:platform"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature:login"))
    implementation(project(":feature:main"))
    implementation(project(":feature:purchase"))
    implementation(project(":feature:statistic"))
    implementation(project(":feature:settings"))

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    //Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    // WorkManager
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.runtime)

    // Splash screen
    implementation(libs.androidx.core.splashscreen)
}