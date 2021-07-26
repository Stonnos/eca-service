package com.ecaservice.external.api.test.config;

import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * External api tests configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@EnableScheduling
@EnableConfigurationProperties({ExternalApiTestsConfig.class, ProcessConfig.class})
public class ExternalApiTestsConfiguration {

    /**
     * Creates object mapper bean.
     *
     * @return object mapper bean
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
