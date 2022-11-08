package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link ExperimentStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentStepRepository extends JpaRepository<ExperimentStepEntity, Long> {

    /**
     * Finds experiment steps with specified statuses.
     *
     * @param experiment   - experiment entity
     * @param stepStatuses - experiment step statuses
     * @return experiments list
     */
    List<ExperimentStepEntity> findByExperimentAndStatusInOrderByStepOrder(Experiment experiment,
                                                                           Collection<ExperimentStepStatus> stepStatuses);

    /**
     * Gets experiment steps statuses.
     *
     * @param experiment - experiment entity
     * @return experiment step statuses
     */
    @Query("select es.status from ExperimentStepEntity es where es.experiment = :experiment order by es.stepOrder")
    List<ExperimentStepStatus> getStepStatuses(@Param("experiment") Experiment experiment);

    /**
     * Cancel all ready steps.
     *
     * @param experiment - experiment entity
     * @param completed  - completed date
     */
    @Transactional
    @Modifying
    @Query("update ExperimentStepEntity es set es.status = 'CANCELED', es.completed = :completed " +
            "where es.experiment = :experiment and es.status = 'READY'")
    void cancelSteps(@Param("experiment") Experiment experiment,
                     @Param("completed") LocalDateTime completed);

    /**
     * Gets experiment steps count to process.
     *
     * @param experimentId - experiment id
     * @return experiment steps count to process
     */
    @Query("select count(es.id) from ExperimentStepEntity es where es.experiment.id = :experimentId " +
            "and (es.status = 'READY' or es.status = 'FAILED')")
    long getExperimentStepsCountToProcess(@Param("experimentId") Long experimentId);
}