package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.ExperimentResultsTestStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link ExperimentResultsTestStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentResultsTestStepRepository extends JpaRepository<ExperimentResultsTestStepEntity, Long> {

    /**
     * Finds experiment results steps to compare results.
     *
     * @return steps ids list
     */
    @Query("select ert.id from ExperimentResultsTestStepEntity ert join ert.evaluationRequestEntity " +
            "where ert.executionStatus = 'IN_PROGRESS' and " +
            "ert.evaluationRequestEntity.stageType = 'REQUEST_FINISHED' order by ert.created")
    List<Long> findStepsToCompareResults();

    /**
     * Finds experiment results steps page with specified ids.
     *
     * @param ids - ids list
     * @return experiment results steps page
     */
    List<ExperimentResultsTestStepEntity> findByIdInOrderByCreated(Collection<Long> ids);
}
