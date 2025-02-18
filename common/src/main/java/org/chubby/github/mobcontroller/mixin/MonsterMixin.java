package org.chubby.github.mobcontroller.mixin;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryMenu;
import org.chubby.github.mobcontroller.common.entity.IMonsterInventory;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(Monster.class)
public abstract class MonsterMixin extends Mob implements IMonsterInventory {

    // ========== Constants ==========
    @Unique private static final int INVENTORY_SIZE = 14;
    @Unique private static final int HELMET_SLOT = 1;
    @Unique private static final int CHESTPLATE_SLOT = 2;
    @Unique private static final int LEGGINGS_SLOT = 3;
    @Unique private static final int BOOTS_SLOT = 4;
    @Unique private static final int SPECIAL_SLOT = 5;
    @Unique private static final int[] INVENTORY_SLOTS = {6, 7, 8, 9, 10, 11, 12, 13};
    @Unique private static final float FOLLOW_DISTANCE = 4.0F;
    @Unique private static final float LOOK_ANGLE = 10.0F;

    // ========== Fields ==========
    @Unique private final SimpleContainer mobcontroller$inventory = new SimpleContainer(INVENTORY_SIZE);
    @Unique private boolean mobcontroller$isInventoryOpened = false;

    // ========== Constructor ==========
    protected MonsterMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    // ========== AI and Movement ==========
    @Inject(method = "aiStep", at = @At("HEAD"))
    private void aiStep(CallbackInfo ci) {
        Monster self = (Monster)(Object)this;
        mobcontroller$getControllingPlayer(self).ifPresent(player -> mobcontroller$updateControlledBehavior(self, player));
    }

    @Unique
    private Optional<Player> mobcontroller$getControllingPlayer(Monster monster) {
        return ItemController.getplayerMobControlMap().getInternalMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == monster)
                .map(Map.Entry::getKey)
                .findFirst()
                .map(uuid -> monster.level().getPlayerByUUID(uuid));
    }

    @Unique
    private void mobcontroller$updateControlledBehavior(Monster monster, Player player) {
        double distanceSquared = monster.distanceToSqr(player);

        if (distanceSquared >= FOLLOW_DISTANCE) {
            monster.getNavigation().moveTo(player, 1.0D);
        } else {
            monster.getNavigation().stop();
        }

        monster.getLookControl().setLookAt(player, LOOK_ANGLE, (float)monster.getMaxHeadXRot());
        monster.setAggressive(false);
        monster.setTarget(player.getLastHurtMob());
    }

    // ========== Inventory Management ==========
    @Override
    protected @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        Monster self = (Monster)(Object)this;

        if (mobcontroller$canOpenInventory(self, player)) {
            mobcontroller$openMonsterInventory(player, self);
            mobcontroller$updateArmorSlots(self);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Unique
    private boolean mobcontroller$canOpenInventory(Monster monster, Player player) {
        return mobcontroller$getControllingPlayer(monster)
                .map(controllingPlayer ->
                        controllingPlayer == player &&
                                !(player.getVehicle() instanceof Monster) &&
                                player.isShiftKeyDown() &&
                                !isInventoryOpened())
                .orElse(false);
    }

    // ========== Inventory Synchronization ==========
    @Unique
    private void mobcontroller$updateArmorSlots(Monster self) {
        for (int slot = HELMET_SLOT; slot <= BOOTS_SLOT; slot++) {
            ItemStack armorStack = mobcontroller$getInventory().getItem(slot);
            if (armorStack.getItem() instanceof ArmorItem armorItem) {
                mobcontroller$handleArmorEquip(self, armorItem.getEquipmentSlot(), armorStack, slot);
            }
        }
    }

    @Unique
    private void mobcontroller$handleArmorEquip(Monster monster, EquipmentSlot equipmentSlot, ItemStack newArmor, int inventorySlot) {
        ItemStack existingArmor = monster.getItemBySlot(equipmentSlot);
        if (!existingArmor.isEmpty()) {
            mobcontroller$storeExistingArmor(existingArmor);
        }

        monster.setItemSlot(equipmentSlot, newArmor.copy());
        mobcontroller$getInventory().setItem(inventorySlot, ItemStack.EMPTY);
    }

    @Unique
    private void mobcontroller$storeExistingArmor(ItemStack armor) {
        for (int slot : INVENTORY_SLOTS) {
            if (mobcontroller$getInventory().getItem(slot).isEmpty()) {
                mobcontroller$getInventory().setItem(slot, armor);
                break;
            }
        }
    }

    // ========== Persistence ==========
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        mobcontroller$saveInventory(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        Monster self = (Monster)(Object)this;
        mobcontroller$loadInventory(tag, self);
        mobcontroller$syncArmorWithInventory(self);
    }

    @Unique
    private void mobcontroller$saveInventory(CompoundTag tag) {
        ListTag inventoryTag = new ListTag();
        SimpleContainer inventory = mobcontroller$getInventory();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stack.save(this.registryAccess(), itemTag);
                inventoryTag.add(itemTag);
            }
        }

        tag.put("MonsterInventory", inventoryTag);
    }

    @Unique
    private void mobcontroller$loadInventory(CompoundTag tag, Monster monster) {
        if (tag.contains("MonsterInventory", Tag.TAG_LIST)) {
            ListTag inventoryTag = tag.getList("MonsterInventory", Tag.TAG_COMPOUND);

            for (int i = 0; i < inventoryTag.size(); i++) {
                CompoundTag itemTag = inventoryTag.getCompound(i);
                int slot = itemTag.getInt("Slot");

                if (mobcontroller$isValidSlot(slot)) {
                    mobcontroller$loadItemIntoSlot(monster, itemTag, slot);
                }
            }
        }
    }

    @Unique
    private boolean mobcontroller$isValidSlot(int slot) {
        return slot >= 0 && slot < mobcontroller$getInventory().getContainerSize();
    }

    @Unique
    private void mobcontroller$loadItemIntoSlot(Monster monster, CompoundTag itemTag, int slot) {
        ItemStack stack = ItemStack.parseOptional(this.registryAccess(), itemTag);

        if (mobcontroller$isArmorSlot(slot) && stack.getItem() instanceof ArmorItem armorItem) {
            mobcontroller$handleArmorLoad(monster, armorItem.getEquipmentSlot(), stack);
        } else {
            mobcontroller$getInventory().setItem(slot, stack);
        }
    }

    @Unique
    private boolean mobcontroller$isArmorSlot(int slot) {
        return slot >= HELMET_SLOT && slot <= BOOTS_SLOT;
    }

    @Unique
    private void mobcontroller$handleArmorLoad(Monster monster, EquipmentSlot equipmentSlot, ItemStack newArmor) {
        ItemStack existingArmor = monster.getItemBySlot(equipmentSlot);
        if (!existingArmor.isEmpty()) {
            mobcontroller$handleExistingArmorOnLoad(monster, existingArmor);
        }
        monster.setItemSlot(equipmentSlot, newArmor.copy());
    }

    @Unique
    private void mobcontroller$handleExistingArmorOnLoad(Monster monster, ItemStack existingArmor) {
        boolean stored = false;
        for (int slot : INVENTORY_SLOTS) {
            if (mobcontroller$getInventory().getItem(slot).isEmpty()) {
                mobcontroller$getInventory().setItem(slot, existingArmor);
                stored = true;
                break;
            }
        }
        if (!stored) {
            monster.spawnAtLocation(existingArmor);
        }
    }

    // ========== Tick Update ==========
    @Override
    public void tick() {
        super.tick();
        if (isInventoryOpened()) {
            mobcontroller$syncArmorWithInventory((Monster)(Object)this);
        }
    }

    @Unique
    private void mobcontroller$syncArmorWithInventory(Monster monster) {
        mobcontroller$syncArmorSlot(monster, EquipmentSlot.HEAD, HELMET_SLOT);
        mobcontroller$syncArmorSlot(monster, EquipmentSlot.CHEST, CHESTPLATE_SLOT);
        mobcontroller$syncArmorSlot(monster, EquipmentSlot.LEGS, LEGGINGS_SLOT);
        mobcontroller$syncArmorSlot(monster, EquipmentSlot.FEET, BOOTS_SLOT);
    }

    @Unique
    private void mobcontroller$syncArmorSlot(Monster monster, EquipmentSlot equipmentSlot, int inventorySlot) {
        ItemStack equippedItem = monster.getItemBySlot(equipmentSlot);
        ItemStack inventoryItem = mobcontroller$getInventory().getItem(inventorySlot);

        if (!ItemStack.matches(equippedItem, inventoryItem)) {
            mobcontroller$getInventory().setItem(inventorySlot, equippedItem.copy());
        }
    }

    // ========== Inventory Interface ==========
    @Override
    public boolean isInventoryOpened() {
        return mobcontroller$isInventoryOpened;
    }

    @Override
    public void setInventoryOpened(boolean inventoryOpened) {
        mobcontroller$isInventoryOpened = inventoryOpened;
    }

    @Unique
    private SimpleContainer mobcontroller$getInventory() {
        return mobcontroller$inventory;
    }

    @Unique
    public void mobcontroller$openMonsterInventory(Player player, Monster monster) {
        setInventoryOpened(true);
        if (player instanceof ServerPlayer serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer,
                    new MenuProvider() {
                        @Override
                        public @NotNull Component getDisplayName() {
                            return Component.empty();
                        }

                        @Override
                        public @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                            return new MonsterInventoryMenu(i, inventory, mobcontroller$getInventory(), monster);
                        }


                    },buf->buf.writeInt(monster.getId()));
        }
    }
}