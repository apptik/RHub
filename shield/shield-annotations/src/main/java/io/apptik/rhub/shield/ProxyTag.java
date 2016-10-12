package io.apptik.rhub.shield;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to either:
 * <br>Proxy input "addUpstream"
 * <br>or Proxy output "getPub"
 */
@Retention(CLASS) @Target(METHOD)
public @interface ProxyTag {
    String value();
}
