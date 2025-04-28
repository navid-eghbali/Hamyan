plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget()
    jvm()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "SharedKit"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.biometric)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.ktor.client.android)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.bundles.ktor)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.coil.compose.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.core)
            implementation(libs.coil.svg)
            implementation(libs.coil.network.ktor)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.navigation)
            implementation(libs.koin.compose.viewmodel)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
        }
    }
}

android {
    namespace = "navid.hamyan.shared"
}

dependencies {
    ksp(libs.room.compiler)
    debugImplementation(compose.uiTooling)
}

room {
    schemaDirectory("$projectDir/schemas")
}

compose.resources {
    publicResClass = true
    packageOfResClass = "navid.hamyan.shared.resources"
}
