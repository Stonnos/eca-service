package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationResultsTestStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link EvaluationResultsTestStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationResultsTestStepRepository extends JpaRepository<EvaluationResultsTestStepEntity, Long> {

    /**
     * Finds evaluation results test step by request entity.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @return evaluation results test step entity
     */
    EvaluationResultsTestStepEntity findByEvaluationRequestEntity(BaseEvaluationRequestEntity evaluationRequestEntity);
}
