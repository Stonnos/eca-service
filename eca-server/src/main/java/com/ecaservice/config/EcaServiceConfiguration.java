package com.ecaservice.config;

import com.ecaservice.classifier.options.config.ClassifiersOptionsConfiguration;
import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.ers.ErsConfig;
import com.ecaservice.core.lock.redis.annotation.EnableRedisLocks;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import eca.data.file.FileDataLoader;
import eca.data.file.FileDataSaver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
@Configuration
@EnableScheduling
@EnableCaching
@EnableRedisLocks
@EnableAsync
@EnableGlobalExceptionHandler
@Oauth2ResourceServer
@EnableConfigurationProperties(
        {CommonConfig.class, CrossValidationConfig.class, ExperimentConfig.class, ErsConfig.class,
                NotificationConfig.class})
@Import(ClassifiersOptionsConfiguration.class)
public class EcaServiceConfiguration {

    public static final String ECA_THREAD_POOL_TASK_EXECUTOR = "ecaThreadPoolTaskExecutor";

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
     * Creates file data saver bean.
     *
     * @param experimentConfig experiment config bean
     * @return file data saver  bean
     */
    @Bean
    public FileDataSaver dataSaver(ExperimentConfig experimentConfig) {
        FileDataSaver dataSaver = new FileDataSaver();
        dataSaver.setDateFormat(experimentConfig.getData().getDateFormat());
        return dataSaver;
    }

    /**
     * Creates file data loader bean.
     *
     * @param experimentConfig experiment config bean
     * @return file data loader bean
     */
    @Bean
    public FileDataLoader dataLoader(ExperimentConfig experimentConfig) {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setDateFormat(experimentConfig.getData().getDateFormat());
        return dataLoader;
    }

    /**
     * Creates thread pool task executor bean.
     *
     * @param commonConfig - common config bean
     * @return thread pool task executor
     */
    @Bean(name = ECA_THREAD_POOL_TASK_EXECUTOR)
    public Executor ecaThreadPoolTaskExecutor(CommonConfig commonConfig) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(commonConfig.getThreadPoolSize());
        executor.setMaxPoolSize(commonConfig.getThreadPoolSize());
        return executor;
    }
}
