package com.ecaservice.repository;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ExperimentProgressEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExperimentProgressRepository extends JpaRepository<ExperimentProgressEntity, Long> {

    /**
     * Finds experiment progress entity by experiment.
     *
     * @param experiment - experiment entity
     * @return experiment progress entity
     */
    ExperimentProgressEntity findByExperiment(Experiment experiment);
}
