package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.test.common.model.ExecutionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

/**
 * Repository to manage with {@link AutoTestsJobEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AutoTestsJobRepository extends JpaRepository<AutoTestsJobEntity, Long> {

    /**
     * Gets new tests for processing.
     *
     * @param pageable - pageable
     * @return auto tests jobs page
     */
    @Query("select t from AutoTestsJobEntity t where t.executionStatus = 'NEW' order by t.created")
    Page<AutoTestsJobEntity> findNewTests(Pageable pageable);

    /**
     * Gets finished tests.
     *
     * @param executionStatuses - finished execution statuses
     * @param pageable          - pageable
     * @return tests ids list
     */
    @Query("select t from AutoTestsJobEntity t where t.executionStatus = 'IN_PROGRESS' and " +
            "not exists (select er.id from BaseEvaluationRequestEntity er where er.job = t " +
            "and er.executionStatus not in (:executionStatuses)) order by t.created")
    Page<AutoTestsJobEntity> findFinishedJobs(@Param("executionStatuses") Collection<ExecutionStatus> executionStatuses,
                                              Pageable pageable);

    /**
     * Finds auto tests job by uuid.
     *
     * @param jobUuid - job uuid
     * @return auto tests job entity
     */
    Optional<AutoTestsJobEntity> findByJobUuid(String jobUuid);
}
