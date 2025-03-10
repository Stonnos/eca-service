package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
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
 * Implements repository that manages with {@link Experiment} entities.
 *
 * @author Roman Batygin
 */
public interface ExperimentRepository extends JpaRepository<Experiment, Long>, JpaSpecificationExecutor<Experiment> {

    /**
     * Finds experiment by request id.
     *
     * @param requestId - request id
     * @return experiment entity
     */
    Optional<Experiment> findByRequestId(String requestId);

    /**
     * Finds all new and in progress experiments to process.
     *
     * @param nowDate  - now date
     * @param pageable - pageable object
     * @return experiments list
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = JAKARTA_LOCK_TIMEOUT, value = SKIP_LOCKED)})
    @Query("select e from Experiment e where (e.requestStatus = 'NEW' or e.requestStatus = 'IN_PROGRESS') and " +
            "(e.lockedTtl is null or e.lockedTtl < :nowDate) and " +
            "(e.retryAt is null or e.retryAt < :nowDate) order by e.creationDate")
    List<Experiment> findExperimentsToProcess(@Param("nowDate") LocalDateTime nowDate, Pageable pageable);

    /**
     * Finds experiments models to delete.
     *
     * @param dateTime - date time threshold value
     * @param nowDate  - now date
     * @param pageable - pageable object
     * @return experiments list
     */
    @Query("select e from Experiment e where e.requestStatus = 'FINISHED' and " +
            "e.deletedDate is null and e.endDate < :dateTime and " +
            "(e.retryAt is null or e.retryAt < :nowDate) order by e.endDate")
    List<Experiment> findExperimentsModelsToDelete(@Param("dateTime") LocalDateTime dateTime,
                                                   @Param("nowDate") LocalDateTime nowDate,
                                                   Pageable pageable);

    /**
     * Calculates requests status counting statistics.
     *
     * @return requests status counting statistics list
     */
    @Query("select e.requestStatus as requestStatus, count(e.requestStatus) as requestsCount from " +
            "Experiment e group by e.requestStatus")
    List<RequestStatusStatistics> getRequestStatusesStatistics();

    /**
     * Locks specified experiment requests.
     *
     * @param ids     - experiments ids
     * @param nowTime - now time
     */
    @Modifying
    @Query("update Experiment e set e.lockedTtl = :nowTime where e.id in (:ids)")
    void lock(@Param("ids") Collection<Long> ids, @Param("nowTime") LocalDateTime nowTime);

    /**
     * Resets experiment lock.
     *
     * @param id - experiment id
     */
    @Transactional
    @Modifying
    @Query("update Experiment e set e.lockedTtl = null where e.id = :id")
    void resetLock(@Param("id") Long id);
}
