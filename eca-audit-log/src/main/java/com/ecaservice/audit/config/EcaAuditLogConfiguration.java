package com.ecaservice.audit.config;

import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.repository.AuditLogRepository;
import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.config.swagger.annotation.EnableOpenApi;
import com.ecaservice.core.filter.annotation.EnableFilters;
import com.ecaservice.core.filter.error.FilterExceptionHandler;
import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Eca audit log configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableOpenApi
@Oauth2ResourceServer
@EnableGlobalExceptionHandler
@EnableFilters
@EnableCaching
@EntityScan(basePackageClasses = AuditLogEntity.class)
@EnableJpaRepositories(basePackageClasses = AuditLogRepository.class)
@Import(FilterExceptionHandler.class)
public class EcaAuditLogConfiguration {
}
