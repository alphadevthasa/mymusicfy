plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

kotlin { jvmToolchain(17) }

android {
    namespace = "com.musicwave.app.core.domain"
    compileSdk = 35
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.paging.runtime)
    implementation(libs.kotlinx.coroutines.core)
}
