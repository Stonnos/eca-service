package com.ecaservice.auto.test.repository;

import com.ecaservice.auto.test.entity.ExperimentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link ExperimentRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentRequestRepository extends JpaRepository<ExperimentRequestEntity, Long> {

    /**
     * Finds experiment request with correlation id and stage.
     *
     * @param correlationId - correlation id
     * @return experiment request entity
     */
    ExperimentRequestEntity findByCorrelationId(String correlationId);

    /**
     * Finds experiment request by request id
     *
     * @param requestId - request id
     * @return experiment request entity
     */
    Optional<ExperimentRequestEntity> findByRequestId(String requestId);
}
