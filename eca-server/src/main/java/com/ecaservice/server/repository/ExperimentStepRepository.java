package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link ExperimentStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentStepRepository extends JpaRepository<ExperimentStepEntity, Long> {

    List<ExperimentStepEntity> findByExperimentAndStatusInOrderByOrder(Experiment experiment,
                                                                       Collection<ExperimentStepStatus> stepStatuses);

    long countByStatusNotIn(Collection<ExperimentStepStatus> stepStatuses);
}