package com.ecaservice.config.swagger;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger security configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableSwagger2
@EnableConfigurationProperties(Swagger2ApiConfig.class)
public class SwaggerBaseConfiguration {

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
