package com.project.sharedfolderserver.utils.http.response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * Custom class annotation to wrap http response with Response.class object
 */
public @interface ResponseWrapper {

}
