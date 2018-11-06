package com.ecaservice.repository;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.evaluation.EvaluationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
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

    /**
     * Gets evaluation logs by specification.
     *
     * @param specification - specification object
     * @param pageable      - pageable object
     * @return evaluation logs page
     */
    @EntityGraph(value = "evaluationLogs", type = EntityGraph.EntityGraphType.FETCH)
    Page<EvaluationLog> findAll(Specification<EvaluationLog> specification, Pageable pageable);
}
