package org.chubby.github.mobcontroller.neoforge.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.client.screen.MonsterInventoryScreen;
import org.chubby.github.mobcontroller.common.registry.MenuRegistry;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientBusEvents
{
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event)
    {
        event.register(MenuRegistry.MONSTER_MENU.get(), MonsterInventoryScreen::new);
    }
}
