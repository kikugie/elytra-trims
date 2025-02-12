plugins {
    `maven-publish`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("dev.architectury.loom")
    id("me.modmuss50.mod-publish-plugin")
    id("me.fallenbreath.yamlang") version "1.3.1"
}

// Variables
class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

val mod = ModData()

val loader = loom.platform.get().name.lowercase()
val isFabric = loader == "fabric"
val mcVersion = stonecutter.current.project.substringBeforeLast('-')
val mcDep = property("mod.mc_dep").toString()
val isSnapshot = hasProperty("env.snapshot")

version = "${mod.version}+$mcVersion"
group = mod.group
base { archivesName.set("${mod.id}-$loader") }

// Dependencies
repositories {
    fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
    strictMaven("https://thedarkcolour.github.io/KotlinForForge/", "thedarkcolour")
    strictMaven("https://maven.fallenbreath.me/releases", "me.fallenbreath")
    maven("https://jitpack.io")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://maven.bawnorton.com/releases/")
}

dependencies {
    fun modrinth(name: String, dep: Any?) = "maven.modrinth:$name:$dep"
    fun modules(vararg modules: String) {
        modules.forEach { modImplementation(fabricApi.module("fabric-$it", "${property("deps.fapi")}")) }
    }

    fun ifStable(str: String, action: (String) -> Unit = { modImplementation(it) }) {
        if (isSnapshot) modCompileOnly(str) else action(str)
    }

    minecraft("com.mojang:minecraft:${mcVersion}")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
        if (stonecutter.eval(mcVersion, "1.20.6"))
            mappings("dev.architectury:yarn-mappings-patch-neoforge:1.20.5+build.3")
        else if (stonecutter.eval(mcVersion, "1.21"))
            mappings(rootProject.file("mappings/fix.tiny"))
    })
    val mixinExtras = "io.github.llamalad7:mixinextras-%s:${property("deps.mixin_extras")}"
    val mixinSquared = "com.github.bawnorton.mixinsquared:mixinsquared-%s:${property("deps.mixin_squared")}"
    implementation(annotationProcessor(mixinSquared.format("common"))!!)
    include(modImplementation("me.fallenbreath:conditional-mixin-$loader:${property("deps.cond_mixin")}")!!)
    if (isFabric) {
        modules("registry-sync-v0", "resource-loader-v0", "entity-events-v1")
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        modImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.flk")}+kotlin.2.1.0")
        include(implementation(mixinSquared.format("fabric"))!!)
        ifStable("com.terraformersmc:modmenu:${property("deps.modmenu")}")
    } else {
        if (loader == "forge") {
            "forge"("net.minecraftforge:forge:${mcVersion}-${property("deps.fml")}")
            compileOnly(annotationProcessor(mixinExtras.format("common"))!!)
            include(implementation(mixinExtras.format("forge"))!!)
        } else
            "neoForge"("net.neoforged:neoforge:${property("deps.fml")}")
        implementation("thedarkcolour:kotlinforforge${if (loader == "neoforge") "-neoforge" else ""}:${property("deps.kff")}")
        include(implementation(mixinSquared.format(loader))!!)
    }
    // Config

    // Compat
//    if (stonecutter.current.isActive) modLocalRuntime("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}") // Uncomment when a compat mod complaints about no fapi
    if (stonecutter.eval(mcVersion, ">=1.20.6") && loader == "neoforge") {
        compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.7.0")
        compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.0")
    }
    for (it in property("deps.compat").toString().split(',')) {
        @Suppress("UselessCallOnNotNull")
        if (it.isNullOrBlank()) continue
        val (modid, version) = it.split('=')
        modCompileOnly(modrinth(modid, version))
    }
}

// Loom config
loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/elytratrims.accesswidener"))

    if (loader == "forge") forge {
        convertAccessWideners.set(true)
        mixinConfigs(
            "${mod.id}-client.mixins.json",
            "${mod.id}-common.mixins.json",
            "${mod.id}-compat.mixins.json"
        )
    } else if (loader == "neoforge") neoForge {

    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "../../run"
        if (environment == "client") programArgs("--username=KikuGie") // Mom look I'm in the codebase!
    }

    decompilers {
        get("vineflower").apply {
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

// Tasks
val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }

    rootProject.tasks.register("runActive") {
        group = "project"
        dependsOn(tasks.named("runClient"))
    }
}

// Resources
tasks.processResources {
    inputs.property("version", mod.version)
    inputs.property("mc", mcDep)

    val map = mapOf(
        "version" to mod.version,
        "mc" to mcDep,
        "fml" to if (loader == "neoforge") "1" else "45",
        "mnd" to if (loader == "neoforge") "" else "mandatory = true"
    )

    fun FileCopyDetails.expandOrExclude(expand: Boolean, map: Map<String, String>): Any = if (expand) expand(map) else exclude()

    filesMatching("fabric.mod.json") { expandOrExclude(loader == "fabric", map) }
    filesMatching("META-INF/mods.toml") { expandOrExclude(loader == "forge", map) }
    filesMatching("META-INF/neoforge.mods.toml") { expandOrExclude(loader == "neoforge", map) }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/${mod.id}/lang")
}

// Env configuration
stonecutter {
    val j21 = eval(mcVersion, ">=1.20.6")
    java {
        withSourcesJar()
        sourceCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
        targetCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(if (j21) 21 else 17)
    }
}

// Publishing
publishMods {
    val modrinthToken = findProperty("modrinthToken")
    val curseforgeToken = findProperty("curseforgeToken")
    dryRun = modrinthToken == null || curseforgeToken == null

    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    displayName =
        "${mod.name} ${loader.replaceFirstChar { it.uppercase() }} ${mod.version} for ${property("mod.mc_title")}"
    version = mod.version
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add(loader)

    val targets = property("mod.mc_targets").toString().split(' ')
    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken.toString()
        targets.forEach(minecraftVersions::add)
        if (isFabric) {
            requires("fabric-api", "fabric-language-kotlin")
            optional("modmenu")
        } else requires("kotlin-for-forge")
        optional("yacl")
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseforgeToken.toString()
        targets.forEach(minecraftVersions::add)
        if (isFabric) {
            requires("fabric-api", "fabric-language-kotlin")
            optional("modmenu")
        } else requires("kotlin-for-forge")
        optional("yacl")
    }
}

publishing {
    repositories {
        maven("https://maven.kikugie.dev/releases") {
            name = "kikugieMaven"
            credentials(PasswordCredentials::class.java)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${property("mod.group")}.${mod.id}"
            artifactId = mod.version
            version = mcVersion

            from(components["java"])
        }
    }
}
