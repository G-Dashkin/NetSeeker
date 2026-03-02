plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.dashkin.netseeker.core.di"
    compileSdk = 36

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.dagger)
}
