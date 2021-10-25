package com.ecaservice.core.mail.client.config;

import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import com.ecaservice.core.mail.client.repository.EmailRequestRepository;
import com.ecaservice.core.mail.client.service.EmailClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Eca mail client configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableAsync
@EnableConfigurationProperties(EcaMailClientProperties.class)
@ComponentScan({"com.ecaservice.core.mail.client"})
@EnableFeignClients(basePackageClasses = EmailClient.class)
@EntityScan(basePackageClasses = EmailRequestEntity.class)
@EnableJpaRepositories(basePackageClasses = EmailRequestRepository.class)
public class EcaMailClientAutoConfiguration {

    /**
     * Mail client thread pool task scheduler executor bean
     */
    public static final String MAIL_THREAD_POOL_TASK_SCHEDULER = "mailThreadPoolTaskScheduler";
    /**
     * Mail lock registry bean
     */
    public static final String MAIL_LOCK_REGISTRY = "mailLockRegistry";

    /**
     * Creates mail client thread pool task scheduler bean.
     *
     * @return mail client thread pool task scheduler bean
     */
    @Bean(name = MAIL_THREAD_POOL_TASK_SCHEDULER)
    @ConditionalOnProperty(value = "mail.client.redelivery", havingValue = "true")
    public ThreadPoolTaskScheduler mailThreadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
