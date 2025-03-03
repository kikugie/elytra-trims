plugins {
    kotlin("jvm") version "2.1.10" apply false
    id("dev.kikugie.stonecutter")
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.+" apply false
}
stonecutter active "1.21.4-fabric"

stonecutter registerChiseled tasks.register("chiseledBuildAndCollect", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

tasks.register<PublishDiscordTask>("publishDiscord") {
    fun prop(string: String) = project.property(string) as String
    stonecutter.tree.nodes.map { "${it.project.path}:buildAndCollect" }
        .let { mustRunAfter(*it.toTypedArray()) }
    dependsOn("chiseledBuildAndCollect")

    files = objects.directoryProperty().value(project.layout.buildDirectory.dir("libs"))
    changelog = file("CHANGES.md").readText()
    version = prop("mod.version")

    token = env.fetch("DISCORD_TOKEN", "_")
    server = env.fetch("DISCORD_SERVER", "0").toLong()
    channel = env.fetch("DISCORD_CHANNEL", "0").toLong()
    role = env.fetch("DISCORD_ROLE", "0").toLong()
}