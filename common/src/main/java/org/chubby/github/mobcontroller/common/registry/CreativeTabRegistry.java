package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreativeTabRegistry
{
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Constants.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final List<Item> ITEM_LIST = new ArrayList<>();
    public static final RegistrySupplier<CreativeModeTab> MOB_CONTROLLER_TAB = TABS.register(Utils.resource("mob_controller_tab"),
            Suppliers.memoize(()-> CreativeModeTab.builder(CreativeModeTab.Row.TOP,5)
                    .icon(() -> new ItemStack(ItemRegistry.COPPER_CONTROLLER.get()))
                    .title(Component.translatable("creativetab.mobcontroller.tab"))
                    .displayItems((itemDisplayParameters, output) -> ITEM_LIST.forEach(output::accept))
                    .build()));
}
