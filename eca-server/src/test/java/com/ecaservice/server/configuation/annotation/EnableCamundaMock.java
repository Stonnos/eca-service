package com.ecaservice.server.configuation.annotation;

import com.ecaservice.server.configuation.CamundaMockConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for enabling camunda mock configuration.
 *
 * @author Roman Batygin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CamundaMockConfiguration.class)
@Documented
public @interface EnableCamundaMock {
}
