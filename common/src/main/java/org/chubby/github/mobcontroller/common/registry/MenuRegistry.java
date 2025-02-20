package org.chubby.github.mobcontroller.common.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.inventory.MenuType;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryMenu;
import org.chubby.github.mobcontroller.common.data.MobControllerDataManager;
import org.chubby.github.mobcontroller.util.Utils;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Constants.MOD_ID, Registries.MENU);

    //TODO::FIX THIS MONSTORUSITY
    public static final RegistrySupplier<MenuType<MonsterInventoryMenu>> MONSTER_MENU = MENUS.register(
            Utils.resource("monster_menu"),
            () -> dev.architectury.registry.menu.MenuRegistry.ofExtended((id, inventory, buf) -> {
                int monsterId = buf.readInt();
                Entity entity = inventory.player.level().getEntity(monsterId);

                if (entity == null) {
                    throw new IllegalStateException("Could not find entity with ID: " + monsterId);
                }

                if (!(entity instanceof Monster monster)) {
                    throw new IllegalStateException("Entity with ID " + monsterId + " is not a monster");
                }

                SimpleContainer monsterContainer = MobControllerDataManager.getMonsterInventory(monster);
                return new MonsterInventoryMenu(
                        id,
                        inventory,
                        monsterContainer,
                        monster
                );
            })
    );
}