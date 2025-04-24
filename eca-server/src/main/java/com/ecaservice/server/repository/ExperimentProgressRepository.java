package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Updates experiment progress value.
     *
     * @param experiment - experiment entity
     * @param progress   - progress value
     */
    @Modifying
    @Query("update ExperimentProgressEntity ep set ep.progress = :progress where ep.experiment = :experiment")
    void updateProgress(@Param("experiment") Experiment experiment,
                        @Param("progress") Integer progress);
}
