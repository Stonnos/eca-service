package com.ecaservice.oauth.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.passay.PasswordGenerator;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Eca oauth module configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableCaching
@EnableScheduling
@EnableOpenApi
@EnableAsync
@EnableGlobalExceptionHandler
@EntityScan(basePackageClasses = UserEntity.class)
@EnableJpaRepositories(basePackageClasses = UserEntityRepository.class)
@EnableConfigurationProperties({AppProperties.class, PasswordConfig.class, TfaConfig.class, UserProfileProperties.class})
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
