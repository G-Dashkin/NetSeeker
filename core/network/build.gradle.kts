plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

// Read WiGLE credentials from local.properties — never commit actual keys.
// Add these lines to your local.properties:
//   WIGLE_API_NAME=your_api_name
//   WIGLE_API_TOKEN=your_api_token
val wigleApiName: String = run {
    val file = rootProject.file("local.properties")
    if (!file.exists()) return@run ""
    file.readLines()
        .firstOrNull { it.startsWith("WIGLE_API_NAME=") }
        ?.substringAfter("=")?.trim() ?: ""
}

val wigleApiToken: String = run {
    val file = rootProject.file("local.properties")
    if (!file.exists()) return@run ""
    file.readLines()
        .firstOrNull { it.startsWith("WIGLE_API_TOKEN=") }
        ?.substringAfter("=")?.trim() ?: ""
}

android {
    namespace = "com.dashkin.netseeker.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 29
        buildConfigField("String", "WIGLE_API_NAME", "\"$wigleApiName\"")
        buildConfigField("String", "WIGLE_API_TOKEN", "\"$wigleApiToken\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)
    implementation(project(":core:utils"))
}
