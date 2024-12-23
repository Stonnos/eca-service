package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationResultsTestStepEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository to manage with {@link EvaluationResultsTestStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationResultsTestStepRepository extends JpaRepository<EvaluationResultsTestStepEntity, Long> {

    /**
     * Finds evaluation results steps to compare results.
     *
     * @param pageable - pageable obhect
     * @return steps page
     */
    @Query("select ert from EvaluationResultsTestStepEntity ert join ert.evaluationRequestEntity " +
            "where ert.executionStatus = 'IN_PROGRESS' and " +
            "ert.evaluationRequestEntity.stageType = 'REQUEST_FINISHED' order by ert.created")
    Page<EvaluationResultsTestStepEntity> findStepsToCompareResults(Pageable pageable);

    /**
     * Finds evaluation results test step by request entity.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @return evaluation results test step entity
     */
    EvaluationResultsTestStepEntity findByEvaluationRequestEntity(BaseEvaluationRequestEntity evaluationRequestEntity);
}
