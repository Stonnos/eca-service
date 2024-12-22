package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.ExperimentResultsTestStepEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository to manage with {@link ExperimentResultsTestStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentResultsTestStepRepository extends JpaRepository<ExperimentResultsTestStepEntity, Long> {

    /**
     * Finds experiment results steps to compare results.
     *
     * @param pageable - pageable
     * @return steps page
     */
    @Query("select ert from ExperimentResultsTestStepEntity ert join ert.evaluationRequestEntity " +
            "where ert.executionStatus = 'IN_PROGRESS' and " +
            "ert.evaluationRequestEntity.stageType = 'REQUEST_FINISHED' order by ert.created")
    Page<ExperimentResultsTestStepEntity> findStepsToCompareResults(Pageable pageable);
}
