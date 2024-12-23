package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
     * Finds new evaluation requests.
     *
     * @param dateTime - date time to compare with lcoked ttl
     * @param pageable - pageable object
     * @return evaluation requests ids list
     */
    @Query("select ev from EvaluationLog ev where ev.requestStatus = 'NEW' and ev.channel = 'WEB' " +
            "and (ev.lockedTtl is null or ev.lockedTtl < :dateTime) order by ev.creationDate")
    Page<EvaluationLog> findNewWebRequests(@Param("dateTime") LocalDateTime dateTime, Pageable pageable);

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
     * @param dateTime - date time threshold value
     * @param nowTime - now date time
     * @param pageable - pageable object
     * @return evaluation log ids list
     */
    @Query("select ev from EvaluationLog ev where ev.requestStatus = 'FINISHED' and " +
            "ev.deletedDate is null and ev.endDate < :dateTime and " +
            "(ev.lockedTtl is null or ev.lockedTtl < :nowTime) order by ev.endDate")
    Page<EvaluationLog> findModelsToDelete(@Param("dateTime") LocalDateTime dateTime,
                                           @Param("nowTime") LocalDateTime nowTime,
                                           Pageable pageable);
}
