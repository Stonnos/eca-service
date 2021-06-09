package com.ecaservice.core.audit.repository;

import com.ecaservice.core.audit.entity.AuditCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Service to manage with {@link AuditCodeEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AuditCodeRepository extends JpaRepository<AuditCodeEntity, String> {
}
