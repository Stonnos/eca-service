package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<ExperimentStepEntity> findByExperimentAndStatusInOrderByOrder(Experiment experiment,
                                                                       Collection<ExperimentStepStatus> stepStatuses);

    /**
     * Gets experiment steps statuses.
     *
     * @param experiment - experiment entity
     * @return experiment step statuses
     */
    @Query("select es.status from ExperimentStepEntity es where es.experiment = :experiment order by es.order")
    List<ExperimentStepStatus> getStepStatuses(@Param("experiment") Experiment experiment);
}