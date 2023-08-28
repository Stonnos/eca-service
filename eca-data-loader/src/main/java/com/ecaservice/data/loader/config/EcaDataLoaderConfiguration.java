package com.ecaservice.data.loader.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Eca data loader configuration.
 *
 * @author Roman Batygin
 */
@EnableOpenApi
@Oauth2ResourceServer
@EnableGlobalExceptionHandler
@Configuration
@EnableConfigurationProperties(EcaDataLoaderProperties.class)
public class EcaDataLoaderConfiguration {
}
