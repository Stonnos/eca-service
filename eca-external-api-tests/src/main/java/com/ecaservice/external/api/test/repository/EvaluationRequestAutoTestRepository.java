package com.ecaservice.external.api.test.repository;

import com.ecaservice.external.api.test.entity.EvaluationRequestAutoTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link EvaluationRequestAutoTestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationRequestAutoTestRepository extends JpaRepository<EvaluationRequestAutoTestEntity, Long> {
}
