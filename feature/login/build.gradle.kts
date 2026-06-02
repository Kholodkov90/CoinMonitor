import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.kholodkov.coinmonitor.feature.login"

    compileSdk = 36

    val localProperties = Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "GOOGLE_CLIENT_ID",
                "\"${localProperties["GOOGLE_CLIENT_ID_DEBUG"]}\""
            )
        }
        release {
            buildConfigField(
                "String",
                "GOOGLE_CLIENT_ID",
                "\"${localProperties["GOOGLE_CLIENT_ID_RELEASE"]}\""
            )
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Modules
    implementation(project(":domain"))
    implementation(project(":core:ui"))

    //Navigation
    implementation(libs.androidx.navigation.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)


    // Auth
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}