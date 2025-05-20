@file:Suppress("UnstableApiUsage")

plugins {
    idea
    kotlin("jvm")
    id("fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
}

version = "${property("mod.version")}+${stonecutter.current.version}"
base.archivesName = property("mod.id") as String

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/elytratrims.accesswidener")
    runs.configureEach {
        ideConfigGenerated(true)
        runDir = "../../run"
        if (environment == "client")
            programArgs("--username=KikuGie")
    }
}

repositories {
    fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://maven.parchmentmc.org", "org.parchmentmc.data")
}

dependencies {
    val modules = listOf(
        "transitive-access-wideners-v1",
        "registry-sync-v0",
        "resource-loader-v0",
        "gametest-api-v1",
        "data-generation-api-v1",
    )

    minecraft("com.mojang:minecraft:${stonecutter.current.version}")
    mappings(loom.layered {
        officialMojangMappings()
        if (hasProperty("deps.parchment"))
            parchment("org.parchmentmc.data:parchment-${stonecutter.current.version}:${property("deps.parchment")}@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric-loader")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.fabric-kotlin")}")
    for (it in modules) modImplementation(fabricApi.module("fabric-$it", property("deps.fabric-api") as String))
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.processResources {
    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>()
    props["id"] = prop("mod.id")
    props["name"] = prop("mod.name")
    props["version"] = prop("mod.version")
    props["minecraft"] = stonecutter.current.version

    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
}

//msPublishing {
//    mpp {
//        type = ALPHA
//        changelog = rootProject.file("CHANGES.md").readText()
//        displayName = "Elytra Trims Fabric ${project.property("mod.version")} for ${stonecutter.current.version}"
//
//        modrinth {
//            projectId = "XpzGz7KD"
//            accessToken = env.fetch("MODRINTH_TOKEN", "_")
//            minecraftVersions.add(stonecutter.current.version)
//            requires("fabric-api", "fabric-language-kotlin")
//        }
//
//        curseforge {
//            projectId = "876199"
//            accessToken = env.fetch("CURSEFORGE_TOKEN", "_")
//            minecraftVersions.add(stonecutter.current.version)
//            requires("fabric-api", "fabric-language-kotlin")
//        }
//    }
//}