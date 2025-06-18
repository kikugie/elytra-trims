plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.1.21"
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()

    maven {
        name = "Sonatype Snapshots (Legacy)"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        name = "Sonatype Snapshots"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("dev.kord:kord-core:0.15.0")
}