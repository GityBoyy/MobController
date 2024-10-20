package org.chubby.github.mobcontroller.core.config.property.impl;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.NonnullDefault;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking configuration properties in the Mob Controller.
 * <p>
 * This annotation can be applied to fields to indicate that they should
 * be treated as configuration properties, allowing for easier
 * serialization/deserialization and management of configuration settings.
 * </p>
 *
 * <p>
 * The fields annotated with this will be processed by the configuration
 * management system to include them in the generated configuration files.
 * </p>
 *
 * <p>
 * Usage:
 * <pre>
 * {@code
 * @ConfigProperty
 * private static IntProperty myProperty = Config.createIntProp("myProperty", 100, "Description of my property");
 * }
 * </pre>
 * </p>
 *
 * <p>
 * The annotation ensures that fields are not null when accessed.
 * It is retained at runtime for reflection purposes.
 * </p>
 */
@NotNull
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@NonnullDefault
public @interface ConfigProperty
{

}
