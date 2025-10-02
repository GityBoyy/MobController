package org.chubby.github.mobcontroller.api.model.obj;

import java.util.HashMap;
import java.util.Map;

public class ObjHelper
{
    private static final Map<String, ObjModelData> LOOKUP = new HashMap<>();

    static {
        for (ObjModelData data : ObjModelData.values()) {
            LOOKUP.put(data.token, data);
        }
    }

    public static ObjModelData fromToken(String token) {
        return LOOKUP.get(token);
    }
}
