package com.ecaservice.config.swagger;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;

/**
 * Swagger security configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(Swagger2ApiConfig.class)
public class SwaggerSecurityConfiguration {

    /**
     * Creates swagger security configuration bean.
     *
     * @param swagger2ApiConfig - swagger api config bean
     * @return swagger security configuration bean
     */
    @Bean
    public SecurityConfiguration security(Swagger2ApiConfig swagger2ApiConfig) {
        return SecurityConfigurationBuilder.builder()
                .clientId(swagger2ApiConfig.getClientId())
                .clientSecret(swagger2ApiConfig.getSecret())
                .useBasicAuthenticationWithAccessCodeGrant(false)
                .build();
    }
}
