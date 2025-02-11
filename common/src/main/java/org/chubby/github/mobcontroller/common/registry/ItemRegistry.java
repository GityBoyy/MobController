package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.items.ControllerType;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.util.Utils;

public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID,
            Registries.ITEM);

    public static RegistrySupplier<Item> COPPER_CONTROLLER = ITEMS.register(Utils.resource("copper_controller"),
            Suppliers.memoize(()->new ItemController(new Item.Properties().stacksTo(1),ControllerType.COPPER)));
    public static RegistrySupplier<Item> IRON_CONTROLLER = ITEMS.register(Utils.resource("iron_controller"),
            Suppliers.memoize(()->new ItemController(new Item.Properties().stacksTo(1),ControllerType.IRON)));
    public static RegistrySupplier<Item> GOLD_CONTROLLER = ITEMS.register(Utils.resource("gold_controller"),
            Suppliers.memoize(()->new ItemController(new Item.Properties().stacksTo(1),ControllerType.GOLD)));
    public static RegistrySupplier<Item> DIAMOND_CONTROLLER = ITEMS.register(Utils.resource("diamond_controller"),
            Suppliers.memoize(()->new ItemController(new Item.Properties().stacksTo(1),ControllerType.DIAMOND)));
    public static RegistrySupplier<Item> NETHERITE_CONTROLLER = ITEMS.register(Utils.resource("netherite_controller"),
            Suppliers.memoize(()->new ItemController(new Item.Properties().stacksTo(1),ControllerType.NETHERITE)));


}
