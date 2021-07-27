package com.ecaservice.auto.test.config;

import com.ecaservice.config.swagger.annotation.EnableOpenApi;
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
public class EcaAutoTestsConfiguration {
}
