package dev.kikugie.elytratrims.platform

import dev.kikugie.elytratrims.common.*

/*? if fabric {*/
object ETCommonWrapper : net.fabricmc.api.ModInitializer {
    override fun onInitialize() {
        ETCommon.init()
    }
}
/*?} elif forge {*/
/*import dev.kikugie.elytratrims.client.ETClient
import dev.kikugie.elytratrims.client.config.lib.ConfigScreenProvider
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runWhenOn

@Mod(ETReference.MOD_ID)
object ETCommonWrapper {
    init {
        ETCommon.init()
        runWhenOn(Dist.CLIENT) {
            ETClient.init()
            MOD_BUS.addListener<RegisterClientReloadListenersEvent> {
                it.registerReloadListener(ETAtlasHolder)
            }
            ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenFactory::class.java,
            ) { ConfigScreenFactory { _, parent -> ConfigScreenProvider.open(parent) } }
        }
    }

}
*//*?} else {*/
/*import dev.kikugie.elytratrims.client.ETClient
import dev.kikugie.elytratrims.client.config.lib.ConfigScreenProvider
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder
import net.minecraft.registry.Registries
import net.neoforged.fml.common.Mod
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModLoadingContext
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn
import java.util.function.Supplier

//? if <1.20.6 {
typealias CSF = net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory
//?} else
/^typealias CSF = net.neoforged.neoforge.client.gui.IConfigScreenFactory^/

@Mod(ETReference.MOD_ID)
object ETCommonWrapper {
    init {
        ETCommon.init()
        runWhenOn(Dist.CLIENT) {
            ETClient.init()
            MOD_BUS.addListener<RegisterClientReloadListenersEvent> {
                it.registerReloadListener(ETAtlasHolder)
            }

            ModLoadingContext.get().registerExtensionPoint(
                CSF::class.java,
            ) { CSF { _, parent -> ConfigScreenProvider.open(parent) } }
        }
    }
}
  *//*?}*/