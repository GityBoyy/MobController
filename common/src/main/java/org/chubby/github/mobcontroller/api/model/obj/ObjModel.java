package org.chubby.github.mobcontroller.api.model.obj;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjModel {

    public final List<VertexData> vertices = new ArrayList<>();
    public final List<UVData> uvs = new ArrayList<>();
    public final List<NormalData> normals = new ArrayList<>();
    public final List<FaceData> faces = new ArrayList<>();
    public final List<ModelInfo> objects = new ArrayList<>();
    public final List<MaterialData> materials = new ArrayList<>();
    public final Map<String, ResourceLocation> materialTextures = new HashMap<>();
    public ResourceLocation defaultTexture = null;
    public static class ModelInfo {
        public String name;
        public String group;
        public String material;
    }

    public static class VertexData {
        public final float x, y, z;
        public VertexData(float x, float y, float z) {
            this.x = x; this.y = y; this.z = z;
        }
    }

    public static class UVData {
        public final float u, v;
        public UVData(float u, float v) {
            this.u = u; this.v = v;
        }
    }

    public static class NormalData {
        public final float nx, ny, nz;
        public NormalData(float nx, float ny, float nz) {
            this.nx = nx; this.ny = ny; this.nz = nz;
        }
    }

    public static class FaceData {
        public final int[] vertexIndices;
        public final int[] uvIndices;
        public final int[] normalIndices;
        public String material;

        public FaceData(int[] vertexIndices, int[] uvIndices, int[] normalIndices) {
            this(vertexIndices, uvIndices, normalIndices, null);
        }

        public FaceData(int[] vertexIndices, int[] uvIndices, int[] normalIndices, String material) {
            this.vertexIndices = vertexIndices;
            this.uvIndices = uvIndices;
            this.normalIndices = normalIndices;
            this.material = material;
        }
    }

    public static class MaterialData {
        public String name;
        public ResourceLocation textureLocation;
        public float[] diffuseColor = {1.0f, 1.0f, 1.0f}; // RGB
        public float alpha = 1.0f;
    }
}
