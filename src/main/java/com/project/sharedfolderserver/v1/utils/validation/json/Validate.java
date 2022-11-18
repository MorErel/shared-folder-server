package com.project.sharedfolderserver.v1.utils.validation.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
/**
 *  Custom annotation to mark parameter that needed to be validated (Json Schema)
 */
public @interface Validate {
    String value();
}
