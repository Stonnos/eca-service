package com.ecaservice.load.test.repository;

import com.ecaservice.load.test.entity.ExecutionStatus;
import com.ecaservice.load.test.entity.LoadTestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

/**
 * Repository to manage with {@link LoadTestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface LoadTestRepository extends JpaRepository<LoadTestEntity, Long> {

    /**
     * Gets load tests with specified execution statuses.
     *
     * @param statuses - execution statuses
     * @param pageable - pageable object
     * @return load tests page
     */
    Page<LoadTestEntity> findAllByExecutionStatusIn(Collection<ExecutionStatus> statuses, Pageable pageable);
}
