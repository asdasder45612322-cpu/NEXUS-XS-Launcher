plugins {
    id("com.android.application") version "8.7.3"
    kotlin("android") version "2.0.21"
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    namespace = "com.nexus.xs.launcher"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nexus.xs.launcher"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}


kotlin {
    jvmToolchain(17)
}
