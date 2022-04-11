package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.TestFeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link TestFeatureEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface TestFeatureRepository extends JpaRepository<TestFeatureEntity, Long> {
}
