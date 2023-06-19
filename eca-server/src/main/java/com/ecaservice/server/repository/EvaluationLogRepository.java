package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Implements repository that manages with {@link EvaluationLog} entities.
 *
 * @author Roman Batygin
 */
public interface EvaluationLogRepository
        extends JpaRepository<EvaluationLog, Long>, JpaSpecificationExecutor<EvaluationLog> {

    /**
     * Finds evaluation log by request id.
     *
     * @param requestId - evaluation log request id
     * @return evaluation log entity
     */
    Optional<EvaluationLog> findByRequestId(String requestId);

    /**
     * Calculates requests status counting statistics.
     *
     * @return requests status counting statistics list
     */
    @Query("select el.requestStatus as requestStatus, count(el.requestStatus) as requestsCount from " +
            "EvaluationLog el group by el.requestStatus")
    List<RequestStatusStatistics> getRequestStatusesStatistics();

    /**
     * Finds classifiers models to delete.
     *
     * @param dateTime date time threshold value
     * @return evaluation log ids list
     */
    @Query("select ev.id from EvaluationLog ev where ev.requestStatus = 'FINISHED' and " +
            "ev.deletedDate is null and ev.endDate < :dateTime order by ev.endDate")
    List<Long> findModelsToDelete(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Gets evaluation logs page with specified ids.
     *
     * @param ids - experiment ids
     * @return evaluation logs page
     */
    List<EvaluationLog> findByIdIn(Collection<Long> ids);
}
