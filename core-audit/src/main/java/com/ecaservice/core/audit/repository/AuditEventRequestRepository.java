package com.ecaservice.core.audit.repository;

import com.ecaservice.core.audit.entity.AuditEventRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link AuditEventRequestEntity}.
 *
 * @author Roman Batygin
 */
public interface AuditEventRequestRepository extends JpaRepository<AuditEventRequestEntity, Long> {
}
