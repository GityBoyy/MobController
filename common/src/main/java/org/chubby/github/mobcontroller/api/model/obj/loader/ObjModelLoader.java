package org.chubby.github.mobcontroller.api.model.obj.loader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.api.model.obj.ObjModel;
import org.chubby.github.mobcontroller.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ObjModelLoader extends SimplePreparableReloadListener<Map<ResourceLocation, ObjModel>>
{
    private static ObjModelLoader INSTANCE;

    protected final Map<ResourceLocation,ObjModel> modelMap = new HashMap<>();

    @Override
    protected Map<ResourceLocation, ObjModel> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, ObjModel> models = new HashMap<>();

        resourceManager.listResources("models/obj", loc -> loc.getPath().endsWith(".obj"))
                .forEach((resourceLocation, resource) -> {
                    try(var reader = resource.openAsReader()) {
                        ObjModel model = ObjModelParser.parse(reader);
                        models.put(resourceLocation, model);
                        System.out.println("Loading OBJ model: " + resourceLocation);

                    } catch (IOException e) {
                        System.err.println("Failed to load OBJ model: " + resourceLocation);
                        throw new RuntimeException(e);
                    }
                });
        return models;
    }

    private void setupModelTextures(ObjModel model, ResourceLocation modelLocation) {
        String modelPath = modelLocation.getPath();
        String modelName = modelPath.substring(modelPath.lastIndexOf('/') + 1, modelPath.lastIndexOf('.'));

        ResourceLocation defaultTexture = Utils.resource("textures/block/" + modelName + ".png");
        model.defaultTexture = defaultTexture;

        for (ObjModel.MaterialData material : model.materials) {
            if (material.name != null) {
                ResourceLocation materialTexture = Utils.resource("textures/block/" + modelName + "_" + material.name + ".png");
                model.materialTextures.put(material.name, materialTexture);
                material.textureLocation = materialTexture;

                Constants.LOGGER.info("Mapped material '{}' to texture: {}", material.name, materialTexture);
            }
        }

        Constants.LOGGER.info("Set up textures for model '{}': default={}, materials={}",
                modelName, defaultTexture, model.materialTextures.size());
    }

    @Override
    protected void apply(Map<ResourceLocation, ObjModel> newModels, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        modelMap.clear(); // Clear old models first
        modelMap.putAll(newModels);
        System.out.println("Loaded " + modelMap.size() + " OBJ models.");

        // Debug: Print all loaded model keys
        for (ResourceLocation key : modelMap.keySet()) {
            System.out.println("Available model: " + key);
        }
    }

    public ObjModel getModel(ResourceLocation id) {
        ObjModel model = modelMap.get(id);
        if (model == null) {
            System.out.println("Model not found for ID: " + id);
            System.out.println("Available models: " + modelMap.keySet());
        }
        return model;
    }

    public static ObjModelLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ObjModelLoader();
        }
        return INSTANCE;
    }
}