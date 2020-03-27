package com.ecaservice.repository;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.projections.RequestStatusStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
    Experiment findByRequestId(String requestId);

    /**
     * Finds experiment by token.
     *
     * @param token - experiment token
     * @return experiment entity
     */
    Experiment findByToken(String token);

    /**
     * Finds not sent experiments by statuses
     *
     * @param statuses - {@link RequestStatus} collection
     * @return experiments list
     */
    @Query("select exp from Experiment exp where exp.requestStatus in (:statuses) and exp.sentDate is null order by exp.creationDate")
    List<Experiment> findNotSentExperiments(@Param("statuses") Collection<RequestStatus> statuses);

    /**
     * Finds experiments which sent date is after N days.
     *
     * @param dateTime date time threshold value
     * @return experiments list
     */
    @Query("select exp from Experiment exp where exp.sentDate is not null and exp.deletedDate is null and " +
            "exp.sentDate < :dateTime order by exp.sentDate")
    List<Experiment> findNotDeletedExperiments(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Calculates requests status counting statistics.
     *
     * @return requests status counting statistics list
     */
    @Query("select e.requestStatus as requestStatus, count(e.requestStatus) as requestsCount from " +
            "Experiment e group by e.requestStatus")
    List<RequestStatusStatistics> getRequestStatusesStatistics();
}
