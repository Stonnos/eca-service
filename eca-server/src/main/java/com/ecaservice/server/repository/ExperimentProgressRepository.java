package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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
    Optional<ExperimentProgressEntity> findByExperiment(Experiment experiment);
}
