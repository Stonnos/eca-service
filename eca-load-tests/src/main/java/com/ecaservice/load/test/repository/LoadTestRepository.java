package com.ecaservice.load.test.repository;

import com.ecaservice.load.test.entity.LoadTestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository to manage with {@link LoadTestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface LoadTestRepository extends JpaRepository<LoadTestEntity, Long> {

    /**
     * Gets load test entity by uuid.
     *
     * @param testUuid - test uuid
     * @return load test entity
     */
    Optional<LoadTestEntity> findByTestUuid(String testUuid);

    /**
     * Gets new tests for processing.
     *
     * @param pageable - pageable object
     * @return tests ids list
     */
    @Query("select t from LoadTestEntity t where t.executionStatus = 'NEW' order by t.created")
    Page<LoadTestEntity> findNewTests(Pageable pageable);

    /**
     * Gets finished tests.
     *
     * @param pageable - pageable
     * @return tests ids list
     */
    @Query("select t from LoadTestEntity t where t.executionStatus = 'IN_PROGRESS' and " +
            "(select count(er) from EvaluationRequestEntity er where er.loadTestEntity = t " +
            "and er.stageType = 'REQUEST_SENT') = 0 order by t.created")
    Page<LoadTestEntity> findFinishedTests(Pageable pageable);
}
