package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Suppliers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CreativeTabRegistry
{

    public static final List<Item> ITEM_LIST = new ArrayList<>();

    public static final Supplier<CreativeModeTab> MOB_CONTROLLER_TAB = Services.REGISTRY_HELPER.registerCreativeTab(Utils.resource("mob_controller_tab"),
            Suppliers.memoize(()-> CreativeModeTab.builder(CreativeModeTab.Row.TOP,5)
                    .icon(() -> new ItemStack(ItemRegistry.COPPER_CONTROLLER.get()))
                    .title(Component.translatable("creativetab.mobcontroller.tab"))
                    .displayItems((itemDisplayParameters, output) -> ITEM_LIST.forEach(output::accept))
                    .build()));

    public static void init(){}
}
