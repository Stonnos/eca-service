package com.ecaservice.load.test.repository;

import com.ecaservice.load.test.entity.LoadTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link LoadTestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface LoadTestRepository extends JpaRepository<LoadTestEntity, Long> {
}
