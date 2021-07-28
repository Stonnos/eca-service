package com.ecaservice.auto.test.repository;

import com.ecaservice.auto.test.entity.ExperimentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ExperimentRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentRequestRepository extends JpaRepository<ExperimentRequestEntity, Long> {

    /**
     * Finds experiment request with correlation id and stage.
     *
     * @param correlationId    - correlation id
     * @return evaluation request entity
     */
    ExperimentRequestEntity findByCorrelationId(String correlationId);
}
