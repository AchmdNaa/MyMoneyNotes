plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.mymoneynotes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mymoneynotes"
        minSdk = 24
        targetSdk = 35   
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {

    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")

    // Compose BOM (biar versi aman & sinkron)
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))

    // Compose utama
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.android.material:material:1.12.0")

    // Chart Library (Vico)
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.22")

    // Debug tools
    debugImplementation("androidx.compose.ui:ui-tooling")
}