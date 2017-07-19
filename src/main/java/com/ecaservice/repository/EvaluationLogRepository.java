package com.ecaservice.repository;

import com.ecaservice.model.entity.EvaluationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Roman Batygin
 */
@Repository
public interface EvaluationLogRepository extends JpaRepository<EvaluationLog, Long> {
}
