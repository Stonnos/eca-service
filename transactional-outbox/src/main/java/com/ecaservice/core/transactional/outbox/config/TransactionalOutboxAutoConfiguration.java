package com.ecaservice.core.transactional.outbox.config;

import com.ecaservice.core.transactional.outbox.entity.OutboxMessageEntity;
import com.ecaservice.core.transactional.outbox.error.DefaultExceptionStrategy;
import com.ecaservice.core.transactional.outbox.error.ExceptionStrategy;
import com.ecaservice.core.transactional.outbox.repository.OutboxMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Transactional outbox lib auto configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(TransactionalOutboxProperties.class)
@ComponentScan({"com.ecaservice.core.transactional.outbox"})
@EntityScan(basePackageClasses = OutboxMessageEntity.class)
@EnableJpaRepositories(basePackageClasses = OutboxMessageRepository.class)
public class TransactionalOutboxAutoConfiguration {

    /**
     * Outbox thread pool task scheduler bean name
     */
    public static final String OUTBOX_THREAD_POOL_TASK_SCHEDULER = "outboxThreadPoolTaskScheduler";

    /**
     * Default exception strategy bean name
     */
    public static final String DEFAULT_EXCEPTION_STRATEGY = "outboxDefaultExceptionStrategy";

    /**
     * Creates thread pool task executor bean.
     *
     * @return thread pool task executor bean
     */
    @Bean(name = OUTBOX_THREAD_POOL_TASK_SCHEDULER)
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        var threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        log.info("[{}] bean has been created", OUTBOX_THREAD_POOL_TASK_SCHEDULER);
        return threadPoolTaskScheduler;
    }

    /**
     * Creates default exception strategy bean.
     *
     * @return default exception strategy bean
     */
    @Bean(DEFAULT_EXCEPTION_STRATEGY)
    public ExceptionStrategy exceptionStrategy() {
        return new DefaultExceptionStrategy();
    }
}
