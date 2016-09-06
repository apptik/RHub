package io.apptik.rxhub.shield;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Retention(CLASS) @Target(METHOD)
public @interface NodeTag {
    String value();
}
