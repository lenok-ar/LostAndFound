import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "com.example.services"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        buildConfigField("String",
            "APPMETRICA_API_KEY",
            "\"${localProperties.getProperty
                ("APPMETRICA_API_KEY", "")}\"")
        buildConfigField("String", "YANDEX_CLIENT_ID", "\"${localProperties.getProperty("YANDEX_CLIENT_ID", "")}\"")
        buildConfigField("String", "VK_CLIENT_ID", "\"${localProperties.getProperty("VK_CLIENT_ID", "")}\"")
        buildConfigField("String", "VK_CLIENT_SECRET", "\"${localProperties.getProperty("VK_CLIENT_SECRET", "")}\"")
        buildConfigField("String", "YANDEX_MAPKIT_API_KEY", "\"${localProperties.getProperty("YANDEX_MAPKIT_API_KEY", "")}\"")
        buildConfigField("String", "GIGACHAT_AUTH_KEY", "\"${localProperties.getProperty("GIGACHAT_AUTH_KEY", "")}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.security.crypto)
    implementation(libs.yandex.auth)
    implementation(libs.vkid)
    implementation(libs.appmetrica)
    implementation(libs.yandex.mapkit)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.javax.inject)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.config)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
    implementation(libs.okhttp)
}

kapt {
    correctErrorTypes = true
}
