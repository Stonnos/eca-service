package com.ecaservice.web.push.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import com.ecaservice.web.push.entity.UserNotificationEntity;
import com.ecaservice.web.push.repository.UserNotificationRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Eca web push application configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@Oauth2ResourceServer
@EnableGlobalExceptionHandler
@EntityScan(basePackageClasses = UserNotificationEntity.class)
@EnableJpaRepositories(basePackageClasses = UserNotificationRepository.class)
@EnableConfigurationProperties({AppProperties.class, UserNotificationProperties.class})
public class EcaWebPushConfiguration {
}
