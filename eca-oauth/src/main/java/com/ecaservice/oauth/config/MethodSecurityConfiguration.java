package com.ecaservice.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * Security annotations configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class MethodSecurityConfiguration {

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        return new OAuth2MethodSecurityExpressionHandler();
    }
}
