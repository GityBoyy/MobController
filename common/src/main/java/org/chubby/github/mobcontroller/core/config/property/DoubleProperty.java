package org.chubby.github.mobcontroller.core.config.property;

import org.chubby.github.mobcontroller.core.config.property.impl.IProperty;

public class DoubleProperty implements IProperty<Double>
{
    private String name;
    private double value;
    private String description;

    public DoubleProperty(String name,double value, String description)
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
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
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
