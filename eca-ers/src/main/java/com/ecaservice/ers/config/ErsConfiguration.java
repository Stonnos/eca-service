package com.ecaservice.ers.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Ers configuration class.
 *
 * @author Roman Batygin
 */
@EnableCaching
@Configuration
@EnableConfigurationProperties(ErsConfig.class)
public class ErsConfiguration {
}
