package org.chubby.github.mobcontroller.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.registry.DataComponentRegistry;
import org.chubby.github.mobcontroller.util.UtilityMethods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LivingEntity.class)
public class EntityTick
{

    @Inject(method = "tick",at = @At("HEAD"))
    public void tick(CallbackInfo ci)
    {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Monster monster)) return;

        var headItem = monster.getItemBySlot(EquipmentSlot.HEAD);
        if (!headItem.has(DataComponentRegistry.CONTROLLER.get())) return;

        var attachment = headItem.get(DataComponentRegistry.CONTROLLER.get());
        if (attachment == null) return;

        UUID playerUUID = attachment.getControllingPlayer();
        var player = monster.level().getPlayerByUUID(playerUUID);
        if (player == null) return;
        if(monster.getTarget() == player)
        {
            monster.setTarget(null);
            monster.setAggressive(false);
        }

        if (player.getLastHurtMob() != null) {
            ItemController.setMonsterState(ItemController.MonsterStates.AGGRESSIVE);
        } else if (player.getLastHurtByMob() != null) {
            ItemController.setMonsterState(ItemController.MonsterStates.DEFENSIVE);
        } else {
            ItemController.setMonsterState(ItemController.MonsterStates.PASSIVE);
        }

        EquipmentSlot[] armorSlots = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
        for (int i = 0; i < armorSlots.length; i++) {
            ItemStack armorPiece = attachment.mobInventory.getItem(i);
            if (armorPiece.getItem() instanceof ArmorItem) {
                monster.setItemSlot(armorSlots[i], armorPiece);
            }
        }

        UtilityMethods.updateMobBehavior(monster, player);
    }
}
