rootProject.name = "ReconnectVelocity"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // add toolchain resolver
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}