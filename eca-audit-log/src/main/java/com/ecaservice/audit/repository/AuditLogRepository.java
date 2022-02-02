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

    /**
     * Check audit log existing with specified event id.
     *
     * @param eventId - event id
     * @return {@code true} if audit log with specified event id exists, otherwise {@link false}
     */
    boolean existsByEventId(String eventId);
}
