package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.test.common.model.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
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
     * @return tests ids list
     */
    @Query("select t.id from AutoTestsJobEntity t where t.executionStatus = 'NEW' order by t.created")
    List<Long> findNewTests();

    /**
     * Gets finished tests.
     *
     * @param executionStatuses - finished execution statuses
     * @return tests ids list
     */
    @Query("select t.id from AutoTestsJobEntity t where t.executionStatus = 'IN_PROGRESS' and " +
            "not exists (select er.id from BaseEvaluationRequestEntity er where er.job = t " +
            "and er.executionStatus not in (:executionStatuses)) order by t.created")
    List<Long> findFinishedJobs(@Param("executionStatuses") Collection<ExecutionStatus> executionStatuses);

    /**
     * Finds auto tests page with specified ids.
     *
     * @param ids - ids list
     * @return evaluation requests page
     */
    List<AutoTestsJobEntity> findByIdInOrderByCreated(Collection<Long> ids);

    /**
     * Finds auto tests job by uuid.
     *
     * @param jobUuid - job uuid
     * @return auto tests job entity
     */
    Optional<AutoTestsJobEntity> findByJobUuid(String jobUuid);
}
