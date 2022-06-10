enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Flow"
include(":flow-android")
include(":flow-shared")

// Transit submodule
/*includeBuild("transit") {
    dependencySubstitution {
        substitute(module("de.julianostarek.transit:interop-hci")).using(project(":interop:interop-hci"))
        substitute(module("de.julianostarek.transit:interop-efa")).using(project(":interop:interop-efa"))
        substitute(module("de.julianostarek.transit:interop-hapi")).using(project(":interop:interop-hapi"))
    }
}*/