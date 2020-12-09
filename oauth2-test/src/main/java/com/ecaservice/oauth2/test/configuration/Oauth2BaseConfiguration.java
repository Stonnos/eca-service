package com.ecaservice.oauth2.test.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;

/**
 * Oauth2 base configuration.
 *
 * @author Roman Batygin
 */
@TestConfiguration
@EnableConfigurationProperties(Oauth2TestConfig.class)
public class Oauth2BaseConfiguration {
}
