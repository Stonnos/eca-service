package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
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
     * Finds new experiments.
     *
     * @return experiments ids list
     */
    @Query("select exp.id from Experiment exp where exp.requestStatus = 'NEW' order by exp.creationDate")
    List<Long> findNewExperiments();

    /**
     * Finds experiments to process.
     *
     * @param dateTime - timeout date time threshold value
     * @return experiments ids list
     */
    @Query("select exp.id from Experiment exp where exp.requestStatus = 'IN_PROGRESS' " +
            "and exp.startDate > :dateTime " +
            "and not exists (select es.id from ExperimentStepEntity es where es.experiment = exp " +
            "and (es.status = 'ERROR' or es.status = 'TIMEOUT' or es.status = 'CANCELED' " +
            "or es.status = 'IN_PROGRESS')) " +
            "and exists (select es.id from ExperimentStepEntity es where es.experiment = exp " +
            "and (es.status = 'READY' or es.status = 'FAILED')) order by exp.creationDate")
    List<Long> findExperimentsToProcess(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Finds timeout experiments to process.
     *
     * @param dateTime - timeout date time threshold value
     * @return experiments ids list
     */
    @Query("select exp.id from Experiment exp where exp.requestStatus = 'IN_PROGRESS' " +
            "and exp.startDate < :dateTime " +
            "and not exists (select es.id from ExperimentStepEntity es where es.experiment = exp " +
            "and (es.status = 'ERROR' or es.status = 'TIMEOUT' or es.status = 'CANCELED')) " +
            "and exists (select es.id from ExperimentStepEntity es where es.experiment = exp " +
            "and (es.status = 'READY' or es.status = 'FAILED' or es.status = 'IN_PROGRESS')) order by exp.creationDate")
    List<Long> findTimeoutExperimentsToProcess(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Finds experiments to finish.
     *
     * @return experiments ids list
     */
    @Query("select exp.id from Experiment exp where exp.requestStatus = 'IN_PROGRESS' " +
            "and not exists (select es.id from ExperimentStepEntity es where es.experiment = exp " +
            "and (es.status = 'READY' or es.status = 'FAILED' or es.status = 'IN_PROGRESS')) order by exp.creationDate")
    List<Long> findExperimentsToFinish();

    /**
     * Finds experiments models to delete.
     *
     * @param dateTime - date time threshold value
     * @param pageable - pageable object
     * @return experiments ids list
     */
    @Query("select e from Experiment e where e.requestStatus = 'FINISHED' and " +
            "e.deletedDate is null and e.endDate < :dateTime and " +
            "(e.deleteModelAfter is null or e.deleteModelAfter < :nowTime) order by e.endDate")
    Page<Experiment> findExperimentsModelsToDelete(@Param("dateTime") LocalDateTime dateTime,
                                                   @Param("nowTime") LocalDateTime nowTime,
                                                   Pageable pageable);

    /**
     * Calculates requests status counting statistics.
     *
     * @return requests status counting statistics list
     */
    @Query("select e.requestStatus as requestStatus, count(e.requestStatus) as requestsCount from " +
            "Experiment e group by e.requestStatus")
    List<RequestStatusStatistics> getRequestStatusesStatistics();
}
