package com.ecaservice.core.audit.repository;

import com.ecaservice.core.audit.entity.AuditGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Service to manage with {@link AuditGroupEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AuditGroupRepository extends JpaRepository<AuditGroupEntity, String> {
}
