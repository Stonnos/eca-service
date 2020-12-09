package com.ecaservice.oauth2.test.configuration.annotation;

import com.ecaservice.oauth2.test.configuration.MethodSecurityConfiguration;
import com.ecaservice.oauth2.test.configuration.Oauth2BaseConfiguration;
import com.ecaservice.oauth2.test.configuration.Oauth2ResourceServerConfiguration;
import com.ecaservice.oauth2.test.configuration.Oauth2ServerConfiguration;
import com.ecaservice.oauth2.test.configuration.Oauth2ServerSecurityConfiguration;
import com.ecaservice.oauth2.test.token.TokenService;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for enabling oauth2 test configurations.
 *
 * @author Roman Batygin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({Oauth2BaseConfiguration.class, Oauth2ResourceServerConfiguration.class, Oauth2ServerConfiguration.class,
        Oauth2ServerSecurityConfiguration.class, MethodSecurityConfiguration.class, TokenService.class})
@Documented
public @interface Oauth2TestConfiguration {
}
