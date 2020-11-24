package com.ecaservice.external.api.test.repository;

import com.ecaservice.external.api.test.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
     * Finds job entity by uuid.
     *
     * @param jobUuid - job uuid
     * @return job entity
     */
    Optional<JobEntity> findByJobUuid(String jobUuid);
}
