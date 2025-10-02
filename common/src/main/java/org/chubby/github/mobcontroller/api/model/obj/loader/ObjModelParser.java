package org.chubby.github.mobcontroller.api.model.obj.loader;

import org.chubby.github.mobcontroller.api.model.obj.ObjModel;
import org.chubby.github.mobcontroller.api.model.obj.ObjModelData;
import org.chubby.github.mobcontroller.api.model.obj.ObjHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ObjModelParser {

    public static ObjModel parse(Reader reader) throws IOException {
        ObjModel model = new ObjModel();
        BufferedReader br = new BufferedReader(reader);

        String currentMaterial = null;
        String currentGroup = null;
        String currentObject = null;

        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] tokens = line.split("\\s+");
            if (tokens.length == 0) continue;

            String prefix = tokens[0];
            ObjModelData dataType = ObjHelper.fromToken(prefix);

            if (dataType != null) {
                switch (dataType) {
                    case VERTICES:
                        if (tokens.length >= 4) {
                            model.vertices.add(new ObjModel.VertexData(
                                    Float.parseFloat(tokens[1]),
                                    Float.parseFloat(tokens[2]),
                                    Float.parseFloat(tokens[3])
                            ));
                        }
                        break;

                    case NORMALS:
                        if (tokens.length >= 4) {
                            model.normals.add(new ObjModel.NormalData(
                                    Float.parseFloat(tokens[1]),
                                    Float.parseFloat(tokens[2]),
                                    Float.parseFloat(tokens[3])
                            ));
                        }
                        break;

                    case TEXTURES:
                        if (tokens.length >= 3) {
                            model.uvs.add(new ObjModel.UVData(
                                    Float.parseFloat(tokens[1]),
                                    Float.parseFloat(tokens[2])
                            ));
                        }
                        break;

                    case FACES:
                        parseFace(model, tokens,currentMaterial);
                        break;

                    case OBJECTS:
                        if (tokens.length >= 2) {
                            currentObject = tokens[1];
                            ObjModel.ModelInfo info = new ObjModel.ModelInfo();
                            info.name = currentObject;
                            info.group = currentGroup;
                            info.material = currentMaterial;
                            model.objects.add(info);
                        }
                        break;

                    case GROUPS:
                        if (tokens.length >= 2) {
                            currentGroup = tokens[1];
                        }
                        break;

                    case MATERIAL:
                        if (tokens.length >= 2) {
                            currentMaterial = tokens[1];
                            ObjModel.ModelInfo info = new ObjModel.ModelInfo();
                            info.material = currentMaterial;
                            model.objects.add(info);
                        }
                        break;

                    case SMOOTHING:
                        break;

                    case COMMENT:
                        break;
                }
            } else if (prefix.equals("usemtl") && tokens.length >= 2) {
                currentMaterial = tokens[1];
                ObjModel.MaterialData material = new ObjModel.MaterialData();
                material.name = currentMaterial;
                model.materials.add(material);
            }
        }

        return model;
    }

    private static void parseFace(ObjModel model, String[] tokens, String currentMaterial) {
        int vertexCount = tokens.length - 1;
        if (vertexCount < 3) return;

        int[] vIdx = new int[vertexCount];
        int[] uvIdx = new int[vertexCount];
        int[] nIdx = new int[vertexCount];

        for (int i = 1; i < tokens.length; i++) {
            String[] parts = tokens[i].split("/");

            vIdx[i - 1] = parseIndex(parts, 0);
            uvIdx[i - 1] = parseIndex(parts, 1);
            nIdx[i - 1] = parseIndex(parts, 2);
        }

        if (vertexCount > 3) {
            triangulate(model, vIdx, uvIdx, nIdx, currentMaterial);
        } else {
            ObjModel.FaceData face = new ObjModel.FaceData(vIdx, uvIdx, nIdx);
            face.material = currentMaterial;
            model.faces.add(face);
        }
    }

    private static int parseIndex(String[] parts, int index) {
        if (parts.length > index && !parts[index].isEmpty()) {
            int value = Integer.parseInt(parts[index]);
            return value > 0 ? value - 1 : value;
        }
        return -1;
    }

    private static void triangulate(ObjModel model, int[] vIdx, int[] uvIdx, int[] nIdx, String currentMaterial) {
        for (int i = 1; i < vIdx.length - 1; i++) {
            int[] triV = {vIdx[0], vIdx[i], vIdx[i + 1]};
            int[] triUV = {uvIdx[0], uvIdx[i], uvIdx[i + 1]};
            int[] triN = {nIdx[0], nIdx[i], nIdx[i + 1]};
            ObjModel.FaceData face = new ObjModel.FaceData(triV, triUV, triN);
            face.material = currentMaterial;
            model.faces.add(face);
        }
    }
}