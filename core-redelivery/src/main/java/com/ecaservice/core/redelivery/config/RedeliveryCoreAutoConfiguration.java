package com.ecaservice.core.redelivery.config;

import com.ecaservice.core.redelivery.converter.RequestMessageConverter;
import com.ecaservice.core.redelivery.converter.impl.JsonRequestMessageConverter;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Redelivery lib auto configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedeliveryProperties.class)
@ComponentScan({"com.ecaservice.core.redelivery"})
@EntityScan(basePackageClasses = RetryRequest.class)
@EnableJpaRepositories(basePackageClasses = RetryRequestRepository.class)
@ConditionalOnProperty(value = "redelivery.enabled", havingValue = "true")
public class RedeliveryCoreAutoConfiguration {

    /**
     * Retry request thread pool task scheduler bean name
     */
    public static final String RETRY_REQUEST_THREAD_POOL_TASK_SCHEDULER = "retryRequestThreadPoolTaskScheduler";

    /**
     * Redelivery lock registry bean name
     */
    public static final String REDELIVERY_LOCK_REGISTRY = "redeliveryLockRegistry";

    /**
     * Json message converter bean name
     */
    public static final String JSON_REQUEST_MESSAGE_CONVERTER = "jsonRequestMessageConverter";

    /**
     * Creates thread pool task executor bean.
     *
     * @param redeliveryProperties - redelivery properties
     * @return thread pool task executor bean
     */
    @Bean(name = RETRY_REQUEST_THREAD_POOL_TASK_SCHEDULER)
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(RedeliveryProperties redeliveryProperties) {
        var threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(redeliveryProperties.getThreadPoolSize());
        log.info("[{}] bean has been created", RETRY_REQUEST_THREAD_POOL_TASK_SCHEDULER);
        return threadPoolTaskScheduler;
    }

    /**
     * Creates request message converter bean.
     *
     * @param objectMapper - object mapper
     * @return request message converter bean
     */
    @Bean(JSON_REQUEST_MESSAGE_CONVERTER)
    public RequestMessageConverter requestMessageConverter(ObjectMapper objectMapper) {
        return new JsonRequestMessageConverter(objectMapper);
    }
}
