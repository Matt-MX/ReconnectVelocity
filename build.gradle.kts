plugins {
    id("java")
    alias(libs.plugins.shadow) apply true
    alias(libs.plugins.runVelocity)
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)

    compileOnly(libs.litebans.api)
    compileOnly(libs.luckperms.api)

    implementation(libs.storage.yaml)
    implementation(libs.storage.mysql)
    implementation(libs.storage.maria)
    implementation(libs.storage.sqlite)
    implementation(libs.storage.hikari)
}

sourceSets["main"].resources.srcDir("src/resources/")

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
    }
    runVelocity {
//        velocityVersion(libs.versions.velocity.api.get())
    }
//    withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
//        javaLauncher.set(javaToolchains.launcherFor {
//            languageVersion = JavaLanguageVersion.of(17)
//            vendor = JvmVendorSpec.JETBRAINS
//        })
//        jvmArgs("-XX:+AllowEnhancedClassRedefinition", "-XX:+AllowRedefinitionToAddDeleteMethods")
//    }
}