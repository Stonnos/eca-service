package com.ecaservice.external.api.test.repository;

import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.test.common.model.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository to manage with {@link JobEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface JobRepository extends JpaRepository<JobEntity, Long> {

    /**
     * Finds new auto tests jobs.
     *
     * @return jobs list
     */
    @Query("select job from JobEntity job where job.executionStatus = 'NEW' order by job.created")
    List<JobEntity> findNewJobs();

    /**
     * Gets finished tests.
     *
     * @param statuses - statuses list
     * @return tests ids list
     */
    @Query("select job from JobEntity job where job.executionStatus = 'IN_PROGRESS' and " +
            "(select count(t) from AutoTestEntity t where t.job = job " +
            "and t.executionStatus not in (:finishedStatuses)) = 0 order by job.created")
    List<JobEntity> findFinishedTests(@Param("finishedStatuses") Collection<ExecutionStatus> statuses);

    /**
     * Finds job entity by uuid.
     *
     * @param jobUuid - job uuid
     * @return job entity
     */
    Optional<JobEntity> findByJobUuid(String jobUuid);
}
