package com.ecaservice.core.audit.repository;

import com.ecaservice.core.audit.entity.AuditCodeEntity;
import com.ecaservice.core.audit.entity.AuditEventEntity;
import com.ecaservice.core.audit.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Service to manage with {@link AuditEventEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AuditEventRepository extends JpaRepository<AuditEventEntity, String> {

    /**
     * Finds audit entity with specified code and type.
     *
     * @param auditCode - audit code
     * @param eventType - event type
     * @return audit entity
     */
    Optional<AuditEventEntity> findByAuditCodeAndEventType(AuditCodeEntity auditCode, EventType eventType);
}
