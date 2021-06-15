package com.ecaservice.audit.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Eca audit log configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@Oauth2ResourceServer
@EnableGlobalExceptionHandler
@EnableConfigurationProperties(EcaAuditLogConfig.class)
public class EcaAuditLogConfiguration {
}
