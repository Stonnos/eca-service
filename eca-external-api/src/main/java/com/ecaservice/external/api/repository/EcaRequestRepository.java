package com.ecaservice.external.api.repository;

import com.ecaservice.external.api.entity.EcaRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link EcaRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EcaRequestRepository extends JpaRepository<EcaRequestEntity, Long> {
}
