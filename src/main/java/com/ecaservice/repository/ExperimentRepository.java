package com.ecaservice.repository;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface ExperimentRepository extends JpaRepository<Experiment, Long> {

    /**
     * Finds experiment by uuid.
     *
     * @param uuid uuid
     * @return {@link Experiment} object
     */
    Experiment findByUuid(String uuid);

    /**
     * Finds not sent experiments by statuses
     *
     * @param statuses {@link ExperimentStatus} collection
     * @return {@link Experiment} list
     */
    @Query("select e from Experiment e where e.experimentStatus in (:statuses) and e.sentDate is null order by e.creationDate")
    List<Experiment> findNotSentExperiments(@Param("statuses") Collection<ExperimentStatus> statuses);

    /**
     * Finds experiments which sent date is after N days.
     *
     * @param dateTime date time threshold value
     * @return {@link Experiment} list
     */
    @Query("select e from Experiment e where e.deletedDate is null and e.sentDate < :dateTime order by e.sentDate")
    List<Experiment> findNotDeletedExperiments(@Param("dateTime") LocalDateTime dateTime);

}
