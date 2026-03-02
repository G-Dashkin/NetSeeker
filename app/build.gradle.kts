plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

// Read Google Maps API key from local.properties — never commit the actual key.
// Add this line to your local.properties: GOOGLE_MAPS_API_KEY=your_key_here
val googleMapsApiKey: String = run {
    val file = rootProject.file("local.properties")
    if (!file.exists()) return@run ""
    file.readLines()
        .firstOrNull { it.startsWith("GOOGLE_MAPS_API_KEY=") }
        ?.substringAfter("=")
        ?.trim()
        ?: ""
}

android {
    namespace = "com.dashkin.netseeker"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.dashkin.netseeker"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = googleMapsApiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Dagger
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Feature modules
    implementation(project(":feature:map"))
    implementation(project(":feature:nearby"))
    implementation(project(":feature:speedtest"))
    implementation(project(":feature:wifidetail"))
    implementation(project(":feature:settings"))

    // Core modules
    implementation(project(":core:di"))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":core:network"))
    implementation(project(":core:wifi"))
    implementation(project(":core:speedtest"))
    implementation(project(":database"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
