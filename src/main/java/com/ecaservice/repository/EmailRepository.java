package com.ecaservice.repository;

import com.ecaservice.model.entity.Email;
import com.ecaservice.model.entity.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Implements repository that manages with {@link Email} entities.
 *
 * @author Roman Batygin
 */
public interface EmailRepository extends JpaRepository<Email, Long> {

    /**
     * Finds email by specified experiment
     *
     * @param experiment experiment {@link Experiment}
     * @return email object
     */
    Email findByExperiment(Experiment experiment);
}
