package com.ecaservice.auto.test.config;

import com.ecaservice.auto.test.config.mail.MailProperties;
import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * Auto test configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@EnableCaching
@EnableScheduling
@EnableGlobalExceptionHandler
@EnableConfigurationProperties({AutoTestsProperties.class, MailProperties.class})
@RequiredArgsConstructor
public class EcaAutoTestsConfiguration implements SchedulingConfigurer {

    private final AutoTestsProperties autoTestsProperties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(threadPoolTaskScheduler());
    }

    /**
     * Creates thread pool task scheduler bean.
     *
     * @return thread pool task scheduler bean
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(autoTestsProperties.getSchedulerPoolSize());
        taskScheduler.initialize();
        return taskScheduler;
    }
}
