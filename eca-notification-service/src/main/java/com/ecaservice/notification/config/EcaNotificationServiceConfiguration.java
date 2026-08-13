package com.ecaservice.notification.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.notification.entity.UserNotificationEntity;
import com.ecaservice.notification.repository.UserNotificationRepository;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Eca web push application configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@Oauth2ResourceServer
@EnableScheduling
@EnableGlobalExceptionHandler
@EntityScan(basePackageClasses = UserNotificationEntity.class)
@EnableJpaRepositories(basePackageClasses = UserNotificationRepository.class)
@EnableConfigurationProperties(
        {AppProperties.class, UserNotificationProperties.class, MailProperties.class, WebPushProperties.class})
public class EcaNotificationServiceConfiguration {
}
