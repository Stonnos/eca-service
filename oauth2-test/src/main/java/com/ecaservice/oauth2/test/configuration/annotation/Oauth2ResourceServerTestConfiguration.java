package com.ecaservice.oauth2.test.configuration.annotation;

import com.ecaservice.oauth2.test.configuration.Oauth2BaseConfiguration;
import com.ecaservice.oauth2.test.configuration.Oauth2ResourceServerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for enabling oauth2 resource server test configurations.
 *
 * @author Roman Batygin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({Oauth2ResourceServerConfiguration.class, Oauth2BaseConfiguration.class})
@Documented
public @interface Oauth2ResourceServerTestConfiguration {
}
