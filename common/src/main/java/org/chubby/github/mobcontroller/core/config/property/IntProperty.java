package org.chubby.github.mobcontroller.core.config.property;

import org.chubby.github.mobcontroller.core.config.property.impl.IProperty;

public class IntProperty implements IProperty<Integer>
{

    private String name;
    private int value;
    private String description;

    public IntProperty(String name,int value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
