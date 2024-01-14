plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.4-SNAPSHOT" apply false
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
