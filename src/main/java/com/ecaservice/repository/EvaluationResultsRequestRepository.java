package com.ecaservice.repository;

import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link EvaluationResultsRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationResultsRequestRepository extends JpaRepository<EvaluationResultsRequestEntity, Long> {
}
