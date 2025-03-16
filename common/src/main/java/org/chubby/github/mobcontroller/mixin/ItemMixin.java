package org.chubby.github.mobcontroller.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.common.items.SoulEssence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getCraftingRemainingItem", at = @At("HEAD"), cancellable = true)
    public void modifyCraftingRemainingItem(CallbackInfoReturnable<Item> cir) {
        Item self = (Item)(Object)this;
        if(self instanceof SoulEssence) {
            cir.setReturnValue(self);
        }
    }
}
