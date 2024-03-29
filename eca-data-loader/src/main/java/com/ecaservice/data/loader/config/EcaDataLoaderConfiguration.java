package com.ecaservice.data.loader.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Eca data loader configuration.
 *
 * @author Roman Batygin
 */
@EnableOpenApi
@EnableScheduling
@Oauth2ResourceServer
@EnableGlobalExceptionHandler
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class EcaDataLoaderConfiguration {
}
