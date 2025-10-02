package org.chubby.github.mobcontroller.client.model.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.api.model.obj.ObjModel;
import org.chubby.github.mobcontroller.api.model.obj.loader.ObjModelLoader;
import org.chubby.github.mobcontroller.util.Utils;
import org.joml.Vector3f;

public class ScepticTankModel {

    public static ScepticTankModel instance;
    private static final ResourceLocation MODEL_LOC = Utils.resource("models/obj/sceptic_tank.obj");
    private static final ObjModelLoader loader = ObjModelLoader.getInstance();

    private ScepticTankModel(){}

    public static void render(PoseStack stack, BlockPos pos, MultiBufferSource source, int packedLight, int packedOverlay) {
        Constants.LOGGER.info("=== Rendering ScepticTankModel at pos {} ===", pos);

        stack.pushPose();
        stack.translate(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
        stack.mulPose(Axis.YP.rotationDegrees(-90));

        ObjModel model = loader.getModel(MODEL_LOC);
        if (model == null) {
            Constants.LOGGER.error("Model [{}] could not be loaded!", MODEL_LOC);
            stack.popPose();
            return;
        }

        Constants.LOGGER.info("Model [{}] loaded: vertices={}, faces={}, normals={}, uvs={}",
                MODEL_LOC, model.vertices.size(), model.faces.size(), model.normals.size(), model.uvs.size());

        renderModel(stack, source, model, packedLight, packedOverlay);

        stack.popPose();
        Constants.LOGGER.info("=== Finished rendering ScepticTankModel ===");
    }

    private static void renderModel(PoseStack stack, MultiBufferSource source, ObjModel model, int packedLight, int packedOverlay) {
        VertexConsumer consumer = null;
        String currentMaterial = null;
        int faceCount = 0;
        for (ObjModel.FaceData face : model.faces) {
            if (consumer == null || !java.util.Objects.equals(currentMaterial, face.material)) {
                currentMaterial = face.material;
                ResourceLocation texture = getTextureForMaterial(model, currentMaterial);
                consumer = source.getBuffer(RenderType.entitySolid(texture));
                Constants.LOGGER.trace("Switched to material: {} with texture: {}", currentMaterial, texture);
            }
            renderFace(stack, consumer, model, face, packedLight, packedOverlay);
            faceCount++;
        }
        Constants.LOGGER.info("Rendered {} faces", faceCount);
    }

    private static ResourceLocation getTextureForMaterial(ObjModel model, String materialName) {
        if (materialName != null && model.materialTextures.containsKey(materialName)) {
            return model.materialTextures.get(materialName);
        }
        return model.defaultTexture != null ? model.defaultTexture : Utils.resource("textures/bl.png");
    }

    private static void renderFace(PoseStack stack, VertexConsumer consumer, ObjModel model, ObjModel.FaceData face, int packedLight, int packedOverlay) {
        if (face.vertexIndices.length < 3) {
            Constants.LOGGER.warn("Skipping invalid face with <3 vertices");
            return;
        }

        Vector3f[] vertices = new Vector3f[face.vertexIndices.length];
        Vector3f[] normals = new Vector3f[face.vertexIndices.length];
        Vector3f[] uvs = new Vector3f[face.vertexIndices.length];

        for (int i = 0; i < face.vertexIndices.length; i++) {
            vertices[i] = getVertex(model, face.vertexIndices[i]);
            normals[i] = getNormal(model, face.normalIndices[i]);
            uvs[i] = getUV(model, face.uvIndices[i]);
            Constants.LOGGER.trace("  Vertex[{}]: pos={}, normal={}, uv={}", i, vertices[i], normals[i], uvs[i]);
        }

        if (face.vertexIndices.length == 3) {
            renderTriangle(stack, consumer, vertices, normals, uvs, packedLight, packedOverlay);
        } else {
            Constants.LOGGER.info("Triangulating polygon with {} vertices", face.vertexIndices.length);
            for (int i = 1; i < vertices.length - 1; i++) {
                Vector3f[] triVerts = {vertices[0], vertices[i], vertices[i + 1]};
                Vector3f[] triNormals = {normals[0], normals[i], normals[i + 1]};
                Vector3f[] triUVs = {uvs[0], uvs[i], uvs[i + 1]};
                renderTriangle(stack, consumer, triVerts, triNormals, triUVs, packedLight, packedOverlay);
            }
        }
    }

    private static void renderTriangle(PoseStack stack, VertexConsumer consumer, Vector3f[] vertices, Vector3f[] normals, Vector3f[] uvs, int packedLight, int packedOverlay) {
        Constants.LOGGER.trace("  Rendering triangle with vertices: {}, {}, {}", vertices[0], vertices[1], vertices[2]);
        for (int i = 0; i < 3; i++) {
            Vector3f pos = vertices[i];
            Vector3f normal = normals[i];
            Vector3f uv = uvs[i];

            consumer.addVertex(stack.last().pose(), pos.x(), pos.y(), pos.z())
                    .setColor(255, 255, 255, 255)
                    .setUv(uv.x(), uv.y())
                    .setOverlay(packedOverlay)
                    .setLight(packedLight)
                    .setNormal(stack.last(), normal.x(), normal.y(), normal.z());
        }
    }

    private static Vector3f getVertex(ObjModel model, int index) {
        if (index < 0 || index >= model.vertices.size()) {
            Constants.LOGGER.error("Invalid vertex index {} (max={})", index, model.vertices.size());
            return new Vector3f(0, 0, 0);
        }
        ObjModel.VertexData vertex = model.vertices.get(index);
        return new Vector3f(vertex.x, vertex.y, vertex.z);
    }

    private static Vector3f getNormal(ObjModel model, int index) {
        if (index < 0 || index >= model.normals.size()) {
            Constants.LOGGER.warn("Invalid normal index {} (max={}), defaulting to UP", index, model.normals.size());
            return new Vector3f(0, 1, 0);
        }
        ObjModel.NormalData normal = model.normals.get(index);
        return new Vector3f(normal.nx, normal.ny, normal.nz);
    }

    private static Vector3f getUV(ObjModel model, int index) {
        if (index < 0 || index >= model.uvs.size()) {
            Constants.LOGGER.warn("Invalid UV index {} (max={}), defaulting to (0,0)", index, model.uvs.size());
            return new Vector3f(0, 0, 0);
        }
        ObjModel.UVData uv = model.uvs.get(index);
        return new Vector3f(uv.u, uv.v, 0);
    }

    public static void renderWireframe(PoseStack stack, BlockPos pos, MultiBufferSource source, int packedLight, int packedOverlay) {
        Constants.LOGGER.info("Rendering wireframe for ScepticTankModel at {}", pos);

        stack.pushPose();
        stack.translate(0.5f, 0.0f, 0.5f);

        ObjModel model = loader.getModel(MODEL_LOC);
        if (model == null) {
            Constants.LOGGER.error("Wireframe render failed: model [{}] is null", MODEL_LOC);
            stack.popPose();
            return;
        }

        VertexConsumer consumer = source.getBuffer(RenderType.lines());

        for (ObjModel.FaceData face : model.faces) {
            renderWireframeFace(stack, consumer, model, face, packedLight, packedOverlay);
        }

        stack.popPose();
        Constants.LOGGER.info("Finished wireframe rendering");
    }

    private static void renderWireframeFace(PoseStack stack, VertexConsumer consumer, ObjModel model, ObjModel.FaceData face, int packedLight, int packedOverlay) {
        Vector3f[] vertices = new Vector3f[face.vertexIndices.length];
        for (int i = 0; i < face.vertexIndices.length; i++) {
            vertices[i] = getVertex(model, face.vertexIndices[i]);
        }

        Constants.LOGGER.trace("Wireframe face with {} edges", vertices.length);

        for (int i = 0; i < vertices.length; i++) {
            Vector3f start = vertices[i];
            Vector3f end = vertices[(i + 1) % vertices.length];

            consumer.addVertex(stack.last().pose(), start.x(), start.y(), start.z())
                    .setColor(255, 0, 0, 255)
                    .setUv(0, 0)
                    .setOverlay(packedOverlay)
                    .setLight(packedLight)
                    .setNormal(stack.last(), 0, 1, 0);

            consumer.addVertex(stack.last().pose(), end.x(), end.y(), end.z())
                    .setColor(255, 0, 0, 255)
                    .setUv(0, 0)
                    .setOverlay(packedOverlay)
                    .setLight(packedLight)
                    .setNormal(stack.last(), 0, 1, 0);

            Constants.LOGGER.trace("  Wire edge: {} -> {}", start, end);
        }
    }

    public static ScepticTankModel getInstance() {
        if(instance == null) {
            instance = new ScepticTankModel();
            Constants.LOGGER.info("Created new ScepticTankModel instance");
        }
        return instance;
    }
}
