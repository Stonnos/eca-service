package com.ecaservice.core.audit.config;

import com.ecaservice.core.audit.entity.BaseAuditEntity;
import com.ecaservice.core.audit.repository.AuditEventTemplateRepository;
import com.ecaservice.core.audit.service.AuditEventClient;
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

import java.util.concurrent.Executor;

import static com.ecaservice.common.web.concurrent.ThreadPoolExecutorFactory.createThreadPoolTaskExecutor;

/**
 * Audit configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
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
     * Creates thread pool task executor bean.
     *
     * @param auditProperties - audit properties
     * @return thread pool task executor
     */
    @Bean(name = AUDIT_EVENT_THREAD_POOL_TASK_EXECUTOR)
    @ConditionalOnProperty(value = "audit.asyncEvents", havingValue = "true")
    public Executor auditEventThreadPoolTaskExecutor(AuditProperties auditProperties) {
        Executor executor = createThreadPoolTaskExecutor(auditProperties.getThreadPoolSize());
        log.info("[{}] audit client thread pool with size [{}] has been configured",
                AUDIT_EVENT_THREAD_POOL_TASK_EXECUTOR, auditProperties.getThreadPoolSize());
        return executor;
    }
}
