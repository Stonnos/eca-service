package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
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
     * Finds new experiments.
     *
     * @return experiments ids list
     */
    @Query("select exp.id from Experiment exp where exp.requestStatus = 'NEW' order by exp.creationDate")
    List<Long> findNewExperiments();

    /**
     * Finds experiments to process with step statuses not in blacklist.
     *
     * @param stepStatuses - experiment step statuses blacklist
     * @return experiments ids list
     */
    @Query("select exp.id from Experiment exp where exp.requestStatus = 'IN_PROGRESS' " +
            "and not exists (select es.id from ExperimentStepEntity es where es.experiment = exp " +
            "and es.status in (:stepStatuses)) order by exp.creationDate")
    List<Long> findExperimentsToProcess(@Param("stepStatuses") Collection<ExperimentStepStatus> stepStatuses);

    /**
     * Gets experiments page with specified ids.
     *
     * @param ids - experiment ids
     * @return experiments page
     */
    List<Experiment> findByIdIn(Collection<Long> ids);

    /**
     * Finds experiments models to delete.
     *
     * @param dateTime date time threshold value
     * @return experiments ids list
     */
    @Query("select exp.id from Experiment exp where exp.requestStatus = 'FINISHED' and " +
            "exp.deletedDate is null and exp.endDate < :dateTime order by exp.endDate")
    List<Long> findExperimentsModelsToDelete(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Finds experiments training data to delete.
     *
     * @param dateTime date time threshold value
     * @return experiments ids list
     */
    @Query("select exp.id from Experiment exp where " +
            "exp.trainingDataPath is not null and exp.creationDate < :dateTime order by exp.creationDate")
    List<Long> findExperimentsTrainingDataToDelete(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Calculates requests status counting statistics.
     *
     * @return requests status counting statistics list
     */
    @Query("select e.requestStatus as requestStatus, count(e.requestStatus) as requestsCount from " +
            "Experiment e group by e.requestStatus")
    List<RequestStatusStatistics> getRequestStatusesStatistics();
}
