plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.kholodkov.coinmonitor.data"

    compileSdk = 36

    defaultConfig {
        minSdk = 26
        buildConfigField("String", "EXCHANGE_API_URL", "\"https://api.frankfurter.dev/\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Modules
    implementation(project(":domain"))

    //Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.config)
    implementation(libs.firebase.crashlytics)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //Network
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

}