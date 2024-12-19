package com.ecaservice.api.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Api gateway configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class ApiGatewayConfiguration {
}
