package com.ecaservice.server.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.repository.EvaluationLogRepository;
import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshotFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Eca - service configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableOpenApi
@EnableScheduling
@EnableCaching
@EnableAsync
@EnableGlobalExceptionHandler
@Oauth2ResourceServer
@EntityScan(basePackageClasses = AbstractEvaluationEntity.class)
@EnableJpaRepositories(basePackageClasses = EvaluationLogRepository.class)
@EnableConfigurationProperties(
        {AppProperties.class, CrossValidationConfig.class, ExperimentConfig.class, ClassifiersProperties.class,
                ProcessConfig.class})
@RequiredArgsConstructor
public class EcaServiceConfiguration implements SchedulingConfigurer {

    private final AppProperties appProperties;

    /**
     * Creates executor service bean.
     *
     * @return executor service bean
     */
    @Bean
    public ExecutorService executorService() {
        var executorService = Executors.newCachedThreadPool();
        return ContextExecutorService.wrap(executorService, ContextSnapshotFactory.builder().build()::captureAll);
    }

    /**
     * Creates evaluation request executor service.
     *
     * @param classifiersProperties - classifiers properties
     * @return executor service
     */
    @Bean
    public ExecutorService evaluationRequestExecutorService(ClassifiersProperties classifiersProperties) {
        return Executors.newFixedThreadPool(classifiersProperties.getThreadPoolSize());
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        var taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(appProperties.getSchedulerPoolSize());
        taskScheduler.initialize();
        taskRegistrar.setScheduler(taskScheduler);
        log.info("Scheduler thread pool with size [{}] has been initialized", appProperties.getSchedulerPoolSize());
    }
}
