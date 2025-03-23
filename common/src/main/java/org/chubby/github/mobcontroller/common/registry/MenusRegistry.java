package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Suppliers;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.data.DataDisplayerData;
import org.chubby.github.mobcontroller.common.data.SoulEssenceData;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.menu.DataDisplayerMenu;
import org.chubby.github.mobcontroller.common.menu.MonsterInventoryMenu;
import org.chubby.github.mobcontroller.common.menu.NeuralInterfaceStationMenu;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.UUID;

public class MenusRegistry
{
    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(Constants.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<MonsterInventoryMenu>> MONSTER_MENU = TYPES.register(
        Utils.resource("monster_menu"),
        () -> MenuRegistry.ofExtended((id, inventory, buf) -> {
            var player = inventory.player;
            int monsterEntityId = buf.readInt();
            Monster monster = (Monster) player.level().getEntity(monsterEntityId);
            return new MonsterInventoryMenu(id, inventory, monster);
        })
);

    public static final RegistrySupplier<MenuType<DataDisplayerMenu>> DATA_DISPLAYER_MENU = TYPES.register(
            Utils.resource("data_displayer_menu"),
            () -> MenuRegistry.ofExtended((id, inventory, buf) -> {
                var player = inventory.player;
                int monsterEntityId = buf.readInt();
                Monster monster = (Monster) player.level().getEntity(monsterEntityId);
                if(monster==null) return null;
                SoulEssenceData data= monster.getItemBySlot(EquipmentSlot.HEAD).get(DataComponentRegistry.SOUL_ESSENCE.get());
                if(data==null) return null;
                return new DataDisplayerMenu(id, inventory, data.getControlledMobIds());
            })
    );

    public static final RegistrySupplier<MenuType<NeuralInterfaceStationMenu>> NEURAL_INTERFACE_STATION_MENU = TYPES
            .register(Utils.resource("neural_interface_station_menu"),
                    Suppliers.memoize(()->MenuRegistry.ofExtended(NeuralInterfaceStationMenu::new)));
}
