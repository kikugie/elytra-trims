import org.gradle.configurationcache.extensions.capitalized

plugins {
    id("dev.architectury.loom")
    id("me.modmuss50.mod-publish-plugin")
    id("me.fallenbreath.yamlang") version "1.3.1"
}

val loader = loom.platform.get().name.lowercase()
val isFabric = loader == "fabric"
val mcVersion = stonecutter.current.version
val mcDep = property("mod.mc_dep").toString()
val modId = property("mod.id").toString()
val modName = property("mod.name").toString()
val modVersion = property("mod.version").toString()
val modGroup = property("mod.group").toString()

version = "$modVersion+$mcVersion"
group = modGroup
base { archivesName.set("$modId-$loader") }

stonecutter.expression {
    when (it) {
        "fabric" -> loader == "fabric"
        "forge" -> loader == "forge"
        "neoforge" -> loader == "neoforge"
        else -> null
    }
}

repositories {
    exclusiveContent {
        forRepository { maven("https://www.cursemaven.com") { name = "CurseForge" } }
        filter { includeGroup("curse.maven") }
    }
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
    maven("https://jitpack.io") { name = "Jitpack" }
    maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.kikugie.dev/releases")
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
    val mixinExtras = "io.github.llamalad7:mixinextras-%s:${property("deps.mixin_extras")}"
    val mixinSquared = "com.github.bawnorton.mixinsquared:mixinsquared-%s:${property("deps.mixin_squared")}"
    implementation(annotationProcessor(mixinSquared.format("common"))!!)
    if (isFabric) {
        modLocalRuntime("dev.kikugie:crash-pipe:0.1.0") // Very important asset
        modLocalRuntime(fabricApi.module("fabric-registry-sync-v0", property("deps.fapi").toString()))
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        modImplementation("com.terraformersmc:modmenu:${property("deps.modmenu")}")
        include(implementation(mixinSquared.format("fabric"))!!)
    } else {
        if (loader == "forge") {
            "forge"("net.minecraftforge:forge:${mcVersion}-${property("deps.fml")}")
            compileOnly(annotationProcessor(mixinExtras.format("common"))!!)
            include(implementation(mixinExtras.format("forge"))!!)
        } else
            "neoForge"("net.neoforged:neoforge:${property("deps.fml")}")
        include(implementation(mixinSquared.format(loader))!!)
    }
    // Config
    modCompileOnly("dev.isxander.yacl:yet-another-config-lib-$loader:${property("deps.yacl")}")
//    modImplementation("me.shedaniel.cloth:cloth-config-$loader:${property("deps.cloth")}") {
//        exclude(group = "net.fabricmc.fabric-api")
//    }

    // Compat
//    modLocalRuntime("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}") // Uncomment when a compat mod complaints about no fapi
    modCompileOnly("maven.modrinth:stacked-armor-trims:1.1.0")
    modCompileOnly("maven.modrinth:allthetrims:${if (isFabric) "3.3.7" else "Ga7vvJCQ"}")
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/elytratrims.accesswidener"))

    if (loader == "forge") {
        forge {
            convertAccessWideners.set(true)
            mixinConfigs("$modId.mixins.json")
            mixinConfigs("$modId-compat.mixins.json")
        }
    }

    runConfigs["client"].apply {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        programArgs("--username=KikuGie") // Mom look I'm in the codebase!
        runDir = "../../run"
    }
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"

        dependsOn(tasks.named("build"))
    }
}

tasks.processResources {
    inputs.property("version", modVersion)
    inputs.property("mc", mcDep)

    val map = mapOf(
        "version" to modVersion,
        "mc" to mcDep,
        "fml" to if (loader == "neoforge") "1" else "45"
    )

    filesMatching("fabric.mod.json") { expand(map) }
    filesMatching("META-INF/mods.toml") { expand(map) }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/$modId/lang")
}

java {
    withSourcesJar()
}

tasks.named("publishMods") {
    mustRunAfter("publish")
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    displayName = "$modName ${loader.capitalized()} $modVersion for $mcVersion"
    version = modVersion
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add(loader)

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null || providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.add(mcVersion)
        if (isFabric) requires {
            slug = "fabric-api"
        }
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.add(mcVersion)
        if (isFabric) requires {
            slug = "fabric-api"
        }
    }
}
