package com.ecaservice.repository;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link EvaluationResultsRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationResultsRequestEntityRepository extends JpaRepository<EvaluationResultsRequestEntity, Long> {

    /**
     * Finds first evaluation results request entity by evaluation log.
     *
     * @param evaluationLog - evaluation log
     * @return evaluation results request entity
     */
    EvaluationResultsRequestEntity findByEvaluationLog(EvaluationLog evaluationLog);
}
