import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import club.minnced.discord.webhook.send.WebhookMessageBuilder

plugins {
    id("dev.kikugie.stonecutter")
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("dev.architectury.loom") version "1.4-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.4.+" apply false
}
stonecutter active "1.19.4-fabric" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledPublishMods", stonecutter.chiseled) {
    group = "project"
    ofTask("publishMods")
}

tasks.register("postUpdate") {
    group = "project"
    doLast {
        val url = env.fetchOrNull("WEBHOOK_URL") ?: return@doLast
        val client = WebhookClient.withUrl(url)
        val message = WebhookMessageBuilder()
            .addEmbeds(
                WebhookEmbedBuilder()
                    .setColor(0xadd8e6)
                    .setThumbnailUrl("https://cdn.modrinth.com/data/XpzGz7KD/8ff6751948e096f540e320681742d0b3b918931e.png")
                    .setTitle(WebhookEmbed.EmbedTitle("Elytra Trims ${rootProject.property("mod.version")}", null))
                    .addField(
                        WebhookEmbed.EmbedField(
                            false,
                            "Changelog",
                            """
                    ```${rootProject.file("CHANGELOG.md").readText().trim()}
                    ```
                """.trimIndent()
                        )
                    ).addField(
                        WebhookEmbed.EmbedField(
                            false,
                            "Links",
                            """
                    [Modrinth](https://modrinth.com/mod/elytra-trims) | [Curseforge](https://www.curseforge.com/minecraft/mc-mods/elytra-trims)
                """.trimIndent()
                        )
                    ).build()
            ).build()
        client.send(message)
    }
}