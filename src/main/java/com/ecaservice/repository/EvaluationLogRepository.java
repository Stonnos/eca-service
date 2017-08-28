package com.ecaservice.repository;

import com.ecaservice.model.EvaluationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Implements repository that deals with {@link EvaluationLog} entities.
 *
 * @author Roman Batygin
 */
@Repository
public interface EvaluationLogRepository extends JpaRepository<EvaluationLog, Long> {
}
