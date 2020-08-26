package com.ecaservice.load.test.repository;

import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.RequestStageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link EvaluationRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationRequestRepository extends JpaRepository<EvaluationRequestEntity, Long> {

    /**
     * Finds evaluation request with correlation id and stage.
     *
     * @param correlationId    - correlation id
     * @param requestStageType - request stage
     * @return evaluation request entity
     */
   EvaluationRequestEntity findByCorrelationIdAndStageTypeEquals(String correlationId,
                                                                            RequestStageType requestStageType);
}
