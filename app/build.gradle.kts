import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.kododake.aabrowser"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kododake.aabrowser"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // Read signing properties from project properties or environment.
            // Preferred: put RELEASE_STORE_FILE, RELEASE_STORE_PASSWORD, RELEASE_KEY_ALIAS, RELEASE_KEY_PASSWORD
            val props = project.properties
            // Also read local.properties (commonly used for local secrets) if present
            val localPropsFile = rootProject.file("local.properties")
            val localProps = Properties()
            if (localPropsFile.exists()) {
                localProps.load(localPropsFile.inputStream())
            }

            val storeFilePath = (project.findProperty("RELEASE_STORE_FILE") as String?) ?: localProps.getProperty("RELEASE_STORE_FILE") ?: System.getenv("RELEASE_STORE_FILE") ?: "../release.keystore"
            val sp = (project.findProperty("RELEASE_STORE_PASSWORD") as String?) ?: localProps.getProperty("RELEASE_STORE_PASSWORD") ?: System.getenv("RELEASE_STORE_PASSWORD")
            val ka = (project.findProperty("RELEASE_KEY_ALIAS") as String?) ?: localProps.getProperty("RELEASE_KEY_ALIAS") ?: System.getenv("RELEASE_KEY_ALIAS")
            val kp = (project.findProperty("RELEASE_KEY_PASSWORD") as String?) ?: localProps.getProperty("RELEASE_KEY_PASSWORD") ?: System.getenv("RELEASE_KEY_PASSWORD")

            // Resolve path relative to project root so both absolute and relative paths work
            storeFile = rootProject.file(storeFilePath)
            if (sp != null) this.storePassword = sp
            if (ka != null) this.keyAlias = ka
            if (kp != null) this.keyPassword = kp
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.google.material)
    implementation(libs.androidx.car.app)
    testImplementation(libs.androidx.car.app.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}