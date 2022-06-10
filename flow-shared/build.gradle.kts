import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.buildkonfig)
}

buildscript {
    dependencies {
        classpath(libs.buildkonfigGradle)
    }
}

kotlin {
    android()
    ios {
        binaries.framework {
            baseName = "Shared"
            export(libs.bundles.transit)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.bundles.transit)
                api(kotlin("stdlib"))
                // api("de.julianostarek.transit:interop-hci")
                // api("de.julianostarek.transit:interop-efa")
                // api("de.julianostarek.transit:interop-hapi")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.ktor.client.ios)
            }
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 28
        targetSdk = 32
    }
}

project.buildkonfig {
    packageName = "de.julianostarek.flow"
    defaultConfigs {
        buildConfigField(STRING, "RMV_AID", rootProject.extra["hapi.rmv.aid"] as String)
        buildConfigField(STRING, "VBB_AID", rootProject.extra["hapi.vbb.aid"] as String)
        buildConfigField(STRING, "HVV_SALT", rootProject.extra["hci.hvv.salt"] as String)
        buildConfigField(STRING, "SBM_SALT", rootProject.extra["hci.sbm.salt"] as String)
    }
}