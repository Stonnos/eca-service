package com.ecaservice.server.config;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.core.filter.annotation.EnableFilters;
import com.ecaservice.core.filter.error.FilterExceptionHandler;
import com.ecaservice.core.form.template.annotation.EnableFormTemplates;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.repository.EvaluationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
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
@EnableFilters
@EnableFormTemplates
@Oauth2ResourceServer
@EntityScan(basePackageClasses = AbstractEvaluationEntity.class)
@EnableJpaRepositories(basePackageClasses = EvaluationLogRepository.class)
@EnableConfigurationProperties(
        {AppProperties.class, CrossValidationConfig.class, ExperimentConfig.class,
                ErsConfig.class, ClassifiersProperties.class})
@Import(FilterExceptionHandler.class)
public class EcaServiceConfiguration {

    public static final String ECA_THREAD_POOL_TASK_EXECUTOR = "ecaThreadPoolTaskExecutor";
    public static final String EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN = "experimentRedisLockRegistry";

    /**
     * Creates executor service bean.
     *
     * @return executor service bean
     */
    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

    /**
     * Creates thread pool task executor bean.
     *
     * @param appProperties - common config bean
     * @return thread pool task executor
     */
    @Bean(name = ECA_THREAD_POOL_TASK_EXECUTOR)
    public Executor ecaThreadPoolTaskExecutor(AppProperties appProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(appProperties.getThreadPoolSize());
        executor.setMaxPoolSize(appProperties.getThreadPoolSize());
        return executor;
    }

    /**
     * Creates redis lock registry for experiments processing.
     *
     * @param redisConnectionFactory - redis connection factory
     * @return experiment redis lock registry
     */
    @Bean(EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    @ConditionalOnProperty(value = "lock.registryType", havingValue = "REDIS")
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisLockRegistry lockRegistry(RedisConnectionFactory redisConnectionFactory,
                                          ExperimentConfig experimentConfig) {
        ExperimentConfig.LockProperties lockProperties = experimentConfig.getLock();
        var lockRegistry = new RedisLockRegistry(redisConnectionFactory, lockProperties.getRegistryKey(),
                lockProperties.getExpireAfter());
        log.info("Redis lock registry [{}] has been initialized for experiment processing. Lock expiration [{}] ms.",
                experimentConfig.getLock().getRegistryKey(), experimentConfig.getLock().getExpireAfter());
        return lockRegistry;
    }

    /**
     * Creates default lock registry for experiments processing.
     *
     * @return experiment redis lock registry
     */
    @Bean(EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    @ConditionalOnProperty(value = "lock.registryType", havingValue = "IN_MEMORY")
    public LockRegistry lockRegistry() {
        var lockRegistry = new DefaultLockRegistry();
        log.info("Default lock registry has been initialized for experiment processing");
        return lockRegistry;
    }
}
