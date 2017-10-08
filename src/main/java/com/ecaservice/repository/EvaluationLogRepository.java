package com.ecaservice.repository;

import com.ecaservice.model.entity.EvaluationLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Implements repository that deals with {@link EvaluationLog} entities.
 *
 * @author Roman Batygin
 */
public interface EvaluationLogRepository extends JpaRepository<EvaluationLog, Long> {
}
