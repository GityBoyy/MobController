package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.inventory.MenuType;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryMenu;
import org.chubby.github.mobcontroller.mixin.MonsterMixin;
import org.chubby.github.mobcontroller.util.Utils;

public class MenuRegistry
{
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Constants.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<MonsterInventoryMenu>> MONSTER_MENU = MENUS.register(
            Utils.resource("monster_menu"),
            () -> dev.architectury.registry.menu.MenuRegistry.ofExtended((id, inventory, buf) -> {
                int monsterId = buf.readInt();
                Monster monster = (Monster) inventory.player.level().getEntity(monsterId);
                Container monsterContainer = new SimpleContainer(14);
                return new MonsterInventoryMenu(
                        id,
                        inventory,
                        monsterContainer,
                        monster
                );
            })
    );
}
