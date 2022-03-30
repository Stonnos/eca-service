package com.ecaservice.external.api.test.repository;

import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link AutoTestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AutoTestRepository extends JpaRepository<AutoTestEntity, Long> {

    /**
     * Finds all auto tests by job.
     *
     * @param jobEntity - job entity
     * @param pageable  - pageable object
     * @return auto tests entities page
     */
    Page<AutoTestEntity> findAllByJob(JobEntity jobEntity, Pageable pageable);

    /**
     * Finds all auto tests by job.
     *
     * @param jobEntity - job entity
     * @return auto tests entities page
     */
    Page<AutoTestEntity> findAllByJob(JobEntity jobEntity);
}
