package com.ecaservice.auto.test.repository;

import com.ecaservice.auto.test.entity.AutoTestsJobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

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
     * @return tests ids list
     */
    /*@Query("select t.id from LoadTestEntity t where t.executionStatus = 'IN_PROGRESS' and " +
            "(select count(er) from EvaluationRequestEntity er where er.loadTestEntity = t " +
            "and er.stageType = 'REQUEST_SENT') = 0 order by t.created")
    List<Long> findFinishedTests();*/

    /**
     * Finds auto tests page with specified ids.
     *
     * @param ids      - ids list
     * @param pageable - pageable object
     * @return evaluation requests page
     */
    Page<AutoTestsJobEntity> findByIdIn(Collection<Long> ids, Pageable pageable);
}
