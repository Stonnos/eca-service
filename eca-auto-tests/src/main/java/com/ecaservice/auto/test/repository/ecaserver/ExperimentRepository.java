package com.ecaservice.auto.test.repository.ecaserver;

import com.ecaservice.auto.test.entity.ecaserver.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Implements repository that manages with {@link Experiment} entities.
 *
 * @author Roman Batygin
 */
public interface ExperimentRepository extends JpaRepository<Experiment, Long> {
}
