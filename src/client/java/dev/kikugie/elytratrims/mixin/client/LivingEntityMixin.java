package dev.kikugie.elytratrims.mixin.client;

import dev.kikugie.elytratrims.access.LivingEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityAccessor {
    @Unique
    private boolean isGui = false;

    @Override
    public void elytra_trims$markGui() {
        this.isGui = true;
    }

    @Override
    public boolean elytra_trims$isGui() {
        return this.isGui;
    }
}
