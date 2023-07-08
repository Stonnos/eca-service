package com.ecaservice.mail.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.core.filter.error.FilterExceptionHandler;
import com.ecaservice.core.lock.annotation.EnableLocks;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Eca mail configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@EnableScheduling
@EnableLocks
@Oauth2ResourceServer
@EnableGlobalExceptionHandler
@EnableConfigurationProperties(MailConfig.class)
@Import(FilterExceptionHandler.class)
public class EcaMailConfiguration {
}
