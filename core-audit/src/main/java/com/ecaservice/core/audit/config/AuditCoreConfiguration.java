package com.ecaservice.core.audit.config;

import com.ecaservice.core.audit.entity.BaseAuditEntity;
import com.ecaservice.core.audit.repository.AuditEventTemplateRepository;
import com.ecaservice.core.audit.service.AuditEventSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Audit configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(AuditProperties.class)
@ComponentScan({"com.ecaservice.core.audit"})
@EnableFeignClients(basePackageClasses = AuditEventSender.class)
@EntityScan(basePackageClasses = BaseAuditEntity.class)
@EnableJpaRepositories(basePackageClasses = AuditEventTemplateRepository.class)
@ConditionalOnProperty(value = "audit.enabled", havingValue = "true")
public class AuditCoreConfiguration {
}
