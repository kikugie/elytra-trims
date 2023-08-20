package dev.kikugie.elytratrims.config.lib;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.kikugie.commandconfig.api.builders.CategoryBuilder;
import dev.kikugie.commandconfig.api.builders.CommandConfigBuilder;
import dev.kikugie.commandconfig.api.option.ExtendedOptions;
import dev.kikugie.commandconfig.api.option.SimpleOptions;
import dev.kikugie.commandconfig.api.util.Defaults;
import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.config.ConfigLoader;
import dev.kikugie.elytratrims.config.ModConfig;
import dev.kikugie.elytratrims.config.RenderConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class CommandConfig {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(command())));
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> command() {
        ModConfig config = ElytraTrims.getConfig();
        return CommandConfigBuilder.client("et-config")
                .category(source -> {
                    var category = CategoryBuilder.create("render", source)
                            .node((node) -> {
                                LiteralArgumentBuilder<FabricClientCommandSource> literal = literal("reset");
                                node.then(literal.executes(context -> {
                                    config.render = new RenderConfig();
                                    return 1;
                                }));
                            });
                    createRenderOptons(config.render, category);
                    return category;
                })
                .category(source -> CategoryBuilder.create("texture", source)
                        .option(ignored -> SimpleOptions.bool("useBannerTextures", source)
                                .valueAccess(Defaults.defaultValueAccess(
                                        () -> config.texture.useBannerTextures,
                                        value -> {
                                            if (value != config.texture.useBannerTextures)
                                                MinecraftClient.getInstance().reloadResourcesConcurrently();
                                            config.texture.useBannerTextures = value;
                                        }))
                                .helpFunc(() -> Text.translatable("elytratrims.config.texture.useBannerTextures.tooltip")))
                        .option(ignored -> SimpleOptions.bool("cropTrims", source)
                                .valueAccess(Defaults.defaultValueAccess(
                                        () -> config.texture.cropTrims,
                                        value -> {
                                            if (value != config.texture.cropTrims)
                                                MinecraftClient.getInstance().reloadResourcesConcurrently();
                                            config.texture.cropTrims = value;
                                        }))
                                .helpFunc(() -> Text.translatable("elytratrims.config.texture.cropTrims.tooltip")))
                        .option(ignored -> SimpleOptions.bool("useDarkerTrim", source)
                                .valueAccess(Defaults.defaultValueAccess(
                                        () -> config.texture.useDarkerTrim,
                                        value -> config.texture.useDarkerTrim = value))
                                .helpFunc(() -> Text.translatable("elytratrims.config.texture.useDarkerTrim.tooltip"))))
                .saveFunc(() -> ConfigLoader.saveConfig(config))
                .build();
    }

    private static void createRenderOptons(RenderConfig config, CategoryBuilder<FabricClientCommandSource> category) {
        for (RenderConfig.RenderType type : RenderConfig.RenderType.values()) {
            category.option(ignored -> ExtendedOptions.enumArg(type.asString(), RenderConfig.RenderMode.class, FabricClientCommandSource.class)
                    .valueAccess(
                            () -> Text.translatable("elytratrims.command.response.get_mode",
                                    type.getName(),
                                    config.get(type).getName()).formatted(Formatting.GREEN),
                            (value) -> {
                                config.set(type, value);
                                return Text.translatable("elytratrims.command.response.set_mode",
                                        type.getName(),
                                        value.getName()).formatted(Formatting.GREEN);
                            })
                    .helpFunc(type::getTooltip)
            );
        }
    }
}
