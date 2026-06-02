import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.navigation.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "su.afk.l4d2.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe)
            packageName = "L4D Allow Mods"
            packageVersion = "1.0.5"
            description = "L4D Allow Mods"
            copyright = "EtoZheSandy"
            vendor = "EtoZheSandy"
            windows {
                windows {
                    shortcut = true
                    menuGroup = "L4D Allow Mods"
                    perUserInstall = true
                    iconFile.set(project.file("src\\commonMain\\composeResources\\drawable\\icon.ico"))
                }
            }
            buildTypes.release.proguard {
                isEnabled.set(false)
            }
        }
    }
}
