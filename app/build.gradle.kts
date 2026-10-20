plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Signing details come from environment variables only.
// The keystore is never inside the project and never committed.
val signingKeystorePath: String? = System.getenv("NAIJASLANG_KEYSTORE_PATH")
val signingKeystorePassword: String? = System.getenv("NAIJASLANG_KEYSTORE_PASSWORD")
val signingKeyAlias: String? = System.getenv("NAIJASLANG_KEY_ALIAS")
val signingKeyPassword: String? = System.getenv("NAIJASLANG_KEY_PASSWORD")

val hasSigningEnv = !signingKeystorePath.isNullOrBlank() &&
    !signingKeystorePassword.isNullOrBlank() &&
    !signingKeyAlias.isNullOrBlank() &&
    !signingKeyPassword.isNullOrBlank()

android {
    namespace = "com.westly.naijaslang"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.westly.naijaslang"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        if (hasSigningEnv) {
            create("release") {
                storeFile = file(signingKeystorePath!!)
                storePassword = signingKeystorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            // Minification stays OFF in Phase 1 (handled in Phase 6).
            isMinifyEnabled = false
            if (hasSigningEnv) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
}
