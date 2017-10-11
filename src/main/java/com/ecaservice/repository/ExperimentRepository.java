package com.ecaservice.repository;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * Implements repository that deals with {@link Experiment} entities.
 * @author Roman Batygin
 */
public interface ExperimentRepository extends JpaRepository<Experiment, Long> {

    /**
     * Finds experiment by uuid.
     * @param uuid uuid
     * @return {@link Experiment} object
     */
    Experiment findByUuid(String uuid);

    /**
     * Finds not sent experiments by statuses
     * @param statuses {@link ExperimentStatus} collection
     * @return {@link Experiment} list
     */
    List<Experiment> findByExperimentStatusInAndSentDateIsNull(Collection<ExperimentStatus> statuses);

}
