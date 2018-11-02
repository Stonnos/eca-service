package com.ecaservice.repository;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Implements repository that manages with {@link Experiment} entities.
 *
 * @author Roman Batygin
 */
public interface ExperimentRepository extends JpaRepository<Experiment, Long>, JpaSpecificationExecutor<Experiment> {

    /**
     * Finds experiment by uuid and statuses.
     *
     * @param uuid               - experiment uuid
     * @param experimentStatuses - experiment statuses
     * @return experiment entity
     */
    Experiment findByUuidAndExperimentStatusIn(String uuid, Collection<ExperimentStatus> experimentStatuses);

    /**
     * Finds experiment by uuid.
     *
     * @param uuid - experiment uuid
     * @return experiment entity
     */
    Experiment findByUuid(String uuid);

    /**
     * Finds not sent experiments by statuses
     *
     * @param statuses - {@link ExperimentStatus} collection
     * @param pageable - {@link Pageable} object
     * @return experiments list
     */
    @Query("select exp from Experiment exp where exp.experimentStatus in (:statuses) and exp.sentDate is null order by exp.creationDate")
    Page<Experiment> findNotSentExperiments(@Param("statuses") Collection<ExperimentStatus> statuses,
                                            Pageable pageable);

    /**
     * Finds experiments which sent date is after N days.
     *
     * @param dateTime date time threshold value
     * @param pageable {@link Pageable} object
     * @return experiments list
     */
    @Query("select exp from Experiment exp where exp.sentDate is not null and exp.deletedDate is null and " +
            "exp.sentDate < :dateTime order by exp.sentDate")
    Page<Experiment> findNotDeletedExperiments(@Param("dateTime") LocalDateTime dateTime, Pageable pageable);

    /**
     * Calculates experiments count with specified status.
     *
     * @param experimentStatus - experiment status
     * @return experiments count
     */
    long countByExperimentStatus(ExperimentStatus experimentStatus);
}
