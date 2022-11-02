package com.ecaservice.external.api.repository;

import com.ecaservice.external.api.entity.ExperimentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link ExperimentRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentRequestRepository extends JpaRepository<ExperimentRequestEntity, Long> {

    /**
     * Finds experiment request entity by correlation id.
     *
     * @param correlationId - correlation id
     * @return experiment request entity
     */
    Optional<ExperimentRequestEntity> findByCorrelationId(String correlationId);
}
