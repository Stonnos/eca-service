package com.ecaservice.config.swagger.annotation;

import com.ecaservice.config.swagger.OpenApi30Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation enables open api.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OpenApi30Configuration.class)
public @interface EnableOpenApi {
}
