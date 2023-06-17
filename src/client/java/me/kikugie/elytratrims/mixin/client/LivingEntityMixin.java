package me.kikugie.elytratrims.mixin.client;

import me.kikugie.elytratrims.access.LivingEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityAccessor {
    private boolean isGui = false;

    @Override
    public void markGui() {
        isGui = true;
    }

    @Override
    public boolean isGui() {
        return isGui;
    }
}
