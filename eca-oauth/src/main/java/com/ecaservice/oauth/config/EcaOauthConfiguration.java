package com.ecaservice.oauth.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.core.audit.entity.BaseAuditEntity;
import com.ecaservice.core.audit.repository.AuditEventTemplateRepository;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.passay.PasswordGenerator;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Eca oauth module configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableAsync
@EnableGlobalExceptionHandler
@EntityScan(basePackageClasses = {UserEntity.class, BaseAuditEntity.class})
@EnableJpaRepositories(basePackageClasses = {UserEntityRepository.class, AuditEventTemplateRepository.class})
@EnableConfigurationProperties({CommonConfig.class, PasswordConfig.class, ResetPasswordConfig.class, TfaConfig.class,
        ChangePasswordConfig.class})
public class EcaOauthConfiguration {

    /**
     * Creates password generator bean.
     *
     * @return password generator bean
     */
    @Bean
    public PasswordGenerator passwordGenerator() {
        return new PasswordGenerator();
    }
}
