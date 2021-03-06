package com.ecaservice.web.push.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import org.springframework.context.annotation.Configuration;

/**
 * Eca web push application configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@Oauth2ResourceServer
@EnableGlobalExceptionHandler
public class EcaWebPushConfiguration {
}
