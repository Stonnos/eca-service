package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationResultsTestStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link EvaluationResultsTestStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationResultsTestStepRepository extends JpaRepository<EvaluationResultsTestStepEntity, Long> {

    /**
     * Finds evaluation results steps to compare results.
     *
     * @return steps ids list
     */
    @Query("select ert.id from EvaluationResultsTestStepEntity ert join ert.evaluationRequestEntity " +
            "where ert.executionStatus = 'IN_PROGRESS' and " +
            "ert.evaluationRequestEntity.stageType = 'REQUEST_FINISHED' order by ert.created")
    List<Long> findStepsToCompareResults();

    /**
     * Finds evaluation results steps page with specified ids.
     *
     * @param ids - ids list
     * @return experiment results steps page
     */
    List<EvaluationResultsTestStepEntity> findByIdInOrderByCreated(Collection<Long> ids);

    /**
     * Finds evaluation results test step by request entity.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @return evaluation results test step entity
     */
    EvaluationResultsTestStepEntity findByEvaluationRequestEntity(BaseEvaluationRequestEntity evaluationRequestEntity);
}
