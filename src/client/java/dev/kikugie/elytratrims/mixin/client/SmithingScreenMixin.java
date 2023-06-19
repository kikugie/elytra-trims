package dev.kikugie.elytratrims.mixin.client;

import dev.kikugie.elytratrims.access.LivingEntityAccessor;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SmithingScreen.class)
public abstract class SmithingScreenMixin {
    private static final Identifier EMPTY_ARMOR_SLOT_ELYTRA_TEXTURE = new Identifier("elytratrims", "item/empty_armor_slot_elytra");
    private final Quaternionf elytraRotation = new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI);
    @Shadow
    private @Nullable ArmorStandEntity armorStand;
    private boolean isElytra = false;
    private boolean renderElytraOutline = false;

    @Inject(method = "setup", at = @At("TAIL"))
    private void markGuiArmorStand(CallbackInfo ci) {
        if (armorStand != null)
            ((LivingEntityAccessor) armorStand).markGui();
        if (Items.ELYTRA.getDefaultStack().isIn(ItemTags.TRIMMABLE_ARMOR))
            renderElytraOutline = true;
    }

    @ModifyArg(method = "handledScreenTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CyclingSlotIcon;updateTexture(Ljava/util/List;)V", ordinal = 1))
    private List<Identifier> renderElytraOutline(List<Identifier> original) {
        if (!renderElytraOutline || original.isEmpty())
            return original;
        ArrayList<Identifier> modified = new ArrayList<>(original);
        modified.add(EMPTY_ARMOR_SLOT_ELYTRA_TEXTURE);
        return modified;
    }

    @Inject(method = "equipArmorStand", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"), cancellable = true)
    private void equipElytra(ItemStack stack, CallbackInfo ci) {
        if (armorStand == null) return;
        if (stack.getItem() instanceof ElytraItem) {
            isElytra = true;
            armorStand.equipStack(EquipmentSlot.CHEST, stack.copy());
            ci.cancel();
            return;
        }
        isElytra = false;
    }

    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE",
            //#if MC >= 12000
            target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V"
            //#else
            //$$ target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/util/math/MatrixStack;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V"
            //#endif
    ), index = 4)
    private Quaternionf rotateElytra(Quaternionf quaternionf) {
        return isElytra ? elytraRotation : quaternionf;
    }
}
