package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.test.common.model.ExecutionStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link ExperimentRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentRequestRepository extends JpaBaseEvaluationRequestRepository<ExperimentRequestEntity> {

    /**
     * Finds finished requests ids.
     *
     * @return requests ids list
     */
    @Query("select er.id from ExperimentRequestEntity er where er.stageType = 'REQUEST_FINISHED' order by er.started")
    List<Long> findFinishedRequests();

    /**
     * Finds finished test ids.
     *
     * @param statuses - final execution statuses for test steps
     * @return requests ids list
     */
    @Query("select er.id from ExperimentRequestEntity er where er.stageType = 'COMPLETED' " +
            "and er.executionStatus not in (:statuses) " +
            "and not exists (select ts.id from BaseTestStepEntity ts where ts.evaluationRequestEntity = er and " +
            "ts.executionStatus not in (:statuses)) order by er.started")
    List<Long> findFinishedTests(@Param("statuses") Collection<ExecutionStatus> statuses);
}
