package io.apptik.rxhub.shield;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to either:
 * <br>Node input "addProvider"
 * <br>or Node output "getNode"
 */
@Retention(CLASS) @Target(METHOD)
public @interface NodeTag {
    String value();
}
