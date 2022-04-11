package com.ecaservice.auto.test.repository.ecaserver;

import com.ecaservice.auto.test.entity.ecaserver.ErsEvaluationResultsRequestEntity;
import com.ecaservice.auto.test.entity.ecaserver.EvaluationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link ErsEvaluationResultsRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ErsEvaluationResultsRequestRepository extends JpaRepository<ErsEvaluationResultsRequestEntity, Long> {

    /**
     * Gets ERS evaluation results requests.
     *
     * @param evaluationLog - evaluation log
     * @return ERS evaluation results requests
     */
    Optional<ErsEvaluationResultsRequestEntity> findByEvaluationLog(EvaluationLog evaluationLog);
}
