@file:Suppress("LocalVariableName", "PropertyName")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.essential.gg/repository/maven-public") { name = "EssentialGG Maven" }
        maven("https://maven.architectury.dev") { name = "Architectury Maven" }
        maven("https://maven.fabricmc.net") { name = "Fabric Maven" }
        maven("https://maven.minecraftforge.net") { name = "Forge Maven" }
        maven("https://maven.kikugie.dev/third-party") { name = "KikuGie Maven Third-Party" }
    }

    plugins {
        val loom_version: String by settings
        id("dev.architectury.loom") version loom_version apply false
    }
}

listOf(
    "1.19.4-fabric",
    "1.20.1-fabric",
    "1.20.2-fabric",

    "1.19.4-forge",
    "1.20.1-forge",
    "1.20.2-forge",
).forEach {
    include(":$it")
    project(":$it").apply {
        projectDir = file("versions/$it")
        buildFileName = "../../build.gradle.kts"
    }
}
rootProject.name = "Elytra Trims"
rootProject.buildFileName = "root.gradle.kts"