package com.ecaservice.external.api.test.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * External api tests configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableCaching
@EnableScheduling
public class ExternalApiTestsConfiguration {
}
