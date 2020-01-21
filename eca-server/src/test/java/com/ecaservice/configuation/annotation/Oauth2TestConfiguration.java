package com.ecaservice.configuation.annotation;

import com.ecaservice.config.oauth2.MethodSecurityConfiguration;
import com.ecaservice.configuation.Oauth2ResourceServerConfiguration;
import com.ecaservice.configuation.Oauth2ServerConfiguration;
import com.ecaservice.configuation.Oauth2ServerSecurityConfiguration;
import com.ecaservice.token.TokenService;
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
@Import({Oauth2ResourceServerConfiguration.class, Oauth2ServerConfiguration.class,
        Oauth2ServerSecurityConfiguration.class, MethodSecurityConfiguration.class, TokenService.class})
@Documented
public @interface Oauth2TestConfiguration {
}
