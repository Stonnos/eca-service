package com.ecaservice.auto.test.config;

import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Auto test configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties(AutoTestsProperties.class)
public class EcaAutoTestsConfiguration {
}
