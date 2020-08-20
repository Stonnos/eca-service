package com.ecaservice.load.test.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Eca load tests configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(EcaLoadTestsConfig.class)
public class EcaLoadTestsConfiguration {
}
