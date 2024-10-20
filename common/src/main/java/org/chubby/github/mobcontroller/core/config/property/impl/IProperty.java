package org.chubby.github.mobcontroller.core.config.property.impl;

/**
 * Interface representing a configurable property.
 *
 * @param <T> the type of the property value
 */
public interface IProperty<T> {

    /**
     * Gets the name of the property.
     *
     * @return the name of the property
     */
    String getName();

    /**
     * Sets the name of the property.
     *
     * @param name the new name of the property
     */
    void setName(String name);

    /**
     * Gets the value of the property.
     *
     * @return the value of the property
     */
    T getValue();

    /**
     * Sets the value of the property.
     *
     * @param value the new value of the property
     */
    void setValue(T value);

    /**
     * Gets the description of the property.
     *
     * @return the description of the property
     */
    String getDescription();

    /**
     * Sets the description of the property.
     *
     * @param description the new description of the property
     */
    void setDescription(String description);
}
