package com.ecaservice.load.test.repository;

import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link EvaluationRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationRequestRepository extends JpaRepository<EvaluationRequestEntity, Long> {
}
