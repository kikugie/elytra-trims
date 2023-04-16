package me.kikugie.elytratrims.mixin;

import me.kikugie.elytratrims.access.ArmorStandEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityMixin implements ArmorStandEntityAccessor {
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
