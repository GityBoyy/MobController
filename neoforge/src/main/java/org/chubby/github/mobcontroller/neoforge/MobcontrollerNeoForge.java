package org.chubby.github.mobcontroller.neoforge;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.MobController;
import org.chubby.github.mobcontroller.client.MobControllerClient;
import org.chubby.github.mobcontroller.client.screen.DataDisplayerScreen;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryScreen;
import org.chubby.github.mobcontroller.client.screen.NeuralInterfaceStationScreen;
import org.chubby.github.mobcontroller.common.registry.CreativeTabRegistry;
import org.chubby.github.mobcontroller.common.registry.ItemRegistry;
import org.chubby.github.mobcontroller.common.registry.MenusRegistry;
import org.chubby.github.mobcontroller.common.registry.PotionRegistry;
import org.chubby.github.mobcontroller.core.CommandsInit;
import org.chubby.github.mobcontroller.debug.screen.DebugScreen;
import org.chubby.github.mobcontroller.neoforge.platform.service.NeoforgeRegistryHelper;
import org.chubby.github.mobcontroller.neoforge.wrapper.NeoForgeEnergyWrapper;
import org.chubby.github.mobcontroller.util.Utils;

@Mod(Constants.MOD_ID)
public final class MobcontrollerNeoForge {

    public MobcontrollerNeoForge(IEventBus eventBus) {
        NeoforgeRegistryHelper.registerAll(eventBus);
        MobController.init();
        if(FMLEnvironment.dist.isClient())
        {
            MobControllerClient.initClient();
        }

        eventBus.addListener(this::registerMenuScreens);
        eventBus.addListener(this::buildCreativeContent);
        eventBus.addListener(NeoForgeEnergyWrapper::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.addListener(this::registerBrewingRecipes);
    }

    public void buildCreativeContent(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTab() == CreativeTabRegistry.MOB_CONTROLLER_TAB.get()){
            event.accept(ItemRegistry.COPPER_CONTROLLER.get());
            event.accept(ItemRegistry.IRON_CONTROLLER.get());
            event.accept(ItemRegistry.GOLD_CONTROLLER.get());
            event.accept(ItemRegistry.DIAMOND_CONTROLLER.get());
            event.accept(ItemRegistry.NETHERITE_CONTROLLER.get());

            event.accept(ItemRegistry.COPPER_TO_IRON_UPGRADE.get());
            event.accept(ItemRegistry.IRON_TO_GOLD_UPGRADE.get());
            event.accept(ItemRegistry.GOLD_TO_DIAMOND_UPGRADE.get());
            event.accept(ItemRegistry.DIAMOND_TO_NETHERITE_UPGRADE.get());

            event.accept(ItemRegistry.GOGGLES.get());
            event.accept(ItemRegistry.SOUL_ESSENCE.get());

            event.accept(ItemRegistry.BRAIN_PIECE.get());
            event.accept(ItemRegistry.BRAIN.get());
        }
    }

    private void registerCommands(RegisterCommandsEvent event)
    {
        CommandsInit.register(event.getDispatcher());
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenusRegistry.MONSTER_MENU.get(), MonsterInventoryScreen::new);
        event.register(MenusRegistry.DATA_DISPLAYER_MENU.get(), DataDisplayerScreen::new);
        event.register(MenusRegistry.NEURAL_INTERFACE_STATION_MENU.get(), NeuralInterfaceStationScreen::new);
    }

    public void registerBrewingRecipes(RegisterBrewingRecipesEvent event)
    {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.WATER, Items.QUARTZ, Holder.direct(PotionRegistry.CHARGED_POTION.get()));
        builder.addMix(Holder.direct(PotionRegistry.CHARGED_POTION.get()), Items.REDSTONE, Holder.direct(PotionRegistry.ELECTROLYTE_POTION.get()));
    }

}
