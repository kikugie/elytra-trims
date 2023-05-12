package me.kikugie.elytratrims.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import me.kikugie.elytratrims.ElytraTrimsMod;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ConfigCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess ignoredAccess) {
        dispatcher.register(literal("et-config")
                .then(getRenderOptions())
                .then(literal("reset").executes(ConfigCommand::reset))
        );
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> getRenderOptions() {
        var source = literal("render");
        for (ConfigState.RenderType type : ConfigState.RenderType.values()) {
            source.then(getLiteralFor(type));
        }
        return source;
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> getLiteralFor(ConfigState.RenderType type) {
        return literal(type.type)
                .executes(context -> get(context, type))
                .then(argument("mode", RenderModeArgumentType.get())
                        .executes(context -> set(context, type)));
    }

    private static int get(CommandContext<FabricClientCommandSource> context, ConfigState.RenderType type) {
        context.getSource().sendFeedback(Text.translatable(
                "elytratrims.command.response.get_mode",
                type.getName(),
                ElytraTrimsMod.getConfigState().getFor(type).getName()).formatted(Formatting.GREEN));
        return 1;
    }

    private static int set(CommandContext<FabricClientCommandSource> context, ConfigState.RenderType type) {
        ConfigState.RenderMode value = context.getArgument("mode", ConfigState.RenderMode.class);
        ElytraTrimsMod.getConfigState().setFor(type, value);
        ElytraTrimsMod.getConfigState().save();
        context.getSource().sendFeedback(Text.translatable(
                "elytratrims.command.response.set_mode",
                type.getName(),
                value.getName()).formatted(Formatting.GREEN));
        return 1;
    }

    private static int reset(CommandContext<FabricClientCommandSource> context) {
        ElytraTrimsMod.getConfigState().reset();
        context.getSource().sendFeedback(Text.translatable("elytratrims.command.response.reset").formatted(Formatting.GREEN));
        return 1;
    }

    private static class RenderModeArgumentType extends EnumArgumentType<ConfigState.RenderMode> {
        public static final Codec<ConfigState.RenderMode> CODEC = StringIdentifiable.createCodec(ConfigState.RenderMode::values);

        private RenderModeArgumentType() {
            super(CODEC, ConfigState.RenderMode::values);
        }

        public static EnumArgumentType<ConfigState.RenderMode> get() {
            return new RenderModeArgumentType();
        }
    }
}
