package com.ecaservice.repository;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.evaluation.EvaluationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;

/**
 * Implements repository that manages with {@link EvaluationLog} entities.
 *
 * @author Roman Batygin
 */
public interface EvaluationLogRepository
        extends JpaRepository<EvaluationLog, Long>, JpaSpecificationExecutor<EvaluationLog> {

    /**
     * Finds evaluation log by request id and statuses.
     *
     * @param requestId          - request id
     * @param evaluationStatuses - evaluation log statuses
     * @return evaluation log entity
     */
    EvaluationLog findByRequestIdAndEvaluationStatusIn(String requestId,
                                                       Collection<EvaluationStatus> evaluationStatuses);
}
