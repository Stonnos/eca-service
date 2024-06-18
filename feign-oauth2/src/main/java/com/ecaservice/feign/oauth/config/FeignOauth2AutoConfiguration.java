package com.ecaservice.feign.oauth.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Feign configuration for oauth2 secured endpoints.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(OAuth2ClientProperties.class)
@ComponentScan({"com.ecaservice.feign.oauth"})
public class FeignOauth2AutoConfiguration {
}
