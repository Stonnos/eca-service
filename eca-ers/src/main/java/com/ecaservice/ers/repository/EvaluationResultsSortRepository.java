package com.ecaservice.ers.repository;

import com.ecaservice.ers.model.EvaluationResultsSortEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository to manage with {@link EvaluationResultsSortEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EvaluationResultsSortRepository extends JpaRepository<EvaluationResultsSortEntity, Long> {

    /**
     * Gets all evaluation results sort fields.
     *
     * @return evaluation results sort fields list
     */
    List<EvaluationResultsSortEntity> findByOrderByFieldOrder();
}
