package com.ecaservice.external.api.repository;

import com.ecaservice.external.api.entity.EcaRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link EcaRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EcaRequestRepository extends JpaRepository<EcaRequestEntity, Long> {

    /**
     * Finds eca request entity by correlation id.
     *
     * @param correlationId - correlation id
     * @return eca request entity
     */
    Optional<EcaRequestEntity> findByCorrelationId(String correlationId);
}
