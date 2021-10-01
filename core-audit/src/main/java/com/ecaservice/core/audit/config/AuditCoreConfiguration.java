package com.ecaservice.core.audit.config;

import com.ecaservice.core.audit.entity.BaseAuditEntity;
import com.ecaservice.core.audit.repository.AuditEventTemplateRepository;
import com.ecaservice.core.audit.service.AuditEventClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

/**
 * Audit configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableAsync
@EnableConfigurationProperties(AuditProperties.class)
@ComponentScan({"com.ecaservice.core.audit"})
@EnableFeignClients(basePackageClasses = AuditEventClient.class)
@EntityScan(basePackageClasses = BaseAuditEntity.class)
@EnableJpaRepositories(basePackageClasses = AuditEventTemplateRepository.class)
@ConditionalOnProperty(value = "audit.enabled", havingValue = "true")
public class AuditCoreConfiguration {

    /**
     * Audit event thread pool task executor bean
     */
    public static final String AUDIT_EVENT_THREAD_POOL_TASK_EXECUTOR = "auditEventThreadPoolTaskExecutor";
    /**
     * Audit thread pool task scheduler executor bean
     */
    public static final String AUDIT_THREAD_POOL_TASK_SCHEDULER = "auditThreadPoolTaskScheduler";

    /**
     * Creates thread pool task executor bean.
     *
     * @param auditProperties - audit properties
     * @return thread pool task executor
     */
    @Bean(name = AUDIT_EVENT_THREAD_POOL_TASK_EXECUTOR)
    @ConditionalOnProperty(value = "audit.asyncEvents", havingValue = "true")
    public Executor auditEventThreadPoolTaskExecutor(AuditProperties auditProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(auditProperties.getThreadPoolSize());
        executor.setMaxPoolSize(auditProperties.getThreadPoolSize());
        return executor;
    }

    /**
     * Creates audit thread pool task scheduler bean.
     *
     * @return audit thread pool task scheduler bean
     */
    @Bean(name = AUDIT_THREAD_POOL_TASK_SCHEDULER)
    @ConditionalOnProperty(value = "audit.redelivery", havingValue = "true")
    public ThreadPoolTaskScheduler auditThreadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
