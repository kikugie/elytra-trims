plugins {
    id("dev.kikugie.stonecutter")
    kotlin("jvm") version "2.1.0" apply false
    kotlin("plugin.serialization") version "2.1.0" apply false
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.+" apply false
}
stonecutter active "1.20.1-fabric"

stonecutter parameters {
    val (mc, loader) = metadata.project.split('-')
    val platforms = listOf("fabric", "forge", "neoforge")
        .map { it to (it == loader) }
    consts(platforms)
    swap("mc", "\"$mc\"")

    val is21 = eval(metadata.version, ">=1.21")
    val oldRender = """
        operation.call(model, matrices, vertices, light, overlay, red, green, blue, alpha);
                ElytraTrimsAPI.renderFeatures(model, matrices, provider, entity, stack, light, red, green, blue, alpha);
        """.trimIndent()

    swap("render_call") {
        if (is21) """
        operation.call(model, matrices, vertices, light, overlay);
                ElytraTrimsAPI.renderFeatures(model, matrices, provider, entity, stack, light, -1);
        """.trimIndent()
        else oldRender
    }
    swap("render_call_color") {
        if (is21) """
        operation.call(model, matrices, vertices, light, overlay, color);
                ElytraTrimsAPI.renderFeatures(model, matrices, provider, entity, stack, light, color);
        """.trimIndent()
        else oldRender
    }
}
