import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
}

buildscript {
    dependencies {
        classpath(libs.buildkonfigGradle)
        classpath(libs.sqldelightGradle)
    }
}

project.sqldelight {
    database("FlowDatabase") {
        packageName = "de.julianostarek.flow.database"
    }
}

kotlin {
    android()
    listOf(
        iosArm64(),
        iosX64()
    ).forEach {
        it.binaries.framework {
            baseName = "Shared"
            export(libs.bundles.transit)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.bundles.transit)
                api(kotlin("stdlib"))
                implementation(libs.koin.core)
                implementation(libs.koin.annotations)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.coroutines.core)
                implementation(libs.kfirebase.firestore)
                // api("de.julianostarek.transit:interop-hci")
                // api("de.julianostarek.transit:interop-efa")
                // api("de.julianostarek.transit:interop-hapi")
            }
            kotlin.srcDir("build/generated/ksp/android/androidDebug/kotlin")
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val iosArm64Main by getting
        val iosX64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosX64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.ios)
                implementation(libs.sqldelight.driver.native)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.driver.android)
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.compiler)
    add("kspAndroid", libs.koin.compiler)
    add("kspIosX64", libs.koin.compiler)
    add("kspIosArm64", libs.koin.compiler)
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