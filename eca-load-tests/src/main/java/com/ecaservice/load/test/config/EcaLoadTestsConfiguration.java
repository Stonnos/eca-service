package com.ecaservice.load.test.config;

import com.ecaservice.classifier.options.config.ClassifiersOptionsConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Eca load tests configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties(EcaLoadTestsConfig.class)
@Import(ClassifiersOptionsConfiguration.class)
public class EcaLoadTestsConfiguration {
}
