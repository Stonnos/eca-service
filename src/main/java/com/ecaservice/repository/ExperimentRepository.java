package com.ecaservice.repository;

import com.ecaservice.model.entity.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Roman Batygin
 */
public interface ExperimentRepository extends JpaRepository<Experiment, Long> {
}
