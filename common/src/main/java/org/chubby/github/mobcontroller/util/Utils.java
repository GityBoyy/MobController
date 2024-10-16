package org.chubby.github.mobcontroller.util;

import net.minecraft.resources.ResourceLocation;
import org.chubby.github.mobcontroller.Constants;

public class Utils
{
    public static ResourceLocation resource(String pPath)
    {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,pPath);
    }
}
