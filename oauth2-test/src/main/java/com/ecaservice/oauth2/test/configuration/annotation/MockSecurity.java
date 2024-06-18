package com.ecaservice.oauth2.test.configuration.annotation;

import com.ecaservice.oauth2.test.configuration.MockSecurityConfiguration;
import com.ecaservice.oauth2.test.configuration.Oauth2ResourceServerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for enabling security mock.
 *
 * @author Roman Batygin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MockSecurityConfiguration.class)
@Documented
public @interface MockSecurity {
}
