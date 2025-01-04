package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.ecaservice.server.util.JpaLockOptions.SKIP_LOCKED;
import static org.hibernate.cfg.AvailableSettings.JAKARTA_LOCK_TIMEOUT;

/**
 * Implements repository that manages with {@link EvaluationLog} entities.
 *
 * @author Roman Batygin
 */
public interface EvaluationLogRepository
        extends JpaRepository<EvaluationLog, Long>, JpaSpecificationExecutor<EvaluationLog> {

    /**
     * Finds evaluation requests to process:
     * Evaluation new request and retry requests from web channel
     * Evaluation retry requests from queue channel
     *
     * @param dateTime - date time to compare with locked ttl
     * @param pageable - pageable object
     * @return evaluation requests list
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = JAKARTA_LOCK_TIMEOUT, value = SKIP_LOCKED)})
    @Query("select ev from EvaluationLog ev where (ev.requestStatus = 'NEW' or ev.requestStatus = 'IN_PROGRESS') " +
            "and (ev.lockedTtl is null or ev.lockedTtl < :dateTime) and " +
            "((ev.channel = 'QUEUE' and ev.retryAt is not null and ev.retryAt < :dateTime) or " +
            "(ev.channel = 'WEB' and (ev.retryAt is null or ev.retryAt < :dateTime))) order by ev.creationDate")
    List<EvaluationLog> findRequestsToProcess(@Param("dateTime") LocalDateTime dateTime, Pageable pageable);

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
     * @param nowDate  - now date
     * @param pageable - pageable object
     * @return evaluation log list
     */
    @Query("select ev from EvaluationLog ev where ev.requestStatus = 'FINISHED' and " +
            "ev.deletedDate is null and ev.endDate < :dateTime and " +
            "(ev.retryAt is null or ev.retryAt < :nowDate) order by ev.endDate")
    List<EvaluationLog> findModelsToDelete(@Param("dateTime") LocalDateTime dateTime,
                                           @Param("nowDate") LocalDateTime nowDate,
                                           Pageable pageable);

    /**
     * Locks specified evaluation requests.
     *
     * @param ids     - evaluation logs ids
     * @param nowTime - now time
     */
    @Modifying
    @Query("update EvaluationLog ev set ev.lockedTtl = :nowTime where ev.id in (:ids)")
    void lock(@Param("ids") Collection<Long> ids, @Param("nowTime") LocalDateTime nowTime);

    /**
     * Resets evaluation log lock.
     *
     * @param id - evaluation log id
     */
    @Transactional
    @Modifying
    @Query("update EvaluationLog ev set ev.lockedTtl = null where ev.id = :id")
    void resetLock(@Param("id") Long id);
}
