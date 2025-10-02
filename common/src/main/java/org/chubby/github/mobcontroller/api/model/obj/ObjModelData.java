package org.chubby.github.mobcontroller.api.model.obj;

public enum ObjModelData
{
    VERTICES("v"),
    TEXTURES("vt"),
    NORMALS("vn"),
    FACES("f"),
    OBJECTS("o"),
    GROUPS("g"),
    MATERIAL("mtllib"),
    SMOOTHING("s"),
    COMMENT("#");

    final String token;

    ObjModelData(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
