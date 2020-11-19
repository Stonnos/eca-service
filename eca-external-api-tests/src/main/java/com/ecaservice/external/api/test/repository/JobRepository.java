package com.ecaservice.external.api.test.repository;

import com.ecaservice.external.api.test.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link JobEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface JobRepository extends JpaRepository<JobEntity, Long> {

    /**
     * Finds job entity by uuid.
     *
     * @param jobUuid - job uuid
     * @return job entity
     */
    Optional<JobEntity> findByJobUuid(String jobUuid);
}
