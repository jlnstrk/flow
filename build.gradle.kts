buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlin.gradle)
        classpath(libs.androidGradle)
        classpath(libs.hiltGradle)
        classpath(libs.googleServicesGradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

val secretsFile = File(rootDir, "secrets.properties")
if (!secretsFile.exists()) {
    throw GradleException("Missing secrets.properties")
}
val secrets = java.util.Properties()
secrets.load(secretsFile.inputStream())
for ((key, value) in secrets) {
    extra[key.toString()] = value
}