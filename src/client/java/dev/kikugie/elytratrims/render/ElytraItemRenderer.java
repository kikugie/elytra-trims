package dev.kikugie.elytratrims.render;

import com.mojang.datafixers.util.Pair;
import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.access.ElytraOverlaysAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class ElytraItemRenderer {
    private static final BannerBlockEntity DUMMY_BANNER = new BannerBlockEntity(BlockPos.ORIGIN, Blocks.WHITE_BANNER.getDefaultState());
    private static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, new Identifier("entity/shield/base"));

    @SuppressWarnings("DataFlowIssue")
    public static void renderElytraItemFeatures(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!ElytraTrims.getConfig().texture.showBannerIcon)
            return;

        List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns = ((ElytraOverlaysAccessor) (Object) stack).elytra_trims$getPatterns();
        int color = ((ElytraOverlaysAccessor) (Object) stack).elytra_trims$getColor();

        if (!patterns.isEmpty())
            renderBannerIcon(patterns, matrices, vertexConsumers, light, overlay);
        else if (color != 0)
            renderColoredIcon(color, matrices, vertexConsumers, light, overlay);
    }

    public static void renderColoredIcon(int color, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;

        BannerBlockEntityRenderer renderer = (BannerBlockEntityRenderer) MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(DUMMY_BANNER);
        assert renderer != null;

        matrices.push();
        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.push();
        matrices.translate(0.45F, 0.5F, 0F);
        matrices.scale(0.4F, -0.4F, -0.4F);

        renderer.banner.render(matrices, SHIELD_BASE.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline), light, overlay, red, green, blue, 1.0F);
        matrices.pop();
        matrices.pop();
    }

    public static void renderBannerIcon(List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BannerBlockEntityRenderer renderer = (BannerBlockEntityRenderer) MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(DUMMY_BANNER);
        assert renderer != null;

        matrices.push();
        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.push();
        matrices.translate(0.45F, 0.5F, 0F);
        matrices.scale(0.4F, -0.4F, -0.4F);

        for(int i = 0; i < 17 && i < patterns.size(); ++i) {
            Pair<RegistryEntry<BannerPattern>, DyeColor> pair = patterns.get(i);
            float[] fs = pair.getSecond().getColorComponents();
            Optional<RegistryKey<BannerPattern>> key = pair.getFirst().getKey();
            if (key.isEmpty())
                continue;

            SpriteIdentifier sprite = TexturedRenderLayers.getShieldPatternTextureId(key.get());
            renderer.banner.render(matrices, sprite.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline), light, overlay, fs[0], fs[1], fs[2], 1.0F);
        }
        matrices.pop();
        matrices.pop();
    }
}
