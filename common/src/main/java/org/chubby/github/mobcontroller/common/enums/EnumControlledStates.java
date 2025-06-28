package org.chubby.github.mobcontroller.common.enums;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum EnumControlledStates
{
    STAY("textures/gui/button/stay.png"),
    FOLLOW("textures/gui/button/follow.png"),
    DEFEND("textures/gui/button/defend.png"),
    ATTACK("textures/gui/button/attack.png"),
    GO_HOME("textures/gui/button/go_home.png");

    public static final Codec<EnumControlledStates> CODEC = Codec.STRING.flatXmap(
            name -> {
                try {
                    return DataResult.success(EnumControlledStates.valueOf(name));
                } catch (IllegalArgumentException e) {
                    return DataResult.error(() -> "Invalid EnumControlledStates: " + name);
                }
            },
            value -> DataResult.success(value.name())
    );

    final String icon;

    EnumControlledStates(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
