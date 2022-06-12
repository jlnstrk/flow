plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.ksp)
}

android {
    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("androidTest").java.srcDir("src/androidTest/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
    }
    compileSdk = 32
    defaultConfig {
        applicationId = "de.julianostarek.flow"
        minSdk = 28
        targetSdk = 32
        versionCode = 1
        versionName = "0.1.0-SNAPSHOT"

        resValue("string", "map_id_night", "e4949b93374b5c4c")
        resValue("string", "map_id_notnight", "b663934f3d170a71")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        exclude("META-INF/*.md")
    }
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    api(project(":flow-shared"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.ktor.client.android)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    coreLibraryDesugaring(libs.desugarJdkLibs)

    implementation(libs.logger)

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.google.material)

    implementation(libs.androidx.preference)

    /* Lifecycle */
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.process)
    kapt(libs.androidx.lifecycle.compiler)

    /* Room */
    implementation(libs.androidx.room.core)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.paging)
    kapt(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)

    /* Paging */
    implementation(libs.androidx.paging.runtime)
    testImplementation(libs.androidx.paging.common)

    implementation(libs.recyclerviewAnimators)

    implementation(libs.firebase.core)
    implementation(libs.firebase.firestore)

    implementation(libs.google.maps.utils)
    implementation(libs.google.gms.location)
    implementation(libs.google.gms.maps)

    implementation(libs.glide.core)
    kapt(libs.glide.compiler)

    implementation(libs.dahdit)

    // debugImplementation(libs.debugDb)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
}