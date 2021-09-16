package com.ecaservice.oauth.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.core.filter.error.FilterExceptionHandler;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.passay.PasswordGenerator;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Eca oauth module configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@EnableAsync
@EnableGlobalExceptionHandler
@EntityScan(basePackageClasses = UserEntity.class)
@EnableJpaRepositories(basePackageClasses = UserEntityRepository.class)
@EnableConfigurationProperties({AppProperties.class, PasswordConfig.class, ResetPasswordConfig.class, TfaConfig.class,
        ChangePasswordConfig.class, ChangeEmailConfig.class})
@Import(FilterExceptionHandler.class)
public class EcaOauthConfiguration {

    public static final String ECA_OAUTH_THREAD_POOL_TASK_EXECUTOR = "ecaOauthThreadPoolTaskExecutor";

    /**
     * Creates password generator bean.
     *
     * @return password generator bean
     */
    @Bean
    public PasswordGenerator passwordGenerator() {
        return new PasswordGenerator();
    }

    /**
     * Creates thread pool task executor bean.
     *
     * @param appProperties - common config bean
     * @return thread pool task executor
     */
    @Bean(name = ECA_OAUTH_THREAD_POOL_TASK_EXECUTOR)
    public Executor ecaOauthThreadPoolTaskExecutor(AppProperties appProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(appProperties.getThreadPoolSize());
        executor.setMaxPoolSize(appProperties.getThreadPoolSize());
        return executor;
    }
}
