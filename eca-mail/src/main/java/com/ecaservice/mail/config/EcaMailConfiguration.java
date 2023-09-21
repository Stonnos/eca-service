package com.ecaservice.mail.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.core.filter.annotation.EnableFilters;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.repository.EmailRepository;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Eca mail configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@EnableScheduling
@EnableFilters
@EnableCaching
@Oauth2ResourceServer
@EnableGlobalExceptionHandler
@EntityScan(basePackageClasses = Email.class)
@EnableJpaRepositories(basePackageClasses = EmailRepository.class)
@EnableConfigurationProperties(MailConfig.class)
public class EcaMailConfiguration {
}
