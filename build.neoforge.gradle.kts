plugins {
    kotlin("jvm")
    id("elytratrims.common")
    id("net.neoforged.moddev")
}

version = "${property("mod.version")}+${property("deps.minecraft")}"
base.archivesName = property("mod.id") as String

neoForge {
    version = property("deps.neoforge") as String
    validateAccessTransformers = true

    if (hasProperty("deps.parchment")) parchment {
        val (mc, ver) = (property("deps.parchment") as String).split(':')
        mappingsVersion = ver
        minecraftVersion = mc
    }

    runs {
        register("client") {
            gameDirectory = file("../../run/")
            client()
        }
    }

    mods {
        register("elytratrims") {
            sourceSet(sourceSets["main"])
        }
    }
}

dependencies {
    implementation("dev.nyon:KotlinLangForge:2.7.1-k2.1.21-3.0+neoforge")
}

tasks {
    processResources {
        exclude("**/fabric.mod.json", "**/elytratrims.accesswidener")
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}