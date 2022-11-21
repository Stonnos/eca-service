package com.ecaservice.external.api.test.repository;

import com.ecaservice.external.api.test.entity.EvaluationRequestAutoTestEntity;
import com.ecaservice.external.api.test.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link EvaluationRequestAutoTestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationRequestAutoTestRepository extends JpaRepository<EvaluationRequestAutoTestEntity, Long> {

    /**
     * Finds all auto tests by job.
     *
     * @param jobEntity - job entity
     * @param pageable  - pageable object
     * @return auto tests entities page
     */
    Page<EvaluationRequestAutoTestEntity> findAllByJob(JobEntity jobEntity, Pageable pageable);
}
