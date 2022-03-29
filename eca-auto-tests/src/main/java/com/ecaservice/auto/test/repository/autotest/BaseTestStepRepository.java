package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.BaseTestStepEntity;
import com.ecaservice.auto.test.projections.TestResultProjection;
import com.ecaservice.test.common.model.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link BaseTestStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface BaseTestStepRepository extends JpaRepository<BaseTestStepEntity, Long> {

    /**
     * Finds all test steps for specified evaluation request.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @param executionStatuses       - execution statuses
     * @return test steps list
     */
    List<BaseTestStepEntity> findAllByEvaluationRequestEntityAndExecutionStatusIn(
            BaseEvaluationRequestEntity evaluationRequestEntity, Collection<ExecutionStatus> executionStatuses);

    /**
     * Finds exceeded steps ids.
     *
     * @param dateTime          - date time value
     * @param executionStatuses - finished execution statuses
     * @return test steps ids list
     */
    @Query("select er.id from BaseTestStepEntity er where er.executionStatus not in (:executionStatuses)" +
            "and er.started < :dateTime order by er.started")
    List<Long> findExceededStepIds(@Param("dateTime") LocalDateTime dateTime,
                                   @Param("executionStatuses") Collection<ExecutionStatus> executionStatuses);

    /**
     * Finds tests steps page with specified ids.
     *
     * @param ids - ids list
     * @return tests steps page
     */
    List<BaseTestStepEntity> findByIdInOrderByCreated(Collection<Long> ids);

    /**
     * Gets test results for specified evaluation request.
     *
     * @param evaluationRequestEntity - evaluation request
     * @return evaluation request entity
     */
    @Query("select ts.testResult as testResult, ts.totalMatched as totalMatched, " +
            "ts.totalNotMatched as totalNotMatched, ts.totalNotFound as totalNotFound from BaseTestStepEntity ts " +
            "where ts.evaluationRequestEntity = :evaluationRequestEntity")
    List<TestResultProjection> getTestResults(
            @Param("evaluationRequestEntity") BaseEvaluationRequestEntity evaluationRequestEntity);
}
