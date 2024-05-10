package com.ecaservice.feign.oauth.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Feign configuration for oauth2 secured endpoints.
 *
 * @author Roman Batygin
 */
@Configuration
@ComponentScan({"com.ecaservice.feign.oauth"})
public class FeignOauth2AutoConfiguration {
}
