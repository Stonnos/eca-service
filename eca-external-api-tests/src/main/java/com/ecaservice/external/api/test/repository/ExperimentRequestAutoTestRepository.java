package com.ecaservice.external.api.test.repository;

import com.ecaservice.external.api.test.entity.ExperimentRequestAutoTestEntity;
import com.ecaservice.external.api.test.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ExperimentRequestAutoTestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentRequestAutoTestRepository extends JpaRepository<ExperimentRequestAutoTestEntity, Long> {

    /**
     * Finds all auto tests by job.
     *
     * @param jobEntity - job entity
     * @param pageable  - pageable object
     * @return auto tests entities page
     */
    Page<ExperimentRequestAutoTestEntity> findAllByJob(JobEntity jobEntity, Pageable pageable);
}
