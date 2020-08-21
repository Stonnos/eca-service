package com.ecaservice.load.test.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Eca load tests configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties(EcaLoadTestsConfig.class)
public class EcaLoadTestsConfiguration {
}
