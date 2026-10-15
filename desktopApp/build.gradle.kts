import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(17)
    compilerOptions { jvmTarget = JvmTarget.JVM_17 }
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.mock)
    implementation(libs.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
    testImplementation(compose.desktop.uiTestJUnit4)
    testImplementation(libs.junit)
    testImplementation(libs.compose.material3)
    testImplementation(libs.koin.core)
}

compose.desktop {
    application {
        mainClass = "pe.edu.upeu.pharmamobile.desktop.MainKt"
    }
}
