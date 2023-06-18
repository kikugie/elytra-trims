package dev.kikugie.elytratrims.config;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.kikugie.commandconfig.api.builders.CategoryBuilder;
import dev.kikugie.commandconfig.api.builders.CommandConfigBuilder;
import dev.kikugie.commandconfig.api.option.ExtendedOptions;
import dev.kikugie.commandconfig.api.option.SimpleOptions;
import dev.kikugie.commandconfig.api.util.Defaults;
import dev.kikugie.elytratrims.ElytraTrimsMod;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class CommandConfig {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(command())));
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> command() {
        ConfigState config = ElytraTrimsMod.getConfigState();
        return CommandConfigBuilder.client("et-config").
                category((source) -> {
                    var category = CategoryBuilder.create("render", source);
                    for (ConfigState.RenderType type : ConfigState.RenderType.values()) {
                        category.option((ignored) -> ExtendedOptions.enumArg(type.type, ConfigState.RenderMode.class, source)
                                .valueAccess(
                                        () -> Text.translatable("elytratrims.command.response.get_mode",
                                                type.getName(),
                                                ElytraTrimsMod.getConfigState().getFor(type).getName()).formatted(Formatting.GREEN),
                                        (value) -> {
                                            config.setFor(type, value);
                                            return Text.translatable("elytratrims.command.response.set_mode",
                                                    type.getName(),
                                                    value.getName()).formatted(Formatting.GREEN);
                                        })
                                .helpFunc(type::getTooltip)
                        );
                    }
                    return category;
                })
                .category((source) -> CategoryBuilder.create("misc", source)
                        .option((unused) -> SimpleOptions.bool("lock_pack", source)
                                .valueAccess(Defaults.defaultValueAccess(
                                        () -> config.misc.lockDefaultPack.value,
                                        (value) -> config.misc.lockDefaultPack.value = value))
                                .helpFunc(config.misc.lockDefaultPack::getTooltip)))
                .node((node) -> {
                    LiteralArgumentBuilder<FabricClientCommandSource> literal = literal("reset");
                    node.then(literal.executes(context -> {
                        config.reset();
                        return 1;
                    }));
                })
                .saveFunc(config::save)
                .build();
    }
}
