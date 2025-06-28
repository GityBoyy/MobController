package org.chubby.github.mobcontroller.common.registry;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.alchemy.Potion;
import org.chubby.github.mobcontroller.platform.services.Services;
import org.chubby.github.mobcontroller.util.Utils;

import java.util.function.Supplier;

public class PotionRegistry
{

    public static final Supplier<Potion> CHARGED_POTION = Services.CLIENT_REGISTRY_HELPER.registerPotion(Utils.resource("charged_potion"),
            Suppliers.memoize(Potion::new));
    public static final Supplier<Potion> ELECTROLYTE_POTION = Services.CLIENT_REGISTRY_HELPER.registerPotion(Utils.resource("electrolyte_potion"),
            Suppliers.memoize(Potion::new));

    public static void init(){}
}
