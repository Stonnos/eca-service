package com.ecaservice.auto.test.repository.ecaserver;

import com.ecaservice.auto.test.entity.ecaserver.EvaluationLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Implements repository that manages with {@link EvaluationLog} entities.
 *
 * @author Roman Batygin
 */
public interface EvaluationLogRepository extends JpaRepository<EvaluationLog, Long> {
}
