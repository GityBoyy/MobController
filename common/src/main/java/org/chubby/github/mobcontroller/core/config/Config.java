package org.chubby.github.mobcontroller.core.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.chubby.github.mobcontroller.core.config.property.BoolProperty;
import org.chubby.github.mobcontroller.core.config.property.DoubleProperty;
import org.chubby.github.mobcontroller.core.config.property.FloatProperty;
import org.chubby.github.mobcontroller.core.config.property.IntProperty;
import org.chubby.github.mobcontroller.core.config.property.impl.ConfigProperty;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Class for managing the configuration properties of the Mob Controller.
 * This class provides functionality to save and load configuration
 * properties to and from a JSON file.
 */
public class Config {
    private static final Gson GSON = new Gson();
    private static final String CONFIG_PATH = "msconfig.json";

    /**
     * Saves the current configuration properties to a JSON file.
     * <p>
     *
     * 1. Create a new JsonObject to hold the configuration data.<br>
     * 2. Iterate through the declared fields of the MCConfig class.<br>
     * 3. For each field annotated with @ConfigProperty:<br>
     *    - Set it accessible to retrieve its value.<br>
     *    - Check the type of the property (IntProperty, BoolProperty, etc.).<br>
     *    - Create a JSON representation of the property and add it to the JsonObject.<br>
     * 4. Write the JsonObject to the specified JSON file using Gson.<br>
     */
    public static void saveConfig() {
        JsonObject configJson = new JsonObject();

        Field[] fields = MCConfig.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                try {
                    field.setAccessible(true);
                    Object property = field.get(null);

                    if (property instanceof IntProperty) {
                        IntProperty intProperty = (IntProperty) property;
                        configJson.add("intProperty", createJsonObject(intProperty));
                    } else if (property instanceof BoolProperty) {
                        BoolProperty boolProperty = (BoolProperty) property;
                        configJson.add("boolProperty", createJsonObject(boolProperty));
                    } else if (property instanceof FloatProperty) {
                        FloatProperty floatProperty = (FloatProperty) property;
                        configJson.add("floatProperty", createJsonObject(floatProperty));
                    } else if (property instanceof DoubleProperty) {
                        DoubleProperty doubleProperty = (DoubleProperty) property;
                        configJson.add("doubleProperty", createJsonObject(doubleProperty));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            GSON.toJson(configJson, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads configuration properties from a JSON file.
     * <p>
     * Algorithm:
     * 1. Open the JSON file for reading.
     * 2. Use Gson to parse the JSON data into a JsonObject.
     * 3. Iterate through the declared fields of the MCConfig class.
     * 4. For each field annotated with @ConfigProperty:
     *    - Set it accessible to retrieve its value.
     *    - Check the type of the property (IntProperty, BoolProperty, etc.).
     *    - If the corresponding property exists in the JsonObject, set its value from the JSON.
     * 5. Close the file reader.
     */
    public static void loadConfig() {
        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            JsonObject configJson = GSON.fromJson(reader, JsonObject.class);
            loadPropertiesFromJson(configJson);
        } catch (IOException e) {
            System.out.println("Config: " + CONFIG_PATH + " doesn't exist or is broken.");
        }
    }

    /**
     * Loads the property values from the given JSON object into the corresponding properties.
     *
     * @param configJson the JSON object containing the configuration properties
     */
    private static void loadPropertiesFromJson(JsonObject configJson) {
        Field[] fields = MCConfig.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                try {
                    field.setAccessible(true);
                    Object property = field.get(null);

                    if (property instanceof IntProperty && configJson.has("intProperty")) {
                        IntProperty intProperty = (IntProperty) property;
                        JsonObject intObj = configJson.getAsJsonObject("intProperty");
                        intProperty.setValue(intObj.get("value").getAsInt());
                    } else if (property instanceof BoolProperty && configJson.has("boolProperty")) {
                        BoolProperty boolProperty = (BoolProperty) property;
                        JsonObject boolObj = configJson.getAsJsonObject("boolProperty");
                        boolProperty.setValue(boolObj.get("value").getAsBoolean());
                    } else if (property instanceof FloatProperty && configJson.has("floatProperty")) {
                        FloatProperty floatProperty = (FloatProperty) property;
                        JsonObject floatObj = configJson.getAsJsonObject("floatProperty");
                        floatProperty.setValue(floatObj.get("value").getAsFloat());
                    } else if (property instanceof DoubleProperty && configJson.has("doubleProperty")) {
                        DoubleProperty doubleProperty = (DoubleProperty) property;
                        JsonObject doubleObj = configJson.getAsJsonObject("doubleProperty");
                        doubleProperty.setValue(doubleObj.get("value").getAsDouble());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates a JSON object representing an IntProperty.
     *
     * @param intProperty the IntProperty to convert to JSON
     * @return a JsonObject representation of the IntProperty
     */
    private static JsonObject createJsonObject(IntProperty intProperty) {
        JsonObject json = new JsonObject();
        json.addProperty("name", intProperty.getName());
        json.addProperty("value", intProperty.getValue());
        return json;
    }

    /**
     * Creates a JSON object representing a BoolProperty.
     *
     * @param boolProperty the BoolProperty to convert to JSON
     * @return a JsonObject representation of the BoolProperty
     */
    private static JsonObject createJsonObject(BoolProperty boolProperty) {
        JsonObject json = new JsonObject();
        json.addProperty("name", boolProperty.getName());
        json.addProperty("value", boolProperty.getValue());
        return json;
    }

    /**
     * Creates a JSON object representing a FloatProperty.
     *
     * @param floatProperty the FloatProperty to convert to JSON
     * @return a JsonObject representation of the FloatProperty
     */
    private static JsonObject createJsonObject(FloatProperty floatProperty) {
        JsonObject json = new JsonObject();
        json.addProperty("name", floatProperty.getName());
        json.addProperty("value", floatProperty.getValue());
        return json;
    }

    /**
     * Creates a JSON object representing a DoubleProperty.
     *
     * @param doubleProperty the DoubleProperty to convert to JSON
     * @return a JsonObject representation of the DoubleProperty
     */
    private static JsonObject createJsonObject(DoubleProperty doubleProperty) {
        JsonObject json = new JsonObject();
        json.addProperty("name", doubleProperty.getName());
        json.addProperty("value", doubleProperty.getValue());
        return json;
    }

    /**
     * Creates a new BoolProperty.
     *
     * @param name        the name of the property
     * @param value       the initial value of the property
     * @param description the description of the property
     * @return a new BoolProperty instance
     */
    public static BoolProperty createBoolProp(String name, boolean value, String description) {
        return new BoolProperty(name, value, description);
    }

    /**
     * Creates a new IntProperty.
     *
     * @param name        the name of the property
     * @param value       the initial value of the property
     * @param description the description of the property
     * @return a new IntProperty instance
     */
    public static IntProperty createIntProp(String name, int value, String description) {
        return new IntProperty(name, value, description);
    }

    /**
     * Creates a new FloatProperty.
     *
     * @param name        the name of the property
     * @param value       the initial value of the property
     * @param description the description of the property
     * @return a new FloatProperty instance
     */
    public static FloatProperty createFloatProp(String name, float value, String description) {
        return new FloatProperty(name, value, description);
    }

    /**
     * Creates a new DoubleProperty.
     *
     * @param name        the name of the property
     * @param value       the initial value of the property
     * @param description the description of the property
     * @return a new DoubleProperty instance
     */
    public static DoubleProperty createDoubleProp(String name, double value, String description) {
        return new DoubleProperty(name, value, description);
    }
}
