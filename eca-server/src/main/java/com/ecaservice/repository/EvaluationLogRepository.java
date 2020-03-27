package com.ecaservice.repository;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.projections.RequestStatusStatistics;
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
     * @param requestStatuses - evaluation log statuses
     * @return evaluation log entity
     */
    EvaluationLog findByRequestIdAndRequestStatusIn(String requestId, Collection<RequestStatus> requestStatuses);

    /**
     * Finds evaluation log by request id.
     *
     * @param requestId - evaluation log request id
     * @return evaluation log entity
     */
    EvaluationLog findByRequestId(String requestId);

    /**
     * Calculates requests status counting statistics.
     *
     * @return requests status counting statistics list
     */
    @Query("select el.requestStatus as requestStatus, count(el.requestStatus) as requestsCount from " +
            "EvaluationLog el group by el.requestStatus")
    List<RequestStatusStatistics> getRequestStatusesStatistics();
}
