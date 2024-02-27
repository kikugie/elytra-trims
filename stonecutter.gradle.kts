import de.maxbossing.webhookbuilder.url
import de.maxbossing.webhookbuilder.webhook

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

    doLast {
        val url = env.WEBHOOK_URL.orNull() ?: return@doLast
        val hook = webhook {
            name("Release Bot")

            embed {
                title("Elytra Trims ${property("mod.version")}")
                thumbnail {
                    url("https://cdn.modrinth.com/data/XpzGz7KD/8ff6751948e096f540e320681742d0b3b918931e.png")
                }
                field {
                    name("Changelog")
                    value("""
                        ```
                        ${rootProject.file("CHANGELOG.md").readText()}
                        ```
                    """.trimIndent())
                }
                field {
                    name("Links")
                    value("[Modrinth](https://modrinth.com/mod/elytra-trims) | [Curseforge](https://www.curseforge.com/minecraft/mc-mods/elytra-trims)")
                }
            }
        }
        hook.send(url(url))
    }
}