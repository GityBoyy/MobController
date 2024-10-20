package org.chubby.github.mobcontroller.core.config.property;

import org.chubby.github.mobcontroller.core.config.property.impl.IProperty;

public class FloatProperty implements IProperty<Float>
{
    private String name;
    private float value;
    private String description;

    public FloatProperty(String name,float value, String description)
    {
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
    public Float getValue() {
        return value;
    }

    @Override
    public void setValue(Float value) {
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
