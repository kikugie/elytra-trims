plugins {
    id("dev.kikugie.stonecutter")
    kotlin("jvm") version "2.1.0" apply false
    kotlin("plugin.serialization") version "2.1.0" apply false
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.+" apply false
}
stonecutter active "1.20.1-fabric" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuildAndCollect", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

stonecutter registerChiseled tasks.register("chiseledPublishMods", stonecutter.chiseled) {
    group = "project"
    ofTask("publishMods")
}

stonecutter registerChiseled tasks.register("chiseledPublishForge", stonecutter.chiseled) {
    versions { _, it -> it.project.endsWith("forge") }
    group = "project"
    ofTask("publishMods")
}

stonecutter configureEach {
    val data = current.project.split('-')
    val platforms = listOf("fabric", "forge", "neoforge")
        .map { it to (it == data[1]) }
    consts(platforms)
    swap("mc", "\"${data[0]}\"")

    val is21 = eval(current.version, ">=1.21")
    val oldRender = """
        operation.call(model, matrices, vertices, light, overlay, red, green, blue, alpha);
        ElytraTrimsAPI.renderFeatures(model, matrices, provider, entity, stack, light, red, green, blue, alpha);
        """.trimIndent().prependIndent("        ")

    swap("render_call") {
        if (is21) """
        operation.call(model, matrices, vertices, light, overlay);
        ElytraTrimsAPI.renderFeatures(model, matrices, provider, entity, stack, light, -1);
        """.trimIndent().prependIndent("        ")
        else oldRender
    }
    swap("render_call_color") {
        if (is21) """
        operation.call(model, matrices, vertices, light, overlay, color);
        ElytraTrimsAPI.renderFeatures(model, matrices, provider, entity, stack, light, color);
        """.trimIndent().prependIndent("        ")
        else oldRender
    }
}
