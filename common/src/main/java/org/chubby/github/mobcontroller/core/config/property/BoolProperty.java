package org.chubby.github.mobcontroller.core.config.property;

import org.chubby.github.mobcontroller.core.config.property.impl.IProperty;

public class BoolProperty implements IProperty<Boolean>
{
    private String name;
    private boolean value;
    private String description;

    public BoolProperty(String name,boolean value, String description)
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
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
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
