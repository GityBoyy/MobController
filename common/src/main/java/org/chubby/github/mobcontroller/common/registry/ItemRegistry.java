package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.common.items.*;
import org.chubby.github.mobcontroller.util.Utils;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID,Registries.ITEM);

    public static final RegistrySupplier<Item> COPPER_CONTROLLER = registerItem("copper_controller",
            () -> new ItemController(new Item.Properties().stacksTo(1), ControllerType.COPPER));
    public static final RegistrySupplier<Item> IRON_CONTROLLER = registerItem("iron_controller",
            () -> new ItemController(new Item.Properties().stacksTo(1), ControllerType.IRON));
    public static final RegistrySupplier<Item> GOLD_CONTROLLER = registerItem("gold_controller",
            () -> new ItemController(new Item.Properties().stacksTo(1), ControllerType.GOLD));
    public static final RegistrySupplier<Item> DIAMOND_CONTROLLER = registerItem("diamond_controller",
            () -> new ItemController(new Item.Properties().stacksTo(1), ControllerType.DIAMOND));
    public static final RegistrySupplier<Item> NETHERITE_CONTROLLER = registerItem("netherite_controller",
            () -> new ItemController(new Item.Properties().stacksTo(1), ControllerType.NETHERITE));

    public static final RegistrySupplier<Item> COPPER_TO_IRON_UPGRADE = registerItem("copper_to_iron_upgrade",
            () -> new ItemControllerUpgrade(new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON), ControllerType.IRON));
    public static final RegistrySupplier<Item> IRON_TO_GOLD_UPGRADE = registerItem("iron_to_gold_upgrade",
            () -> new ItemControllerUpgrade(new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON), ControllerType.GOLD));
    public static final RegistrySupplier<Item> GOLD_TO_DIAMOND_UPGRADE = registerItem("gold_to_diamond_upgrade",
            () -> new ItemControllerUpgrade(new Item.Properties().stacksTo(8).rarity(Rarity.RARE), ControllerType.DIAMOND));
    public static final RegistrySupplier<Item> DIAMOND_TO_NETHERITE_UPGRADE = registerItem("diamond_to_netherite_upgrade",
            () -> new ItemControllerUpgrade(new Item.Properties().stacksTo(8).rarity(Rarity.EPIC), ControllerType.NETHERITE));

    public static final RegistrySupplier<Item> GOGGLES = registerItem("goggles",
            () -> new ItemGoggles(new Item.Properties().stacksTo(1)));
    public static final RegistrySupplier<Item> SOUL_ESSENCE = registerItem("soul_essence",
            ()-> new SoulEssence(new Item.Properties()));
    public static final RegistrySupplier<Item> TABLET = registerItem("tablet",
            ()-> new TabletItem(new Item.Properties()));

    public static RegistrySupplier<Item> registerItem(String name, Supplier<Item> supplier) {
        RegistrySupplier<Item> regObj = ITEMS.register(Utils.resource(name), supplier);
        regObj.listen(CreativeTabRegistry.ITEM_LIST::add);
        return regObj;
    }
}
