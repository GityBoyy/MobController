package org.chubby.github.mobcontroller.platform.services;

import org.chubby.github.mobcontroller.Constants;

import java.util.ServiceLoader;

public class Services
{

    public static final IRegistryHelper.IServerRegistry REGISTRY_HELPER = load(IRegistryHelper.IServerRegistry.class);
    public static final IRegistryHelper.IClientRegistry CLIENT_REGISTRY_HELPER = load(IRegistryHelper.IClientRegistry.class);
    public static final IMenuHelper MENU_HELPER = load(IMenuHelper.class);

    public static <T> T load(Class<T> clazz)
    {
        final T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
