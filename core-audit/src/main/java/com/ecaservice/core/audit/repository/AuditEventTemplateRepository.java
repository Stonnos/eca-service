package com.ecaservice.core.audit.repository;

import com.ecaservice.core.audit.entity.AuditCodeEntity;
import com.ecaservice.core.audit.entity.AuditEventTemplateEntity;
import com.ecaservice.core.audit.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Service to manage with {@link AuditEventTemplateEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AuditEventTemplateRepository extends JpaRepository<AuditEventTemplateEntity, String> {

    /**
     * Finds audit entity with specified code and type.
     *
     * @param auditCode - audit code
     * @param eventType - event type
     * @return audit entity
     */
    Optional<AuditEventTemplateEntity> findByAuditCodeAndEventType(AuditCodeEntity auditCode, EventType eventType);
}
