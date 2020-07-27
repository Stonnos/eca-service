package com.ecaservice.oauth2.annotation;

import com.ecaservice.oauth2.MethodSecurityConfiguration;
import com.ecaservice.oauth2.ResourceServerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation enables oauth2 resource server.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ResourceServerConfiguration.class, MethodSecurityConfiguration.class})
public @interface Oauth2ResourceServer {
}
