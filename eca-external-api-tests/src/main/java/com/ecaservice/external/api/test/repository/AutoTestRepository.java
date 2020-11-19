package com.ecaservice.external.api.test.repository;

import com.ecaservice.external.api.test.entity.AutoTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link AutoTestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AutoTestRepository extends JpaRepository<AutoTestEntity, Long> {
}
