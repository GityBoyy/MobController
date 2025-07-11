package org.chubby.github.mobcontroller.platform.services;

import org.chubby.github.mobcontroller.Constants;

import java.util.ServiceLoader;

public class Services {

    private static IRegistryHelper.IServerRegistry serverRegistry;
    private static IRegistryHelper.IClientRegistry clientRegistry;
    private static IMenuHelper menuHelper;

    public static IRegistryHelper.IServerRegistry REGISTRY_HELPER() {
        if (serverRegistry == null) {
            serverRegistry = load(IRegistryHelper.IServerRegistry.class);
        }
        return serverRegistry;
    }

    public static IRegistryHelper.IClientRegistry CLIENT_REGISTRY_HELPER() {
        if (clientRegistry == null) {
            clientRegistry = load(IRegistryHelper.IClientRegistry.class);
        }
        return clientRegistry;
    }

    public static IMenuHelper MENU_HELPER() {
        if (menuHelper == null) {
            menuHelper = load(IMenuHelper.class);
        }
        return menuHelper;
    }

    @Deprecated(forRemoval = true)
    public static final IRegistryHelper.IServerRegistry REGISTRY_HELPER = REGISTRY_HELPER();
    @Deprecated(forRemoval = true)
    public static final IRegistryHelper.IClientRegistry CLIENT_REGISTRY_HELPER = CLIENT_REGISTRY_HELPER();
    @Deprecated(forRemoval = true)
    public static final IMenuHelper MENU_HELPER = MENU_HELPER();

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}