package com.ecaservice.oauth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Eca oauth module configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(CommonConfig.class)
public class EcaOauthConfiguration {
}
