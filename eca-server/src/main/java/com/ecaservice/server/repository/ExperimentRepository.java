package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
     * @param dateTime - date time to compare with locked ttl
     * @param pageable - pageable object
     * @return experiments page
     */
    @Query("select e from Experiment e where (e.requestStatus = 'NEW' or e.requestStatus = 'IN_PROGRESS') and " +
            "(e.lockedTtl is null or e.lockedTtl < :dateTime) order by e.creationDate")
    Page<Experiment> findExperimentsToProcess(@Param("dateTime") LocalDateTime dateTime, Pageable pageable);

    /**
     * Finds experiments models to delete.
     *
     * @param dateTime - date time threshold value
     * @param pageable - pageable object
     * @return experiments ids list
     */
    @Query("select e from Experiment e where e.requestStatus = 'FINISHED' and " +
            "e.deletedDate is null and e.endDate < :dateTime order by e.endDate")
    Page<Experiment> findExperimentsModelsToDelete(@Param("dateTime") LocalDateTime dateTime,
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
     * Resets experiment lock.
     *
     * @param id - experiment id
     */
    @Transactional
    @Modifying
    @Query("update Experiment e set e.lockedTtl = null where e.id = :id")
    void resetLock(@Param("id") Long id);
}
