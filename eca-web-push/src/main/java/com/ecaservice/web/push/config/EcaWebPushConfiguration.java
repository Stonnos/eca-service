package com.ecaservice.web.push.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Eca web push application configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@Oauth2ResourceServer
@EnableGlobalExceptionHandler
@EnableConfigurationProperties(AppProperties.class)
public class EcaWebPushConfiguration {
}
