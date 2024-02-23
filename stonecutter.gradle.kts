plugins {
    id("dev.kikugie.stonecutter")
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

stonecutter registerChiseled tasks.register("chiseledPublishMaven", stonecutter.chiseled) {
    group = "project"
    ofTask("publish")
}
