package com.ecaservice.ers.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Ers configuration class.
 *
 * @author Roman Batygin
 */
@EnableGlobalExceptionHandler
@EnableCaching
@Configuration
@EnableConfigurationProperties(ErsConfig.class)
public class ErsConfiguration {
}
