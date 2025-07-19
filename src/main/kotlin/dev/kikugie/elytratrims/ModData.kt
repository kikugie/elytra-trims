package dev.kikugie.elytratrims

import java.nio.file.Path

interface ModDataSpec {
    val isDevEnv: Boolean
    val isClient: Boolean
    val isServer: Boolean
    val gameDir: Path
}

//? if fabric {
object ModData : ModDataSpec {
    private val LOADER = net.fabricmc.loader.api.FabricLoader.getInstance()

    override val isDevEnv: Boolean get() = LOADER.isDevelopmentEnvironment
    override val isClient: Boolean get() = LOADER.environmentType == net.fabricmc.api.EnvType.CLIENT
    override val isServer: Boolean get() = LOADER.environmentType == net.fabricmc.api.EnvType.SERVER
    override val gameDir: Path get() = LOADER.gameDir
}
//?} else {
/*object ModData : ModDataSpec {
    override val isDevEnv: Boolean get() = false
    override val isClient: Boolean get() = net.neoforged.fml.loading.FMLLoader.getDist().isClient
    override val isServer: Boolean get() = net.neoforged.fml.loading.FMLLoader.getDist().isDedicatedServer
    override val gameDir: Path get() = TODO("Not yet implemented")
}
*///?}