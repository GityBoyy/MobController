package org.chubby.github.mobcontroller.neoforge.compat;

import mezz.jei.api.IModPlugin;
import net.minecraft.resources.ResourceLocation;
import org.chubby.github.mobcontroller.util.Utils;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin
{


    @Override
    public ResourceLocation getPluginUid() {
        return Utils.resource("mobcontroller_jei_plugin");
    }
}
