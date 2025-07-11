package org.chubby.github.mobcontroller.common.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionContents;
import org.chubby.github.mobcontroller.common.items.*;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.Supplier;

public class ItemRegistry {

    public static final Supplier<Item> COPPER_CONTROLLER = registerItem("copper_controller",
            () -> new ItemController(new Item.Properties().stacksTo(1), ControllerType.COPPER));
    public static final Supplier<Item> IRON_CONTROLLER = registerItem("iron_controller",
            () -> new ItemController(new Item.Properties().stacksTo(1), ControllerType.IRON));
    public static final Supplier<Item> GOLD_CONTROLLER = registerItem("gold_controller",
            () -> new ItemController(new Item.Properties().stacksTo(1), ControllerType.GOLD));
    public static final Supplier<Item> DIAMOND_CONTROLLER = registerItem("diamond_controller",
            () -> new ItemController(new Item.Properties().stacksTo(1), ControllerType.DIAMOND));
    public static final Supplier<Item> NETHERITE_CONTROLLER = registerItem("netherite_controller",
            () -> new ItemController(new Item.Properties().stacksTo(1), ControllerType.NETHERITE));

    public static final Supplier<Item> COPPER_TO_IRON_UPGRADE = registerItem("copper_to_iron_upgrade",
            () -> new ItemControllerUpgrade(new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON), ControllerType.IRON));
    public static final Supplier<Item> IRON_TO_GOLD_UPGRADE = registerItem("iron_to_gold_upgrade",
            () -> new ItemControllerUpgrade(new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON), ControllerType.GOLD));
    public static final Supplier<Item> GOLD_TO_DIAMOND_UPGRADE = registerItem("gold_to_diamond_upgrade",
            () -> new ItemControllerUpgrade(new Item.Properties().stacksTo(8).rarity(Rarity.RARE), ControllerType.DIAMOND));
    public static final Supplier<Item> DIAMOND_TO_NETHERITE_UPGRADE = registerItem("diamond_to_netherite_upgrade",
            () -> new ItemControllerUpgrade(new Item.Properties().stacksTo(8).rarity(Rarity.EPIC), ControllerType.NETHERITE));

    public static final Supplier<Item> GOGGLES = registerItem("goggles",
            () -> new ItemGoggles(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> SOUL_ESSENCE = registerItem("soul_essence",
            () -> new SoulEssence(new Item.Properties()));

    public static final Supplier<Item> BRAIN_PIECE = registerItem("brain_piece",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BRAIN = registerItem("brain",
            () -> new Item(new Item.Properties().stacksTo(1)));

    // Fixed potion items - uncomment when ready to use
    // public static final Supplier<Item> CHARGED_POTION_BOTTLE = registerItem("charged_potion_bottle",
    //         () -> new PotionItem(new Item.Properties().stacksTo(1)
    //                 .component(DataComponents.POTION_CONTENTS, new PotionContents(PotionRegistry.CHARGED_POTION))));
    // public static final Supplier<Item> ELECTROLYTE = registerItem("electrolyte",
    //         () -> new PotionItem(new Item.Properties().stacksTo(8)
    //                 .component(DataComponents.POTION_CONTENTS, new PotionContents(Holder.direct(PotionRegistry.ELECTROLYTE_POTION.get())))));

    public static Supplier<Item> registerItem(String name, Supplier<Item> supplier) {
        ResourceLocation id = Utils.resource(name);
        return Services.REGISTRY_HELPER().registerItem(id, supplier);
    }

    public static void init() {
        // This method ensures the class is loaded and all static fields are initialized
    }
}