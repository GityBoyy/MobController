package org.chubby.github.mobcontroller.core.config.property;

import org.chubby.github.mobcontroller.core.config.property.impl.IProperty;

public class IntProperty implements IProperty<Integer> {
    private String name;
    private int value;
    private String description;
    private final int min;
    private final int max;
    private final int defaultValue;

    public IntProperty(String name, int value, String description) {
        this(name, value, description, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntProperty(String name, int value, String description, int min, int max) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.defaultValue = value;
        this.description = description;
        setValue(value);
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
        this.value = Math.clamp(value, min, max);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void reset() {
        setValue(defaultValue);
    }
}