package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link EvaluationRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationRequestRepository extends JpaRepository<EvaluationRequestEntity, Long> {

    /**
     * Finds evaluation request with correlation id.
     *
     * @param correlationId - correlation id
     * @return evaluation request entity
     */
    Optional<EvaluationRequestEntity> findByCorrelationId(String correlationId);
}
