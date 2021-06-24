package com.ecaservice.audit.repository;

import com.ecaservice.audit.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository to manage with {@link AuditLogEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AuditLogRepository
        extends JpaRepository<AuditLogEntity, Long>, JpaSpecificationExecutor<AuditLogEntity> {
}
