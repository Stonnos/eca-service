package com.ecaservice.auto.test.repository.ecaserver;

import com.ecaservice.auto.test.entity.ecaserver.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Implements repository that manages with {@link Experiment} entities.
 *
 * @author Roman Batygin
 */
public interface ExperimentRepository extends JpaRepository<Experiment, Long> {

    /**
     * Gets experiment by request id.
     *
     * @param requestId - request id
     * @return experiment entity
     */
    Optional<Experiment> findByRequestId(String requestId);
}
