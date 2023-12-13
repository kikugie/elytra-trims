package dev.kikugie.elytratrims.mixin.client;

import dev.kikugie.elytratrims.client.access.ResourceTypeAccessor;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Save resource type to reload mod's atlas only for client resources.
 */
@Mixin(LifecycledResourceManagerImpl.class)
public class LifecycledResourceManagerImplMixin implements ResourceTypeAccessor {
    @Unique
    private ResourceType resourceType;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void saveResourceType(ResourceType type, List<?> packs, CallbackInfo ci) {
        resourceType = type;
    }

    @Override
    public ResourceType getResourceType() {
        return resourceType;
    }
}