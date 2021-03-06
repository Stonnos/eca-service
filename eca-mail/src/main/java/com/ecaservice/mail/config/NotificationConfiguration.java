package com.ecaservice.mail.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Notification service configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableScheduling
@EnableGlobalExceptionHandler
@EnableConfigurationProperties(MailConfig.class)
public class NotificationConfiguration {
}
