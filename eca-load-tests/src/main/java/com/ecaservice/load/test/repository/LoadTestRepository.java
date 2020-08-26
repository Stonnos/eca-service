package com.ecaservice.load.test.repository;

import com.ecaservice.load.test.entity.LoadTestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link LoadTestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface LoadTestRepository extends JpaRepository<LoadTestEntity, Long> {

    /**
     * Gets finished tests.
     *
     * @return tests ids list
     */
    @Query("select t.id from LoadTestEntity t where t.executionStatus = 'IN_PROGRESS' and " +
            "(select count(er) from EvaluationRequestEntity er where er.loadTestEntity = t " +
            "and er.stageType = 'REQUEST_SENT') = 0")
    List<Long> findFinishedTests();

    /**
     * Finds load tests page with specified ids.
     *
     * @param ids      - ids list
     * @param pageable - pageable object
     * @return evaluation requests page
     */
    Page<LoadTestEntity> findByIdIn(Collection<Long> ids, Pageable pageable);
}
