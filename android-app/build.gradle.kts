import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(projects.shared)

            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
    }
}

android {
    namespace = "navid.hamyan"
    defaultConfig {
        applicationId = "navid.hamyan.android"
        versionCode = 1
        versionName = "1.0.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
