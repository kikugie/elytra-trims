package dev.kikugie.elytratrims

import java.nio.file.Path

interface ModDataSpec {
    val isDevEnv: Boolean
    val gameDir: Path
}

//? if fabric {
object ModData : ModDataSpec {
    private val LOADER = net.fabricmc.loader.api.FabricLoader.getInstance()

    override val isDevEnv: Boolean get() = LOADER.isDevelopmentEnvironment
    override val gameDir: Path get() = LOADER.gameDir
}
//?} else {
/*object ModData : ModDataSpec {
    override val isDevEnv: Boolean get() = false
    override val gameDir: Path get() = TODO("Not yet implemented")
}
*///?}