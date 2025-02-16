package org.chubby.github.mobcontroller.core.config;

import org.chubby.github.mobcontroller.core.config.property.IntProperty;
import org.chubby.github.mobcontroller.core.config.property.impl.ConfigProperty;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MCConfig {
    @ConfigProperty
    public static IntProperty controlTick = Config.createIntProp("controlTick", 250, "Determines For How Long The MobController Will Function");

    private static final Map<String, Object> properties = new ConcurrentHashMap<>();

    static {
        Field[] fields = MCConfig.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                try {
                    properties.put(field.getName(), field.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Object getProperty(String name) {
        return properties.get(name);
    }
}
