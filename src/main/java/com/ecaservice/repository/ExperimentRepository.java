package com.ecaservice.repository;

import com.ecaservice.model.entity.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
