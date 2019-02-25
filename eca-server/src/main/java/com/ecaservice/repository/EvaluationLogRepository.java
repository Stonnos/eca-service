package com.ecaservice.repository;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.projections.RequestStatusStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

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
    EvaluationLog findByRequestIdAndEvaluationStatusIn(String requestId, Collection<RequestStatus> evaluationStatuses);

    /**
     * Calculates requests status counting statistics.
     *
     * @return requests status counting statistics list
     */
    @Query("select el.evaluationStatus as requestStatus, count(el.evaluationStatus) as requestsCount from " +
            "EvaluationLog el group by el.evaluationStatus")
    List<RequestStatusStatistics> getRequestStatusesStatistics();

    @EntityGraph(value = "inputOptions", type = EntityGraph.EntityGraphType.FETCH)
    Page<EvaluationLog> findAll(Specification<EvaluationLog> specification, Pageable pageable);
}
