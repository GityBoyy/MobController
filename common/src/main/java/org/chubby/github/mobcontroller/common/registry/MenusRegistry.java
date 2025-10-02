package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Suppliers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.chubby.github.mobcontroller.api.registry.MenuRegistry;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryScreen;
import org.chubby.github.mobcontroller.common.blocks.entity.ElectrolyticDiffuserBE;
import org.chubby.github.mobcontroller.common.data.SoulEssenceData;
import org.chubby.github.mobcontroller.common.menu.DataDisplayerMenu;
import org.chubby.github.mobcontroller.common.menu.ElectrolyticDiffuserMenu;
import org.chubby.github.mobcontroller.common.menu.MonsterInventoryMenu;
import org.chubby.github.mobcontroller.common.menu.NeuralInterfaceStationMenu;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.Supplier;

public class MenusRegistry
{

    public static final Supplier<MenuType<MonsterInventoryMenu>> MONSTER_MENU = Services.CLIENT_REGISTRY_HELPER().registerMenu(
            Utils.resource("monster_menu"),
            MonsterInventoryMenu::new
    );


    public static final Supplier<MenuType<DataDisplayerMenu>> DATA_DISPLAYER_MENU = Services.CLIENT_REGISTRY_HELPER().registerMenu(
            Utils.resource("data_displayer_menu"),
            (id, inventory,  buf) -> {
                var player = inventory.player;
                int monsterEntityId = buf.readInt();
                Monster monster = (Monster) player.level().getEntity(monsterEntityId);
                if(monster==null) return null;
                SoulEssenceData data= monster.getItemBySlot(EquipmentSlot.HEAD).get(DataComponentRegistry.SOUL_ESSENCE.get());
                if(data==null) return null;
                return new DataDisplayerMenu(id, inventory, data.getControlledMobIds());
            }
    );

    public static final Supplier<MenuType<NeuralInterfaceStationMenu>> NEURAL_INTERFACE_STATION_MENU = Services.CLIENT_REGISTRY_HELPER()
            .registerMenu(Utils.resource("neural_interface_station_menu"),
                    NeuralInterfaceStationMenu::new);

    public static final Supplier<MenuType<ElectrolyticDiffuserMenu>> ELECTROLYTIC_DIFFUSER_MENU = Services.CLIENT_REGISTRY_HELPER().registerMenu(
            Utils.resource("electrolytic_diffuser_menu"),
            (containerID, inventory, extraData) -> new ElectrolyticDiffuserMenu(containerID,inventory)
    );

    public static void init(){}
}
