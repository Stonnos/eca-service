package com.ecaservice.load.test.repository;

import com.ecaservice.load.test.entity.LoadTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
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
     * @return tests ids list
     */
    @Query("select t.id from LoadTestEntity t where t.executionStatus = 'NEW' order by t.created")
    List<Long> findNewTests();

    /**
     * Gets finished tests.
     *
     * @return tests ids list
     */
    @Query("select t.id from LoadTestEntity t where t.executionStatus = 'IN_PROGRESS' and " +
            "(select count(er) from EvaluationRequestEntity er where er.loadTestEntity = t " +
            "and er.stageType = 'REQUEST_SENT') = 0 order by t.created")
    List<Long> findFinishedTests();

    /**
     * Finds load tests page with specified ids.
     *
     * @param ids - ids list
     * @return evaluation requests page
     */
    List<LoadTestEntity> findByIdIn(Collection<Long> ids);
}
