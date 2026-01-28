import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm()

    sourceSets {

        /* =========================
         * COMMON MAIN
         * ========================= */
        val commonMain by getting {
            dependencies {
                // Compose Multiplatform
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)

                // Coroutines
                implementation(libs.kotlinx.coroutines.core)

                // Date & Time (KMP-safe)
                implementation(libs.kotlinx.datetime)

                // Serialization
                implementation(libs.kotlinx.serialization.json)

                // Ktor (core only, engine in jvmMain)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.serialization.kotlinxJson)
                implementation(libs.ktor.client.logging)

                // SQLDelight
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutinesExtensions)
            }
        }

        /* =========================
         * COMMON TEST
         * ========================= */
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.mockk)
            }
        }

        /* =========================
         * JVM MAIN (DESKTOP)
         * ========================= */
        val jvmMain by getting {
            dependencies {
                // Compose Desktop
                implementation(compose.desktop.currentOs)

                // Coroutines Main Dispatcher for Desktop
                implementation(libs.kotlinx.coroutinesSwing)

                // Ktor JVM Engine
                implementation(libs.ktor.client.cio)

                // SQLDelight JVM Driver
                implementation(libs.sqldelight.sqliteDriver)

                // Date & Time (explicit for JVM)
                implementation(libs.kotlinx.datetime)

                implementation("org.slf4j:slf4j-simple:2.0.13")
            }
        }

        /* =========================
         * JVM TEST
         * ========================= */
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.testJunit)
                implementation(libs.junit)
            }
        }
    }
}

/* =========================
 * COMPOSE DESKTOP CONFIG
 * ========================= */
compose.desktop {
    application {
        mainClass = "com.rahman.railwayapp.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Deb
            )
            packageName = "com.rahman.railwayapp"
            packageVersion = "1.0.0"
        }
    }
}
