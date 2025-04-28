import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()
    sourceSets {
        jvmMain.dependencies {
            implementation(projects.shared)

            implementation(compose.components.resources)
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "navid.hamyan.desktop"
            packageVersion = "1.0.0"

            linux {
                iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
            }
            windows {
                iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
            }
            macOS {
                iconFile.set(project.file("desktopAppIcons/MacosIcon.icns"))
                bundleID = "navid.hamyan.desktop"
            }
        }
    }
}
